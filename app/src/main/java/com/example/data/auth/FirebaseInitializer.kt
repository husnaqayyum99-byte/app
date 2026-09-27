package com.example.data.auth

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.google.firebase.FirebaseApp
import com.google.firebase.FirebaseOptions

/**
 * Ensures FirebaseApp is reliably initialized on application launch,
 * utilizing google-services configuration if available or providing fallback options
 * for development environments.
 */
object FirebaseInitializer {

    private const val TAG = "ApnaWakeel_Firebase"

    fun init(context: Context): Boolean {
        return try {
            if (FirebaseApp.getApps(context).isNotEmpty()) {
                return true
            }

            // Attempt default initialization from google-services.json generated resources
            try {
                val app = FirebaseApp.initializeApp(context.applicationContext)
                if (app != null) {
                    Log.d(TAG, "FirebaseApp initialized via google-services.")
                    return true
                }
            } catch (e: Exception) {
                Log.d(TAG, "Default Firebase initialization not present, using programmatic options: ${e.message}")
            }

            val apiKey = try {
                BuildConfig.GEMINI_API_KEY.ifBlank { "AIzaSyDevPlaceholderKey000000000000" }
            } catch (e: Exception) {
                "AIzaSyDevPlaceholderKey000000000000"
            }

            val options = FirebaseOptions.Builder()
                .setApplicationId("1:298973801485:android:apnawakeelkpxch")
                .setApiKey(apiKey)
                .setProjectId("apna-wakeel-kp")
                .setStorageBucket("apna-wakeel-kp.appspot.com")
                .build()

            FirebaseApp.initializeApp(context.applicationContext, options)
            Log.d(TAG, "FirebaseApp initialized with options successfully.")
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to initialize FirebaseApp: ${e.message}", e)
            false
        }
    }
}
