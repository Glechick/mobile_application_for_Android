package com.example.android_application.data.event

import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ScanEventBus @Inject constructor() {

    private val _events = MutableSharedFlow<ScanEvent>(
        extraBufferCapacity = 16
    )
    val events: SharedFlow<ScanEvent> = _events.asSharedFlow()

    suspend fun publish(event: ScanEvent) {
        _events.emit(event)
    }
}