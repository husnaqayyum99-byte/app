package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.LegalCategoryEntity
import com.example.data.local.LegalTopicEntity
import com.example.data.models.LanguageMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalTopicsScreen(
    language: LanguageMode,
    categories: List<LegalCategoryEntity>,
    topics: List<LegalTopicEntity>,
    selectedCategoryId: String?,
    searchQuery: String,
    selectedTopicDetail: LegalTopicEntity?,
    showBookmarkedOnly: Boolean,
    onCategorySelected: (String?) -> Unit,
    onSearchQueryChanged: (String) -> Unit,
    onTopicSelected: (LegalTopicEntity?) -> Unit,
    onToggleBookmark: (LegalTopicEntity) -> Unit,
    onToggleBookmarkedOnly: () -> Unit
) {
    val isUrdu = language == LanguageMode.URDU

    // Filter topics based on category, search, and bookmark
    val filteredTopics = topics.filter { topic ->
        val matchesCategory = selectedCategoryId == null || topic.categoryId == selectedCategoryId
        val matchesBookmark = !showBookmarkedOnly || topic.isBookmarked
        val matchesSearch = searchQuery.isBlank() ||
                topic.titleEn.contains(searchQuery, ignoreCase = true) ||
                topic.titleUr.contains(searchQuery, ignoreCase = true) ||
                topic.statuteName.contains(searchQuery, ignoreCase = true) ||
                topic.relevantSections.contains(searchQuery, ignoreCase = true) ||
                topic.summaryEn.contains(searchQuery, ignoreCase = true) ||
                topic.summaryUr.contains(searchQuery, ignoreCase = true) ||
                topic.responsibleAuthorityEn.contains(searchQuery, ignoreCase = true)

        matchesCategory && matchesBookmark && matchesSearch
    }

    // Detail Modal Sheet when a topic is selected
    if (selectedTopicDetail != null) {
        ModalBottomSheet(
            onDismissRequest = { onTopicSelected(null) },
            containerColor = SurfaceCard,
            dragHandle = { BottomSheetDefaults.DragHandle(color = BorderStone) }
        ) {
            TopicDetailContent(
                topic = selectedTopicDetail,
                isUrdu = isUrdu,
                onToggleBookmark = { onToggleBookmark(selectedTopicDetail) },
                onClose = { onTopicSelected(null) }
            )
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        // Screen Header
        item {
            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "خیبر پختونخوا کے قانونی موضوعات" else "Khyber Pakhtunkhwa Legal Topics",
                        style = MaterialTheme.typography.titleLarge.copy(
                            fontWeight = FontWeight.Bold,
                            color = JudicialNavy
                        )
                    )
                    IconButton(onClick = onToggleBookmarkedOnly) {
                        Icon(
                            imageVector = if (showBookmarkedOnly) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Show Bookmarked",
                            tint = if (showBookmarkedOnly) AntiqueGoldDark else CharcoalMuted
                        )
                    }
                }
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (isUrdu)
                        "اراضی و ریونیو، خاندانی قوانین اور ضابطہ فوجداری کے مصدقہ دفعات اور طریقہ کار۔"
                    else
                        "Statutory guides on Property Law, Family Law, Criminal Procedure, and Police powers in KP.",
                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                )
            }
        }

        // Search Input Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = onSearchQueryChanged,
                placeholder = {
                    Text(
                        text = if (isUrdu) "قانون، دفعہ یا موضوع تلاش کریں..." else "Search statute, section, or legal topic...",
                        fontSize = 13.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = CharcoalMuted
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = { onSearchQueryChanged("") }) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Clear",
                                tint = CharcoalMuted
                            )
                        }
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_topics_field"),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = BorderStone,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                )
            )
        }

        // Category Filter Chips
        item {
            LazyRow(
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                contentPadding = PaddingValues(vertical = 2.dp)
            ) {
                item {
                    val isAllSelected = selectedCategoryId == null
                    FilterChip(
                        selected = isAllSelected,
                        onClick = { onCategorySelected(null) },
                        label = {
                            Text(
                                text = if (isUrdu) "تمام قوانین" else "All Topics (${topics.size})",
                                fontSize = 12.sp,
                                fontWeight = if (isAllSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JudicialNavy,
                            selectedLabelColor = WarmIvory,
                            containerColor = SurfaceCard,
                            labelColor = CharcoalText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isAllSelected,
                            borderColor = if (isAllSelected) AntiqueGold else BorderStone
                        )
                    )
                }

                items(categories) { cat ->
                    val isSelected = selectedCategoryId == cat.categoryId
                    val topicCountInCat = topics.count { it.categoryId == cat.categoryId }
                    FilterChip(
                        selected = isSelected,
                        onClick = { onCategorySelected(cat.categoryId) },
                        label = {
                            Text(
                                text = if (isUrdu) cat.nameUr else "${cat.nameEn} ($topicCountInCat)",
                                fontSize = 12.sp,
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = JudicialNavy,
                            selectedLabelColor = WarmIvory,
                            containerColor = SurfaceCard,
                            labelColor = CharcoalText
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = true,
                            selected = isSelected,
                            borderColor = if (isSelected) AntiqueGold else BorderStone
                        )
                    )
                }
            }
        }

        // Filter Summary
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUrdu)
                        "دستیاب قانونی موضوعات (${filteredTopics.size})"
                    else
                        "Available Legal Topics (${filteredTopics.size})",
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CharcoalMuted
                    )
                )
                if (showBookmarkedOnly) {
                    Surface(
                        shape = RoundedCornerShape(4.dp),
                        color = AntiqueGold.copy(alpha = 0.2f)
                    ) {
                        Text(
                            text = if (isUrdu) "صرف بک مارکس" else "Bookmarked Only",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JudicialNavyDark,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold
                            ),
                            modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                        )
                    }
                }
            }
        }

        // Topics List
        if (filteredTopics.isEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderStone),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.SearchOff,
                                contentDescription = null,
                                tint = CharcoalMuted,
                                modifier = Modifier.size(36.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isUrdu) "کوئی موضوع نہیں ملا" else "No matching legal topics found",
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = CharcoalText
                                )
                            )
                        }
                    }
                }
            }
        } else {
            items(filteredTopics) { topic ->
                LegalTopicCard(
                    topic = topic,
                    isUrdu = isUrdu,
                    onClick = { onTopicSelected(topic) },
                    onToggleBookmark = { onToggleBookmark(topic) }
                )
            }
        }
    }
}

@Composable
private fun LegalTopicCard(
    topic: LegalTopicEntity,
    isUrdu: Boolean,
    onClick: () -> Unit,
    onToggleBookmark: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderStone),
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick)
            .testTag("topic_card_${topic.topicId}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.Top
            ) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = if (isUrdu) topic.titleUr else topic.titleEn,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = JudicialNavy
                        )
                    )
                    Spacer(modifier = Modifier.height(3.dp))
                    Text(
                        text = if (isUrdu) topic.statuteNameUr else topic.statuteName,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AntiqueGoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )
                }

                IconButton(
                    onClick = onToggleBookmark,
                    modifier = Modifier.size(32.dp)
                ) {
                    Icon(
                        imageVector = if (topic.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                        contentDescription = "Bookmark",
                        tint = if (topic.isBookmarked) AntiqueGoldDark else CharcoalMuted,
                        modifier = Modifier.size(20.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.height(6.dp))

            // Section Pill
            Surface(
                shape = RoundedCornerShape(4.dp),
                color = JudicialNavy.copy(alpha = 0.08f)
            ) {
                Text(
                    text = topic.relevantSections,
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JudicialNavy,
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            Text(
                text = if (isUrdu) topic.summaryUr else topic.summaryEn,
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CharcoalText,
                    lineHeight = 18.sp
                ),
                maxLines = 3
            )

            Spacer(modifier = Modifier.height(10.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.AccountBalance,
                        contentDescription = null,
                        tint = CharcoalMuted,
                        modifier = Modifier.size(14.dp)
                    )
                    Spacer(modifier = Modifier.width(4.dp))
                    Text(
                        text = if (isUrdu) topic.responsibleAuthorityUr else topic.responsibleAuthorityEn,
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = CharcoalMuted,
                            fontSize = 11.sp
                        ),
                        maxLines = 1
                    )
                }

                Text(
                    text = if (isUrdu) "تفصیلات دیکھیں →" else "View Details →",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = AntiqueGoldDark,
                        fontWeight = FontWeight.Bold
                    )
                )
            }
        }
    }
}

@Composable
private fun TopicDetailContent(
    topic: LegalTopicEntity,
    isUrdu: Boolean,
    onToggleBookmark: () -> Unit,
    onClose: () -> Unit
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 20.dp),
        contentPadding = PaddingValues(bottom = 36.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = JudicialNavy.copy(alpha = 0.1f)
                ) {
                    Text(
                        text = topic.relevantSections,
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = JudicialNavy
                        ),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    IconButton(onClick = onToggleBookmark) {
                        Icon(
                            imageVector = if (topic.isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                            contentDescription = "Bookmark",
                            tint = if (topic.isBookmarked) AntiqueGoldDark else CharcoalMuted
                        )
                    }
                    IconButton(onClick = onClose) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = CharcoalMuted
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isUrdu) topic.titleUr else topic.titleEn,
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = JudicialNavy
                )
            )
            Spacer(modifier = Modifier.height(2.dp))
            Text(
                text = if (isUrdu) topic.statuteNameUr else topic.statuteName,
                style = MaterialTheme.typography.bodyMedium.copy(
                    color = AntiqueGoldDark,
                    fontWeight = FontWeight.SemiBold
                )
            )
        }

        // Summary
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = WarmIvory),
                border = BorderStroke(1.dp, BorderStone)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Text(
                        text = if (isUrdu) "خلاصہ و قانونی اہمیت:" else "Summary & Legal Scope:",
                        style = MaterialTheme.typography.labelMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isUrdu) topic.summaryUr else topic.summaryEn,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = CharcoalText,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }

        // Responsible Authority
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = AntiqueGoldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUrdu) "مجاز فورم یا ادارہ:" else "Competent Forum / Authority:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = JudicialNavy
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = if (isUrdu) topic.responsibleAuthorityUr else topic.responsibleAuthorityEn,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalText
                        )
                    )
                    Spacer(modifier = Modifier.height(2.dp))
                    Text(
                        text = topic.jurisdiction,
                        style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                    )
                }
            }
        }

        // Required Documents Checklist
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Description,
                            contentDescription = null,
                            tint = VerifiedGreen,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUrdu) "درکار سرکاری کاغذات:" else "Required Documents Checklist:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isUrdu) topic.requiredDocumentsUr else topic.requiredDocumentsEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalText,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }

        // Procedural Steps
        item {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.FormatListNumbered,
                            contentDescription = null,
                            tint = AntiqueGoldDark,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = if (isUrdu) "مرحلہ وار قانونی طریقہ کار:" else "Step-by-Step Procedure:",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = JudicialNavy
                            )
                        )
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = if (isUrdu) topic.proceduralStepsUr else topic.proceduralStepsEn,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalText,
                            lineHeight = 20.sp
                        )
                    )
                }
            }
        }

        // Statutory Link
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Link,
                    contentDescription = null,
                    tint = CharcoalMuted,
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = "${if (isUrdu) "سرکاری ماخذ: " else "Official Source: "}${topic.officialSourceUrl}",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JudicialNavyLight,
                        fontSize = 11.sp
                    )
                )
            }
        }
    }
}
