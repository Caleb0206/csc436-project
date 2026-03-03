package com.example.preppin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.ui.tooling.preview.Preview
import com.example.preppin.ui.theme.PreppinTheme
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.preppin.ui.MealPlanScreen

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PreppinTheme(dynamicColor = false) {
                MainApp()
            }
        }
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
