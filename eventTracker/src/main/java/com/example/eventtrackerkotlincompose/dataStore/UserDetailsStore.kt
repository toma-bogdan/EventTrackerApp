package com.example.eventtrackerkotlincompose.dataStore

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.preferencesOf
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.example.eventtrackerkotlincompose.network.Organizer
import com.example.eventtrackerkotlincompose.network.User
import com.example.eventtrackerkotlincompose.network.UserRegistration
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json

class UserDetailsStore(private val context: Context) {
    companion object {
        private val Context.dataStore: DataStore<Preferences> by preferencesDataStore("UserDetails")
        val USER_TOKEN_KEY = stringPreferencesKey("user_token")
        private val USER_KEY = stringPreferencesKey("user")
        private val USER_REGS_KEY = stringPreferencesKey("user_registration")
        private val ORGANIZER_KEY = stringPreferencesKey("organizer")
    }

    val getToken: Flow<String?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_TOKEN_KEY] ?: ""
        }

    suspend fun saveToken(token: String) {
        context.dataStore.edit { preferences ->
            preferences[USER_TOKEN_KEY] = token
        }
    }

    val getOrganizer: Flow<Organizer?> = context.dataStore.data
        .map { preferences->
            preferences[ORGANIZER_KEY]?.let { Json.decodeFromString(it) }
        }

    suspend fun saveOrganizer(organizer: Organizer) {
        val organizerJson = Json.encodeToString(organizer)
        context.dataStore.edit { preferences ->
            preferences[ORGANIZER_KEY] = organizerJson
        }
    }

    val getUserRegistrations: Flow<List<UserRegistration>?> = context.dataStore.data
        .map {preferences ->
            preferences[USER_REGS_KEY]?.let { Json.decodeFromString(it) }
        }

    suspend fun saveUserRegs(userRegistrations: List<UserRegistration>) {
        val userRegsJson = Json.encodeToString(userRegistrations)
        context.dataStore.edit { preferences ->
            preferences[USER_REGS_KEY] = userRegsJson
        }
    }

    val getUser: Flow<User?> = context.dataStore.data
        .map { preferences ->
            preferences[USER_KEY]?.let { Json.decodeFromString(it) }
        }

    suspend fun saveUser(user: User) {
        val userJson = Json.encodeToString(user)
        context.dataStore.edit { preferences ->
            preferences[USER_KEY] = userJson
        }
    }

    suspend fun clearUserDetails() {
        context.dataStore.edit {preferences ->
            preferences.clear()
        }
    }
}
