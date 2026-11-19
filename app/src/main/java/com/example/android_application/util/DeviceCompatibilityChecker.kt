package com.example.android_application.util

import android.app.ActivityManager
import android.content.Context
import android.os.Build
import dagger.hilt.android.qualifiers.ApplicationContext
import java.io.File
import javax.inject.Inject
import javax.inject.Singleton

data class CompatibilityResult(val isCompatible: Boolean, val reason: String? = null)

@Singleton
class DeviceCompatibilityChecker @Inject constructor(
    @ApplicationContext private val context: Context
) {
    fun check(): CompatibilityResult {
        if (Build.SUPPORTED_64_BIT_ABIS.isEmpty()) {
            return CompatibilityResult(false, "Устройство не поддерживает 64-битные ABI")
        }

        val am = context.getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager
        val memInfo = ActivityManager.MemoryInfo()
        am.getMemoryInfo(memInfo)
        val freeGb = memInfo.availMem / 1_073_741_824.0
        if (freeGb < 0.8) {
            return CompatibilityResult(false, "Недостаточно памяти: ${"%.1f".format(freeGb)} ГБ свободно, нужно ≥ 0.8 ГБ")
        }

        val model = File(context.filesDir, "qwen2.5-1.5b-instruct-q4_k_m.gguf")
        if (!model.exists()) {
            return CompatibilityResult(false, "Модель не найдена. Скопируйте GGUF-файл в filesDir")
        }

        return CompatibilityResult(true)
    }
}