package com.example.android_application.data.local.realm

import java.security.MessageDigest

object HashUtils {
    fun sha256(input: String): String {
        val bytes = MessageDigest.getInstance("SHA-256").digest(input.toByteArray())
        return bytes.joinToString("") { "%02x".format(it) }
    }
}