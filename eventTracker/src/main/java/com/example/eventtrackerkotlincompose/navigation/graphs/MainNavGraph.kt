package com.example.eventtrackerkotlincompose.navigation.graphs

import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.example.eventtrackerkotlincompose.MapMainScreen
import com.example.eventtrackerkotlincompose.navigation.AuthRouteScreen
import com.example.eventtrackerkotlincompose.navigation.Graph
import com.example.eventtrackerkotlincompose.navigation.HomeRouteScreen
import com.example.eventtrackerkotlincompose.navigation.MainRouteScreen
import com.example.eventtrackerkotlincompose.screens.main.HomeScreen
import com.example.eventtrackerkotlincompose.screens.main.ProfileScreen
import com.example.eventtrackerkotlincompose.screens.main.SavedEvents


@Composable
fun MainNavGraph(
    rootNavController: NavHostController,
    homeNavController: NavHostController,
    innerPadding: PaddingValues,
    searchQuery: String,
) {
    NavHost(
        navController = homeNavController,
        route = Graph.MainScreenGraph,
        startDestination = MainRouteScreen.Home.route
    ) {
        composable(route = MainRouteScreen.Home.route) {
            HomeScreen(
                innerPadding = innerPadding,
                searchQuery = searchQuery,
                onEventClick = {id ->
                    rootNavController.navigate("${HomeRouteScreen.EventDetail.route}/${id}")
                })
        }
        composable(route = MainRouteScreen.Notification.route) {
            MapMainScreen(onEventClick= {id ->
                rootNavController.navigate("${HomeRouteScreen.EventDetail.route}/${id}")
            })

        }
        composable(route = MainRouteScreen.Profile.route) {
            ProfileScreen(
                innerPadding = innerPadding,
                onLogoutClick = {rootNavController.navigate(AuthRouteScreen.Login.route)},
                )
        }
        composable(route = MainRouteScreen.SavedEvents.route) {
            SavedEvents(
                innerPadding = innerPadding,
                onEventClick = {id ->
                    rootNavController.navigate("${HomeRouteScreen.EventDetail.route}/${id}")
                })
        }
    }
}