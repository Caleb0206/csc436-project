package com.example.preppin.data

import com.example.preppin.model.*
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MealRepository(
    private val recipeDao: RecipeDao,
    private val slotDao: MealSlotDao
) {
    val recipesFlow: Flow<List<RecipeEntity>> = recipeDao.getAllRecipes()

    suspend fun upsertRecipe(r: RecipeEntity) = recipeDao.upsert(r)
    suspend fun deleteRecipe(r: RecipeEntity) = recipeDao.delete(r)

    val slotsFlow: Flow<List<MealSlotEntity>> = slotDao.getAllSlots()
    fun slotsForDay(day: Day): Flow<List<MealSlotEntity>> = slotDao.getSlotsForDay(day.name)

    suspend fun upsertSlot(slot: MealSlotEntity) = slotDao.upsert(slot)
    suspend fun clearSlots() = slotDao.clearAll()
}