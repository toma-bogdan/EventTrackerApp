package com.example.eventtrackerkotlincompose.navigation.graphs

import androidx.navigation.NavController
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.eventtrackerkotlincompose.navigation.AddEventRouteScreen
import com.example.eventtrackerkotlincompose.navigation.Graph
import com.example.eventtrackerkotlincompose.screens.notification.AddEventScreen

fun NavGraphBuilder.addEventNavGraph(rootNavController: NavController) {
    navigation(
        route = Graph.AddEvent,
        startDestination = AddEventRouteScreen.AddEvent.route
    ) {
        composable(route = AddEventRouteScreen.AddEvent.route) {
            AddEventScreen(onBackToMainScreenClick = {rootNavController.navigateUp()}, onEventAdded = {})
        }
    }
}