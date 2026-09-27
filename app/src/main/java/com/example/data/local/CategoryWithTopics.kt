package com.example.data.local

import androidx.room.Embedded
import androidx.room.Relation

/**
 * Composite data class representing a Legal Category with all its associated Legal Topics.
 */
data class CategoryWithTopics(
    @Embedded
    val category: LegalCategoryEntity,
    @Relation(
        parentColumn = "categoryId",
        entityColumn = "categoryId"
    )
    val topics: List<LegalTopicEntity>
)
