package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Verified
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LanguageMode
import com.example.data.sources.LegalSourcesRegistry
import com.example.ui.theme.*

@Composable
fun SourcesDirectoryScreen(
    language: LanguageMode
) {
    val isUrdu = language == LanguageMode.URDU
    var searchQuery by remember { mutableStateOf("") }
    var selectedFilter by remember { mutableStateOf("ALL") }

    val filterOptions = listOf(
        "ALL" to if (isUrdu) "تمام سرکاری ذرائع" else "All Sources",
        "KP" to if (isUrdu) "کے پی حکومت" else "KP Province",
        "CHITRAL" to if (isUrdu) "چترال انتظامیہ" else "Chitral Authorities",
        "FEDERAL" to if (isUrdu) "وفاقی ادارے" else "Federal Authorities"
    )

    val allSources = LegalSourcesRegistry.ALL_SOURCES

    val filtered = allSources.filter { source ->
        val matchesQuery = searchQuery.isBlank() ||
                source.title.contains(searchQuery, ignoreCase = true) ||
                source.authority.contains(searchQuery, ignoreCase = true) ||
                source.description.contains(searchQuery, ignoreCase = true)

        val matchesFilter = when (selectedFilter) {
            "KP" -> source.jurisdiction.contains("Khyber", ignoreCase = true)
            "CHITRAL" -> source.jurisdiction.contains("Chitral", ignoreCase = true)
            "FEDERAL" -> source.jurisdiction.contains("Federal", ignoreCase = true)
            else -> true
        }

        matchesQuery && matchesFilter
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(14.dp)
    ) {
        item {
            Text(
                text = if (isUrdu) "مستند سرکاری قوانین و اداروں کی ڈائریکٹری" else "Official Statutory Sources Registry",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = JudicialNavy
                )
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = if (isUrdu)
                    "اپنا وکیل صرف انہی مصدقہ سرکاری و عدالتی پورٹلز سے قانونی معلومات حاصل کرتا ہے۔"
                else
                    "Apna Wakeel exclusively references verified statutory repositories, high courts, and official government authorities.",
                style = MaterialTheme.typography.bodySmall.copy(
                    color = CharcoalMuted
                )
            )
        }

        // Search Bar
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (isUrdu) "قانون یا ادارہ تلاش کریں..." else "Search statute, department, or jurisdiction...",
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
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(10.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = BorderStone,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                )
            )
        }

        // Filter Chips
        item {
            LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                items(filterOptions) { (key, label) ->
                    val isSelected = selectedFilter == key
                    FilterChip(
                        selected = isSelected,
                        onClick = { selectedFilter = key },
                        label = { Text(label, fontSize = 12.sp) },
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

        // Sources List
        items(filtered) { source ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isUrdu && source.titleUr.isNotEmpty()) source.titleUr else source.title,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = JudicialNavy
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = VerifiedGreenLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = VerifiedGreen,
                                    modifier = Modifier.size(12.dp)
                                )
                                Spacer(modifier = Modifier.width(3.dp))
                                Text(
                                    text = "Verified",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = VerifiedGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${source.authority} • ${source.jurisdiction}",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = AntiqueGoldDark,
                            fontWeight = FontWeight.SemiBold
                        )
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = source.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalText,
                            lineHeight = 18.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(10.dp))
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = null,
                            tint = CharcoalMuted,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = source.officialUrl,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = JudicialNavyLight,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }
            }
        }
    }
}
