package com.example.data.agents

import com.example.data.models.*
import com.example.data.sources.LegalSourcesRegistry
import java.util.UUID

object LegalPlanBuilder {

    fun buildPlan(
        caseId: String = UUID.randomUUID().toString(),
        originalProblem: String,
        category: LegalCategory,
        subCategory: String,
        facts: CaseFacts,
        answers: Map<String, String>
    ): FinalLegalPlan {
        val specialist = OrchestrationAgent.routeToSpecialist(category)
        val authority = specialist.getResponsibleAuthority(facts, subCategory)
        val verifiedSources = LegalSourcesRegistry.getSourcesForCategory(category.name)

        val isEmergency = facts.isSafetyCritical || facts.detectedUrgency == UrgencyLevel.EMERGENCY

        val steps = mutableListOf<ActionStep>()
        val documents = mutableListOf<String>()
        val documentsUr = mutableListOf<String>()
        val evidence = mutableListOf<String>()
        val evidenceUr = mutableListOf<String>()
        val uncertainties = mutableListOf<String>()
        val uncertaintiesUr = mutableListOf<String>()

        var summaryEn = ""
        var summaryUr = ""
        var timelineEn = ""
        var timelineUr = ""
        var lawyerAdviceEn = ""
        var lawyerAdviceUr = ""
        var needsLawyer = false

        when (category) {
            LegalCategory.TRAFFIC -> {
                summaryEn = "Traffic incident in ${facts.location}, KP. Vehicle seized/detained by police following an accident or documentation issue."
                summaryUr = "چترال میں ٹریفک حادثے یا دستاویزات کے تناظر میں پولیس کی جانب سے گاڑی تحویل میں لینے کا معاملہ۔"

                if (subCategory == "vehicle_seizure" || facts.isVehicleDetained) {
                    steps.add(
                        ActionStep(
                            stepNumber = 1,
                            titleEn = "Verify Police Seizure Memo (Zabti / Fard-e-Maqboozi)",
                            titleUr = "پولیس کی ضبطی پرچی یا فردِ مقبوضی حاصل کریں",
                            descriptionEn = "Ask the police Moharrir or investigating officer for the formal written seizure inventory sheet (Fard-e-Maqboozi) recording the vehicle's engine number, chassis number, and condition.",
                            descriptionUr = "تھانے کے محرر یا تفتیشی افسر سے تحریری فردِ مقبوضی طلب کریں جس میں گاڑی کا انجن نمبر، چیسس نمبر اور موجودہ حالت درج ہو۔",
                            isMandatory = true,
                            authorityOrDesk = "Police Station Moharrir Desk"
                        )
                    )
                    steps.add(
                        ActionStep(
                            stepNumber = 2,
                            titleEn = "Present Original Registration & Ownership Proof",
                            titleUr = "اصل رجسٹریشن کارڈ اور ملکیت کے ثبوت پیش کریں",
                            descriptionEn = "Produce the original computerized registration card/book and excise tax clearance. If the vehicle is in a previous owner's name, carry the original transfer deed or Biometric verification slip.",
                            descriptionUr = "گاڑی کا اصل سمارٹ کارڈ، ایکسائز ریکارڈ اور بائیو میٹرک ٹرانسفر سلپ پیش کریں۔",
                            isMandatory = true,
                            authorityOrDesk = "DPO Traffic Branch Chitral"
                        )
                    )
                    steps.add(
                        ActionStep(
                            stepNumber = 3,
                            titleEn = "Apply for Superdari before Judicial Magistrate (If FIR is registered)",
                            titleUr = "جوڈیشل مجسٹریٹ چترال کی عدالت میں سپرداری کی درخواست دیں",
                            descriptionEn = "Under Section 516-A of the Code of Criminal Procedure (CrPC), submit an application for interim custody (Superdari) before the Judicial Magistrate Chitral, submitting indemnity bond for vehicle release.",
                            descriptionUr = "اگر باقاعدہ ایف آئی آر یا دفعہ 550 کے تحت گاڑی بند ہو، تو ضابطہ فوجداری کی دفعہ 516-اے کے تحت علاقہ مجسٹریٹ کے ہاں سپرداری کی درخواست دائر کریں۔",
                            isMandatory = false,
                            authorityOrDesk = "Court of Judicial Magistrate Chitral"
                        )
                    )
                    steps.add(
                        ActionStep(
                            stepNumber = 4,
                            titleEn = "Vehicle Inspection and Physical Handover",
                            titleUr = "گاڑی کا معائنہ اور باقاعدہ تحویل لینا",
                            descriptionEn = "Upon receipt of the release order or traffic clearance, inspect the vehicle thoroughly in the presence of the police officer before signing the receipt log.",
                            descriptionUr = "عدالتی یا ٹریفک آرڈر کے بعد تھانے میں گاڑی کی حالت اور اشیاء کا معائنہ کر کے رسید پر دستخط کریں۔",
                            isMandatory = true,
                            authorityOrDesk = "Police Malkhana Incharge"
                        )
                    )

                    documents.addAll(listOf(
                        "Original Vehicle Registration Book / Smart Card",
                        "Original Driving Licence of the driver at the time of incident",
                        "Owner's CNIC copy",
                        "Police Challan slip or FIR copy (if registered)",
                        "Vehicle transfer letter / affidavit (if not yet transferred in your name)"
                    ))
                    documentsUr.addAll(listOf(
                        "گاڑی کی اصل رجسٹریشن بک / سمارٹ کارڈ",
                        "حادثے کے وقت ڈرائیور کا اصل ڈرائیونگ لائسنس",
                        "مالک کا قومی شناختی کارڈ",
                        "پولیس چالان یا ایف آئی آر کی مصدقہ نقل",
                        "گاڑی کا ٹرانسفر لیٹر یا بائیو میٹرک دستاویز"
                    ))

                    evidence.addAll(listOf(
                        "Photographs of vehicle condition at time of seizure",
                        "Photographs of accident spot (if applicable)",
                        "Receipt / Fard-e-Maqboozi copy issued by police",
                        "Witness phone numbers and names present at scene"
                    ))
                    evidenceUr.addAll(listOf(
                        "تحویل میں لیتے وقت گاڑی کی موجودہ تصاویر",
                        "جائے حادثہ کی تصاویر (اگر حادثہ ہوا ہو)",
                        "پولیس کی جاری کردہ ضبطی پرچی یا فردِ مقبوضی",
                        "موقع پر موجود گواہوں کے نام اور فون نمبرز"
                    ))

                    timelineEn = "No statutory deadline is mandated for vehicle custody application, but submitting the Superdari petition within 3 to 7 working days avoids prolonged police custody and vehicle deterioration."
                    timelineUr = "سپرداری کی درخواست کے لیے کوئی سخت قانونی میعاد مقرر نہیں ہے، تاہم 3 سے 7 دفتری ایام میں رجوع کرنا گاڑی کو تھانے میں خراب ہونے سے بچاتا ہے۔"
                    needsLawyer = answers["seizure_reason"] == "fir_criminal_case" || facts.hasInjury
                    lawyerAdviceEn = if (needsLawyer) {
                        "Because criminal FIR or bodily injury is involved, engaging an advocate registered with Chitral Bar Association is strongly recommended for drafting the Section 516-A CrPC petition."
                    } else {
                        "For simple documentation or traffic challan seizure, direct representation before the Traffic Incharge is standard procedure."
                    }
                    lawyerAdviceUr = if (needsLawyer) {
                        "چونکہ مقدمہ یا چوٹ کا عنصر موجود ہے، اس لیے چترال بار ایسوسی ایشن کے مستند وکیل سے سپرداری کی درخواست تیار کروانا ضروری ہے۔"
                    } else {
                        "عام ٹریفک چالان کے لیے آپ بذات خود اصل کاغذات دکھا کر ٹریفک پولیس سے گاڑی واگزار کرا سکتے ہیں۔"
                    }
                } else {
                    // General accident
                    steps.add(
                        ActionStep(
                            stepNumber = 1,
                            titleEn = "Ensure Physical Safety & Medical Attention",
                            titleUr = "طبی امداد اور جسمانی سلامتی کو یقینی بنائیں",
                            descriptionEn = "If anyone has sustained injuries, prioritize medical examination at District Headquarter (DHQ) Hospital Chitral to obtain a formal Medico-Legal Certificate (MLC).",
                            descriptionUr = "اگر کوئی زخمی ہے تو فوری طور پر ڈی ایچ کیو ہسپتال چترال سے میڈیکو لیگل سرٹیفکیٹ (ایم ایل سی) حاصل کریں۔",
                            isMandatory = true
                        )
                    )
                    steps.add(
                        ActionStep(
                            stepNumber = 2,
                            titleEn = "Preserve Scene & Exchange Vehicle Information",
                            titleUr = "گاڑیوں کے نقصان کی تصاویر اور فریقین کی تفصیلات محفوظ کریں",
                            descriptionEn = "Record the other driver's CNIC, vehicle registration number, and contact details before moving vehicles off the roadway.",
                            descriptionUr = "دوسری گاڑی کا نمبر، ڈرائیور کا شناختی کارڈ اور رابطہ نمبر محفوظ کریں۔",
                            isMandatory = true
                        )
                    )
                    documents.add("Valid Driving Licence")
                    documents.add("Vehicle Registration Smart Card")
                    documentsUr.add("درست ڈرائیونگ لائسنس")
                    documentsUr.add("گاڑی کا سمارٹ کارڈ")
                    evidence.add("Clear photos of vehicle damage, impact angle, and road conditions")
                    evidenceUr.add("گاڑیوں کے نقصان، ٹکر کے زاویے اور سڑک کی تصاویر")
                    timelineEn = "No verified deadline was identified from the available sources for civil settlement, but criminal reporting should be done immediately at the nearest police post."
                    timelineUr = "فوجداری رپورٹ کے لیے فوری تھانے اطلاع دینا ضروری ہے، باہمی تصفیے کے لیے کوئی مخصوص قانونی میعاد نہیں۔"
                }

                uncertainties.add("Exact towing charges or municipal storage fees may vary by Tehsil administration notification.")
                uncertainties.add("Court processing duration for Superdari depends on the duty roster of the Judicial Magistrate Chitral.")
                uncertaintiesUr.add("میونسپل کسٹڈی فیس تحصیل کونسل کے سالانہ نوٹیفکیشن کے مطابق مختلف ہو سکتی ہے۔")
                uncertaintiesUr.add("عدالتی سپرداری کے فیصلے کا دورانیہ جوڈیشل مجسٹریٹ چترال کی عدالتی مصروفیات پر منحصر ہے۔")
            }

            LegalCategory.DOMICILE -> {
                summaryEn = "Domicile and residency documentation matter in ${facts.location}, KP regarding verification, duplicate records, or correction."
                summaryUr = "چترال، خیبر پختونخوا میں ڈومیسائل سرٹیفکیٹ کی تصدیق، دوہرے ریکارڈ یا درستی سے متعلق معاملہ۔"

                steps.add(
                    ActionStep(
                        stepNumber = 1,
                        titleEn = "Procure Local Revenue Verification (Patwari & Tehsildar Report)",
                        titleUr = "پٹواری حلقہ اور تحصیلدار سے رہائشی تصدیق حاصل کریں",
                        descriptionEn = "Obtain verification report regarding ancestral or permanent residence from the Halqa Patwari and Tehsildar confirming entry in land settlement or residence register.",
                        descriptionUr = "حلقہ پٹواری اور تحصیلدار سے مستقل سکونت کی تصدیقی رپورٹ حاصل کریں۔",
                        isMandatory = true,
                        authorityOrDesk = "Tehsil Revenue Office"
                    )
                )
                steps.add(
                    ActionStep(
                        stepNumber = 2,
                        titleEn = "Submit Verification Petition to Deputy Commissioner Office",
                        titleUr = "ڈپٹی کمشنر آفس چترال میں درخواست برائے انکوائری جمع کروائیں",
                        descriptionEn = "Address a written application to the Additional Deputy Commissioner (General) Chitral highlighting the duplicate or contested domicile issue with supporting documents.",
                        descriptionUr = "ایڈیشنل ڈپٹی کمشنر چترال کے نام باقاعدہ درخواست جمع کروائیں جس میں دوہرے یا مشتبہ ڈومیسائل کی جانچ کی استدعا ہو۔",
                        isMandatory = true,
                        authorityOrDesk = "DC Office Domicile Branch"
                    )
                )
                steps.add(
                    ActionStep(
                        stepNumber = 3,
                        titleEn = "Participate in Executive Inquiry Hearing",
                        titleUr = "مقررہ تاریخ پر انکوائری میں اصل ریکارڈ کے ساتھ پیش ہوں",
                        descriptionEn = "Attend the scheduled inquiry hearing before the Assistant Commissioner or Revenue Officer with witnesses and family tree records.",
                        descriptionUr = "اسسٹنٹ کمشنر یا مجسٹریٹ کے روبرو خاندان کے شجرہ نسب اور گواہوں کے ساتھ پیش ہوں۔",
                        isMandatory = true
                    )
                )

                documents.addAll(listOf(
                    "Applicant's CNIC / B-Form copy",
                    "Father's / Guardian's Chitral Domicile Certificate",
                    "Educational certificates / School leaving certificates showing schooling in Chitral",
                    "Land Revenue Record (Fard Jamabandi / Shamilat entry)",
                    "Union Council permanent residence certificate"
                ))
                documentsUr.addAll(listOf(
                    "سائل کا شناختی کارڈ یا ب فارم",
                    "والد کا اصل چترال ڈومیسائل",
                    "چترال کے اسکول سے تعلیمی اسناد",
                    "فرد جمع بندی یا رہائشی ملکیتی ریکارڈ",
                    "متعلقہ یونین کونسل سے تصدیق نامہ"
                ))

                evidence.addAll(listOf(
                    "Certified copy of contested duplicate domicile (if obtainable)",
                    "Voter list entry showing name in Chitral electoral roll",
                    "Electricity/utility bill showing residential address in Chitral"
                ))
                evidenceUr.addAll(listOf(
                    "مشکوک یا دوہرے ڈومیسائل کی تصدیق شدہ نقل",
                    "چترال کی انتخابی ووٹر لسٹ کا اندراج",
                    "چترال کے پتے پر بجلی یا دیگر یوٹیلٹی بل"
                ))

                timelineEn = "Under KP administrative guidelines, domicile inquiries are typically concluded within 14 to 30 working days. No statutory limitation period bars challenging a fraudulent document."
                timelineUr = "سرکاری ہدایات کے مطابق انکوائری عام طور پر 14 سے 30 ایام میں مکمل کی جاتی ہے۔ جعلی دستاویز کو چیلنج کرنے کی کوئی حتمی میعاد نہیں۔"
                needsLawyer = false
                lawyerAdviceEn = "Administrative representation before the DC Office does not legally require an advocate. However, if an adverse administrative order is passed, a constitutional writ petition before Peshawar High Court requires legal counsel."
                lawyerAdviceUr = "ڈی سی آفس کی کارروائی کے لیے وکیل لازمی نہیں، لیکن اگر ہائی کورٹ میں رٹ دائر کرنی ہو تو وکیل کی ضرورت ہوگی۔"

                uncertainties.add("Inquiry duration depends on the field verification timeline of the local revenue staff in remote valleys.")
                uncertaintiesUr.add("دور دراز وادیوں میں پٹواری کی فیلڈ رپورٹ کی بنیاد پر انکوائری کے دورانیے میں فرق آ سکتا ہے۔")
            }

            LegalCategory.CYBER_HARASSMENT -> {
                summaryEn = "Cyber harassment, blackmail, or non-consensual image sharing reported in ${facts.location}, KP."
                summaryUr = "چترال، کے پی میں آن لائن ہراسانی، بلیک میلنگ یا نجی تصاویر شیئر کرنے سے متعلق سنگین معاملہ۔"

                steps.add(
                    ActionStep(
                        stepNumber = 1,
                        titleEn = "Do NOT Delete Messages & Stop All Payments",
                        titleUr = "کسی بھی صورت پیغامات ڈیلیٹ نہ کریں اور پیسے ادا نہ کریں",
                        descriptionEn = "Immediately cease responding to demands. Paying money never stops blackmail. Keep the blackmailer's account visible for digital evidence extraction.",
                        descriptionUr = "بلیک میلر سے بات چیت بند کریں اور پیسے ہرگز نہ دیں۔ چیٹ ڈیلیٹ نہ کریں تاکہ ڈیجیٹل فرانزک ممکن ہو۔",
                        isMandatory = true
                    )
                )
                steps.add(
                    ActionStep(
                        stepNumber = 2,
                        titleEn = "Preserve Complete Forensic Screenshots & URLs",
                        titleUr = "موبائل نمبر، تاریخ اور پروفائل لنک سمیت اسکرین شاٹس محفوظ کریں",
                        descriptionEn = "Capture full-screen images showing sender's phone number (+92...), profile handle, exact timestamp, and the threatening messages.",
                        descriptionUr = "فرستندہ کا فون نمبر، فیس بک یا واٹس ایپ پروفائل لنک، تاریخ اور دھمکی آمیز پیغامات کا مکمل اسکرین شاٹ لیں۔",
                        isMandatory = true
                    )
                )
                steps.add(
                    ActionStep(
                        stepNumber = 3,
                        titleEn = "Lodge Formal Complaint on FIA NR3C Portal",
                        titleUr = "ایف آئی اے سائبر کرائم پورٹل پر باقاعدہ شکایت درج کروائیں",
                        descriptionEn = "Register a complaint online at complaint.fia.gov.pk under Section 21 and 24 of PECA 2016 or call the FIA National Cyber Helpline at 1991.",
                        descriptionUr = "حکومتی پورٹل complaint.fia.gov.pk پر پیکا ایکٹ کی دفعہ 21 اور 24 کے تحت شکایت درج کروائیں یا ہیلپ لائن 1991 پر کال کریں۔",
                        isMandatory = true,
                        authorityOrDesk = "FIA Cyber Crime Wing Peshawar / Online Desk"
                    )
                )

                documents.addAll(listOf(
                    "Complainant's CNIC copy",
                    "Written statement describing timeline and threats received",
                    "Details of bank account / mobile wallet if extortion payment was demanded"
                ))
                documentsUr.addAll(listOf(
                    "شکایت کنندہ کے قومی شناختی کارڈ کی نقل",
                    "وقوعہ کی تحریری تفصیل اور دھمکیوں کی تاریخ وار تفصیل",
                    "بلیک میلر کا فراہم کردہ بینک یا ایزی پیسہ اکاؤنٹ نمبر"
                ))

                evidence.addAll(listOf(
                    "High-resolution screenshots of chats, calls, and threatening messages",
                    "Original digital image file with metadata / timestamp",
                    "Audio recordings of threatening phone or voice calls",
                    "URL links to defamatory posts or profiles"
                ))
                evidenceUr.addAll(listOf(
                    "پیغامات، کالز اور دھمکیوں کے واضح اسکرین شاٹس",
                    "اصل تصویر یا فائل مع ڈیجیٹل ٹائم اسٹیمپ",
                    "دھمکی آمیز کالز کی آڈیو ریکارڈنگز",
                    "سوشل میڈیا پوسٹس اور پروفائل کے لائیو لنکس"
                ))

                timelineEn = "Complaints should be filed immediately. Content takedown requests through FIA to social media platforms are processed within 24 to 72 hours."
                timelineUr = "شکایت فوری درج کروانی چاہیے۔ مواد ہٹانے کی درخواست ایف آئی اے کے ذریعے 24 سے 72 گھنٹوں میں پراسیس کی جاتی ہے۔"
                needsLawyer = false
                lawyerAdviceEn = "FIA Cybercrime portal does not require a lawyer for registration. If trial commences in the PECA Special Court, legal representation becomes advisable."
                lawyerAdviceUr = "ایف آئی اے میں شکایت درج کروانے کے لیے وکیل کی ضرورت نہیں، البتہ عدالتی ٹرائل کے لیے وکیل کی خدمات حاصل کی جا سکتی ہیں۔"

                uncertainties.add("Takedown speed of international social media platforms depends on their automated trust and safety queues.")
                uncertaintiesUr.add("بین الاقوامی سوشل میڈیا پلیٹ فارمز سے مواد ہٹانے کی رفتار ان کے داخلی طریقہ کار پر منحصر ہے۔")
            }

            LegalCategory.LAND_PROPERTY -> {
                summaryEn = "Land or immovable property dispute in ${facts.location}, KP regarding ownership, boundaries, or possession."
                summaryUr = "چترال، خیبر پختونخوا میں زمین یا غیر منقولہ جائیداد کی ملکیت، حد براری یا قبضے سے متعلق تنازع۔"

                steps.add(
                    ActionStep(
                        stepNumber = 1,
                        titleEn = "Procure Certified Revenue Records (Fard-e-Malkiat & Aks Shajra)",
                        titleUr = "پٹوار خانے سے فردِ ملکیت اور عکس شجرہ حاصل کریں",
                        descriptionEn = "Apply to the Halqa Patwari and Tehsildar for an official certified copy of Jamabandi / Fard and cadastral map (Aks Shajra).",
                        descriptionUr = "حلقہ پٹواری سے سرکاری فردِ ملکیت اور زمین کا تصدیق شدہ نقشہ حاصل کریں۔",
                        isMandatory = true,
                        authorityOrDesk = "Tehsil Patwarkhana"
                    )
                )
                steps.add(
                    ActionStep(
                        stepNumber = 2,
                        titleEn = "Apply for Statutory Demarcation (Hadd-Barari) under Section 117",
                        titleUr = "تحصیلدار کو دفعہ 117 کے تحت حد براری کی درخواست دیں",
                        descriptionEn = "If boundaries are disputed or encroached upon, file an application before the Tehsildar for on-site demarcation by the field revenue staff.",
                        descriptionUr = "اگر حدود کا تنازع ہو تو لینڈ ریونیو ایکٹ کی دفعہ 117 کے تحت باقاعدہ فیلڈ پیمائش کی درخواست دیں۔",
                        isMandatory = false,
                        authorityOrDesk = "Tehsildar Revenue Court"
                    )
                )
                steps.add(
                    ActionStep(
                        stepNumber = 3,
                        titleEn = "File Suit for Declaration & Injunction (If title or construction is challenged)",
                        titleUr = "سول کورٹ میں دعویٰ استقرارِ حق اور حکمِ امتناعی دائر کریں",
                        descriptionEn = "If opposing party attempts unauthorized construction or denies title, institute a civil suit before the Senior Civil Judge Chitral seeking temporary injunction (Stay Order).",
                        descriptionUr = "اگر مخالف فریق غیر قانونی تعمیر کرے تو سینئر سول جج چترال کی عدالت میں حکمِ امتناعی (سٹے آرڈر) کا دعویٰ دائر کریں۔",
                        isMandatory = false,
                        authorityOrDesk = "Civil Courts Chitral"
                    )
                )

                documents.addAll(listOf(
                    "Certified Copy of Fard Jamabandi / Record of Rights",
                    "Mutation Deed (Intiqal) / Registered Sale Deed",
                    "Applicant's CNIC copy",
                    "Inheritance pedigree table (Shajra Nasab) if family property"
                ))
                documentsUr.addAll(listOf(
                    "مصدقہ فرد جمع بندی / مثل میعاد",
                    "رجسٹرڈ بیع نامہ یا انتقال اراضی",
                    "سائل کا شناختی کارڈ",
                    "خاندانی وراثت کا شجرہ نسب"
                ))

                evidence.addAll(listOf(
                    "Current site photographs showing boundary marks or illegal encroachment",
                    "Utility bills showing continuous possession",
                    "Receipts of land revenue tax paid to government"
                ))
                evidenceUr.addAll(listOf(
                    "موقع پر حد بندی یا غیر قانونی تعمیر کی تازہ تصاویر",
                    "مسلسل قبضے کے ثبوت کے طور پر بجلی کے بل",
                    "مال گزاری کی سرکاری رسیدات"
                ))

                timelineEn = "No verified deadline was identified from the available sources for demarcation; however, for temporary stay orders, urgency requires filing before construction advances."
                timelineUr = "حد براری کے لیے کوئی سخت میعاد نہیں، البتہ سٹے آرڈر کے لیے تعمیر شروع ہونے سے قبل رجوع ضروری ہے۔"
                needsLawyer = true
                lawyerAdviceEn = "Land disputes involving civil suits or injunctions in District Courts Chitral require an advocate licensed with the KP Bar Council."
                lawyerAdviceUr = "سول کورٹ چترال میں دعویٰ دائر کرنے کے لیے خیبر پختونخوا بار کونسل کے وکیل کی خدمات ضروری ہیں۔"

                uncertainties.add("Status of customary communal land (Shamilat) depends on past settlement records and customary law in Chitral.")
                uncertaintiesUr.add("شاملات دہ کے حقوق چترال کے پرانے ریونیو ریکارڈ اور روایتی رسم و رواج پر منحصر ہیں۔")
            }

            else -> {
                // CNIC, Employment, General
                summaryEn = "Legal guidance regarding documentation and administrative remedies in ${facts.location}, KP."
                summaryUr = "چترال، کے پی میں انتظامی و قانونی امور سے متعلق رہنمائی۔"

                steps.add(
                    ActionStep(
                        stepNumber = 1,
                        titleEn = "Assemble Personal Identification & Official Records",
                        titleUr = "اپنے شناختی و ضروری سرکاری کاغذات اکٹھے کریں",
                        descriptionEn = "Gather your CNIC, birth certificates, and relevant department letters before approaching the authorized desk.",
                        descriptionUr = "متعلقہ محکمے سے رجوع کرنے سے قبل تمام اصل کاغذات اور نقول تیار رکھیں۔",
                        isMandatory = true
                    )
                )
                steps.add(
                    ActionStep(
                        stepNumber = 2,
                        titleEn = "Present Matter to Designated Facilitation Centre",
                        titleUr = "متعلقہ سرکاری سہولت مرکز یا ڈیسک پر رجوع کریں",
                        descriptionEn = "Visit the responsible public facilitation centre and obtain a dated receiving token or receipt for your submission.",
                        descriptionUr = "متعلقہ دفتر سے اپنی درخواست کی باقاعدہ تحریری وصولی یا ٹوکن حاصل کریں۔",
                        isMandatory = true
                    )
                )

                documents.add("Valid CNIC / Family Registration Certificate (FRC)")
                documents.add("Relevant departmental correspondence or notices")
                documentsUr.add("قومی شناختی کارڈ یا فیملی سرٹیفکیٹ")
                documentsUr.add("متعلقہ محکمے کے جاری کردہ نوٹسز یا خطوط")

                evidence.add("Written record of department visits, officer designations, and dates")
                evidenceUr.add("دفاتری ملاقاتوں، افسران کے نام اور تاریخوں کا ریکارڈ")

                timelineEn = "No verified deadline was identified from the available sources. Inquire directly with the relevant department for procedural timetables."
                timelineUr = "دستیاب ذرائع سے کسی تصدیق شدہ حتمی میعاد کی تصدیق نہیں ہوئی۔ متعلقہ محکمے کے شیڈول کے مطابق کارروائی ہو گی۔"
                needsLawyer = false
                lawyerAdviceEn = "Consult a licensed lawyer if formal litigation or statutory appeal becomes necessary."
                lawyerAdviceUr = "اگر عدالتی اپیل درکار ہو تو قانونی مشیر سے رجوع کریں۔"

                uncertainties.add("Administrative timelines vary according to department caseload.")
                uncertaintiesUr.add("انتظامی کاموں کا وقت محکمے کے موجودہ کام کے بوجھ پر منحصر ہے۔")
            }
        }

        return FinalLegalPlan(
            caseId = caseId,
            originalProblem = originalProblem,
            caseSummaryEn = summaryEn,
            caseSummaryUr = summaryUr,
            legalArea = category,
            subCategory = subCategory,
            urgency = facts.detectedUrgency,
            isEmergency = isEmergency,
            emergencyNoticeEn = if (isEmergency) "SAFETY WARNING: If there is an imminent threat to life or severe physical danger, contact Emergency Services (15 Police / 1122 Rescue) immediately." else null,
            emergencyNoticeUr = if (isEmergency) "حفاظتی انتباہ: اگر جان یا شدید جسمانی خطرہ لاحق ہو تو فوری طور پر ایمرجنسی 15 پولیس یا 1122 ریسکیو سے رابطہ کریں۔" else null,
            responsibleAuthority = authority,
            actionSteps = steps,
            documentsRequired = documents,
            documentsRequiredUr = documentsUr,
            evidenceToPreserve = evidence,
            evidenceToPreserveUr = evidenceUr,
            timelineGuidance = timelineEn,
            timelineGuidanceUr = timelineUr,
            hasStrictDeadline = false,
            verifiedSources = verifiedSources,
            uncertainties = uncertainties,
            uncertaintiesUr = uncertaintiesUr,
            needsProfessionalLawyer = needsLawyer,
            lawyerConsultationAdvice = lawyerAdviceEn,
            lawyerConsultationAdviceUr = lawyerAdviceUr
        )
    }
}
