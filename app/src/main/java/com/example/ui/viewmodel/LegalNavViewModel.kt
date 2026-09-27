package com.example.ui.viewmodel

import android.app.Activity
import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.agents.*
import com.example.data.ai.FirebaseAIChatService
import com.example.data.ai.GeminiLegalService
import com.example.data.ai.GeminiLiveVoiceService
import com.example.data.ai.GeminiTranscriptionService
import com.example.data.ai.LegalChatMessage
import com.example.data.ai.LiveConnectionState
import com.example.data.ai.LiveTurn
import com.example.data.ai.MessageSender
import com.example.data.audio.AudioRecordingManager
import com.example.data.auth.FirebaseAuthService
import com.example.data.cloud.FirestoreSyncService
import com.example.data.local.AppDatabase
import com.example.data.local.CaseEntity
import com.example.data.local.LegalCategoryEntity
import com.example.data.local.LegalTopicEntity
import com.example.data.local.LegalTopicRepository
import com.example.data.models.*
import com.google.firebase.auth.FirebaseUser
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch
import java.util.UUID

enum class ScreenState {
    HOME,
    INTAKE,
    QUESTIONS,
    ANALYZING,
    PLAN_RESULT,
    SOURCES_DIRECTORY,
    SAVED_CASES,
    LEGAL_TOPICS,
    VOICE_LIVE,
    ACCOUNT_SYNC,
    LEGAL_CHAT
}

data class AnalyzingStage(
    val title: String,
    val titleUr: String,
    val isCompleted: Boolean,
    val isCurrent: Boolean
)

data class LegalNavUiState(
    val currentScreen: ScreenState = ScreenState.HOME,
    val language: LanguageMode = LanguageMode.ENGLISH,
    val caseId: String = "",
    val problemInput: String = "",
    val selectedLocation: String = "Chitral (Lower)",
    val selectedProvince: String = "Khyber Pakhtunkhwa",
    val detectedCategory: LegalCategory? = null,
    val subCategory: String = "",
    val caseFacts: CaseFacts? = null,
    val questions: List<FollowUpQuestion> = emptyList(),
    val currentQuestionIndex: Int = 0,
    val userAnswers: Map<String, String> = emptyMap(),
    val analyzingStages: List<AnalyzingStage> = emptyList(),
    val finalPlan: FinalLegalPlan? = null,
    val savedCases: List<CaseEntity> = emptyList(),
    val isBookmarked: Boolean = false,
    val isAiEnriched: Boolean = false,
    val aiNote: String? = null,
    val errorMessage: String? = null,
    // KP Legal Topics Room Database State
    val legalCategories: List<LegalCategoryEntity> = emptyList(),
    val legalTopics: List<LegalTopicEntity> = emptyList(),
    val selectedCategoryId: String? = null,
    val topicSearchQuery: String = "",
    val selectedTopicDetail: LegalTopicEntity? = null,
    val showBookmarkedTopicsOnly: Boolean = false,
    // Firebase Auth & Cloud Firestore State
    val currentUser: FirebaseUser? = null,
    val isAuthLoading: Boolean = false,
    val authError: String? = null,
    val isCloudSyncing: Boolean = false,
    val cloudSyncMessage: String? = null,
    // Audio Transcription (gemini-3.5-transcribe)
    val isAudioRecording: Boolean = false,
    val audioDurationSeconds: Int = 0,
    val audioAmplitude: Float = 0f,
    val isTranscribing: Boolean = false,
    val transcriptionError: String? = null,
    // Live Voice (gemini-3.8-live)
    val liveState: LiveConnectionState = LiveConnectionState.IDLE,
    val liveTurns: List<LiveTurn> = emptyList(),
    val liveStatusMessage: String? = "Ready for voice consultation",
    val isLiveTtsEnabled: Boolean = true,
    // Firebase AI SDK Chat (KP Legal Procedures)
    val chatMessages: List<LegalChatMessage> = emptyList(),
    val isChatLoading: Boolean = false,
    val chatError: String? = null
)

class LegalNavViewModel(application: Application) : AndroidViewModel(application) {

    private val db = AppDatabase.getDatabase(application)
    private val caseDao = db.caseDao()
    val topicRepository = LegalTopicRepository(db.legalTopicDao())

    // Feature Services
    val authService = FirebaseAuthService(application)
    val firestoreService = FirestoreSyncService()
    val audioRecordingManager = AudioRecordingManager(application)
    val transcriptionService = GeminiTranscriptionService()
    val liveVoiceService = GeminiLiveVoiceService(application)
    val firebaseAIChatService = FirebaseAIChatService(application)

    private val _uiState = MutableStateFlow(LegalNavUiState())
    val uiState: StateFlow<LegalNavUiState> = _uiState.asStateFlow()

    init {
        observeSavedCases()
        initLegalTopics()
        observeAuth()
        observeAudioRecording()
        observeLiveVoice()
    }

    private fun observeAuth() {
        viewModelScope.launch {
            authService.currentUser.collectLatest { user ->
                _uiState.value = _uiState.value.copy(currentUser = user)
                if (user != null) {
                    syncCasesWithFirestore(autoDownload = true)
                }
            }
        }
        viewModelScope.launch {
            authService.isLoading.collectLatest { loading ->
                _uiState.value = _uiState.value.copy(isAuthLoading = loading)
            }
        }
        viewModelScope.launch {
            authService.authError.collectLatest { err ->
                _uiState.value = _uiState.value.copy(authError = err)
            }
        }
    }

    private fun observeAudioRecording() {
        viewModelScope.launch {
            audioRecordingManager.isRecording.collectLatest { rec ->
                _uiState.value = _uiState.value.copy(isAudioRecording = rec)
            }
        }
        viewModelScope.launch {
            audioRecordingManager.recordingDurationSeconds.collectLatest { sec ->
                _uiState.value = _uiState.value.copy(audioDurationSeconds = sec)
            }
        }
        viewModelScope.launch {
            audioRecordingManager.currentAmplitude.collectLatest { amp ->
                _uiState.value = _uiState.value.copy(audioAmplitude = amp)
            }
        }
    }

    private fun observeLiveVoice() {
        viewModelScope.launch {
            liveVoiceService.connectionState.collectLatest { state ->
                _uiState.value = _uiState.value.copy(liveState = state)
            }
        }
        viewModelScope.launch {
            liveVoiceService.conversationHistory.collectLatest { turns ->
                _uiState.value = _uiState.value.copy(liveTurns = turns)
            }
        }
        viewModelScope.launch {
            liveVoiceService.statusMessage.collectLatest { msg ->
                _uiState.value = _uiState.value.copy(liveStatusMessage = msg)
            }
        }
        viewModelScope.launch {
            liveVoiceService.isTtsEnabled.collectLatest { ttsOn ->
                _uiState.value = _uiState.value.copy(isLiveTtsEnabled = ttsOn)
            }
        }
    }

    private fun initLegalTopics() {
        viewModelScope.launch {
            // Pre-seed default KP topics if empty
            topicRepository.seedDatabaseIfEmpty()

            // Observe Categories
            launch {
                topicRepository.allCategories.collectLatest { categories ->
                    _uiState.value = _uiState.value.copy(legalCategories = categories)
                }
            }

            // Observe Topics
            launch {
                observeTopics()
            }
        }
    }

    private suspend fun observeTopics() {
        topicRepository.allTopics.collectLatest { topics ->
            _uiState.value = _uiState.value.copy(legalTopics = topics)
        }
    }

    private fun observeSavedCases() {
        viewModelScope.launch {
            caseDao.getAllCases().collectLatest { cases ->
                _uiState.value = _uiState.value.copy(savedCases = cases)
            }
        }
    }

    fun toggleLanguage() {
        val nextLang = if (_uiState.value.language == LanguageMode.ENGLISH) {
            LanguageMode.URDU
        } else {
            LanguageMode.ENGLISH
        }
        _uiState.value = _uiState.value.copy(language = nextLang)
    }

    fun navigateTo(screen: ScreenState) {
        _uiState.value = _uiState.value.copy(currentScreen = screen, errorMessage = null)
    }

    fun updateProblemInput(input: String) {
        _uiState.value = _uiState.value.copy(problemInput = input)
    }

    fun updateLocation(location: String) {
        _uiState.value = _uiState.value.copy(selectedLocation = location)
    }

    fun selectSampleCase(sampleProblem: String, location: String = "Chitral (Lower)") {
        _uiState.value = _uiState.value.copy(
            problemInput = sampleProblem,
            selectedLocation = location,
            currentScreen = ScreenState.INTAKE
        )
    }

    fun startIntake() {
        val text = _uiState.value.problemInput.trim()
        if (text.isBlank()) {
            _uiState.value = _uiState.value.copy(
                errorMessage = if (_uiState.value.language == LanguageMode.URDU)
                    "براہ کرم اپنے مسئلے کی تفصیل درج کریں۔"
                else
                    "Please describe your legal situation before continuing."
            )
            return
        }

        val caseId = "CASE-${UUID.randomUUID().toString().take(8).uppercase()}"
        Log.d("ApnaWakeel", "[CASE] $caseId created")

        // 1. Intake Agent Fact Extraction
        val intakeResult = IntakeAgent.processIntake(
            problem = text,
            province = _uiState.value.selectedProvince,
            location = _uiState.value.selectedLocation
        )
        Log.d("ApnaWakeel", "[INTAKE] Category identified: ${intakeResult.category}, Sub: ${intakeResult.subCategory}")

        // 2. Orchestration & Specialist Question Generation
        val questions = OrchestrationAgent.getQuestions(
            category = intakeResult.category,
            facts = intakeResult.facts,
            subCategory = intakeResult.subCategory
        )
        Log.d("ApnaWakeel", "[ROUTER] Generated ${questions.size} tailored follow-up questions")

        _uiState.value = _uiState.value.copy(
            caseId = caseId,
            detectedCategory = intakeResult.category,
            subCategory = intakeResult.subCategory,
            caseFacts = intakeResult.facts,
            questions = questions,
            currentQuestionIndex = 0,
            userAnswers = emptyMap(),
            currentScreen = if (questions.isNotEmpty()) ScreenState.QUESTIONS else ScreenState.ANALYZING,
            errorMessage = null
        )

        if (questions.isEmpty()) {
            startAnalysis()
        }
    }

    fun answerQuestion(questionId: String, optionId: String) {
        val currentAnswers = _uiState.value.userAnswers.toMutableMap()
        currentAnswers[questionId] = optionId
        _uiState.value = _uiState.value.copy(userAnswers = currentAnswers)

        val nextIndex = _uiState.value.currentQuestionIndex + 1
        if (nextIndex < _uiState.value.questions.size) {
            _uiState.value = _uiState.value.copy(currentQuestionIndex = nextIndex)
        } else {
            startAnalysis()
        }
    }

    fun previousQuestion() {
        val prevIndex = _uiState.value.currentQuestionIndex - 1
        if (prevIndex >= 0) {
            _uiState.value = _uiState.value.copy(currentQuestionIndex = prevIndex)
        } else {
            _uiState.value = _uiState.value.copy(currentScreen = ScreenState.INTAKE)
        }
    }

    fun startAnalysis() {
        _uiState.value = _uiState.value.copy(currentScreen = ScreenState.ANALYZING)

        val stages = listOf(
            AnalyzingStage("Fact intake and emergency check completed", "حقائق کا ادراک اور ہنگامی نوعیت کی جانچ مکمل", false, true),
            AnalyzingStage("Routing to ${ _uiState.value.detectedCategory?.titleEn ?: "Specialist"} Agent", "متعلقہ ماہر قانونی ایجنٹ سے رابطہ", false, false),
            AnalyzingStage("Retrieving official KP Code & Chitral sources", "خیبر پختونخوا کوڈ اور چترال کے سرکاری قوانین کی تلاش", false, false),
            AnalyzingStage("Verifying evidence and statutory authorities", "سرکاری شواہد اور متعلقہ محکموں کی تصدیق", false, false),
            AnalyzingStage("Synthesizing step-by-step legal navigation plan", "عملی قانونی رہنمائی اور ایکشن پلان کی تیاری", false, false)
        )

        _uiState.value = _uiState.value.copy(analyzingStages = stages)

        viewModelScope.launch {
            // Animate realistic stages
            for (i in 0 until stages.size) {
                delay(650)
                val updated = _uiState.value.analyzingStages.mapIndexed { index, stage ->
                    when {
                        index < i -> stage.copy(isCompleted = true, isCurrent = false)
                        index == i -> stage.copy(isCompleted = false, isCurrent = true)
                        else -> stage.copy(isCompleted = false, isCurrent = false)
                    }
                }
                _uiState.value = _uiState.value.copy(analyzingStages = updated)
            }

            delay(500)
            val finalStages = _uiState.value.analyzingStages.map { it.copy(isCompleted = true, isCurrent = false) }
            _uiState.value = _uiState.value.copy(analyzingStages = finalStages)

            // Build Legal Plan
            val facts = _uiState.value.caseFacts ?: CaseFacts()
            val category = _uiState.value.detectedCategory ?: LegalCategory.GENERAL
            val plan = LegalPlanBuilder.buildPlan(
                caseId = _uiState.value.caseId,
                originalProblem = _uiState.value.problemInput,
                category = category,
                subCategory = _uiState.value.subCategory,
                facts = facts,
                answers = _uiState.value.userAnswers
            )

            // Optional live Gemini check
            var aiNote: String? = null
            if (GeminiLegalService.isConfigured()) {
                aiNote = GeminiLegalService.enhanceGuidance(
                    userProblem = _uiState.value.problemInput,
                    category = category,
                    facts = facts,
                    location = _uiState.value.selectedLocation
                )
            }

            // Persist to Room
            val entity = CaseEntity(
                caseId = plan.caseId,
                originalProblem = plan.originalProblem,
                location = _uiState.value.selectedLocation,
                province = _uiState.value.selectedProvince,
                language = _uiState.value.language.name,
                category = plan.legalArea.name,
                subCategory = plan.subCategory,
                urgency = plan.urgency.name,
                isEmergency = plan.isEmergency,
                summaryEn = plan.caseSummaryEn,
                summaryUr = plan.caseSummaryUr,
                authorityName = plan.responsibleAuthority.nameEn,
                authorityAddress = plan.responsibleAuthority.officeAddressChitral,
                stepsCount = plan.actionSteps.size,
                createdAtTimestamp = plan.createdAtTimestamp,
                isBookmarked = false
            )
            caseDao.insertCase(entity)

            // Auto-sync to Firestore if logged in
            _uiState.value.currentUser?.let { user ->
                firestoreService.saveCaseToCloud(user.uid, entity)
            }

            _uiState.value = _uiState.value.copy(
                finalPlan = plan,
                aiNote = aiNote,
                isAiEnriched = aiNote != null,
                currentScreen = ScreenState.PLAN_RESULT
            )
        }
    }

    fun toggleBookmark() {
        val plan = _uiState.value.finalPlan ?: return
        val newStatus = !_uiState.value.isBookmarked
        _uiState.value = _uiState.value.copy(isBookmarked = newStatus)
        viewModelScope.launch {
            caseDao.updateBookmark(plan.caseId, newStatus)
            _uiState.value.currentUser?.let { user ->
                val entity = caseDao.getCaseById(plan.caseId)
                if (entity != null) {
                    firestoreService.saveCaseToCloud(user.uid, entity)
                }
            }
        }
    }

    fun loadSavedCase(caseEntity: CaseEntity) {
        val category = try {
            LegalCategory.valueOf(caseEntity.category)
        } catch (e: Exception) {
            LegalCategory.GENERAL
        }

        val facts = CaseFacts(
            location = caseEntity.location,
            province = caseEntity.province,
            isSafetyCritical = caseEntity.isEmergency,
            detectedUrgency = try { UrgencyLevel.valueOf(caseEntity.urgency) } catch (e: Exception) { UrgencyLevel.NORMAL }
        )

        val plan = LegalPlanBuilder.buildPlan(
            caseId = caseEntity.caseId,
            originalProblem = caseEntity.originalProblem,
            category = category,
            subCategory = caseEntity.subCategory,
            facts = facts,
            answers = emptyMap()
        )

        _uiState.value = _uiState.value.copy(
            caseId = caseEntity.caseId,
            problemInput = caseEntity.originalProblem,
            selectedLocation = caseEntity.location,
            detectedCategory = category,
            subCategory = caseEntity.subCategory,
            caseFacts = facts,
            finalPlan = plan,
            isBookmarked = caseEntity.isBookmarked,
            currentScreen = ScreenState.PLAN_RESULT
        )
    }

    fun deleteSavedCase(caseId: String) {
        viewModelScope.launch {
            caseDao.deleteCase(caseId)
            _uiState.value.currentUser?.let { user ->
                firestoreService.deleteCaseFromCloud(user.uid, caseId)
            }
        }
    }

    fun selectTopicCategory(categoryId: String?) {
        _uiState.value = _uiState.value.copy(
            selectedCategoryId = categoryId,
            selectedTopicDetail = null
        )
    }

    fun updateTopicSearchQuery(query: String) {
        _uiState.value = _uiState.value.copy(topicSearchQuery = query)
    }

    fun selectTopicDetail(topic: LegalTopicEntity?) {
        _uiState.value = _uiState.value.copy(selectedTopicDetail = topic)
    }

    fun toggleTopicBookmark(topic: LegalTopicEntity) {
        viewModelScope.launch {
            val newStatus = !topic.isBookmarked
            topicRepository.toggleBookmark(topic.topicId, newStatus)
            if (_uiState.value.selectedTopicDetail?.topicId == topic.topicId) {
                _uiState.value = _uiState.value.copy(
                    selectedTopicDetail = topic.copy(isBookmarked = newStatus)
                )
            }
        }
    }

    fun toggleBookmarkedTopicsFilter() {
        _uiState.value = _uiState.value.copy(
            showBookmarkedTopicsOnly = !_uiState.value.showBookmarkedTopicsOnly
        )
    }

    // ==========================================
    // FIREBASE AUTH & GOOGLE SIGN-IN ACTIONS
    // ==========================================

    fun signInWithGoogle(activity: Activity) {
        viewModelScope.launch {
            authService.signInWithGoogle(activity)
        }
    }

    fun signInAsGuest() {
        viewModelScope.launch {
            authService.signInAnonymously("Guest Citizen")
        }
    }

    fun signOut() {
        authService.signOut()
        _uiState.value = _uiState.value.copy(cloudSyncMessage = "Signed out")
    }

    fun clearAuthError() {
        authService.clearError()
    }

    // ==========================================
    // FIRESTORE CLOUD PERSISTENCE ACTIONS
    // ==========================================

    fun syncCasesWithFirestore(autoDownload: Boolean = false) {
        val user = _uiState.value.currentUser ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isCloudSyncing = true,
                cloudSyncMessage = "Syncing with Firestore..."
            )

            try {
                // 1. Upload local cases to Firestore
                val localCases = _uiState.value.savedCases
                for (case in localCases) {
                    firestoreService.saveCaseToCloud(user.uid, case)
                }

                // 2. Download any cloud cases if autoDownload or restore requested
                val cloudCases = firestoreService.fetchCloudCases(user.uid)
                for (cCase in cloudCases) {
                    caseDao.insertCase(cCase)
                }

                _uiState.value = _uiState.value.copy(
                    isCloudSyncing = false,
                    cloudSyncMessage = "Synced ${localCases.size.coerceAtLeast(cloudCases.size)} cases with Firestore"
                )
            } catch (e: Exception) {
                _uiState.value = _uiState.value.copy(
                    isCloudSyncing = false,
                    cloudSyncMessage = "Sync notice: ${e.localizedMessage ?: "Offline"}"
                )
            }
        }
    }

    // ==========================================
    // AUDIO TRANSCRIPTION (gemini-3.5-transcribe)
    // ==========================================

    fun startAudioRecording(): Boolean {
        _uiState.value = _uiState.value.copy(transcriptionError = null)
        return audioRecordingManager.startRecording()
    }

    fun stopAudioRecordingAndTranscribe(
        onSuccess: ((String) -> Unit)? = null
    ) {
        viewModelScope.launch {
            val result = audioRecordingManager.stopRecording()
            if (result == null) {
                _uiState.value = _uiState.value.copy(
                    transcriptionError = "No audio captured. Please speak into the microphone."
                )
                return@launch
            }

            _uiState.value = _uiState.value.copy(
                isTranscribing = true,
                transcriptionError = null
            )

            val transcriptionResult = transcriptionService.transcribeAudio(
                base64AudioData = result.base64Data,
                mimeType = result.mimeType
            )

            transcriptionResult.fold(
                onSuccess = { text ->
                    _uiState.value = _uiState.value.copy(
                        isTranscribing = false,
                        transcriptionError = null
                    )
                    // If callback provided, use it, else update problemInput directly
                    if (onSuccess != null) {
                        onSuccess(text)
                    } else {
                        val currentText = _uiState.value.problemInput
                        val newText = if (currentText.isBlank()) text else "$currentText $text"
                        updateProblemInput(newText)
                    }
                },
                onFailure = { err ->
                    _uiState.value = _uiState.value.copy(
                        isTranscribing = false,
                        transcriptionError = err.localizedMessage ?: "Transcription failed"
                    )
                }
            )
        }
    }

    fun cancelAudioRecording() {
        audioRecordingManager.cancelRecording()
    }

    // ==========================================
    // LIVE VOICE CONVERSATIONS (gemini-3.8-live)
    // ==========================================

    fun sendLiveVoiceMessage(text: String) {
        viewModelScope.launch {
            liveVoiceService.sendConversationTurn(text)
        }
    }

    fun startLiveRecordingAndSend() {
        viewModelScope.launch {
            val result = audioRecordingManager.stopRecording()
            if (result != null) {
                // Transcribe with gemini-3.5-transcribe then send to gemini-3.8-live
                _uiState.value = _uiState.value.copy(isTranscribing = true)
                val transResult = transcriptionService.transcribeAudio(
                    base64AudioData = result.base64Data,
                    mimeType = result.mimeType
                )
                _uiState.value = _uiState.value.copy(isTranscribing = false)

                transResult.onSuccess { spokenText ->
                    sendLiveVoiceMessage(spokenText)
                }.onFailure { err ->
                    _uiState.value = _uiState.value.copy(
                        transcriptionError = "Voice intake: ${err.message}"
                    )
                }
            }
        }
    }

    fun toggleLiveTts() {
        liveVoiceService.toggleTts()
    }

    fun stopLiveSpeaking() {
        liveVoiceService.stopSpeaking()
    }

    fun clearLiveVoiceHistory() {
        liveVoiceService.clearConversation()
    }

    fun createCaseFromLiveConversation() {
        val turns = _uiState.value.liveTurns
        val userMessages = turns.filter { it.role == "user" }.joinToString(". ") { it.text }
        if (userMessages.isNotBlank()) {
            _uiState.value = _uiState.value.copy(
                problemInput = userMessages,
                currentScreen = ScreenState.INTAKE
            )
        } else {
            navigateTo(ScreenState.INTAKE)
        }
    }

    // ==========================================
    // FIREBASE AI SDK CHAT (KP LEGAL PROCEDURES)
    // ==========================================

    fun sendChatMessage(userText: String) {
        if (userText.isBlank()) return
        val userMsg = LegalChatMessage(sender = MessageSender.USER, text = userText.trim())
        val updatedList = _uiState.value.chatMessages + userMsg
        _uiState.value = _uiState.value.copy(
            chatMessages = updatedList,
            isChatLoading = true,
            chatError = null
        )

        viewModelScope.launch {
            val result = firebaseAIChatService.sendMessage(userText.trim())
            result.fold(
                onSuccess = { answer ->
                    val aiMsg = LegalChatMessage(sender = MessageSender.AI_COUNSEL, text = answer)
                    _uiState.value = _uiState.value.copy(
                        chatMessages = _uiState.value.chatMessages + aiMsg,
                        isChatLoading = false
                    )
                },
                onFailure = { err ->
                    val fallbackMsg = LegalChatMessage(
                        sender = MessageSender.AI_COUNSEL,
                        text = "Regarding KP legal procedures: under Khyber Pakhtunkhwa statutes, you should present this matter directly to the competent authority (e.g. Police Station for FIR/CrPC 154, Family Court for Khula, or Tehsildar for Section 135 land partition). (Notice: ${err.localizedMessage})"
                    )
                    _uiState.value = _uiState.value.copy(
                        chatMessages = _uiState.value.chatMessages + fallbackMsg,
                        isChatLoading = false,
                        chatError = err.localizedMessage
                    )
                }
            )
        }
    }

    fun clearLegalChat() {
        firebaseAIChatService.resetChat()
        _uiState.value = _uiState.value.copy(
            chatMessages = emptyList(),
            isChatLoading = false,
            chatError = null
        )
    }

    fun createCaseFromChatMessage(chatMessageText: String) {
        _uiState.value = _uiState.value.copy(
            problemInput = chatMessageText.take(500),
            currentScreen = ScreenState.INTAKE
        )
    }

    fun startChatVoiceRecording(): Boolean {
        return audioRecordingManager.startRecording()
    }

    fun stopChatVoiceRecording() {
        viewModelScope.launch {
            val result = audioRecordingManager.stopRecording()
            if (result != null) {
                _uiState.value = _uiState.value.copy(isTranscribing = true)
                val transResult = transcriptionService.transcribeAudio(result.base64Data, result.mimeType)
                _uiState.value = _uiState.value.copy(isTranscribing = false)
                transResult.onSuccess { spokenText ->
                    sendChatMessage(spokenText)
                }
            }
        }
    }

    override fun onCleared() {
        super.onCleared()
        liveVoiceService.release()
    }
}
