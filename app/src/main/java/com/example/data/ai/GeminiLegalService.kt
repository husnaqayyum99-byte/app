package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import com.example.data.models.CaseFacts
import com.example.data.models.LegalCategory
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.RequestBody.Companion.toRequestBody
import okhttp3.ResponseBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.Body
import retrofit2.http.POST
import retrofit2.http.Query
import java.util.concurrent.TimeUnit

interface GeminiRestApi {
    @POST("v1beta/models/gemini-2.5-flash:generateContent")
    suspend fun generateContent(
        @Query("key") apiKey: String,
        @Body requestBody: okhttp3.RequestBody
    ): ResponseBody
}

object GeminiLegalService {

    private const val TAG = "ApnaWakeel_Gemini"

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(30, TimeUnit.SECONDS)
        .readTimeout(30, TimeUnit.SECONDS)
        .writeTimeout(30, TimeUnit.SECONDS)
        .build()

    private val retrofit = Retrofit.Builder()
        .baseUrl("https://generativelanguage.googleapis.com/")
        .client(okHttpClient)
        .addConverterFactory(
            MoshiConverterFactory.create(
                Moshi.Builder().add(KotlinJsonAdapterFactory()).build()
            )
        )
        .build()

    private val api: GeminiRestApi = retrofit.create(GeminiRestApi::class.java)

    fun isConfigured(): Boolean {
        val key = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }
        return !key.isNullOrBlank() && key != "MY_GEMINI_API_KEY"
    }

    suspend fun enhanceGuidance(
        userProblem: String,
        category: LegalCategory,
        facts: CaseFacts,
        location: String
    ): String? = withContext(Dispatchers.IO) {
        if (!isConfigured()) {
            Log.d(TAG, "Gemini API key is not configured; using local verified legal knowledge base.")
            return@withContext null
        }

        try {
            val key = BuildConfig.GEMINI_API_KEY
            val prompt = """
                You are a senior legal assistant for 'Apna Wakeel', specialized in Chitral and Khyber Pakhtunkhwa (KP), Pakistan.
                User Problem: $userProblem
                Category: ${category.name}
                Location: $location, KP
                
                Provide 2-3 concise, practical tips for the user regarding:
                - What to expect when visiting the Chitral authority
                - Practical advice on maintaining composure and keeping copies of all submissions
                Do NOT invent laws, sections, or deadlines.
            """.trimIndent()

            val jsonPayload = """
                {
                  "contents": [
                    {
                      "parts": [
                        { "text": ${escapeJson(prompt)} }
                      ]
                    }
                  ],
                  "generationConfig": {
                    "temperature": 0.2,
                    "maxOutputTokens": 300
                  }
                }
            """.trimIndent()

            val body = jsonPayload.toRequestBody("application/json".toMediaType())
            val response = api.generateContent(key, body)
            val responseText = response.string()
            Log.d(TAG, "Gemini response received: ${responseText.take(100)}...")
            // Simple extraction of candidate text if present
            extractCandidateText(responseText)
        } catch (e: Exception) {
            Log.w(TAG, "Gemini API call failed or timed out: ${e.message}. Falling back cleanly.")
            null
        }
    }

    private fun escapeJson(str: String): String {
        return "\"" + str.replace("\\", "\\\\")
            .replace("\"", "\\\"")
            .replace("\n", "\\n")
            .replace("\r", "\\r") + "\""
    }

    private fun extractCandidateText(json: String): String? {
        val textMarker = "\"text\":"
        val idx = json.indexOf(textMarker)
        if (idx == -1) return null
        val start = json.indexOf("\"", idx + textMarker.length)
        if (start == -1) return null
        val end = json.indexOf("\"", start + 1)
        if (end == -1) return null
        return json.substring(start + 1, end)
            .replace("\\n", "\n")
            .replace("\\\"", "\"")
    }
}
