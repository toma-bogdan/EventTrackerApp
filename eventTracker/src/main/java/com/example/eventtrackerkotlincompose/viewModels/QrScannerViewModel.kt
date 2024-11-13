package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.network.EventsRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.launch

class QrScannerViewModel(application: Application) : AndroidViewModel(application) {
    private val apiService = HttpService(NetworkClient.client)
    private val eventsRepository = EventsRepository(apiService)
    private val userDetailsStore = UserDetailsStore(application)
    private val _scanResult = MutableStateFlow<String?>(null)
    val scanResult: StateFlow<String?> = _scanResult


    fun updateAttendance(ticketCode: String) {
        viewModelScope.launch {
            userDetailsStore.getToken.collect { token ->
                if (!token.isNullOrEmpty()) {
                    val response = eventsRepository.scanTicket(token, ticketCode)
                    if (response) {
                        _scanResult.value = "Ticket successfully scanned"
                    } else {
                        _scanResult.value = "Ticket was already scanned"
                    }
                }
            }
        }
    }

    fun resetScanResult() {
        _scanResult.value = null
    }
}