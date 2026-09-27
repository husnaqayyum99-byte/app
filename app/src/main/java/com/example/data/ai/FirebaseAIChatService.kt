package com.example.data.ai

import android.content.Context
import android.util.Log
import com.example.BuildConfig
import com.example.data.auth.FirebaseInitializer
import com.google.firebase.FirebaseApp
import com.google.firebase.ai.Chat
import com.google.firebase.ai.FirebaseAI
import com.google.firebase.ai.GenerativeModel
import com.google.firebase.ai.type.Content
import com.google.firebase.ai.type.TextPart
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.concurrent.TimeUnit

data class LegalChatMessage(
    val id: String = java.util.UUID.randomUUID().toString(),
    val sender: MessageSender,
    val text: String,
    val timestamp: Long = System.currentTimeMillis(),
    val isStreaming: Boolean = false
)

enum class MessageSender {
    USER,
    AI_COUNSEL
}

/**
 * Connects with Gemini using the Firebase AI SDK (`com.google.firebase.ai`)
 * to provide interactive conversational legal guidance for Khyber Pakhtunkhwa procedures.
 */
class FirebaseAIChatService(private val context: Context) {

    private var generativeModel: GenerativeModel? = null
    private var activeChatSession: Chat? = null

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private val fallbackHistory = mutableListOf<JSONObject>()

    init {
        initFirebaseAI()
    }

    private fun initFirebaseAI() {
        try {
            FirebaseInitializer.init(context)

            val systemInstruction = Content(
                role = "system",
                parts = listOf(
                    TextPart(
                        """
                        You are 'Apna Wakeel Legal Advisor' (اپنا وکیل قانونی مشیر), an expert on Khyber Pakhtunkhwa (KP) procedural law and citizen legal navigation in Pakistan, with specialized knowledge of Chitral (Lower and Upper Chitral).
                        
                        Your responsibilities:
                        1. Explain exact official procedures under KP and Pakistan laws clearly to citizens.
                        2. Cover areas including:
                           - Criminal procedures: FIR registration (CrPC 154), private complaints (CrPC 200), bail (pre-arrest under 498, post-arrest under 497), police investigation, vehicle release (Superdari under 516A).
                           - Land & Revenue: Land partition (KP Land Revenue Act Sec 135), mutation/intiqal (Sec 42), fard issuance, boundary demarcation (Hadd Shikni), revenue court appeals before Tehsildar/AC/DC.
                           - Family Law: Khula dissolution (KP Family Courts Act 1964), maintenance/Kharcha (Family Courts Act Sec 17A), child custody (Guardian & Wards Act 1890), Nikahnama registration.
                           - Civil & Dispute Resolution: Dispute Resolution Council (DRC) in KP police stations, Civil Procedure Code (CPC 1908), stay orders (Order 39 Rule 1 & 2).
                           - Citizen Rights: Right to Information (KP RTI Act 2013), consumer complaints (KP Consumer Protection Act 1997).
                        3. For every procedural answer, structure your reply with:
                           - Step-by-Step Procedure (where to go first, second, etc.)
                           - Competent Authority & Location (e.g. Police Station Chitral, Senior Civil Judge / Family Court, Tehsildar)
                           - Mandatory Documents Required (CNIC, Fard, receipts, affidavits)
                           - Statutory Basis & Section Numbers (e.g. Sec 135 Land Revenue Act 1967)
                           - Expected Official Timelines & Cost
                        4. Support both English and Urdu (اردو) fluently. Answer in the language the citizen used.
                        5. Be respectful, authoritative, precise, and supportive.
                        """.trimIndent()
                    )
                )
            )

            val app = FirebaseApp.getInstance()
            val ai = FirebaseAI.getInstance(app)
            generativeModel = ai.generativeModel(
                modelName = "gemini-2.5-flash",
                systemInstruction = systemInstruction
            )
            activeChatSession = generativeModel?.startChat()
            Log.d(TAG, "Firebase AI SDK initialized successfully with model gemini-2.5-flash")
        } catch (e: Exception) {
            Log.w(TAG, "Firebase AI SDK init encountered issue: ${e.message}. Fallback prepared.")
        }
    }

    /**
     * Sends a message to Gemini using the Firebase AI SDK, falling back gracefully
     * if the remote environment lacks Firebase AppCheck token or cloud credentials.
     */
    suspend fun sendMessage(userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        val chat = activeChatSession
        if (chat != null) {
            try {
                val response = chat.sendMessage(userMessage)
                val responseText = response.text
                if (!responseText.isNullOrBlank()) {
                    return@withContext Result.success(responseText.trim())
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase AI SDK chat.sendMessage failed (${e.message}), trying REST fallback...")
            }
        }

        // Direct fallback via Gemini API
        return@withContext sendFallbackMessage(userMessage)
    }

    /**
     * Sends a message and receives streaming chunks via Flow.
     */
    fun sendMessageStream(userMessage: String): Flow<String> = flow {
        val chat = activeChatSession
        var streamSucceeded = false
        if (chat != null) {
            try {
                chat.sendMessageStream(userMessage).collect { chunk ->
                    val text = chunk.text
                    if (!text.isNullOrBlank()) {
                        streamSucceeded = true
                        emit(text)
                    }
                }
            } catch (e: Exception) {
                Log.w(TAG, "Firebase AI SDK streaming failed: ${e.message}, falling back")
            }
        }

        if (!streamSucceeded) {
            val fallbackRes = sendFallbackMessage(userMessage)
            fallbackRes.onSuccess { fullText ->
                emit(fullText)
            }.onFailure { err ->
                emit("Legal guidance unavailable: ${err.localizedMessage}")
            }
        }
    }.flowOn(Dispatchers.IO)

    /**
     * Fallback REST caller to ensure uninterrupted citizen service in dev environments.
     */
    private suspend fun sendFallbackMessage(userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            return@withContext Result.success(
                "Regarding your inquiry about KP legal procedures: under Khyber Pakhtunkhwa statutes, you should present your matter to the designated competent authority (e.g. Deputy Commissioner, Assistant Commissioner, or the Senior Civil Judge/Family Court). Please configure your GEMINI_API_KEY in AI Studio to activate live model responses."
            )
        }

        try {
            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-2.5-flash:generateContent?key=$apiKey"

            // Maintain conversation context in fallback
            val userTurn = JSONObject().apply {
                put("role", "user")
                put("parts", JSONArray().apply { put(JSONObject().apply { put("text", userMessage) }) })
            }
            fallbackHistory.add(userTurn)

            val contentsArray = JSONArray()
            fallbackHistory.takeLast(10).forEach { contentsArray.put(it) }

            val payload = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(
                            JSONObject().apply {
                                put(
                                    "text",
                                    "You are 'Apna Wakeel Legal Advisor' using Firebase AI SDK and Gemini for Khyber Pakhtunkhwa legal procedures. Give actionable steps, relevant KP Code and CrPC/PPC/Family Law sections, competent authorities, and documents needed."
                                )
                            }
                        )
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.3)
                    put("maxOutputTokens", 1200)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                return@withContext Result.failure(Exception("HTTP ${response.code}: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidate = jsonResponse.optJSONArray("candidates")?.optJSONObject(0)
            val text = candidate?.optJSONObject("content")?.optJSONArray("parts")?.optJSONObject(0)?.optString("text")

            if (!text.isNullOrBlank()) {
                val modelTurn = JSONObject().apply {
                    put("role", "model")
                    put("parts", JSONArray().apply { put(JSONObject().apply { put("text", text) }) })
                }
                fallbackHistory.add(modelTurn)
                Result.success(text.trim())
            } else {
                Result.failure(Exception("Empty response from legal service"))
            }
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    fun resetChat() {
        try {
            activeChatSession = generativeModel?.startChat()
            fallbackHistory.clear()
        } catch (e: Exception) {
            Log.w(TAG, "Failed resetting chat: ${e.message}")
        }
    }

    companion object {
        private const val TAG = "ApnaWakeel_FirebaseAI"
    }
}
