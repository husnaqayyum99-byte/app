package com.example

import android.content.Context
import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import com.example.data.local.AppDatabase
import com.example.data.local.KpLegalSeedData
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith
import org.robolectric.RobolectricTestRunner
import org.robolectric.annotation.Config

@RunWith(RobolectricTestRunner::class)
@Config(sdk = [36])
class ExampleRobolectricTest {

    private lateinit var db: AppDatabase

    @Before
    fun setup() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        db = Room.inMemoryDatabaseBuilder(context, AppDatabase::class.java)
            .allowMainThreadQueries()
            .build()
    }

    @After
    fun tearDown() {
        db.close()
    }

    @Test
    fun `read string from context`() {
        val context = ApplicationProvider.getApplicationContext<Context>()
        val appName = context.getString(R.string.app_name)
        assertEquals("Apna Wakeel", appName)
    }

    @Test
    fun `room database stores and retrieves KP legal categories and topics`() = runBlocking {
        val dao = db.legalTopicDao()

        // Insert categories and topics
        dao.insertCategories(KpLegalSeedData.defaultCategories)
        dao.insertTopics(KpLegalSeedData.defaultTopics)

        // Verify categories
        val categories = dao.getAllCategories().first()
        assertTrue(categories.isNotEmpty())
        assertTrue(categories.any { it.categoryId == "property_law" })
        assertTrue(categories.any { it.categoryId == "family_law" })
        assertTrue(categories.any { it.categoryId == "criminal_procedure" })

        // Verify topics by category
        val propertyTopics = dao.getTopicsByCategory("property_law").first()
        assertTrue(propertyTopics.isNotEmpty())
        assertTrue(propertyTopics.any { it.relevantSections.contains("117") })

        // Verify search
        val searchResults = dao.searchTopics("Khula").first()
        assertTrue(searchResults.isNotEmpty())
        assertEquals("fam_khula_dissolution", searchResults.first().topicId)

        // Verify search topics by category
        val categorySearchResults = dao.searchTopicsByCategory("property_law", "Partition").first()
        assertTrue(categorySearchResults.isNotEmpty())
        assertEquals("prop_partition_135", categorySearchResults.first().topicId)

        // Verify retrieve all topics
        val allTopics = dao.getAllTopics().first()
        assertTrue(allTopics.size >= KpLegalSeedData.defaultTopics.size)

        // Verify update topic
        val topicToUpdate = allTopics.first()
        val updatedTopic = topicToUpdate.copy(summaryEn = "Updated summary for legal testing")
        dao.updateTopic(updatedTopic)
        val fetchedUpdated = dao.getTopicByIdSync(topicToUpdate.topicId)
        assertNotNull(fetchedUpdated)
        assertEquals("Updated summary for legal testing", fetchedUpdated?.summaryEn)

        // Verify bookmarking
        val topic = searchResults.first()
        dao.updateTopicBookmark(topic.topicId, true)
        val bookmarked = dao.getBookmarkedTopics().first()
        assertEquals(1, bookmarked.size)
        assertEquals(topic.topicId, bookmarked.first().topicId)

        // Verify delete topic
        dao.deleteTopicById(topicToUpdate.topicId)
        val deletedCheck = dao.getTopicByIdSync(topicToUpdate.topicId)
        assertEquals(null, deletedCheck)
    }
}
