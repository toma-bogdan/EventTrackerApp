package com.example.eventtrackerkotlincompose.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.net.toUri
import androidx.navigation.NavDeepLinkRequest
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.Navigation.findNavController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.eventtrackerkotlincompose.screens.BottomBarScreen
import com.example.eventtrackerkotlincompose.screens.HomeScreen
import com.example.eventtrackerkotlincompose.screens.ProfileScreen
import com.example.eventtrackerkotlincompose.screens.ScreenContent
import com.example.eventtrackerkotlincompose.screens.SettingsScreen

@Composable
fun HomeNavGraph(navController: NavHostController, modifier: Modifier = Modifier) {
    NavHost(
        navController = navController,
        startDestination = BottomBarScreen.Home.route,
        modifier = modifier
    ) {
        composable(route = BottomBarScreen.Home.route) {
            ScreenContent(
                name = BottomBarScreen.Home.route,
                onClick = {
                    navController.navigate(Graph.DETAILS)
                })
        }
        composable(route = BottomBarScreen.Profile.route) {
            ScreenContent(
                name = BottomBarScreen.Profile.route,
                onClick = {
                    val request = NavDeepLinkRequest.Builder
                        .fromUri("android-app://androidx.navigation.app/profile".toUri())
                        .build()
                    navController.navigate(request)
                })
        }
        composable(route = BottomBarScreen.Settings.route) {
            SettingsScreen()
        }
        detailsNavGraph(navController = navController)
    }
}
fun NavGraphBuilder.detailsNavGraph(navController: NavHostController) {
    navigation(
        route = Graph.DETAILS,
        startDestination = DetailsScreen.Information.route
    ) {
        composable(route = DetailsScreen.Information.route) {
            ScreenContent(name = DetailsScreen.Information.route) {
                navController.navigate(DetailsScreen.Overview.route)
            }
        }
        composable(route = DetailsScreen.Overview.route) {
            ScreenContent(name = DetailsScreen.Overview.route,
                onClick = {
                    navController.navigateUp()
                    // to do: this is crashing the app, how do i go to home?
                    navController.popBackStack(route = Graph.DETAILS, inclusive = false)
                }
            )
//                navController.popBackStack(
//                    route = DetailsScreen.Information.route,
//                    inclusive = false
//                )

        }
    }
}

sealed class DetailsScreen(val route: String) {
    object Information : DetailsScreen(route = "INFORMATION")
    object Overview : DetailsScreen(route = "OVERVIEW")
}