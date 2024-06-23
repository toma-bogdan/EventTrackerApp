package com.example.eventtrackerkotlincompose.screens.main

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
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.eventtrackerkotlincompose.config.AppConfig
import com.example.eventtrackerkotlincompose.network.Category
import com.example.eventtrackerkotlincompose.network.Event
import com.example.eventtrackerkotlincompose.viewModels.HomeScreenViewModel


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    viewModel: HomeScreenViewModel = viewModel(),
    innerPadding: PaddingValues,
    onEventClick: (Int) -> Unit,
    searchQuery: String
) {
    var selectedCategory by remember { mutableStateOf<Category?>(null) }
    var categoryMenuExpanded by remember { mutableStateOf(false) }

    Column(modifier = Modifier.padding(innerPadding)) {
        ExposedDropdownMenuBox(
            expanded = categoryMenuExpanded,
            onExpandedChange = { categoryMenuExpanded = !categoryMenuExpanded }
        ) {
            TextField(
                modifier = Modifier
                    .weight(1f)
                    .menuAnchor()
                    .padding(horizontal = 16.dp)
                    .fillMaxWidth(),
                value = selectedCategory?.name?.replace('_', ' ') ?: "All Categories",
                onValueChange = {},
                label = { Text("Filter Event by Category") },
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
                    text = { Text("All Categories") },
                    onClick = {
                        selectedCategory = null
                        categoryMenuExpanded = false
                    },
                    contentPadding = ExposedDropdownMenuDefaults.ItemContentPadding,
                )
                Category.entries.forEach { category ->
                    DropdownMenuItem(
                        text = { Text(category.name.replace('_', ' ')) },
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

        if (viewModel.events == null) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth()) {
                CircularProgressIndicator()
            }
        } else {
            val filteredEvents = viewModel.events!!.filter { event ->
                (searchQuery.isEmpty() || event.name.contains(searchQuery, ignoreCase = true)) &&
                        (selectedCategory == null || event.category == selectedCategory)
            }

            if (filteredEvents.isEmpty()) {
                Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                    Text("No events found.", style = MaterialTheme.typography.bodyLarge)
                }
            } else {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 16.dp),
                    contentPadding = PaddingValues(vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    items(filteredEvents) { event ->
                        EventCard(event = event, onEventClick = onEventClick)
                    }
                }
            }
        }
    }
}

@Composable
fun EventCard(event: Event, onEventClick: (Int) -> Unit) {
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
        }
    }
}

