package com.example.preppin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.example.preppin.data.MealRepository
import com.example.preppin.data.MealSlotEntity
import com.example.preppin.data.cellToEntity
import com.example.preppin.data.toCell
import com.example.preppin.data.toDomain
import com.example.preppin.data.toEntity
import kotlinx.coroutines.flow.StateFlow
import kotlin.math.max
import com.example.preppin.model.*
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class MealPlanUiState(
    val calendar: Map<Day, DayMeals> = emptyMap()
)

class MealPlanViewModel(
    private val repo: MealRepository,
    private val savedStateHandle: SavedStateHandle
) : ViewModel() {

    // PrepScreen state
    var prepDayName by mutableStateOf(savedStateHandle["prepDayName"] ?: Day.SUN.name)
        private set

    var prepTimeName by mutableStateOf(savedStateHandle["prepTimeName"] ?: MealType.BREAKFAST.name)
        private set

    var prepRecipeId by mutableStateOf(savedStateHandle["prepRecipeId"] ?: "")
        private set

    var prepServingsText by mutableStateOf(savedStateHandle["prepServingsText"] ?: "2")
        private set

    var prepBreakfastOnly by mutableStateOf(savedStateHandle["prepBreakfastOnly"] ?: false)
        private set

    var prepEatOne by mutableStateOf(savedStateHandle["prepEatOne"] ?: true)
        private set

    var prepError by mutableStateOf<String?>(savedStateHandle["prepError"])
        private set

    fun updatePrepDayName(value: String) {
        prepDayName = value
        savedStateHandle["prepDayName"] = value
    }

    fun updatePrepTimeName(value: String) {
        prepTimeName = value
        savedStateHandle["prepTimeName"] = value
    }

    fun updatePrepRecipeId(value: String) {
        prepRecipeId = value
        savedStateHandle["prepRecipeId"] = value
    }

    fun updatePrepServingsText(value: String) {
        prepServingsText = value
        savedStateHandle["prepServingsText"] = value
    }

    fun updatePrepBreakfastOnly(value: Boolean) {
        prepBreakfastOnly = value
        savedStateHandle["prepBreakfastOnly"] = value
    }

    fun updatePrepEatOne(value: Boolean) {
        prepEatOne = value
        savedStateHandle["prepEatOne"] = value
    }

    fun updatePrepError(value: String?) {
        prepError = value
        savedStateHandle["prepError"] = value
    }


    // RecipeScreen state
    var recipeDialogOpen by mutableStateOf(savedStateHandle["recipeDialogOpen"] ?: false)
        private set

    var recipeDialogModeName by mutableStateOf(savedStateHandle["recipeDialogModeName"] ?: "EDIT")
        private set

    var activeRecipeId by mutableStateOf<String?>(savedStateHandle["activeRecipeId"])
        private set

    var recipeDraftName by mutableStateOf(savedStateHandle["recipeDraftName"] ?: "")
        private set

    var recipeDraftIngredients by mutableStateOf(savedStateHandle["recipeDraftIngredients"] ?: "")
        private set

    fun updateRecipeDraftName(value: String) {
        recipeDraftName = value
        savedStateHandle["recipeDraftName"] = value
    }

    fun updateRecipeDraftIngredients(value: String) {
        recipeDraftIngredients = value
        savedStateHandle["recipeDraftIngredients"] = value
    }

    fun openAddRecipeDialog() {
        recipeDialogModeName = "ADD"
        activeRecipeId = null
        recipeDraftName = ""
        recipeDraftIngredients = ""

        savedStateHandle["recipeDialogModeName"] = recipeDialogModeName
        savedStateHandle["activeRecipeId"] = activeRecipeId
        savedStateHandle["recipeDraftName"] = recipeDraftName
        savedStateHandle["recipeDraftIngredients"] = recipeDraftIngredients

        recipeDialogOpen = true
        savedStateHandle["recipeDialogOpen"] = true
    }

    fun openEditRecipeDialog(recipe: Recipe) {
        recipeDialogModeName = "EDIT"
        activeRecipeId = recipe.id
        recipeDraftName = recipe.name
        recipeDraftIngredients = recipe.ingredients

        savedStateHandle["recipeDialogModeName"] = recipeDialogModeName
        savedStateHandle["activeRecipeId"] = activeRecipeId
        savedStateHandle["recipeDraftName"] = recipeDraftName
        savedStateHandle["recipeDraftIngredients"] = recipeDraftIngredients

        recipeDialogOpen = true
        savedStateHandle["recipeDialogOpen"] = true
    }

    fun closeRecipeDialog() {
        recipeDialogOpen = false
        savedStateHandle["recipeDialogOpen"] = false
    }

    val recipes: StateFlow<List<Recipe>> = repo.recipesFlow
        .map { list -> list.map { it.toDomain() } }
        .stateIn(viewModelScope, SharingStarted.Eagerly, emptyList())

    private fun slotsListToCalendar(slots: List<MealSlotEntity>): Map<Day, DayMeals> {
        val base = Day.entries.associateWith { DayMeals() }.toMutableMap()
        for (e in slots) {
            val day = try {
                Day.valueOf(e.day)
            } catch (e: Exception) {
                continue
            }
            val meal = try {
                MealType.valueOf(e.mealType)
            } catch (e: Exception) {
                continue
            }
            val cell = e.toCell()
            val dm = base[day] ?: DayMeals()
            base[day] = dm.set(meal, cell)
        }
        return base
    }

    val uiState: StateFlow<MealPlanUiState> = repo.slotsFlow
        .map { slots -> MealPlanUiState(calendar = slotsListToCalendar(slots)) }
        .stateIn(viewModelScope, SharingStarted.Eagerly, MealPlanUiState(calendar = seedCalendar()))


    fun prepSubmit(req: PrepRequest): PrepResult {
        val servingsNum = req.servings.coerceAtLeast(0)
        val leftovers = max(0, servingsNum - (if (req.eatOneServing) 1 else 0))

        val days = Day.entries
        val dayIndex = days.indexOf(req.day)

        val mealOrder = mapOf(
            MealType.BREAKFAST to 0,
            MealType.LUNCH to 1,
            MealType.DINNER to 2
        )

        val allowedMeals = if (req.fillBreakfastOnly)
            listOf(MealType.BREAKFAST)
        else
            listOf(MealType.LUNCH, MealType.DINNER)
        // build candidate leftover slots after cooking, no wrap past Saturday
        val slots = mutableListOf<Pair<Day, MealType>>()

        for (offset in 0 until days.size) {
            val idx = dayIndex + offset
            if (idx >= days.size) break
            val dayKey = days[idx]

            for (meal in allowedMeals) {
                if (offset == 0) {
                    if (mealOrder.getValue(meal) <= mealOrder.getValue(req.time))
                        continue
                }
                slots.add(dayKey to meal)
            }
        }
        val targets = buildList {
            add(req.day to req.time)
            addAll(slots.take(leftovers))
        }

        val currentSlotsSnapshot = runBlockingFetchSlots()
        val currentCalendar = slotsListToCalendar(currentSlotsSnapshot)

        // check for conflicts
        val conflicts = targets.filter { (d, m) ->
            (currentCalendar[d] ?: DayMeals()).get(m) != null
        }

        if (conflicts.isNotEmpty()) {
            val (d, m) = conflicts.first()
            return PrepResult(
                ok = false,
                message = "Slot filled already (${d.name.lowercase()} ${m.name.lowercase()}). Try another time"
            )
        }

        viewModelScope.launch {
            repo.upsertSlot(
                cellToEntity(
                    req.day,
                    req.time,
                    Cell.Cooking(req.recipeName, req.eatOneServing)
                )
            )
            targets.drop(1).forEach { (d, m) ->
                repo.upsertSlot(cellToEntity(d, m, Cell.Prepped(req.recipeName)))

            }
        }
        return PrepResult(ok = true)
    }

    private fun runBlockingFetchSlots(): List<MealSlotEntity> {
        return kotlinx.coroutines.runBlocking {
            repo.slotsFlow.first()
        }
    }


    private fun seedCalendar(): Map<Day, DayMeals> {
        val base = Day.entries.associateWith { DayMeals() }.toMutableMap()
        base[Day.TUES] =
            base[Day.TUES]!!.set(MealType.DINNER, Cell.Cooking("Chicken tacos", ateOne = false))
        base[Day.WED] = base[Day.WED]!!.set(MealType.LUNCH, Cell.Prepped("Pasta salad"))
            .set(MealType.DINNER, Cell.Prepped("Stir fry"))
        return base
    }

    fun upsertRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repo.upsertRecipe(recipe.toEntity())
        }
    }

    fun deleteRecipe(recipe: Recipe) {
        viewModelScope.launch {
            repo.deleteRecipe(recipe.toEntity())
        }
    }

    fun resetCalendar() {
        viewModelScope.launch {
            repo.clearSlots()
        }
    }


    fun setRecipePhoto(recipeId: String, photoUri: String) {
        viewModelScope.launch {
            val current = repo.getRecipeById(recipeId) ?: return@launch
            repo.upsertRecipe(current.copy(photoUri = photoUri))
        }
    }

}