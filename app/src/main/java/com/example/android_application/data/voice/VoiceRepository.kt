package com.example.android_application.data.voice

import kotlinx.coroutines.flow.SharedFlow

interface VoiceRepository {
    val events: SharedFlow<VoiceEvent>
    fun startListening()
    fun stopListening()
    fun speak(text: String)
    fun stopSpeaking()
    fun destroy()
}