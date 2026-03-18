package com.example.preppin.ui

import androidx.compose.runtime.Composable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.preppin.MealPlanViewModel
import java.util.UUID
import com.example.preppin.Recipe
import java.io.File

private enum class RecipeDialogMode { ADD, EDIT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    recipes: List<Recipe>,
    viewModel: MealPlanViewModel,
    onUpsertRecipe: (Recipe) -> Unit,
    onDeleteRecipe: (Recipe) -> Unit,
    onTakePhoto: (String) -> Unit,
) {
    val isDialogOpen = viewModel.recipeDialogOpen
    val mode = RecipeDialogMode.valueOf(viewModel.recipeDialogModeName)
    val activeRecipe = recipes.firstOrNull { it.id == viewModel.activeRecipeId }

    fun handleSave() {
        val name = viewModel.recipeDraftName.trim()
        val ingredients = viewModel.recipeDraftIngredients.trim()
        if (name.isBlank()) return

        val recipe = if (mode == RecipeDialogMode.ADD) {
            Recipe(
                id = UUID.randomUUID().toString(),
                name = name,
                ingredients = ingredients
            )
        } else {
            val existing = activeRecipe ?: return
            existing.copy(name = name, ingredients = ingredients)
        }

        onUpsertRecipe(recipe)
        viewModel.closeRecipeDialog()
    }

    Scaffold() { inner ->
        val isLandscape =
            LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
        Box(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
        ) {
            if (isLandscape) {
                LazyVerticalGrid(
                    columns = GridCells.Fixed(2),
                    modifier = Modifier
                        .padding(inner)
                        .fillMaxSize()
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(recipes, key = { it.id }) { r ->
                        RecipeCard(
                            recipe = r,
                            onEdit = { viewModel.openEditRecipeDialog(r) },
                            onTakePhoto = { onTakePhoto(r.id) },
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(10.dp),
                    contentPadding = PaddingValues(bottom = 88.dp)
                ) {
                    items(recipes, key = { it.id }) { r ->
                        RecipeCard(
                            recipe = r,
                            onEdit = { viewModel.openEditRecipeDialog(r) },
                            onTakePhoto = { onTakePhoto(r.id) }
                        )
                    }
                }
            }
            Button(
                onClick = { viewModel.openAddRecipeDialog() },
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .padding(10.dp)
            ) {
                Text("Add Recipe")
            }


            if (isDialogOpen) {
                EditRecipeDialog(
                    mode = mode,
                    recipe = activeRecipe,
                    name = viewModel.recipeDraftName,
                    ingredients = viewModel.recipeDraftIngredients,
                    onNameChange = { viewModel.updateRecipeDraftName(it) },
                    onIngredientsChange = { viewModel.updateRecipeDraftIngredients(it) },
                    onDismiss = { viewModel.closeRecipeDialog() },
                    onSave = { handleSave() },
                    onTakePhoto = { activeRecipe?.id?.let { id -> onTakePhoto(id) } },
                    onDelete = {
                        activeRecipe?.let {
                            onDeleteRecipe(it)
                            viewModel.closeRecipeDialog()
                        }
                    }
                )
            }
        }
    }
}

@Composable
private fun RecipeCard(
    recipe: Recipe,
    onEdit: () -> Unit,
    onTakePhoto: () -> Unit,
) {
    Card {
        Column(Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Text(recipe.name, fontWeight = FontWeight.SemiBold)
                TextButton(onClick = onEdit) { Text("Edit") }
            }

            // Placeholder image area (since web img isn't being loaded here)
            Surface(
                tonalElevation = 1.dp,
                shape = MaterialTheme.shapes.medium,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(120.dp)
            ) {
                if (!recipe.photoUri.isNullOrBlank()) {
                    AsyncImage(
                        model = File(recipe.photoUri),
                        contentDescription = "${recipe.name} captured on Android",
                        modifier = Modifier.fillMaxSize()
                    )
                } else {
                    Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = onTakePhoto) { Text("Add Photo") }
                    }
                }
            }

            Text(recipe.ingredients)
        }
    }
}

@Composable
private fun EditRecipeDialog(
    mode: RecipeDialogMode,
    recipe: Recipe?,
    name: String,
    ingredients: String,
    onNameChange: (String) -> Unit,
    onIngredientsChange: (String) -> Unit,
    onDismiss: () -> Unit,
    onSave: () -> Unit,
    onDelete: () -> Unit,
    onTakePhoto: () -> Unit,
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == RecipeDialogMode.ADD) "Add Recipe" else "Edit Recipe") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = onNameChange,
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ingredients,
                    onValueChange = onIngredientsChange,
                    label = { Text("Ingredients") },
                    modifier = Modifier.fillMaxWidth(),
                    minLines = 3
                )
                if (mode == RecipeDialogMode.EDIT) {
                    Box(Modifier.fillMaxWidth(), contentAlignment = Alignment.Center) {
                        TextButton(onClick = onTakePhoto) { Text("Add Photo") }
                    }
                } else {
                    Text(
                        text = "Save the recipe first to add a photo!",
                        style = MaterialTheme.typography.bodySmall
                    )
                }

            }
        },
        confirmButton = {
            Button(onClick = onSave) {
                Text("Save")
            }
        },
        dismissButton = {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                if (mode == RecipeDialogMode.EDIT) {
                    Button(
                        onClick = onDelete,
                        colors = ButtonDefaults.buttonColors(
                            containerColor = MaterialTheme.colorScheme.errorContainer,
                            contentColor = MaterialTheme.colorScheme.onErrorContainer
                        )

                    ) {
                        Text("Delete")
                    }
                } else {
                    Spacer(modifier = Modifier.width(1.dp))
                }
                TextButton(onClick = onDismiss) { Text("Cancel") }
            }

        }
    )
}
