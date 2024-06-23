package com.example.eventtrackerkotlincompose.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import androidx.navigation.navigation
import com.example.eventtrackerkotlincompose.navigation.Graph
import com.example.eventtrackerkotlincompose.navigation.HomeRouteScreen
import com.example.eventtrackerkotlincompose.screens.details.EventDetailScreen

fun NavGraphBuilder.homeNavGraph(rootNavController: NavHostController){
    navigation(
        route = Graph.HOMEGRAPH,
        startDestination = HomeRouteScreen.EventDetail.route
    ){
        composable(
            route =  "${HomeRouteScreen.EventDetail.route}/{eventId}",
            arguments = listOf(navArgument("eventId") {type = NavType.IntType})
        ){backStackEntry ->
            val eventId = backStackEntry.arguments?.getInt("eventId")
            if (eventId != null) {
                EventDetailScreen(
                    onBackToMainScreenClick = { rootNavController.navigateUp() }
                )
            }
        }
    }
}