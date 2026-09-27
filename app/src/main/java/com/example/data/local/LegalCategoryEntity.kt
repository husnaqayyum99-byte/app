package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Represents a major legal branch under Khyber Pakhtunkhwa law
 * (e.g., Property Law, Family Law, Criminal Procedure).
 */
@Entity(tableName = "kp_legal_categories")
data class LegalCategoryEntity(
    @PrimaryKey
    val categoryId: String,
    val nameEn: String,
    val nameUr: String,
    val descriptionEn: String,
    val descriptionUr: String,
    val iconName: String,
    val displayOrder: Int = 0
)
