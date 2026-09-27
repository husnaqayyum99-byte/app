package com.example.ui.screens

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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.ai.LiveConnectionState
import com.example.data.ai.LiveTurn
import com.example.data.models.LanguageMode
import com.example.ui.theme.*

@Composable
fun VoiceConversationScreen(
    language: LanguageMode,
    liveState: LiveConnectionState,
    liveTurns: List<LiveTurn>,
    liveStatusMessage: String?,
    isTtsEnabled: Boolean,
    isRecording: Boolean,
    recordingDurationSeconds: Int,
    audioAmplitude: Float,
    isTranscribing: Boolean,
    onBackClick: () -> Unit,
    onStartRecording: () -> Boolean,
    onStopRecordingAndSend: () -> Unit,
    onCancelRecording: () -> Unit,
    onSendTextMessage: (String) -> Unit,
    onToggleTts: () -> Unit,
    onClearConversation: () -> Unit,
    onCreateCaseFromConversation: () -> Unit
) {
    val isUrdu = language == LanguageMode.URDU
    var textInput by remember { mutableStateOf("") }
    val listState = rememberLazyListState()

    // Auto-scroll to bottom on new turns
    LaunchedEffect(liveTurns.size) {
        if (liveTurns.isNotEmpty()) {
            listState.animateScrollToItem(liveTurns.size - 1)
        }
    }

    // Pulsing animation for microphone and live visualizer
    val infiniteTransition = rememberInfiniteTransition(label = "voice_orb")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording || liveState == LiveConnectionState.SPEAKING) 1.25f else 1.05f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 800, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .padding(horizontal = 16.dp)
    ) {
        // Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 12.dp),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Row(verticalAlignment = Alignment.CenterVertically) {
                IconButton(
                    onClick = onBackClick,
                    modifier = Modifier.testTag("voice_back_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                        contentDescription = "Back",
                        tint = JudicialNavy
                    )
                }
                Spacer(modifier = Modifier.width(6.dp))
                Column {
                    Text(
                        text = if (isUrdu) "وکیل لائیو آواز" else "Apna Wakeel Live Voice",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = JudicialNavy
                        )
                    )
                    Text(
                        text = if (isUrdu) "ماڈل: gemini-3.8-live لائیو رہنمائی" else "Powered by model: gemini-3.8-live",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                }
            }

            Row {
                IconButton(
                    onClick = onToggleTts,
                    modifier = Modifier.testTag("toggle_tts_button")
                ) {
                    Icon(
                        imageVector = if (isTtsEnabled) Icons.Default.VolumeUp else Icons.Default.VolumeOff,
                        contentDescription = "Toggle TTS",
                        tint = if (isTtsEnabled) AntiqueGold else TextMuted
                    )
                }
                IconButton(
                    onClick = onClearConversation,
                    modifier = Modifier.testTag("clear_voice_chat_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = "Clear Chat",
                        tint = TextMuted
                    )
                }
            }
        }

        // Live Voice Orb / Visualizer Banner
        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(16.dp),
            colors = CardDefaults.cardColors(containerColor = JudicialNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 3.dp)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(68.dp)
                        .scale(pulseScale)
                        .clip(CircleShape)
                        .background(
                            Brush.radialGradient(
                                colors = listOf(
                                    AntiqueGold,
                                    if (isRecording) DeepCrimson else LightGold,
                                    JudicialNavyDark
                                )
                            )
                        ),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Mic else if (liveState == LiveConnectionState.SPEAKING) Icons.Default.GraphicEq else Icons.Default.Hearing,
                        contentDescription = "Voice State",
                        tint = WarmPaper,
                        modifier = Modifier.size(34.dp)
                    )
                }

                Spacer(modifier = Modifier.height(10.dp))

                Text(
                    text = when {
                        isRecording -> if (isUrdu) "سن رہا ہے... ($recordingDurationSeconds سیکنڈ)" else "Listening... (${recordingDurationSeconds}s)"
                        isTranscribing -> if (isUrdu) "ٹرانسکرائب ہو رہا ہے (gemini-3.5-transcribe)..." else "Transcribing with gemini-3.5-transcribe..."
                        liveState == LiveConnectionState.PROCESSING -> if (isUrdu) "ماڈل gemini-3.8-live سے رابطہ جاری ہے..." else "Consulting gemini-3.8-live..."
                        liveState == LiveConnectionState.SPEAKING -> if (isUrdu) "وکیل بول رہا ہے..." else "Wakeel is responding..."
                        else -> liveStatusMessage ?: if (isUrdu) "آواز میں اپنا قانونی سوال پوچھیں" else "Ask your legal question with your voice"
                    },
                    style = MaterialTheme.typography.bodyMedium.copy(
                        color = WarmPaper,
                        fontWeight = FontWeight.Medium
                    ),
                    textAlign = TextAlign.Center
                )
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Quick Topic Suggestions Chips
        val suggestions = if (isUrdu) {
            listOf(
                "چترال میں ایف آئی آر کیسے درج کروائیں؟",
                "کے پی فیملی کورٹ میں خلع کا طریقہ",
                "زمین کی تقسیم (دفعہ 135 لینڈ ریونیو ایکٹ)",
                "سیشن کورٹ میں پیشگی ضمانت کا عمل"
            )
        } else {
            listOf(
                "How to lodge an FIR in Chitral?",
                "Khula dissolution in KP Family Court",
                "Land partition under Section 135",
                "Pre-arrest bail procedure in Session Court"
            )
        }

        LazyRow(
            horizontalArrangement = Arrangement.spacedBy(8.dp),
            contentPadding = PaddingValues(vertical = 4.dp)
        ) {
            items(suggestions) { prompt ->
                SuggestionChip(
                    onClick = { onSendTextMessage(prompt) },
                    label = { Text(prompt, fontSize = 12.sp, color = JudicialNavy) },
                    border = BorderStroke(1.dp, BorderStone)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Conversation Turns List
        LazyColumn(
            state = listState,
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp),
            contentPadding = PaddingValues(bottom = 12.dp)
        ) {
            if (liveTurns.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 32.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            Icon(
                                imageVector = Icons.Default.RecordVoiceOver,
                                contentDescription = null,
                                tint = BorderStone,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(8.dp))
                            Text(
                                text = if (isUrdu) "گفتگو شروع کرنے کے لیے مائیک دبائیں" else "Tap the microphone below to start talking",
                                style = MaterialTheme.typography.bodyMedium.copy(color = TextMuted)
                            )
                        }
                    }
                }
            }

            items(liveTurns) { turn ->
                val isUser = turn.role == "user"
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        modifier = Modifier.widthIn(max = 300.dp),
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
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(
                                text = if (isUser) (if (isUrdu) "آپ" else "You") else (if (isUrdu) "اپنا وکیل لائیو" else "Apna Wakeel Live"),
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = if (isUser) LightGold else JudicialNavy
                                )
                            )
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = turn.text,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    color = if (isUser) WarmPaper else JudicialNavyDark,
                                    lineHeight = 20.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Action: Create Case Plan from conversation
        if (liveTurns.isNotEmpty()) {
            OutlinedButton(
                onClick = onCreateCaseFromConversation,
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 4.dp)
                    .testTag("create_case_from_voice_button"),
                border = BorderStroke(1.dp, AntiqueGold),
                shape = RoundedCornerShape(8.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.Description,
                    contentDescription = null,
                    tint = AntiqueGold,
                    modifier = Modifier.size(18.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isUrdu) "اس گفتگو کو باضابطہ کیس میں تبدیل کریں" else "Create Formal Case Plan From Dialogue",
                    color = JudicialNavy,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }

        // Bottom Controls: Mic Recording and Text input fallback
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 8.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = textInput,
                onValueChange = { textInput = it },
                modifier = Modifier
                    .weight(1f)
                    .testTag("voice_text_input"),
                placeholder = {
                    Text(
                        text = if (isUrdu) "یا یہاں قانونی سوال لکھیں..." else "Or type question here...",
                        style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                    )
                },
                shape = RoundedCornerShape(24.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = AntiqueGold,
                    unfocusedBorderColor = BorderStone,
                    focusedContainerColor = SurfaceCard,
                    unfocusedContainerColor = SurfaceCard
                ),
                maxLines = 2
            )

            Spacer(modifier = Modifier.width(8.dp))

            if (textInput.isNotBlank()) {
                IconButton(
                    onClick = {
                        val text = textInput.trim()
                        textInput = ""
                        onSendTextMessage(text)
                    },
                    modifier = Modifier
                        .size(48.dp)
                        .clip(CircleShape)
                        .background(JudicialNavy)
                        .testTag("send_voice_text_button")
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.Send,
                        contentDescription = "Send",
                        tint = WarmPaper
                    )
                }
            } else {
                // Record Audio Button
                IconButton(
                    onClick = {
                        if (isRecording) {
                            onStopRecordingAndSend()
                        } else {
                            onStartRecording()
                        }
                    },
                    modifier = Modifier
                        .size(54.dp)
                        .scale(if (isRecording) pulseScale else 1f)
                        .clip(CircleShape)
                        .background(if (isRecording) DeepCrimson else JudicialNavy)
                        .testTag("voice_mic_toggle_button")
                ) {
                    Icon(
                        imageVector = if (isRecording) Icons.Default.Stop else Icons.Default.Mic,
                        contentDescription = if (isRecording) "Stop Recording" else "Speak",
                        tint = WarmPaper,
                        modifier = Modifier.size(28.dp)
                    )
                }
            }
        }
    }
}
