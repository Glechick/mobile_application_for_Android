package com.example.android_application.data.local.room

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "player_profile")
data class PlayerProfile(
    @PrimaryKey val id: Int = 1,
    val playerName: String = "Игрок",
    val totalScore: Int = 0,
    val createdAt: Long = System.currentTimeMillis()
)