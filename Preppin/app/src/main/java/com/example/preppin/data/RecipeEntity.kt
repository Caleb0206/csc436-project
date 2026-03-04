package com.example.preppin.data

import androidx.room.Entity
import androidx.room.ColumnInfo
import androidx.room.PrimaryKey

@Entity(tableName = "recipes")
data class RecipeEntity(
    @PrimaryKey val id: String,
    @ColumnInfo val name: String,
    @ColumnInfo val ingredients: String
)