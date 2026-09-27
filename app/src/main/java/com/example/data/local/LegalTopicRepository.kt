package com.example.data.local

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

/**
 * Repository for managing Khyber Pakhtunkhwa legal topics and categories,
 * abstracting the Room database layer and handling initial seeding.
 */
class LegalTopicRepository(
    private val legalTopicDao: LegalTopicDao,
    private val ioDispatcher: CoroutineDispatcher = Dispatchers.IO
) {

    val allCategories: Flow<List<LegalCategoryEntity>> = legalTopicDao.getAllCategories()
    val allTopics: Flow<List<LegalTopicEntity>> = legalTopicDao.getAllTopics()
    val categoriesWithTopics: Flow<List<CategoryWithTopics>> = legalTopicDao.getCategoriesWithTopics()
    val bookmarkedTopics: Flow<List<LegalTopicEntity>> = legalTopicDao.getBookmarkedTopics()

    fun getTopicsByCategory(categoryId: String): Flow<List<LegalTopicEntity>> {
        return legalTopicDao.getTopicsByCategory(categoryId)
    }

    fun getCategoryWithTopicsById(categoryId: String): Flow<CategoryWithTopics?> {
        return legalTopicDao.getCategoryWithTopicsById(categoryId)
    }

    fun getTopicById(topicId: String): Flow<LegalTopicEntity?> {
        return legalTopicDao.getTopicById(topicId)
    }

    fun searchTopics(query: String): Flow<List<LegalTopicEntity>> {
        return legalTopicDao.searchTopics(query.trim())
    }

    suspend fun toggleBookmark(topicId: String, isBookmarked: Boolean) = withContext(ioDispatcher) {
        legalTopicDao.updateTopicBookmark(topicId, isBookmarked)
    }

    suspend fun insertTopic(topic: LegalTopicEntity) = withContext(ioDispatcher) {
        legalTopicDao.insertTopic(topic)
    }

    suspend fun deleteTopic(topicId: String) = withContext(ioDispatcher) {
        legalTopicDao.deleteTopicById(topicId)
    }

    suspend fun updateTopic(topic: LegalTopicEntity) = withContext(ioDispatcher) {
        legalTopicDao.updateTopic(topic)
    }

    fun searchTopicsByCategory(categoryId: String, query: String): Flow<List<LegalTopicEntity>> {
        return legalTopicDao.searchTopicsByCategory(categoryId, query.trim())
    }

    /**
     * Seeds the database with official Khyber Pakhtunkhwa legal categories and topics
     * if the tables are empty.
     */
    suspend fun seedDatabaseIfEmpty() = withContext(ioDispatcher) {
        val categoryCount = legalTopicDao.getCategoryCount()
        if (categoryCount == 0) {
            legalTopicDao.insertCategories(KpLegalSeedData.defaultCategories)
        }
        val topicCount = legalTopicDao.getTopicCount()
        if (topicCount == 0) {
            legalTopicDao.insertTopics(KpLegalSeedData.defaultTopics)
        }
    }
}
