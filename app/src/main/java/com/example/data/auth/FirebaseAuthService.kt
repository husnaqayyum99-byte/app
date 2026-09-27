package com.example.data.auth

import android.app.Activity
import android.content.Context
import android.util.Log
import androidx.credentials.CredentialManager
import androidx.credentials.GetCredentialRequest
import androidx.credentials.exceptions.GetCredentialCancellationException
import androidx.credentials.exceptions.GetCredentialException
import com.example.BuildConfig
import com.google.android.libraries.identity.googleid.GetGoogleIdOption
import com.google.android.libraries.identity.googleid.GoogleIdTokenCredential
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.FirebaseUser
import com.google.firebase.auth.GoogleAuthProvider
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Manages Firebase Authentication and Google Sign-In using Android Credential Manager.
 */
class FirebaseAuthService(private val context: Context) {

    private val auth: FirebaseAuth? by lazy {
        try {
            FirebaseInitializer.init(context)
            FirebaseAuth.getInstance()
        } catch (e: Exception) {
            Log.e(TAG, "Error obtaining FirebaseAuth instance: ${e.message}")
            null
        }
    }

    private val credentialManager: CredentialManager by lazy { CredentialManager.create(context) }

    private val _currentUser = MutableStateFlow<FirebaseUser?>(null)
    val currentUser: StateFlow<FirebaseUser?> = _currentUser.asStateFlow()

    private val _authError = MutableStateFlow<String?>(null)
    val authError: StateFlow<String?> = _authError.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    init {
        try {
            val authInstance = auth
            if (authInstance != null) {
                _currentUser.value = authInstance.currentUser
                authInstance.addAuthStateListener { firebaseAuth ->
                    _currentUser.value = firebaseAuth.currentUser
                }
            } else {
                Log.w(TAG, "FirebaseAuth instance is null, running in offline/guest mode")
            }
        } catch (e: Exception) {
            Log.e(TAG, "Error initializing FirebaseAuth: ${e.message}")
        }
    }

    /**
     * Initiates Google Sign-In via Credential Manager.
     */
    suspend fun signInWithGoogle(activity: Activity): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _authError.value = null

        val authInstance = auth
        if (authInstance == null) {
            Log.w(TAG, "FirebaseAuth is null, proceeding in guest mode")
            return@withContext signInAnonymously("Guest Citizen")
        }

        try {
            // Google Web Client ID for OAuth
            val webClientId = "298973801485-placeholder.apps.googleusercontent.com"

            val googleIdOption = GetGoogleIdOption.Builder()
                .setFilterByAuthorizedAccounts(false)
                .setServerClientId(webClientId)
                .setAutoSelectEnabled(false)
                .build()

            val request = GetCredentialRequest.Builder()
                .addCredentialOption(googleIdOption)
                .build()

            val result = credentialManager.getCredential(
                context = activity,
                request = request
            )

            val credential = result.credential
            if (credential.type == GoogleIdTokenCredential.TYPE_GOOGLE_ID_TOKEN_CREDENTIAL) {
                val googleIdTokenCredential = GoogleIdTokenCredential.createFrom(credential.data)
                val idToken = googleIdTokenCredential.idToken

                val firebaseCredential = GoogleAuthProvider.getCredential(idToken, null)
                val authResult = authInstance.signInWithCredential(firebaseCredential).await()
                val user = authResult.user
                _currentUser.value = user
                _isLoading.value = false
                return@withContext Result.success(user)
            } else {
                val err = "Unsupported credential type: ${credential.type}"
                _authError.value = err
                _isLoading.value = false
                return@withContext Result.failure(Exception(err))
            }
        } catch (e: GetCredentialCancellationException) {
            _isLoading.value = false
            _authError.value = "Sign-in was cancelled"
            return@withContext Result.failure(e)
        } catch (e: GetCredentialException) {
            Log.w(TAG, "CredentialManager failed, trying anonymous fallback: ${e.message}")
            return@withContext signInAnonymously("Guest (${e.localizedMessage?.take(20) ?: "Dev Mode"})")
        } catch (e: Exception) {
            Log.e(TAG, "Sign-in exception: ${e.message}", e)
            return@withContext signInAnonymously("Guest User")
        } finally {
            _isLoading.value = false
        }
    }

    /**
     * Sign in anonymously / guest mode for quick access or dev testing.
     */
    suspend fun signInAnonymously(fallbackName: String = "Guest Citizen"): Result<FirebaseUser?> = withContext(Dispatchers.IO) {
        _isLoading.value = true
        _authError.value = null
        val authInstance = auth
        if (authInstance == null) {
            _isLoading.value = false
            return@withContext Result.success(null)
        }

        try {
            val authResult = authInstance.signInAnonymously().await()
            val user = authResult.user
            _currentUser.value = user
            _isLoading.value = false
            Result.success(user)
        } catch (e: Exception) {
            Log.e(TAG, "Anonymous sign-in error: ${e.message}")
            _authError.value = e.localizedMessage
            _isLoading.value = false
            Result.failure(e)
        }
    }

    /**
     * Sign out the currently logged-in user.
     */
    fun signOut() {
        try {
            auth?.signOut()
            _currentUser.value = null
            _authError.value = null
        } catch (e: Exception) {
            Log.e(TAG, "Error signing out: ${e.message}")
        }
    }

    fun clearError() {
        _authError.value = null
    }

    companion object {
        private const val TAG = "ApnaWakeel_Auth"
    }
}
