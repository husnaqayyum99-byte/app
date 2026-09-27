package com.example.data.ai

import android.content.Context
import android.speech.tts.TextToSpeech
import android.util.Log
import com.example.BuildConfig
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.OkHttpClient
import okhttp3.Request
import okhttp3.RequestBody.Companion.toRequestBody
import org.json.JSONArray
import org.json.JSONObject
import java.util.Locale
import java.util.concurrent.TimeUnit

data class LiveTurn(
    val id: String = java.util.UUID.randomUUID().toString(),
    val role: String, // "user" or "model"
    val text: String,
    val timestamp: Long = System.currentTimeMillis()
)

enum class LiveConnectionState {
    IDLE,
    CONNECTING,
    LISTENING,
    PROCESSING,
    SPEAKING,
    ERROR
}

/**
 * Service managing real-time voice conversations with the model `gemini-3.8-live`.
 */
class GeminiLiveVoiceService(private val context: Context) : TextToSpeech.OnInitListener {

    private val okHttpClient = OkHttpClient.Builder()
        .connectTimeout(60, TimeUnit.SECONDS)
        .readTimeout(60, TimeUnit.SECONDS)
        .writeTimeout(60, TimeUnit.SECONDS)
        .build()

    private var tts: TextToSpeech? = null
    private var isTtsReady = false

    private val _connectionState = MutableStateFlow(LiveConnectionState.IDLE)
    val connectionState: StateFlow<LiveConnectionState> = _connectionState.asStateFlow()

    private val _conversationHistory = MutableStateFlow<List<LiveTurn>>(emptyList())
    val conversationHistory: StateFlow<List<LiveTurn>> = _conversationHistory.asStateFlow()

    private val _statusMessage = MutableStateFlow<String?>("Ready for voice consultation")
    val statusMessage: StateFlow<String?> = _statusMessage.asStateFlow()

    private val _isTtsEnabled = MutableStateFlow(true)
    val isTtsEnabled: StateFlow<Boolean> = _isTtsEnabled.asStateFlow()

    init {
        try {
            tts = TextToSpeech(context, this)
        } catch (e: Exception) {
            Log.w(TAG, "TTS initialization failed: ${e.message}")
        }
    }

    override fun onInit(status: Int) {
        if (status == TextToSpeech.SUCCESS) {
            val result = tts?.setLanguage(Locale("ur", "PK"))
            if (result == TextToSpeech.LANG_MISSING_DATA || result == TextToSpeech.LANG_NOT_SUPPORTED) {
                tts?.setLanguage(Locale.ENGLISH)
            }
            isTtsReady = true
        } else {
            isTtsReady = false
        }
    }

    fun toggleTts() {
        val next = !_isTtsEnabled.value
        _isTtsEnabled.value = next
        if (!next) {
            stopSpeaking()
        }
    }

    fun stopSpeaking() {
        if (isTtsReady) {
            tts?.stop()
        }
        if (_connectionState.value == LiveConnectionState.SPEAKING) {
            _connectionState.value = LiveConnectionState.IDLE
        }
    }

    /**
     * Sends a user voice turn or transcript to `gemini-3.8-live` and obtains the live response.
     */
    suspend fun sendConversationTurn(userMessage: String): Result<String> = withContext(Dispatchers.IO) {
        if (userMessage.isBlank()) return@withContext Result.failure(IllegalArgumentException("Message cannot be empty"))

        val apiKey = try {
            BuildConfig.GEMINI_API_KEY
        } catch (e: Exception) {
            ""
        }

        if (apiKey.isBlank() || apiKey == "MY_GEMINI_API_KEY") {
            val fallbackResponse = "Assalam-o-Alaikum. I am your Apna Wakeel legal assistant. Please note that Gemini Live API requires an API key in the Secrets panel. Regarding your question: under KP law, disputes should be brought before the competent authority such as the Deputy Commissioner or Senior Civil Judge."
            addTurn("user", userMessage)
            addTurn("model", fallbackResponse)
            speakResponse(fallbackResponse)
            return@withContext Result.success(fallbackResponse)
        }

        try {
            _connectionState.value = LiveConnectionState.PROCESSING
            _statusMessage.value = "Consulting gemini-3.8-live..."

            // Add user turn to UI immediately
            addTurn("user", userMessage)

            val url = "https://generativelanguage.googleapis.com/v1beta/models/gemini-3.8-live:generateContent?key=$apiKey"

            // Build multi-turn context
            val contentsArray = JSONArray()

            // System instructions context in first turn or prompt
            val systemInstruction = """
                You are 'Apna Wakeel Live' (اپنا وکیل لائیو), a real-time conversational legal voice advisor for citizens in Chitral and Khyber Pakhtunkhwa (KP), Pakistan.
                Answer conversationally in 2-4 sentences suitable for spoken audio.
                Be respectful, authoritative, and compassionate.
                Cite the KP Code, CrPC 1898, Family Courts Act 1964, or Land Revenue Act 1967 when relevant.
                Direct citizens to the exact official desk (e.g. DC Lower Chitral, DPO Upper Chitral, Tehsildar, Family Court).
            """.trimIndent()

            // Add recent history (up to last 6 turns)
            val recentTurns = _conversationHistory.value.takeLast(6)
            for (turn in recentTurns) {
                contentsArray.put(JSONObject().apply {
                    put("role", if (turn.role == "user") "user" else "model")
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", turn.text) })
                    })
                })
            }

            val payload = JSONObject().apply {
                put("contents", contentsArray)
                put("systemInstruction", JSONObject().apply {
                    put("parts", JSONArray().apply {
                        put(JSONObject().apply { put("text", systemInstruction) })
                    })
                })
                put("generationConfig", JSONObject().apply {
                    put("temperature", 0.4)
                    put("maxOutputTokens", 400)
                })
            }

            val request = Request.Builder()
                .url(url)
                .post(payload.toString().toRequestBody("application/json".toMediaType()))
                .build()

            val response = okHttpClient.newCall(request).execute()
            val responseBody = response.body?.string() ?: ""

            if (!response.isSuccessful) {
                Log.e(TAG, "Live call failed (HTTP ${response.code}): $responseBody")
                _connectionState.value = LiveConnectionState.ERROR
                _statusMessage.value = "Live API error (HTTP ${response.code})"
                return@withContext Result.failure(Exception("Live API error: $responseBody"))
            }

            val jsonResponse = JSONObject(responseBody)
            val candidates = jsonResponse.optJSONArray("candidates")
            val candidate = candidates?.optJSONObject(0)
            val content = candidate?.optJSONObject("content")
            val parts = content?.optJSONArray("parts")
            val modelText = parts?.optJSONObject(0)?.optString("text")?.trim() ?: "I understand your legal question. Could you clarify the location or authority involved?"

            addTurn("model", modelText)
            _connectionState.value = LiveConnectionState.SPEAKING
            _statusMessage.value = "Responding with gemini-3.8-live"

            speakResponse(modelText)

            Result.success(modelText)
        } catch (e: Exception) {
            Log.e(TAG, "Live conversation error: ${e.message}", e)
            _connectionState.value = LiveConnectionState.ERROR
            _statusMessage.value = "Error: ${e.localizedMessage}"
            Result.failure(e)
        }
    }

    private fun speakResponse(text: String) {
        if (!_isTtsEnabled.value || !isTtsReady) {
            _connectionState.value = LiveConnectionState.IDLE
            return
        }

        try {
            // Clean text of markdown characters before speaking
            val cleanText = text.replace(Regex("[*#_`>]"), "").trim()
            tts?.speak(cleanText, TextToSpeech.QUEUE_FLUSH, null, "LiveVoiceResponse")
        } catch (e: Exception) {
            Log.e(TAG, "Failed TTS speak: ${e.message}")
        }
    }

    private fun addTurn(role: String, text: String) {
        val newTurn = LiveTurn(role = role, text = text)
        _conversationHistory.value = _conversationHistory.value + newTurn
    }

    fun clearConversation() {
        stopSpeaking()
        _conversationHistory.value = emptyList()
        _connectionState.value = LiveConnectionState.IDLE
        _statusMessage.value = "Ready for voice consultation"
    }

    fun release() {
        try {
            tts?.stop()
            tts?.shutdown()
        } catch (e: Exception) {
            // Ignore
        }
        tts = null
    }

    companion object {
        private const val TAG = "ApnaWakeel_LiveVoice"
    }
}
