package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import android.content.Context
import android.net.Uri
import android.util.Log
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.network.AuthRepository
import com.example.eventtrackerkotlincompose.network.ChangePassword
import com.example.eventtrackerkotlincompose.network.EventsRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ProfileViewModel(application: Application): AndroidViewModel(application) {
    private val apiService = HttpService(NetworkClient.client)
    private val eventsRepository = EventsRepository(apiService)
    private val httpService = AuthRepository(apiService)
    private val repository = UserDetailsStore(application)

    private val userToken = repository.getToken
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")

    val organizer = repository.getOrganizer
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val userDetails = repository.getUser
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    var updatedOrganizer by mutableStateOf(false)
    var changePasswordError by mutableStateOf<String?>(null)

    fun logout() {
        viewModelScope.launch {
            repository.clearUserDetails()
        }
    }

    suspend fun changeProfileImage(uri: Uri, context: Context) {
        userToken.collect {token ->
            if (!token.isNullOrEmpty()) {
                val response = apiService.uploadImage(uri,context)
                val user = eventsRepository.changeProfileImage(userToken.value!!, userDetails.value!!.email, response)
                Log.d("imagineee",user.toString())
                repository.saveUser(user)
            }
        }
    }

    suspend fun changePassword(email: String, oldPassword: String, newPassword: String, confirmPassword: String): Boolean {
        changePasswordError = null
        if (newPassword != confirmPassword) {
            changePasswordError = "New passwords does not match!"
            return false
        }
        try {
            httpService.changePassword(email,oldPassword,newPassword)
            return true
        } catch (e: Exception) {
            e.localizedMessage?.let { Log.d("eroare", it) }
            changePasswordError = e.localizedMessage
            return false
        }
    }

    suspend fun updateOrganizer(organizationName: String, organizationDescription: String) {
        updatedOrganizer = false
        userToken.collect {token ->
            if (!token.isNullOrEmpty()) {
                val updatedOrganizer =
                    eventsRepository.updateOrganizer(token, organizationDescription, organizationName, organizer.value!!.id)
                repository.saveOrganizer(updatedOrganizer)
                this.updatedOrganizer = true
            }
        }
    }
}
