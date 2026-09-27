package com.example.data.audio

import android.content.Context
import android.media.MediaRecorder
import android.os.Build
import android.util.Base64
import android.util.Log
import kotlinx.coroutines.*
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.io.File
import java.io.FileInputStream

/**
 * Manages audio recording from the device microphone for speech transcription
 * and voice conversations.
 */
class AudioRecordingManager(private val context: Context) {

    private var mediaRecorder: MediaRecorder? = null
    private var currentAudioFile: File? = null
    private var amplitudeJob: Job? = null
    private val scope = CoroutineScope(Dispatchers.Default + SupervisorJob())

    private val _isRecording = MutableStateFlow(false)
    val isRecording: StateFlow<Boolean> = _isRecording.asStateFlow()

    private val _recordingDurationSeconds = MutableStateFlow(0)
    val recordingDurationSeconds: StateFlow<Int> = _recordingDurationSeconds.asStateFlow()

    private val _currentAmplitude = MutableStateFlow(0f)
    val currentAmplitude: StateFlow<Float> = _currentAmplitude.asStateFlow()

    fun startRecording(): Boolean {
        if (_isRecording.value) return true

        return try {
            val audioDir = File(context.cacheDir, "audio_records").apply { mkdirs() }
            val outputFile = File(audioDir, "rec_${System.currentTimeMillis()}.m4a")
            currentAudioFile = outputFile

            val recorder = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                MediaRecorder(context)
            } else {
                @Suppress("DEPRECATION")
                MediaRecorder()
            }

            recorder.apply {
                setAudioSource(MediaRecorder.AudioSource.MIC)
                setOutputFormat(MediaRecorder.OutputFormat.MPEG_4)
                setAudioEncoder(MediaRecorder.AudioEncoder.AAC)
                setAudioSamplingRate(16000)
                setAudioEncodingBitRate(32000)
                setOutputFile(outputFile.absolutePath)
                prepare()
                start()
            }

            mediaRecorder = recorder
            _isRecording.value = true
            _recordingDurationSeconds.value = 0

            startAmplitudePolling()
            true
        } catch (e: Exception) {
            Log.e(TAG, "Failed to start audio recording: ${e.message}", e)
            releaseRecorder()
            false
        }
    }

    private fun startAmplitudePolling() {
        amplitudeJob?.cancel()
        amplitudeJob = scope.launch {
            var seconds = 0
            var ticks = 0
            while (_isRecording.value) {
                try {
                    val maxAmp = mediaRecorder?.maxAmplitude ?: 0
                    // Normalize amplitude 0..1
                    val normalized = (maxAmp.toFloat() / 32767f).coerceIn(0f, 1f)
                    _currentAmplitude.value = normalized
                } catch (e: Exception) {
                    // Ignore transient errors
                }
                delay(100)
                ticks++
                if (ticks % 10 == 0) {
                    seconds++
                    _recordingDurationSeconds.value = seconds
                }
            }
        }
    }

    /**
     * Stops recording and returns the base64-encoded audio data and mimeType ("audio/mp4").
     */
    fun stopRecording(): AudioRecordingResult? {
        if (!_isRecording.value) return null

        try {
            amplitudeJob?.cancel()
            _isRecording.value = false
            _currentAmplitude.value = 0f

            mediaRecorder?.apply {
                stop()
                release()
            }
            mediaRecorder = null

            val file = currentAudioFile ?: return null
            if (!file.exists() || file.length() == 0L) {
                return null
            }

            val bytes = FileInputStream(file).use { it.readBytes() }
            val base64Data = Base64.encodeToString(bytes, Base64.NO_WRAP)

            return AudioRecordingResult(
                file = file,
                base64Data = base64Data,
                mimeType = "audio/mp4",
                durationSeconds = _recordingDurationSeconds.value
            )
        } catch (e: Exception) {
            Log.e(TAG, "Failed to stop recording: ${e.message}", e)
            releaseRecorder()
            return null
        }
    }

    fun cancelRecording() {
        amplitudeJob?.cancel()
        _isRecording.value = false
        _currentAmplitude.value = 0f
        releaseRecorder()
        currentAudioFile?.delete()
        currentAudioFile = null
    }

    private fun releaseRecorder() {
        try {
            mediaRecorder?.reset()
            mediaRecorder?.release()
        } catch (e: Exception) {
            // Ignore
        }
        mediaRecorder = null
    }

    companion object {
        private const val TAG = "ApnaWakeel_Audio"
    }
}

data class AudioRecordingResult(
    val file: File,
    val base64Data: String,
    val mimeType: String,
    val durationSeconds: Int
)
