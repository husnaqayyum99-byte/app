package com.example.data.agents

import com.example.data.models.CaseFacts
import com.example.data.models.LegalCategory
import com.example.data.models.UrgencyLevel

object IntakeAgent {

    data class IntakeResult(
        val category: LegalCategory,
        val subCategory: String,
        val facts: CaseFacts
    )

    fun processIntake(
        problem: String,
        province: String = "Khyber Pakhtunkhwa",
        location: String = "Chitral"
    ): IntakeResult {
        val lowerText = problem.lowercase()

        // 1. Emergency & Safety Detection
        val isEmergency = detectImmediateDanger(lowerText)
        val urgency = if (isEmergency) UrgencyLevel.EMERGENCY else detectUrgency(lowerText)

        // 2. Fact Extraction
        val hasInjury = lowerText.contains("injury") || lowerText.contains("injured") ||
                lowerText.contains("zakhmi") || lowerText.contains("hospital") ||
                lowerText.contains("bleeding") || lowerText.contains("hurt") || lowerText.contains("زخمی")

        val vehicleInvolved = lowerText.contains("car") || lowerText.contains("gaari") ||
                lowerText.contains("vehicle") || lowerText.contains("bike") ||
                lowerText.contains("accident") || lowerText.contains("motorcycle") ||
                lowerText.contains("truck") || lowerText.contains("police ne gari") || lowerText.contains("گاڑی")

        val isVehicleDetained = lowerText.contains("police ne") && (
                lowerText.contains("rok") || lowerText.contains("seize") ||
                lowerText.contains("detain") || lowerText.contains("le gayi") ||
                lowerText.contains("impound") || lowerText.contains("taken") ||
                lowerText.contains("thana") || lowerText.contains("band")
        ) || lowerText.contains("vehicle rok") || lowerText.contains("car seized")

        val policeInvolved = lowerText.contains("police") || lowerText.contains("thana") ||
                lowerText.contains("fir") || lowerText.contains("challan") ||
                lowerText.contains("chowki") || lowerText.contains("پولیس")

        val hasFirRegistered = lowerText.contains("fir") || lowerText.contains("report likhwai")

        // 3. Category Classification
        val (category, subCategory) = classifyCategory(lowerText, vehicleInvolved, isVehicleDetained)

        // 4. Missing Information Identification
        val missing = mutableListOf<String>()
        if (category == LegalCategory.TRAFFIC) {
            if (!lowerText.contains("injury") && !lowerText.contains("safe")) missing.add("Any physical injuries or casualties")
            if (!lowerText.contains("licence") && !lowerText.contains("license")) missing.add("Driving licence and registration status")
            if (!lowerText.contains("fir") && !lowerText.contains("challan")) missing.add("Whether FIR or formal seizure memo was issued")
            if (!lowerText.contains("chitral") && !lowerText.contains("booni") && !lowerText.contains("drosh")) missing.add("Exact Tehsil / Police Station area in Chitral")
        } else if (category == LegalCategory.DOMICILE) {
            missing.add("Whether issue is first-time issuance, duplicate, or duplicate record dispute")
            missing.add("Whether father/guardian holds Chitral Domicile")
        } else if (category == LegalCategory.CYBER_HARASSMENT) {
            missing.add("Platform used (WhatsApp, Facebook, Phone call)")
            missing.add("Whether digital screenshots or audio messages are preserved")
        } else if (category == LegalCategory.LAND_PROPERTY) {
            missing.add("Whether official Fard/Jamabandi revenue record exists")
            missing.add("Whether parties are currently in physical possession")
        }

        val facts = CaseFacts(
            location = location,
            province = province,
            extractedSummary = generateSummary(problem, category, subCategory, urgency),
            detectedUrgency = urgency,
            isSafetyCritical = isEmergency,
            hasInjury = hasInjury,
            hasPoliceInvolvement = policeInvolved,
            isVehicleDetained = isVehicleDetained,
            hasFirRegistered = hasFirRegistered,
            missingInformation = missing
        )

        return IntakeResult(category, subCategory, facts)
    }

    private fun detectImmediateDanger(text: String): Boolean {
        val emergencyKeywords = listOf(
            "life threat", "murder threat", "shooting", "gun", "weapon",
            "kidnap", "critical injury", "dying", "severe bleeding",
            "jaan ka khatra", "maar dalne", "qatl", "aggressor",
            "ongoing violence", "assault in progress"
        )
        return emergencyKeywords.any { text.contains(it) }
    }

    private fun detectUrgency(text: String): UrgencyLevel {
        val elevatedKeywords = listOf(
            "threat", "blackmail", "private photo", "seized", "arrest",
            "police custody", "harras", "damkhi", "le gaye", "detained"
        )
        return if (elevatedKeywords.any { text.contains(it) }) {
            UrgencyLevel.ELEVATED
        } else {
            UrgencyLevel.NORMAL
        }
    }

    private fun classifyCategory(text: String, vehicleInvolved: Boolean, vehicleDetained: Boolean): Pair<LegalCategory, String> {
        return when {
            // Traffic & Vehicle
            vehicleInvolved || text.contains("traffic") || text.contains("licence") ||
            text.contains("license") || text.contains("challan") || text.contains("accident") ||
            text.contains("car") || text.contains("motorcycle") || text.contains("highway") -> {
                val sub = when {
                    vehicleDetained -> "vehicle_seizure"
                    text.contains("accident") -> "accident"
                    text.contains("challan") -> "traffic_challan"
                    text.contains("licence") || text.contains("license") -> "driving_licence"
                    else -> "general_traffic"
                }
                Pair(LegalCategory.TRAFFIC, sub)
            }

            // Domicile
            text.contains("domicile") || text.contains("residence certificate") ||
            text.contains("sakoonat") || text.contains("dc office chitral") -> {
                val sub = if (text.contains("duplicate") || text.contains("doosri") || text.contains("same area")) {
                    "duplicate_or_disputed_record"
                } else if (text.contains("incorrect") || text.contains("error") || text.contains("correction")) {
                    "record_correction"
                } else {
                    "domicile_issuance"
                }
                Pair(LegalCategory.DOMICILE, sub)
            }

            // CNIC & Identity
            text.contains("cnic") || text.contains("nadra") || text.contains("identity card") ||
            text.contains("shanakhti card") || text.contains("b-form") || text.contains("b form") ||
            text.contains("crc") -> {
                Pair(LegalCategory.CNIC, "cnic_record_correction")
            }

            // Cyber Harassment & Blackmail
            text.contains("private picture") || text.contains("private photo") ||
            text.contains("blackmail") || text.contains("threaten online") ||
            text.contains("harassment") || text.contains("cyber") ||
            text.contains("social media") || text.contains("whatsapp") -> {
                Pair(LegalCategory.CYBER_HARASSMENT, "online_blackmail_and_harassment")
            }

            // Land & Property
            text.contains("land") || text.contains("property") || text.contains("zameen") ||
            text.contains("patwari") || text.contains("tehsildar") || text.contains("fard") ||
            text.contains("ownership") || text.contains("plot") || text.contains("shamilat") -> {
                Pair(LegalCategory.LAND_PROPERTY, "land_ownership_dispute")
            }

            // Fraud & Financial
            text.contains("fraud") || text.contains("scam") || text.contains("cheating") ||
            text.contains("dhoka") || text.contains("paisa") || text.contains("bank account") -> {
                Pair(LegalCategory.FRAUD, "financial_fraud")
            }

            // Employment
            text.contains("salary") || text.contains("job") || text.contains("employer") ||
            text.contains("terminated") || text.contains("workplace") || text.contains("mulazmat") -> {
                Pair(LegalCategory.EMPLOYMENT, "workplace_dispute")
            }

            else -> Pair(LegalCategory.GENERAL, "general_inquiry")
        }
    }

    private fun generateSummary(
        problem: String,
        category: LegalCategory,
        subCategory: String,
        urgency: UrgencyLevel
    ): String {
        return "Legal matter identified under ${category.titleEn} ($subCategory). Urgency level: ${urgency.name}. Initial fact intake completed."
    }
}
