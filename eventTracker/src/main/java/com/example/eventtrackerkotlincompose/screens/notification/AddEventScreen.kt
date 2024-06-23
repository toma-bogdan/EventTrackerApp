package com.example.eventtrackerkotlincompose.screens.notification

import android.net.Uri
import android.util.Log
import android.widget.Toast
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.DateRange
import androidx.compose.material.icons.filled.DriveFileRenameOutline
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.Checkbox
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilledTonalButton
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
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.rememberAsyncImagePainter
import com.example.eventtrackerkotlincompose.components.CustomTextField
import com.example.eventtrackerkotlincompose.network.Category
import com.example.eventtrackerkotlincompose.network.Location
import com.example.eventtrackerkotlincompose.viewModels.AddEventViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEventScreen(
    viewModel: AddEventViewModel = viewModel(),
    onEventAdded: () -> Unit,
    onBackToMainScreenClick: () -> Unit
) {
    var eventName by remember { mutableStateOf("") }
    var locationId by remember { mutableStateOf("") }
    var startDate by remember { mutableStateOf("") }
    var endDate by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var selectedImageUri by remember { mutableStateOf<Uri?>(null) }
    var isFreeEntry by remember { mutableStateOf(true) }
    var ticketName by remember { mutableStateOf("") }
    var ticketDescription by remember { mutableStateOf("") }
    var ticketPrice by remember { mutableStateOf("") }
    val coroutineScope = rememberCoroutineScope()
    val locations = viewModel.locations.collectAsState()
    var locationMenuExpanded by remember { mutableStateOf(false) }
    var newLocationDialogOpen by remember { mutableStateOf(false) }
    var newLocationName by remember { mutableStateOf("") }
    var newLocationCity by remember { mutableStateOf("") }
    var newLocationStreet by remember { mutableStateOf("") }
    var categoryMenuExpanded by remember { mutableStateOf(false) }
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    val context = LocalContext.current
    val pickImageLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        uri?.let {
            selectedImageUri = it
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Add New Event") },
                navigationIcon = {
                    IconButton(onClick = onBackToMainScreenClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
                .padding(16.dp)
                .verticalScroll(rememberScrollState())
        ) {
             viewModel.addedEvent?.let {
                 if (it) {
                     Text(
                         text = "Successfully created event!",
                         color = Color.Green,
                         modifier = Modifier.padding(all = 8.dp),
                         style = MaterialTheme.typography.titleLarge
                     )
                 } else {
                     Text(
                         text = viewModel.submitError,
                         color = Color.Red,
                         modifier = Modifier.padding(all = 8.dp),
                         style = MaterialTheme.typography.titleLarge
                     )
                 }
            }
            CustomTextField(labelValue = "Event Name", imgVector = Icons.Default.DriveFileRenameOutline) {
                eventName = it
            }
            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = locationMenuExpanded,
                onExpandedChange = { locationMenuExpanded = !locationMenuExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = locationId,
                    onValueChange = { locationId = it },
                    label = { Text("Location") },
                    readOnly = true,
                    trailingIcon = {
                        ExposedDropdownMenuDefaults.TrailingIcon(
                            expanded = locationMenuExpanded
                        )
                    },
                    colors = ExposedDropdownMenuDefaults.textFieldColors(
                        focusedContainerColor = Color.White,
                        unfocusedContainerColor = Color.White
                    ),
                )
                DropdownMenu(
                    expanded = locationMenuExpanded,
                    onDismissRequest = { locationMenuExpanded = false }
                ) {
                    locations.value?.forEach { selectionOption ->
                        DropdownMenuItem(
                            text = { Text(selectionOption.name + ", " + selectionOption.street) },
                            onClick = {
                                locationId = selectionOption.name
                                locationMenuExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                    DropdownMenuItem(
                        text = { Text("Create new location") },
                        onClick = {
                            newLocationDialogOpen = true
                            locationMenuExpanded = false
                        },
                        contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            ExposedDropdownMenuBox(
                expanded = categoryMenuExpanded,
                onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
            ) {
                TextField(
                    modifier = Modifier
                        .fillMaxWidth()
                        .menuAnchor(),
                    value = selectedCategory?.name?.replace("_", " ") ?: "",
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
                    Category.entries.forEach { category ->
                        DropdownMenuItem(
                            text = { Text(category.name.replace("_", " ")) },
                            onClick = {
                                selectedCategory = category
                                categoryMenuExpanded = false
                            },
                            contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(16.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                OutlinedTextField(
                    value = startDate,
                    onValueChange = { startDate = it },
                    label = { Text("Start Date") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("yyyy-mm-dd") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                    }
                )
                Spacer(modifier = Modifier.width(16.dp))
                OutlinedTextField(
                    value = endDate,
                    onValueChange = { endDate = it },
                    label = { Text("End Date") },
                    modifier = Modifier.weight(1f),
                    placeholder = { Text("yyyy-mm-dd") },
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next),
                    leadingIcon = {
                        Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                    }
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            OutlinedTextField(
                value = description,
                onValueChange = { description = it },
                label = { Text("Description") },
                modifier = Modifier.fillMaxWidth(),
                maxLines = 4,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Done),
                leadingIcon = {
                    Icon(imageVector = Icons.Default.DateRange, contentDescription = "")
                }
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(verticalAlignment = Alignment.CenterVertically) {
                Checkbox(
                    checked = isFreeEntry,
                    onCheckedChange = { isFreeEntry = it }
                )
                Text(text = "Free Entry")
            }

            if (!isFreeEntry) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween
                ) {
                    OutlinedTextField(
                        value = ticketName,
                        onValueChange = { ticketName = it },
                        label = { Text("Ticket Name") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                    )
                    Spacer(modifier = Modifier.width(16.dp))
                    OutlinedTextField(
                        value = ticketPrice,
                        onValueChange = { ticketPrice = it },
                        label = { Text("Ticket Price") },
                        modifier = Modifier.weight(1f),
                        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number, imeAction = ImeAction.Done)
                    )
                }
                Spacer(modifier = Modifier.height(8.dp))
                OutlinedTextField(
                    value = ticketDescription,
                    onValueChange = { ticketDescription = it },
                    label = { Text("Ticket Description (Optional)") },
                    modifier = Modifier.fillMaxWidth(),
                    keyboardOptions = KeyboardOptions(imeAction = ImeAction.Next)
                )
            }

            Text(
                text = "Upload Event Thumbnail",
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.primary,
                modifier = Modifier.align(Alignment.CenterHorizontally)
            )
            Spacer(modifier = Modifier.height(8.dp))
            FilledTonalButton(
                onClick = { pickImageLauncher.launch("image/*") },
                modifier = Modifier.align(Alignment.CenterHorizontally)
            ) {
                Text(text = "Select Image")
            }

            selectedImageUri?.let { uri ->
                Spacer(modifier = Modifier.height(8.dp))
                Image(
                    painter = rememberAsyncImagePainter(uri),
                    contentDescription = null,
                    modifier = Modifier
                        .padding(8.dp)
                        .fillMaxWidth()
                        .size(200.dp)
                        .align(Alignment.CenterHorizontally),
                    contentScale = ContentScale.Crop
                )
            }

            Spacer(modifier = Modifier.height(16.dp))
            if (viewModel.isLoading) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                    CircularProgressIndicator()
                }
            } else {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.addEvent(
                                eventName,
                                locationId,
                                startDate,
                                endDate,
                                description,
                                selectedImageUri,
                                context,
                                isFreeEntry,
                                ticketName,
                                ticketPrice.toDoubleOrNull(),
                                ticketDescription,
                                selectedCategory
                            )
                        }
                    },
                    modifier = Modifier
                        .align(Alignment.CenterHorizontally)
                        .fillMaxWidth()
                        .size(50.dp)
                ) {
                    Text("Create Event")
                }
            }
            if (newLocationDialogOpen) {
                AlertDialog(
                    onDismissRequest = { newLocationDialogOpen = false },
                    title = { Text("Create New Location") },
                    text = {
                        Column {
                            OutlinedTextField(
                                value = newLocationName,
                                onValueChange = { newLocationName = it },
                                label = { Text("New Location") }
                            )
                            OutlinedTextField(
                                value = newLocationCity,
                                onValueChange = { newLocationCity = it },
                                label = { Text("Location city") }
                            )
                            OutlinedTextField(
                                value = newLocationStreet,
                                onValueChange = { newLocationStreet = it },
                                label = { Text("Location Address") }
                            )
                        }
                    },
                    confirmButton = {
                        TextButton(
                            onClick = {
                                viewModel.createLocation(Location(0,newLocationName,newLocationStreet,newLocationCity))
                                locationId = newLocationName
                                newLocationName = ""
                                newLocationDialogOpen = false
                            }
                        ) {
                            Text("Create")
                        }
                    },
                    dismissButton = {
                        TextButton(
                            onClick = {
                                newLocationName = ""
                                newLocationDialogOpen = false
                            }
                        ) {
                            Text("Cancel")
                        }
                    }
                )
            }
        }
    }
}
