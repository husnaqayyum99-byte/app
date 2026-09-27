package com.example.data.local

import androidx.room.*
import kotlinx.coroutines.flow.Flow

/**
 * Data Access Object (DAO) interface for performing CRUD operations
 * on the Khyber Pakhtunkhwa legal topics and categories entities.
 */
@Dao
interface LegalTopicDao {

    // ==========================================
    // CREATE (INSERT) OPERATIONS
    // ==========================================

    /**
     * Inserts a single legal topic into the database.
     * Replaces existing record if topicId collides.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopic(topic: LegalTopicEntity)

    /**
     * Inserts a list of legal topics in batch into the database.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTopics(topics: List<LegalTopicEntity>)

    /**
     * Inserts a single legal category.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategory(category: LegalCategoryEntity)

    /**
     * Inserts multiple legal categories in batch.
     */
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertCategories(categories: List<LegalCategoryEntity>)

    // ==========================================
    // READ (RETRIEVE) OPERATIONS
    // ==========================================

    /**
     * Retrieves all legal topics ordered by English title as an observable Flow.
     */
    @Query("SELECT * FROM kp_legal_topics ORDER BY titleEn ASC")
    fun getAllTopics(): Flow<List<LegalTopicEntity>>

    /**
     * Retrieves all legal topics as a synchronous one-shot list.
     */
    @Query("SELECT * FROM kp_legal_topics ORDER BY titleEn ASC")
    suspend fun getAllTopicsSync(): List<LegalTopicEntity>

    /**
     * Retrieves all legal topics belonging to a specific category.
     */
    @Query("SELECT * FROM kp_legal_topics WHERE categoryId = :categoryId ORDER BY titleEn ASC")
    fun getTopicsByCategory(categoryId: String): Flow<List<LegalTopicEntity>>

    /**
     * Retrieves legal topics for a category as a synchronous list.
     */
    @Query("SELECT * FROM kp_legal_topics WHERE categoryId = :categoryId ORDER BY titleEn ASC")
    suspend fun getTopicsByCategorySync(categoryId: String): List<LegalTopicEntity>

    /**
     * Searches topics filtered by a specific category and query matching
     * title, statute name, relevant sections, or summary.
     */
    @Query("""
        SELECT * FROM kp_legal_topics 
        WHERE categoryId = :categoryId
          AND (titleEn LIKE '%' || :query || '%' 
            OR titleUr LIKE '%' || :query || '%'
            OR statuteName LIKE '%' || :query || '%'
            OR relevantSections LIKE '%' || :query || '%'
            OR summaryEn LIKE '%' || :query || '%')
        ORDER BY titleEn ASC
    """)
    fun searchTopicsByCategory(categoryId: String, query: String): Flow<List<LegalTopicEntity>>

    /**
     * Global search across all legal topics regardless of category.
     */
    @Query("""
        SELECT * FROM kp_legal_topics 
        WHERE titleEn LIKE '%' || :query || '%' 
           OR titleUr LIKE '%' || :query || '%'
           OR statuteName LIKE '%' || :query || '%'
           OR relevantSections LIKE '%' || :query || '%'
           OR summaryEn LIKE '%' || :query || '%'
           OR summaryUr LIKE '%' || :query || '%'
           OR responsibleAuthorityEn LIKE '%' || :query || '%'
        ORDER BY titleEn ASC
    """)
    fun searchTopics(query: String): Flow<List<LegalTopicEntity>>

    /**
     * Retrieves a single legal topic by its unique ID as an observable Flow.
     */
    @Query("SELECT * FROM kp_legal_topics WHERE topicId = :topicId LIMIT 1")
    fun getTopicById(topicId: String): Flow<LegalTopicEntity?>

    /**
     * Retrieves a single legal topic by its unique ID synchronously.
     */
    @Query("SELECT * FROM kp_legal_topics WHERE topicId = :topicId LIMIT 1")
    suspend fun getTopicByIdSync(topicId: String): LegalTopicEntity?

    /**
     * Retrieves all bookmarked topics.
     */
    @Query("SELECT * FROM kp_legal_topics WHERE isBookmarked = 1 ORDER BY titleEn ASC")
    fun getBookmarkedTopics(): Flow<List<LegalTopicEntity>>

    /**
     * Retrieves all legal categories ordered by display order.
     */
    @Query("SELECT * FROM kp_legal_categories ORDER BY displayOrder ASC")
    fun getAllCategories(): Flow<List<LegalCategoryEntity>>

    /**
     * Retrieves a specific category by its ID.
     */
    @Query("SELECT * FROM kp_legal_categories WHERE categoryId = :categoryId LIMIT 1")
    suspend fun getCategoryById(categoryId: String): LegalCategoryEntity?

    /**
     * Composite query retrieving categories with all their associated legal topics.
     */
    @Transaction
    @Query("SELECT * FROM kp_legal_categories ORDER BY displayOrder ASC")
    fun getCategoriesWithTopics(): Flow<List<CategoryWithTopics>>

    /**
     * Composite query retrieving a single category with its associated legal topics.
     */
    @Transaction
    @Query("SELECT * FROM kp_legal_categories WHERE categoryId = :categoryId LIMIT 1")
    fun getCategoryWithTopicsById(categoryId: String): Flow<CategoryWithTopics?>

    /**
     * Returns the count of total registered legal categories.
     */
    @Query("SELECT COUNT(*) FROM kp_legal_categories")
    suspend fun getCategoryCount(): Int

    /**
     * Returns the count of total registered legal topics.
     */
    @Query("SELECT COUNT(*) FROM kp_legal_topics")
    suspend fun getTopicCount(): Int

    // ==========================================
    // UPDATE OPERATIONS
    // ==========================================

    /**
     * Updates an existing legal topic entity in the database.
     */
    @Update
    suspend fun updateTopic(topic: LegalTopicEntity)

    /**
     * Updates the bookmark status for a specific legal topic.
     */
    @Query("UPDATE kp_legal_topics SET isBookmarked = :isBookmarked WHERE topicId = :topicId")
    suspend fun updateTopicBookmark(topicId: String, isBookmarked: Boolean)

    /**
     * Updates an existing category entity.
     */
    @Update
    suspend fun updateCategory(category: LegalCategoryEntity)

    // ==========================================
    // DELETE OPERATIONS
    // ==========================================

    /**
     * Deletes a legal topic by entity instance.
     */
    @Delete
    suspend fun deleteTopic(topic: LegalTopicEntity)

    /**
     * Deletes a legal topic by its unique ID.
     */
    @Query("DELETE FROM kp_legal_topics WHERE topicId = :topicId")
    suspend fun deleteTopicById(topicId: String)

    /**
     * Deletes all legal topics belonging to a specific category.
     */
    @Query("DELETE FROM kp_legal_topics WHERE categoryId = :categoryId")
    suspend fun deleteTopicsByCategory(categoryId: String)

    /**
     * Deletes all legal topics from the database.
     */
    @Query("DELETE FROM kp_legal_topics")
    suspend fun deleteAllTopics()
}
