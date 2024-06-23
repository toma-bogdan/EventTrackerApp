package com.example.eventtrackerkotlincompose.network

import android.content.Context
import android.net.Uri
import android.os.Build
import android.util.Log
import androidx.annotation.RequiresApi
import com.example.eventtrackerkotlincompose.config.AppConfig
import io.ktor.client.*
import io.ktor.client.call.body
import io.ktor.client.request.*
import io.ktor.client.request.forms.formData
import io.ktor.client.request.forms.submitFormWithBinaryData
import io.ktor.client.statement.HttpResponse
import io.ktor.client.statement.bodyAsText
import io.ktor.client.statement.readText
import io.ktor.http.*
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.InputStream

class HttpService(private val client: HttpClient) {
    suspend fun login(authRequest: AuthRequest): AuthResponse {
        val response = client.post("${AppConfig.SERVER_URL}/auth/authenticate") {
            contentType(ContentType.Application.Json)
            setBody(authRequest)
        }
        return response.body()
    }

    suspend fun register(registerRequest: RegisterRequest): AuthResponse {
        val response = client.post("${AppConfig.SERVER_URL}/auth/register") {
            contentType(ContentType.Application.Json)
            setBody(registerRequest)
        }
        if (response.status == HttpStatusCode.BadRequest) {
            throw Exception("Email already in use")
        }
        return response.body()
    }
    suspend fun registerOrganizer(registerRequest: RegisterOrganizerRequest): AuthResponse {
        val response = client.post("${AppConfig.SERVER_URL}/auth/organizer") {
            contentType(ContentType.Application.Json)
            setBody(registerRequest)
        }
        if (response.status == HttpStatusCode.BadRequest) {
            throw Exception("Email already in use")
        }
        return response.body()
    }
    suspend fun getUserDetails(email: String, token: String): User {
        val response = client.get("${AppConfig.SERVER_URL}/users/email") {
            parameter("email", email)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return response.body();
    }
    suspend fun getAllEvents(token: String): List<Event> {
        val response = client.get("${AppConfig.SERVER_URL}/allEvents") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        Log.d("response",response.body())
        return response.body()
    }
    suspend fun getEvent(token: String, eventId: Int): Event {
        val response = client.get("${AppConfig.SERVER_URL}/events/$eventId") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return response.body()
    }
    suspend fun getEventInfo(token: String, eventId: Int): List<EventTicket> {
        val response = client.get("${AppConfig.SERVER_URL}/eventInfo") {
            parameter("eventId", eventId)
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return response.body()
    }
    suspend fun getUserRegistrations(token: String, userId: Int): List<UserRegistration> {
        val response = client.get("${AppConfig.SERVER_URL}/registration/user/$userId") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return when (response.status) {
            HttpStatusCode.NoContent -> emptyList()
            HttpStatusCode.OK -> response.body()
            else -> throw Exception("Failed to fetch user registrations: ${response.status}")
        }
    }
    suspend fun getOrganizer(token: String, id: Int): Organizer {
        val response = client.get("${AppConfig.SERVER_URL}/organizer") {
            parameter("id", id)
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return response.body()
    }
    suspend fun getLocations(token: String): List<Location> {
        val response = client.get("${AppConfig.SERVER_URL}/locations") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return response.body()
    }
    suspend fun uploadImage(uri: Uri, context: Context): String {
        val contentResolver = context.contentResolver
        val inputStream: InputStream? = contentResolver.openInputStream(uri)
        val response = client.submitFormWithBinaryData(
            url = "${AppConfig.SERVER_URL}/api/images/upload",
            formData = formData {
                append("file", inputStream!!.readBytes(), Headers.build {
                    append(HttpHeaders.ContentType, "image/jpeg") // Change this based on your image type
                    append(HttpHeaders.ContentDisposition, "filename=\"${uri.lastPathSegment}\"")
                })
            }
        )
        return response.bodyAsText()
    }
    suspend fun addEvent(token: String, createEvent: CreateEvent): HttpResponse {
        return client.post("${AppConfig.SERVER_URL}/postEvent") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(createEvent)
        }
    }

    suspend fun editEvent(token: String, eventId: Int, createEvent: CreateEvent): HttpResponse {
        return client.put("${AppConfig.SERVER_URL}/editEvent/$eventId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(createEvent)
        }
    }
    suspend fun addEventTicket(token: String, createEventTicket: CreateEvenTicket): HttpResponse {
        return client.post("${AppConfig.SERVER_URL}/postEventInfo") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(createEventTicket)
        }
    }
    suspend fun deleteEventTicket(token: String, eventInfoId: Int): HttpResponse {
        return client.delete("${AppConfig.SERVER_URL}/deleteEventInfo/${eventInfoId}") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }
    }
    suspend fun deleteEvent(token: String, eventId: Int): HttpResponse {
        return client.delete("${AppConfig.SERVER_URL}/deleteEvent/${eventId}") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }
    }
    suspend fun addLocation(token: String, createLocation: CreateLocation): HttpResponse {
        return client.post("${AppConfig.SERVER_URL}/postLocation") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(createLocation)
        }
    }
    suspend fun addRegistration(token: String, createRegistration: CreateRegistration): HttpResponse {
        return client.post("${AppConfig.SERVER_URL}/registration") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
            setBody(createRegistration)
        }
    }
    suspend fun deleteRegistration(token: String, registrationId: Int): HttpResponse {
        return client.delete("${AppConfig.SERVER_URL}/registration/$registrationId") {
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            contentType(ContentType.Application.Json)
        }
    }

    suspend fun getOrganizerEvents(token: String, organizerId: Int): HttpResponse {
        val response = client.get("${AppConfig.SERVER_URL}/events") {
            parameter("organizerId", organizerId)
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
        return response.body()
    }
    suspend fun getEventRatings(token: String, eventId: Int): HttpResponse {
        return client.get("${AppConfig.SERVER_URL}/ratings/event/$eventId"){
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }
    suspend fun addEventRating(token: String, createRating: CreateRating): HttpResponse {
        return client.post("${AppConfig.SERVER_URL}/ratings") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(createRating)
        }
    }
    suspend fun updateOrganizer(token: String, updateOrganizer: UpdateOrganizer, organizerId: Int): HttpResponse {
        return client.put("${AppConfig.SERVER_URL}/organizer/$organizerId") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(updateOrganizer)
        }
    }

    suspend fun changePassword(changePassword: ChangePassword): HttpResponse {
        return client.post("${AppConfig.SERVER_URL}/auth/change-password") {
            contentType(ContentType.Application.Json)
            setBody(changePassword)
        }
    }

    suspend fun changeProfileImage(token: String, email: String, profile_image: String): HttpResponse {
        return client.post("${AppConfig.SERVER_URL}/users/profileImage") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            parameter("email",email)
            parameter("profile_image", profile_image)
        }
    }

    suspend fun getEventComments(token: String, eventId: Int): HttpResponse {
        return client.get("${AppConfig.SERVER_URL}/comments/event/${eventId}"){
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
        }
    }

    suspend fun postEventComment(token: String, postUserComment: PostUserComment): HttpResponse {
        return client.post("${AppConfig.SERVER_URL}/comments") {
            contentType(ContentType.Application.Json)
            headers {
                append(HttpHeaders.Authorization, "Bearer $token")
            }
            setBody(postUserComment)
        }
    }
}