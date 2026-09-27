package com.example.ui.screens

import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.scale
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LanguageMode
import com.example.ui.theme.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CaseIntakeScreen(
    language: LanguageMode,
    problemInput: String,
    selectedLocation: String,
    errorMessage: String?,
    isRecording: Boolean = false,
    recordingDurationSeconds: Int = 0,
    isTranscribing: Boolean = false,
    transcriptionError: String? = null,
    onProblemChange: (String) -> Unit,
    onLocationChange: (String) -> Unit,
    onStartRecording: () -> Boolean = { false },
    onStopRecording: () -> Unit = {},
    onCancelRecording: () -> Unit = {},
    onNavigateToLiveVoice: () -> Unit = {},
    onSubmit: () -> Unit
) {
    val isUrdu = language == LanguageMode.URDU
    val scrollState = rememberScrollState()

    val chitralLocations = listOf(
        "Chitral (Lower)",
        "Chitral (Upper)",
        "Peshawar",
        "Khyber Pakhtunkhwa (General)"
    )

    // Check for emergency keywords in real time
    val lowerText = problemInput.lowercase()
    val isEmergency = lowerText.contains("life threat") || lowerText.contains("murder") ||
            lowerText.contains("gun") || lowerText.contains("severe injury") ||
            lowerText.contains("dying") || lowerText.contains("jaan ka khatra") || lowerText.contains("قتل")

    val infiniteTransition = rememberInfiniteTransition(label = "mic_pulse")
    val pulseScale by infiniteTransition.animateFloat(
        initialValue = 1f,
        targetValue = if (isRecording) 1.2f else 1f,
        animationSpec = infiniteRepeatable(
            animation = tween(durationMillis = 600, easing = FastOutSlowInEasing),
            repeatMode = RepeatMode.Reverse
        ),
        label = "pulse_scale"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .verticalScroll(scrollState)
            .padding(16.dp)
    ) {
        Text(
            text = if (isUrdu) "اپنا واقعہ یا مسئلہ بیان کریں" else "Describe Your Legal Problem",
            style = MaterialTheme.typography.headlineSmall.copy(
                fontWeight = FontWeight.Bold,
                color = JudicialNavy
            )
        )

        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = if (isUrdu)
                "اپنے آسان الفاظ میں تفصیل بتائیں یا مائیک کے ذریعے بول کر ٹرانسکرائب کریں۔"
            else
                "Describe your situation in your own words, or speak using the microphone to transcribe with gemini-3.5-transcribe.",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = CharcoalMuted
            )
        )

        Spacer(modifier = Modifier.height(14.dp))

        // Banner to switch to Live Voice (gemini-3.8-live)
        Card(
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onNavigateToLiveVoice() }
                .testTag("open_live_voice_banner"),
            shape = RoundedCornerShape(10.dp),
            colors = CardDefaults.cardColors(containerColor = JudicialNavy),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(12.dp),
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
                            imageVector = Icons.Default.RecordVoiceOver,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(20.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(10.dp))
                    Column {
                        Text(
                            text = if (isUrdu) "لائیو صوتی مشاورت (gemini-3.8-live)" else "Try Live Voice (gemini-3.8-live)",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmPaper
                            )
                        )
                        Text(
                            text = if (isUrdu) "ریئل ٹائم میں وکیل سے بول کر بات کریں" else "Have a real-time conversation in English/Urdu",
                            style = MaterialTheme.typography.bodySmall.copy(color = LightGold, fontSize = 11.sp)
                        )
                    }
                }
                Icon(
                    imageVector = Icons.Default.ChevronRight,
                    contentDescription = null,
                    tint = WarmPaper
                )
            }
        }

        Spacer(modifier = Modifier.height(14.dp))

        // Emergency banner if danger detected
        if (isEmergency) {
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = UrgentRedBg),
                border = BorderStroke(1.dp, UrgentRed),
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(bottom = 12.dp)
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.Warning,
                        contentDescription = "Alert",
                        tint = UrgentRed
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = if (isUrdu)
                            "فوری انتباہ: اگر جان کو خطرہ یا فوری جسمانی حملہ درپیش ہے تو پولیس 15 یا ریسکیو 1122 کو کال کریں۔"
                        else
                            "Immediate Safety Alert: If there is an ongoing physical threat or danger to life, prioritize calling Police (15) or Rescue (1122).",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = UrgentRed,
                            fontWeight = FontWeight.Bold
                        )
                    )
                }
            }
        }

        // Main Text Input Card
        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderStone),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                OutlinedTextField(
                    value = problemInput,
                    onValueChange = onProblemChange,
                    placeholder = {
                        Text(
                            text = if (isUrdu)
                                "مثال کے طور پر: میری گاڑی کا ایکسیڈنٹ ہوا ہے چترال میں، دوسری گاڑی والے نے نقصان کیا اور پولیس نے گاڑی تھانے میں روک لی ہے۔ مجھے اب کیا کرنا چاہیے؟"
                            else
                                "e.g., My car was involved in an accident in Chitral and the police have detained the vehicle. What documents do I need and how do I get it released?",
                            style = MaterialTheme.typography.bodyMedium.copy(color = CharcoalMuted.copy(alpha = 0.7f))
                        )
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .heightIn(min = 140.dp)
                        .testTag("problem_input_field"),
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = AntiqueGold,
                        unfocusedBorderColor = BorderStone,
                        focusedContainerColor = SurfaceCard,
                        unfocusedContainerColor = SurfaceCard,
                        focusedTextColor = CharcoalText,
                        unfocusedTextColor = CharcoalText
                    ),
                    shape = RoundedCornerShape(8.dp)
                )

                Spacer(modifier = Modifier.height(10.dp))

                // Audio Recording & Transcription Bar (gemini-3.5-transcribe)
                if (isRecording) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = DeepCrimson.copy(alpha = 0.08f)),
                        border = BorderStroke(1.dp, DeepCrimson)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically) {
                                Box(
                                    modifier = Modifier
                                        .size(32.dp)
                                        .scale(pulseScale)
                                        .clip(CircleShape)
                                        .background(DeepCrimson),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Mic,
                                        contentDescription = null,
                                        tint = WarmPaper,
                                        modifier = Modifier.size(18.dp)
                                    )
                                }
                                Spacer(modifier = Modifier.width(10.dp))
                                Column {
                                    Text(
                                        text = if (isUrdu) "آواز ریکارڈ ہو رہی ہے... (${recordingDurationSeconds}s)" else "Recording voice... (${recordingDurationSeconds}s)",
                                        style = MaterialTheme.typography.bodySmall.copy(
                                            color = DeepCrimson,
                                            fontWeight = FontWeight.Bold
                                        )
                                    )
                                    Text(
                                        text = if (isUrdu) "روکنے کے لیے 'ٹرانسکرائب' دبائیں" else "Tap stop to transcribe with gemini-3.5-transcribe",
                                        style = MaterialTheme.typography.labelSmall.copy(color = TextMuted)
                                    )
                                }
                            }

                            Row {
                                IconButton(
                                    onClick = onCancelRecording,
                                    modifier = Modifier.testTag("cancel_mic_recording_button")
                                ) {
                                    Icon(imageVector = Icons.Default.Close, contentDescription = "Cancel", tint = TextMuted)
                                }
                                Button(
                                    onClick = onStopRecording,
                                    colors = ButtonDefaults.buttonColors(containerColor = DeepCrimson),
                                    shape = RoundedCornerShape(6.dp),
                                    modifier = Modifier.testTag("stop_and_transcribe_button")
                                ) {
                                    Text(text = if (isUrdu) "ٹرانسکرائب" else "Transcribe", fontSize = 12.sp)
                                }
                            }
                        }
                    }
                } else if (isTranscribing) {
                    Card(
                        modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(8.dp),
                        colors = CardDefaults.cardColors(containerColor = AntiqueGold.copy(alpha = 0.1f)),
                        border = BorderStroke(1.dp, AntiqueGold)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(12.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            CircularProgressIndicator(
                                modifier = Modifier.size(20.dp),
                                strokeWidth = 2.dp,
                                color = JudicialNavy
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isUrdu) "ماڈل gemini-3.5-transcribe سے ٹرانسکرائب کیا جا رہا ہے..." else "Transcribing audio using gemini-3.5-transcribe...",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = JudicialNavy,
                                    fontWeight = FontWeight.SemiBold
                                )
                            )
                        }
                    }
                } else {
                    // Tap to record button
                    OutlinedButton(
                        onClick = { onStartRecording() },
                        modifier = Modifier
                            .fillMaxWidth()
                            .testTag("mic_input_button"),
                        border = BorderStroke(1.dp, BorderStone),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = JudicialNavy,
                            modifier = Modifier.size(18.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "مائیک سے بولیں (gemini-3.5-transcribe)" else "Speak into Mic (Transcribe with gemini-3.5-transcribe)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = JudicialNavy,
                                fontWeight = FontWeight.Medium
                            )
                        )
                    }
                }

                if (transcriptionError != null) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = transcriptionError,
                        style = MaterialTheme.typography.bodySmall.copy(color = DeepCrimson, fontSize = 11.sp)
                    )
                }

                Spacer(modifier = Modifier.height(6.dp))
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isUrdu) "اردو یا انگریزی میں بیان کریں" else "Natural language or voice input",
                        style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                    )
                    Text(
                        text = "${problemInput.length} chars",
                        style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                    )
                }
            }
        }

        if (errorMessage != null) {
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = errorMessage,
                style = MaterialTheme.typography.bodySmall.copy(color = UrgentRed, fontWeight = FontWeight.Bold),
                modifier = Modifier.padding(horizontal = 4.dp)
            )
        }

        Spacer(modifier = Modifier.height(18.dp))

        // Jurisdiction & Location Selectors
        Text(
            text = if (isUrdu) "مقام اور دائرہ اختیار" else "Jurisdiction & Location",
            style = MaterialTheme.typography.titleSmall.copy(
                fontWeight = FontWeight.Bold,
                color = CharcoalText
            )
        )
        Spacer(modifier = Modifier.height(8.dp))

        Card(
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(containerColor = SurfaceCard),
            border = BorderStroke(1.dp, BorderStone),
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(14.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.Default.LocationOn,
                        contentDescription = null,
                        tint = AntiqueGoldDark,
                        modifier = Modifier.size(20.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu) "صوبہ: خیبر پختونخوا (KP)" else "Province: Khyber Pakhtunkhwa (KP)",
                        style = MaterialTheme.typography.bodyMedium.copy(
                            fontWeight = FontWeight.SemiBold,
                            color = CharcoalText
                        )
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))
                Text(
                    text = if (isUrdu) "ضلع یا علاقہ منتخب کریں:" else "Select District or Area:",
                    style = MaterialTheme.typography.labelMedium.copy(color = CharcoalMuted)
                )
                Spacer(modifier = Modifier.height(8.dp))

                chitralLocations.forEach { loc ->
                    val isSelected = loc == selectedLocation
                    Surface(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp)
                            .clickable { onLocationChange(loc) }
                            .testTag("location_option_${loc.replace(" ", "_")}"),
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) AntiqueGold.copy(alpha = 0.15f) else SurfaceCard,
                        border = BorderStroke(1.dp, if (isSelected) AntiqueGold else BorderStone)
                    ) {
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(horizontal = 12.dp, vertical = 10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            RadioButton(
                                selected = isSelected,
                                onClick = { onLocationChange(loc) },
                                colors = RadioButtonDefaults.colors(
                                    selectedColor = JudicialNavy,
                                    unselectedColor = CharcoalMuted
                                )
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = loc,
                                style = MaterialTheme.typography.bodyMedium.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isSelected) JudicialNavy else CharcoalText
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Continue Button
        Button(
            onClick = onSubmit,
            modifier = Modifier
                .fillMaxWidth()
                .height(52.dp)
                .testTag("submit_case_button"),
            colors = ButtonDefaults.buttonColors(
                containerColor = JudicialNavy,
                contentColor = WarmPaper
            ),
            shape = RoundedCornerShape(10.dp),
            elevation = ButtonDefaults.buttonElevation(defaultElevation = 2.dp)
        ) {
            Icon(
                imageVector = Icons.Default.Search,
                contentDescription = null,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.width(8.dp))
            Text(
                text = if (isUrdu) "قانونی راستہ تلاش کریں" else "Continue to Legal Path",
                style = MaterialTheme.typography.bodyLarge.copy(
                    fontWeight = FontWeight.Bold
                )
            )
        }

        Spacer(modifier = Modifier.height(30.dp))
    }
}
