package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LanguageMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun JudicialTopAppBar(
    title: String,
    subtitle: String? = null,
    showBackButton: Boolean = false,
    onBackClick: () -> Unit = {},
    language: LanguageMode,
    isUserLoggedIn: Boolean = false,
    onToggleLanguage: () -> Unit,
    onLegalTopicsClick: () -> Unit,
    onVoiceLiveClick: () -> Unit,
    onChatClick: () -> Unit = {},
    onSourcesClick: () -> Unit,
    onSavedCasesClick: () -> Unit,
    onAccountClick: () -> Unit
) {
    TopAppBar(
        title = {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(
                        imageVector = Icons.Default.Gavel,
                        contentDescription = "Court Icon",
                        tint = AntiqueGold,
                        modifier = Modifier.size(20.dp).padding(end = 4.dp)
                    )
                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = WarmIvory,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
                if (subtitle != null) {
                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = AntiqueGold,
                            fontSize = 11.sp
                        )
                    )
                }
            }
        },
        navigationIcon = {
            if (showBackButton) {
                IconButton(onClick = onBackClick, modifier = Modifier.testTag("app_bar_back_button")) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = WarmIvory
                    )
                }
            }
        },
        actions = {
            // Language Toggle Button (Urdu / English)
            FilledTonalButton(
                onClick = onToggleLanguage,
                colors = ButtonDefaults.filledTonalButtonColors(
                    containerColor = JudicialNavy.copy(alpha = 0.8f),
                    contentColor = AntiqueGold
                ),
                contentPadding = PaddingValues(horizontal = 8.dp, vertical = 2.dp),
                modifier = Modifier.padding(end = 2.dp).testTag("app_bar_language_toggle")
            ) {
                Text(
                    text = if (language == LanguageMode.ENGLISH) "اردو" else "ENG",
                    fontWeight = FontWeight.Bold,
                    fontSize = 12.sp
                )
            }

            // Live Voice Consultation Button (gemini-3.8-live)
            IconButton(onClick = onVoiceLiveClick, modifier = Modifier.testTag("app_bar_voice_live_button")) {
                Icon(
                    imageVector = Icons.Default.Mic,
                    contentDescription = "Live Voice Consultation",
                    tint = LightGold
                )
            }

            // KP Legal Procedures Chat (Firebase AI SDK)
            IconButton(onClick = onChatClick, modifier = Modifier.testTag("app_bar_chat_button")) {
                Icon(
                    imageVector = Icons.Default.Chat,
                    contentDescription = "KP Legal Q&A Chat",
                    tint = LightGold
                )
            }

            // Legal Topics Library Button
            IconButton(onClick = onLegalTopicsClick, modifier = Modifier.testTag("app_bar_topics_button")) {
                Icon(
                    imageVector = Icons.Default.AutoStories,
                    contentDescription = "KP Legal Topics Library",
                    tint = WarmIvory
                )
            }

            // Saved Cases Button
            IconButton(onClick = onSavedCasesClick, modifier = Modifier.testTag("app_bar_saved_cases_button")) {
                Icon(
                    imageVector = Icons.Default.Bookmark,
                    contentDescription = "Saved Cases",
                    tint = WarmIvory
                )
            }

            // Account & Cloud Firestore Sync Button
            IconButton(onClick = onAccountClick, modifier = Modifier.testTag("app_bar_account_button")) {
                Box {
                    Icon(
                        imageVector = if (isUserLoggedIn) Icons.Default.CloudDone else Icons.Default.AccountCircle,
                        contentDescription = "Account & Cloud Sync",
                        tint = if (isUserLoggedIn) EmeraldVerified else WarmIvory
                    )
                    if (isUserLoggedIn) {
                        Box(
                            modifier = Modifier
                                .size(7.dp)
                                .clip(CircleShape)
                                .background(EmeraldVerified)
                                .align(Alignment.TopEnd)
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = JudicialNavy,
            titleContentColor = WarmIvory
        )
    )
}
