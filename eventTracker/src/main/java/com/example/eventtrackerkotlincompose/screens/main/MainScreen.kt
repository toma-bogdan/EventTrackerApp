package com.example.eventtrackerkotlincompose.screens.main

import android.util.Log
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.ContentAlpha
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.navigation.AddEventRouteScreen
import com.example.eventtrackerkotlincompose.navigation.bottomNavigationItemsList
import com.example.eventtrackerkotlincompose.navigation.BottomNavigationBar
import com.example.eventtrackerkotlincompose.navigation.MainRouteScreen
import com.example.eventtrackerkotlincompose.navigation.graphs.MainNavGraph
import com.example.eventtrackerkotlincompose.network.Role

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(
    rootNavHostController: NavHostController,
    homeNavController : NavHostController = rememberNavController()
) {
    val context = LocalContext.current
    val dataStore = UserDetailsStore(context)
    val user by dataStore.getUser.collectAsState(initial = null)
    val navBackStackEntry by homeNavController.currentBackStackEntryAsState()
    val currentRoute by remember(navBackStackEntry) {
        derivedStateOf {
            navBackStackEntry?.destination?.route
        }
    }
    var searchQuery by remember { mutableStateOf("") }
    val topBarTitle by remember(currentRoute) {
        derivedStateOf {
            if (currentRoute != null) {
                bottomNavigationItemsList[bottomNavigationItemsList.indexOfFirst {
                    it.route == currentRoute
                }].title
            } else {
                bottomNavigationItemsList[0].title
            }
        }
    }
    Scaffold(
        topBar = {
            TopAppBar(title = {
                Row (
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(5.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    if (user?.role != Role.USER && topBarTitle == "Home") {
                        ExtendedFloatingActionButton(
                            onClick = { rootNavHostController.navigate(AddEventRouteScreen.AddEvent.route) },
                            text = {
                                Text(
                                    text = "Add Event",
                                    color = Color.White,
                                    fontSize = 12.sp,
                                )
                            },
                            icon = {
                                Icon(
                                    Icons.Filled.Add,
                                    contentDescription = "Add event",
                                    tint = Color.White
                                )
                            },
                            containerColor = Color(0xFF92A3FD),
                            contentColor = Color.White,
                            modifier = Modifier
                                .height(40.dp)
                                .width(130.dp)
                        )
                    } else {
                        Text(text = topBarTitle)
                    }
                    if (topBarTitle == "Home") {
                        OutlinedTextField(
                            value = searchQuery,
                            onValueChange = { newValue -> searchQuery = newValue },
                            label = { Text("Search") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            modifier = Modifier.height(65.dp).width(140.dp),
                            keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                        )
                    }
                }
            },
            )
        },
        bottomBar = {
            BottomNavigationBar(items = bottomNavigationItemsList, currentRoute = currentRoute ){ currentNavigationItem->
                homeNavController.navigate(currentNavigationItem.route){
                    homeNavController.graph.startDestinationRoute?.let { startDestinationRoute ->
                        popUpTo(startDestinationRoute) {
                            saveState = true
                        }
                    }
                    launchSingleTop = true
                    restoreState = true
                }
            }
        }
    ) {innerPadding->
        MainNavGraph(
            rootNavHostController,
            homeNavController,
            innerPadding,
            searchQuery
        )
    }
}