package com.jobguardai.jobguardai

import android.app.Application
import com.jobguardai.jobguardai.data.local.AppDatabase

class JobGuardApplication : Application() {
    val database: AppDatabase by lazy { AppDatabase.getDatabase(this) }

    companion object {
        lateinit var instance: JobGuardApplication
            private set
    }

    override fun onCreate() {
        super.onCreate()
        instance = this
    }
}
