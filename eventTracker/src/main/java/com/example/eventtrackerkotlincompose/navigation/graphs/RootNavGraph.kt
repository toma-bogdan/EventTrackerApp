package com.meet.nestednavigationjc.navigation.graphs

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.example.eventtrackerkotlincompose.navigation.Graph
import com.example.eventtrackerkotlincompose.navigation.graphs.addEventNavGraph
import com.example.eventtrackerkotlincompose.navigation.graphs.authNavGraph
import com.example.eventtrackerkotlincompose.navigation.graphs.homeNavGraph
import com.example.eventtrackerkotlincompose.navigation.graphs.notificationNavGraph
import com.example.eventtrackerkotlincompose.screens.main.MainScreen

@Composable
fun RootNavGraph(isAuth : Boolean) {
    val rootNavController = rememberNavController()

    NavHost(
        navController = rootNavController,
        route = Graph.RootGraph,
        startDestination = if (isAuth) Graph.MainScreenGraph else Graph.AuthGraph
    ) {
        authNavGraph(rootNavController = rootNavController)
        composable(route = Graph.MainScreenGraph){
            MainScreen(rootNavHostController = rootNavController)
        }
        homeNavGraph(rootNavController)
        notificationNavGraph(rootNavController)
        settingNavGraph(rootNavController)
        addEventNavGraph(rootNavController)
    }
}