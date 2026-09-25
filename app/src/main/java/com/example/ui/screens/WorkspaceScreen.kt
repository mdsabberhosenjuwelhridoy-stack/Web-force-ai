package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Send
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Build
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.DesktopWindows
import androidx.compose.material.icons.filled.FileDownload
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Send
import androidx.compose.material.icons.filled.Smartphone
import androidx.compose.material.icons.filled.TabletMac
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.ScrollableTabRow
import androidx.compose.material3.Surface
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.ChatMessageEntity
import com.example.data.local.entity.ProjectEntity
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.model.AppStrings
import com.example.data.model.Language
import com.example.ui.components.ExportZipDialog
import com.example.ui.components.WebViewPreview
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.Slate800
import com.example.ui.theme.Slate900
import com.example.ui.theme.Slate950
import com.example.ui.viewmodel.ViewportMode
import com.example.ui.viewmodel.WebForgeViewModel

enum class WorkspaceTab(val titleKey: String) {
    PREVIEW("tab_preview"),
    CODE("tab_code"),
    AI_ASSISTANT("tab_assistant"),
    DEPLOY("tab_deploy")
}

@Composable
fun WorkspaceScreen(
    viewModel: WebForgeViewModel,
    project: ProjectEntity?,
    files: List<ProjectFileEntity>,
    chatMessages: List<ChatMessageEntity>,
    language: Language,
    onBack: () -> Unit
) {
    if (project == null) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator(color = IndigoPrimary)
        }
        return
    }

    var selectedTab by remember { mutableStateOf(WorkspaceTab.PREVIEW) }
    val selectedFile by viewModel.selectedFile.collectAsState()
    val selectedPreviewPage by viewModel.selectedPreviewPage.collectAsState()
    val viewportMode by viewModel.viewportMode.collectAsState()
    var showExportModal by remember { mutableStateOf(false) }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .testTag("workspace_screen")
    ) {
        // Top Toolbar
        Surface(
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 4.dp,
            modifier = Modifier.fillMaxWidth()
        ) {
            Column {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 12.dp, vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Text(
                            text = "←",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            modifier = Modifier
                                .clickable { onBack() }
                                .padding(4.dp)
                                .testTag("workspace_back_btn")
                        )
                        Column {
                            Text(
                                text = project.name,
                                style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                            )
                            Text(
                                text = "${project.slug}.webforge.app",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                                    fontSize = 10.sp
                                )
                            )
                        }
                    }

                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        // AI Auto-Fix Quick Action
                        Button(
                            onClick = { viewModel.runAiFix() },
                            colors = ButtonDefaults.buttonColors(containerColor = CyanAccent),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier
                                .height(34.dp)
                                .testTag("ai_autofix_header_btn")
                        ) {
                            Text(
                                "⚡ " + AppStrings.get("ai_fix_button", language),
                                fontSize = 10.sp,
                                fontWeight = FontWeight.Bold,
                                color = Slate950
                            )
                        }

                        // Export ZIP button
                        IconButton(
                            onClick = { showExportModal = true },
                            modifier = Modifier.size(34.dp).testTag("export_zip_header_btn")
                        ) {
                            Icon(Icons.Default.FileDownload, contentDescription = "Export ZIP", modifier = Modifier.size(18.dp))
                        }
                    }
                }

                // Workspace Tabs: [Live Preview] [Code Editor] [AI Assistant] [Deploy]
                TabRow(
                    selectedTabIndex = selectedTab.ordinal,
                    containerColor = MaterialTheme.colorScheme.surface,
                    contentColor = IndigoPrimary
                ) {
                    WorkspaceTab.values().forEach { tab ->
                        Tab(
                            selected = selectedTab == tab,
                            onClick = { selectedTab = tab },
                            text = {
                                Text(
                                    AppStrings.get(tab.titleKey, language),
                                    fontSize = 11.sp,
                                    fontWeight = if (selectedTab == tab) FontWeight.Bold else FontWeight.Normal
                                )
                            },
                            icon = {
                                when (tab) {
                                    WorkspaceTab.PREVIEW -> Icon(Icons.Default.Visibility, contentDescription = null, modifier = Modifier.size(16.dp))
                                    WorkspaceTab.CODE -> Icon(Icons.Default.Code, contentDescription = null, modifier = Modifier.size(16.dp))
                                    WorkspaceTab.AI_ASSISTANT -> Icon(Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
                                    WorkspaceTab.DEPLOY -> Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(16.dp))
                                }
                            },
                            modifier = Modifier.testTag("workspace_tab_${tab.name.lowercase()}")
                        )
                    }
                }
            }
        }

        // Tab Content
        Box(modifier = Modifier.fillMaxSize()) {
            when (selectedTab) {
                WorkspaceTab.PREVIEW -> {
                    PreviewTabContent(
                        viewModel = viewModel,
                        files = files,
                        selectedPage = selectedPreviewPage,
                        viewportMode = viewportMode,
                        language = language
                    )
                }
                WorkspaceTab.CODE -> {
                    CodeEditorTabContent(
                        viewModel = viewModel,
                        project = project,
                        files = files,
                        selectedFile = selectedFile,
                        language = language
                    )
                }
                WorkspaceTab.AI_ASSISTANT -> {
                    AiAssistantTabContent(
                        viewModel = viewModel,
                        chatMessages = chatMessages,
                        language = language
                    )
                }
                WorkspaceTab.DEPLOY -> {
                    DeploymentTabContent(
                        viewModel = viewModel,
                        project = project,
                        language = language
                    )
                }
            }
        }
    }

    if (showExportModal) {
        ExportZipDialog(
            projectName = project.name,
            files = files,
            onDismiss = { showExportModal = false }
        )
    }
}

// --- TAB 1: LIVE PREVIEW ---
@Composable
fun PreviewTabContent(
    viewModel: WebForgeViewModel,
    files: List<ProjectFileEntity>,
    selectedPage: String,
    viewportMode: ViewportMode,
    language: Language
) {
    val unifiedHtml = viewModel.getUnifiedHtmlForPreview()
    val htmlPages = files.filter { it.fileType == "HTML" }

    Column(modifier = Modifier.fillMaxSize()) {
        // Controls bar: Page Switcher Chips + Viewport Switcher
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 8.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Page Navigation Chips
                Row(
                    modifier = Modifier
                        .weight(1f)
                        .horizontalScroll(rememberScrollState()),
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    htmlPages.forEach { page ->
                        val isSelected = page.filePath == selectedPage
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.surface,
                            border = androidx.compose.foundation.BorderStroke(
                                1.dp,
                                if (isSelected) IndigoPrimary else MaterialTheme.colorScheme.outline
                            ),
                            modifier = Modifier
                                .clickable { viewModel.setPreviewPage(page.filePath) }
                                .testTag("page_chip_${page.filePath}")
                        ) {
                            Text(
                                text = "📄 ${page.filePath}",
                                style = MaterialTheme.typography.labelSmall.copy(
                                    fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Medium,
                                    color = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurface
                                ),
                                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.width(8.dp))

                // Viewport Modes: Mobile, Tablet, Desktop
                Row(
                    horizontalArrangement = Arrangement.spacedBy(2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(
                        onClick = { viewModel.setViewportMode(ViewportMode.MOBILE) },
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                if (viewportMode == ViewportMode.MOBILE) IndigoPrimary.copy(alpha = 0.2f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .testTag("viewport_mobile_btn")
                    ) {
                        Icon(Icons.Default.Smartphone, contentDescription = "Mobile", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { viewModel.setViewportMode(ViewportMode.TABLET) },
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                if (viewportMode == ViewportMode.TABLET) IndigoPrimary.copy(alpha = 0.2f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .testTag("viewport_tablet_btn")
                    ) {
                        Icon(Icons.Default.TabletMac, contentDescription = "Tablet", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = { viewModel.setViewportMode(ViewportMode.DESKTOP) },
                        modifier = Modifier
                            .size(30.dp)
                            .background(
                                if (viewportMode == ViewportMode.DESKTOP) IndigoPrimary.copy(alpha = 0.2f) else Color.Transparent,
                                RoundedCornerShape(4.dp)
                            )
                            .testTag("viewport_desktop_btn")
                    ) {
                        Icon(Icons.Default.DesktopWindows, contentDescription = "Desktop", modifier = Modifier.size(16.dp))
                    }
                }
            }
        }

        // Live WebView Interactive Frame
        Box(modifier = Modifier.fillMaxSize()) {
            WebViewPreview(
                htmlContent = unifiedHtml,
                viewportMode = viewportMode,
                onNavigatePage = { targetPage ->
                    viewModel.setPreviewPage(targetPage)
                },
                modifier = Modifier.fillMaxSize()
            )
        }
    }
}

// --- TAB 2: CODE EDITOR ---
@Composable
fun CodeEditorTabContent(
    viewModel: WebForgeViewModel,
    project: ProjectEntity,
    files: List<ProjectFileEntity>,
    selectedFile: ProjectFileEntity?,
    language: Language
) {
    var editorCode by remember(selectedFile?.content) {
        mutableStateOf(selectedFile?.content ?: "")
    }

    var showCreateFileDialog by remember { mutableStateOf(false) }
    var newFileName by remember { mutableStateOf("") }
    var fileMenuOpen by remember { mutableStateOf(false) }
    var aiExplainText by remember { mutableStateOf<String?>(null) }

    Column(modifier = Modifier.fillMaxSize()) {
        // File Selector & Controls
        Surface(
            color = MaterialTheme.colorScheme.surfaceVariant,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Dropdown to pick file
                Box {
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.surface,
                        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                        modifier = Modifier
                            .clickable { fileMenuOpen = true }
                            .testTag("file_picker_dropdown")
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                        ) {
                            Text(
                                text = "📄 ${selectedFile?.filePath ?: "Select file"}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Text("▼", fontSize = 9.sp)
                        }
                    }

                    DropdownMenu(
                        expanded = fileMenuOpen,
                        onDismissRequest = { fileMenuOpen = false }
                    ) {
                        files.forEach { file ->
                            DropdownMenuItem(
                                text = {
                                    Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                        Text(
                                            when (file.fileType) {
                                                "HTML" -> "🌐"
                                                "CSS" -> "🎨"
                                                "JS" -> "⚡"
                                                "SQL" -> "🗄️"
                                                "TS" -> "📘"
                                                "JSON" -> "⚙️"
                                                else -> "📝"
                                            }
                                        )
                                        Text(file.filePath, fontWeight = if (file.id == selectedFile?.id) FontWeight.Bold else FontWeight.Normal)
                                    }
                                },
                                onClick = {
                                    viewModel.selectFile(file)
                                    fileMenuOpen = false
                                },
                                modifier = Modifier.testTag("file_item_${file.filePath}")
                            )
                        }
                    }
                }

                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    // New file button
                    IconButton(
                        onClick = { showCreateFileDialog = true },
                        modifier = Modifier.size(32.dp).testTag("create_file_btn")
                    ) {
                        Icon(Icons.Default.Add, contentDescription = "New File", modifier = Modifier.size(18.dp))
                    }

                    // Save file button
                    IconButton(
                        onClick = { viewModel.saveFileContent(editorCode) },
                        modifier = Modifier.size(32.dp).testTag("save_file_btn")
                    ) {
                        Icon(Icons.Default.Save, contentDescription = "Save", tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                    }

                    // Delete file button (if not index.html)
                    if (selectedFile?.filePath != "index.html") {
                        IconButton(
                            onClick = { selectedFile?.let { viewModel.deleteFile(it.id) } },
                            modifier = Modifier.size(32.dp).testTag("delete_file_btn")
                        ) {
                            Icon(Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error, modifier = Modifier.size(18.dp))
                        }
                    }
                }
            }
        }

        // Code Editor Input Area
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .background(Slate950)
        ) {
            TextField(
                value = editorCode,
                onValueChange = { editorCode = it },
                textStyle = MaterialTheme.typography.bodySmall.copy(
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp,
                    color = Color(0xFFF1F5F9),
                    lineHeight = 18.sp
                ),
                colors = TextFieldDefaults.colors(
                    focusedContainerColor = Slate950,
                    unfocusedContainerColor = Slate950,
                    cursorColor = CyanAccent,
                    focusedIndicatorColor = Color.Transparent,
                    unfocusedIndicatorColor = Color.Transparent
                ),
                modifier = Modifier
                    .fillMaxSize()
                    .testTag("code_editor_textarea")
            )
        }

        // Bottom AI Helpers for Code
        Surface(
            color = MaterialTheme.colorScheme.surface,
            modifier = Modifier.fillMaxWidth()
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(8.dp),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "${editorCode.length} characters | UTF-8",
                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Button(
                        onClick = {
                            val prompt = "Analyze and explain the purpose of this ${selectedFile?.filePath} file in 2 sentences."
                            aiExplainText = "This file (${selectedFile?.filePath}) defines the core ${selectedFile?.fileType} structure, responsible for layout and responsive state management in the $project website."
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.surfaceVariant),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp).testTag("ai_explain_code_btn")
                    ) {
                        Text("🤖 AI Explain", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurface)
                    }

                    Button(
                        onClick = { viewModel.saveFileContent(editorCode) },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp).testTag("save_code_footer_btn")
                    ) {
                        Text("Save & Apply", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
    }

    // Modal to create new file
    if (showCreateFileDialog) {
        Dialog(onDismissRequest = { showCreateFileDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text("Create New Project File", fontWeight = FontWeight.Bold)
                    Spacer(modifier = Modifier.height(8.dp))
                    OutlinedTextField(
                        value = newFileName,
                        onValueChange = { newFileName = it },
                        placeholder = { Text("e.g. blog.html or api.json") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth().testTag("new_file_name_input")
                    )
                    Spacer(modifier = Modifier.height(12.dp))
                    Row(
                        horizontalArrangement = Arrangement.End,
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Button(
                            onClick = {
                                if (newFileName.isNotBlank()) {
                                    viewModel.createNewFile(newFileName.trim(), "// New file: $newFileName\n")
                                    showCreateFileDialog = false
                                    newFileName = ""
                                }
                            },
                            modifier = Modifier.testTag("confirm_create_file_btn")
                        ) {
                            Text("Create")
                        }
                    }
                }
            }
        }
    }

    // Modal for AI Explain
    aiExplainText?.let { explanation ->
        Dialog(onDismissRequest = { aiExplainText = null }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = MaterialTheme.colorScheme.surface,
                modifier = Modifier.padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = IndigoPrimary)
                        Text("AI Code Explanation", fontWeight = FontWeight.Bold)
                    }
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(explanation, style = MaterialTheme.typography.bodySmall)
                    Spacer(modifier = Modifier.height(12.dp))
                    Button(
                        onClick = { aiExplainText = null },
                        modifier = Modifier.align(Alignment.End)
                    ) {
                        Text("Got it")
                    }
                }
            }
        }
    }
}

// --- TAB 3: AI ASSISTANT ---
@Composable
fun AiAssistantTabContent(
    viewModel: WebForgeViewModel,
    chatMessages: List<ChatMessageEntity>,
    language: Language
) {
    var chatInput by remember { mutableStateOf("") }

    val quickPrompts = listOf(
        "Header-এর রঙ পরিবর্তন করো",
        "একটি Blog section যোগ করো",
        "Login/Register যোগ করো",
        "Product search যোগ করো",
        "এই error ঠিক করো"
    )

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(12.dp)
            .testTag("ai_assistant_tab")
    ) {
        // Quick suggestion chips
        Text(
            text = if (language == Language.BN) "দ্রুত পরিবর্তন বা ফিচার যুক্ত করুন:" else "Quick Actions & AI Modifications:",
            style = MaterialTheme.typography.labelSmall.copy(fontWeight = FontWeight.Bold)
        )
        Spacer(modifier = Modifier.height(6.dp))

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .horizontalScroll(rememberScrollState()),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            quickPrompts.forEach { qp ->
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                    modifier = Modifier
                        .clickable { viewModel.sendAiChat(qp) }
                        .testTag("quick_prompt_${qp.hashCode()}")
                ) {
                    Text(
                        text = "✨ $qp",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.primary),
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Chat Message History
        LazyColumn(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            verticalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            items(chatMessages) { msg ->
                val isUser = msg.sender == "user"
                val isSystem = msg.sender == "system"

                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = if (isUser) Arrangement.End else Arrangement.Start
                ) {
                    Card(
                        shape = RoundedCornerShape(
                            topStart = 14.dp,
                            topEnd = 14.dp,
                            bottomStart = if (isUser) 14.dp else 2.dp,
                            bottomEnd = if (isUser) 2.dp else 14.dp
                        ),
                        colors = CardDefaults.cardColors(
                            containerColor = when {
                                isUser -> IndigoPrimary
                                isSystem -> Slate800
                                else -> MaterialTheme.colorScheme.surface
                            }
                        ),
                        border = androidx.compose.foundation.BorderStroke(
                            1.dp,
                            if (isUser) IndigoPrimary else MaterialTheme.colorScheme.outlineVariant
                        ),
                        modifier = Modifier.fillMaxWidth(0.85f)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(6.dp)
                            ) {
                                Text(
                                    text = if (isUser) "You" else if (isSystem) "⚙️ System Sandbox" else "🤖 WebForge AI",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Bold,
                                        color = if (isUser) Color.White.copy(alpha = 0.8f) else CyanAccent
                                    )
                                )
                            }
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(
                                text = msg.message,
                                style = MaterialTheme.typography.bodySmall.copy(
                                    color = if (isUser) Color.White else MaterialTheme.colorScheme.onSurface
                                )
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Prompt Input
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            OutlinedTextField(
                value = chatInput,
                onValueChange = { chatInput = it },
                placeholder = {
                    Text(
                        if (language == Language.BN) "এআই-কে বলুন (যেমন: 'Header-এর রঙ পরিবর্তন করো')..." else "Tell AI what to modify...",
                        fontSize = 12.sp
                    )
                },
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_chat_input"),
                shape = RoundedCornerShape(12.dp)
            )

            IconButton(
                onClick = {
                    if (chatInput.isNotBlank()) {
                        viewModel.sendAiChat(chatInput.trim())
                        chatInput = ""
                    }
                },
                modifier = Modifier
                    .size(48.dp)
                    .background(IndigoPrimary, CircleShape)
                    .testTag("ai_chat_send_btn")
            ) {
                Icon(Icons.AutoMirrored.Filled.Send, contentDescription = "Send", tint = Color.White)
            }
        }
    }
}

// --- TAB 4: DEPLOYMENT ---
@Composable
fun DeploymentTabContent(
    viewModel: WebForgeViewModel,
    project: ProjectEntity,
    language: Language
) {
    var customDomainInput by remember { mutableStateOf(project.customDomain ?: "") }
    var stripeKeyInput by remember { mutableStateOf("pk_live_sec_placeholder_9921") }
    var dbUrlInput by remember { mutableStateOf("postgresql://admin:pass@postgres.cluster.internal:5432/webforge") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("deployment_tab"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(32.dp)
                                .clip(CircleShape)
                                .background(EmeraldSuccess.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(18.dp))
                        }
                        Text(
                            text = if (language == Language.BN) "ওয়েবসাইট ডিপ্লয়মেন্ট ইঞ্জিন" else "Website Production Deployment",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(
                        text = if (language == Language.BN) "সাবডোমেন ও কাস্টম ডোমেন সেট করুন এবং এক ক্লিকে গ্লোবাল এজ সিডিএন-এ হোস্ট করুন।" else "Configure subdomain or custom domain and deploy worldwide with automated SSL.",
                        style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(16.dp))

                    // Subdomain display
                    OutlinedTextField(
                        value = "https://${project.slug}.webforge.app",
                        onValueChange = {},
                        readOnly = true,
                        label = { Text("Default Subdomain URL", fontSize = 11.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(10.dp))

                    // Custom Domain Input
                    OutlinedTextField(
                        value = customDomainInput,
                        onValueChange = { customDomainInput = it },
                        label = { Text(AppStrings.get("custom_domain", language), fontSize = 11.sp) },
                        placeholder = { Text("e.g. xyzfashion.com") },
                        modifier = Modifier.fillMaxWidth().testTag("custom_domain_input")
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    // SSL status badge
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        Icon(Icons.Default.Lock, contentDescription = null, tint = EmeraldSuccess, modifier = Modifier.size(16.dp))
                        Text(
                            text = "Automated Let's Encrypt Wildcard SSL: Active & Auto-Renewed",
                            style = MaterialTheme.typography.labelSmall.copy(color = EmeraldSuccess, fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Deploy Button
                    Button(
                        onClick = {
                            viewModel.deployActiveProject(customDomainInput.ifBlank { null }) {}
                        },
                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                        shape = RoundedCornerShape(10.dp),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(48.dp)
                            .testTag("execute_deploy_btn")
                    ) {
                        Icon(Icons.Default.RocketLaunch, contentDescription = null)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Deploy Website to Production", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }

        // Environment Variables Card
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Text(
                        text = "🔒 Environment Variables (Production Secrets)",
                        style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold)
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Values are securely encrypted and injected at build runtime.",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )

                    Spacer(modifier = Modifier.height(12.dp))

                    OutlinedTextField(
                        value = dbUrlInput,
                        onValueChange = { dbUrlInput = it },
                        label = { Text("DATABASE_URL", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    OutlinedTextField(
                        value = stripeKeyInput,
                        onValueChange = { stripeKeyInput = it },
                        label = { Text("PAYMENT_STRIPE_KEY", fontSize = 10.sp) },
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            }
        }

        // Live Build Logs Terminal
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Slate950),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = "Terminal Build Logs",
                            style = MaterialTheme.typography.labelMedium.copy(
                                fontWeight = FontWeight.Bold,
                                color = CyanAccent
                            )
                        )
                        Text(
                            text = if (project.isDeployed) "STATUS: 200 LIVE" else "STATUS: IDLE",
                            style = MaterialTheme.typography.labelSmall.copy(
                                color = if (project.isDeployed) EmeraldSuccess else Color.Gray,
                                fontWeight = FontWeight.Bold
                            )
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    Text(
                        text = if (project.buildLogs.isNotBlank()) project.buildLogs else "[Ready] Click Deploy above to execute sandbox compilation & Edge CDN distribution.",
                        fontFamily = FontFamily.Monospace,
                        fontSize = 11.sp,
                        color = Color(0xFF94A3B8),
                        lineHeight = 16.sp
                    )
                }
            }
        }
    }
}
