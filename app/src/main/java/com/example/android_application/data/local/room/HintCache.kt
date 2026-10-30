package com.example.android_application.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "hint_cache")
data class HintCache(
    @PrimaryKey val riddle: String,
    val hintsJson: String,
    val createdAt: Long
)