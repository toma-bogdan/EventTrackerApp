package com.example.eventtrackerkotlincompose.network

import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.http.*

class HttpService(private val client: HttpClient) {
    suspend fun login(authRequest: AuthRequest): AuthResponse {
        val response = client.post("http://172.26.64.1:8080/auth/authenticate") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }
        return response.body()
    }
}