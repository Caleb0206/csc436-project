package com.example.preppin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlin.math.max
import com.example.preppin.model.*

data class MealPlanUiState(
    val calendar: Map<Day, DayMeals> = emptyMap()
)

class MealPlanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        MealPlanUiState(calendar = seedCalendar())
    )
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    fun setCooking(day: Day, meal: MealType, recipe: String, ateOne: Boolean = false) {
        updateCell(day, meal, Cell.Cooking(recipe, ateOne))
    }

    fun setPrepped(day: Day, meal: MealType, recipe: String) {
        updateCell(day, meal, Cell.Prepped(recipe))
    }

    fun clear(day: Day, meal: MealType) {
        updateCell(day, meal, null)
    }

    fun prepSubmit(req: PrepRequest) : PrepResult {
        val servingsNum = req.servings.coerceAtLeast(0)
        val leftovers = max(0, servingsNum - (if (req.eatOneServing) 1 else 0))

        val days = Day.entries
        val dayIndex = days.indexOf(req.day)

        val mealOrder = mapOf (
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

        val current = _uiState.value.calendar

        // check for conflicts
        val conflicts = targets.filter { (d, m) ->
            (current[d] ?: DayMeals()).get(m) != null
        }

        if (conflicts.isNotEmpty()) {
            val (d, m) = conflicts.first()
            return PrepResult(
                ok = false,
                message = "Slot filled already (${d.name.lowercase()} ${m.name.lowercase()}). Try another time"
            )
        }
        val updated = current.toMutableMap()

        fun putCell(day: Day, meal: MealType, cell: Cell) {
            val dm = updated[day] ?: DayMeals()
            updated[day] = dm.set(meal, cell)
        }

        putCell(req.day, req.time, Cell.Cooking(recipe = req.recipeName, ateOne = req.eatOneServing))

        targets.drop(1).forEach { (d, m) ->
            putCell(d, m, Cell.Prepped(recipe = req.recipeName))
        }

        _uiState.value = _uiState.value.copy(calendar = updated)
        return PrepResult(ok = true)
    }


    private fun updateCell(day: Day, meal: MealType, cell: Cell?) {
        val current = _uiState.value.calendar
        val dayMeals = current[day] ?: DayMeals()
        _uiState.value = MealPlanUiState(
            calendar = current + (day to dayMeals.set(meal, cell))
        )
    }

    private fun seedCalendar(): Map<Day, DayMeals> {
        // Create all 7 days present, like your DAYS array
        val base = Day.entries.associateWith { DayMeals() }.toMutableMap()

        // Seed like your HTML example
        base[Day.TUES] = base[Day.TUES]!!.set(MealType.DINNER, Cell.Cooking("Chicken tacos", ateOne = false))
        base[Day.WED] = base[Day.WED]!!
            .set(MealType.LUNCH, Cell.Prepped("Pasta salad"))
            .set(MealType.DINNER, Cell.Prepped("Stir fry"))

        return base
    }

}