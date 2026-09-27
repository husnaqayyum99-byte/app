package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DeleteOutline
import androidx.compose.material.icons.filled.FolderOpen
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.CaseEntity
import com.example.data.models.LanguageMode
import com.example.ui.theme.*
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun CaseHistoryScreen(
    language: LanguageMode,
    cases: List<CaseEntity>,
    onSelectCase: (CaseEntity) -> Unit,
    onDeleteCase: (String) -> Unit
) {
    val isUrdu = language == LanguageMode.URDU
    val dateFormat = SimpleDateFormat("dd MMM yyyy, hh:mm a", Locale.getDefault())

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .padding(horizontal = 16.dp)
    ) {
        Spacer(modifier = Modifier.height(16.dp))
        Text(
            text = if (isUrdu) "محفوظ شدہ قانونی کیسز" else "Saved Legal Cases",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = JudicialNavy
            )
        )
        Text(
            text = if (isUrdu) "آپ کے تمام پچھلے کیسز اور رہنمائی پلان یہاں محفوظ ہیں۔" else "History of your previous legal inquiries and plans.",
            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
        )

        Spacer(modifier = Modifier.height(16.dp))

        if (cases.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(32.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Box(
                        modifier = Modifier
                            .size(64.dp)
                            .clip(CircleShape)
                            .background(BorderStone.copy(alpha = 0.5f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.FolderOpen,
                            contentDescription = null,
                            tint = CharcoalMuted,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                    Spacer(modifier = Modifier.height(12.dp))
                    Text(
                        text = if (isUrdu) "ابھی تک کوئی کیس محفوظ نہیں کیا گیا" else "No saved cases found",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isUrdu) "جب آپ کوئی نیا کیس شروع کریں گے، تو وہ یہاں محفوظ رہے گا۔" else "When you describe and analyze a legal situation, it will be stored here.",
                        style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted),
                        modifier = Modifier.padding(horizontal = 16.dp)
                    )
                }
            }
        } else {
            LazyColumn(
                verticalArrangement = Arrangement.spacedBy(12.dp),
                contentPadding = PaddingValues(bottom = 32.dp)
            ) {
                items(cases) { case ->
                    Card(
                        shape = RoundedCornerShape(12.dp),
                        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                        border = BorderStroke(1.dp, BorderStone),
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelectCase(case) }
                            .testTag("saved_case_${case.caseId}")
                    ) {
                        Column(modifier = Modifier.padding(14.dp)) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Surface(
                                    shape = RoundedCornerShape(4.dp),
                                    color = JudicialNavy.copy(alpha = 0.1f)
                                ) {
                                    Text(
                                        text = case.caseId,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            fontWeight = FontWeight.Bold,
                                            color = JudicialNavy
                                        ),
                                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                    )
                                }

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    if (case.isBookmarked) {
                                        Icon(
                                            imageVector = Icons.Default.Bookmark,
                                            contentDescription = "Bookmarked",
                                            tint = AntiqueGoldDark,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(6.dp))
                                    }
                                    IconButton(
                                        onClick = { onDeleteCase(case.caseId) },
                                        modifier = Modifier.size(28.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.DeleteOutline,
                                            contentDescription = "Delete",
                                            tint = CharcoalMuted,
                                            modifier = Modifier.size(18.dp)
                                        )
                                    }
                                }
                            }

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = case.originalProblem,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = FontWeight.SemiBold,
                                    color = CharcoalText
                                ),
                                maxLines = 2
                            )

                            Spacer(modifier = Modifier.height(6.dp))
                            Text(
                                text = "${case.category} • ${case.location}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = AntiqueGoldDark,
                                    fontWeight = FontWeight.Bold
                                )
                            )

                            Spacer(modifier = Modifier.height(4.dp))
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(
                                    text = dateFormat.format(Date(case.createdAtTimestamp)),
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CharcoalMuted,
                                        fontSize = 10.sp
                                    )
                                )
                                Text(
                                    text = "${case.stepsCount} steps",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = VerifiedGreen,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}
