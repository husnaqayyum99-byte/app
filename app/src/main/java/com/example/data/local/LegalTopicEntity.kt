package com.example.data.local

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

/**
 * Stores specific categorized legal topics under Khyber Pakhtunkhwa law,
 * detailing statutory provisions, relevant procedural steps, authorities, and documents.
 */
@Entity(
    tableName = "kp_legal_topics",
    foreignKeys = [
        ForeignKey(
            entity = LegalCategoryEntity::class,
            parentColumns = ["categoryId"],
            childColumns = ["categoryId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["categoryId"]),
        Index(value = ["statuteName"]),
        Index(value = ["isBookmarked"])
    ]
)
data class LegalTopicEntity(
    @PrimaryKey
    val topicId: String,
    val categoryId: String,
    val titleEn: String,
    val titleUr: String,
    val statuteName: String,
    val statuteNameUr: String,
    val relevantSections: String,
    val jurisdiction: String,
    val summaryEn: String,
    val summaryUr: String,
    val responsibleAuthorityEn: String,
    val responsibleAuthorityUr: String,
    val requiredDocumentsEn: String,
    val requiredDocumentsUr: String,
    val proceduralStepsEn: String,
    val proceduralStepsUr: String,
    val officialSourceUrl: String,
    val isKpSpecific: Boolean = true,
    val isBookmarked: Boolean = false,
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
