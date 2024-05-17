package com.example.eventtrackerkotlincompose

import LoginScreen
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.compose.rememberNavController
import com.arcgismaps.ApiKey
import com.arcgismaps.ArcGISEnvironment
import com.example.eventtrackerkotlincompose.navigation.RootNavigationGraph
import com.example.eventtrackerkotlincompose.network.AuthRepository
import com.example.eventtrackerkotlincompose.network.AuthResponse
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import com.example.eventtrackerkotlincompose.ui.theme.EventTrackerKotlinComposeTheme
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        ArcGISEnvironment.apiKey = ApiKey.create("AAPK32c0e608e8944f0f97345acdc5a770587RulzSGWPGcXn8hphGd5HukJjb25vD0QM7bZy__0XsPk8BTQCps-OxMdVW8p7dC7")
        setContent {
            EventTrackerKotlinComposeTheme {
//                val coroutineScope = rememberCoroutineScope()
//                val apiService = HttpService(NetworkClient.client)
//                val authRepository = AuthRepository(apiService)
//                var responseString = "no response";
//                coroutineScope.launch {
//
//                    val response = authRepository.login("plspls","plspls")
//                    responseString = response.toString()
//                    Log.d("aa",responseString)
//                }
                RootNavigationGraph(rootNavController = rememberNavController())
//                MainScreen()
//                SampleApp()
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
