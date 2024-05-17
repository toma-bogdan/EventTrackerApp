package com.example.eventtrackerkotlincompose.navigation

import Login
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.navDeepLink
import androidx.navigation.navigation
import com.example.eventtrackerkotlincompose.screens.SignUp

fun NavGraphBuilder.authNavGraph(navHostController: NavHostController) {
    navigation(
        route = Graph.AUTHENTICATION,
        startDestination = AuthScreen.Login.route
    ) {
        composable(route = AuthScreen.Login.route) {
            Login(
                onLoginSuccessful = {
                    navHostController.popBackStack()
                    navHostController.navigate(Graph.HOME)
                },
                onSignUpClick = {
                    navHostController.navigate(AuthScreen.SignUp.route)
                }
            )
        }
        composable(route = AuthScreen.SignUp.route) {
            SignUp(
                onSignUpComplete = {
                    navHostController.popBackStack()
                    navHostController.navigate(Graph.HOME)
                },
                onLoginClick = {
                    navHostController.navigate(AuthScreen.Login.route)
                }
            )
        }
    }
}

sealed class AuthScreen(val route: String) {
    object Login : AuthScreen(route = "LOGIN")
    object SignUp : AuthScreen(route = "SIGN_UP")
    object Forgot : AuthScreen(route = "Forgot")
}