package com.example.preppin.model

data class DayMeals(
    val breakfast: Cell? = null,
    val lunch: Cell? = null,
    val dinner: Cell? = null
) {
    fun get(meal: MealType): Cell? = when (meal) {
        MealType.BREAKFAST -> breakfast
        MealType.LUNCH -> lunch
        MealType.DINNER -> dinner
    }
    fun set(meal: MealType, cell: Cell?): DayMeals = when (meal) {
        MealType.BREAKFAST -> copy(breakfast = cell)
        MealType.LUNCH -> copy(lunch = cell)
        MealType.DINNER -> copy(dinner = cell)
    }
}