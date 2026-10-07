package com.example.android_application

import android.app.Application
import com.example.android_application.data.local.realm.StageSeeder
import dagger.hilt.android.HiltAndroidApp
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltAndroidApp
class QuestApplication : Application() {

    @Inject
    lateinit var stageSeeder: StageSeeder

    private val appScope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

    override fun onCreate() {
        super.onCreate()
        appScope.launch {
            stageSeeder.seedIfEmpty()
        }
    }
}