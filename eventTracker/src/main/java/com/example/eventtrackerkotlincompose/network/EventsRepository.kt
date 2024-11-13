package com.example.eventtrackerkotlincompose.network

import android.util.Log
import io.ktor.client.call.body
import io.ktor.http.HttpStatusCode
import java.time.LocalDate


class EventsRepository ( private val httpService: HttpService) {
    suspend fun getAllEvents(token: String): List<Event> {
        return httpService.getAllEvents(token)
    }
    suspend fun getEvent(token: String, eventId: Int): Event {
        return httpService.getEvent(token, eventId)
    }
    suspend fun getEventInfo(token: String, eventId: Int): List<EventTicket> {
        return httpService.getEventInfo(token, eventId)
    }
    suspend fun getLocations(token: String): List<Location> {
        return httpService.getLocations(token)
    }
    suspend fun addEvent(
        token: String,
        name: String,
        organizerId: Int,
        locationId: Int,
        startDate: String,
        endDate: String,
        description: String,
        imageUrl: String?,
        selectedCategory: Category?
    ): Event {
        val createEvent = CreateEvent(name, organizerId, locationId, LocalDate.parse(startDate), LocalDate.parse(endDate), description, imageUrl, selectedCategory)
        Log.d("createEvent",createEvent.toString())
        val response = httpService.addEvent(token, createEvent)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error creating the event")
        } else {
            return response.body()
        }
    }

    suspend fun editEvent(token: String, event: Event): Event {
        val createEvent = CreateEvent(
            event.name,
            event.organizer.id,
            event.location.id,
            event.startDate,
            event.endDate,
            event.description,
            event.imageUrl,
            event.category
        )
        val response = httpService.editEvent(token, event.id, createEvent)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error creating the event")
        } else {
            return response.body()
        }
    }

    suspend fun addEventTicket(
        token: String,
        name: String,
        eventId: Int,
        description: String?,
        price: Double
    ): EventTicket {
        val createEvenTicket = CreateEvenTicket(name, eventId, description, price)
        val response = httpService.addEventTicket(token, createEvenTicket)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error creating the event ticket")
        } else {
            return response.body()
        }
    }

    suspend fun deleteEventTicket(token: String, eventInfoId: Int) {
        val response = httpService.deleteEventTicket(token, eventInfoId)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error deleting the event ticket")
        } else {
            return response.body()
        }
    }

    suspend fun deleteEvent(token: String, eventId: Int) {
        val response = httpService.deleteEvent(token, eventId)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error deleting the event")
        } else {
            return response.body()
        }
    }

    suspend fun addLocation(token: String, name: String, street: String, city: String): Location {
        val createLocation = CreateLocation(name, street, city)
        val response = httpService.addLocation(token, createLocation)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error creating the new location")
        } else {
            return response.body()
        }
    }
    suspend fun addRegistration(token: String, userId: Int, eventInfoId: Int): UserRegistration {
        val createRegistration = CreateRegistration(userId,eventInfoId)
        val response = httpService.addRegistration(token, createRegistration)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error creating the new registration for event ticket $eventInfoId!")
        } else {
            return response.body()
        }
    }
    suspend fun deleteRegistration(token: String, registrationId: Int) {
        val response = httpService.deleteRegistration(token, registrationId)
        Log.d("response",response.body())
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error unregistering from event!")
        } else {
            return response.body()
        }
    }
    suspend fun getOrganizerEvents(token: String, organizerId: Int): List<Event>? {
        val response = httpService.getOrganizerEvents(token, organizerId)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error getting events for organizer!")
        } else {
            return response.body()
        }
    }

    suspend fun getEventRating(token: String, eventId: Int): List<Rating> {
        val response = httpService.getEventRatings(token, eventId)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error getting ratings for event!")
        } else {
            return response.body()
        }
    }

    suspend fun addEventRating(token: String, rating: Int, userId: Int, eventId: Int): Rating {
        val createRating = CreateRating(rating,userId,eventId)
        val response = httpService.addEventRating(token, createRating)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error posting rating for event!")
        } else {
            return response.body()
        }
    }

    suspend fun updateOrganizer(token: String, description: String, organizerName: String, organizerId: Int): Organizer {
        val updateOrganizer = UpdateOrganizer(organizerName,description)
        val response = httpService.updateOrganizer(token, updateOrganizer, organizerId)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error updating organizer!")
        } else {
            return response.body()
        }
    }

    suspend fun changeProfileImage(token: String, email: String, profile_image: String): User {
        val response = httpService.changeProfileImage(token, email, profile_image)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error updating profile image!")
        } else {
            return response.body()
        }
    }

    suspend fun getUserComments(token: String, eventId: Int): List<UserComment> {
        val response = httpService.getEventComments(token, eventId)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error getting user comments!")
        } else {
            return response.body()
        }
    }

    suspend fun postUserComment(token: String, comment: String, userId: Int, eventId: Int): UserComment {
        val postUserComment = PostUserComment(comment, userId, eventId)
        val response = httpService.postEventComment(token, postUserComment)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error posting comment!")
        } else {
            return response.body()
        }
    }

    suspend fun scanTicket(token: String, ticketCode: String): Boolean {
        val response = httpService.scanTicket(token, ticketCode)
        if (response.status != HttpStatusCode.OK) {
            throw Exception("Error scanning ticket!")
        } else {
            return response.body()
        }
    }
}