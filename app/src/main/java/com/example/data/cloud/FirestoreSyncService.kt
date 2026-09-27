package com.example.data.cloud

import android.util.Log
import com.example.data.local.CaseEntity
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.SetOptions
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext

/**
 * Service to persist and synchronize user legal data, consultations, and bookmarked
 * cases with Cloud Firestore under user-specific subcollections.
 */
class FirestoreSyncService {

    private val firestore: FirebaseFirestore? by lazy {
        try {
            FirebaseFirestore.getInstance()
        } catch (e: Exception) {
            Log.w(TAG, "Firestore instance unavailable: ${e.message}")
            null
        }
    }

    /**
     * Saves or updates a legal case in Firestore for the given user.
     */
    suspend fun saveCaseToCloud(userId: String, case: CaseEntity): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.success(Unit)
        try {
            val caseMap = hashMapOf(
                "caseId" to case.caseId,
                "originalProblem" to case.originalProblem,
                "location" to case.location,
                "province" to case.province,
                "language" to case.language,
                "category" to case.category,
                "subCategory" to case.subCategory,
                "urgency" to case.urgency,
                "isEmergency" to case.isEmergency,
                "summaryEn" to case.summaryEn,
                "summaryUr" to case.summaryUr,
                "authorityName" to case.authorityName,
                "authorityAddress" to case.authorityAddress,
                "stepsCount" to case.stepsCount,
                "isBookmarked" to case.isBookmarked,
                "createdAtTimestamp" to case.createdAtTimestamp,
                "lastSyncedAt" to System.currentTimeMillis()
            )

            fs.collection("users")
                .document(userId)
                .collection("cases")
                .document(case.caseId)
                .set(caseMap, SetOptions.merge())
                .await()

            Log.d(TAG, "Successfully synced case ${case.caseId} to Firestore for user $userId")
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to sync case to Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Deletes a legal case from Firestore.
     */
    suspend fun deleteCaseFromCloud(userId: String, caseId: String): Result<Unit> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext Result.success(Unit)
        try {
            fs.collection("users")
                .document(userId)
                .collection("cases")
                .document(caseId)
                .delete()
                .await()
            Result.success(Unit)
        } catch (e: Exception) {
            Log.e(TAG, "Failed to delete case from Firestore: ${e.message}", e)
            Result.failure(e)
        }
    }

    /**
     * Fetches all cloud-synced legal cases for a user.
     */
    suspend fun fetchCloudCases(userId: String): List<CaseEntity> = withContext(Dispatchers.IO) {
        val fs = firestore ?: return@withContext emptyList()
        try {
            val snapshot = fs.collection("users")
                .document(userId)
                .collection("cases")
                .get()
                .await()

            snapshot.documents.mapNotNull { doc ->
                try {
                    CaseEntity(
                        caseId = doc.getString("caseId") ?: doc.id,
                        originalProblem = doc.getString("originalProblem") ?: "",
                        location = doc.getString("location") ?: "Chitral (Lower)",
                        province = doc.getString("province") ?: "Khyber Pakhtunkhwa",
                        language = doc.getString("language") ?: "ENGLISH",
                        category = doc.getString("category") ?: "GENERAL",
                        subCategory = doc.getString("subCategory") ?: "",
                        urgency = doc.getString("urgency") ?: "NORMAL",
                        isEmergency = doc.getBoolean("isEmergency") ?: false,
                        summaryEn = doc.getString("summaryEn") ?: "",
                        summaryUr = doc.getString("summaryUr") ?: "",
                        authorityName = doc.getString("authorityName") ?: "",
                        authorityAddress = doc.getString("authorityAddress") ?: "",
                        stepsCount = (doc.getLong("stepsCount") ?: 0L).toInt(),
                        createdAtTimestamp = doc.getLong("createdAtTimestamp") ?: System.currentTimeMillis(),
                        isBookmarked = doc.getBoolean("isBookmarked") ?: false
                    )
                } catch (ex: Exception) {
                    null
                }
            }
        } catch (e: Exception) {
            Log.e(TAG, "Failed to fetch cases from Firestore: ${e.message}")
            emptyList()
        }
    }

    /**
     * Observes real-time updates of cloud cases for a user.
     */
    fun observeCloudCases(userId: String): Flow<List<CaseEntity>> = callbackFlow {
        val fs = firestore
        if (fs == null) {
            trySend(emptyList())
            close()
            return@callbackFlow
        }

        val listener = fs.collection("users")
            .document(userId)
            .collection("cases")
            .addSnapshotListener { snapshot, error ->
                if (error != null) {
                    Log.w(TAG, "Listen failed for user cases: ${error.message}")
                    return@addSnapshotListener
                }

                if (snapshot != null) {
                    val cases = snapshot.documents.mapNotNull { doc ->
                        try {
                            CaseEntity(
                                caseId = doc.getString("caseId") ?: doc.id,
                                originalProblem = doc.getString("originalProblem") ?: "",
                                location = doc.getString("location") ?: "Chitral (Lower)",
                                province = doc.getString("province") ?: "Khyber Pakhtunkhwa",
                                language = doc.getString("language") ?: "ENGLISH",
                                category = doc.getString("category") ?: "GENERAL",
                                subCategory = doc.getString("subCategory") ?: "",
                                urgency = doc.getString("urgency") ?: "NORMAL",
                                isEmergency = doc.getBoolean("isEmergency") ?: false,
                                summaryEn = doc.getString("summaryEn") ?: "",
                                summaryUr = doc.getString("summaryUr") ?: "",
                                authorityName = doc.getString("authorityName") ?: "",
                                authorityAddress = doc.getString("authorityAddress") ?: "",
                                stepsCount = (doc.getLong("stepsCount") ?: 0L).toInt(),
                                createdAtTimestamp = doc.getLong("createdAtTimestamp") ?: System.currentTimeMillis(),
                                isBookmarked = doc.getBoolean("isBookmarked") ?: false
                            )
                        } catch (ex: Exception) {
                            null
                        }
                    }
                    trySend(cases)
                }
            }

        awaitClose { listener.remove() }
    }

    companion object {
        private const val TAG = "ApnaWakeel_Firestore"
    }
}
