package com.example.eventtrackerkotlincompose.network

import kotlinx.serialization.*
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import java.time.LocalDate
import java.time.format.DateTimeFormatter

@Serializable
data class AuthRequest(val email: String, val password: String)

@Serializable
data class RegisterRequest(val firstName: String, val lastName: String,val email: String, val password: String, val role: Role)

@Serializable
data class RegisterOrganizerRequest(
    val firstName: String,
    val lastName: String,
    val email: String,
    val password: String,
    val role: Role,
    val organizerName: String,
    val description: String
)

@Serializable
data class AuthResponse(val token: String)

@Serializable
data class User(
    val id: Int,
    val firstName: String,
    val lastName: String,
    val email: String,
    val role: Role,
    val organization_id: Int? = null,
    var profile_image: String? = null
)
@OptIn(ExperimentalSerializationApi::class)
@Serializer(forClass = LocalDate::class)
class LocalDateSerializer : KSerializer<LocalDate> {
    private val formatter: DateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override fun serialize(encoder: Encoder, value: LocalDate) {
        encoder.encodeString(value.format(formatter))
    }

    override fun deserialize(decoder: Decoder): LocalDate {
        return LocalDate.parse(decoder.decodeString(), formatter)
    }
}
@Serializable
data class Event(
    val id: Int,
    val name: String,
    val organizer: Organizer,
    val location: Location,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,
    val description: String? = null,
    val imageUrl: String? = null,
    val category: Category? = null
)

@Serializable
data class EventTicket(
    val id: Int,
    val eventId: Int,
    val name: String,
    val description: String,
    val price: Double
)

@Serializable
data class Organizer(
    val id: Int,
    val name: String,
    val description: String,
)

@Serializable
data class Location(
    var id: Int,
    val name: String,
    val street: String,
    val city: String
)

@Serializable
data class UserRegistration(
    val id: Int,
    val ticketCode: String,
    val eventInfo: EventTicket,
)

@Serializable
enum class Role {
    ORGANIZER,
    ADMIN,
    USER
}

@Serializable
enum class Category {
    Party_And_Concerts,
    Theatre_and_Opera,
    Stand_Up,
    Exhibitions,
    Sports_and_Outdoor_Activities
}
@Serializable
data class CreateEvent(
    val name: String,
    val organizerId: Int,
    val locationId: Int,
    @Serializable(with = LocalDateSerializer::class)
    val startDate: LocalDate,
    @Serializable(with = LocalDateSerializer::class)
    val endDate: LocalDate,
    val description: String?,
    val imageUrl: String?,
    val category: Category?
)

@Serializable
data class CreateEvenTicket(
    val name: String,
    val eventId: Int,
    val description: String?,
    val price: Double
)

@Serializable
data class CreateLocation(
    val name: String,
    val street: String,
    val city: String
)

@Serializable
data class CreateRegistration(
    val userId: Int,
    val eventInfoId: Int
)

@Serializable
data class CreateRating(
    val rating: Int,
    val userId: Int,
    val eventId: Int
)

@Serializable
data class Rating(
    val id: Int,
    val rating: Int,
    val userId: Int,
    val eventId: Int
)

@Serializable
data class UpdateOrganizer(
    val organizerName: String,
    val description: String
)

@Serializable
data class ChangePassword(
    val email: String,
    val oldPassword: String,
    val newPassword: String
)

@Serializable
data class UserComment(
    val id: Int,
    val eventId: Int,
    val user: User,
    val comment: String
)

@Serializable
data class PostUserComment(
    val comment: String,
    val userId: Int,
    val eventId: Int
)
