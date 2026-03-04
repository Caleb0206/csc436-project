package com.example.preppin.data

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "meal_slots", primaryKeys = ["day", "meal_type"])
data class MealSlotEntity(
    @ColumnInfo val day: String,
    @ColumnInfo(name = "meal_type") val mealType: String,
    @ColumnInfo val status: String,
    @ColumnInfo val recipeName: String? = null,
    @ColumnInfo val ateOne: Boolean = false
)
