package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.network.AuthRepository
import com.example.eventtrackerkotlincompose.network.Event
import com.example.eventtrackerkotlincompose.network.EventsRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeScreenViewModel(application: Application): AndroidViewModel(application) {
    private val apiService = HttpService(NetworkClient.client)
    private val eventsRepository = EventsRepository(apiService)
    private val repository = UserDetailsStore(application)
    private val userToken = repository.getToken
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    var events by mutableStateOf<List<Event>?>(null)
    init {
        viewModelScope.launch {
            userToken.collect{
                if (!it.isNullOrEmpty()) {
                    val response = eventsRepository.getAllEvents(userToken.value!!)
                    events = response
                    Log.d("events", response.toString())
                }
            }
        }
    }
}