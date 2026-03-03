package com.example.preppin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.preppin.MealPlanViewModel
import com.example.preppin.model.Cell
import com.example.preppin.model.Day
import com.example.preppin.model.DayMeals
import com.example.preppin.model.MealType

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MealPlanScreen(
    viewModel: MealPlanViewModel,
    onRecipesClick: () -> Unit = {},
    onPrepClick: () -> Unit = {}
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Preppin'")}
            )
        },
        bottomBar = {
            BottomButtons(
                onRecipesClick = onRecipesClick,
                onPrepClick = onPrepClick
            )
        }
    ) { innerPadding ->
        CalendarGrid(
            calendar = uiState.calendar,
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}
@Composable
private fun CalendarGrid(
    calendar: Map<Day, DayMeals>,
    modifier: Modifier = Modifier
) {
    val meals = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER)
    val days = Day.entries

    Column(modifier) {
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(30.dp))

            meals.forEach { meal ->
                Text(
                    text = meal.name.lowercase().replaceFirstChar {it.uppercase()},
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        // rows
        days.forEach {day ->
            val dayMeals = calendar[day] ?: DayMeals()

            Row(
                Modifier
                    .fillMaxWidth()
                    .padding(vertical = 1.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = day.short,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .width(40.dp)
                        .padding(4.dp)
                )

                meals.forEach { meal ->
                    val cell = dayMeals.get(meal)

                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(1.dp)
                    ) {
                        SlotCell( cell = cell )
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotCell(
    cell: Cell?,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .height(70.dp)
            .fillMaxWidth()
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (cell) {
                null -> { /* Empty */ }

                is Cell.Cooking -> Column {
                    Text("Cooking", fontWeight = FontWeight.Bold)
                    Text(cell.recipe)
                    if (cell.ateOne) Text("Eat 1 serving", style = MaterialTheme.typography.labelSmall)
                }
                is Cell.Prepped -> Column {
                    Text("Prepped", fontWeight = FontWeight.Bold)
                    Text(cell.recipe)
                }
            }
        }
    }
}

@Composable
private fun StatusChip(text: String) {
    Surface(
        shape = MaterialTheme.shapes.small,
        tonalElevation = 4.dp
    ) {
        Text(
            text = text,
            modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
            fontWeight = FontWeight.Medium
        )
    }
}

@Composable
private fun BottomButtons(
    onRecipesClick: () -> Unit,
    onPrepClick: () -> Unit
) {
    Surface(tonalElevation = 4.dp) {
        Row(
            Modifier
                .fillMaxWidth()
                .padding(12.dp)
        ) {
            Button(
                onClick = onRecipesClick,
                modifier = Modifier.weight(1f)
            ) { Text("Recipes") }

            Button(
                onClick = onPrepClick,
                modifier = Modifier.weight(1f)
            ) { Text("Prep!") }
        }
    }
}