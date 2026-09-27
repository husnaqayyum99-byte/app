package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "saved_legal_cases")
data class CaseEntity(
    @PrimaryKey
    val caseId: String,
    val originalProblem: String,
    val location: String,
    val province: String,
    val language: String,
    val category: String,
    val subCategory: String,
    val urgency: String,
    val isEmergency: Boolean,
    val summaryEn: String,
    val summaryUr: String,
    val authorityName: String,
    val authorityAddress: String,
    val stepsCount: Int,
    val createdAtTimestamp: Long,
    val isBookmarked: Boolean = false
)
