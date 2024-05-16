package com.example.eventtrackerkotlincompose

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.example.eventtrackerkotlincompose.ui.theme.EventTrackerKotlinComposeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ArcGISEnvironment.apiKey = ApiKey.create("AAPK32c0e608e8944f0f97345acdc5a770587RulzSGWPGcXn8hphGd5HukJjb25vD0QM7bZy__0XsPk8BTQCps-OxMdVW8p7dC7")
        setContent {
            EventTrackerKotlinComposeTheme {
                SampleApp()
            }
        }
    }
}

@Composable
private fun SampleApp() {
    Surface(
        color = MaterialTheme.colorScheme.background
    ) {
        MapMainScreen()
    }
}