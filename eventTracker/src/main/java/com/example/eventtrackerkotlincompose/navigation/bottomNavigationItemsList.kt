package com.example.eventtrackerkotlincompose.navigation

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material.icons.outlined.*

val bottomNavigationItemsList = listOf(
    NavigationItem(
        title = "Home",
        route = MainRouteScreen.Home.route,
        selectedIcon = Icons.Filled.Home,
        unSelectedIcon = Icons.Outlined.Home,
    ),
    NavigationItem(
        title = "Map",
        route = MainRouteScreen.Notification.route,
        selectedIcon = Icons.Outlined.Map,
        unSelectedIcon = Icons.Outlined.Map,
    ),
    NavigationItem(
        title = "Saved",
        route = MainRouteScreen.SavedEvents.route,
        selectedIcon = Icons.Filled.Bookmarks,
        unSelectedIcon = Icons.Outlined.Bookmarks,
    ),
    NavigationItem(
        title = "Profile",
        route = MainRouteScreen.Profile.route,
        selectedIcon = Icons.Filled.Person,
        unSelectedIcon = Icons.Outlined.Person,
    ),
)