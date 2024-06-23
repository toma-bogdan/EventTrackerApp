package com.example.eventtrackerkotlincompose.network

import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode

class AuthRepository(private val httpService: HttpService) {
    suspend fun login(email: String, password: String): AuthResponse {
        val authRequest = AuthRequest(email, password)
        return httpService.login(authRequest)
    }

    suspend fun register(firstName: String, lastName: String,email: String, password: String, role: Role): AuthResponse {
        val registerRequest = RegisterRequest(firstName, lastName, email, password, role)
        return httpService.register(registerRequest)
    }

    suspend fun changePassword(email: String, oldPassword: String, newPassword: String): String {
        val changePassword = ChangePassword(email, oldPassword, newPassword)
        val response = httpService.changePassword(changePassword)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Old password is incorrect")
        } else {
            return response.body()
        }
    }
    suspend fun registerOrganizer(firstName: String, lastName: String, email: String, password: String, role: Role, organizerName: String, description: String): AuthResponse {
        val registerRequest = RegisterOrganizerRequest(firstName, lastName, email, password, role, organizerName, description)
        return httpService.registerOrganizer(registerRequest)
    }

    suspend fun getUserDetails(email: String, token: String): User {
        return httpService.getUserDetails(email, token)
    }
    suspend fun getUserRegistration(token: String, userId: Int): List<UserRegistration> {
        return httpService.getUserRegistrations(token, userId)
    }
    suspend fun getOrganizer(token: String, id: Int): Organizer {
        return httpService.getOrganizer(token, id)
    }
}
