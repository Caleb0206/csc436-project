package com.example.preppin.model

sealed interface Cell {
    data class Cooking(val recipe: String, val ateOne: Boolean = false) : Cell
    data class Prepped(val recipe: String) : Cell
}