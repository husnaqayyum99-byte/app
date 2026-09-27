package com.example.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.MenuBook
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.data.models.LanguageMode
import com.example.ui.theme.*

data class QuickScenario(
    val titleEn: String,
    val titleUr: String,
    val problemText: String,
    val location: String,
    val icon: ImageVector,
    val tagEn: String,
    val tagUr: String
)

@Composable
fun HomeScreen(
    language: LanguageMode,
    isUserLoggedIn: Boolean = false,
    onStartCaseClick: () -> Unit,
    onVoiceLiveClick: () -> Unit = {},
    onChatClick: () -> Unit = {},
    onAccountClick: () -> Unit = {},
    onSelectScenario: (String, String) -> Unit,
    onBrowseTopicsClick: () -> Unit,
    onBrowseSourcesClick: () -> Unit
) {
    val isUrdu = language == LanguageMode.URDU

    val sampleScenarios = listOf(
        QuickScenario(
            titleEn = "Car Accident & Police Vehicle Seizure",
            titleUr = "گاڑی کا حادثہ اور پولیس ضبطی",
            problemText = "My car was involved in an accident in Chitral. Nobody was seriously injured, but the police have taken my vehicle. What should I do?",
            location = "Chitral (Lower)",
            icon = Icons.Default.DirectionsCar,
            tagEn = "Traffic",
            tagUr = "ٹریفک"
        ),
        QuickScenario(
            titleEn = "Domicile Record & Duplicate Issue",
            titleUr = "ڈومیسائل میں غلطی یا دوہرا ریکارڈ",
            problemText = "I live in Chitral and I have an issue with my domicile. I think there may be a duplicate or incorrect domicile record.",
            location = "Chitral (Lower)",
            icon = Icons.Default.Badge,
            tagEn = "Domicile",
            tagUr = "ڈومیسائل"
        ),
        QuickScenario(
            titleEn = "Online Blackmail & Private Photos",
            titleUr = "آن لائن ہراسانی اور بلیک میلنگ",
            problemText = "Someone is threatening me online and has been sending my private pictures to other people.",
            location = "Chitral (Lower)",
            icon = Icons.Default.Security,
            tagEn = "Cyber / PECA",
            tagUr = "سائبر کرائم"
        ),
        QuickScenario(
            titleEn = "Land & Boundary Ownership Dispute",
            titleUr = "اراضی اور حد براری کا تنازع",
            problemText = "There is a dispute about ownership and boundary encroachment of a piece of land in Chitral.",
            location = "Chitral (Upper)",
            icon = Icons.Default.Landscape,
            tagEn = "Land Revenue",
            tagUr = "محکمہ مال"
        )
    )

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 32.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Hero Card with Judicial Chamber Banner
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = JudicialNavy),
                elevation = CardDefaults.cardElevation(defaultElevation = 4.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_banner_card")
            ) {
                Column {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(160.dp)
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.legal_hero_banner),
                            contentDescription = "Chitral Judicial Chamber",
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize()
                        )
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(
                                    Brush.verticalGradient(
                                        colors = listOf(
                                            Color.Transparent,
                                            JudicialNavy.copy(alpha = 0.85f),
                                            JudicialNavy
                                        )
                                    )
                                )
                        )
                        // Scope Badge
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = AntiqueGold.copy(alpha = 0.95f),
                            modifier = Modifier
                                .padding(12.dp)
                                .align(Alignment.TopStart)
                        ) {
                            Text(
                                text = if (isUrdu) "چترال اور خیبر پختونخوا اولیت" else "Chitral & KP First",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JudicialNavyDark
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }

                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = if (isUrdu) "اپنا وکیل" else "Apna Wakeel",
                            style = MaterialTheme.typography.headlineSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = WarmIvory
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUrdu) "قانونی راستہ سمجھیں۔" else "Understand your legal path.",
                            style = MaterialTheme.typography.titleMedium.copy(
                                color = AntiqueGold,
                                fontWeight = FontWeight.SemiBold
                            )
                        )
                        Spacer(modifier = Modifier.height(8.dp))
                        Text(
                            text = if (isUrdu)
                                "اپنے مسئلے کو اپنے آسان الفاظ میں بیان کریں۔ ہم متعلقہ قانون، مجاز ادارہ، درکار کاغذات، شواہد اور اگلے عملی اقدامات کی رہنمائی کریں گے۔"
                            else
                                "Describe your situation in your own words. We will help you identify the relevant legal area, authority, documents, evidence, and clear next steps.",
                            style = MaterialTheme.typography.bodyMedium.copy(
                                color = WarmPaper.copy(alpha = 0.9f),
                                lineHeight = 20.sp
                            )
                        )
                        Spacer(modifier = Modifier.height(16.dp))

                        Button(
                            onClick = onStartCaseClick,
                            shape = RoundedCornerShape(10.dp),
                            colors = ButtonDefaults.buttonColors(
                                containerColor = AntiqueGold,
                                contentColor = JudicialNavyDark
                            ),
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(50.dp)
                                .testTag("start_case_button")
                        ) {
                            Icon(
                                imageVector = Icons.Default.PlayArrow,
                                contentDescription = null,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "نیا کیس شروع کریں" else "Start Your Case",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold
                                )
                            )
                        }
                    }
                }
            }
        }

        // Live Voice Consultation (gemini-3.8-live) Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = WarmIvory),
                border = BorderStroke(1.5.dp, AntiqueGold),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onVoiceLiveClick() }
                    .testTag("home_live_voice_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(JudicialNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Mic,
                            contentDescription = null,
                            tint = LightGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isUrdu) "لائیو وکیل صوتی مشاورت" else "Live Voice Consultation",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JudicialNavy
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = DeepCrimson.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "gemini-3.8-live",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = DeepCrimson,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isUrdu)
                                "ریئل ٹائم میں اپنے الفاظ میں قانونی مشورہ حاصل کریں"
                            else
                                "Have real-time voice conversations with AI legal counsel",
                            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Voice",
                        tint = AntiqueGoldDark
                    )
                }
            }
        }

        // KP Legal Procedures Q&A Chat (Firebase AI SDK) Card
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onChatClick() }
                    .testTag("home_legal_chat_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(EmeraldVerified.copy(alpha = 0.12f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Chat,
                            contentDescription = null,
                            tint = EmeraldVerified,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isUrdu) "کے پی قانونی سوال و جواب" else "KP Legal Procedures Chat",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JudicialNavy
                                )
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = EmeraldVerified.copy(alpha = 0.12f)
                            ) {
                                Text(
                                    text = "Firebase AI SDK",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = EmeraldVerified,
                                        fontSize = 10.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(2.dp))
                        Text(
                            text = if (isUrdu)
                                "تھانہ، عدالت، اور پٹوار کے قانونی ضوابط کے بارے میں سوالات پوچھیں"
                            else
                                "Ask questions about court, police, land partition, and family procedures",
                            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Open Chat",
                        tint = AntiqueGoldDark
                    )
                }
            }
        }

        // Institutional Trust Principles
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                TrustBadge(
                    icon = Icons.Default.Verified,
                    title = if (isUrdu) "سرکاری ذرائع" else "Official Sources",
                    subtitle = if (isUrdu) "کے پی کوڈ و عدالت" else "KP Code & Courts",
                    modifier = Modifier.weight(1f)
                )
                TrustBadge(
                    icon = Icons.Default.NearMe,
                    title = if (isUrdu) "چترال پر فوکس" else "Chitral Focus",
                    subtitle = if (isUrdu) "ڈی سی، پولیس و نادرا" else "DC, Police & Nadra",
                    modifier = Modifier.weight(1f)
                )
                TrustBadge(
                    icon = Icons.Default.ChatBubbleOutline,
                    title = if (isUrdu) "سادہ زبان" else "Plain Language",
                    subtitle = if (isUrdu) "اردو اور انگلش" else "English & Urdu",
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Quick Case Scenarios Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (isUrdu) "عمومی قانونی مسائل (آزما کر دیکھیں)" else "Common Legal Situations (Tap to Test)",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = CharcoalText
                    )
                )
            }
        }

        // Quick Scenarios Cards
        items(sampleScenarios) { scenario ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone),
                elevation = CardDefaults.cardElevation(defaultElevation = 1.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onSelectScenario(scenario.problemText, scenario.location) }
                    .testTag("scenario_card_${scenario.tagEn.lowercase()}")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(44.dp)
                            .clip(CircleShape)
                            .background(JudicialNavy.copy(alpha = 0.08f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = scenario.icon,
                            contentDescription = null,
                            tint = JudicialNavy,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isUrdu) scenario.titleUr else scenario.titleEn,
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = JudicialNavy.copy(alpha = 0.08f)
                            ) {
                                Text(
                                    text = if (isUrdu) scenario.tagUr else scenario.tagEn,
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = JudicialNavyLight,
                                        fontSize = 10.sp
                                    ),
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = scenario.problemText,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CharcoalMuted
                            ),
                            maxLines = 2
                        )
                    }
                    Icon(
                        imageVector = Icons.Default.ChevronRight,
                        contentDescription = "Select",
                        tint = CharcoalMuted
                    )
                }
            }
        }

        // KP Legal Topics Library Card
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.5.dp, AntiqueGold),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBrowseTopicsClick() }
                    .testTag("browse_kp_topics_card")
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(46.dp)
                            .clip(CircleShape)
                            .background(JudicialNavy),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.AutoStories,
                            contentDescription = null,
                            tint = AntiqueGold,
                            modifier = Modifier.size(24.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp)
                        ) {
                            Text(
                                text = if (isUrdu) "خیبر پختونخوا کے قانونی موضوعات" else "KP Law Topics Library",
                                style = MaterialTheme.typography.titleSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JudicialNavy
                                )
                            )
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = AntiqueGold.copy(alpha = 0.2f)
                            ) {
                                Text(
                                    text = if (isUrdu) "قوانین" else "Room DB",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = JudicialNavyDark,
                                        fontSize = 9.sp,
                                        fontWeight = FontWeight.Bold
                                    ),
                                    modifier = Modifier.padding(horizontal = 5.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Spacer(modifier = Modifier.height(3.dp))
                        Text(
                            text = if (isUrdu)
                                "قانونِ اراضی، خاندانی قوانین اور ضابطہ فوجداری (22-اے، سپرداری، ضمانت)"
                            else
                                "Property Law, Family Law, Criminal Procedure (Sec 22-A, Bail, Superdari)",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CharcoalMuted
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "Browse Topics",
                        tint = AntiqueGoldDark
                    )
                }
            }
        }

        // Browse Authoritative Directory Card
        item {
            OutlinedCard(
                shape = RoundedCornerShape(12.dp),
                border = BorderStroke(1.dp, BorderStone),
                colors = CardDefaults.outlinedCardColors(containerColor = WarmIvory),
                modifier = Modifier
                    .fillMaxWidth()
                    .clickable { onBrowseSourcesClick() }
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.MenuBook,
                        contentDescription = null,
                        tint = AntiqueGoldDark,
                        modifier = Modifier.size(24.dp)
                    )
                    Spacer(modifier = Modifier.width(12.dp))
                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isUrdu) "سرکاری و مصدقہ قوانین کی ڈائریکٹری" else "Official Statutes & Sources Directory",
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = JudicialNavyDark
                            )
                        )
                        Text(
                            text = if (isUrdu) "کے پی کوڈ، پشاور ہائی کورٹ، نادرا، چترال انتظامیہ" else "Browse verified KP Code, High Court, NADRA, and Chitral sources",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CharcoalMuted
                            )
                        )
                    }
                    Icon(
                        imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                        contentDescription = "View Sources",
                        tint = AntiqueGoldDark
                    )
                }
            }
        }

        // Legal Safety Disclaimer
        item {
            Surface(
                shape = RoundedCornerShape(8.dp),
                color = SurfaceCard,
                border = BorderStroke(1.dp, BorderStone.copy(alpha = 0.6f)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(12.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Icon(
                        imageVector = Icons.Default.Info,
                        contentDescription = "Notice",
                        tint = CharcoalMuted,
                        modifier = Modifier.size(16.dp).padding(top = 2.dp)
                    )
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = if (isUrdu)
                            "قانونی نوٹس: اپنا وکیل مصدقہ سرکاری قوانین اور انتظامی طریقہ کار کے مطابق رہنمائی فراہم کرتا ہے۔ یہ کسی مستند وکیل، عدالت یا سرکاری ادارے کا متبادل نہیں ہے۔"
                        else
                            "Legal Notice: Apna Wakeel provides navigation based on authoritative official sources. It does not replace a licensed advocate, court of law, or competent government authority.",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalMuted,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )
                }
            }
        }
    }
}

@Composable
private fun TrustBadge(
    icon: ImageVector,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(8.dp),
        colors = CardDefaults.cardColors(containerColor = SurfaceCard),
        border = BorderStroke(1.dp, BorderStone),
        modifier = modifier
    ) {
        Column(
            modifier = Modifier.padding(8.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                tint = AntiqueGoldDark,
                modifier = Modifier.size(20.dp)
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.Bold,
                    color = CharcoalText,
                    fontSize = 11.sp
                )
            )
            Text(
                text = subtitle,
                style = MaterialTheme.typography.labelSmall.copy(
                    color = CharcoalMuted,
                    fontSize = 9.sp
                )
            )
        }
    }
}
