package com.example.eventtrackerkotlincompose.network

class AuthRepository(private val httpService: HttpService) {
    suspend fun login(email: String, password: String): AuthResponse {
        val authRequest = AuthRequest(email, password)
        return httpService.login(authRequest)
    }
}