package com.example.preppin

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.example.preppin.ui.theme.PreppinTheme
import androidx.lifecycle.viewmodel.compose.viewModel

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            PreppinTheme {

                val vm: MealPlanViewModel = viewModel()
                MealPlanScreen(
                    viewModel = vm,
                    onRecipesClick = { /* TODO later */ },
                    onPrepClick = { /* TODO later */ }
                )
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
