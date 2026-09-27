package com.example.data.models

enum class LanguageMode {
    ENGLISH,
    URDU
}

enum class LegalCategory(val titleEn: String, val titleUr: String) {
    TRAFFIC("Traffic & Vehicle", "ٹریفک اور گاڑیاں"),
    DOMICILE("Domicile & Residence", "ڈومیسائل اور رہائش"),
    CNIC("CNIC & Identity (NADRA)", "شناختی کارڈ اور نادرا"),
    LAND_PROPERTY("Land & Property", "زمین اور جائیداد"),
    CYBER_HARASSMENT("Cyber Harassment & Blackmail", "آن لائن ہراسانی اور بلیک میل"),
    FRAUD("Fraud & Financial Deception", "دھوکہ دہی اور مالیاتی فراڈ"),
    EMPLOYMENT("Employment & Workplace", "ملازمت اور پیشہ ورانہ تنازعات"),
    GENERAL("General Legal Navigation", "عام قانونی رہنمائی")
}

enum class UrgencyLevel {
    NORMAL,
    ELEVATED,
    EMERGENCY
}

data class QuestionOption(
    val id: String,
    val labelEn: String,
    val labelUr: String,
    val descriptionEn: String? = null,
    val descriptionUr: String? = null
)

data class FollowUpQuestion(
    val id: String,
    val questionEn: String,
    val questionUr: String,
    val hintEn: String? = null,
    val hintUr: String? = null,
    val options: List<QuestionOption>,
    val category: LegalCategory
)

data class CaseFacts(
    val location: String = "Chitral",
    val province: String = "Khyber Pakhtunkhwa",
    val extractedSummary: String = "",
    val detectedUrgency: UrgencyLevel = UrgencyLevel.NORMAL,
    val isSafetyCritical: Boolean = false,
    val hasInjury: Boolean = false,
    val hasPoliceInvolvement: Boolean = false,
    val isVehicleDetained: Boolean = false,
    val hasFirRegistered: Boolean = false,
    val disputeParties: String = "",
    val keyItems: List<String> = emptyList(),
    val missingInformation: List<String> = emptyList()
)

data class ResearchPlan(
    val researchQuestions: List<String>,
    val jurisdictionsToCheck: List<String>,
    val sourceTypesNeeded: List<String>,
    val applicableStatutes: List<String>,
    val searchQueries: List<String>
)

data class VerifiedEvidence(
    val claim: String,
    val sourceName: String,
    val sourceUrl: String,
    val sourceType: String,
    val jurisdiction: String,
    val verified: Boolean,
    val supportingText: String,
    val confidence: String
)

data class ActionStep(
    val stepNumber: Int,
    val titleEn: String,
    val titleUr: String,
    val descriptionEn: String,
    val descriptionUr: String,
    val isMandatory: Boolean = true,
    val authorityOrDesk: String? = null
)

data class AuthorityContact(
    val nameEn: String,
    val nameUr: String,
    val departmentEn: String,
    val departmentUr: String,
    val jurisdiction: String,
    val officeAddressChitral: String,
    val procedureSummary: String,
    val procedureSummaryUr: String,
    val officialSource: String,
    val helpline: String? = null
)

data class OfficialSource(
    val id: String,
    val title: String,
    val titleUr: String = "",
    val authority: String,
    val jurisdiction: String,
    val category: String,
    val officialUrl: String,
    val verificationBadge: String = "Verified Official Source",
    val description: String
)

data class FinalLegalPlan(
    val caseId: String,
    val originalProblem: String,
    val caseSummaryEn: String,
    val caseSummaryUr: String,
    val legalArea: LegalCategory,
    val subCategory: String,
    val urgency: UrgencyLevel,
    val isEmergency: Boolean,
    val emergencyNoticeEn: String? = null,
    val emergencyNoticeUr: String? = null,
    val responsibleAuthority: AuthorityContact,
    val actionSteps: List<ActionStep>,
    val documentsRequired: List<String>,
    val documentsRequiredUr: List<String> = emptyList(),
    val evidenceToPreserve: List<String>,
    val evidenceToPreserveUr: List<String> = emptyList(),
    val timelineGuidance: String,
    val timelineGuidanceUr: String = "",
    val hasStrictDeadline: Boolean,
    val verifiedSources: List<OfficialSource>,
    val uncertainties: List<String>,
    val uncertaintiesUr: List<String> = emptyList(),
    val needsProfessionalLawyer: Boolean,
    val lawyerConsultationAdvice: String,
    val lawyerConsultationAdviceUr: String = "",
    val createdAtTimestamp: Long = System.currentTimeMillis()
)
