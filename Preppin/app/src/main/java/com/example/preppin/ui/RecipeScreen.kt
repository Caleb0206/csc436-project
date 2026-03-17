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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import java.util.UUID
import com.example.preppin.Recipe
import java.io.File

private enum class RecipeDialogMode { ADD, EDIT }

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun RecipeScreen(
    recipes: List<Recipe>,
    onUpsertRecipe: (Recipe) -> Unit,
    onDeleteRecipe: (Recipe) -> Unit,
    onTakePhoto: (String) -> Unit,
) {
    var isDialogOpen by remember { mutableStateOf(false) }
    var mode by remember { mutableStateOf(RecipeDialogMode.EDIT) }
    var activeRecipeId by remember { mutableStateOf<String?>(null) }

    val activeRecipe = remember(activeRecipeId, recipes) {
        activeRecipeId?.let { id -> recipes.firstOrNull { it.id == id } }
    }

    fun openAdd() {
        mode = RecipeDialogMode.ADD
        activeRecipeId = null
        isDialogOpen = true
    }

    fun openEdit(recipe: Recipe) {
        mode = RecipeDialogMode.EDIT
        activeRecipeId = recipe.id
        isDialogOpen = true
    }

    fun closeDialog() {
        isDialogOpen = false
    }

    fun handleSave(nameRaw: String, ingredientsRaw: String) {
        val name = nameRaw.trim()
        val ingredients = ingredientsRaw.trim()
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
        closeDialog()
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
                            onEdit = { openEdit(r) },
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
                            onEdit = { openEdit(r) },
                            onTakePhoto = { onTakePhoto(r.id) }
                        )
                    }
                }
            }
            Button(
                onClick = { openAdd() },
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
                    onDismiss = { closeDialog() },
                    onSave = { name, ingredients -> handleSave(name, ingredients) },
                    onTakePhoto = { activeRecipe?.id?.let { id -> onTakePhoto(id) } },
                    onDelete = {
                        activeRecipe?.let {
                            onDeleteRecipe(it)
                            closeDialog()
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
    onDismiss: () -> Unit,
    onSave: (String, String) -> Unit,
    onDelete: () -> Unit,
    onTakePhoto: () -> Unit,
) {
    var name by remember { mutableStateOf(recipe?.name ?: "") }
    var ingredients by remember { mutableStateOf(recipe?.ingredients ?: "") }

    LaunchedEffect(recipe?.id, mode) {
        name = recipe?.name ?: ""
        ingredients = recipe?.ingredients ?: ""
    }
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(if (mode == RecipeDialogMode.ADD) "Add Recipe" else "Edit Recipe") },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text("Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth()
                )
                OutlinedTextField(
                    value = ingredients,
                    onValueChange = { ingredients = it },
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
            Button(onClick = { onSave(name, ingredients) }) {
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
                }
            }
            TextButton(onClick = onDismiss) { Text("Cancel") }
        }
    )
}
