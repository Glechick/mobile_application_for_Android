package com.example.android_application.data.event

sealed interface ScanEvent {
    data class CodeDetected(
        val stageId: Int,
        val rawCode: String,
        val startedAt: Long
    ) : ScanEvent
}