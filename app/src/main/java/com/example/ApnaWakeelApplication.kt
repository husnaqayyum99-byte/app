package com.example

import android.app.Application
import com.example.data.auth.FirebaseInitializer

class ApnaWakeelApplication : Application() {
    override fun onCreate() {
        super.onCreate()
        FirebaseInitializer.init(this)
    }
}
