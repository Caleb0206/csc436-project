package com.example.preppin.model

data class PrepRequest(
    val day: Day,
    val time: MealType,
    val recipeName: String,
    val servings: Int,
    val eatOneServing: Boolean,
    val fillBreakfastOnly: Boolean
)