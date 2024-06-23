package com.example.eventtrackerkotlincompose.navigation
object Graph {
    const val NotificationGraph = "notification"
    const val AuthGraph = "auth"
    const val RootGraph = "rootGraph"
    const val MainScreenGraph = "mainScreenGraph"
    const val SettingGraph = "settingGraph"
    const val HOMEGRAPH = "homeGraph"
    const val AddEvent = "addEvent"
}

sealed class AuthRouteScreen(val route: String) {
    data object Login : AuthRouteScreen("login")
    data object SignUp : AuthRouteScreen("signUp")
    data object Forget : AuthRouteScreen("forget")
}

sealed class MainRouteScreen(var route: String) {

    data object Home : MainRouteScreen("home")
    data object Profile : MainRouteScreen("profile")
    data object Notification : MainRouteScreen("notification")
    data object SavedEvents : MainRouteScreen("savedEvents")
}


sealed class SettingRouteScreen(var route: String) {
    data object SettingDetail : SettingRouteScreen("settingDetail")

}

sealed class NotificationRouteScreen(var route: String) {
    data object NotificationDetail : NotificationRouteScreen("notificationDetail")
}

sealed class HomeRouteScreen(var route: String) {
    data object EventDetail : HomeRouteScreen("eventDetail")
}

sealed class AddEventRouteScreen(var route: String) {
    data object AddEvent : AddEventRouteScreen("addEventScreen") // to do maybe change
}

