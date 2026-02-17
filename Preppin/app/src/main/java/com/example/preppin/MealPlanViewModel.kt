package com.example.preppin

import androidx.lifecycle.ViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

enum class Day(val short: String) {
    SUN("S"), MON("M"), TUES("T"), WED("W"), THUR("T"), FRI("F"), SAT("S")
}
enum class MealType { BREAKFAST, LUNCH, DINNER }
enum class MealStatus { EMPTY, COOKING, PREPPED }

data class MealSlot(
    val day: Day,
    val mealType: MealType,
    val status: MealStatus = MealStatus.EMPTY,
    val label: String? = null
)

data class MealPlanUiState(
    val slots: List<MealSlot> = emptyList()
)

class MealPlanViewModel : ViewModel() {
    private val _uiState = MutableStateFlow(
        MealPlanUiState(slots = buildInitialSlots())
    )
    val uiState: StateFlow<MealPlanUiState> = _uiState.asStateFlow()

    fun toggleSlot(day: Day, mealType: MealType) {
        val cur = _uiState.value
        val updated = cur.slots.map { slot ->
            if (slot.day == day && slot.mealType == mealType) {
                val next = when (slot.status) {
                    MealStatus.EMPTY -> MealStatus.COOKING
                    MealStatus.COOKING -> MealStatus.PREPPED
                    MealStatus.PREPPED -> MealStatus.EMPTY
                }
                slot.copy(
                    status = next,
                    label = when (next) {
                        MealStatus.EMPTY -> null
                        MealStatus.COOKING -> "Cooking"
                        MealStatus.PREPPED -> "Prepped!"
                    }
                )
            } else slot
        }
        _uiState.value = cur.copy(slots = updated)
    }

    private companion object {
        fun buildInitialSlots(): List<MealSlot> {
            val days = Day.entries
            val meals = MealType.entries

            val base = days.flatMap { day ->
                meals.map { meal -> MealSlot(day = day, mealType = meal) }
            }.toMutableList()

            fun set(day: Day, meal: MealType, status: MealStatus, label: String) {
                val idx = base.indexOfFirst { it.day == day && it.mealType == meal }
                if (idx != -1) base[idx] = base[idx].copy(status = status, label = label)
            }

            set(Day.TUES, MealType.DINNER, MealStatus.COOKING, "Cooking")
            set(Day.WED, MealType.LUNCH, MealStatus.PREPPED, "Prepped!")
            set(Day.WED, MealType.DINNER, MealStatus.PREPPED, "Prepped!")

            return base
        }
    }
}