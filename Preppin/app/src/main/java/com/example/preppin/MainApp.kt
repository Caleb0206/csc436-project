package com.example.preppin

import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.*
import androidx.navigation.compose.rememberNavController
import com.example.preppin.ui.AppLayout
import com.example.preppin.ui.CameraScreen
import com.example.preppin.ui.MealPlanScreen
import com.example.preppin.ui.PrepScreen
import com.example.preppin.ui.RecipeScreen
import com.example.preppin.ui.theme.PreppinTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainApp(
    viewModel: MealPlanViewModel,
    darkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit
) {
    val navController = rememberNavController()

    val recipes by viewModel.recipes.collectAsState()

    PreppinTheme(
        darkTheme = darkMode,
        dynamicColor = false
    ) {
        AppLayout(
            navController = navController,
            darkMode = darkMode,
            onToggleDarkMode = onToggleDarkMode
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
                        onUpsertRecipe = { recipe -> viewModel.upsertRecipe(recipe) },
                        onTakePhoto = { recipeId -> navController.navigate("camera/$recipeId") },
                    )
                }

                composable("camera/{recipeId}") { backStack ->
                    val recipeId = backStack.arguments?.getString("recipeId") ?: return@composable
                    CameraScreen(
                        recipeId = recipeId,
                        onCancel = { navController.popBackStack() },
                        onPhotoSaved = { photoUri ->
                            viewModel.setRecipePhoto(recipeId, photoUri)
                            navController.popBackStack()
                        }
                    )
                }
            }
        }
    }

}