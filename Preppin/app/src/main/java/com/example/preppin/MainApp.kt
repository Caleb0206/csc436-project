package com.example.preppin

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.preppin.ui.AppLayout
import com.example.preppin.ui.MealPlanScreen
import com.example.preppin.ui.PrepScreen
import com.example.preppin.ui.RecipeScreen
import com.example.preppin.ui.theme.PreppinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp() {
    val navController = rememberNavController()
    val viewModel: MealPlanViewModel = viewModel()
    var darkMode by remember { mutableStateOf(false) }

    var recipes by remember {
        mutableStateOf(
            listOf(
                Recipe("1", "Braised Pork Rice", "Ground Pork, White Rice, Soy Sauce..."),
                Recipe("2", "Stir Fry Beef Udon", "Beef Rolls, Udon noodles..."),
                Recipe("3", "Scallion Oil Noodles", "Noodles, Green Onions, Shallots...")
            )
        )
    }

    fun upsertRecipe(updated: Recipe) {
        recipes = if (recipes.any { it.id == updated.id }) {
            recipes.map { if (it.id == updated.id) updated else it }
        } else {
            recipes + updated
        }
    }
    PreppinTheme(
        darkTheme = darkMode,
        dynamicColor = false
    ) {
        AppLayout(
            navController = navController,
            darkMode = darkMode,
            onToggleDarkMode = { darkMode = it }
        ) { contentModifier ->
            NavHost(
                navController = navController,
                startDestination = "home",
                modifier = contentModifier
            ) {

                composable("home") {
                    MealPlanScreen(
                        viewModel = viewModel,
                    )
                }

                composable("prep") {
                    PrepScreen(
                        recipes = recipes,
                        viewModel = viewModel,
                        onCancel = { navController.popBackStack() },
                        onSubmitted = { navController.popBackStack() }
                    )
                }

                composable("recipes") {
                    RecipeScreen(
                        recipes = recipes,
                        onUpsertRecipe = { upsertRecipe(it) },
                    )
                }
            }
        }
    }

}