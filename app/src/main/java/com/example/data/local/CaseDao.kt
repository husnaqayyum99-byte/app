package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface CaseDao {
    @Query("SELECT * FROM saved_legal_cases ORDER BY createdAtTimestamp DESC")
    fun getAllCases(): Flow<List<CaseEntity>>

    @Query("SELECT * FROM saved_legal_cases WHERE caseId = :caseId LIMIT 1")
    suspend fun getCaseById(caseId: String): CaseEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCase(caseEntity: CaseEntity)

    @Query("UPDATE saved_legal_cases SET isBookmarked = :bookmarked WHERE caseId = :caseId")
    suspend fun updateBookmark(caseId: String, bookmarked: Boolean)

    @Query("DELETE FROM saved_legal_cases WHERE caseId = :caseId")
    suspend fun deleteCase(caseId: String)
}
