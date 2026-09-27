package com.example.data.sources

import com.example.data.models.OfficialSource

/**
 * Centralized Authoritative Legal Source Registry for Apna Wakeel.
 * Strictly verified official portals, statutes, and government authorities
 * in Chitral, Khyber Pakhtunkhwa (KP), and Federal Pakistan.
 */
object LegalSourcesRegistry {

    val KP_CODE = OfficialSource(
        id = "KP_CODE",
        title = "The Khyber Pakhtunkhwa Code",
        titleUr = "خیبر پختونخوا کوڈ (سرکاری قوانین)",
        authority = "Law, Parliamentary Affairs & Human Rights Department, Govt of KP",
        jurisdiction = "Khyber Pakhtunkhwa",
        category = "Official Legislation Repository",
        officialUrl = "https://kpcode.kp.gov.pk/",
        verificationBadge = "Official Statutory Repository",
        description = "Official repository of all Acts, Ordinances, and statutory rules enacted in Khyber Pakhtunkhwa, including West Pakistan Motor Vehicles Ordinance 1965 (as adapted by KP)."
    )

    val KP_POLICE = OfficialSource(
        id = "KP_POLICE",
        title = "Khyber Pakhtunkhwa Police Official Portal",
        titleUr = "خیبر پختونخوا پولیس سرکاری پورٹل",
        authority = "Inspector General of Police, Khyber Pakhtunkhwa",
        jurisdiction = "Khyber Pakhtunkhwa / Chitral District Police",
        category = "Law Enforcement & Traffic Regulation",
        officialUrl = "https://kppolice.gov.pk/",
        verificationBadge = "Official Law Enforcement Authority",
        description = "Provides official challan verification, driving licence systems, Police Khidmat Marakaz procedures, vehicle seizure documentation, and DPO Lower & Upper Chitral jurisdiction."
    )

    val KP_TRANSPORT = OfficialSource(
        id = "KP_TRANSPORT",
        title = "KP Transport & Mass Transit Department",
        titleUr = "محکمہ ٹرانسپورٹ خیبر پختونخوا",
        authority = "Government of Khyber Pakhtunkhwa",
        jurisdiction = "Khyber Pakhtunkhwa",
        category = "Transport & Licensing Authority",
        officialUrl = "https://transport.kp.gov.pk/",
        verificationBadge = "Official Provincial Department",
        description = "Responsible for motor vehicle registration, commercial fitness, route permits, and regional transport authority regulations across KP districts."
    )

    val KP_REVENUE = OfficialSource(
        id = "KP_REVENUE",
        title = "KP Revenue & Estate Department",
        titleUr = "محکمہ مال و ریونیو خیبر پختونخوا",
        authority = "Board of Revenue, Khyber Pakhtunkhwa",
        jurisdiction = "Khyber Pakhtunkhwa / Chitral Revenue Circles",
        category = "Land Administration & Property",
        officialUrl = "https://revenue.kp.gov.pk/",
        verificationBadge = "Official Land Authority",
        description = "Governs land records (Jamabandi/Fard), boundary demarcation, inheritance transfers (Intiqal), Patwari & Tehsildar administrative procedures under the Land Revenue Act 1967."
    )

    val DC_CHITRAL = OfficialSource(
        id = "DC_CHITRAL",
        title = "Deputy Commissioner Office (Lower & Upper Chitral)",
        titleUr = "ڈپٹی کمشنر آفس چترال (لوئر اور اپر)",
        authority = "District Administration Chitral, KP Government",
        jurisdiction = "District Lower Chitral & Upper Chitral",
        category = "District Executive & Domicile Issuing Authority",
        officialUrl = "https://chitral.kp.gov.pk/",
        verificationBadge = "Official District Administration",
        description = "The competent statutory authority for issuing and verifying Domicile Certificates under Pakistan Citizenship Act 1951, executive magistrate hearings, and district administrative relief."
    )

    val NADRA = OfficialSource(
        id = "NADRA",
        title = "National Database & Registration Authority (NADRA)",
        titleUr = "نادرا - نیشنل ڈیٹا بیس اینڈ رجسٹریشن اتھارٹی",
        authority = "Ministry of Interior, Government of Pakistan",
        jurisdiction = "Federal / Chitral NRCs (Bypass Road Chitral & Booni)",
        category = "Civil Registration & Citizen Identity",
        officialUrl = "https://www.nadra.gov.pk/",
        verificationBadge = "Official National Identity Authority",
        description = "Statutory authority under the NADRA Ordinance 2000 for Computerized National Identity Cards (CNIC), Child Registration Certificates (B-Form), family registration, and biometric corrections."
    )

    val PESHAWAR_HIGH_COURT = OfficialSource(
        id = "PESHAWAR_HIGH_COURT",
        title = "Peshawar High Court & Chitral District Judiciary",
        titleUr = "پشاور ہائی کورٹ اور ڈسٹرکٹ کورٹ چترال",
        authority = "Judiciary of Khyber Pakhtunkhwa",
        jurisdiction = "Khyber Pakhtunkhwa / District Courts Chitral",
        category = "Judicial Authority & Case Law",
        officialUrl = "https://peshawarhighcourt.gov.pk/",
        verificationBadge = "Constitutional High Court",
        description = "Provincial appellate court with jurisdiction over District & Sessions Courts of Lower Chitral and Upper Chitral, entertaining civil suits, criminal appeals, and constitutional writ petitions."
    )

    val PAKISTAN_CODE = OfficialSource(
        id = "PAKISTAN_CODE",
        title = "Pakistan Code (Federal Legislative Portal)",
        titleUr = "پاکستان کوڈ (وفاقی سرکاری قوانین)",
        authority = "Ministry of Law and Justice, Government of Pakistan",
        jurisdiction = "Federal Pakistan",
        category = "Federal Statutory Repository",
        officialUrl = "https://pakistancode.gov.pk/",
        verificationBadge = "Official Federal Repository",
        description = "Authoritative federal statute database including Pakistan Penal Code 1860, Code of Criminal Procedure 1898, Specific Relief Act 1877, and Prevention of Electronic Crimes Act 2016."
    )

    val FIA_CYBERCRIME = OfficialSource(
        id = "FIA_CYBERCRIME",
        title = "FIA National Response Centre for Cyber Crime (NR3C)",
        titleUr = "ایف آئی اے سائبر کرائم ونگ (این آر تھری سی)",
        authority = "Federal Investigation Agency, Ministry of Interior",
        jurisdiction = "Federal / KP Zone",
        category = "Cybercrime & Online Harassment Investigation",
        officialUrl = "https://cybercrime.fia.gov.pk/",
        verificationBadge = "Designated PECA Law Enforcement Agency",
        description = "The sole designated statutory law enforcement agency under Section 29 of PECA 2016 investigating online blackmail, unauthorized sharing of private pictures, stalking, and electronic extortion."
    )

    val NHMP = OfficialSource(
        id = "NHMP",
        title = "National Highways & Motorway Police (NHMP)",
        titleUr = "نیشنل ہائی ویز اینڈ موٹروے پولیس",
        authority = "Ministry of Communications, Government of Pakistan",
        jurisdiction = "Federal Highways (N-45 Lowari Tunnel / Chitral Highway)",
        category = "Highway Safety & Enforcement",
        officialUrl = "https://nhmp.gov.pk/",
        verificationBadge = "Official Highway Police Authority",
        description = "Regulates vehicular transit, emergency assistance, and accident reporting along National Highway N-45 passing through Lowari Tunnel connecting Chitral with the rest of KP."
    )

    val ALL_SOURCES: List<OfficialSource> = listOf(
        KP_CODE,
        KP_POLICE,
        DC_CHITRAL,
        KP_TRANSPORT,
        KP_REVENUE,
        NADRA,
        FIA_CYBERCRIME,
        PESHAWAR_HIGH_COURT,
        PAKISTAN_CODE,
        NHMP
    )

    fun getSourceById(id: String): OfficialSource? {
        return ALL_SOURCES.find { it.id == id }
    }

    fun getSourcesForCategory(category: String): List<OfficialSource> {
        return when (category.lowercase()) {
            "traffic" -> listOf(KP_CODE, KP_POLICE, KP_TRANSPORT, NHMP)
            "domicile" -> listOf(DC_CHITRAL, KP_CODE, NADRA)
            "cnic", "identity" -> listOf(NADRA, PAKISTAN_CODE)
            "land_property", "property" -> listOf(KP_REVENUE, PESHAWAR_HIGH_COURT, KP_CODE)
            "cyber_harassment", "cyber" -> listOf(FIA_CYBERCRIME, PAKISTAN_CODE, PESHAWAR_HIGH_COURT)
            "fraud" -> listOf(PAKISTAN_CODE, KP_POLICE, FIA_CYBERCRIME)
            "employment" -> listOf(KP_CODE, PESHAWAR_HIGH_COURT)
            else -> listOf(KP_CODE, PESHAWAR_HIGH_COURT, PAKISTAN_CODE)
        }
    }
}
