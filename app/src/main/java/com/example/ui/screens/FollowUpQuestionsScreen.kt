package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.HelpOutline
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.FollowUpQuestion
import com.example.data.models.LanguageMode
import com.example.ui.theme.*

@Composable
fun FollowUpQuestionsScreen(
    language: LanguageMode,
    questions: List<FollowUpQuestion>,
    currentIndex: Int,
    userAnswers: Map<String, String>,
    onAnswerSelected: (String, String) -> Unit,
    onBackClick: () -> Unit
) {
    val isUrdu = language == LanguageMode.URDU
    val currentQuestion = questions.getOrNull(currentIndex) ?: return
    val totalQuestions = questions.size
    val selectedOptionId = userAnswers[currentQuestion.id]

    val progress = (currentIndex + 1).toFloat() / totalQuestions.toFloat()
    val scrollState = rememberScrollState()

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        // Step progress header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(
                text = if (isUrdu) "معاملے کی تفہیم" else "Understanding Your Case",
                style = MaterialTheme.typography.labelLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = JudicialNavy
                )
            )
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = AntiqueGold.copy(alpha = 0.2f)
            ) {
                Text(
                    text = if (isUrdu)
                        "مرحلہ ${currentIndex + 1} از $totalQuestions"
                    else
                        "Step ${currentIndex + 1} of $totalQuestions",
                    style = MaterialTheme.typography.labelSmall.copy(
                        color = JudicialNavyDark,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))
        LinearProgressIndicator(
            progress = { progress },
            modifier = Modifier
                .fillMaxWidth()
                .height(6.dp),
            color = AntiqueGold,
            trackColor = BorderStone
        )

        Spacer(modifier = Modifier.height(20.dp))

        // Question Card
        Card(
            shape = RoundedCornerShape(14.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderStone),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(18.dp)) {
                Row(verticalAlignment = Alignment.Top) {
                    Icon(
                        imageVector = Icons.Default.HelpOutline,
                        contentDescription = null,
                        tint = AntiqueGoldDark,
                        modifier = Modifier.size(24.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isUrdu) currentQuestion.questionUr else currentQuestion.questionEn,
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText,
                                lineHeight = 24.sp
                            )
                        )
                        val hint = if (isUrdu) currentQuestion.hintUr else currentQuestion.hintEn
                        if (hint != null) {
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = hint,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CharcoalMuted,
                                    fontSize = 12.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(18.dp))

        Text(
            text = if (isUrdu) "مندرجہ ذیل میں سے ایک آپشن منتخب کریں:" else "Select the option that best matches:",
            style = MaterialTheme.typography.labelMedium.copy(color = CharcoalMuted)
        )

        Spacer(modifier = Modifier.height(10.dp))

        // Option Cards
        currentQuestion.options.forEach { option ->
            val isSelected = selectedOptionId == option.id
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(
                    containerColor = if (isSelected) JudicialNavy.copy(alpha = 0.05f) else SurfaceCard
                ),
                border = BorderStroke(
                    width = if (isSelected) 2.dp else 1.dp,
                    color = if (isSelected) AntiqueGoldDark else BorderStone
                ),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 5.dp)
                    .clickable { onAnswerSelected(currentQuestion.id, option.id) }
                    .testTag("option_${option.id}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    RadioButton(
                        selected = isSelected,
                        onClick = { onAnswerSelected(currentQuestion.id, option.id) },
                        colors = RadioButtonDefaults.colors(
                            selectedColor = AntiqueGoldDark,
                            unselectedColor = CharcoalMuted
                        )
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isUrdu) option.labelUr else option.labelEn,
                            style = MaterialTheme.typography.bodyMedium.copy(
                                fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                color = CharcoalText
                            )
                        )
                    }
                    if (isSelected) {
                        Icon(
                            imageVector = Icons.Default.CheckCircle,
                            contentDescription = "Selected",
                            tint = AntiqueGoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Navigation Controls
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            OutlinedButton(
                onClick = onBackClick,
                shape = RoundedCornerShape(8.dp),
                border = BorderStroke(1.dp, BorderStone),
                colors = ButtonDefaults.outlinedButtonColors(contentColor = CharcoalText)
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    modifier = Modifier.size(16.dp)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(text = if (isUrdu) "پیچھے" else "Back")
            }

            if (selectedOptionId != null) {
                Button(
                    onClick = { onAnswerSelected(currentQuestion.id, selectedOptionId) },
                    shape = RoundedCornerShape(8.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JudicialNavy,
                        contentColor = WarmIvory
                    )
                ) {
                    Text(
                        text = if (currentIndex + 1 < totalQuestions) {
                            if (isUrdu) "اگلا سوال  ←" else "Next Question  →"
                        } else {
                            if (isUrdu) "قانونی تجزیہ شروع کریں" else "Analyze Case"
                        }
                    )
                }
            }
        }
    }
}
