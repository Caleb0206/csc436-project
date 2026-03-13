package com.example.preppin.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.List
import androidx.compose.material.icons.filled.AddCircle
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppLayout(
    navController: NavController,
    darkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
    content: @Composable (Modifier) -> Unit
) {
    Scaffold(
        topBar = {
            AppHeader(
                darkMode = darkMode,
                onToggleDarkMode = onToggleDarkMode,
            )
        },
        bottomBar = {
            AppNavBar(
                navController = navController
            )
        }
    ) { innerPadding ->
        content(Modifier.padding(innerPadding))
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppHeader(
    darkMode: Boolean,
    onToggleDarkMode: (Boolean) -> Unit,
) {
    TopAppBar(
        modifier = Modifier.height(90.dp),
        title = { Text("Preppin'", style = MaterialTheme.typography.titleLarge) },
        actions = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(6.dp)
            ) {
                Text("Dark")
                Switch(
                    checked = darkMode,
                    onCheckedChange = onToggleDarkMode
                )
            }

        }
    )
}

private data class BottomDest(
    val route: String,
    val label: String,
    val icon: androidx.compose.ui.graphics.vector.ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
private fun AppNavBar(
    navController: NavController
) {
    val items = listOf(
        BottomDest("recipes", "Recipes", Icons.Filled.List),
        BottomDest("home", "Home", Icons.Filled.Home),
        BottomDest("prep", "Prep", Icons.Filled.AddCircle),
    )
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route

    NavigationBar {
        items.forEach { dest ->
            val selected = currentRoute == dest.route
            NavigationBarItem(
                selected = selected,
                onClick = {
                    navController.navigate(dest.route) {
                        launchSingleTop = true
                        restoreState = true
                        popUpTo(navController.graph.startDestinationId) {
                            saveState = true
                        }
                    }
                },
                icon = { Icon(dest.icon, contentDescription = dest.label) },
                label = { Text(dest.label) }
            )
        }
    }
}