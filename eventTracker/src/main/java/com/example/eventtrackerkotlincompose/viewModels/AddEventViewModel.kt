package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.network.Category
import com.example.eventtrackerkotlincompose.network.EventsRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.Location
import com.example.eventtrackerkotlincompose.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class AddEventViewModel (application: Application): AndroidViewModel(application) {
    private val apiService = HttpService(NetworkClient.client)
    private val eventsRepository = EventsRepository(apiService)
    private val repository = UserDetailsStore(application)
    private val userToken = repository.getToken
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    private val organizer = repository.getOrganizer
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    private val _locations = MutableStateFlow<List<Location>?>(null)
    val locations: StateFlow<List<Location>?> = _locations.asStateFlow()
    var submitError by mutableStateOf("")
    var isLoading by mutableStateOf(false)
    var addedEvent by mutableStateOf<Boolean?>(null)


    init {
        viewModelScope.launch {
            userToken.collect {
                if (!it.isNullOrEmpty()) {
                    try {
                        _locations.value = eventsRepository.getLocations(it)
                        Log.d("location",_locations.value.toString())
                    } catch (e: Exception) {
                        val error = e.localizedMessage ?: "Unknown error"
                        Log.e("error", error)
                    }
                }
            }
        }
    }
    fun createLocation(location: Location) {
        val currentLocations = (_locations.value?.plus(location)) ?: listOf(location)
        _locations.value = currentLocations
    }
    private suspend fun uploadImage(uri: Uri, context: Context): String {
        val response = apiService.uploadImage(uri,context)
        return response
    }

    suspend fun addEvent(
        eventName: String,
        locationName: String,
        startDate: String,
        endDate: String,
        description: String,
        selectedImageUri: Uri?,
        context: Context,
        isFreeEntry: Boolean,
        ticketName: String,
        ticketPrice: Double?,
        ticketDescription: String,
        selectedCategory: Category?
    ) {
        addedEvent = null
        isLoading = true
        submitError = ""
        var imageUrl: String? = null
        if (eventName.isEmpty() || locationName.isEmpty() || startDate.isEmpty() || endDate.isEmpty() || description.isEmpty()) {
            submitError = "Please complete all the required field!"
            isLoading = false
            addedEvent = false
            return
        }
        if (selectedImageUri != null) {
            try {
                imageUrl = uploadImage(uri = selectedImageUri, context = context)
            } catch (e: Exception) {
                submitError = "Error uploading image"
                isLoading = false
                addedEvent = false
                val error = e.localizedMessage ?: "Unknown error"
                Log.e("error", error)
                return
            }
        }
        viewModelScope.launch {
            val location = locations.value?.find { l -> l.name == locationName }
            combine(organizer, userToken) { organizerValue, userTokenValue ->
                Pair(organizerValue, userTokenValue)
            }.collect { (organizerValue, userTokenValue) ->
                try {
                    if (organizerValue != null && userTokenValue != null) {
                        if (location?.id == 0) {
                            location.id = eventsRepository.addLocation(userTokenValue,location.name, location.street, location.city).id
                        }
                        val eventResponse = eventsRepository.addEvent(
                            userTokenValue,
                            eventName,
                            organizerValue.id,
                            location!!.id,
                            startDate,
                            endDate,
                            description,
                            imageUrl,
                            selectedCategory
                        )
                        Log.d("bbbb", eventResponse.toString())
                        if (isFreeEntry) {
                            val response = eventsRepository.addEventTicket(
                                token = userTokenValue,
                                name = "Free Entry ticket",
                                eventId = eventResponse.id,
                                description = "This event is entry free",
                                price = 0.0
                            )
                            Log.d("aaaa",response.toString())
                        } else {
                            val response = eventsRepository.addEventTicket(
                                userTokenValue,
                                ticketName,
                                eventResponse.id,
                                ticketDescription,
                                ticketPrice!!
                            )
                            Log.d("aaaa",response.toString())
                        }
                        addedEvent = true
                    }
                } catch (e: Exception) {
                    addedEvent = false
                    submitError = "Error uploading image"
                    val error = e.localizedMessage ?: "Unknown error"
                    Log.e("error", error)
                } finally {
                    isLoading = false
                }
            }
        }

    }
}