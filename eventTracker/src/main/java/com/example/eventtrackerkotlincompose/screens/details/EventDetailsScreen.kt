package com.example.eventtrackerkotlincompose.screens.details

import android.util.Log
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Cancel
import androidx.compose.material.icons.filled.Category
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Person
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.ExtendedFloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerkotlincompose.viewModels.EventDetailViewModel
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import coil.compose.rememberAsyncImagePainter
import com.example.eventtrackerkotlincompose.components.RatingBar
import com.example.eventtrackerkotlincompose.config.AppConfig
import com.example.eventtrackerkotlincompose.network.Category
import com.example.eventtrackerkotlincompose.network.Event
import com.example.eventtrackerkotlincompose.network.EventTicket
import com.example.eventtrackerkotlincompose.network.Role
import com.example.eventtrackerkotlincompose.network.UserRegistration
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import java.time.LocalDate

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EventDetailScreen(
    viewModel: EventDetailViewModel = viewModel(),
    onBackToMainScreenClick: () -> Unit
) {
    val event by viewModel.event.collectAsState()
    val eventTickets by viewModel.eventTickets.collectAsState()
    val userRegistrations by viewModel.userRegistrations.collectAsState()
    val user by viewModel.userDetails.collectAsState()
    viewModel.organizer.collectAsState()
    var isEditMode by remember { mutableStateOf(false) }
    val coroutineScope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = event?.name ?: "Loading...") },
                navigationIcon = {
                    IconButton(onClick = { onBackToMainScreenClick() }) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    if (user?.role == Role.ORGANIZER && viewModel.eventBelongsToOrganizer) {
                        Row {
                            IconButton(onClick = { isEditMode = !isEditMode }) {
                                Icon(Icons.Filled.Edit, contentDescription = "Edit Event")
                            }
                            IconButton(onClick = {
                                coroutineScope.launch {
                                    viewModel.deleteEvent()
                                    onBackToMainScreenClick()
                                }
                            }) {
                                Icon(Icons.Default.Clear, contentDescription = "Delete Event")
                            }
                        }
                    }
                }
            )
        }
    ) { innerPadding ->
        event?.let { nonNullEvent ->
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
                    .verticalScroll(rememberScrollState())
            ) {
                EventImageHeader(imageUrl = nonNullEvent.imageUrl)
                EventDetailsSection(
                    event = nonNullEvent,
                    isEditMode = isEditMode,
                    coroutineScope = coroutineScope,
                    viewModel = viewModel,
                    onSave = { updatedEvent ->
                        coroutineScope.launch {
                            viewModel.editEvent(updatedEvent)
                            isEditMode = false
                        }
                    }
                )
                EventTicketsSection(
                    tickets = eventTickets,
                    eventName = event!!.name.replace(" ", ""),
                    userRegistrations = userRegistrations,
                    viewModel = viewModel,
                    coroutineScope = coroutineScope
                )
                EventCommentsSection(
                    comments = viewModel.eventComments,
                    onCommentSubmit = {comment ->
                        coroutineScope.launch {
                            viewModel.addComment(comment)
                        }
                    },
                    user = user!!,
                )
            }
        }
    }
}

@Composable
fun EventImageHeader(imageUrl: String?) {
    imageUrl?.let { url ->
        val correctUrl = url.replace("localhost", AppConfig.SERVER_IP)
        Log.d("image",correctUrl)
        Image(
            painter = rememberAsyncImagePainter(model = correctUrl),
            contentDescription = "Event Banner",
            modifier = Modifier
                .fillMaxWidth()
                .height(250.dp),
            contentScale = ContentScale.Crop
        )
    }
}

@Composable
fun EventDetailsSection(
    event: Event,
    isEditMode: Boolean,
    onSave: (Event) -> Unit,
    coroutineScope: CoroutineScope,
    viewModel: EventDetailViewModel
) {
    var submittedReview by remember {
        mutableStateOf(false)
    }

    Column(modifier = Modifier.padding(16.dp)) {
        if (isEditMode) {
            EditEvent(event, onSave)
        } else {
            Text(
                text = event.name,
                style = MaterialTheme.typography.headlineMedium,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Row(modifier = Modifier.padding(bottom = 4.dp), verticalAlignment = Alignment.CenterVertically) {
                Icon(Icons.Default.Person, contentDescription = null, modifier = Modifier.size(20.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Organizer: ${event.organizer.name}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            if (event.organizer.description.isNotEmpty()) {
                Spacer(modifier = Modifier.size(8.dp))
                Row(
                    modifier = Modifier.padding(bottom = 4.dp),
                    verticalAlignment = Alignment.Top,
                ) {
                    Icon(
                        Icons.Default.Description,
                        contentDescription = null,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Organizer description: ${event.organizer.description}",
                        style = MaterialTheme.typography.bodySmall,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(imageVector = Icons.Filled.LocationOn, contentDescription = "Location")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "${event.location.name}, ${event.location.street}, ${event.location.city}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                Icon(imageVector = Icons.Filled.CalendarToday, contentDescription = "Date")
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "Date: ${event.startDate} to ${event.endDate}",
                    style = MaterialTheme.typography.bodyLarge
                )
            }
            event.description?.let {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(imageVector = Icons.Filled.Description, contentDescription = "Date")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = it,
                        style = MaterialTheme.typography.bodyMedium,
                        modifier = Modifier.padding(bottom = 16.dp)
                    )
                }
            }
            event.category?.let {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(bottom = 8.dp)) {
                    Icon(imageVector = Icons.Filled.Category, contentDescription = "Category")
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = "Category: ${it.name.replace('_', ' ')}",
                        style = MaterialTheme.typography.bodyLarge
                    )
                }
            }
            Text(
                text = "Rate this event:",
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier.padding(top = 8.dp, bottom = 8.dp)
            )
            if (submittedReview) {
                Text(
                    text = "Thanks for the review!",
                    style = MaterialTheme.typography.bodyLarge,
                    modifier = Modifier.padding(top = 4.dp, bottom = 4.dp),
                    color = Color.Green
                )
            }
            RatingBar(
                rating = viewModel.eventRating,
                numberOfReviews = viewModel.ratings.size,
                onRatingChanged = { newRating ->
                coroutineScope.launch {
                    viewModel.addRating(newRating)
                    submittedReview = true
                }
            })
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditEvent(
    event: Event,
    onSave: (Event) -> Unit
) {
    var name by remember { mutableStateOf(event.name) }
    var description by remember { mutableStateOf(event.description ?: "") }
    val location by remember { mutableStateOf(event.location) }
    var startDate by remember { mutableStateOf(event.startDate.toString()) }
    var endDate by remember { mutableStateOf(event.endDate.toString()) }
    var category by remember { mutableStateOf(event.category) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    OutlinedTextField(
        value = name,
        onValueChange = { name = it },
        label = { Text("Event Name") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    OutlinedTextField(
        value = description,
        onValueChange = { description = it },
        label = { Text("Event Description") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    OutlinedTextField(
        value = "${location.name}, ${location.street}, ${location.city}",
        onValueChange = { /* do nothing, location is not editable */ },
        label = { Text("Location") },
        readOnly = true,
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    OutlinedTextField(
        value = startDate,
        onValueChange = { startDate = it },
        label = { Text("Start Date") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
    )
    OutlinedTextField(
        value = endDate,
        onValueChange = { endDate = it },
        label = { Text("End Date") },
        modifier = Modifier.fillMaxWidth(),
        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done)
    )

    ExposedDropdownMenuBox(
        expanded = categoryMenuExpanded,
        onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
    ) {
        TextField(
            modifier = Modifier
                .fillMaxWidth()
                .menuAnchor(),
            value = category?.name?.replace("_", " ") ?: "No category",
            onValueChange = {},
            label = { Text("Category") },
            readOnly = true,
            trailingIcon = {
                ExposedDropdownMenuDefaults.TrailingIcon(
                    expanded = categoryMenuExpanded
                )
            },
            colors = ExposedDropdownMenuDefaults.textFieldColors(
                focusedContainerColor = Color.White,
                unfocusedContainerColor = Color.White
            ),
        )
        DropdownMenu(
            expanded = categoryMenuExpanded,
            onDismissRequest = { categoryMenuExpanded = false }
        ) {
            DropdownMenuItem(
                text = { Text("No category") },
                onClick = {
                    category = null
                    categoryMenuExpanded = false
                },
                contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
            )
            Category.entries.forEach { newCategory ->
                DropdownMenuItem(
                    text = { Text(newCategory.name.replace("_", " ")) },
                    onClick = {
                        category = newCategory
                        categoryMenuExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
            }
        }
    }
    Button(
        onClick = {
            onSave(
                event.copy(
                    name = name,
                    description = description,
                    startDate = LocalDate.parse(startDate),
                    endDate = LocalDate.parse(endDate),
                    category = category
                )
            )
        },
        modifier = Modifier.padding(top = 16.dp)
    ) {
        Text("Save")
    }
}

@Composable
fun EventTicketsSection(
    tickets: List<EventTicket>?,
    eventName: String,
    userRegistrations: List<UserRegistration>?,
    viewModel: EventDetailViewModel,
    coroutineScope: CoroutineScope
) {
    var showAddTicketModal by remember { mutableStateOf(false) }
    tickets?.let {
        Column(modifier = Modifier.padding(16.dp)) {
            Row (
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ){
                Text(
                    text = "Tickets",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                if (viewModel.eventBelongsToOrganizer) {
                    ExtendedFloatingActionButton(
                        text = { Text(text = "Add ticket") },
                        icon = { Icon(Icons.Default.Add, contentDescription = "Add Ticket") },
                        onClick = { showAddTicketModal = true },
                        containerColor = MaterialTheme.colorScheme.primary,
                        contentColor = Color.White,
                        modifier = Modifier
                            .height(40.dp)
                            .width(140.dp)
                    )
                }
            }
            it.forEach { ticket ->
                TicketCard(ticket, eventName, userRegistrations, viewModel, coroutineScope)
            }
        }
    }
    if (showAddTicketModal) {
        AddTicketModal(
            onDismiss = { showAddTicketModal = false },
            onConfirm = { ticketName, ticketDescription, ticketPrice ->
                coroutineScope.launch {
                    viewModel.addNewTicket(ticketName, ticketDescription, ticketPrice)
                }
                showAddTicketModal = false
            }
        )
    }

}

@Composable
fun TicketCard(
    ticket: EventTicket,
    eventName: String,
    userRegistrations: List<UserRegistration>?,
    viewModel: EventDetailViewModel,
    coroutineScope: CoroutineScope
) {
    var showDialog by remember { mutableStateOf(false) }
    val isRegistered = userRegistrations?.any { reg -> reg.eventInfo.id == ticket.id } == true

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(8.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
        elevation = CardDefaults.cardElevation()
    ) {
        if (viewModel.eventBelongsToOrganizer) {
            IconButton(onClick = {
                coroutineScope.launch {
                    viewModel.deleteTicket(ticket)
                }
            }) {
                Icon(Icons.Default.Clear, contentDescription = "")
            }
        }
        Column(
            modifier = Modifier
                .padding(16.dp)
                .fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = ticket.name,
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(bottom = 8.dp)
            )
            Text(
                text = ticket.description,
                style = MaterialTheme.typography.bodyLarge,
                modifier = Modifier
                    .padding(bottom = 8.dp)
                    .align(Alignment.CenterHorizontally)
            )
            if (ticket.price > 0) {
                Text(
                    text = "Price: $${ticket.price}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Medium
                )
            }
            if (isRegistered) {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.unregisterEvent(ticket.id)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color.Red)
                ) {
                    Icon(Icons.Filled.Cancel, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Cancel confirmation")
                }
            } else {
                Button(
                    onClick = {
                        if (ticket.price > 0) {
                            showDialog = true
                        } else {
                            coroutineScope.launch {
                                viewModel.registerEvent(ticket.id)
                            }
                        }
                    },
                    colors = ButtonDefaults.buttonColors(MaterialTheme.colorScheme.primary)
                ) {
                    Icon(Icons.Filled.Event, contentDescription = null)
                    Spacer(modifier = Modifier.width(4.dp))
                    Text("Attend Event")
                }
            }
        }
    }

    if (showDialog) {
        TicketInfoDialog(
            ticket = ticket,
            onDismiss = { showDialog = false },
            onConfirm = {
                coroutineScope.launch {
                    viewModel.registerEvent(ticket.id)
                }
                showDialog = false
            },
            eventName = eventName
        )
    }
}

@Composable
fun TicketInfoDialog(ticket: EventTicket, onDismiss: () -> Unit, onConfirm: () -> Unit, eventName: String) {
    Dialog(onDismissRequest = onDismiss) {
        Card(
            shape = RoundedCornerShape(16.dp),
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .heightIn(min = 300.dp, max = 520.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp)
            ) {
                Text(
                    text = ticket.name,
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = ticket.description,
                    style = MaterialTheme.typography.titleLarge,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Text(
                    text = "Price: $${ticket.price}",
                    style = MaterialTheme.typography.titleLarge,
                    fontWeight = FontWeight.Medium,
                    modifier = Modifier.padding(bottom = 8.dp)
                )
                Spacer(modifier = Modifier.padding(10.dp))
                Text(
                    text = "Visit these authorized ticket sellers links for purchasing this ticket:" +
                            " https://m.iabilet.ro/$eventName\n\n",
                    style = MaterialTheme.typography.bodyLarge,
                    textAlign = TextAlign.Justify,
                )
                Text(
                    text = "Currently, you cannot buy tickets directly from our app," +
                            " but confirming your attendance really helps us",
                    style = MaterialTheme.typography.bodyMedium,
                    textAlign = TextAlign.Left,
                    modifier = Modifier.padding(bottom = 5.dp)
                )
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    TextButton(onClick = onDismiss) {
                        Text("Dismiss")
                    }
                    Button(onClick = onConfirm) {
                        Text("Confirm Attendance")
                    }
                }
            }
        }
    }
}
@Composable
fun AddTicketModal(onDismiss: () -> Unit, onConfirm: (String, String, Double) -> Unit) {
    var ticketName by remember { mutableStateOf("") }
    var ticketDescription by remember { mutableStateOf("") }
    var ticketPrice by remember { mutableStateOf("") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = {
            Text(text = "Add New Ticket")
        },
        text = {
            Column {
                OutlinedTextField(
                    value = ticketName,
                    onValueChange = { ticketName = it },
                    label = { Text("Ticket Name") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = ticketDescription,
                    onValueChange = { ticketDescription = it },
                    label = { Text("Ticket Description") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
                OutlinedTextField(
                    value = ticketPrice,
                    onValueChange = { ticketPrice = it },
                    label = { Text("Ticket Price") },
                    keyboardOptions = KeyboardOptions.Default.copy(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                )
            }
        },
        confirmButton = {
            Button(onClick = {
                val price = ticketPrice.toDoubleOrNull() ?: 0.0
                onConfirm(ticketName, ticketDescription, price)
            }) {
                Text("Add Ticket")
            }
        },
        dismissButton = {
            Button(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
}
