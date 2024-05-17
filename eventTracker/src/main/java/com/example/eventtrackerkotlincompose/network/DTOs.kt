package com.example.eventtrackerkotlincompose.network

import kotlinx.serialization.*

@Serializable
data class AuthRequest(val email: String, val password: String)

@Serializable
data class AuthResponse(val token: String)