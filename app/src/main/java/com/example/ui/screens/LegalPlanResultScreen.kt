package com.example.ui.screens

import android.content.ClipData
import android.content.ClipboardManager
import android.content.Context
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.FinalLegalPlan
import com.example.data.models.LanguageMode
import com.example.ui.theme.*

@Composable
fun LegalPlanResultScreen(
    language: LanguageMode,
    plan: FinalLegalPlan,
    isBookmarked: Boolean,
    aiNote: String?,
    onToggleBookmark: () -> Unit,
    onStartNewCase: () -> Unit
) {
    val isUrdu = language == LanguageMode.URDU
    val context = LocalContext.current

    val caseSummary = if (isUrdu) plan.caseSummaryUr else plan.caseSummaryEn
    val documents = if (isUrdu && plan.documentsRequiredUr.isNotEmpty()) plan.documentsRequiredUr else plan.documentsRequired
    val evidenceList = if (isUrdu && plan.evidenceToPreserveUr.isNotEmpty()) plan.evidenceToPreserveUr else plan.evidenceToPreserve
    val timeline = if (isUrdu && plan.timelineGuidanceUr.isNotEmpty()) plan.timelineGuidanceUr else plan.timelineGuidance
    val lawyerAdvice = if (isUrdu && plan.lawyerConsultationAdviceUr.isNotEmpty()) plan.lawyerConsultationAdviceUr else plan.lawyerConsultationAdvice
    val uncertainties = if (isUrdu && plan.uncertaintiesUr.isNotEmpty()) plan.uncertaintiesUr else plan.uncertainties

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(WarmPaper)
            .padding(horizontal = 16.dp),
        contentPadding = PaddingValues(top = 16.dp, bottom = 40.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header & Case Identifier
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = JudicialNavy),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = AntiqueGold.copy(alpha = 0.25f)
                        ) {
                            Text(
                                text = plan.caseId,
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGold
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                            )
                        }

                        IconButton(
                            onClick = onToggleBookmark,
                            modifier = Modifier.size(36.dp)
                        ) {
                            Icon(
                                imageVector = if (isBookmarked) Icons.Default.Bookmark else Icons.Default.BookmarkBorder,
                                contentDescription = "Bookmark",
                                tint = if (isBookmarked) AntiqueGold else WarmIvory
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = if (isUrdu) "آپ کا قانونی راستہ" else "Your Legal Path",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = WarmIvory
                        )
                    )

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isUrdu)
                            "چترال اور خیبر پختونخوا کے مصدقہ سرکاری قوانین پر مبنی رہنمائی"
                        else
                            "Verified practical navigation plan for Chitral & Khyber Pakhtunkhwa",
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = WarmPaper.copy(alpha = 0.8f)
                        )
                    )
                }
            }
        }

        // Emergency Notice if critical
        if (plan.isEmergency) {
            item {
                Card(
                    shape = RoundedCornerShape(10.dp),
                    colors = CardDefaults.cardColors(containerColor = UrgentRedBg),
                    border = BorderStroke(1.dp, UrgentRed),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Row(
                        modifier = Modifier.padding(14.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Warning,
                            contentDescription = "Urgent",
                            tint = UrgentRed
                        )
                        Spacer(modifier = Modifier.width(10.dp))
                        Text(
                            text = if (isUrdu)
                                plan.emergencyNoticeUr ?: "فوری حفاظت: پہلے ایمرجنسی سروسز (15 پولیس یا 1122) سے رابطہ کریں۔"
                            else
                                plan.emergencyNoticeEn ?: "Immediate Safety Notice: Contact Emergency Services (15 Police / 1122 Rescue).",
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = UrgentRed,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }
                }
            }
        }

        // Section 1: Your Situation & Legal Area
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = JudicialNavy.copy(alpha = 0.1f)
                        ) {
                            Text(
                                text = if (isUrdu) plan.legalArea.titleUr else plan.legalArea.titleEn,
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JudicialNavy
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = VerifiedGreenLight
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Icon(
                                    imageVector = Icons.Default.Verified,
                                    contentDescription = null,
                                    tint = VerifiedGreen,
                                    modifier = Modifier.size(14.dp)
                                )
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(
                                    text = if (isUrdu) "مصدقہ ضابطہ" else "Statutory Law",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        color = VerifiedGreen,
                                        fontWeight = FontWeight.Bold
                                    )
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (isUrdu) "آپ کا واقعہ" else "Your Situation",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = caseSummary,
                        style = MaterialTheme.typography.bodyMedium.copy(
                            color = CharcoalText,
                            lineHeight = 22.sp
                        )
                    )
                }
            }
        }

        // Section 2: Responsible Authority
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.AccountBalance,
                            contentDescription = null,
                            tint = AntiqueGoldDark,
                            modifier = Modifier.size(22.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "مجاز سرکاری ادارہ" else "Responsible Authority",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = JudicialNavy
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (isUrdu) plan.responsibleAuthority.nameUr else plan.responsibleAuthority.nameEn,
                        style = MaterialTheme.typography.titleSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    )
                    Text(
                        text = if (isUrdu) plan.responsibleAuthority.departmentUr else plan.responsibleAuthority.departmentEn,
                        style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted)
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Row(verticalAlignment = Alignment.Top) {
                        Icon(
                            imageVector = Icons.Default.Place,
                            contentDescription = null,
                            tint = CharcoalMuted,
                            modifier = Modifier.size(16.dp).padding(top = 2.dp)
                        )
                        Spacer(modifier = Modifier.width(6.dp))
                        Text(
                            text = plan.responsibleAuthority.officeAddressChitral,
                            style = MaterialTheme.typography.bodySmall.copy(color = CharcoalText)
                        )
                    }

                    if (plan.responsibleAuthority.helpline != null) {
                        Spacer(modifier = Modifier.height(4.dp))
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Phone,
                                contentDescription = null,
                                tint = VerifiedGreen,
                                modifier = Modifier.size(16.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = plan.responsibleAuthority.helpline,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = VerifiedGreen
                                )
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = WarmIvory,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Column(modifier = Modifier.padding(10.dp)) {
                            Text(
                                text = if (isUrdu) "متعلقہ طریقہ کار:" else "Official Procedure:",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGoldDark
                                )
                            )
                            Spacer(modifier = Modifier.height(2.dp))
                            Text(
                                text = if (isUrdu) plan.responsibleAuthority.procedureSummaryUr else plan.responsibleAuthority.procedureSummary,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = CharcoalText,
                                    lineHeight = 18.sp
                                )
                            )
                        }
                    }
                }
            }
        }

        // Section 3: Action Steps (Numbered)
        item {
            Text(
                text = if (isUrdu) "آپ کو کیا کرنا چاہیے (عملی اقدامات)" else "What You Should Do (Action Plan)",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = JudicialNavy
                )
            )
        }

        items(plan.actionSteps) { step ->
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone),
                modifier = Modifier.fillMaxWidth()
            ) {
                Row(
                    modifier = Modifier.padding(14.dp),
                    verticalAlignment = Alignment.Top
                ) {
                    Box(
                        modifier = Modifier
                            .size(30.dp)
                            .clip(CircleShape)
                            .background(AntiqueGold),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = "${step.stepNumber}",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = JudicialNavyDark
                            )
                        )
                    }

                    Spacer(modifier = Modifier.width(12.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = if (isUrdu) step.titleUr else step.titleEn,
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = if (isUrdu) step.descriptionUr else step.descriptionEn,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CharcoalText,
                                lineHeight = 18.sp
                            )
                        )

                        if (step.authorityOrDesk != null) {
                            Spacer(modifier = Modifier.height(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = WarmIvory
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(
                                        imageVector = Icons.Default.Business,
                                        contentDescription = null,
                                        tint = CharcoalMuted,
                                        modifier = Modifier.size(12.dp)
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text(
                                        text = step.authorityOrDesk,
                                        style = MaterialTheme.typography.labelSmall.copy(
                                            color = CharcoalMuted,
                                            fontSize = 11.sp
                                        )
                                    )
                                }
                            }
                        }
                    }
                }
            }
        }

        // Section 4: Documents to Prepare
        if (documents.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderStone),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Description,
                                contentDescription = null,
                                tint = AntiqueGoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "درکار ضروری دستاویزات" else "Documents to Prepare",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        documents.forEach { doc ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircleOutline,
                                    contentDescription = null,
                                    tint = VerifiedGreen,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = doc,
                                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalText)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 5: Evidence to Preserve
        if (evidenceList.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                    border = BorderStroke(1.dp, BorderStone),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.Inventory,
                                contentDescription = null,
                                tint = AntiqueGoldDark,
                                modifier = Modifier.size(20.dp)
                            )
                            Spacer(modifier = Modifier.width(8.dp))
                            Text(
                                text = if (isUrdu) "شواہد جو محفوظ رکھنے ضروری ہیں" else "Evidence to Preserve",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = CharcoalText
                                )
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        evidenceList.forEach { ev ->
                            Row(
                                modifier = Modifier.padding(vertical = 4.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Icon(
                                    imageVector = Icons.Default.CameraAlt,
                                    contentDescription = null,
                                    tint = CharcoalMuted,
                                    modifier = Modifier.size(16.dp).padding(top = 2.dp)
                                )
                                Spacer(modifier = Modifier.width(8.dp))
                                Text(
                                    text = ev,
                                    style = MaterialTheme.typography.bodySmall.copy(color = CharcoalText)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Section 6: Timeline Guidance
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Schedule,
                            contentDescription = null,
                            tint = AntiqueGoldDark,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "قانونی میعاد اور وقت" else "Timeline & Deadlines",
                            style = MaterialTheme.typography.titleMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalText
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = timeline,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalText,
                            lineHeight = 18.sp
                        )
                    )
                }
            }
        }

        // Section 7: Official Sources Cards
        item {
            Text(
                text = if (isUrdu) "مصدقہ سرکاری ذرائع (KP & Federal)" else "Official Authoritative Sources",
                style = MaterialTheme.typography.titleMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = JudicialNavy
                )
            )
        }

        items(plan.verifiedSources) { source ->
            Card(
                shape = RoundedCornerShape(10.dp),
                colors = CardDefaults.cardColors(containerColor = WarmIvory),
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
                            style = MaterialTheme.typography.titleSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = JudicialNavy
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = VerifiedGreenLight
                        ) {
                            Text(
                                text = "Official",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = VerifiedGreen,
                                    fontSize = 10.sp,
                                    fontWeight = FontWeight.Bold
                                ),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = "${source.authority} • ${source.jurisdiction}",
                        style = MaterialTheme.typography.labelSmall.copy(color = CharcoalMuted)
                    )

                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = source.description,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalText,
                            fontSize = 11.sp,
                            lineHeight = 16.sp
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            imageVector = Icons.Default.Link,
                            contentDescription = null,
                            tint = AntiqueGoldDark,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = source.officialUrl,
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = AntiqueGoldDark,
                                fontSize = 11.sp
                            )
                        )
                    }
                }
            }
        }

        // Section 8: Professional Assistance & Uncertainties
        item {
            Card(
                shape = RoundedCornerShape(12.dp),
                colors = CardDefaults.cardColors(containerColor = SurfaceCard),
                border = BorderStroke(1.dp, BorderStone),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = if (isUrdu) "وکیل کی ضرورت اور قانونی حدود" else "When to Consult a Qualified Lawyer",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = CharcoalText
                        )
                    )
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = lawyerAdvice,
                        style = MaterialTheme.typography.bodySmall.copy(
                            color = CharcoalText,
                            lineHeight = 18.sp
                        )
                    )

                    if (uncertainties.isNotEmpty()) {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = if (isUrdu) "غیر یقینی نکات (تحقیقی حدود):" else "Unverified Points (Statutory Limitations):",
                            style = MaterialTheme.typography.labelSmall.copy(
                                fontWeight = FontWeight.Bold,
                                color = CharcoalMuted
                            )
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        uncertainties.forEach { un ->
                            Row(
                                modifier = Modifier.padding(vertical = 2.dp),
                                verticalAlignment = Alignment.Top
                            ) {
                                Text("• ", style = MaterialTheme.typography.bodySmall.copy(color = CharcoalMuted))
                                Text(
                                    text = un,
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
        }

        // Optional Live AI Enrichment Note
        if (aiNote != null) {
            item {
                Card(
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmIvory),
                    border = BorderStroke(1.dp, AntiqueGold.copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(14.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Default.TipsAndUpdates,
                                contentDescription = null,
                                tint = AntiqueGoldDark,
                                modifier = Modifier.size(18.dp)
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                text = if (isUrdu) "اضافی عدالتی و انتظامی ٹپس" else "Practical Field Guidance",
                                style = MaterialTheme.typography.labelMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = AntiqueGoldDark
                                )
                            )
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        Text(
                            text = aiNote,
                            style = MaterialTheme.typography.bodySmall.copy(
                                color = CharcoalText,
                                lineHeight = 18.sp
                            )
                        )
                    }
                }
            }
        }

        // Action Buttons: Copy Plan & Start New Case
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        val clipboard = context.getSystemService(Context.CLIPBOARD_SERVICE) as ClipboardManager
                        val clip = ClipData.newPlainText("Apna Wakeel Legal Path", "${plan.caseId}\n$caseSummary\n\nAuthority: ${plan.responsibleAuthority.nameEn}\n${plan.responsibleAuthority.officeAddressChitral}")
                        clipboard.setPrimaryClip(clip)
                        Toast.makeText(context, if (isUrdu) "قانونی پلان کاپی کر لیا گیا" else "Legal Path summary copied to clipboard", Toast.LENGTH_SHORT).show()
                    },
                    shape = RoundedCornerShape(10.dp),
                    border = BorderStroke(1.dp, BorderStone),
                    modifier = Modifier.weight(1f)
                ) {
                    Icon(
                        imageVector = Icons.Default.ContentCopy,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isUrdu) "کاپی کریں" else "Copy Plan")
                }

                Button(
                    onClick = onStartNewCase,
                    shape = RoundedCornerShape(10.dp),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = JudicialNavy,
                        contentColor = WarmIvory
                    ),
                    modifier = Modifier.weight(1f).testTag("start_another_case_button")
                ) {
                    Icon(
                        imageVector = Icons.Default.Refresh,
                        contentDescription = null,
                        modifier = Modifier.size(16.dp)
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Text(text = if (isUrdu) "نیا کیس" else "New Case")
                }
            }
        }
    }
}
