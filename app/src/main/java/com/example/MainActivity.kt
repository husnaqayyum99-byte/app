package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import com.example.data.models.LanguageMode
import com.example.ui.components.AccountSyncDialog
import com.example.ui.components.JudicialTopAppBar
import com.example.ui.screens.*
import com.example.ui.theme.ApnaWakeelTheme
import com.example.ui.viewmodel.LegalNavViewModel
import com.example.ui.viewmodel.ScreenState

class MainActivity : ComponentActivity() {

    private val viewModel: LegalNavViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ApnaWakeelTheme {
                val uiState by viewModel.uiState.collectAsState()
                val snackbarHostState = remember { SnackbarHostState() }
                var showAccountDialog by remember { mutableStateOf(false) }

                // System Back Button Handling
                BackHandler(enabled = uiState.currentScreen != ScreenState.HOME) {
                    when (uiState.currentScreen) {
                        ScreenState.INTAKE -> viewModel.navigateTo(ScreenState.HOME)
                        ScreenState.QUESTIONS -> viewModel.previousQuestion()
                        ScreenState.ANALYZING -> { /* Processing in progress */ }
                        ScreenState.PLAN_RESULT -> viewModel.navigateTo(ScreenState.HOME)
                        ScreenState.SOURCES_DIRECTORY -> viewModel.navigateTo(ScreenState.HOME)
                        ScreenState.SAVED_CASES -> viewModel.navigateTo(ScreenState.HOME)
                        ScreenState.LEGAL_TOPICS -> viewModel.navigateTo(ScreenState.HOME)
                        ScreenState.VOICE_LIVE -> viewModel.navigateTo(ScreenState.HOME)
                        ScreenState.LEGAL_CHAT -> viewModel.navigateTo(ScreenState.HOME)
                        ScreenState.ACCOUNT_SYNC -> viewModel.navigateTo(ScreenState.HOME)
                        ScreenState.HOME -> { /* Exit app */ }
                    }
                }

                val isUrdu = uiState.language == LanguageMode.URDU
                val appTitle = if (isUrdu) "اپنا وکیل" else "Apna Wakeel"
                val appSubtitle = when (uiState.currentScreen) {
                    ScreenState.HOME -> if (isUrdu) "چترال و خیبر پختونخوا قانونی رہنمائی" else "Chitral & KP Legal Navigation"
                    ScreenState.INTAKE -> if (isUrdu) "کیس کا بیان" else "Case Intake"
                    ScreenState.QUESTIONS -> if (isUrdu) "تفہیمی سوالات" else "Case Specific Inquiries"
                    ScreenState.ANALYZING -> if (isUrdu) "قانونی تحقیق" else "Statutory Research"
                    ScreenState.PLAN_RESULT -> if (isUrdu) "قانونی پلان" else "Navigation Plan"
                    ScreenState.SOURCES_DIRECTORY -> if (isUrdu) "سرکاری ذرائع" else "Statutory Sources"
                    ScreenState.SAVED_CASES -> if (isUrdu) "محفوظ کیسز" else "Saved Cases"
                    ScreenState.LEGAL_TOPICS -> if (isUrdu) "کے پی قوانین و موضوعات" else "KP Law Topics"
                    ScreenState.VOICE_LIVE -> if (isUrdu) "وکیل لائیو آواز" else "Live Voice (gemini-3.8-live)"
                    ScreenState.LEGAL_CHAT -> if (isUrdu) "کے پی قانونی طریقہ کار چیٹ" else "KP Legal Procedures Q&A"
                    ScreenState.ACCOUNT_SYNC -> if (isUrdu) "کلاؤڈ سنک" else "Cloud Sync & Auth"
                }

                // Account & Firestore Sync Dialog
                if (showAccountDialog) {
                    AccountSyncDialog(
                        user = uiState.currentUser,
                        isLoading = uiState.isAuthLoading,
                        isSyncing = uiState.isCloudSyncing,
                        syncMessage = uiState.cloudSyncMessage,
                        authError = uiState.authError,
                        savedCasesCount = uiState.savedCases.size,
                        language = uiState.language,
                        onDismiss = { showAccountDialog = false },
                        onSignInWithGoogle = { activity -> viewModel.signInWithGoogle(activity) },
                        onSignInAsGuest = { viewModel.signInAsGuest() },
                        onSignOut = { viewModel.signOut() },
                        onSyncNow = { viewModel.syncCasesWithFirestore(autoDownload = true) }
                    )
                }

                Scaffold(
                    modifier = Modifier.fillMaxSize(),
                    snackbarHost = { SnackbarHost(snackbarHostState) },
                    topBar = {
                        JudicialTopAppBar(
                            title = appTitle,
                            subtitle = appSubtitle,
                            showBackButton = uiState.currentScreen != ScreenState.HOME && uiState.currentScreen != ScreenState.ANALYZING,
                            onBackClick = {
                                when (uiState.currentScreen) {
                                    ScreenState.INTAKE -> viewModel.navigateTo(ScreenState.HOME)
                                    ScreenState.QUESTIONS -> viewModel.previousQuestion()
                                    ScreenState.PLAN_RESULT -> viewModel.navigateTo(ScreenState.HOME)
                                    ScreenState.SOURCES_DIRECTORY -> viewModel.navigateTo(ScreenState.HOME)
                                    ScreenState.SAVED_CASES -> viewModel.navigateTo(ScreenState.HOME)
                                    ScreenState.LEGAL_TOPICS -> viewModel.navigateTo(ScreenState.HOME)
                                    ScreenState.VOICE_LIVE -> viewModel.navigateTo(ScreenState.HOME)
                                    ScreenState.LEGAL_CHAT -> viewModel.navigateTo(ScreenState.HOME)
                                    ScreenState.ACCOUNT_SYNC -> viewModel.navigateTo(ScreenState.HOME)
                                    else -> {}
                                }
                            },
                            language = uiState.language,
                            isUserLoggedIn = uiState.currentUser != null,
                            onToggleLanguage = { viewModel.toggleLanguage() },
                            onLegalTopicsClick = { viewModel.navigateTo(ScreenState.LEGAL_TOPICS) },
                            onVoiceLiveClick = { viewModel.navigateTo(ScreenState.VOICE_LIVE) },
                            onChatClick = { viewModel.navigateTo(ScreenState.LEGAL_CHAT) },
                            onSourcesClick = { viewModel.navigateTo(ScreenState.SOURCES_DIRECTORY) },
                            onSavedCasesClick = { viewModel.navigateTo(ScreenState.SAVED_CASES) },
                            onAccountClick = { showAccountDialog = true }
                        )
                    }
                ) { innerPadding ->
                    Box(modifier = Modifier.fillMaxSize().padding(innerPadding)) {
                        when (uiState.currentScreen) {
                            ScreenState.HOME -> {
                                HomeScreen(
                                    language = uiState.language,
                                    isUserLoggedIn = uiState.currentUser != null,
                                    onStartCaseClick = { viewModel.navigateTo(ScreenState.INTAKE) },
                                    onVoiceLiveClick = { viewModel.navigateTo(ScreenState.VOICE_LIVE) },
                                    onChatClick = { viewModel.navigateTo(ScreenState.LEGAL_CHAT) },
                                    onAccountClick = { showAccountDialog = true },
                                    onSelectScenario = { problem, loc ->
                                        viewModel.selectSampleCase(problem, loc)
                                    },
                                    onBrowseTopicsClick = { viewModel.navigateTo(ScreenState.LEGAL_TOPICS) },
                                    onBrowseSourcesClick = { viewModel.navigateTo(ScreenState.SOURCES_DIRECTORY) }
                                )
                            }
                            ScreenState.INTAKE -> {
                                CaseIntakeScreen(
                                    language = uiState.language,
                                    problemInput = uiState.problemInput,
                                    selectedLocation = uiState.selectedLocation,
                                    errorMessage = uiState.errorMessage,
                                    isRecording = uiState.isAudioRecording,
                                    recordingDurationSeconds = uiState.audioDurationSeconds,
                                    isTranscribing = uiState.isTranscribing,
                                    transcriptionError = uiState.transcriptionError,
                                    onProblemChange = { viewModel.updateProblemInput(it) },
                                    onLocationChange = { viewModel.updateLocation(it) },
                                    onStartRecording = { viewModel.startAudioRecording() },
                                    onStopRecording = { viewModel.stopAudioRecordingAndTranscribe() },
                                    onCancelRecording = { viewModel.cancelAudioRecording() },
                                    onNavigateToLiveVoice = { viewModel.navigateTo(ScreenState.VOICE_LIVE) },
                                    onSubmit = { viewModel.startIntake() }
                                )
                            }
                            ScreenState.QUESTIONS -> {
                                FollowUpQuestionsScreen(
                                    language = uiState.language,
                                    questions = uiState.questions,
                                    currentIndex = uiState.currentQuestionIndex,
                                    userAnswers = uiState.userAnswers,
                                    onAnswerSelected = { qId, optId ->
                                        viewModel.answerQuestion(qId, optId)
                                    },
                                    onBackClick = { viewModel.previousQuestion() }
                                )
                            }
                            ScreenState.ANALYZING -> {
                                AnalyzingScreen(
                                    language = uiState.language,
                                    stages = uiState.analyzingStages
                                )
                            }
                            ScreenState.PLAN_RESULT -> {
                                val plan = uiState.finalPlan
                                if (plan != null) {
                                    LegalPlanResultScreen(
                                        language = uiState.language,
                                        plan = plan,
                                        isBookmarked = uiState.isBookmarked,
                                        aiNote = uiState.aiNote,
                                        onToggleBookmark = { viewModel.toggleBookmark() },
                                        onStartNewCase = {
                                            viewModel.updateProblemInput("")
                                            viewModel.navigateTo(ScreenState.INTAKE)
                                        }
                                    )
                                }
                            }
                            ScreenState.SOURCES_DIRECTORY -> {
                                SourcesDirectoryScreen(
                                    language = uiState.language
                                )
                            }
                            ScreenState.SAVED_CASES -> {
                                CaseHistoryScreen(
                                    language = uiState.language,
                                    cases = uiState.savedCases,
                                    onSelectCase = { caseEntity ->
                                        viewModel.loadSavedCase(caseEntity)
                                    },
                                    onDeleteCase = { caseId ->
                                        viewModel.deleteSavedCase(caseId)
                                    }
                                )
                            }
                            ScreenState.LEGAL_TOPICS -> {
                                LegalTopicsScreen(
                                    language = uiState.language,
                                    categories = uiState.legalCategories,
                                    topics = uiState.legalTopics,
                                    selectedCategoryId = uiState.selectedCategoryId,
                                    searchQuery = uiState.topicSearchQuery,
                                    selectedTopicDetail = uiState.selectedTopicDetail,
                                    showBookmarkedOnly = uiState.showBookmarkedTopicsOnly,
                                    onCategorySelected = { viewModel.selectTopicCategory(it) },
                                    onSearchQueryChanged = { viewModel.updateTopicSearchQuery(it) },
                                    onTopicSelected = { viewModel.selectTopicDetail(it) },
                                    onToggleBookmark = { viewModel.toggleTopicBookmark(it) },
                                    onToggleBookmarkedOnly = { viewModel.toggleBookmarkedTopicsFilter() }
                                )
                            }
                            ScreenState.VOICE_LIVE -> {
                                VoiceConversationScreen(
                                    language = uiState.language,
                                    liveState = uiState.liveState,
                                    liveTurns = uiState.liveTurns,
                                    liveStatusMessage = uiState.liveStatusMessage,
                                    isTtsEnabled = uiState.isLiveTtsEnabled,
                                    isRecording = uiState.isAudioRecording,
                                    recordingDurationSeconds = uiState.audioDurationSeconds,
                                    audioAmplitude = uiState.audioAmplitude,
                                    isTranscribing = uiState.isTranscribing,
                                    onBackClick = { viewModel.navigateTo(ScreenState.HOME) },
                                    onStartRecording = { viewModel.startAudioRecording() },
                                    onStopRecordingAndSend = { viewModel.startLiveRecordingAndSend() },
                                    onCancelRecording = { viewModel.cancelAudioRecording() },
                                    onSendTextMessage = { viewModel.sendLiveVoiceMessage(it) },
                                    onToggleTts = { viewModel.toggleLiveTts() },
                                    onClearConversation = { viewModel.clearLiveVoiceHistory() },
                                    onCreateCaseFromConversation = { viewModel.createCaseFromLiveConversation() }
                                )
                            }
                            ScreenState.LEGAL_CHAT -> {
                                LegalChatScreen(
                                    language = uiState.language,
                                    messages = uiState.chatMessages,
                                    isLoading = uiState.isChatLoading,
                                    isRecording = uiState.isAudioRecording,
                                    onBackClick = { viewModel.navigateTo(ScreenState.HOME) },
                                    onSendMessage = { viewModel.sendChatMessage(it) },
                                    onStartVoiceInput = { viewModel.startChatVoiceRecording() },
                                    onStopVoiceInput = { viewModel.stopChatVoiceRecording() },
                                    onClearChat = { viewModel.clearLegalChat() },
                                    onCreateCaseFromMessage = { viewModel.createCaseFromChatMessage(it) }
                                )
                            }
                            ScreenState.ACCOUNT_SYNC -> {
                                // Direct to account sheet dialog and fallback to HOME
                                showAccountDialog = true
                                viewModel.navigateTo(ScreenState.HOME)
                            }
                        }
                    }
                }
            }
        }
    }
}
