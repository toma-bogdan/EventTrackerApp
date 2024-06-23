package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.helpers.EventBus
import com.example.eventtrackerkotlincompose.network.Event
import com.example.eventtrackerkotlincompose.network.EventsRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import com.example.eventtrackerkotlincompose.network.Role
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SavedEventsViewModel(application: Application): AndroidViewModel(application) {
    private val apiService = HttpService(NetworkClient.client)
    private val eventsRepository = EventsRepository(apiService)
    private val repository = UserDetailsStore(application)
    val userDetails = repository.getUser
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    private val userToken = repository.getToken
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    private val userRegistrations = repository.getUserRegistrations
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    private val organizer = repository.getOrganizer
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)
    private var _filteredEvents = mutableStateOf<List<Event>?>(null)
    var filteredEvents: MutableState<List<Event>?> = _filteredEvents

    init {
        viewModelScope.launch {
            combine(userDetails, userToken) { user, userTokenValue ->
                Pair(user, userTokenValue)
            }.collect{(user, userTokenValue) ->
                if (user != null && !userTokenValue.isNullOrEmpty()) {
                    if (user.role == Role.USER){
                        getUserRegistrations(userTokenValue)
                        Log.d("events filtered", _filteredEvents.value.toString())
                    } else {
                        getOrganizerEvents(userTokenValue)
                        Log.d("events filtered", _filteredEvents.value.toString())
                    }
                }
            }
        }
        viewModelScope.launch {
            EventBus.events.collect{ event ->
                Log.d("din event", event.toString())
                when (event) {
                    is EventBus.Event.EventDeleted -> {
                        getOrganizerEvents(userToken.value!!)
                    }
                }
            }
        }
    }
    private suspend fun getUserRegistrations(token: String) {
        val allEvents = eventsRepository.getAllEvents(token)

        userRegistrations.collect {
            if (it != null) {
                 _filteredEvents.value = allEvents.filter {event ->
                    it.any { registration ->
                        registration.eventInfo.eventId == event.id
                    }
                }
                Log.d("filtered",_filteredEvents.value.toString())
            }
        }
    }

    private suspend fun getOrganizerEvents(token: String) {
        organizer.collect{
            if (it != null) {
                _filteredEvents.value = eventsRepository.getOrganizerEvents(token, it.id);
            }
        }
    }

    fun getTicketCode(eventId: Int): String {
        val registration = userRegistrations.value!!
            .find { userRegistration -> userRegistration.eventInfo.eventId == eventId }
        return registration!!.ticketCode
    }
}