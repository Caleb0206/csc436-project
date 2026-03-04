package com.example.preppin.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface RecipeDao {
    @Query("SELECT * FROM recipes ORDER BY name ASC")
    fun getAllRecipes(): Flow<List<RecipeEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(recipe: RecipeEntity)

    @Delete
    suspend fun delete(recipe: RecipeEntity)

    @Query("SELECT * FROM recipes WHERE id = :id LIMIT 1")
    suspend fun getById(id: String): RecipeEntity?
}