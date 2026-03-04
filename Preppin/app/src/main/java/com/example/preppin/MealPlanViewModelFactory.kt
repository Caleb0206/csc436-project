package com.example.preppin

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.preppin.data.MealRepository

class MealPlanViewModelFactory(
    private val repository: MealRepository
) : ViewModelProvider.Factory {
    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MealPlanViewModel::class.java)) {
            return MealPlanViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
    }
}