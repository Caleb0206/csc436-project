package com.example.preppin

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

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
            slots = uiState.slots,
            onSlotClick = { day, meal -> viewModel.toggleSlot(day, meal) },
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
                .padding(16.dp)
        )
    }
}
@Composable
private fun CalendarGrid(
    slots: List<MealSlot>,
    onSlotClick: (Day, MealType) -> Unit,
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


        days.forEach {day ->
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
                    val slot = slots.first {
                        it.day == day && it.mealType == meal
                    }
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(1.dp)
                    ) {
                        SlotCell(
                            slot = slot,
                            onClick = { onSlotClick(day, meal) }
                        )
                    }
                }
            }
        }



    }
}

@Composable
private fun SlotCell(
    slot: MealSlot,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .height(70.dp)
            .fillMaxWidth()
            .clickable { onClick() }
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (slot.status) {
                MealStatus.EMPTY -> Text("")
                MealStatus.COOKING -> StatusChip(text = slot.label ?: "Cooking")
                MealStatus.PREPPED -> StatusChip(text = slot.label ?: "Prepped!")
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