package com.example.android_application.util

import android.content.Context
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.io.File

object ModelInitializer {

    private const val MODEL_NAME = "qwen2.5-1.5b-instruct-q4_k_m.gguf"

    suspend fun ensureModel(context: Context): File = withContext(Dispatchers.IO) {
        val target = File(context.filesDir, MODEL_NAME)
        if (target.exists() && target.length() > 0) {
            return@withContext target
        }

        context.assets.open(MODEL_NAME).use { input ->
            target.outputStream().use { output ->
                input.copyTo(output)
            }
        }
        target
    }
}