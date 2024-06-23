package com.example.eventtrackerkotlincompose.navigation.graphs

import Login
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navigation
import com.example.eventtrackerkotlincompose.navigation.AuthRouteScreen
import com.example.eventtrackerkotlincompose.navigation.Graph
import com.example.eventtrackerkotlincompose.screens.ForgotPasswordScreen
import com.example.eventtrackerkotlincompose.screens.SignUp

fun NavGraphBuilder.authNavGraph(rootNavController: NavHostController) {
    navigation(
        route = Graph.AuthGraph,
        startDestination = AuthRouteScreen.Login.route
    ) {
        composable(route = AuthRouteScreen.Login.route) {
            Login(
                onLoginSuccessful = {
                    rootNavController.navigate(Graph.MainScreenGraph){
                        popUpTo(AuthRouteScreen.Login.route){inclusive = true}
                    }
                },
                onSignUpClick = {
                    rootNavController.navigate(AuthRouteScreen.SignUp.route)
                },
                onForgotPassword = { rootNavController.navigate(AuthRouteScreen.Forget.route) }
                )
        }
        composable(route = AuthRouteScreen.SignUp.route) {
            SignUp(
                onSignUpComplete = {
                    rootNavController.navigate(Graph.MainScreenGraph){
                        popUpTo(AuthRouteScreen.SignUp.route){inclusive = true}
                    }
                },
                onLoginClick = { rootNavController.navigateUp() }
            )

        }
        composable(route = AuthRouteScreen.Forget.route) {
            ForgotPasswordScreen(
                onClick = { rootNavController.navigateUp() }
            )
        }

    }
}