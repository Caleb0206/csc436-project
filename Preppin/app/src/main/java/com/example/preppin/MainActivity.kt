package com.example.preppin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.preppin.ui.theme.PreppinTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.preppin.data.AppDatabase
import com.example.preppin.data.MealRepository
import com.example.preppin.ui.MealPlanScreen

class MainActivity : ComponentActivity() {
    private lateinit var db: AppDatabase
    private lateinit var repo: MealRepository
    private lateinit var vmFactory: MealPlanViewModelFactory

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java,
            "preppin.db"

        ).build()

        repo = MealRepository(db.recipeDao(), db.mealSlotDao())
        vmFactory = MealPlanViewModelFactory(repo)

        setContent {
            PreppinTheme(dynamicColor = false) {
                val vm: MealPlanViewModel = viewModel(factory = vmFactory)
                MainApp(viewModel = vm)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
    }
}

@Preview(showBackground = true)
@Composable
fun MealPlanWithViewModelPreview() {
    PreppinTheme {
        val vm: MealPlanViewModel = viewModel()
        MealPlanScreen(viewModel = vm)
    }
}
