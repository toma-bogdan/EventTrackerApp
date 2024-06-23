package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.network.AuthRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import com.example.eventtrackerkotlincompose.network.Role
import kotlinx.coroutines.launch

class SignUpStateViewModel(application: Application): AndroidViewModel(application) {
    var isRegistered by mutableStateOf(false)
    var registerError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    private val apiService = HttpService(NetworkClient.client)
    private val authRepository = AuthRepository(apiService)
    private val repository = UserDetailsStore(application)

    suspend fun register (
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        confirmPassword: String,
        isOrganizer: Boolean,
        organizerName: String,
        description: String
    ) {
        isLoading = true
        registerError = null
        if (firstName.isEmpty() || lastName.isEmpty() || email.isEmpty() || password.isEmpty() || confirmPassword.isEmpty()){
            registerError = "Please complete all the fields!"
            isLoading = false
            return
        }

        if (password != confirmPassword) {
            isLoading = false
            registerError = "Passwords does not match!"
            return
        }
        Log.d("aaaa",isOrganizer.toString())
        if (isOrganizer){
            registerOrganizer(firstName, lastName, email, password, organizerName, description)
        } else {
            registerUser(firstName, lastName, email, password)
        }
    }
    private suspend fun registerUser(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
    ) {
        viewModelScope.launch {
            try {
                Log.d("register","Trying to register with $email, $firstName, $lastName and $password")
                val response = authRepository.register(firstName, lastName, email, password, Role.USER)
                repository.saveToken(response.token)
                Log.d("baaa",response.toString())
                val userDetails = authRepository.getUserDetails(email, response.token)
                repository.saveUser(userDetails)
                Log.d("userDetails",userDetails.toString())
                repository.saveUserRegs(emptyList())
                isRegistered = true
            } catch (e: Exception) {
                registerError = e.localizedMessage ?: "Unknown error"
                Log.d("error", registerError!!)
                isLoading = false
            } finally {
                isLoading = false
            }
        }
    }
    private suspend fun registerOrganizer(
        firstName: String,
        lastName: String,
        email: String,
        password: String,
        organizerName: String,
        organizerDescription: String
    ) {
        if (organizerName.isEmpty()) {
            registerError = "Please complete all the fields!"
            isLoading = false
            return
        }
        viewModelScope.launch {
            try {
                Log.d("register","Trying to register with $email, $firstName, $lastName, $password, $organizerName, $organizerDescription")
                val response = authRepository.registerOrganizer(firstName, lastName, email, password, Role.ORGANIZER, organizerName, organizerDescription)
                repository.saveToken(response.token)
                Log.d("baaa",response.toString())
                val userDetails = authRepository.getUserDetails(email, response.token)
                repository.saveUser(userDetails)
                Log.d("userDetails",userDetails.toString())
                val organizer = authRepository.getOrganizer(token = response.token, userDetails.organization_id!!)
                repository.saveOrganizer(organizer)
                Log.d("organizer",organizer.toString())
                isRegistered = true
            } catch (e: Exception) {
                registerError = e.localizedMessage ?: "Unknown error"
                Log.d("error", registerError!!)
                isLoading = false
            } finally {
                isLoading = false
            }
        }
    }
}