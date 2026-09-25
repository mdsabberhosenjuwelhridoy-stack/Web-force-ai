package com.example.data.model

enum class WebsiteType(
    val id: String,
    val titleEn: String,
    val titleBn: String,
    val titleAr: String,
    val iconName: String,
    val defaultPages: List<String>,
    val defaultFeatures: List<String>,
    val samplePromptEn: String,
    val samplePromptBn: String
) {
    E_COMMERCE(
        id = "ecommerce",
        titleEn = "E-Commerce Store",
        titleBn = "অনলাইন কাপড়ের/পণ্যের দোকান",
        titleAr = "متجر إلكتروني",
        iconName = "shopping_cart",
        defaultPages = listOf("Home", "Products", "Cart", "Checkout", "About Us", "Contact"),
        defaultFeatures = listOf("Product Catalog", "Cart System", "Checkout Flow", "Search & Filters", "Reviews"),
        samplePromptEn = "Build an online clothing store named XYZ Fashion. Include Home, Products, Cart, Checkout, About Us, and Contact pages with responsive product cards, cart drawer, and order confirmation.",
        samplePromptBn = "আমি একটি অনলাইন কাপড়ের দোকানের ওয়েবসাইট চাই। নাম হবে XYZ Fashion। Home, Products, Cart, Checkout, About Us এবং Contact পেজ থাকবে।"
    ),
    SPORTS_CRICKET(
        id = "sports",
        titleEn = "Cricket & Sports Live",
        titleBn = "ক্রিকেট ও স্পোর্টস লাইভ পোর্টাল",
        titleAr = "موقع رياضي ونتائج مباشرة",
        iconName = "sports_cricket",
        defaultPages = listOf("Home", "Live Scores", "Teams", "Fixtures", "Points Table", "News"),
        defaultFeatures = listOf("Live Scoreboard", "Player Profiles", "Match Schedules", "Dark Stadium Mode"),
        samplePromptEn = "Create a modern Cricket news and live score website with match fixtures, team rankings, player stats, and live commentary card.",
        samplePromptBn = "একটি আধুনিক ক্রিকেট লাইভ স্কোর ও স্পোর্টস নিউজ ওয়েবসাইট তৈরি করো যার নাম হবে CricPulse। লাইভ স্কোর, টিম এবং ফিক্সচার থাকবে।"
    ),
    RESTAURANT(
        id = "restaurant",
        titleEn = "Restaurant & Cafe",
        titleBn = "রেস্টুরেন্ট ও ফুড ক্যাফে",
        titleAr = "مطعم ومقهى",
        iconName = "restaurant",
        defaultPages = listOf("Home", "Menu", "Special Offers", "Table Reservation", "Gallery", "Contact"),
        defaultFeatures = listOf("Interactive Menu with Filtering", "Online Table Booking", "Chef Specialties", "Customer Reviews"),
        samplePromptEn = "Create a premium restaurant website named Gourmet Bistro with dynamic menu categories, online table reservation form, and food gallery.",
        samplePromptBn = "একটি চমৎকার রেস্টুরেন্ট ওয়েবসাইট তৈরি করো Gourmet Bistro নামে। মেনু ফিল্টারিং, টেবিল বুকিং ও রিভিউ থাকবে।"
    ),
    SAAS_LANDING(
        id = "saas",
        titleEn = "SaaS & Tech Platform",
        titleBn = "স্যাস ও সফটওয়্যার ল্যান্ডিং পেজ",
        titleAr = "منصة برمجيات SaaS",
        iconName = "rocket_launch",
        defaultPages = listOf("Home", "Features", "Pricing", "Testimonials", "Documentation", "Contact"),
        defaultFeatures = listOf("Pricing Plan Switcher", "Feature Comparison", "Interactive Demo", "Newsletter Signup"),
        samplePromptEn = "Build a modern SaaS landing page named FlowCraft with interactive monthly/yearly pricing switcher, product screenshot showcase, and FAQ accordion.",
        samplePromptBn = "একটি আধুনিক SaaS ওয়েবসাইট তৈরি করো যার নাম FlowCraft। প্রাইসিং টেবিল, ফিচার লিস্ট এবং এফএকিউ থাকবে।"
    ),
    PORTFOLIO(
        id = "portfolio",
        titleEn = "Creative Portfolio",
        titleBn = "প্রফেশনাল পোর্টফোলিও",
        titleAr = "معرض أعمال شخصي",
        iconName = "person",
        defaultPages = listOf("Home", "Projects", "Skills", "Experience", "Testimonials", "Contact"),
        defaultFeatures = listOf("Filterable Projects Grid", "Skills Progress Indicators", "Download Resume Button", "Contact Form"),
        samplePromptEn = "Create a sleek personal developer & designer portfolio with project case studies, tech stack badges, and interactive contact form.",
        samplePromptBn = "আমার জন্য একটি চমৎকার প্রফেশনাল পোর্টফোলিও ওয়েবসাইট তৈরি করো। আমার প্রজেক্টস, স্কিলস এবং কন্টাক্ট ফরম থাকবে।"
    ),
    NEWS_BLOG(
        id = "news_blog",
        titleEn = "News & Magazine Portal",
        titleBn = "নিউজ ও ম্যাগাজিন পোর্টাল",
        titleAr = "بوابة أخبار ومجلة",
        iconName = "newspaper",
        defaultPages = listOf("Home", "Breaking News", "Technology", "Business", "Opinion", "Contact"),
        defaultFeatures = listOf("Breaking News Ticker", "Trending Articles", "Category Filter", "Newsletter Subscription"),
        samplePromptEn = "Build a modern responsive news magazine website with breaking news marquee, categorized article feeds, and reading progress indicator.",
        samplePromptBn = "একটি প্রফেশনাল অনলাইন নিউজ পোর্টাল তৈরি করো। ব্রেকিং নিউজ, ট্রেন্ডিং টপিক এবং ক্যাটাগরি ফিল্টার থাকবে।"
    ),
    REAL_ESTATE(
        id = "real_estate",
        titleEn = "Real Estate & Housing",
        titleBn = "রিয়েল এস্টেট ও প্রপার্টি",
        titleAr = "عقارات وإسكان",
        iconName = "apartment",
        defaultPages = listOf("Home", "Properties", "Agents", "Mortgage Calculator", "Virtual Tours", "Contact"),
        defaultFeatures = listOf("Property Search Filter", "Price Slider", "Interactive Property Cards", "Agent Contact"),
        samplePromptEn = "Create a luxury real estate portal named Skyline Living with property search filters, bedroom count, price range, and agent inquiry modal.",
        samplePromptBn = "একটি আধুনিক রিয়েল এস্টেট ওয়েবসাইট তৈরি করো যার নাম Skyline Living। প্রপার্টি ফিল্টার ও এজেন্ট কন্টাক্ট থাকবে।"
    ),
    EDUCATION(
        id = "education",
        titleEn = "School & Academy",
        titleBn = "স্কুল, কলেজ ও একাডেমি",
        titleAr = "مدرسة وأكاديمية تعليمية",
        iconName = "school",
        defaultPages = listOf("Home", "Courses", "Faculty", "Admissions", "Notice Board", "Contact"),
        defaultFeatures = listOf("Course Directory", "Online Admission Application", "Notice Board", "Student Testimonials"),
        samplePromptEn = "Build an educational institute website with course catalog, notice board announcements, admission inquiry form, and faculty list.",
        samplePromptBn = "একটি আধুনিক একাডেমি ও কলেজ ওয়েবসাইট তৈরি করো যেখানে কোর্স তালিকা, ভর্তি আবেদন ও নোটিশ বোর্ড থাকবে।"
    ),
    CUSTOM(
        id = "custom",
        titleEn = "Custom Any Website",
        titleBn = "কাস্টম যেকোনো ওয়েবসাইট",
        titleAr = "موقع مخصص حسب رغبتك",
        iconName = "auto_awesome",
        defaultPages = listOf("Home", "About", "Services", "Contact"),
        defaultFeatures = listOf("Responsive Design", "Modern UI", "Interactive Elements", "SEO Ready"),
        samplePromptEn = "Build a unique custom interactive website based entirely on my custom prompt description.",
        samplePromptBn = "আমার নিজের মতো করে সম্পূর্ণ কাস্টম একটি ওয়েবসাইট তৈরি করো।"
    );

    fun getTitle(lang: Language): String {
        return when (lang) {
            Language.BN -> titleBn
            Language.AR -> titleAr
            Language.EN -> titleEn
        }
    }

    fun getSamplePrompt(lang: Language): String {
        return when (lang) {
            Language.BN -> samplePromptBn
            else -> samplePromptEn
        }
    }
}

enum class GenerationStep(
    val stepNumber: Int,
    val titleEn: String,
    val titleBn: String,
    val titleAr: String,
    val descriptionEn: String,
    val descriptionBn: String
) {
    ANALYZING_REQUIREMENTS(1, "Requirement Analysis", "প্রয়োজনীয়তা বিশ্লেষণ", "تحليل المتطلبات", "Understanding natural language prompt, pages, and features...", "আপনার প্রম্পট, পেজ ও ফিচারসমূহের চাহিদা বোঝা হচ্ছে..."),
    PROJECT_SPECIFICATION(2, "Project Specification", "প্রজেক্ট স্পেসিফিকেশন", "مواصفات المشروع", "Structuring website architecture, user flows, and tech stack...", "ওয়েবসাইটের আর্কিটেকচার ও টেক স্ট্যাক নির্ধারণ হচ্ছে..."),
    ARCHITECTURE_GENERATION(3, "Architecture Generation", "আর্কিটেকচার জেনারেশন", "توليد الهيكل التقني", "Designing PostgreSQL schema, REST API routes, and state...", "ডেটাবেস স্কিমা, এপিআই রুট এবং স্টেট ম্যানেজমেন্ট গঠন হচ্ছে..."),
    FILE_STRUCTURE_GENERATION(4, "File Structure Generation", "ফাইল স্ট্রাকচার তৈরি", "إنشاء هيكل الملفات", "Organizing HTML, CSS, JS, components, and configs...", "HTML, CSS, JS ও ব্যাকএন্ড ফাইলসমূহ সাজানো হচ্ছে..."),
    CODE_GENERATION(5, "Code Generation", "কোড তৈরি হচ্ছে", "توليد الكود البرمجي", "Generating responsive UI, Tailwind styling, interactive JS...", "রেসপনসিভ UI, স্টাইলিং ও ইন্টারঅ্যাক্টিভ কোড লেখা হচ্ছে..."),
    SANDBOX_BUILD(6, "Sandbox Build & Lint", "স্যান্ডবক্স বিল্ড ও লিন্ট", "بناء وفحص البيئة التجريبية", "Compiling sandbox assets, verifying scripts, error detection...", "স্যান্ডবক্সে কোড কম্পাইল এবং এরর চেক করা হচ্ছে..."),
    READY(7, "Live Preview Ready", "লাইভ প্রিভিউ প্রস্তুত", "المعاينة المباشرة جاهزة", "Website built successfully! Ready to preview, edit, or deploy.", "ওয়েবসাইট সফলভাবে তৈরি হয়েছে! প্রিভিউ ও এডিট করুন।");

    fun getTitle(lang: Language): String = when (lang) {
        Language.BN -> titleBn
        Language.AR -> titleAr
        Language.EN -> titleEn
    }

    fun getDescription(lang: Language): String = when (lang) {
        Language.BN -> descriptionBn
        else -> descriptionEn
    }
}
