package com.example.preppin

data class Recipe(
    val id: String,
    val name: String,
    val ingredients: String,
    val photoUri: String? = null,
)