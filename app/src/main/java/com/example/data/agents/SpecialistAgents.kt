package com.example.data.agents

import com.example.data.models.*

interface SpecialistAgent {
    val category: LegalCategory
    fun generateQuestions(facts: CaseFacts, subCategory: String): List<FollowUpQuestion>
    fun getResearchPlan(answers: Map<String, String>, facts: CaseFacts, subCategory: String): ResearchPlan
    fun getResponsibleAuthority(facts: CaseFacts, subCategory: String): AuthorityContact
}

object TrafficAgent : SpecialistAgent {
    override val category = LegalCategory.TRAFFIC

    override fun generateQuestions(facts: CaseFacts, subCategory: String): List<FollowUpQuestion> {
        val questions = mutableListOf<FollowUpQuestion>()

        if (subCategory == "vehicle_seizure") {
            questions.add(
                FollowUpQuestion(
                    id = "seizure_reason",
                    questionEn = "Why was your vehicle detained or taken by the police?",
                    questionUr = "پولیس نے آپ کی گاڑی کس وجہ سے روکی یا تحویل میں لی؟",
                    hintEn = "Select the primary ground stated by the police officer",
                    hintUr = "پولیس اہلکار کی طرف سے بتائی گئی بنیادی وجہ منتخب کریں",
                    options = listOf(
                        QuestionOption("accident_investigation", "Accident investigation / vehicle inspection", "حادثے کی تحقیقات یا گاڑی کا معائنہ"),
                        QuestionOption("missing_documents", "Missing registration or driving licence", "رجسٹریشن یا ڈرائیونگ لائسنس کی عدم موجودگی"),
                        QuestionOption("challan_non_payment", "Unpaid traffic challan / traffic violation", "ٹریفک چالان یا ٹریفک قوانین کی خلاف ورزی"),
                        QuestionOption("fir_criminal_case", "Formal FIR registered against driver/vehicle", "ڈرائیور یا گاڑی کے خلاف باقاعدہ ایف آئی آر کا اندراج"),
                        QuestionOption("unknown_reason", "No clear written reason was provided", "کوئی تحریری وجہ فراہم نہیں کی گئی")
                    ),
                    category = category
                )
            )

            questions.add(
                FollowUpQuestion(
                    id = "vehicle_documents",
                    questionEn = "Do you have the original vehicle registration book/card and valid driving licence?",
                    questionUr = "کیا آپ کے پاس اصل رجسٹریشن بک/کارڈ اور درست ڈرائیونگ لائسنس موجود ہے؟",
                    options = listOf(
                        QuestionOption("have_all_docs", "Yes, I have both original documents available", "جی ہاں، میرے پاس دونوں اصل دستاویزات موجود ہیں"),
                        QuestionOption("have_registration_only", "I have registration, but licence is missing/expired", "رجسٹریشن ہے لیکن لائسنس گمشدہ یا میعاد ختم ہے"),
                        QuestionOption("vehicle_in_other_name", "Vehicle is registered in another person's name", "گاڑی کسی دوسرے شخص کے نام پر رجسٹرڈ ہے"),
                        QuestionOption("no_docs", "Neither document is currently accessible", "فی الحال دونوں دستاویزات دستیاب نہیں ہیں")
                    ),
                    category = category
                )
            )

            questions.add(
                FollowUpQuestion(
                    id = "chitral_police_station",
                    questionEn = "Which police station or jurisdiction in Chitral currently holds the vehicle?",
                    questionUr = "چترال کے کس تھانے یا علاقے میں گاڑی تحویل میں ہے؟",
                    options = listOf(
                        QuestionOption("ps_chitral_city", "Police Station Chitral City (Thana Chitral)", "تھانہ چترال سٹی"),
                        QuestionOption("ps_ayun_drosh", "Police Station Ayun or Drosh (Lower Chitral)", "تھانہ ایون یا دروش (لوئر چترال)"),
                        QuestionOption("ps_booni_upper", "Police Station Booni / Mastuj (Upper Chitral)", "تھانہ بونی یا مستوج (اپر چترال)"),
                        QuestionOption("nhmp_lowari", "National Highway / Lowari Tunnel NHMP Post", "قومی شاہراہ / لواری ٹنل موٹروے پولیس")
                    ),
                    category = category
                )
            )
        } else {
            // General Accident or Traffic Issue
            questions.add(
                FollowUpQuestion(
                    id = "injury_status",
                    questionEn = "Was any person injured in the collision or incident?",
                    questionUr = "کیا اس حادثے میں کوئی شخص زخمی ہوا ہے؟",
                    hintEn = "Determines whether medical legal certificate (MLC) or Section 320/337 PPC applies",
                    hintUr = "طبی قانونی سرٹیفکیٹ اور تعزیرات پاکستان کے نفاذ کا تعین کرتا ہے",
                    options = listOf(
                        QuestionOption("no_injury", "No injuries, only vehicular/property damage occurred", "کوئی زخمی نہیں، صرف گاڑی یا مالی نقصان ہوا ہے"),
                        QuestionOption("minor_injury", "Minor injuries, first aid received locally", "معمولی چوٹیں، مقامی طور پر ابتدائی طبی امداد لی گئی"),
                        QuestionOption("serious_injury", "Serious injury requiring hospital admission", "شدید چوٹ جس کے لیے ہسپتال داخل کرایا گیا")
                    ),
                    category = category
                )
            )

            questions.add(
                FollowUpQuestion(
                    id = "evidence_status",
                    questionEn = "Do you have photographs of the vehicle damage and scene?",
                    questionUr = "کیا آپ کے پاس جائے حادثہ اور گاڑی کے نقصان کی تصاویر موجود ہیں؟",
                    options = listOf(
                        QuestionOption("photos_ready", "Yes, photographs and driver details recorded", "جی ہاں، تصاویر اور ڈرائیور کی تفصیلات محفوظ ہیں"),
                        QuestionOption("need_to_take", "No, but I can still photograph the vehicles", "نہیں، لیکن میں ابھی تصاویر لے سکتا ہوں"),
                        QuestionOption("no_photos", "Vehicles have already been moved away", "گاڑیاں موقع سے ہٹائی جا چکی ہیں")
                    ),
                    category = category
                )
            )
        }

        return questions
    }

    override fun getResearchPlan(answers: Map<String, String>, facts: CaseFacts, subCategory: String): ResearchPlan {
        return ResearchPlan(
            researchQuestions = listOf(
                "What statutory authority governs vehicle impoundment and release under KP Motor Vehicles Ordinance 1965?",
                "What is the procedure for Superdari (custody release) of vehicle under Section 516-A CrPC in Chitral Courts?",
                "Which authority in Chitral issues vehicle clearance: DPO Traffic Branch or Judicial Magistrate?"
            ),
            jurisdictionsToCheck = listOf("Lower Chitral", "Upper Chitral", "Khyber Pakhtunkhwa"),
            sourceTypesNeeded = listOf("KP Code", "KP Police Standing Orders", "Code of Criminal Procedure 1898"),
            applicableStatutes = listOf(
                "West Pakistan Motor Vehicles Ordinance 1965 (as adapted by KP)",
                "KP Police Act 2017",
                "Section 516-A, Code of Criminal Procedure 1898 (Custody & Disposal of Property Pending Trial)"
            ),
            searchQueries = listOf("vehicle seizure procedure chitral", "superdari car chitral magistrate", "kp police traffic challan rules")
        )
    }

    override fun getResponsibleAuthority(facts: CaseFacts, subCategory: String): AuthorityContact {
        return AuthorityContact(
            nameEn = "Office of the District Police Officer (DPO) / Traffic Branch Chitral",
            nameUr = "دفتر ڈسٹرکٹ پولیس آفیسر (ڈی پی او) / ٹریفک برانچ چترال",
            departmentEn = "Khyber Pakhtunkhwa Police - District Lower Chitral",
            departmentUr = "خیبر پختونخوا پولیس - ضلع لوئر چترال",
            jurisdiction = "District Lower Chitral (Main Police Line, Chitral City)",
            officeAddressChitral = "DPO Office, Police Lines Road, Chitral City, Khyber Pakhtunkhwa",
            procedureSummary = "For vehicle release without FIR: produce original documents to Traffic Incharge or Moharrir. If seized under criminal case or Section 550 CrPC: file an application for Superdari before the Judicial Magistrate Chitral.",
            procedureSummaryUr = "اگر بغیر ایف آئی آر گاڑی روکی گئی ہو تو اصل کاغذات ٹریفک انچارج یا محرر کو دکھائیں۔ اگر دفعہ 550 ضابطہ فوجداری کے تحت تحویل میں ہو تو جوڈیشل مجسٹریٹ چترال کی عدالت میں سپرداری کی درخواست جمع کروائیں۔",
            officialSource = "Khyber Pakhtunkhwa Police Act 2017 & KP Motor Vehicles Ordinance 1965",
            helpline = "Chitral Police Control: 0943-412222 / Emergency: 15"
        )
    }
}

object DomicileAgent : SpecialistAgent {
    override val category = LegalCategory.DOMICILE

    override fun generateQuestions(facts: CaseFacts, subCategory: String): List<FollowUpQuestion> {
        return listOf(
            FollowUpQuestion(
                id = "domicile_nature",
                questionEn = "What is the specific issue regarding your Chitral Domicile?",
                questionUr = "آپ کے چترال ڈومیسائل سے متعلق مخصوص مسئلہ کیا ہے؟",
                options = listOf(
                    QuestionOption("duplicate_dispute", "Suspected duplicate domicile issued to someone else", "کسی دوسرے شخص کو اسی علاقے سے جعلی یا دہرا ڈومیسائل جاری ہونا"),
                    QuestionOption("record_correction", "Typographical error in name, father's name, or Union Council", "نام، والد کے نام یا یونین کونسل میں غلطی کی درستگی"),
                    QuestionOption("lost_duplicate_copy", "Lost original certificate and need an official certified copy", "اصل ڈومیسائل گم ہو گیا ہے اور مصدقہ نقل درکار ہے"),
                    QuestionOption("first_time_apply", "First-time domicile application for education or employment", "تعلیم یا ملازمت کے لیے پہلی بار ڈومیسائل بنوانا")
                ),
                category = category
            ),
            FollowUpQuestion(
                id = "district_subdivision",
                questionEn = "Which administrative district do you permanently reside in?",
                questionUr = "آپ مستقل طور پر کس ضلع کے رہائشی ہیں؟",
                options = listOf(
                    QuestionOption("lower_chitral", "District Lower Chitral (Chitral, Drosh, Ayun, Lotkoh, Garam Chashma)", "ضلع لوئر چترال (چترال، دروش، ایون، گرم چشمہ)"),
                    QuestionOption("upper_chitral", "District Upper Chitral (Booni, Mastuj, Mulkhow, Torkhow)", "ضلع اپر چترال (بونی، مستوج، تورکھو، موڑکھو)")
                ),
                category = category
            )
        )
    }

    override fun getResearchPlan(answers: Map<String, String>, facts: CaseFacts, subCategory: String): ResearchPlan {
        return ResearchPlan(
            researchQuestions = listOf(
                "What procedure governs cancellation of duplicate/fraudulent domicile in KP?",
                "Which authority verifies permanent residence in Chitral under Pakistan Citizenship Rules 1952?"
            ),
            jurisdictionsToCheck = listOf("Deputy Commissioner Lower Chitral", "Deputy Commissioner Upper Chitral"),
            sourceTypesNeeded = listOf("KP Code", "Pakistan Citizenship Act 1951", "Citizenship Rules 1952"),
            applicableStatutes = listOf(
                "Pakistan Citizenship Act 1951 (Section 17 - Certificate of Domicile)",
                "Pakistan Citizenship Rules 1952 (Rule 23 - Domicile Verification Procedure)"
            ),
            searchQueries = listOf("domicile cancellation procedure kp", "deputy commissioner chitral domicile verification")
        )
    }

    override fun getResponsibleAuthority(facts: CaseFacts, subCategory: String): AuthorityContact {
        val isUpper = facts.location.contains("Upper", ignoreCase = true)
        return if (isUpper) {
            AuthorityContact(
                nameEn = "Office of the Deputy Commissioner, Upper Chitral (Booni)",
                nameUr = "دفتر ڈپٹی کمشنر، ضلع اپر چترال (بونی)",
                departmentEn = "District Administration Upper Chitral, Government of KP",
                departmentUr = "ضلعی انتظامیہ اپر چترال، حکومت خیبر پختونخوا",
                jurisdiction = "District Upper Chitral",
                officeAddressChitral = "DC Complex, Booni Headquarters, Upper Chitral",
                procedureSummary = "Submit application for verification or inquiry regarding duplicate/disputed domicile to the Additional Deputy Commissioner (General) along with Land Revenue verification (Fard/Nakal) and NADRA family registration.",
                procedureSummaryUr = "ڈومیسائل کی تصدیق یا منسوخی کے لیے درخواست ایڈیشنل ڈپٹی کمشنر جنرل اپر چترال کو مع پٹواری تصدیق اور نادرا فیملی سرٹیفکیٹ جمع کروائیں۔",
                officialSource = "Pakistan Citizenship Rules 1952 & KP District Administration Rules",
                helpline = "DC Upper Chitral Office: 0943-470001"
            )
        } else {
            AuthorityContact(
                nameEn = "Office of the Deputy Commissioner, Lower Chitral",
                nameUr = "دفتر ڈپٹی کمشنر، ضلع لوئر چترال",
                departmentEn = "District Administration Lower Chitral, Government of KP",
                departmentUr = "ضلعی انتظامیہ لوئر چترال، حکومت خیبر پختونخوا",
                jurisdiction = "District Lower Chitral",
                officeAddressChitral = "DC Office Complex, Shahi Qila Road, Chitral City, Khyber Pakhtunkhwa",
                procedureSummary = "Domicile branch DC Office Chitral processes inquiries, verifications, and cancellations through the local Tehsildar/Assistant Commissioner report.",
                procedureSummaryUr = "ڈومیسائل برانچ ڈی سی آفس چترال متعلقہ اسسٹنٹ کمشنر یا تحصیلدار کی انکوائری رپورٹ کی بنیاد پر تصدیق یا منسوخی کا حکم جاری کرتی ہے۔",
                officialSource = "Pakistan Citizenship Act 1951, Section 17",
                helpline = "DC Lower Chitral Office: 0943-412519"
            )
        }
    }
}

object CnicAgent : SpecialistAgent {
    override val category = LegalCategory.CNIC

    override fun generateQuestions(facts: CaseFacts, subCategory: String): List<FollowUpQuestion> {
        return listOf(
            FollowUpQuestion(
                id = "cnic_issue_type",
                questionEn = "What type of correction or issue do you have with your CNIC / NADRA record?",
                questionUr = "آپ کے شناختی کارڈ یا نادرا ریکارڈ میں کس قسم کا مسئلہ ہے؟",
                options = listOf(
                    QuestionOption("date_of_birth", "Date of birth mismatch with Matriculation certificate", "میٹرک سند اور شناختی کارڈ کی تاریخ پیدائش میں فرق"),
                    QuestionOption("name_spelling", "Name or father's name spelling mistake", "نام یا والد کے نام کے ہجے کی غلطی"),
                    QuestionOption("biometric_complex", "Biometric / Family tree blockage (suspect status)", "بائیو میٹرک تصدیق یا فیملی ٹری کا بلاک ہونا"),
                    QuestionOption("lost_card", "Lost / Expired National Identity Card", "شناختی کارڈ گمشدہ یا منسوخ ہو گیا ہے")
                ),
                category = category
            )
        )
    }

    override fun getResearchPlan(answers: Map<String, String>, facts: CaseFacts, subCategory: String): ResearchPlan {
        return ResearchPlan(
            researchQuestions = listOf(
                "What statutory documents are required for age modification in NADRA under NADRA Ordinance 2000?",
                "What is the appeal mechanism if NADRA blocks a Chitral resident's CNIC?"
            ),
            jurisdictionsToCheck = listOf("NADRA Chitral Centre", "NADRA Regional HQ Peshawar"),
            sourceTypesNeeded = listOf("NADRA Ordinance 2000", "NADRA SOP for Modification"),
            applicableStatutes = listOf("National Database and Registration Authority Ordinance 2000"),
            searchQueries = listOf("nadra cnic age correction rules", "nadra registration center chitral")
        )
    }

    override fun getResponsibleAuthority(facts: CaseFacts, subCategory: String): AuthorityContact {
        return AuthorityContact(
            nameEn = "NADRA Registration Centre (NRC) Chitral",
            nameUr = "نادرا رجسٹریشن سینٹر (این آر سی) چترال",
            departmentEn = "National Database and Registration Authority, Ministry of Interior",
            departmentUr = "نیشنل ڈیٹا بیس اینڈ رجسٹریشن اتھارٹی، وزارت داخلہ",
            jurisdiction = "Chitral (Lower & Upper Centres)",
            officeAddressChitral = "Main NRC: Bypass Road, Near Polo Ground, Chitral City / Sub-Centre: Booni Bazar",
            procedureSummary = "Visit the NRC with original Matric certificate / educational board verification or court declaratory decree (if age variance exceeds NADRA internal SOP limits).",
            procedureSummaryUr = "اصل تعلیمی اسناد، برتھ سرٹیفکیٹ یا سول کورٹ کے ڈگری کے ساتھ نادرا سینٹر چترال تشریف لے جائیں۔",
            officialSource = "NADRA Ordinance 2000, Section 13",
            helpline = "NADRA Helpline: 1777 (from mobile) or 051-111-786-100"
        )
    }
}

object CyberAgent : SpecialistAgent {
    override val category = LegalCategory.CYBER_HARASSMENT

    override fun generateQuestions(facts: CaseFacts, subCategory: String): List<FollowUpQuestion> {
        return listOf(
            FollowUpQuestion(
                id = "harassment_medium",
                questionEn = "Where and how is the harassment or blackmail taking place?",
                questionUr = "ہراسانی یا بلیک میلنگ کس ذریعے سے کی جا رہی ہے؟",
                hintEn = "Identifies evidence preservation protocol",
                hintUr = "شواہد محفوظ کرنے کے قانونی طریقے کا تعین کرتا ہے",
                options = listOf(
                    QuestionOption("whatsapp_phone", "WhatsApp messages / direct voice calls and threats", "واٹس ایپ پیغامات، وائس کالز اور دھمکیاں"),
                    QuestionOption("social_media_photos", "Facebook / TikTok / Instagram sharing private photos", "فیس بک، انسٹاگرام یا ٹک ٹاک پر ذاتی تصاویر شیئر کرنا"),
                    QuestionOption("financial_extortion", "Demanding money (extortion) under threat of leak", "تصاویر یا معلومات افشا کرنے کی دھمکی دے کر پیسے مانگنا")
                ),
                category = category
            ),
            FollowUpQuestion(
                id = "evidence_preserved",
                questionEn = "Have you preserved full unedited screenshots with visible phone numbers/URLs?",
                questionUr = "کیا آپ نے موبائل نمبر یا پروفائل لنک سمیت مکمل اسکرین شاٹس محفوظ کر لیے ہیں؟",
                options = listOf(
                    QuestionOption("yes_preserved", "Yes, screenshots and call logs are saved safely", "جی ہاں، اسکرین شاٹس اور کال ریکارڈ محفوظ ہیں"),
                    QuestionOption("deleted_some", "I deleted the chat or blocked the person", "میں نے چیٹ ڈیلیٹ کر دی تھی یا بندے کو بلاک کیا ہے"),
                    QuestionOption("ongoing_now", "The blackmailer is actively sending messages right now", "بلیک میلر اس وقت بھی مسلسل پیغامات بھیج رہا ہے")
                ),
                category = category
            )
        )
    }

    override fun getResearchPlan(answers: Map<String, String>, facts: CaseFacts, subCategory: String): ResearchPlan {
        return ResearchPlan(
            researchQuestions = listOf(
                "Which section of PECA 2016 applies to non-consensual dissemination of private images?",
                "What is the statutory procedure to file a complaint with FIA Cyber Crime Wing in KP?"
            ),
            jurisdictionsToCheck = listOf("FIA Cyber Crime Wing Peshawar / KP", "Sessions Court Chitral (PECA Court)"),
            sourceTypesNeeded = listOf("Prevention of Electronic Crimes Act 2016 (PECA)"),
            applicableStatutes = listOf(
                "Section 21, PECA 2016 (Offences against modesty of a person and minor)",
                "Section 24, PECA 2016 (Cyber Stalking)",
                "Section 20, PECA 2016 (Offences against dignity of natural person)"
            ),
            searchQueries = listOf("peca section 21 private images fia", "fia cyber crime complaint portal pakistan")
        )
    }

    override fun getResponsibleAuthority(facts: CaseFacts, subCategory: String): AuthorityContact {
        return AuthorityContact(
            nameEn = "FIA National Response Centre for Cyber Crime (NR3C) - KP Circle",
            nameUr = "ایف آئی اے نیشنل رسپانس سینٹر فار سائبر کرائم - خیبر پختونخوا",
            departmentEn = "Federal Investigation Agency, Government of Pakistan",
            departmentUr = "فیڈرل انویسٹی گیشن ایجنسی، حکومت پاکستان",
            jurisdiction = "Khyber Pakhtunkhwa & Chitral (PECA Exclusive Jurisdiction)",
            officeAddressChitral = "FIA Cyber Crime Reporting Centre, Peshawar / Online Portal complaint.fia.gov.pk",
            procedureSummary = "Submit an online complaint immediately through complaint.fia.gov.pk or visit FIA Cybercrime Centre. Do NOT negotiate with or pay money to the blackmailer.",
            procedureSummaryUr = "فوری طور پر complaint.fia.gov.pk پر آن لائن شکایت درج کریں۔ کسی بھی صورت بلیک میلر کو پیسے نہ دیں اور تمام اسکرین شاٹس محفوظ رکھیں۔",
            officialSource = "Prevention of Electronic Crimes Act 2016 (PECA), Section 29",
            helpline = "FIA Cybercrime Helpline: 1991 / 051-9106384"
        )
    }
}

object LandPropertyAgent : SpecialistAgent {
    override val category = LegalCategory.LAND_PROPERTY

    override fun generateQuestions(facts: CaseFacts, subCategory: String): List<FollowUpQuestion> {
        return listOf(
            FollowUpQuestion(
                id = "land_record_type",
                questionEn = "What is the status of the land revenue records for this property in Chitral?",
                questionUr = "چترال میں اس زمین کے سرکاری ریونیو ریکارڈ کی کیا صورتحال ہے؟",
                options = listOf(
                    QuestionOption("settlement_fard_exists", "Regular settlement done, Fard / Jamabandi exists in Patwarkhana", "باقاعدہ بندوبست مکمل ہے، پٹوار خانے میں فرد/جمع بندی موجود ہے"),
                    QuestionOption("customary_shamilat", "Customary village or communal land (Shamilat / Qaum record)", "روایتی دیہی یا مشترکہ اراضی (شاملات دہ)"),
                    QuestionOption("unregistered_stamp", "Oral agreement or simple stamp paper without mutation (Intiqal)", "زبانی سودا یا بغیر انتقال سادہ اسٹامپ پیپر")
                ),
                category = category
            ),
            FollowUpQuestion(
                id = "dispute_nature",
                questionEn = "What is the primary nature of the land dispute?",
                questionUr = "زمین کے تنازع کی بنیادی نوعیت کیا ہے؟",
                options = listOf(
                    QuestionOption("boundary_encroachment", "Boundary encroachment / illegal construction by neighbour", "حد براری کا تنازع یا ہمسائے کی غیر قانونی تعمیر"),
                    QuestionOption("inheritance_claim", "Inheritance share denied by family members", "خاندانی وراثت میں شرعی یا قانونی حصہ نہ ملنا"),
                    QuestionOption("dispossession_threat", "Threat of forceful illegal dispossession", "زبردستی غیر قانونی بے دخلی کا خطرہ")
                ),
                category = category
            )
        )
    }

    override fun getResearchPlan(answers: Map<String, String>, facts: CaseFacts, subCategory: String): ResearchPlan {
        return ResearchPlan(
            researchQuestions = listOf(
                "What is the demarcation procedure under Section 117 of West Pakistan Land Revenue Act 1967 in KP?",
                "Which court possesses jurisdiction for illegal dispossession under Illegal Dispossession Act 2005 in Chitral?"
            ),
            jurisdictionsToCheck = listOf("Chitral Revenue Circles", "Court of Senior Civil Judge Chitral"),
            sourceTypesNeeded = listOf("West Pakistan Land Revenue Act 1967", "Illegal Dispossession Act 2005"),
            applicableStatutes = listOf(
                "West Pakistan Land Revenue Act 1967 (as applied to KP)",
                "Specific Relief Act 1877 (Section 8, 9, 42 & 54)",
                "Illegal Dispossession Act 2005"
            ),
            searchQueries = listOf("land demarcation procedure tehsildar kp", "fard jamabandi chitral revenue department")
        )
    }

    override fun getResponsibleAuthority(facts: CaseFacts, subCategory: String): AuthorityContact {
        return AuthorityContact(
            nameEn = "Office of the Tehsildar & Assistant Commissioner (Revenue) Chitral",
            nameUr = "دفتر تحصیلدار اور اسسٹنٹ کمشنر (محکمہ مال) چترال",
            departmentEn = "Board of Revenue Khyber Pakhtunkhwa / District Revenue Administration",
            departmentUr = "بورڈ آف ریونیو خیبر پختونخوا / ضلعی ریونیو انتظامیہ",
            jurisdiction = "Tehsil Chitral / Tehsil Drosh / Tehsil Booni",
            officeAddressChitral = "Tehsil Revenue Complex, Deputy Commissioner Office Road, Chitral City",
            procedureSummary = "For boundary demarcation: apply to Tehsildar under Section 117 Land Revenue Act. For ownership declaration or injunction against illegal construction: institute a civil suit before Senior Civil Judge Chitral.",
            procedureSummaryUr = "حد براری کے لیے تحصیلدار کو دفعہ 117 کے تحت درخواست دیں۔ ملکیت کے حق یا حکم امتناعی (سٹے آرڈر) کے لیے سینئر سول جج چترال کی عدالت سے رجوع کریں۔",
            officialSource = "West Pakistan Land Revenue Act 1967",
            helpline = "Assistant Commissioner Office Chitral: 0943-412211"
        )
    }
}

object GeneralAgent : SpecialistAgent {
    override val category = LegalCategory.GENERAL

    override fun generateQuestions(facts: CaseFacts, subCategory: String): List<FollowUpQuestion> {
        return listOf(
            FollowUpQuestion(
                id = "general_objective",
                questionEn = "What is the primary legal remedy or assistance you are seeking?",
                questionUr = "آپ بنیادی طور پر کس قسم کی قانونی داد رسی یا رہنمائی چاہتے ہیں؟",
                options = listOf(
                    QuestionOption("identify_office", "Identify which government department has statutory authority", "یہ معلوم کرنا کہ کس سرکاری محکمے کے پاس اختیارات ہیں"),
                    QuestionOption("document_checklist", "Find out the exact documentation checklist required", "درکار سرکاری کاغذات اور اسناد کی فہرست جاننا"),
                    QuestionOption("court_procedure", "Understand whether a lawyer and civil/criminal court is needed", "یہ سمجھنا کہ کیا وکیل یا عدالت سے رجوع ضروری ہے")
                ),
                category = category
            )
        )
    }

    override fun getResearchPlan(answers: Map<String, String>, facts: CaseFacts, subCategory: String): ResearchPlan {
        return ResearchPlan(
            researchQuestions = listOf("What official public authority in Chitral or KP handles this matter?"),
            jurisdictionsToCheck = listOf("Chitral", "Khyber Pakhtunkhwa"),
            sourceTypesNeeded = listOf("Official KP Government Portal", "Peshawar High Court Rules"),
            applicableStatutes = listOf("Relevant KP Provincial Statutes"),
            searchQueries = listOf("government authority chitral kp procedure")
        )
    }

    override fun getResponsibleAuthority(facts: CaseFacts, subCategory: String): AuthorityContact {
        return AuthorityContact(
            nameEn = "District Administration & District Courts Chitral Information Desk",
            nameUr = "ضلعی انتظامیہ اور ڈسٹرکٹ کورٹس چترال معلوماتی ڈیسک",
            departmentEn = "Government of Khyber Pakhtunkhwa / District Judiciary",
            departmentUr = "حکومت خیبر پختونخوا / ضلعی عدلیہ چترال",
            jurisdiction = "District Lower Chitral / Upper Chitral",
            officeAddressChitral = "District Courts & Bar Association Complex, Shahi Qila Road, Chitral City",
            procedureSummary = "Consult the District Bar Association legal aid desk or District Administration Public Facilitation Centre (Khidmat Markaz).",
            procedureSummaryUr = "ڈسٹرکٹ بار ایسوسی ایشن چترال کے پبلک لیگل ایڈ ڈیسک یا ضلعی خدمت مرکز سے رابطہ کریں۔",
            officialSource = "Khyber Pakhtunkhwa Right to Information Act 2013",
            helpline = "Chitral Bar Association: 0943-412435"
        )
    }
}
