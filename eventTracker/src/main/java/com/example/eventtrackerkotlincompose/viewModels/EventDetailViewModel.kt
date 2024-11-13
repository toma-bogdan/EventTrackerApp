package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableDoubleStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalHapticFeedback
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.helpers.EventBus
import com.example.eventtrackerkotlincompose.network.Event
import com.example.eventtrackerkotlincompose.network.EventTicket
import com.example.eventtrackerkotlincompose.network.EventsRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import com.example.eventtrackerkotlincompose.network.Rating
import com.example.eventtrackerkotlincompose.network.UserComment
import com.example.eventtrackerkotlincompose.network.UserRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.takeWhile
import kotlinx.coroutines.launch

class EventDetailViewModel(
    application: Application,
    savedStateHandle: SavedStateHandle
): AndroidViewModel(application) {
    private val apiService = HttpService(NetworkClient.client)
    private val eventsRepository = EventsRepository(apiService)
    private val repository = UserDetailsStore(application)

    private val userToken = repository.getToken
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val userRegistrations = repository.getUserRegistrations
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val userDetails = repository.getUser
        .distinctUntilChanged().stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val organizer = repository.getOrganizer
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    private val eventId: Int = checkNotNull(savedStateHandle["eventId"])
    private val _event = MutableStateFlow<Event?>(null)
    val event: StateFlow<Event?> = _event.asStateFlow()

    private val _eventTickets = MutableStateFlow<List<EventTicket>?>(null)
    var eventTickets: StateFlow<List<EventTicket>?> = _eventTickets.asStateFlow()

    var eventBelongsToOrganizer by mutableStateOf(false)
    var ratings by  mutableStateOf(emptyList<Rating>())
    var eventRating by mutableDoubleStateOf(0.0)
    var eventComments by mutableStateOf(emptyList<UserComment>())

    init {
        viewModelScope.launch {
            userToken.collect { token ->
                if (!token.isNullOrEmpty()) {
                    try {
                        _event.value = eventsRepository.getEvent(token, eventId)
                        Log.d("event",_event.value.toString())
                        _eventTickets.value = eventsRepository.getEventInfo(token, eventId)
                        eventBelongsToOrganizer(_event.value!!.id)
                        ratings = eventsRepository.getEventRating(token, eventId)
                        if (ratings.isNotEmpty()){
                            eventRating = ratings.map { it.rating }.average()
                        }
                        eventComments = eventsRepository.getUserComments(token, eventId)
                    } catch (e: Exception) {
                        val error = e.localizedMessage ?: "Unknown error"
                        Log.e("error", error)
                    }
                }
            }
        }
    }

    suspend fun addRating(rating: Int) {
        eventsRepository.addEventRating(userToken.value!!, rating, userDetails.value!!.id, eventId)
        ratings = eventsRepository.getEventRating(userToken.value!!, eventId)
        eventRating = ratings.map { it.rating }.average()
    }

    private suspend fun eventBelongsToOrganizer(eventId: Int) {
        if (organizer.value != null) {
            val organizerEvents =
                eventsRepository.getOrganizerEvents(userToken.value!!, organizer.value!!.id)
            if (organizerEvents != null) {
                eventBelongsToOrganizer = organizerEvents.any { event -> event.id == eventId }
            }
        }
    }

    suspend fun editEvent(event: Event) {
        _event.value = eventsRepository.editEvent(userToken.value!!, event)
    }

    suspend fun registerEvent(ticketId: Int) {
        viewModelScope.launch {
            var shouldCollect = true
            combine(userDetails, userRegistrations) { userDetails, userRegistrations ->
                Pair(userDetails, userRegistrations)
            }
                .takeWhile { shouldCollect }
                .collect { (userValue, registrationsValue) ->
                    if (userValue != null && registrationsValue != null) {
                        try {
                            val reg = eventsRepository.addRegistration(
                                userToken.value!!,
                                userValue.id,
                                ticketId
                            )
                            Log.d("reg", reg.toString())
                            val updatedRegistrations =
                                if (registrationsValue.any { r -> r.eventInfo.eventId == reg.eventInfo.eventId }) {
                                    registrationsValue.map { existingReg ->
                                        if (existingReg.eventInfo.eventId == reg.eventInfo.eventId) reg else existingReg
                                    }
                                } else {
                                    registrationsValue + reg
                                }
                            repository.saveUserRegs(updatedRegistrations)
                            shouldCollect = false
                        } catch (e: Exception) {
                            val error = e.localizedMessage ?: "Unknown error"
                            Log.e("error", error)
                        }
                    }
                }
        }
    }

    suspend fun unregisterEvent(ticketId: Int) {
        viewModelScope.launch {
            var shouldCollect = true
            combine(userDetails, userRegistrations) { userDetails, userRegistrations ->
                Pair(userDetails, userRegistrations)
            }
            .takeWhile { shouldCollect }
            .collect { (userValue, registrationsValue) ->
                if (userValue != null && registrationsValue != null) {
                    try {
                        val reg =
                            registrationsValue.find { r -> r.eventInfo.id == ticketId }
                        Log.d("reggg",reg.toString())
                        if (reg != null) {
                            eventsRepository.deleteRegistration(userToken.value!!, reg.id)
                            val updatedRegistrations =
                                registrationsValue.filter { it.id != reg.id }
                            repository.saveUserRegs(updatedRegistrations)
                            shouldCollect = false
                        }
                    } catch (e: Exception) {
                        val error = e.localizedMessage ?: "Unknown error"
                        Log.e("error", error)
                    }
                }
            }
        }
    }

    suspend fun addNewTicket(ticketName: String, ticketDescription: String, ticketPrice: Double) {
        val response = eventsRepository.addEventTicket(
            userToken.value!!,
            ticketName,
            _event.value!!.id,
            ticketDescription,
            ticketPrice
        )
        _eventTickets.value = _eventTickets.value?.plus(response)
        Log.i("Add new ticket",response.toString())
    }

    suspend fun deleteTicket(eventTicket: EventTicket) {
        eventsRepository.deleteEventTicket(userToken.value!!, eventTicket.id)
        _eventTickets.value = _eventTickets.value?.minus(eventTicket)
    }

    suspend fun deleteEvent() {
//        eventsRepository.deleteEvent(userToken.value!!, eventId)
        EventBus.publish(EventBus.Event.EventDeleted(_event.value!!.id))
    }
    suspend fun addComment(comment: String) {
        val response = eventsRepository.postUserComment(userToken.value!!, comment, userDetails.value!!.id, eventId)
        eventComments = eventComments + response
        Log.d("post comment", response.toString())
    }
}