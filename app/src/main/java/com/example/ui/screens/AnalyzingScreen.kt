package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Gavel
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LanguageMode
import com.example.ui.theme.*
import com.example.ui.viewmodel.AnalyzingStage

@Composable
fun AnalyzingScreen(
    language: LanguageMode,
    stages: List<AnalyzingStage>
) {
    val isUrdu = language == LanguageMode.URDU

    val infiniteTransition = rememberInfiniteTransition(label = "pulse")
    val pulseAlpha by infiniteTransition.animateFloat(
        initialValue = 0.4f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(800, easing = LinearEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulseAlpha"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Box(
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape)
                .background(JudicialNavy),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Icons.Default.Gavel,
                contentDescription = null,
                tint = AntiqueGold,
                modifier = Modifier.size(36.dp)
            )
        }

        Spacer(modifier = Modifier.height(20.dp))

        Text(
            text = if (isUrdu) "قانونی تحقیق اور جانچ جاری ہے" else "Legal Research in Progress",
            style = MaterialTheme.typography.titleLarge.copy(
                fontWeight = FontWeight.Bold,
                color = JudicialNavy
            )
        )

        Spacer(modifier = Modifier.height(6.dp))

        Text(
            text = if (isUrdu)
                "خیبر پختونخوا کوڈ اور چترال کی مجاز اتھارٹیز کے مصدقہ ریکارڈ کی جانچ کی جا رہی ہے۔"
            else
                "Cross-referencing KP Code, District Chitral authorities, and statutory evidence...",
            style = MaterialTheme.typography.bodySmall.copy(
                color = CharcoalMuted
            )
        )

        Spacer(modifier = Modifier.height(32.dp))

        // Stages List
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderStone),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                stages.forEachIndexed { index, stage ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 10.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        // Status indicator
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(
                                    when {
                                        stage.isCompleted -> VerifiedGreen
                                        stage.isCurrent -> AntiqueGold.copy(alpha = pulseAlpha)
                                        else -> BorderStone
                                    }
                                ),
                            contentAlignment = Alignment.Center
                        ) {
                            if (stage.isCompleted) {
                                Icon(
                                    imageVector = Icons.Default.Check,
                                    contentDescription = "Completed",
                                    tint = WarmIvory,
                                    modifier = Modifier.size(16.dp)
                                )
                            } else if (stage.isCurrent) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    color = JudicialNavyDark,
                                    strokeWidth = 2.dp
                                )
                            } else {
                                Text(
                                    text = "${index + 1}",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = CharcoalMuted,
                                        fontSize = 10.sp
                                    )
                                )
                            }
                        }

                        Spacer(modifier = Modifier.width(14.dp))

                        Text(
                            text = if (isUrdu) stage.titleUr else stage.title,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (stage.isCurrent) FontWeight.Bold else FontWeight.Normal,
                                color = if (stage.isCompleted || stage.isCurrent) CharcoalText else CharcoalMuted.copy(alpha = 0.6f)
                            )
                        )
                    }
                    if (index < stages.size - 1) {
                        Divider(
                            color = BorderStone.copy(alpha = 0.5f),
                            thickness = 1.dp,
                            modifier = Modifier.padding(start = 42.dp)
                        )
                    }
                }
            }
        }
    }
}
