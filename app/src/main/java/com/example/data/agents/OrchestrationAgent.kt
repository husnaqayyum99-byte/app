package com.example.data.agents

import android.util.Log
import com.example.data.models.CaseFacts
import com.example.data.models.FollowUpQuestion
import com.example.data.models.LegalCategory
import com.example.data.models.ResearchPlan

object OrchestrationAgent {

    private const val TAG = "ApnaWakeel_Orchestrator"

    fun routeToSpecialist(category: LegalCategory): SpecialistAgent {
        Log.d(TAG, "[ROUTER] Routing to specialist agent for category: $category")
        return when (category) {
            LegalCategory.TRAFFIC -> TrafficAgent
            LegalCategory.DOMICILE -> DomicileAgent
            LegalCategory.CNIC -> CnicAgent
            LegalCategory.CYBER_HARASSMENT -> CyberAgent
            LegalCategory.LAND_PROPERTY -> LandPropertyAgent
            LegalCategory.FRAUD -> LandPropertyAgent // Shares property/police remedies
            LegalCategory.EMPLOYMENT -> GeneralAgent
            LegalCategory.GENERAL -> GeneralAgent
        }
    }

    fun getQuestions(category: LegalCategory, facts: CaseFacts, subCategory: String): List<FollowUpQuestion> {
        val specialist = routeToSpecialist(category)
        Log.d(TAG, "[ORCHESTRATOR] Invoking ${specialist.javaClass.simpleName} to generate tailored questions")
        return specialist.generateQuestions(facts, subCategory)
    }

    fun buildResearchPlan(
        category: LegalCategory,
        answers: Map<String, String>,
        facts: CaseFacts,
        subCategory: String
    ): ResearchPlan {
        val specialist = routeToSpecialist(category)
        Log.d(TAG, "[ORCHESTRATOR] Invoking ${specialist.javaClass.simpleName} to generate research plan")
        return specialist.getResearchPlan(answers, facts, subCategory)
    }
}
