package com.example.preppin.data

import com.example.preppin.Recipe
import com.example.preppin.model.Day
import com.example.preppin.model.MealType
import com.example.preppin.model.MealStatus
import com.example.preppin.model.Cell

fun RecipeEntity.toDomain(): Recipe =
    Recipe(id = id, name = name, ingredients = ingredients, photoUri = photoUri)

fun Recipe.toEntity(): RecipeEntity =
    RecipeEntity(id = id, name = name, ingredients = ingredients, photoUri = photoUri)

// entity -> domain Cell?
fun MealSlotEntity.toCell(): Cell? {
    return when (status) {
        "COOKING" -> {
            val recipe = recipeName ?: ""
            Cell.Cooking(recipe = recipe, ateOne = ateOne)
        }

        "PREPPED" -> {
            val recipe = recipeName ?: ""
            Cell.Prepped(recipe)
        }

        else -> null
    }
}

// domain Cell -> entity for a given day+meal
fun cellToEntity(day: Day, meal: MealType, cell: Cell): MealSlotEntity {
    return when (cell) {
        is Cell.Cooking -> MealSlotEntity(
            day = day.name,
            mealType = meal.name,
            status = "COOKING",
            recipeName = cell.recipe,
            ateOne = cell.ateOne
        )

        is Cell.Prepped -> MealSlotEntity(
            day = day.name,
            mealType = meal.name,
            status = "PREPPED",
            recipeName = cell.recipe,
            ateOne = false
        )
    }
}