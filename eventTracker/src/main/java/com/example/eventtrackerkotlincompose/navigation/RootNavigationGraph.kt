package com.example.eventtrackerkotlincompose.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eventtrackerkotlincompose.screens.HomeScreen

@Composable
fun RootNavigationGraph(rootNavController: NavHostController) {
    NavHost(
        navController = rootNavController,
        startDestination = Graph.AUTHENTICATION,
        route = Graph.ROOT) {
        authNavGraph(rootNavController)
        composable(route = Graph.HOME) {
            HomeScreen(rootNavController)
        }
        detailsNavGraph(rootNavController)
    }
}

object Graph {
    const val ROOT = "root_graph"
    const val AUTHENTICATION = "auth_graph"
    const val HOME = "home_graph"
    const val DETAILS = "details_graph"
}