package com.example

import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.BackHandler
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.components.AppDestination
import com.example.ui.components.GenerationProgressDialog
import com.example.ui.components.WebForgeBottomBar
import com.example.ui.components.WebForgeTopBar
import com.example.ui.screens.AdminPanelScreen
import com.example.ui.screens.BillingScreen
import com.example.ui.screens.DashboardScreen
import com.example.ui.screens.DeploymentsScreen
import com.example.ui.screens.ProjectsScreen
import com.example.ui.screens.TemplatesScreen
import com.example.ui.screens.WorkspaceScreen
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.theme.MyApplicationTheme
import com.example.ui.viewmodel.WebForgeViewModel

class MainActivity : ComponentActivity() {
    private val viewModel: WebForgeViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        setContent {
            MyApplicationTheme(darkTheme = true) {
                MainAppContent(viewModel = viewModel)
            }
        }
    }
}

@Composable
fun MainAppContent(viewModel: WebForgeViewModel) {
    val context = LocalContext.current
    var currentDestination by remember { mutableStateOf(AppDestination.DASHBOARD) }

    val currentLanguage by viewModel.currentLanguage.collectAsState()
    val userPlan by viewModel.userPlan.collectAsState()
    val allProjects by viewModel.allProjects.collectAsState()
    val activeProject by viewModel.activeProject.collectAsState()
    val activeFiles by viewModel.activeProjectFiles.collectAsState()
    val activeChatMessages by viewModel.activeChatMessages.collectAsState()
    val adminConfig by viewModel.adminConfig.collectAsState()
    val currentGenStep by viewModel.currentGenStep.collectAsState()
    val toastMessage by viewModel.toastMessage.collectAsState()

    var showBroadcastBanner by remember { mutableStateOf(true) }

    // Toast message handling
    LaunchedEffect(toastMessage) {
        toastMessage?.let { msg ->
            Toast.makeText(context, msg, Toast.LENGTH_SHORT).show()
            viewModel.clearToast()
        }
    }

    // Handle Android system back press
    BackHandler(enabled = currentDestination != AppDestination.DASHBOARD) {
        currentDestination = AppDestination.DASHBOARD
    }

    Scaffold(
        topBar = {
            WebForgeTopBar(
                currentLanguage = currentLanguage,
                userPlan = userPlan,
                onLanguageChange = { viewModel.setLanguage(it) },
                onNavigateDestination = { currentDestination = it }
            )
        },
        bottomBar = {
            // Keep bottom navigation visible on main destinations
            if (currentDestination != AppDestination.WORKSPACE) {
                WebForgeBottomBar(
                    currentDestination = currentDestination,
                    currentLanguage = currentLanguage,
                    onNavigate = { currentDestination = it }
                )
            }
        },
        modifier = Modifier
            .fillMaxSize()
            .testTag("main_app_scaffold")
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(innerPadding)
        ) {
            // Admin Broadcast Announcement Banner
            val broadcastNotice = adminConfig?.broadcastNotice
            if (showBroadcastBanner && !broadcastNotice.isNullOrBlank() && currentDestination == AppDestination.DASHBOARD) {
                Surface(
                    color = IndigoPrimary.copy(alpha = 0.15f),
                    border = androidx.compose.foundation.BorderStroke(1.dp, IndigoPrimary.copy(alpha = 0.4f)),
                    shape = RoundedCornerShape(10.dp),
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 6.dp)
                        .testTag("broadcast_banner")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 8.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.Campaign,
                            contentDescription = null,
                            tint = CyanAccent,
                            modifier = Modifier.size(20.dp)
                        )
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = broadcastNotice,
                            style = MaterialTheme.typography.bodySmall.copy(
                                fontWeight = FontWeight.SemiBold,
                                color = MaterialTheme.colorScheme.onSurface
                            ),
                            modifier = Modifier.weight(1f)
                        )
                        IconButton(
                            onClick = { showBroadcastBanner = false },
                            modifier = Modifier.size(24.dp)
                        ) {
                            Icon(Icons.Default.Close, contentDescription = "Close", modifier = Modifier.size(14.dp))
                        }
                    }
                }
            }

            // Main View Switcher
            Box(modifier = Modifier.fillMaxSize()) {
                when (currentDestination) {
                    AppDestination.DASHBOARD -> {
                        DashboardScreen(
                            viewModel = viewModel,
                            projects = allProjects,
                            language = currentLanguage,
                            onOpenWorkspace = { projId ->
                                viewModel.selectProject(projId)
                                currentDestination = AppDestination.WORKSPACE
                            },
                            onNavigate = { currentDestination = it }
                        )
                    }
                    AppDestination.WORKSPACE -> {
                        WorkspaceScreen(
                            viewModel = viewModel,
                            project = activeProject,
                            files = activeFiles,
                            chatMessages = activeChatMessages,
                            language = currentLanguage,
                            onBack = { currentDestination = AppDestination.DASHBOARD }
                        )
                    }
                    AppDestination.PROJECTS -> {
                        ProjectsScreen(
                            viewModel = viewModel,
                            projects = allProjects,
                            language = currentLanguage,
                            onOpenWorkspace = { projId ->
                                viewModel.selectProject(projId)
                                currentDestination = AppDestination.WORKSPACE
                            }
                        )
                    }
                    AppDestination.TEMPLATES -> {
                        TemplatesScreen(
                            viewModel = viewModel,
                            language = currentLanguage,
                            onOpenWorkspace = { projId ->
                                viewModel.selectProject(projId)
                                currentDestination = AppDestination.WORKSPACE
                            }
                        )
                    }
                    AppDestination.DEPLOYMENTS -> {
                        DeploymentsScreen(
                            viewModel = viewModel,
                            projects = allProjects,
                            language = currentLanguage,
                            onOpenWorkspace = { projId ->
                                viewModel.selectProject(projId)
                                currentDestination = AppDestination.WORKSPACE
                            }
                        )
                    }
                    AppDestination.ADMIN -> {
                        AdminPanelScreen(
                            viewModel = viewModel,
                            adminConfig = adminConfig,
                            language = currentLanguage
                        )
                    }
                    AppDestination.BILLING -> {
                        BillingScreen(
                            viewModel = viewModel,
                            userPlan = userPlan,
                            language = currentLanguage
                        )
                    }
                }
            }
        }
    }

    // Fullscreen 7-Step Generation Dialog
    GenerationProgressDialog(
        currentStep = currentGenStep,
        lang = currentLanguage
    )
}
