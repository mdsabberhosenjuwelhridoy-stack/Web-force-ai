package com.example.data.model

enum class Language(val code: String, val displayName: String, val nativeName: String) {
    EN("en", "English", "English"),
    BN("bn", "Bengali", "বাংলা"),
    AR("ar", "Arabic", "العربية")
}

object AppStrings {
    fun get(key: String, lang: Language): String {
        return strings[key]?.get(lang) ?: strings[key]?.get(Language.EN) ?: key
    }

    private val strings = mapOf(
        "app_title" to mapOf(
            Language.EN to "WebForge AI",
            Language.BN to "ওয়েবফোর্জ এআই",
            Language.AR to "ويب فورج للذكاء الاصطناعي"
        ),
        "tagline" to mapOf(
            Language.EN to "Turn natural ideas into full-stack responsive websites",
            Language.BN to "আপনার ভাবনামতো সম্পূর্ণ রেসপনসিভ ওয়েবসাইট তৈরি করুন এআই দিয়ে",
            Language.AR to "حوّل أفكارك إلى مواقع ويب متكاملة ومتجاوبة بالذكاء الاصطناعي"
        ),
        "describe_prompt_hint" to mapOf(
            Language.EN to "Describe the website you want to build (e.g., 'An online clothing store named XYZ Fashion with Home, Products, Cart, Checkout, About Us, and Contact pages')...",
            Language.BN to "আপনি যে ধরনের ওয়েবসাইট তৈরি করতে চান তা বাংলায় বা ইংরেজিতে লিখুন (যেমন: 'আমি একটি অনলাইন কাপড়ের দোকানের ওয়েবসাইট চাই। নাম হবে XYZ Fashion। Home, Products, Cart, Checkout, About Us এবং Contact পেজ থাকবে।')...",
            Language.AR to "صف الموقع الذي تريد إنشاءه (مثلاً: 'متجر ملابس إلكتروني باسم XYZ Fashion يحتوي على صفحات الرئيسية، المنتجات، السلة، الدفع، من نحن، واتصل بنا')..."
        ),
        "generate_button" to mapOf(
            Language.EN to "Generate Website",
            Language.BN to "ওয়েবসাইট তৈরি করুন",
            Language.AR to "إنشاء الموقع"
        ),
        "generating" to mapOf(
            Language.EN to "AI is building your website...",
            Language.BN to "এআই আপনার ওয়েবসাইট তৈরি করছে...",
            Language.AR to "الذكاء الاصطناعي يقوم ببناء موقعك..."
        ),
        "nav_dashboard" to mapOf(
            Language.EN to "Dashboard",
            Language.BN to "ড্যাশবোর্ড",
            Language.AR to "لوحة التحكم"
        ),
        "nav_projects" to mapOf(
            Language.EN to "My Projects",
            Language.BN to "আমার প্রজেক্ট",
            Language.AR to "مشاريعي"
        ),
        "nav_templates" to mapOf(
            Language.EN to "Templates",
            Language.BN to "টেমপ্লেট",
            Language.AR to "القوالب"
        ),
        "nav_deployments" to mapOf(
            Language.EN to "Deployments",
            Language.BN to "ডিপ্লয়মেন্ট",
            Language.AR to "النشر"
        ),
        "nav_admin" to mapOf(
            Language.EN to "Admin Panel",
            Language.BN to "অ্যাডমিন প্যানেল",
            Language.AR to "لوحة الإدارة"
        ),
        "nav_billing" to mapOf(
            Language.EN to "Billing & Plans",
            Language.BN to "সাবস্ক্রিপশন ও প্ল্যান",
            Language.AR to "الاشتراكات والخطط"
        ),
        "tab_preview" to mapOf(
            Language.EN to "Live Preview",
            Language.BN to "লাইভ প্রিভিউ",
            Language.AR to "معاينة مباشرة"
        ),
        "tab_code" to mapOf(
            Language.EN to "Code Editor",
            Language.BN to "কোড এডিটর",
            Language.AR to "محرر الكود"
        ),
        "tab_assistant" to mapOf(
            Language.EN to "AI Assistant",
            Language.BN to "এআই অ্যাসিস্ট্যান্ট",
            Language.AR to "مساعد الذكاء الاصطناعي"
        ),
        "tab_deploy" to mapOf(
            Language.EN to "Deploy",
            Language.BN to "ডিপ্লয়",
            Language.AR to "نشر"
        ),
        "ai_fix_button" to mapOf(
            Language.EN to "AI Auto-Fix",
            Language.BN to "এআই অটো-ফিক্স",
            Language.AR to "إصلاح تلقائي بالذكاء الاصطناعي"
        ),
        "export_zip" to mapOf(
            Language.EN to "Export ZIP",
            Language.BN to "জিপ এক্সপোর্ট",
            Language.AR to "تصدير ZIP"
        ),
        "custom_domain" to mapOf(
            Language.EN to "Custom Domain",
            Language.BN to "কাস্টম ডোমেন",
            Language.AR to "نطاق مخصص"
        ),
        "free_tier" to mapOf(
            Language.EN to "FREE Plan",
            Language.BN to "ফ্রি প্ল্যান",
            Language.AR to "الخطة المجانية"
        ),
        "pro_tier" to mapOf(
            Language.EN to "PRO Plan",
            Language.BN to "প্রো প্ল্যান",
            Language.AR to "خطة برو"
        ),
        "business_tier" to mapOf(
            Language.EN to "BUSINESS Plan",
            Language.BN to "বিজনেস প্ল্যান",
            Language.AR to "خطة الأعمال"
        )
    )
}
