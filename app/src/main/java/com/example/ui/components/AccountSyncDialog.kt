package com.example.ui.components

import android.app.Activity
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.models.LanguageMode
import com.example.ui.theme.*
import com.google.firebase.auth.FirebaseUser

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AccountSyncDialog(
    user: FirebaseUser?,
    isLoading: Boolean,
    isSyncing: Boolean,
    syncMessage: String?,
    authError: String?,
    savedCasesCount: Int,
    language: LanguageMode,
    onDismiss: () -> Unit,
    onSignInWithGoogle: (Activity) -> Unit,
    onSignInAsGuest: () -> Unit,
    onSignOut: () -> Unit,
    onSyncNow: () -> Unit
) {
    val isUrdu = language == LanguageMode.URDU
    val context = LocalContext.current
    val activity = context as? Activity

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        containerColor = SurfaceCard,
        dragHandle = { BottomSheetDefaults.DragHandle(color = BorderStone) }
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Header icon
            Box(
                modifier = Modifier
                    .size(60.dp)
                    .clip(CircleShape)
                    .background(if (user != null) EmeraldVerified.copy(alpha = 0.15f) else JudicialNavy.copy(alpha = 0.1f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = if (user != null) Icons.Default.CloudDone else Icons.Default.CloudQueue,
                    contentDescription = null,
                    tint = if (user != null) EmeraldVerified else JudicialNavy,
                    modifier = Modifier.size(32.dp)
                )
            }

            Spacer(modifier = Modifier.height(12.dp))

            Text(
                text = if (isUrdu) "گوگل اکاؤنٹ اور کلاؤڈ سنک" else "Firebase Auth & Cloud Firestore",
                style = MaterialTheme.typography.titleLarge.copy(
                    fontWeight = FontWeight.Bold,
                    color = JudicialNavy
                )
            )

            Text(
                text = if (isUrdu) "اپنے کیسز اور قانونی تاریخ کو فائر بیس کے ساتھ محفوظ رکھیں" else "Securely sync your legal cases and consultations across devices with Google Sign-In and Cloud Firestore.",
                style = MaterialTheme.typography.bodySmall.copy(color = TextMuted),
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            if (authError != null) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp),
                    colors = CardDefaults.cardColors(containerColor = DeepCrimson.copy(alpha = 0.1f))
                ) {
                    Text(
                        text = authError,
                        style = MaterialTheme.typography.bodySmall.copy(color = DeepCrimson),
                        modifier = Modifier.padding(12.dp)
                    )
                }
                Spacer(modifier = Modifier.height(12.dp))
            }

            if (user != null) {
                // Logged in user details
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(12.dp),
                    colors = CardDefaults.cardColors(containerColor = WarmPaper),
                    border = BorderStroke(1.dp, BorderStone)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Box(
                                modifier = Modifier
                                    .size(40.dp)
                                    .clip(CircleShape)
                                    .background(JudicialNavy),
                                contentAlignment = Alignment.Center
                            ) {
                                Text(
                                    text = (user.displayName?.take(1) ?: user.email?.take(1) ?: "U").uppercase(),
                                    color = WarmPaper,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                            Spacer(modifier = Modifier.width(12.dp))
                            Column {
                                Text(
                                    text = user.displayName ?: if (user.isAnonymous) "Guest Citizen" else "Citizen User",
                                    style = MaterialTheme.typography.titleMedium.copy(
                                        fontWeight = FontWeight.SemiBold,
                                        color = JudicialNavy
                                    )
                                )
                                Text(
                                    text = user.email ?: if (user.isAnonymous) "Anonymous Session" else "Authenticated",
                                    style = MaterialTheme.typography.bodySmall.copy(color = TextMuted)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))
                        HorizontalDivider(color = BorderStone.copy(alpha = 0.5f))
                        Spacer(modifier = Modifier.height(12.dp))

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isUrdu) "محفوظ کیسز:" else "Synced Legal Cases:",
                                    style = MaterialTheme.typography.bodyMedium.copy(color = JudicialNavy)
                                )
                                if (syncMessage != null) {
                                    Text(
                                        text = syncMessage,
                                        style = MaterialTheme.typography.bodySmall.copy(color = EmeraldVerified, fontSize = 11.sp)
                                    )
                                }
                            }
                            Text(
                                text = "$savedCasesCount",
                                style = MaterialTheme.typography.titleMedium.copy(
                                    fontWeight = FontWeight.Bold,
                                    color = JudicialNavy
                                )
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Sync Now Button
                Button(
                    onClick = onSyncNow,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("firestore_sync_now_button"),
                    enabled = !isSyncing,
                    colors = ButtonDefaults.buttonColors(containerColor = JudicialNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isSyncing) {
                        CircularProgressIndicator(
                            color = WarmPaper,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isUrdu) "سنک ہو رہا ہے..." else "Syncing with Firestore...")
                    } else {
                        Icon(imageVector = Icons.Default.Sync, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isUrdu) "ابھی کلاؤڈ سنک کریں" else "Sync Now With Firestore")
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))

                // Sign Out Button
                OutlinedButton(
                    onClick = onSignOut,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("auth_sign_out_button"),
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderStone)
                ) {
                    Icon(imageVector = Icons.Default.Logout, contentDescription = null, tint = DeepCrimson, modifier = Modifier.size(18.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(text = if (isUrdu) "سائن آؤٹ کریں" else "Sign Out", color = DeepCrimson)
                }

            } else {
                // Not logged in buttons
                Button(
                    onClick = {
                        activity?.let { onSignInWithGoogle(it) } ?: onSignInAsGuest()
                    },
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("google_sign_in_button"),
                    enabled = !isLoading,
                    colors = ButtonDefaults.buttonColors(containerColor = JudicialNavy),
                    shape = RoundedCornerShape(8.dp)
                ) {
                    if (isLoading) {
                        CircularProgressIndicator(
                            color = WarmPaper,
                            modifier = Modifier.size(18.dp),
                            strokeWidth = 2.dp
                        )
                    } else {
                        Icon(
                            imageVector = Icons.Default.AccountCircle,
                            contentDescription = null,
                            tint = LightGold,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = if (isUrdu) "گوگل سے سائن ان کریں" else "Sign In with Google",
                            color = WarmPaper,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.height(10.dp))

                OutlinedButton(
                    onClick = onSignInAsGuest,
                    modifier = Modifier
                        .fillMaxWidth()
                        .testTag("guest_sign_in_button"),
                    enabled = !isLoading,
                    shape = RoundedCornerShape(8.dp),
                    border = BorderStroke(1.dp, BorderStone)
                ) {
                    Text(
                        text = if (isUrdu) "بطور مہمان شہری جاری رکھیں" else "Continue as Guest Citizen",
                        color = JudicialNavy
                    )
                }
            }

            Spacer(modifier = Modifier.height(16.dp))
        }
    }
}
