package com.example

import com.example.data.ai.AiGeneratorEngine
import com.example.data.model.Language
import com.example.data.model.WebsiteType
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Test

class AiGeneratorTest {

    @Test
    fun testDetectBengaliLanguage() {
        val prompt = "আমি একটি অনলাইন কাপড়ের দোকানের ওয়েবসাইট চাই। নাম হবে XYZ Fashion।"
        val lang = AiGeneratorEngine.detectLanguage(prompt)
        assertEquals(Language.BN, lang)
    }

    @Test
    fun testGenerateFullProjectStructure() {
        val prompt = "আমি একটি অনলাইন কাপড়ের দোকানের ওয়েবসাইট চাই। নাম হবে XYZ Fashion। Home, Products, Cart, Checkout, About Us এবং Contact পেজ থাকবে।"
        val result = AiGeneratorEngine.generateProject(prompt)

        assertEquals("XYZ Fashion", result.name)
        assertEquals(WebsiteType.E_COMMERCE, result.category)

        val filePaths = result.files.map { it.filePath }
        assertTrue("Must contain index.html", filePaths.contains("index.html"))
        assertTrue("Must contain products.html", filePaths.contains("products.html"))
        assertTrue("Must contain cart.html", filePaths.contains("cart.html"))
        assertTrue("Must contain checkout.html", filePaths.contains("checkout.html"))
        assertTrue("Must contain about.html", filePaths.contains("about.html"))
        assertTrue("Must contain contact.html", filePaths.contains("contact.html"))
        assertTrue("Must contain styles.css", filePaths.contains("styles.css"))
        assertTrue("Must contain app.js", filePaths.contains("app.js"))
        assertTrue("Must contain schema.sql", filePaths.contains("schema.sql"))
        assertTrue("Must contain api.ts", filePaths.contains("api.ts"))
        assertTrue("Must contain deployment.json", filePaths.contains("deployment.json"))
        assertTrue("Must contain README.md", filePaths.contains("README.md"))
    }

    @Test
    fun testAiModification() {
        val prompt = "Build clothing shop"
        val project = AiGeneratorEngine.generateProject(prompt)
        val modified = AiGeneratorEngine.applyAiModification(
            existingFiles = project.files,
            userInstruction = "একটি Blog section যোগ করো",
            projectId = 1
        )
        val hasBlog = modified.any { it.filePath == "blog.html" }
        assertTrue("Must add blog.html", hasBlog)
    }
}
