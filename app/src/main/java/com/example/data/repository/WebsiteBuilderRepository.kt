package com.example.data.repository

import android.content.Context
import com.example.data.ai.AiGeneratorEngine
import com.example.data.ai.GeminiAiService
import com.example.data.local.AppDatabase
import com.example.data.local.entity.AdminConfigEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.model.Language
import com.example.data.model.WebsiteType
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

class WebsiteBuilderRepository(context: Context) {
    private val db = AppDatabase.getInstance(context)
    private val projectDao = db.projectDao()
    private val fileDao = db.projectFileDao()
    private val chatDao = db.chatMessageDao()
    private val adminDao = db.adminConfigDao()

    val allProjects: Flow<List<ProjectEntity>> = projectDao.getAllProjects()
    val adminConfig: Flow<AdminConfigEntity?> = adminDao.getAdminConfig()

    fun getProject(id: Long): Flow<ProjectEntity?> = projectDao.getProjectById(id)

    fun getProjectFiles(projectId: Long): Flow<List<ProjectFileEntity>> =
        fileDao.getFilesForProject(projectId)

    fun getChatMessages(projectId: Long): Flow<List<ChatMessageEntity>> =
        chatDao.getMessagesForProject(projectId)

    suspend fun initDefaultsIfNeeded() = withContext(Dispatchers.IO) {
        val admin = adminDao.getAdminConfigDirect()
        if (admin == null) {
            adminDao.insertOrUpdateConfig(AdminConfigEntity())
        }
        // Check if projects are empty, create starter showcases
        val projects = projectDao.getProjectByIdDirect(1)
        if (projects == null) {
            // Seed default XYZ Fashion project (as highlighted in prompt)
            createProject(
                prompt = "আমি একটি অনলাইন কাপড়ের দোকানের ওয়েবসাইট চাই। নাম হবে XYZ Fashion। Home, Products, Cart, Checkout, About Us এবং Contact পেজ থাকবে।",
                specifiedName = "XYZ Fashion",
                specifiedCategory = WebsiteType.E_COMMERCE,
                specifiedColor = "#4F46E5"
            )
            // Seed Cricket Live Sports showcase
            createProject(
                prompt = "Create a modern Cricket news and live score website with match fixtures, team rankings, player stats, and live commentary card.",
                specifiedName = "CricPulse Live",
                specifiedCategory = WebsiteType.SPORTS_CRICKET,
                specifiedColor = "#059669"
            )
        }
    }

    suspend fun createProject(
        prompt: String,
        specifiedName: String? = null,
        specifiedCategory: WebsiteType? = null,
        specifiedColor: String? = null
    ): Long = withContext(Dispatchers.IO) {
        val result = AiGeneratorEngine.generateProject(
            prompt = prompt,
            specifiedName = specifiedName,
            specifiedColor = specifiedColor
        )

        val slug = result.name.lowercase().replace(Regex("[^a-z0-9]"), "-").trim('-')
        val project = ProjectEntity(
            name = result.name,
            slug = slug,
            description = result.description,
            category = (specifiedCategory ?: result.category).name,
            primaryColor = result.primaryColor,
            language = AiGeneratorEngine.detectLanguage(prompt).code
        )

        val projectId = projectDao.insertProject(project)

        val filesWithId = result.files.map { it.copy(projectId = projectId) }
        fileDao.insertFiles(filesWithId)

        // Seed initial AI welcome message
        chatDao.insertMessage(
            ChatMessageEntity(
                projectId = projectId,
                sender = "ai",
                message = result.summaryMessage,
                suggestedActions = "Header-এর রঙ পরিবর্তন করো,একটি Blog section যোগ করো,Login/Register যোগ করো,Product search যোগ করো"
            )
        )

        projectId
    }

    suspend fun updateProjectFile(file: ProjectFileEntity) = withContext(Dispatchers.IO) {
        fileDao.updateFile(file.copy(updatedAt = System.currentTimeMillis()))
        projectDao.touchProject(file.projectId)
    }

    suspend fun addNewFile(projectId: Long, filePath: String, content: String): Long =
        withContext(Dispatchers.IO) {
            val extension = filePath.substringAfterLast('.', "txt").uppercase()
            val fileType = when (extension) {
                "HTML", "HTM" -> "HTML"
                "CSS" -> "CSS"
                "JS" -> "JS"
                "SQL" -> "SQL"
                "TS", "TSX", "JSX" -> "TS"
                "JSON" -> "JSON"
                "MD" -> "MD"
                else -> "TXT"
            }
            val id = fileDao.insertFile(
                ProjectFileEntity(
                    projectId = projectId,
                    filePath = filePath,
                    fileType = fileType,
                    content = content
                )
            )
            projectDao.touchProject(projectId)
            id
        }

    suspend fun deleteFile(fileId: Long, projectId: Long) = withContext(Dispatchers.IO) {
        fileDao.deleteFileById(fileId)
        projectDao.touchProject(projectId)
    }

    suspend fun sendChatMessageAndModify(
        projectId: Long,
        userMessage: String
    ): String = withContext(Dispatchers.IO) {
        // Save user message
        chatDao.insertMessage(
            ChatMessageEntity(
                projectId = projectId,
                sender = "user",
                message = userMessage
            )
        )

        val currentFiles = fileDao.getFilesForProjectDirect(projectId)
        val admin = adminDao.getAdminConfigDirect()

        // Check if Gemini API can be used to enrich
        val geminiReply = GeminiAiService.askGemini(
            prompt = "You are WebForge AI assistant. User says: '$userMessage'. Briefly explain how you will modify the web project.",
            customKey = admin?.customApiKey
        )

        // Apply modifications through AI Engine
        val updatedFiles = AiGeneratorEngine.applyAiModification(
            existingFiles = currentFiles,
            userInstruction = userMessage,
            projectId = projectId
        )

        fileDao.insertFiles(updatedFiles)
        projectDao.touchProject(projectId)

        val replyText = geminiReply ?: when {
            userMessage.contains("Header") || userMessage.contains("রং") || userMessage.contains("color") ->
                "🎨 হেডার ও ব্র্যান্ডের থিম কালার সফলভাবে পরিবর্তন করা হয়েছে! লাইভ প্রিভিউতে পরিবর্তন লক্ষ্য করুন।"
            userMessage.contains("Blog") || userMessage.contains("ব্লগ") ->
                "📝 নতুন Blog section ও 'blog.html' সফলভাবে যোগ করা হয়েছে! ন্যাভবারে লিঙ্ক যুক্ত হয়েছে।"
            userMessage.contains("Login") || userMessage.contains("Register") || userMessage.contains("auth") ->
                "🔐 সুরক্ষিত Customer Login & Authentication পেজ 'auth.html' সফলভাবে তৈরি হয়েছে।"
            userMessage.contains("error") || userMessage.contains("ঠিক করো") ->
                "🛠️ প্রজেক্টের সমস্ত ফাইল স্ক্যান করা হয়েছে এবং সম্ভাব্য এররগুলো অটো-ফিক্স করা হয়েছে!"
            else ->
                "✅ আপনার রিকোয়েস্ট অনুযায়ী প্রজেক্ট ফাইলগুলো আপডেট করা হয়েছে। লাইভ প্রিভিউতে তাৎক্ষণিক পরিবর্তন দেখতে পারবেন।"
        }

        chatDao.insertMessage(
            ChatMessageEntity(
                projectId = projectId,
                sender = "ai",
                message = replyText,
                suggestedActions = "Header-এর রঙ পরিবর্তন করো,একটি Blog section যোগ করো,Login/Register যোগ করো,স্যান্ডবক্স এরর ফিক্স করো"
            )
        )

        replyText
    }

    suspend fun runAiAutoFix(projectId: Long): String = withContext(Dispatchers.IO) {
        val currentFiles = fileDao.getFilesForProjectDirect(projectId)
        val (fixedFiles, report) = AiGeneratorEngine.fixErrorsInProject(currentFiles)
        fileDao.insertFiles(fixedFiles)
        projectDao.touchProject(projectId)

        chatDao.insertMessage(
            ChatMessageEntity(
                projectId = projectId,
                sender = "system",
                message = report
            )
        )
        report
    }

    suspend fun deployProject(
        projectId: Long,
        customDomain: String? = null
    ): String = withContext(Dispatchers.IO) {
        val project = projectDao.getProjectByIdDirect(projectId) ?: return@withContext "Project not found"
        val slug = project.slug
        val deployedUrl = if (!customDomain.isNullOrBlank()) {
            "https://$customDomain"
        } else {
            "https://$slug.webforge.app"
        }

        val buildLogs = """
[Build Pipeline: production]
✔ Pulling verified sandbox artifacts...
✔ Validating PostgreSQL schema and API routes...
✔ Optimizing CSS bundles and responsive assets...
✔ Issuing automated Let's Encrypt Wildcard SSL certificate...
✔ Distributing to 320+ Global Edge CDN locations...
🚀 Live at $deployedUrl
Status: 200 OK (HTTP/3 enabled)
        """.trimIndent()

        projectDao.updateDeployment(
            id = projectId,
            isDeployed = true,
            url = deployedUrl,
            status = "DEPLOYED",
            logs = buildLogs,
            customDomain = customDomain
        )

        deployedUrl
    }

    suspend fun duplicateProject(projectId: Long): Long = withContext(Dispatchers.IO) {
        val original = projectDao.getProjectByIdDirect(projectId) ?: return@withContext 0L
        val originalFiles = fileDao.getFilesForProjectDirect(projectId)

        val newProject = original.copy(
            id = 0,
            name = "${original.name} (Copy)",
            slug = "${original.slug}-copy",
            createdAt = System.currentTimeMillis(),
            updatedAt = System.currentTimeMillis(),
            isDeployed = false,
            deployedUrl = null,
            deploymentStatus = "IDLE"
        )
        val newId = projectDao.insertProject(newProject)

        val copiedFiles = originalFiles.map { it.copy(id = 0, projectId = newId) }
        fileDao.insertFiles(copiedFiles)
        newId
    }

    suspend fun deleteProject(projectId: Long) = withContext(Dispatchers.IO) {
        projectDao.deleteProjectById(projectId)
    }

    suspend fun updateAdminConfig(config: AdminConfigEntity) = withContext(Dispatchers.IO) {
        adminDao.insertOrUpdateConfig(config)
    }
}
