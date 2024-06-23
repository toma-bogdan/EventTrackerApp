package com.example.eventtrackerkotlincompose.screens.main

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Place
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eventtrackerkotlincompose.config.AppConfig
import com.example.eventtrackerkotlincompose.network.Event
import com.example.eventtrackerkotlincompose.network.Role
import com.example.eventtrackerkotlincompose.qrScanner.QrScannerActivity
import com.example.eventtrackerkotlincompose.viewModels.SavedEventsViewModel
import com.google.zxing.BarcodeFormat
import com.google.zxing.EncodeHintType
import com.google.zxing.MultiFormatWriter
import com.google.zxing.common.BitMatrix
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel
import java.time.LocalDate


@Composable
fun SavedEvents(
    viewModel: SavedEventsViewModel = viewModel(),
    innerPadding: PaddingValues,
    onEventClick: (Int) -> Unit
) {
    val registeredEvents by viewModel.filteredEvents
    val userDetails by viewModel.userDetails.collectAsState()

    val isOrganizer = userDetails?.role == Role.ORGANIZER

    EventList(
        events = registeredEvents,
        innerPadding = innerPadding,
        onEventClick = onEventClick,
        getTicketCode = viewModel::getTicketCode,
        isOrganizer = isOrganizer
    )
}

@Composable
fun EventList(
    events: List<Event>?,
    innerPadding: PaddingValues,
    onEventClick: (Int) -> Unit,
    getTicketCode: (Int) -> String,
    isOrganizer: Boolean
) {
    val context = LocalContext.current
    val currentDate = LocalDate.now()

    if (events.isNullOrEmpty()) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            Text(if (isOrganizer) "You have not created any events yet." else "You have not registered for any events yet.")
        }
    } else {
        val (upcomingEvents, pastEvents) = events.partition { it.startDate >= currentDate }

        LazyColumn(
            contentPadding = innerPadding,
            verticalArrangement = Arrangement.spacedBy(16.dp),
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp),
        ) {
            items(upcomingEvents) { event ->
                RegisteredEventCard(
                    event = event,
                    onEventClick = onEventClick,
                    onScanTicketClick = { startQrScannerActivity(context) },
                    getTicketCode = getTicketCode,
                    isOrganizer = isOrganizer
                )
            }
            if (pastEvents.isNotEmpty()) {
                item {
                    Column {
                        Text(
                            text = "Past Events",
                            style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                        Divider(
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(bottom = 8.dp)
                        )
                    }
                }
                items(pastEvents) { event ->
                    RegisteredEventCard(
                        event = event,
                        onEventClick = onEventClick,
                        onScanTicketClick = { startQrScannerActivity(context) },
                        getTicketCode = getTicketCode,
                        isOrganizer = isOrganizer
                    )
                }
            }
        }
    }
}

@Composable
fun RegisteredEventCard(
    event: Event,
    onEventClick: (Int) -> Unit,
    onScanTicketClick: () -> Unit,
    getTicketCode: (Int) -> String,
    isOrganizer: Boolean
) {
    Card(
        shape = MaterialTheme.shapes.medium,
        elevation = CardDefaults.cardElevation(8.dp),
        modifier = Modifier
            .fillMaxWidth()
            .clickable {
                onEventClick(event.id)
            }
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            event.imageUrl?.let { url ->
                Log.d("url",url)
                val correctUrl = url.replace("localhost", AppConfig.SERVER_IP)
                AsyncImage(
                    model = correctUrl,
                    contentDescription = "",
                    modifier = Modifier
                        .height(200.dp)
                        .fillMaxWidth()
                        .padding(bottom = 8.dp),
                    contentScale = ContentScale.Crop
                )
            }
            Text(
                text = event.name,
                style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(bottom = 4.dp)
            )
            Row(modifier = Modifier.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Organizer: ${event.organizer.name}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(modifier = Modifier.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Place, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Location: ${event.location.name}, ${event.location.street}, ${event.location.city}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(modifier = Modifier.padding(bottom = 2.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Start Date: ${event.startDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }
            Row(modifier = Modifier.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Event, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "End Date: ${event.endDate}",
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            event.description?.let {
                Row(modifier = Modifier.padding(bottom = 8.dp), verticalAlignment = Alignment.Top) {
                    Icon(Icons.Default.Description, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            event.category?.let {
                Row(modifier = Modifier.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Category, contentDescription = null, modifier = Modifier.size(20.dp))
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Category: ${it.name.replace('_', ' ')}",
                        style = MaterialTheme.typography.bodyMedium
                    )
                }
            }

            if (isOrganizer) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    Button(
                        onClick = onScanTicketClick,
                        colors = ButtonDefaults.buttonColors(Color(0xFF92A3FD))
                    ) {
                        Text("Scan Tickets")
                    }
                    Button(
                        onClick = { onEventClick(event.id) },
                        colors = ButtonDefaults.buttonColors(Color(0xFF92A3FD))
                    ) {
                        Text("Edit Event")
                    }
                }
            } else {
                val qrBitmap = generateQrCode(getTicketCode(event.id))
                qrBitmap?.let {
                    Image(
                        bitmap = it.asImageBitmap(),
                        contentDescription = "QR Code",
                        modifier = Modifier
                            .size(100.dp)
                            .padding(top = 8.dp)
                    )
                }
            }
        }
    }
}

fun startQrScannerActivity(context: Context) {
    val intent = Intent(context, QrScannerActivity::class.java)
    context.startActivity(intent)
}
fun generateQrCode(text: String, width: Int = 200, height: Int = 200): Bitmap? {
    return try {
        val hints = hashMapOf<EncodeHintType, Any>()
        hints[EncodeHintType.ERROR_CORRECTION] = ErrorCorrectionLevel.H
        val bitMatrix: BitMatrix = MultiFormatWriter().encode(text, BarcodeFormat.QR_CODE, width, height, hints)
        val bitmap = Bitmap.createBitmap(width, height, Bitmap.Config.RGB_565)
        for (x in 0 until width) {
            for (y in 0 until height) {
                bitmap.setPixel(x, y, if (bitMatrix[x, y]) android.graphics.Color.BLACK else android.graphics.Color.WHITE)
            }
        }
        bitmap
    } catch (e: Exception) {
        e.printStackTrace()
        null
    }
}
