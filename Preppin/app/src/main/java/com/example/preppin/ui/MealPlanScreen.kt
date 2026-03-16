package com.example.preppin.ui

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.unit.Dp
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
) {
    val uiState by viewModel.uiState.collectAsState()
    val isLandscape =
        LocalConfiguration.current.orientation == android.content.res.Configuration.ORIENTATION_LANDSCAPE
    var showResetDialog by remember { mutableStateOf(false) }

    Scaffold(

    ) { _ ->
        Box(
            modifier = Modifier
                .fillMaxSize()
        ) {
            CalendarGrid(
                calendar = uiState.calendar,
                isLandscape = isLandscape,
                modifier = Modifier
                    .fillMaxSize()
            )
            Button(
                onClick = { showResetDialog = true },
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 2.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = MaterialTheme.colorScheme.error,
                    contentColor = MaterialTheme.colorScheme.onError
                )
            ) {
                Text("Reset")
            }
        }


    }
    if (showResetDialog) {
        AlertDialog(
            onDismissRequest = { showResetDialog = false },
            title = { Text("Clear calendar?") },
            text = { Text("This will delete all meal slots from the database.") },
            confirmButton = {
                Button(
                    onClick = {
                        viewModel.resetCalendar()
                        showResetDialog = false
                    }
                ) { Text("Clear") }
            },
            dismissButton = {
                TextButton(onClick = { showResetDialog = false }) {
                    Text("Cancel")
                }
            }

        )
    }
}

@Composable
private fun CalendarGrid(
    calendar: Map<Day, DayMeals>,
    isLandscape: Boolean,
    modifier: Modifier = Modifier
) {
    BoxWithConstraints(modifier = modifier) {
        val availableH = maxHeight
        val headerH = 32.dp
        val headerSpacer = 8.dp
        val rows = if (isLandscape) 3 else Day.entries.size
        val rowSpacing = 2.dp

        val chromeH = headerH + headerSpacer + (rowSpacing * (rows - 1))
        val rawCellH = (availableH - chromeH) / rows

        val cellH = rawCellH.coerceIn(44.dp, 90.dp)

        if (isLandscape) {
            LandscapeCalendarGrid(
                calendar = calendar,
                cellHeight = cellH,
                modifier = Modifier.fillMaxSize()
            )
        } else {
            PortraitCalendarGrid(
                calendar = calendar,
                cellHeight = cellH,
                modifier = modifier.fillMaxSize()
            )
        }
    }

}

@Composable
private fun PortraitCalendarGrid(
    calendar: Map<Day, DayMeals>,
    cellHeight: Dp,
    modifier: Modifier = Modifier
) {
    val meals = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER)
    val days = Day.entries

    Column(modifier, verticalArrangement = Arrangement.Top) {
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(30.dp))

            meals.forEach { meal ->
                Text(
                    text = meal.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        // rows
        days.forEach { day ->
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
                        SlotCell(cell = cell, height = cellHeight)
                    }
                }
            }
        }
    }
}

@Composable
private fun LandscapeCalendarGrid(
    calendar: Map<Day, DayMeals>,
    cellHeight: Dp,
    modifier: Modifier = Modifier
) {
    val meals = listOf(MealType.BREAKFAST, MealType.LUNCH, MealType.DINNER)
    val days = Day.entries

    Column(modifier, verticalArrangement = Arrangement.Top) {
        // header row (days across)
        Row(Modifier.fillMaxWidth()) {
            Spacer(modifier = Modifier.width(90.dp))
            days.forEach { day ->
                Text(
                    text = day.short,
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier.weight(1f),
                )
            }
        }
        Spacer(Modifier.height(8.dp))

        // rows: each meal type
        meals.forEach { meal ->
            Row(
                Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // left label column = MealType
                Text(
                    text = meal.name.lowercase().replaceFirstChar { it.uppercase() },
                    fontWeight = FontWeight.SemiBold,
                    modifier = Modifier
                        .width(90.dp)
                        .padding(4.dp)
                )

                // across = days
                days.forEach { day ->
                    val cell = (calendar[day] ?: DayMeals()).get(meal)
                    Box(
                        modifier = Modifier
                            .weight(1f)
                            .padding(2.dp)
                    ) {
                        // smaller height works better in landscape
                        SlotCell(cell = cell, height = cellHeight)
                    }
                }
            }
        }
    }
}

@Composable
private fun SlotCell(
    cell: Cell?,
    modifier: Modifier = Modifier,
    height: Dp = 50.dp
) {
    val containerColor = when (cell) {
        is Cell.Cooking -> MaterialTheme.colorScheme.tertiaryContainer
        is Cell.Prepped -> MaterialTheme.colorScheme.primaryContainer
        null -> MaterialTheme.colorScheme.surface
    }
    val contentColor = when (cell) {
        is Cell.Cooking -> MaterialTheme.colorScheme.onTertiaryContainer
        is Cell.Prepped -> MaterialTheme.colorScheme.onPrimaryContainer
        null -> MaterialTheme.colorScheme.onSurface
    }
    Surface(
        color = containerColor,
        contentColor = contentColor,
        tonalElevation = 2.dp,
        shape = MaterialTheme.shapes.medium,
        modifier = modifier
            .height(height)
            .fillMaxWidth()
    ) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            when (cell) {
                null -> {}

                is Cell.Cooking -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Cooking", fontWeight = FontWeight.Bold)
                    Text(cell.recipe, style = MaterialTheme.typography.labelMedium)
                    if (cell.ateOne) Text("Eat 1", style = MaterialTheme.typography.labelSmall)
                }

                is Cell.Prepped -> Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Prepped", fontWeight = FontWeight.Bold)
                    Text(cell.recipe, style = MaterialTheme.typography.labelMedium)
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
