package com.example.preppin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.lifecycleScope
import com.example.preppin.ui.theme.PreppinTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.room.Room
import com.example.preppin.data.AppDatabase
import com.example.preppin.data.MealRepository
import com.example.preppin.data.SettingsStore
import com.example.preppin.ui.MealPlanScreen
import kotlinx.coroutines.launch

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
            PreppinTheme {
                val vm: MealPlanViewModel = viewModel(factory = vmFactory)
                val context = LocalContext.current
                val settings = remember { SettingsStore(context) }
                val darkMode by settings.darkModeFlow.collectAsState(initial = false)
                MainApp(
                    viewModel = vm,
                    darkMode = darkMode,
                    onToggleDarkMode = { enabled ->
                        lifecycleScope.launch {
                            settings.setDarkMode(enabled)
                        }
                    })
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
