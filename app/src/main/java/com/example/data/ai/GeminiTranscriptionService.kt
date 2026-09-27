package com.example.data.ai

import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONObject
import java.util.concurrent.TimeUnit

/**
 * Service to transcribe microphone audio using the Gemini model `gemini-3.5-transcribe`.
 */
class GeminiTranscriptionService {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    /**
     * Transcribes audio base64 data into clean text using `gemini-3.5-transcribe`.
     */
    suspend fun transcribeAudio(
        base64AudioData: String,
        mimeType: String = "audio/mp4"
    ): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.failure(
                IllegalStateException("Gemini API key is missing or not configured. Please add GEMINI_API_KEY to AI Studio Secrets.")
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.5-transcribe:generateContent?key=$apiKey"

            val promptText = "Transcribe the spoken audio verbatim in its original spoken language (Urdu, English, Pashto, or Chitrali/Khowar). Capture legal issues accurately including names of places in Chitral or KP, police stations, courts, and dispute details. Output ONLY the transcription text."

            val jsonPayload = JSONObject().apply {
                val contentsArray = org.json.JSONArray().apply {
                    val contentObj = JSONObject().apply {
                        val partsArray = org.json.JSONArray().apply {
                            // Audio inline data part
                            put(JSONObject().apply {
                                put("inlineData", JSONObject().apply {
                                    put("mimeType", mimeType)
                                    put("data", base64AudioData)
                                })
                            })
                            // Text instruction part
                            put(JSONObject().apply {
                                put("text", promptText)
                            })
                        }
                        put("parts", partsArray)
                    }
                    put(contentObj)
                }
                put("contents", contentsArray)
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.0)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(jsonPayload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Transcription failed with code ${response.code}: $responseBody")
                return@withContext Result.failure(
                    Exception("Transcription request failed (HTTP ${response.code}): $responseBody")
                )
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val firstCandidate = candidates?.optJSONObject(0)
            val content = firstCandidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val text = parts?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                Log.d(TAG, "Audio transcribed successfully: $text")
                Result.success(text.trim())
            } else {
                Result.failure(Exception("Transcription response contained no text."))
            }
        } catch (e: Exception) {
            Log.e(TAG, "Transcription exception: ${e.message}", e)
            Result.failure(e)
        }
    }

    companion object {
        private const val TAG = "ApnaWakeel_Transcribe"
    }
}
