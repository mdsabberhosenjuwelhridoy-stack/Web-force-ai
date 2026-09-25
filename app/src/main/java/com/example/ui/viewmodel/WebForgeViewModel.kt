package com.example.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.data.local.entity.AdminConfigEntity
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
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
            _toastMessage.value = "Website generated successfully!"
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
}
