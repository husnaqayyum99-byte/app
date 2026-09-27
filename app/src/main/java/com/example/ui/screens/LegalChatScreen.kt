package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.LegalChatMessage
import com.example.data.ai.MessageSender
import com.example.data.models.LanguageMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LegalChatScreen(
    language: LanguageMode,
    messages: List<LegalChatMessage>,
    isLoading: Boolean,
    isRecording: Boolean,
    onBackClick: () -> Unit,
    onSendMessage: (String) -> Unit,
    onStartVoiceInput: () -> Boolean,
    onStopVoiceInput: () -> Unit,
    onClearChat: () -> Unit,
    onCreateCaseFromMessage: (String) -> Unit
) {
    val isUrdu = language == LanguageMode.URDU
    val context = LocalContext.current
    var inputText by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto scroll to bottom when new messages arrive
    LaunchedEffect(messages.size, isLoading) {
        if (messages.isNotEmpty()) {
            listState.animateScrollToItem(messages.size - 1)
        }
    }

    val suggestedQuestions = if (isUrdu) {
        listOf(
            "چترال میں ایف آئی آر درج کروانے کا مکمل قانونی طریقہ کیا ہے؟",
            "کے پی فیملی کورٹ میں خلع اور خرچہ نان نفقہ کا طریقہ کار",
            "لینڈ ریونیو ایکٹ کی دفعہ 135 کے تحت زمین کی تقسیم کیسے کروائیں؟",
            "کے پی پولیس میں تنازعات کے حل کی کونسل (DRC) کیسے کام کرتی ہے؟",
            "سیشن کورٹ میں پیشگی ضمانت (Bail) کی درخواست کیسے دی جاتی ہے؟",
            "تھانے میں بند گاڑی کو چھڑوانے کے لیے سپرداری کی درخواست کیسے دائر کریں؟",
            "کے پی میں رائٹ ٹو انفارمیشن (RTI) کے تحت معلومات کیسے حاصل کریں؟"
        )
    } else {
        listOf(
            "Step-by-step procedure to lodge an FIR in Chitral (CrPC 154)?",
            "Procedure for Khula & maintenance under KP Family Courts Act?",
            "How to apply for land partition under Section 135 Land Revenue Act?",
            "How does the Dispute Resolution Council (DRC) work in KP?",
            "Process of filing pre-arrest bail in the Sessions Court?",
            "Procedure for vehicle release (Superdari under CrPC 516A)?",
            "How to file a Right to Information (RTI) request in KP?"
        )
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .imePadding()
    ) {
        // Screen Sub-Header / Status Banner
        Surface(
            modifier = Modifier.fillMaxWidth(),
            color = JudicialNavy,
            shadowElevation = 2.dp
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 10.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Box(
                        modifier = Modifier
                            .size(36.dp)
                            .clip(CircleShape)
                            .background(LightGold.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.SmartToy,
                            contentDescription = null,
                            tint = LightGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isUrdu) "کے پی قانونی سوال و جواب" else "KP Legal Procedures Q&A",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmPaper
                            )
                        )
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(6.dp)
                                    .clip(CircleShape)
                                    .background(EmeraldVerified)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = if (isUrdu) "فائر بیس اے آئی ایس ڈی کے (Gemini)" else "Firebase AI SDK • Gemini",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = LightGold,
                                    fontSize = 11.sp
                                )
                            )
                        }
                    }
                }

                IconButton(
                    onClick = onClearChat,
                    modifier = Modifier.testTag("clear_legal_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear Chat",
                        tint = WarmPaper
                    )
                }
            }
        }

        // Suggestions Horizontal Carousel
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .background(SurfaceCardElevated)
                .padding(vertical = 8.dp)
        ) {
            Text(
                text = if (isUrdu) "اکثر پوچھے گئے سوالات:" else "Common KP Procedural Questions:",
                style = MaterialTheme.typography.labelSmall.copy(
                    color = TextMuted,
                    fontWeight = FontWeight.SemiBold
                ),
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 2.dp)
            )
            LazyRow(
                contentPadding = PaddingValues(horizontal = 16.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                items(suggestedQuestions) { question ->
                    SuggestionChip(
                        onClick = { onSendMessage(question) },
                        label = {
                            Text(
                                text = question,
                                fontSize = 12.sp,
                                color = JudicialNavy,
                                maxLines = 1
                            )
                        },
                        border = BorderStroke(1.dp, BorderStone),
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = SurfaceCard
                        )
                    )
                }
            }
        }

        // Chat Message History List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .padding(horizontal = 14.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp),
            contentPadding = PaddingValues(vertical = 12.dp)
        ) {
            if (messages.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Card(
                            modifier = Modifier.fillMaxWidth(0.92f),
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = BorderStroke(1.dp, BorderStone)
                        ) {
                            Column(
                                modifier = Modifier.padding(20.dp),
                                horizontalAlignment = Alignment.CenterHorizontally
                            ) {
                                Box(
                                    modifier = Modifier
                                        .size(54.dp)
                                        .clip(CircleShape)
                                        .background(JudicialNavy.copy(alpha = 0.08f)),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Gavel,
                                        contentDescription = null,
                                        tint = JudicialNavy,
                                        modifier = Modifier.size(28.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.height(12.dp))
                                Text(
                                    text = if (isUrdu) "خیبر پختونخوا کے قانونی طریقہ کار کے بارے میں پوچھیں" else "Ask About Any KP Legal Procedure",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = JudicialNavy
                                    ),
                                    textAlign = TextAlign.Center
                                )
                                Spacer(modifier = Modifier.height(6.dp))
                                Text(
                                    text = if (isUrdu)
                                        "تھانہ، کچہری، پٹوار، ریونیو کورٹ، فیملی کورٹ یا ڈی آر سی کے طریقہ کار اور ضروری دستاویزات کے بارے میں رہنمائی حاصل کریں۔"
                                    else
                                        "Get instant, structured guidance on Police Station visits, Court procedures, Land Revenue mutations, DRC arbitration, and Family Court matters.",
                                    style = MaterialTheme.typography.bodySmall.copy(
                                        color = CharcoalMuted,
                                        lineHeight = 18.sp
                                    ),
                                    textAlign = TextAlign.Center
                                )
                            }
                        }
                    }
                }
            }

            items(messages, key = { it.id }) { message ->
                ChatMessageItem(
                    message = message,
                    isUrdu = isUrdu,
                    onCopyText = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        clipboard.setPrimaryClip(ClipData.newPlainText("Legal Answer", message.text))
                        Toast.makeText(context, if (isUrdu) "متن کاپی ہو گیا" else "Copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    onCreateCasePlan = {
                        onCreateCaseFromMessage(message.text)
                    }
                )
            }

            if (isLoading) {
                item {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Start
                    ) {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                            border = BorderStroke(1.dp, BorderStone)
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = JudicialNavy
                                )
                                Spacer(modifier = Modifier.width(10.dp))
                                Text(
                                    text = if (isUrdu) "وکیل قانونی ضابطوں کا جائزہ لے رہا ہے..." else "Analyzing KP legal statutes...",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Input Control Bar
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp),
            border = BorderStroke(1.dp, BorderStone)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 10.dp, vertical = 4.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Voice input toggle
                IconButton(
                    onClick = {
                        if (isRecording) {
                            onStopVoiceInput()
                        } else {
                            onStartVoiceInput()
                        }
                    },
                    modifier = Modifier.testTag("chat_voice_mic_button")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = "Voice Input",
                        tint = if (isRecording) DeepCrimson else JudicialNavy
                    )
                }

                OutlinedTextField(
                    value = inputText,
                    onValueChange = { inputText = it },
                    placeholder = {
                        Text(
                            text = if (isUrdu) "قانونی طریقہ کار کے بارے میں لکھیں..." else "Ask about a KP legal procedure...",
                            style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                        )
                    },
                    modifier = Modifier
                        .weight(1f)
                        .testTag("chat_input_text_field"),
                    maxLines = 3,
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = Color.Transparent,
                        unfocusedBorderColor = Color.Transparent,
                        focusedContainerColor = Color.Transparent,
                        unfocusedContainerColor = Color.Transparent
                    )
                )

                IconButton(
                    onClick = {
                        val text = inputText.trim()
                        if (text.isNotBlank()) {
                            inputText = ""
                            onSendMessage(text)
                        }
                    },
                    enabled = inputText.isNotBlank() && !isLoading,
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(if (inputText.isNotBlank() && !isLoading) JudicialNavy else BorderStone)
                        .testTag("chat_send_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = WarmPaper,
                        modifier = Modifier.size(18.dp)
                    )
                }
            }
        }
    }
}

@Composable
fun ChatMessageItem(
    message: LegalChatMessage,
    isUrdu: Boolean,
    onCopyText: () -> Unit,
    onCreateCasePlan: () -> Unit
) {
    val isUser = message.sender == MessageSender.USER

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
    ) {
        Column(
            modifier = Modifier.widthIn(max = 330.dp),
            horizontalAlignment = if (isUser) Alignment.End else Alignment.Start
        ) {
            Card(
                shape = RoundedCornerShape(
                    topStart = 16.dp,
                    topEnd = 16.dp,
                    bottomStart = if (isUser) 16.dp else 4.dp,
                    bottomEnd = if (isUser) 4.dp else 16.dp
                ),
                colors = CardDefaults.cardColors(
                    containerColor = if (isUser) JudicialNavy else SurfaceCard
                ),
                border = if (isUser) null else BorderStroke(1.dp, BorderStone),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
            ) {
                Column(modifier = Modifier.padding(14.dp)) {
                    if (!isUser) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.padding(bottom = 6.dp)
                        ) {
                            Icon(
                                imageVector = Icons.Default.Gavel,
                                contentDescription = null,
                                tint = AntiqueGold,
                                modifier = Modifier.size(15.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isUrdu) "اپنا وکیل قانونی مشیر (Firebase AI)" else "Apna Wakeel Legal Counsel",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JudicialNavy
                                )
                            )
                        }
                    }

                    Text(
                        text = message.text,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = if (isUser) WarmPaper else JudicialNavyDark,
                            lineHeight = 22.sp
                        )
                    )
                }
            }

            // AI Action Buttons below bot message
            if (!isUser) {
                Row(
                    modifier = Modifier.padding(top = 4.dp, start = 4.dp),
                    horizontalArrangement = Arrangement.spacedBy(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = onCopyText,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(13.dp), tint = TextMuted)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isUrdu) "کاپی" else "Copy", fontSize = 11.sp, color = TextMuted)
                    }

                    TextButton(
                        onClick = onCreateCasePlan,
                        contentPadding = PaddingValues(horizontal = 6.dp, vertical = 2.dp)
                    ) {
                        Icon(imageVector = Icons.Default.AltRoute, contentDescription = null, modifier = Modifier.size(13.dp), tint = AntiqueGoldDark)
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(text = if (isUrdu) "کیس پلان بنائیں" else "Create Plan", fontSize = 11.sp, color = AntiqueGoldDark, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }
}
