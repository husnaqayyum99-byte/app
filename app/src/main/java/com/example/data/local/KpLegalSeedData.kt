package com.example.data.local

/**
 * Verified statutory reference data for Khyber Pakhtunkhwa legal topics.
 * Grounded in the Khyber Pakhtunkhwa Code, Pakistan Code, and provincial judicial precedents.
 */
object KpLegalSeedData {

    val defaultCategories = listOf(
        LegalCategoryEntity(
            categoryId = "property_law",
            nameEn = "Property & Land Revenue Law",
            nameUr = "قانون اراضی و ریونیو",
            descriptionEn = "Land demarcation, boundary disputes (Hadd-Barari), mutation (Intiqal), partition (Taqseem), and dispossession relief in KP.",
            descriptionUr = "چترال اور خیبر پختونخوا میں زمین کی حد براری، انتقال اراضی، مشترکہ کھاتے کی تقسیم اور غیر قانونی بے دخلی کے قانونی حل۔",
            iconName = "landscape",
            displayOrder = 1
        ),
        LegalCategoryEntity(
            categoryId = "family_law",
            nameEn = "Family & Matrimonial Law",
            nameUr = "خاندانی و ازدواجی قوانین",
            descriptionEn = "Dissolution of marriage (Khula/Talaq), child custody (Hizanat), maintenance (Kharch-e-Naan-o-Nafqa), and dower (Haq Mehr) in KP Family Courts.",
            descriptionUr = "خیبر پختونخوا فیملی کورٹس میں خلع، طلاق کی تصدیق، بچوں کی تحویل (حضانت)، خرچہ نان و نفقہ اور حق مہر کی وصولی۔",
            iconName = "family_restroom",
            displayOrder = 2
        ),
        LegalCategoryEntity(
            categoryId = "criminal_procedure",
            nameEn = "Criminal Procedure & Police Powers",
            nameUr = "ضابطہ فوجداری و اختیارات پولیس",
            descriptionEn = "FIR registration, 22-A/22-B petitions before Sessions Court, pre & post arrest bail, and vehicle/property release (Superdari).",
            descriptionUr = "ایف آئی آر کا اندراج، سیشن کورٹ میں 22-اے کی درخواست، قبل از گرفتاری و بعد از گرفتاری ضمانت اور عدالتی سپرداری۔",
            iconName = "gavel",
            displayOrder = 3
        ),
        LegalCategoryEntity(
            categoryId = "traffic_vehicles",
            nameEn = "Traffic, Vehicles & Licensing",
            nameUr = "ٹریفک، گاڑیاں اور ڈرائیونگ لائسنس",
            descriptionEn = "Motor vehicle detention, traffic challans, accident liability under KP Motor Vehicles Ordinance 1965.",
            descriptionUr = "گاڑیوں کی ضبطی، ٹریفک چالان، حادثات اور موٹر وہیکلز آرڈیننس 1965 کے تحت قانونی تقاضے۔",
            iconName = "directions_car",
            displayOrder = 4
        ),
        LegalCategoryEntity(
            categoryId = "cyber_law",
            nameEn = "Cybercrime & Digital Protection",
            nameUr = "سائبر کرائم اور ڈیجیٹل تحفظ",
            descriptionEn = "Online harassment, unauthorized sharing of private pictures, blackmailing, and FIA NR3C remedies under PECA 2016.",
            descriptionUr = "سوشل میڈیا پر ہراسانی، نجی تصاویر کا پھیلاؤ، بلیک میلنگ اور پیکا ایکٹ کے تحت ایف آئی اے میں قانونی چارہ جوئی۔",
            iconName = "security",
            displayOrder = 5
        )
    )

    val defaultTopics = listOf(
        // ================= Property Law =================
        LegalTopicEntity(
            topicId = "prop_demarcation_117",
            categoryId = "property_law",
            titleEn = "Demarcation of Land Boundaries (Hadd-Barari)",
            titleUr = "دفعہ 117 کے تحت زمین کی حد براری اور پیمائش",
            statuteName = "West Pakistan Land Revenue Act, 1967 (KP Adaptation)",
            statuteNameUr = "ویسٹ پاکستان لینڈ ریونیو ایکٹ 1967 (کے پی ایڈاپٹیشن)",
            relevantSections = "Section 117 (Demarcation of Boundaries)",
            jurisdiction = "Tehsil Revenue Courts / Halqa Patwarkhana (Lower & Upper Chitral)",
            summaryEn = "Provides statutory mechanism for determining exact field boundaries when adjoining landowners encroach or boundaries become obscured.",
            summaryUr = "جب ہمسایہ زمیندار تجاوز کرے یا حدود مٹ جائیں تو پٹواری اور قانون گو کے ذریعے سرکاری پیمائش اور حد براری کا قانونی طریقہ کار۔",
            responsibleAuthorityEn = "Tehsildar / Assistant Commissioner (Revenue) Chitral",
            responsibleAuthorityUr = "تحصیلدار / اسسٹنٹ کمشنر (محکمہ مال) چترال",
            requiredDocumentsEn = "1. Original CNIC copy\n2. Certified copy of Fard Jamabandi / Record of Rights\n3. Aks Shajra (Cadastral field map) from Patwari\n4. Written application detailing neighboring survey/Khasra numbers",
            requiredDocumentsUr = "1۔ قومی شناختی کارڈ کی نقل\n2۔ فرد جمع بندی / مثل میعاد کی تصدیق شدہ نقل\n3۔ پٹوار خانے سے جاری کردہ عکس شجرہ\n4۔ ہمسایہ خسرہ نمبرات کی تفصیل مع تحریری درخواست",
            proceduralStepsEn = "1. File application under Section 117 before the Tehsildar.\n2. Tehsildar marks file to Field Qanungo and Halqa Patwari.\n3. Notices served on all adjoining boundary holders.\n4. On-site measurement using Gunter's chain / survey equipment.\n5. Submission of Demarcation Report and demarcation pillars erected.",
            proceduralStepsUr = "1۔ تحصیلدار کی ریونیو کورٹ میں دفعہ 117 کے تحت درخواست جمع کروائیں۔\n2۔ تحصیلدار فیلڈ قانون گو اور حلقہ پٹواری کو موقع معائنہ کا حکم دیتا ہے۔\n3۔ تمام ہمسایہ کھاتہ داروں کو باقاعدہ نوٹس جاری کیا جاتا ہے۔\n4۔ موقع پر باقاعدہ پیمائش اور رپورٹ تیار کی جاتی ہے۔\n5۔ مستقل حد بندی نشانات یا برجی نصب کی جاتی ہے۔",
            officialSourceUrl = "https://kpcode.kp.gov.pk/",
            isKpSpecific = true
        ),
        LegalTopicEntity(
            topicId = "prop_partition_135",
            categoryId = "property_law",
            titleEn = "Partition of Joint Holding (Taqseem-e-Arazi)",
            titleUr = "مشترکہ کھاتے کی قانونی تقسیم (تقسیمِ اراضی)",
            statuteName = "West Pakistan Land Revenue Act, 1967 (KP Adaptation)",
            statuteNameUr = "ویسٹ پاکستان لینڈ ریونیو ایکٹ 1967 (کے پی ایڈاپٹیشن)",
            relevantSections = "Sections 135 to 150 (Partition of Land)",
            jurisdiction = "Revenue Court of Assistant Commissioner (Collector) Chitral",
            summaryEn = "Governs the separation of joint ancestral shares and mutation of exclusive ownership parcels among co-owners.",
            summaryUr = "مشترکہ زرعی یا رہائشی کھاتے میں شریک مالکان کے حصے الگ کر کے باقاعدہ الگ قرعہ جات قائم کرنے کا ضابطہ۔",
            responsibleAuthorityEn = "Assistant Commissioner / Collector Grade-I Chitral",
            responsibleAuthorityUr = "اسسٹنٹ کمشنر / کلکٹر درجہ اول چترال",
            requiredDocumentsEn = "1. Certified Jamabandi of current quadrennial settlement\n2. Shajra Nasab (Pedigree table)\n3. List of co-sharers with residential addresses\n4. Proposed mode of partition (Naqsha 'Alif' / 'Bay')",
            requiredDocumentsUr = "1۔ حالیہ جمع بندی کی مصدقہ نقل\n2۔ شجرہ نسب (خاندانی وراثت)\n3۔ تمام شرکائے کھاتہ کی فہرست مع پتے\n4۔ مجوزہ طریقہ تقسیم اور نقشہ جات",
            proceduralStepsEn = "1. Petition presented to Collector under Section 135.\n2. Proclamation issued and co-owners summoned.\n3. Ascertainment of questions of title or dispute.\n4. Preparation of instrument of partition and formal entry in mutation register.",
            proceduralStepsUr = "1۔ کلکٹر کے روبرو دفعہ 135 کے تحت درخواست دائر کریں۔\n2۔ اششتہار جاری کر کے تمام شرکاء کو طلب کیا جاتا ہے۔\n3۔ ملکیت کے سوالات کا حل اور نقشہ جات کی تیاری۔\n4۔ انتقالِ تقسیم (دستاویز تقسیم) کی ریونیو ریکارڈ میں باقاعدہ منظوری۔",
            officialSourceUrl = "https://revenue.kp.gov.pk/",
            isKpSpecific = true
        ),
        LegalTopicEntity(
            topicId = "prop_illegal_dispossession",
            categoryId = "property_law",
            titleEn = "Relief Against Illegal Dispossession (Qabza Mafia)",
            titleUr = "غیر قانونی بے دخلی کے خلاف کارروائی (حفاظتِ قبضہ)",
            statuteName = "Illegal Dispossession Act, 2005 / Specific Relief Act 1877",
            statuteNameUr = "ال لیگل ڈسپوزیشن ایکٹ 2005 / اسپیسیفک ریلیف ایکٹ 1877",
            relevantSections = "Section 3 & 4 (Illegal Dispossession) / Section 9 Specific Relief Act",
            jurisdiction = "Court of Sessions Judge Chitral (Exclusive Jurisdiction)",
            summaryEn = "Protects lawful occupants and owners against property grabbers, forceful trespass, and illegal eviction without due process.",
            summaryUr = "جائز مالکان یا قابضین کو زبردستی اور غیر قانونی قبضے سے بچانے کے لیے سیشن کورٹ کا فوری اور سخت کارروائی کا طریقہ کار۔",
            responsibleAuthorityEn = "District & Sessions Judge Chitral",
            responsibleAuthorityUr = "ڈسٹرکٹ اینڈ سیشن جج چترال",
            requiredDocumentsEn = "1. Proof of prior peaceful possession (utility bills, lease, witness affidavits)\n2. Revenue title documents (Fard/Sale deed)\n3. Photographs or police complaints of forceful intrusion",
            requiredDocumentsUr = "1۔ سابقہ پرامن قبضے کے ثبوت (بلات، رسیدات، کرایہ نامہ)\n2۔ ملکیتی ریونیو دستاویزات\n3۔ زبردستی قبضے کی تصاویر اور پولیس کمپلینٹ کی نقل",
            proceduralStepsEn = "1. Direct complaint filed before Sessions Court under Section 3 of IDA 2005.\n2. Court orders preliminary inquiry by Police or Revenue Officer.\n3. Attachment of property or interim eviction of encroachers.\n4. Final order restoring possession and criminal sentencing.",
            proceduralStepsUr = "1۔ سیشن کورٹ میں دفعہ 3 کے تحت براہ راست استغاثہ دائر کریں۔\n2۔ عدالت پولیس یا مجسٹریٹ سے ابتدائی انکوائری رپورٹ طلب کرتی ہے۔\n3۔ متنازعہ اراضی کی قرقی یا قابضین کی عبوری بے دخلی کا حکم۔\n4۔ اصل مالک کو قبضہ واگزار کرانے اور ملزمان کو قید و جرمانے کی سزا۔",
            officialSourceUrl = "https://peshawarhighcourt.gov.pk/",
            isKpSpecific = false
        ),

        // ================= Family Law =================
        LegalTopicEntity(
            topicId = "fam_khula_dissolution",
            categoryId = "family_law",
            titleEn = "Dissolution of Marriage via Khula",
            titleUr = "فیملی کورٹ کے ذریعے تنسیخِ نکاح بربنائے خلع",
            statuteName = "KP Family Courts Act, 1964 & Muslim Family Laws Ordinance 1961",
            statuteNameUr = "خیبر پختونخوا فیملی کورٹس ایکٹ 1964 اور مسلم فیملی لاز آرڈیننس 1961",
            relevantSections = "Section 7, 8, 9 & 10 (KP Family Courts Act 1964)",
            jurisdiction = "Family Court / Civil Judge Court (Chitral / Drosh / Booni)",
            summaryEn = "Statutory right of a Muslim wife to seek judicial dissolution of marriage through the Family Court when living together within God's limits becomes impossible.",
            summaryUr = "مسلم خاتون کا شرعی و قانونی حق کہ وہ مصالحت ناممکن ہونے کی صورت میں فیملی کورٹ کے ذریعے خلع کی ڈگری حاصل کر سکے۔",
            responsibleAuthorityEn = "Judge Family Court Chitral",
            responsibleAuthorityUr = "جج فیملی کورٹ چترال",
            requiredDocumentsEn = "1. Original Nikahnama or certified copy from Union Council\n2. Wife's CNIC copy\n3. Details of dowry articles and dower status\n4. List of witnesses and children's birth certificates (if any)",
            requiredDocumentsUr = "1۔ اصل نکاح نامہ یا یونین کونسل سے مصدقہ نقل\n2۔ سائلہ کا قومی شناختی کارڈ\n3۔ حق مہر اور سامانِ جہیز کی تفصیلی فہرست\n4۔ بچوں کے پیدائشی سرٹیفکیٹس اور گواہان کی فہرست",
            proceduralStepsEn = "1. Plaint instituted in Family Court Chitral.\n2. Summons served upon husband by courier and publication.\n3. Pre-trial reconciliation proceedings conducted by the Judge.\n4. If reconciliation fails, court decrees Khula (directing surrender of partial dower if unpaid).\n5. Copy sent to Union Council for statutory 90-day arbitration notice.",
            proceduralStepsUr = "1۔ فیملی کورٹ چترال میں دعویٰ تنسیخ نکاح دائر کریں۔\n2۔ مدعا علیہ کو بذریعہ ڈاک، نوٹس و اخبار اشتہار طلب کیا جاتا ہے۔\n3۔ عدالت پہلے مصالحت کی سنجیدہ کوشش کرتی ہے۔\n4۔ مصالحت کی ناکامی پر عدالت فوری خلع کی ڈگری جاری کرتی ہے۔\n5۔ عدالتی ڈگری کی کاپی یونین کونسل کو موثر نوٹس کے لیے ارسال کی جاتی ہے۔",
            officialSourceUrl = "https://kpcode.kp.gov.pk/",
            isKpSpecific = true
        ),
        LegalTopicEntity(
            topicId = "fam_maintenance_custody",
            categoryId = "family_law",
            titleEn = "Child Maintenance & Custody (Kharch-e-Atfal & Hizanat)",
            titleUr = "نابالغ بچوں کا خرچہ نان و نفقہ اور حضانت (تحویل)",
            statuteName = "KP Family Courts Act 1964 & Guardians and Wards Act 1890",
            statuteNameUr = "کے پی فیملی کورٹس ایکٹ 1964 اور گارڈینز اینڈ وارڈز ایکٹ 1890",
            relevantSections = "Section 5 & Schedule (Family Courts Act), Section 25 (Custody of Minors)",
            jurisdiction = "Guardian Court / Family Court Chitral",
            summaryEn = "Enforces the statutory financial obligation of the father to provide monthly food, clothing, education, and medical expenses for minor children, alongside welfare-based custody determination.",
            summaryUr = "والد کی قانونی ذمہ داری کہ وہ نابالغ بچوں کے ماہوار اخراجات تعلیم، لباس اور علاج ادا کرے۔ ساتھ ہی بچوں کی فلاح و بہبود کے تحت تحویل کا تعین۔",
            responsibleAuthorityEn = "Guardian Judge / Senior Civil Judge Chitral",
            responsibleAuthorityUr = "گارڈین جج / سینئر سول جج چترال",
            requiredDocumentsEn = "1. B-Form / Child Registration Certificate (CRC) from NADRA\n2. School fee slips and medical expense receipts\n3. Evidence of father's income, employment, or property holdings",
            requiredDocumentsUr = "1۔ نادرا بے فارم یا بچوں کے برتھ سرٹیفکیٹس\n2۔ اسکول فیس کے چالان اور میڈیکل رسیدات\n3۔ والد کی آمدنی، تنخواہ کی سلپ یا جائیداد کے ثبوت",
            proceduralStepsEn = "1. Suit for maintenance filed along with application for interim maintenance.\n2. Court fixes interim monthly maintenance at the very first hearing.\n3. Evidence recorded regarding living standards and father's financial capacity.\n4. Execution proceedings with power to attach salary or arrest upon non-payment.",
            proceduralStepsUr = "1۔ خرچہ نان و نفقہ کا دعویٰ مع عبوری خرچے کی درخواست دائر کریں۔\n2۔ عدالت پہلی تاریخ پر ہی ماہانہ عبوری خرچہ مقرر کرتی ہے۔\n3۔ والد کی مالی حیثیت اور بچوں کی ضروریات پر شہادت قلمبند ہوتی ہے۔\n4۔ عدم ادائیگی کی صورت میں والد کی جائیداد قرق یا گرفتاری کا حکم دیا جاتا ہے۔",
            officialSourceUrl = "https://peshawarhighcourt.gov.pk/",
            isKpSpecific = true
        ),

        // ================= Criminal Procedure =================
        LegalTopicEntity(
            topicId = "crim_fir_justice_peace_22a",
            categoryId = "criminal_procedure",
            titleEn = "Remedies for Refusal to Register FIR (Section 22-A CrPC)",
            titleUr = "ایف آئی آر درج نہ ہونے پر سیشن کورٹ میں دفعہ 22-اے کی درخواست",
            statuteName = "Code of Criminal Procedure, 1898 & Police Act",
            statuteNameUr = "ضابطہ فوجداری 1898 اور کے پی پولیس ایکٹ 2017",
            relevantSections = "Section 154 (Information in cognizable cases) & Section 22-A / 22-B CrPC",
            jurisdiction = "Sessions Court Chitral (Acting as Ex-Officio Justice of Peace)",
            summaryEn = "Provides judicial remedy when the local police station refuses to register an FIR regarding a cognizable criminal offense.",
            summaryUr = "جب متعلقہ تھانہ قابل دست اندازی جرم پر ایف آئی آر درج کرنے سے انکار کرے تو سیشن جج کے روبرو قانونی چارہ جوئی۔",
            responsibleAuthorityEn = "Ex-Officio Justice of the Peace / Sessions Judge Chitral",
            responsibleAuthorityUr = "جسٹس آف دی پیس / سیشن جج چترال",
            requiredDocumentsEn = "1. Written complaint previously submitted to SHO / DPO\n2. Postal receipt / registry slip of complaint dispatch\n3. Medico-legal report (MLC) if physical violence occurred\n4. CNIC copy and witness affidavits",
            requiredDocumentsUr = "1۔ تھانے کے محرر یا ڈی پی او کو دی گئی تحریری درخواست کی نقل\n2۔ ڈاک کی رسید (اگر درخواست بذریعہ رجسٹری بھیجی گئی ہو)\n3۔ تشدد کی صورت میں ڈی ایچ کیو ہسپتال کی میڈیکو لیگل رپورٹ\n4۔ سائل کا شناختی کارڈ اور گواہان کے بیان حلفی",
            proceduralStepsEn = "1. File petition under Section 22-A CrPC before Sessions Judge Chitral.\n2. Court issues notice to SHO and calls for comments.\n3. SHO submits written report or appears in person.\n4. If cognizable offense is disclosed, court issues mandatory direction to register FIR.",
            proceduralStepsUr = "1۔ سیشن جج چترال کے روبرو دفعہ 22-اے کے تحت رٹ پٹیشن دائر کریں۔\n2۔ عدالت ایس ایچ او تھانہ سے تحریری رپورٹ طلب کرتی ہے۔\n3۔ محرر یا تفتیشی افسر تفصیلی ریکارڈ پیش کرتا ہے۔\n4۔ جرم ثابت ہونے پر عدالت پولیس کو فوری ایف آئی آر درج کرنے کا حکم صادر کرتی ہے۔",
            officialSourceUrl = "https://pakistancode.gov.pk/",
            isKpSpecific = false
        ),
        LegalTopicEntity(
            topicId = "crim_superdari_516a",
            categoryId = "criminal_procedure",
            titleEn = "Superdari: Interim Release of Seized Vehicle/Property",
            titleUr = "دفعہ 516-اے کے تحت گاڑی یا املاک کی سپرداری (واگزاری)",
            statuteName = "Code of Criminal Procedure, 1898",
            statuteNameUr = "ضابطہ فوجداری 1898",
            relevantSections = "Section 516-A CrPC (Order for custody and disposal of property pending trial)",
            jurisdiction = "Court of Judicial Magistrate Chitral",
            summaryEn = "Governs the application for interim custody and handover of vehicles or personal belongings impounded by police during an investigation or trial.",
            summaryUr = "مقدمے کے دوران تھانے میں بند گاڑی یا دیگر اشیاء کو خراب ہونے سے بچانے کے لیے مجسٹریٹ سے عبوری تحویل حاصل کرنے کا طریقہ۔",
            responsibleAuthorityEn = "Judicial Magistrate Section 30 Chitral",
            responsibleAuthorityUr = "علاقہ جوڈیشل مجسٹریٹ چترال",
            requiredDocumentsEn = "1. Original Registration Book / Smart Card\n2. Copy of Police FIR or Seizure Memo (Fard-e-Maqboozi)\n3. Driving Licence of owner/applicant\n4. Surety / Indemnity bond guarantee",
            requiredDocumentsUr = "1۔ گاڑی کا اصل رجسٹریشن کارڈ یا سمارٹ کارڈ\n2۔ ایف آئی آر یا پولیس ضبطی پرچی (فرد مقبوضی) کی نقل\n3۔ ڈرائیونگ لائسنس اور قومی شناختی کارڈ\n4۔ ضمانتی مچلکہ اور مقامی ضامن کے کاغذات",
            proceduralStepsEn = "1. Move Superdari petition under Section 516-A before Judicial Magistrate.\n2. Police submit report confirming engine/chassis number verification.\n3. Magistrate hears counsel and approves interim custody subject to surety bond.\n4. Release robkar (warrant) issued to SHO / Malkhana incharge.",
            proceduralStepsUr = "1۔ مجسٹریٹ کے روبرو دفعہ 516-اے ضابطہ فوجداری کے تحت درخواست دیں۔\n2۔ تھانے کا محرر گاڑی کے چیسس و انجن نمبر کی تصدیقی رپورٹ دیتا ہے۔\n3۔ مجسٹریٹ مناسب ضمانتی مچلکوں پر گاڑی واگزار کرنے کا حکم جاری کرتا ہے۔\n4۔ تھانے کے مال خانے کے نام عدالتی روبکار جاری ہوتا ہے۔",
            officialSourceUrl = "https://pakistancode.gov.pk/",
            isKpSpecific = false
        ),
        LegalTopicEntity(
            topicId = "crim_pre_arrest_bail_498",
            categoryId = "criminal_procedure",
            titleEn = "Pre-Arrest Bail (Zamanat Qabal-az-Giraftari)",
            titleUr = "قبل از گرفتاری ضمانت (دفعہ 498 ضابطہ فوجداری)",
            statuteName = "Code of Criminal Procedure, 1898",
            statuteNameUr = "ضابطہ فوجداری 1898",
            relevantSections = "Section 498 CrPC (Power to direct admission to bail)",
            jurisdiction = "Court of Additional Sessions Judge Chitral",
            summaryEn = "Protects citizens from humiliation, unjustified custody, or police arrest engineered with ulterior motives and mala fides.",
            summaryUr = "بے گناہ شہری کو بدنیتی، جھوٹے مقدمے یا پولیس کی ناجائز گرفتاری و تذلیل سے بچانے کے لیے سیشن کورٹ سے حفاظتی ضمانت۔",
            responsibleAuthorityEn = "Sessions Court Chitral",
            responsibleAuthorityUr = "سیشن کورٹ چترال",
            requiredDocumentsEn = "1. Copy of FIR and police diary extract (if available)\n2. Affidavit affirming petitioner has never been proclaimed offender\n3. Proof of medical urgency, alibi, or false implication\n4. Local solvent surety with title documents",
            requiredDocumentsUr = "1۔ ایف آئی آر کی مصدقہ نقل\n2۔ سائل کا حلف نامہ کہ وہ اشتہاری ملزم نہیں\n3۔ بے گناہی یا جائے وقوعہ پر عدم موجودگی کا ثبوت\n4۔ چترال کا مقامی معتبر ضامن مع جائیداد کی فرد",
            proceduralStepsEn = "1. Petitioner must appear in person before Sessions Judge.\n2. Court grants ad-interim pre-arrest bail and issues notice to State and complainant.\n3. Police record produced and investigated.\n4. After hearing arguments on mala fide intent, court confirms or recalls bail.",
            proceduralStepsUr = "1۔ ملزم کا عدالت میں اصالتاً پیش ہونا لازمی ہے۔\n2۔ عدالت عبوری ضمانت منظور کر کے پولیس کو ریکارڈ سمیت طلب کرتی ہے۔\n3۔ تھانے کی ضمنیاں اور تفتیشی رپورٹ کا جائزہ لیا جاتا ہے۔\n4۔ دلائل کے بعد عدالت ضمانت کی توثیق یا خارج کرنے کا فیصلہ کرتی ہے۔",
            officialSourceUrl = "https://pakistancode.gov.pk/",
            isKpSpecific = false
        ),

        // ================= Traffic & Vehicles =================
        LegalTopicEntity(
            topicId = "traf_vehicle_impoundment_kp",
            categoryId = "traffic_vehicles",
            titleEn = "Vehicle Impoundment & Inspection Rules in KP",
            titleUr = "خیبر پختونخوا موٹر وہیکلز آرڈیننس کے تحت گاڑیوں کی ضبطی کے قواعد",
            statuteName = "West Pakistan Motor Vehicles Ordinance, 1965 (as adapted by KP)",
            statuteNameUr = "ویسٹ پاکستان موٹر وہیکلز آرڈیننس 1965 (کے پی ایڈاپٹیشن)",
            relevantSections = "Sections 115, 116 & 117 (Inspection & detention of unroadworthy/unregistered vehicles)",
            jurisdiction = "DPO Traffic Branch Chitral / Regional Transport Authority (RTA) KP",
            summaryEn = "Specifies powers and legal limits of traffic police officers when impounding vehicles for missing fitness, documents, or rash driving.",
            summaryUr = "ٹریفک پولیس کے گاڑی روکنے، معائنہ کرنے اور تحویل میں لینے کے قانونی اختیارات اور شہریوں کے تحفظات۔",
            responsibleAuthorityEn = "District Police Officer (Traffic Incharge) Chitral",
            responsibleAuthorityUr = "ڈسٹرکٹ پولیس آفیسر (ٹریفک انچارج) چترال",
            requiredDocumentsEn = "1. Valid Driver's Licence\n2. Motor Vehicle Registration Smart Card\n3. Token tax clearance receipt from KP Excise\n4. Fitness Certificate (for commercial/transport vehicles)",
            requiredDocumentsUr = "1۔ اصل ڈرائیونگ لائسنس\n2۔ گاڑی کا سمارٹ کارڈ\n3۔ ایکسائز ڈیپارٹمنٹ کی ٹوکن ٹیکس ادائیگی رسید\n4۔ کمرشل گاڑیوں کا روٹ پرمٹ و فٹنس سرٹیفکیٹ",
            proceduralStepsEn = "1. Traffic warden must issue computerized or carbonized challan ticket.\n2. Minor violations require fine payment through designated bank/Easypaisa.\n3. Arbitrary seizure without written memo is unlawful under KP Police Act 2017.",
            proceduralStepsUr = "1۔ ٹریفک وارڈن باقاعدہ چالان پرچی جاری کرنے کا پابند ہے۔\n2۔ جرمانہ نامزد بینک یا آن لائن ایپ کے ذریعے ادا کیا جاتا ہے۔\n3۔ تحریری پرچی کے بغیر گاڑی ضبط کرنا غیر قانونی ہے۔",
            officialSourceUrl = "https://transport.kp.gov.pk/",
            isKpSpecific = true
        ),

        // ================= Cyber & PECA =================
        LegalTopicEntity(
            topicId = "cyber_blackmail_peca_21",
            categoryId = "cyber_law",
            titleEn = "Online Blackmail & Private Images Dissemination (PECA)",
            titleUr = "پیکا ایکٹ 2016 کے تحت آن لائن بلیک میلنگ اور نجی تصاویر کا پھیلاؤ",
            statuteName = "Prevention of Electronic Crimes Act, 2016 (PECA)",
            statuteNameUr = "پریوینشن آف الیکٹرانک کرائمز ایکٹ 2016 (پیکا)",
            relevantSections = "Section 21 (Offences against modesty of person) & Section 24 (Cyber Stalking)",
            jurisdiction = "FIA Cyber Crime Wing (NR3C) / Special Court PECA Peshawar",
            summaryEn = "Special federal legislation prescribing up to 5 years rigorous imprisonment for non-consensual transmission of private photos or cyber blackmailing.",
            summaryUr = "کسی کی نجی تصاویر و ویڈیوز اس کی مرضی کے بغیر پھیلانے یا بلیک میل کرنے پر 5 سال تک قیدِ بامشقت اور بھاری جرمانے کی سزا۔",
            responsibleAuthorityEn = "FIA National Response Centre for Cyber Crime (NR3C)",
            responsibleAuthorityUr = "ایف آئی اے سائبر کرائم ونگ (این آر تھری سی)",
            requiredDocumentsEn = "1. High-resolution screenshots showing sender's phone number or URL\n2. Original unedited digital files with EXIF metadata\n3. Audio recordings of threats\n4. Complainant's CNIC copy",
            requiredDocumentsUr = "1۔ فرستندہ کے موبائل نمبر یا پروفائل لنک سمیت واضح اسکرین شاٹس\n2۔ اصل ڈیجیٹل فائل مع ٹائم اور تاریخ کا ریکارڈ\n3۔ دھمکی آمیز کالز یا وائس نوٹس کی آڈیو ریکارڈنگز\n4۔ سائل کا شناختی کارڈ",
            proceduralStepsEn = "1. Register formal complaint on complaint.fia.gov.pk or call 1991.\n2. Do NOT delete messages or pay extortion money.\n3. Content takedown request issued by FIA to social platforms within 24-48 hours.\n4. Tracing of IP address and criminal arrest of culprit.",
            proceduralStepsUr = "1۔ حکومتی پورٹل complaint.fia.gov.pk پر شکایت درج کریں یا 1991 پر کال کریں۔\n2۔ بلیک میلر کو پیسے نہ دیں اور پیغامات ڈیلیٹ نہ کریں۔\n3۔ ایف آئی اے فوری طور پر سوشل میڈیا سے مواد ہٹانے کا نوٹس دیتی ہے۔\n4۔ آئی پی ایڈریس اور لوکیشن ٹریس کر کے ملزم کو گرفتار کیا جاتا ہے۔",
            officialSourceUrl = "https://cybercrime.fia.gov.pk/",
            isKpSpecific = false
        )
    )
}
