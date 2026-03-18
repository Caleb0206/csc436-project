package com.example.preppin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Alignment
import com.example.preppin.MealPlanViewModel
import com.example.preppin.Recipe
import com.example.preppin.model.Day
import com.example.preppin.model.MealType
import com.example.preppin.model.PrepRequest


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PrepScreen(
    recipes: List<Recipe>,
    onCancel: () -> Unit,
    onSubmitted: () -> Unit,
    viewModel: MealPlanViewModel
) {
    val day = Day.valueOf(viewModel.prepDayName)
    val time = MealType.valueOf(viewModel.prepTimeName)
    val recipeId = viewModel.prepRecipeId
    val servingsText = viewModel.prepServingsText
    val breakfastOnly = viewModel.prepBreakfastOnly
    val eatOne = viewModel.prepEatOne
    val error = viewModel.prepError

    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE

    LaunchedEffect(recipes) {
        if (recipes.none { it.id == recipeId }) {
            viewModel.updatePrepRecipeId(recipes.firstOrNull()?.id ?: "")
        }
    }

    Scaffold() { inner ->
        Column(
            modifier = Modifier
                .padding(inner)
                .fillMaxSize()
                .padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (isLandscape) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(5.dp)
                ) {
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(5.dp)
                    ) {
                        DayDropDown(
                            value = day,
                            onChange = { viewModel.updatePrepDayName(it.name) })
                        MealDropdown(
                            value = time,
                            onChange = { viewModel.updatePrepTimeName(it.name) })
                        RecipeDropdown(
                            recipes = recipes,
                            value = recipeId,
                            onChange = { viewModel.updatePrepRecipeId(it) })
                    }
                    Column(
                        modifier = Modifier.weight(1f),
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedTextField(
                            value = servingsText,
                            onValueChange = {
                                viewModel.updatePrepServingsText(it.filter { ch -> ch.isDigit() })
                            },
                            label = { Text("Servings") },
                            singleLine = true,
                            keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Checkbox(
                                checked = eatOne,
                                onCheckedChange = { viewModel.updatePrepEatOne(it) })
                            Text("Eat one serving on cooking day")
                        }
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Checkbox(
                                checked = breakfastOnly,
                                onCheckedChange = { viewModel.updatePrepBreakfastOnly(it) })
                            Text("Fill breakfast only")
                        }
                    }
                }
            } else {


                DayDropDown(value = day, onChange = { viewModel.updatePrepDayName(it.name) })
                MealDropdown(value = time, onChange = { viewModel.updatePrepTimeName(it.name) })
                RecipeDropdown(
                    recipes = recipes,
                    value = recipeId,
                    onChange = { viewModel.updatePrepRecipeId(it) })

                OutlinedTextField(
                    value = servingsText,
                    onValueChange = {
                        viewModel.updatePrepServingsText(it.filter { ch -> ch.isDigit() })
                    },
                    label = { Text("Servings") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                )
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Checkbox(checked = eatOne, onCheckedChange = { viewModel.updatePrepEatOne(it) })
                    Text("Eat one serving on cooking day")
                }
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Checkbox(
                        checked = breakfastOnly,
                        onCheckedChange = { viewModel.updatePrepBreakfastOnly(it) })
                    Text("Fill breakfast only")
                }
            }
            error?.let { Text(it, color = MaterialTheme.colorScheme.error) }

            Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedButton(
                    onClick = onCancel,
                    modifier = Modifier.weight(1f)
                ) { Text("Cancel") }

                Button(
                    onClick = {
                        val recipeName = recipes.firstOrNull { it.id == recipeId }?.name ?: ""
                        val servings = servingsText.toIntOrNull() ?: 0

                        val res = viewModel.prepSubmit(
                            PrepRequest(
                                day = day,
                                time = time,
                                recipeName = recipeName,
                                servings = servings,
                                eatOneServing = eatOne,
                                fillBreakfastOnly = breakfastOnly
                            )
                        )

                        if (!res.ok) {
                            viewModel.updatePrepError(res.message)
                        } else {
                            viewModel.updatePrepError(null)
                            onSubmitted()
                        }
                    },
                    modifier = Modifier.weight(1f)
                ) { Text("Prep!") }
            }
        }
    }
}

@Composable
private fun DayDropDown(value: Day, onChange: (Day) -> Unit) {
    SimpleEnumDropdown("Select a day", Day.entries, value, onChange) {
        it.name.lowercase().replaceFirstChar { c -> c.uppercase() }
    }
}

@Composable
private fun MealDropdown(value: MealType, onChange: (MealType) -> Unit) {
    SimpleEnumDropdown("Select meal time", MealType.entries, value, onChange) {
        it.name.lowercase().replaceFirstChar { c -> c.uppercase() }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun RecipeDropdown(
    recipes: List<Recipe>,
    value: String,
    onChange: (String) -> Unit
) {
    var expanded by remember { mutableStateOf(false) }
    val current = recipes.firstOrNull { it.id == value }?.name ?: ""

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = current,
            onValueChange = {},
            label = { Text("Select recipe") },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            recipes.forEach { r ->
                DropdownMenuItem(
                    text = { Text(r.name) },
                    onClick = {
                        onChange(r.id)
                        expanded = false
                    }
                )
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun <T> SimpleEnumDropdown(
    label: String,
    items: List<T>,
    value: T,
    onChange: (T) -> Unit,
    text: (T) -> String
) {
    var expanded by remember { mutableStateOf(false) }

    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
        OutlinedTextField(
            readOnly = true,
            value = text(value),
            onValueChange = {},
            label = { Text(label) },
            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) },
            modifier = Modifier
                .menuAnchor()
                .fillMaxWidth()
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
            items.forEach { item ->
                DropdownMenuItem(
                    text = { Text(text(item)) },
                    onClick = {
                        onChange(item)
                        expanded = false
                    }
                )
            }
        }
    }
}