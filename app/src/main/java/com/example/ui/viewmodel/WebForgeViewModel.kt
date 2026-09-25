package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.AdminConfigEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.GenerationStep
import com.example.data.model.Language
import com.example.data.model.WebsiteType
import com.example.data.repository.WebsiteBuilderRepository
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

enum class ViewportMode(val widthDp: Int?, val label: String) {
    MOBILE(375, "Mobile 375px"),
    TABLET(680, "Tablet 768px"),
    DESKTOP(null, "Desktop 100%")
}

enum class SubscriptionPlan(
    val title: String,
    val priceMonthly: String,
    val priceYearly: String,
    val maxGenerations: Int,
    val maxProjects: Int,
    val customDomain: Boolean,
    val priorityAi: Boolean,
    val teamCollaboration: Boolean
) {
    FREE("FREE", "$0", "$0", 5, 3, false, false, false),
    PRO("PRO", "$19/mo", "$190/yr", 50, 20, true, true, false),
    BUSINESS("BUSINESS", "$49/mo", "$490/yr", 9999, 9999, true, true, true)
}

class WebForgeViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = WebsiteBuilderRepository(application)

    val allProjects: StateFlow<List<ProjectEntity>> = repository.allProjects
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val adminConfig: StateFlow<AdminConfigEntity?> = repository.adminConfig
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val allUsers: StateFlow<List<UserEntity>> = repository.allUsers
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    private val _currentUser = MutableStateFlow<UserEntity?>(null)
    val currentUser: StateFlow<UserEntity?> = _currentUser.asStateFlow()

    private val _currentLanguage = MutableStateFlow(Language.BN)
    val currentLanguage: StateFlow<Language> = _currentLanguage.asStateFlow()

    private val _activeProjectId = MutableStateFlow<Long?>(null)
    val activeProjectId: StateFlow<Long?> = _activeProjectId.asStateFlow()

    private val _activeProject = MutableStateFlow<ProjectEntity?>(null)
    val activeProject: StateFlow<ProjectEntity?> = _activeProject.asStateFlow()

    private val _activeProjectFiles = MutableStateFlow<List<ProjectFileEntity>>(emptyList())
    val activeProjectFiles: StateFlow<List<ProjectFileEntity>> = _activeProjectFiles.asStateFlow()

    private val _activeChatMessages = MutableStateFlow<List<ChatMessageEntity>>(emptyList())
    val activeChatMessages: StateFlow<List<ChatMessageEntity>> = _activeChatMessages.asStateFlow()

    private val _selectedFile = MutableStateFlow<ProjectFileEntity?>(null)
    val selectedFile: StateFlow<ProjectFileEntity?> = _selectedFile.asStateFlow()

    private val _selectedPreviewPage = MutableStateFlow("index.html")
    val selectedPreviewPage: StateFlow<String> = _selectedPreviewPage.asStateFlow()

    private val _viewportMode = MutableStateFlow(ViewportMode.DESKTOP)
    val viewportMode: StateFlow<ViewportMode> = _viewportMode.asStateFlow()

    private val _isGenerating = MutableStateFlow(false)
    val isGenerating: StateFlow<Boolean> = _isGenerating.asStateFlow()

    private val _currentGenStep = MutableStateFlow<GenerationStep?>(null)
    val currentGenStep: StateFlow<GenerationStep?> = _currentGenStep.asStateFlow()

    private val _toastMessage = MutableStateFlow<String?>(null)
    val toastMessage: StateFlow<String?> = _toastMessage.asStateFlow()

    private val _userPlan = MutableStateFlow(SubscriptionPlan.FREE)
    val userPlan: StateFlow<SubscriptionPlan> = _userPlan.asStateFlow()

    init {
        viewModelScope.launch {
            repository.initDefaultsIfNeeded()
            // Auto login default admin / owner user
            val defaultUser = repository.getUserByEmail("mdsabberhosenjuwel@gmail.com")
                ?: repository.getUserByEmail("admin@webforge.ai")
            _currentUser.value = defaultUser
            defaultUser?.let {
                _userPlan.value = when (it.plan.uppercase()) {
                    "PRO" -> SubscriptionPlan.PRO
                    "BUSINESS" -> SubscriptionPlan.BUSINESS
                    else -> SubscriptionPlan.FREE
                }
            }
        }
    }

    fun setLanguage(lang: Language) {
        _currentLanguage.value = lang
    }

    fun selectProject(id: Long) {
        _activeProjectId.value = id
        viewModelScope.launch {
            repository.getProject(id).collect { proj ->
                _activeProject.value = proj
            }
        }
        viewModelScope.launch {
            repository.getProjectFiles(id).collect { files ->
                _activeProjectFiles.value = files
                if (_selectedFile.value == null || _selectedFile.value?.projectId != id) {
                    _selectedFile.value = files.firstOrNull { it.filePath == "index.html" } ?: files.firstOrNull()
                }
            }
        }
        viewModelScope.launch {
            repository.getChatMessages(id).collect { msgs ->
                _activeChatMessages.value = msgs
            }
        }
    }

    fun generateWebsite(
        prompt: String,
        name: String? = null,
        category: WebsiteType? = null,
        color: String? = null,
        onComplete: (Long) -> Unit
    ) {
        viewModelScope.launch {
            val user = _currentUser.value
            if (user != null) {
                val deductResult = repository.deductPointsForGeneration(user.id)
                if (deductResult.isFailure) {
                    val errMsg = deductResult.exceptionOrNull()?.message ?: "পয়েন্ট শেষ! প্রো সাবস্ক্রিপশন কিনুন।"
                    _toastMessage.value = errMsg
                    return@launch
                }
                // Refresh updated points
                val updatedUser = repository.getUserById(user.id)
                _currentUser.value = updatedUser
            }

            _isGenerating.value = true
            // Animate realistic 7-stage workflow
            val steps = GenerationStep.values()
            for (step in steps) {
                _currentGenStep.value = step
                delay(350)
            }

            val newId = repository.createProject(
                prompt = prompt,
                specifiedName = name,
                specifiedCategory = category,
                specifiedColor = color
            )
            _isGenerating.value = false
            _currentGenStep.value = null
            selectProject(newId)
            _toastMessage.value = if (_currentLanguage.value == Language.BN) "৫ পয়েন্ট খরচ করে ওয়েবসাইট তৈরি হয়েছে!" else "Website generated (-5 points)!"
            onComplete(newId)
        }
    }

    fun sendAiChat(message: String) {
        val projId = _activeProjectId.value ?: return
        viewModelScope.launch {
            _toastMessage.value = "AI is updating project..."
            repository.sendChatMessageAndModify(projId, message)
            _toastMessage.value = "Project updated!"
        }
    }

    fun runAiFix() {
        val projId = _activeProjectId.value ?: return
        viewModelScope.launch {
            _toastMessage.value = "AI scanning & auto-fixing code..."
            val report = repository.runAiAutoFix(projId)
            _toastMessage.value = "Auto-fix applied successfully!"
        }
    }

    fun selectFile(file: ProjectFileEntity) {
        _selectedFile.value = file
        if (file.fileType == "HTML") {
            _selectedPreviewPage.value = file.filePath
        }
    }

    fun saveFileContent(content: String) {
        val cur = _selectedFile.value ?: return
        viewModelScope.launch {
            val updated = cur.copy(content = content)
            repository.updateProjectFile(updated)
            _selectedFile.value = updated
            _toastMessage.value = "Saved '${cur.filePath}'"
        }
    }

    fun createNewFile(fileName: String, content: String) {
        val projId = _activeProjectId.value ?: return
        viewModelScope.launch {
            val newId = repository.addNewFile(projId, fileName, content)
            _toastMessage.value = "File '$fileName' created!"
        }
    }

    fun deleteFile(fileId: Long) {
        val projId = _activeProjectId.value ?: return
        viewModelScope.launch {
            repository.deleteFile(fileId, projId)
            _toastMessage.value = "File deleted"
        }
    }

    fun deployActiveProject(customDomain: String?, onComplete: (String) -> Unit) {
        val projId = _activeProjectId.value ?: return
        viewModelScope.launch {
            _toastMessage.value = "Building and deploying to edge CDN..."
            delay(1200)
            val url = repository.deployProject(projId, customDomain)
            _toastMessage.value = "Deployed! $url"
            onComplete(url)
        }
    }

    fun duplicateProject(id: Long) {
        viewModelScope.launch {
            val newId = repository.duplicateProject(id)
            _toastMessage.value = "Project duplicated!"
        }
    }

    fun deleteProject(id: Long) {
        viewModelScope.launch {
            repository.deleteProject(id)
            if (_activeProjectId.value == id) {
                _activeProjectId.value = null
                _activeProject.value = null
            }
            _toastMessage.value = "Project deleted"
        }
    }

    fun setViewportMode(mode: ViewportMode) {
        _viewportMode.value = mode
    }

    fun setPreviewPage(fileName: String) {
        _selectedPreviewPage.value = fileName
    }

    fun upgradePlan(plan: SubscriptionPlan) {
        _userPlan.value = plan
        _toastMessage.value = "Plan upgraded to ${plan.title}!"
    }

    fun updateAdminConfig(config: AdminConfigEntity) {
        viewModelScope.launch {
            repository.updateAdminConfig(config)
            _toastMessage.value = "Admin settings saved!"
        }
    }

    fun clearToast() {
        _toastMessage.value = null
    }

    /**
     * Combines all files (HTML, CSS, JS) into a unified renderable HTML payload for WebView preview
     */
    fun getUnifiedHtmlForPreview(): String {
        val files = _activeProjectFiles.value
        val targetPageName = _selectedPreviewPage.value
        val htmlFile = files.firstOrNull { it.filePath == targetPageName }
            ?: files.firstOrNull { it.filePath == "index.html" }
            ?: return "<html><body><h3>Generating website files...</h3></body></html>"

        val cssContent = files.firstOrNull { it.filePath == "styles.css" }?.content ?: ""
        val jsContent = files.firstOrNull { it.filePath == "app.js" }?.content ?: ""

        var renderedHtml = htmlFile.content

        // Inject inline CSS if styles.css reference is found or before </head>
        val inlineStyle = "<style>\n$cssContent\n</style>"
        renderedHtml = if (renderedHtml.contains("<link rel=\"stylesheet\" href=\"styles.css\" />")) {
            renderedHtml.replace("<link rel=\"stylesheet\" href=\"styles.css\" />", inlineStyle)
        } else if (renderedHtml.contains("<link rel=\"stylesheet\" href=\"styles.css\">")) {
            renderedHtml.replace("<link rel=\"stylesheet\" href=\"styles.css\">", inlineStyle)
        } else if (renderedHtml.contains("</head>")) {
            renderedHtml.replace("</head>", "$inlineStyle\n</head>")
        } else {
            inlineStyle + renderedHtml
        }

        // Intercept <a> links so page switching within preview stays in our sandbox
        val linkInterceptorScript = """
            <script>
            document.addEventListener('click', function(e) {
                const target = e.target.closest('a');
                if (target && target.getAttribute('href')) {
                    const href = target.getAttribute('href');
                    if (href.endsWith('.html')) {
                        e.preventDefault();
                        if (window.AndroidBridge) {
                            window.AndroidBridge.navigateToPage(href);
                        } else {
                            window.location.hash = href;
                        }
                    }
                }
            });
            </script>
        """.trimIndent()

        // Inject inline JS
        val inlineJs = "<script>\n$jsContent\n</script>\n$linkInterceptorScript"
        renderedHtml = if (renderedHtml.contains("<script src=\"app.js\"></script>")) {
            renderedHtml.replace("<script src=\"app.js\"></script>", inlineJs)
        } else if (renderedHtml.contains("</body>")) {
            renderedHtml.replace("</body>", "$inlineJs\n</body>")
        } else {
            renderedHtml + inlineJs
        }

        return renderedHtml
    }

    fun login(email: String, password: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val user = repository.login(email, password)
            if (user != null) {
                if (!user.isActive) {
                    val errMsg = if (_currentLanguage.value == Language.BN) "এই অ্যাকাউন্টটি নিষ্ক্রিয় করা হয়েছে!" else "This account is disabled!"
                    _toastMessage.value = errMsg
                    onResult(false, errMsg)
                    return@launch
                }
                _currentUser.value = user
                _userPlan.value = when (user.plan.uppercase()) {
                    "PRO" -> SubscriptionPlan.PRO
                    "BUSINESS" -> SubscriptionPlan.BUSINESS
                    else -> SubscriptionPlan.FREE
                }
                val successMsg = if (_currentLanguage.value == Language.BN) "স্বাগতম, ${user.name}!" else "Welcome back, ${user.name}!"
                _toastMessage.value = successMsg
                onResult(true, successMsg)
            } else {
                val errMsg = if (_currentLanguage.value == Language.BN) "ভুল ইমেইল বা পাসওয়ার্ড!" else "Invalid email or password!"
                _toastMessage.value = errMsg
                onResult(false, errMsg)
            }
        }
    }

    fun register(
        name: String,
        email: String,
        password: String,
        role: String = "USER",
        plan: String = "FREE",
        onResult: (Boolean, String) -> Unit = { _, _ -> }
    ) {
        viewModelScope.launch {
            val result = repository.registerUser(name, email, password, role, plan)
            result.onSuccess { user ->
                _currentUser.value = user
                _userPlan.value = when (user.plan.uppercase()) {
                    "PRO" -> SubscriptionPlan.PRO
                    "BUSINESS" -> SubscriptionPlan.BUSINESS
                    else -> SubscriptionPlan.FREE
                }
                val successMsg = if (_currentLanguage.value == Language.BN) "রেজিস্ট্রেশন সফল হয়েছে!" else "Registration successful!"
                _toastMessage.value = successMsg
                onResult(true, successMsg)
            }.onFailure { err ->
                val errMsg = err.message ?: "Registration failed"
                _toastMessage.value = errMsg
                onResult(false, errMsg)
            }
        }
    }

    fun logout() {
        _currentUser.value = null
        _userPlan.value = SubscriptionPlan.FREE
        _toastMessage.value = if (_currentLanguage.value == Language.BN) "সফলভাবে লগআউট হয়েছেন।" else "Logged out successfully."
    }

    fun updateUser(user: UserEntity) {
        viewModelScope.launch {
            repository.updateUser(user)
            if (_currentUser.value?.id == user.id) {
                _currentUser.value = user
                _userPlan.value = when (user.plan.uppercase()) {
                    "PRO" -> SubscriptionPlan.PRO
                    "BUSINESS" -> SubscriptionPlan.BUSINESS
                    else -> SubscriptionPlan.FREE
                }
            }
            _toastMessage.value = if (_currentLanguage.value == Language.BN) "ইউজার আপডেট সম্পন্ন হয়েছে।" else "User updated."
        }
    }

    fun toggleUserStatus(user: UserEntity) {
        updateUser(user.copy(isActive = !user.isActive))
    }

    fun toggleUserRole(user: UserEntity) {
        val newRole = if (user.role.uppercase() == "ADMIN") "USER" else "ADMIN"
        updateUser(user.copy(role = newRole))
    }

    fun changeUserPlan(user: UserEntity, newPlan: String) {
        updateUser(user.copy(plan = newPlan))
    }

    fun deleteUser(userId: Long) {
        viewModelScope.launch {
            if (_currentUser.value?.id == userId) {
                logout()
            }
            repository.deleteUser(userId)
            _toastMessage.value = if (_currentLanguage.value == Language.BN) "ইউজার মুছে ফেলা হয়েছে।" else "User deleted."
        }
    }

    fun loginWithGoogle(name: String, email: String, onResult: (Boolean, String) -> Unit = { _, _ -> }) {
        viewModelScope.launch {
            val user = repository.loginOrRegisterGoogle(name, email)
            _currentUser.value = user
            _userPlan.value = when (user.plan.uppercase()) {
                "PRO" -> SubscriptionPlan.PRO
                "BUSINESS" -> SubscriptionPlan.BUSINESS
                else -> SubscriptionPlan.FREE
            }
            val msg = if (_currentLanguage.value == Language.BN) "গুগল অ্যাকাউন্ট দিয়ে স্বাগতম, ${user.name}!" else "Welcome via Google, ${user.name}!"
            _toastMessage.value = msg
            onResult(true, msg)
        }
    }

    fun claimDailyPoints() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.claimDailyReward(user.id)
            result.onSuccess { added ->
                val updated = repository.getUserById(user.id)
                _currentUser.value = updated
                _toastMessage.value = if (_currentLanguage.value == Language.BN) "🎉 অভিনন্দন! দৈনিক $added ফ্রি পয়েন্ট সংগ্রহ সম্পন্ন হয়েছে।" else "🎉 Success! Claimed $added daily free points."
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "Failed"
            }
        }
    }

    fun claimMonthlyBonus() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val result = repository.claimMonthlyReward(user.id)
            result.onSuccess { added ->
                val updated = repository.getUserById(user.id)
                _currentUser.value = updated
                _toastMessage.value = if (_currentLanguage.value == Language.BN) "🌟 অভিনন্দন! মাসিক $added বোনাস পয়েন্ট সংগ্রহ সম্পন্ন হয়েছে।" else "🌟 Success! Claimed $added monthly bonus points."
            }.onFailure { err ->
                _toastMessage.value = err.message ?: "Failed"
            }
        }
    }

    fun purchasePointsPack(pointsCount: Int, packTitle: String) {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            repository.addPointsToUser(user.id, pointsCount)
            val updated = repository.getUserById(user.id)
            _currentUser.value = updated
            _toastMessage.value = if (_currentLanguage.value == Language.BN) "✅ সফলভাবে $packTitle ($pointsCount পয়েন্ট) কেনা সম্পন্ন হয়েছে!" else "✅ Successfully purchased $packTitle ($pointsCount points)!"
        }
    }

    fun upgradeToProPlan() {
        val user = _currentUser.value ?: return
        viewModelScope.launch {
            val updated = user.copy(plan = "PRO", points = user.points + 200)
            repository.updateUser(updated)
            _currentUser.value = updated
            _userPlan.value = SubscriptionPlan.PRO
            _toastMessage.value = if (_currentLanguage.value == Language.BN) "🚀 অভিনন্দন! আপনার প্রো সাবস্ক্রিপশন চালু হয়েছে (+২০০ বোনাস পয়েন্ট)।" else "🚀 Congratulations! Pro Subscription activated with +200 bonus points."
        }
    }

    fun adminAddPoints(userId: Long, amount: Int) {
        viewModelScope.launch {
            repository.addPointsToUser(userId, amount)
            if (_currentUser.value?.id == userId) {
                val updated = repository.getUserById(userId)
                _currentUser.value = updated
            }
            _toastMessage.value = if (_currentLanguage.value == Language.BN) "ইউজারের অ্যাকাউন্টে $amount পয়েন্ট যোগ করা হয়েছে।" else "Added $amount points to user."
        }
    }

    fun updateAdminPointPricing(
        pointsPricePer100: Double,
        proPlanPrice: Double,
        dailyReward: Int,
        monthlyBonus: Int,
        pointsPerGen: Int,
        currency: String
    ) {
        val current = adminConfig.value ?: AdminConfigEntity()
        updateAdminConfig(
            current.copy(
                pointsPricePer100 = pointsPricePer100,
                proPlanPrice = proPlanPrice,
                dailyFreePoints = dailyReward,
                monthlyBonusPoints = monthlyBonus,
                pointsPerGeneration = pointsPerGen,
                currency = currency
            )
        )
        _toastMessage.value = if (_currentLanguage.value == Language.BN) "পয়েন্টের দাম ও সিস্টেম সেটিংস আপডেট হয়েছে!" else "Point pricing and rules updated!"
    }
}
