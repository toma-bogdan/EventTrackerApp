package com.example.eventtrackerkotlincompose.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.eventtrackerkotlincompose.navigation.Graph
import com.example.eventtrackerkotlincompose.navigation.NotificationRouteScreen
import com.example.eventtrackerkotlincompose.screens.notification.AddEventScreen


fun NavGraphBuilder.notificationNavGraph(rootNavController: NavHostController){
    navigation(
        route = Graph.NotificationGraph,
        startDestination = NotificationRouteScreen.NotificationDetail.route
    ){
        composable(route =  NotificationRouteScreen.NotificationDetail.route){
            AddEventScreen(
                onBackToMainScreenClick = {rootNavController.navigateUp()},
                onEventAdded = {}
            )
        }
    }
}