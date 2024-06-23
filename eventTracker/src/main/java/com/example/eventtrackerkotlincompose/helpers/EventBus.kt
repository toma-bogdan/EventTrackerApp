package com.example.eventtrackerkotlincompose.helpers

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow

object EventBus {
    private val _events = MutableSharedFlow<Event>(extraBufferCapacity = 1)
    val events: SharedFlow<Event> get() = _events

    suspend fun publish(event: Event) {
        _events.emit(event)
    }

    sealed class Event {
        data class EventDeleted(val eventId: Int) : Event()
    }
}

