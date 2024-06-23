package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import android.util.Log
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.network.AuthRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import com.example.eventtrackerkotlincompose.network.Role
import com.example.eventtrackerkotlincompose.network.UserRegistration
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class LoginStateViewModel(application: Application): AndroidViewModel(application) {
    var isLoggedIn by mutableStateOf(false)
    var loginError by mutableStateOf<String?>(null)
    var isLoading by mutableStateOf(false)
    private val apiService = HttpService(NetworkClient.client)
    private val authRepository = AuthRepository(apiService)
    private val repository = UserDetailsStore(application)

    suspend fun login(email: String, password: String) {
        isLoading = true
        loginError = null
        viewModelScope.launch {
            try {
                Log.d("login","Trying to log in with $email and  $password")
                val response = authRepository.login(email, password)
                repository.saveToken(response.token)
                Log.d("baaa",response.toString())
                val userDetails = authRepository.getUserDetails(email, response.token)
                repository.saveUser(userDetails)
                Log.d("userDetails",userDetails.toString())
                if (userDetails.role == Role.USER) {
                    val userRegistrations =
                        authRepository.getUserRegistration(response.token, userDetails.id)
                    repository.saveUserRegs(userRegistrations)
                    Log.d("userRegistrations", userRegistrations.toString())
                } else {
                    val organizer = authRepository.getOrganizer(token = response.token, userDetails.organization_id!!)
                    repository.saveOrganizer(organizer)
                    Log.d("organizer",organizer.toString())
                }

                isLoggedIn = true
            } catch (e: Exception) {
                loginError = e.localizedMessage ?: "Unknown error"
                Log.d("error", loginError!!)
                isLoading = false
            } finally {
                isLoading = false
            }
        }
    }
}
