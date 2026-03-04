package com.example.preppin.data

import androidx.room.*
import com.example.preppin.model.Day
import kotlinx.coroutines.flow.Flow

@Dao
interface MealSlotDao {
    @Query("SELECT * FROM meal_slots")
    fun getAllSlots(): Flow<List<MealSlotEntity>>

    @Query("SELECT * FROM meal_slots WHERE day = :day ORDER BY meal_type")
    fun getSlotsForDay(day: String): Flow<List<MealSlotEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun upsert(slot: MealSlotEntity)

    @Delete
    suspend fun delete(slot: MealSlotEntity)

    @Query("DELETE FROM meal_slots")
    suspend fun clearAll()
}