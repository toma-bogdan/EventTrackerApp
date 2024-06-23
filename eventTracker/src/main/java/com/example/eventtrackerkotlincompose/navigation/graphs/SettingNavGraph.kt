package com.meet.nestednavigationjc.navigation.graphs

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.eventtrackerkotlincompose.navigation.AuthRouteScreen
import com.example.eventtrackerkotlincompose.navigation.Graph
import com.example.eventtrackerkotlincompose.navigation.SettingRouteScreen
import com.example.eventtrackerkotlincompose.screens.settings.SettingDetailScreen

fun NavGraphBuilder.settingNavGraph(rootNavController: NavHostController){
    navigation(
        route = Graph.SettingGraph,
        startDestination = SettingRouteScreen.SettingDetail.route
    ){
        composable(route =  SettingRouteScreen.SettingDetail.route){
            SettingDetailScreen(
                onBackToLoginClick = {
                    rootNavController.navigate(AuthRouteScreen.Login.route){
                        popUpTo(Graph.MainScreenGraph){
                            inclusive = true
                        }
                        launchSingleTop = true
                    }
                }
            )
        }
    }
}