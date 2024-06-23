package com.example.eventtrackerkotlincompose

import android.content.Context
import android.content.pm.PackageManager
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.content.ContextCompat
import com.arcgismaps.toolkit.geoviewcompose.MapView
import androidx.lifecycle.viewmodel.compose.viewModel
import com.arcgismaps.ArcGISEnvironment
import com.arcgismaps.location.LocationDisplayAutoPanMode
import com.arcgismaps.toolkit.geoviewcompose.rememberLocationDisplay
import com.example.eventtrackerkotlincompose.viewModels.MapViewModel
import android.Manifest
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.material.ExperimentalMaterialApi
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.unit.dp
import com.google.accompanist.pager.ExperimentalPagerApi
import kotlinx.coroutines.launch
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.ui.Alignment
import com.example.eventtrackerkotlincompose.network.Event
import com.google.accompanist.pager.*


fun checkPermissions(context: Context): Boolean {
    // Check permissions to see if both permissions are granted.
    // Coarse location permission.
    val permissionCheckCoarseLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_COARSE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED
    // Fine location permission.
    val permissionCheckFineLocation = ContextCompat.checkSelfPermission(
        context,
        Manifest.permission.ACCESS_FINE_LOCATION
    ) == PackageManager.PERMISSION_GRANTED

    return permissionCheckCoarseLocation && permissionCheckFineLocation
}

@Composable
fun RequestPermissions(context: Context, onPermissionsGranted: () -> Unit) {

    // Create an activity result launcher using permissions contract and handle the result.
    val activityResultLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        // Check if both fine & coarse location permissions are true.
        if (permissions.all { it.value }) {
            onPermissionsGranted()
        } else {
            showError(context, "Location permissions were denied")
        }
    }

    LaunchedEffect(Unit) {
        activityResultLauncher.launch(
            // Request both fine and coarse location permissions.
            arrayOf(
                Manifest.permission.ACCESS_COARSE_LOCATION,
                Manifest.permission.ACCESS_FINE_LOCATION
            )
        )
    }

}
fun showError(context: Context, message: String) {
    Toast.makeText(context, message, Toast.LENGTH_LONG).show()
}

@OptIn(ExperimentalMaterialApi::class)
@Composable
fun MapMainScreen(
    viewModel: MapViewModel = viewModel(),
    onEventClick: (Int) -> Unit
) {
    val context = LocalContext.current
    val coroutineScope = rememberCoroutineScope()
    val map by viewModel.map.collectAsState()
    val graphicsOverlay by viewModel.graphicsOverlay.collectAsState()
    val events by viewModel.events.collectAsState()
    val showEventBottomSheet by viewModel.showEventBottomSheet

    ArcGISEnvironment.applicationContext = context.applicationContext

    val locationDisplay = rememberLocationDisplay()

    if (checkPermissions(context)) {
        LaunchedEffect(Unit) {
            locationDisplay.dataSource.start()
        }
    } else {
        RequestPermissions(
            context = context,
            onPermissionsGranted = {
                coroutineScope.launch {
                    locationDisplay.dataSource.start()
                }
            }
        )

    }
    val bottomSheetState = rememberModalBottomSheetState(
        initialValue = ModalBottomSheetValue.Hidden,
        skipHalfExpanded = true
    )

    LaunchedEffect(showEventBottomSheet) {
        if (showEventBottomSheet) {
            bottomSheetState.show()
        } else {
            bottomSheetState.hide()
        }
    }

    Scaffold { innerPadding ->
        ModalBottomSheetLayout(
            sheetState = bottomSheetState,
            sheetContent = {
                if (showEventBottomSheet && events != null) {
                    EventBottomSheet(events = events!!, onEventClick = onEventClick)
                }
            }
        ) {
            MapView(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                arcGISMap = map,
                graphicsOverlays = listOf(graphicsOverlay),
                mapViewProxy = viewModel.mapViewProxy,
                onSingleTapConfirmed = {
                    coroutineScope.launch {
                        viewModel.identify(it)
                    }
                },
                onPan = { viewModel.dismissBottomSheet() },
            locationDisplay = locationDisplay
            )
        }
    }
}
@OptIn(ExperimentalPagerApi::class)
@Composable
fun EventBottomSheet(events: List<Event>, onEventClick: (Int) -> Unit) {
    val pagerState = rememberPagerState()

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .heightIn(min = 200.dp, max = 330.dp)
            .background(MaterialTheme.colors.surface)
    ) {
        if (events.isNotEmpty()) {
            Column {
                HorizontalPager(
                    count = events.size,
                    state = pagerState
                ) { page ->
                    EventDetails(event = events[page], onEventClick)
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.Center,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Text(
                        text = "Event ${pagerState.currentPage + 1} of ${events.size}",
                        style = MaterialTheme.typography.caption
                    )
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Hint to swipe
                if (events.size > 1) {
                    Row(
                        horizontalArrangement = Arrangement.Center,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Text(
                            text = "Swipe left or right to see more events",
                            style = MaterialTheme.typography.body2,
                            color = MaterialTheme.colors.primary
                        )
                    }
                }
            }

            if (events.size > 1) {
                Icon(
                    imageVector = Icons.Default.MoreHoriz,
                    contentDescription = "Multiple events indicator",
                    tint = MaterialTheme.colors.primary,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(8.dp)
                        .size(24.dp)
                )
            }
        } else {
            Text("No events available")
        }
    }
}

@Composable
fun EventDetails(event: Event, onEventClick: (Int) -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
    ) {
        Text(text = event.name, style = MaterialTheme.typography.h6)
        Spacer(modifier = Modifier.height(8.dp))
        event.description?.let { Text(text = it, style = MaterialTheme.typography.body1) }
        Spacer(modifier = Modifier.height(8.dp))
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.Start
        ) {
            Text(
                text = event.startDate.toString(),
                style = MaterialTheme.typography.body2,
            )
            if (event.endDate != event.startDate) {
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "to ${event.endDate}",
                    style = MaterialTheme.typography.body2,
                )
            }
        }
        Spacer(modifier = Modifier.height(8.dp))
        Text(
            text = event.location.name,
            style = MaterialTheme.typography.subtitle1,
            color = MaterialTheme.colors.onSurface.copy(alpha = 0.6f)
        )
        Spacer(modifier = Modifier.height(16.dp))
        Row(
            horizontalArrangement = Arrangement.Start,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = Icons.Default.LocationOn,
                contentDescription = "Location",
                tint = MaterialTheme.colors.primary
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = "${event.location.city}, ${event.location.street}",
                style = MaterialTheme.typography.body2
            )
        }
        Button(
            onClick = { onEventClick(event.id) },
            colors = ButtonDefaults.buttonColors(backgroundColor = MaterialTheme.colors.secondary),
            modifier = Modifier
                .fillMaxWidth()
        ) {
            Text(text = "See Event")
        }
    }
}

