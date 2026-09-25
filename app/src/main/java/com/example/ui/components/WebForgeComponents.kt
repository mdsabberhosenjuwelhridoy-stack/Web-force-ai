package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.Dashboard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Layers
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.example.data.local.entity.ProjectFileEntity
import com.example.data.model.AppStrings
import com.example.data.model.GenerationStep
import com.example.data.model.Language
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.SubscriptionPlan

enum class AppDestination(val route: String) {
    DASHBOARD("dashboard"),
    WORKSPACE("workspace"),
    PROJECTS("projects"),
    TEMPLATES("templates"),
    DEPLOYMENTS("deployments"),
    ADMIN("admin"),
    BILLING("billing")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebForgeTopBar(
    currentLanguage: Language,
    userPlan: SubscriptionPlan,
    onLanguageChange: (Language) -> Unit,
    onNavigateDestination: (AppDestination) -> Unit
) {
    var langMenuExpanded by remember { mutableStateOf(false) }

    TopAppBar(
        title = {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp),
                modifier = Modifier
                    .clickable { onNavigateDestination(AppDestination.DASHBOARD) }
                    .testTag("topbar_brand_logo")
            ) {
                Box(
                    modifier = Modifier
                        .size(34.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .background(IndigoPrimary),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AutoAwesome,
                        contentDescription = "Logo",
                        tint = Color.White,
                        modifier = Modifier.size(20.dp)
                    )
                }
                Column {
                    Text(
                        text = AppStrings.get("app_title", currentLanguage),
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            letterSpacing = (-0.5).sp
                        )
                    )
                    Text(
                        text = "AI Website Builder Platform",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f),
                            fontSize = 9.sp
                        )
                    )
                }
            }
        },
        actions = {
            // Plan Badge
            Surface(
                color = when (userPlan) {
                    SubscriptionPlan.FREE -> MaterialTheme.colorScheme.surfaceVariant
                    SubscriptionPlan.PRO -> IndigoPrimary.copy(alpha = 0.2f)
                    SubscriptionPlan.BUSINESS -> CyanAccent.copy(alpha = 0.2f)
                },
                shape = RoundedCornerShape(12.dp),
                border = androidx.compose.foundation.BorderStroke(
                    1.dp,
                    when (userPlan) {
                        SubscriptionPlan.FREE -> MaterialTheme.colorScheme.outline
                        SubscriptionPlan.PRO -> IndigoPrimary
                        SubscriptionPlan.BUSINESS -> CyanAccent
                    }
                ),
                modifier = Modifier
                    .clickable { onNavigateDestination(AppDestination.BILLING) }
                    .testTag("plan_badge")
            ) {
                Text(
                    text = userPlan.title,
                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                    style = MaterialTheme.typography.labelSmall.copy(
                        fontWeight = FontWeight.Bold,
                        color = when (userPlan) {
                            SubscriptionPlan.FREE -> MaterialTheme.colorScheme.onSurface
                            SubscriptionPlan.PRO -> IndigoPrimary
                            SubscriptionPlan.BUSINESS -> CyanAccent
                        }
                    )
                )
            }

            Spacer(modifier = Modifier.width(6.dp))

            // Language Selector Button
            Box {
                IconButton(
                    onClick = { langMenuExpanded = true },
                    modifier = Modifier.testTag("language_toggle_btn")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(2.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Language,
                            contentDescription = "Language",
                            tint = MaterialTheme.colorScheme.primary
                        )
                    }
                }
                DropdownMenu(
                    expanded = langMenuExpanded,
                    onDismissRequest = { langMenuExpanded = false }
                ) {
                    Language.values().forEach { lang ->
                        DropdownMenuItem(
                            text = {
                                Text(
                                    text = "${lang.nativeName} (${lang.displayName})",
                                    fontWeight = if (lang == currentLanguage) FontWeight.Bold else FontWeight.Normal,
                                    color = if (lang == currentLanguage) MaterialTheme.colorScheme.primary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onLanguageChange(lang)
                                langMenuExpanded = false
                            },
                            modifier = Modifier.testTag("lang_option_${lang.code}")
                        )
                    }
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = MaterialTheme.colorScheme.surface,
            titleContentColor = MaterialTheme.colorScheme.onSurface
        ),
        modifier = Modifier.testTag("webforge_top_app_bar")
    )
}

@Composable
fun WebForgeBottomBar(
    currentDestination: AppDestination,
    currentLanguage: Language,
    onNavigate: (AppDestination) -> Unit
) {
    NavigationBar(
        containerColor = MaterialTheme.colorScheme.surface,
        contentColor = MaterialTheme.colorScheme.onSurface,
        modifier = Modifier.testTag("webforge_bottom_nav_bar")
    ) {
        NavigationBarItem(
            selected = currentDestination == AppDestination.DASHBOARD,
            onClick = { onNavigate(AppDestination.DASHBOARD) },
            icon = { Icon(Icons.Default.Dashboard, contentDescription = "Dashboard") },
            label = { Text(AppStrings.get("nav_dashboard", currentLanguage), fontSize = 10.sp) },
            modifier = Modifier.testTag("nav_btn_dashboard")
        )
        NavigationBarItem(
            selected = currentDestination == AppDestination.PROJECTS,
            onClick = { onNavigate(AppDestination.PROJECTS) },
            icon = { Icon(Icons.Default.Folder, contentDescription = "Projects") },
            label = { Text(AppStrings.get("nav_projects", currentLanguage), fontSize = 10.sp) },
            modifier = Modifier.testTag("nav_btn_projects")
        )
        NavigationBarItem(
            selected = currentDestination == AppDestination.TEMPLATES,
            onClick = { onNavigate(AppDestination.TEMPLATES) },
            icon = { Icon(Icons.Default.Layers, contentDescription = "Templates") },
            label = { Text(AppStrings.get("nav_templates", currentLanguage), fontSize = 10.sp) },
            modifier = Modifier.testTag("nav_btn_templates")
        )
        NavigationBarItem(
            selected = currentDestination == AppDestination.DEPLOYMENTS,
            onClick = { onNavigate(AppDestination.DEPLOYMENTS) },
            icon = { Icon(Icons.Default.RocketLaunch, contentDescription = "Deploy") },
            label = { Text(AppStrings.get("nav_deployments", currentLanguage), fontSize = 10.sp) },
            modifier = Modifier.testTag("nav_btn_deployments")
        )
        NavigationBarItem(
            selected = currentDestination == AppDestination.ADMIN,
            onClick = { onNavigate(AppDestination.ADMIN) },
            icon = { Icon(Icons.Default.AdminPanelSettings, contentDescription = "Admin") },
            label = { Text(AppStrings.get("nav_admin", currentLanguage), fontSize = 10.sp) },
            modifier = Modifier.testTag("nav_btn_admin")
        )
        NavigationBarItem(
            selected = currentDestination == AppDestination.BILLING,
            onClick = { onNavigate(AppDestination.BILLING) },
            icon = { Icon(Icons.Default.Paid, contentDescription = "Billing") },
            label = { Text(AppStrings.get("nav_billing", currentLanguage), fontSize = 10.sp) },
            modifier = Modifier.testTag("nav_btn_billing")
        )
    }
}

@Composable
fun GenerationProgressDialog(
    currentStep: GenerationStep?,
    lang: Language
) {
    if (currentStep == null) return

    Dialog(onDismissRequest = { /* Modal during active generation */ }) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("generation_progress_dialog")
        ) {
            Column(
                modifier = Modifier.padding(24.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Box(
                    modifier = Modifier
                        .size(56.dp)
                        .clip(CircleShape)
                        .background(IndigoPrimary.copy(alpha = 0.15f)),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(36.dp),
                        color = IndigoPrimary,
                        strokeWidth = 3.dp
                    )
                }

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = AppStrings.get("generating", lang),
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )

                Spacer(modifier = Modifier.height(8.dp))

                LinearProgressIndicator(
                    progress = { currentStep.stepNumber / 7f },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(6.dp)
                        .clip(RoundedCornerShape(3.dp)),
                    color = IndigoPrimary,
                    trackColor = MaterialTheme.colorScheme.surfaceVariant
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Steps list
                Column(
                    verticalArrangement = Arrangement.spacedBy(8.dp),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    GenerationStep.values().forEach { step ->
                        val isDone = step.stepNumber < currentStep.stepNumber
                        val isCurrent = step.stepNumber == currentStep.stepNumber

                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            if (isDone) {
                                Icon(
                                    imageVector = Icons.Default.CheckCircle,
                                    contentDescription = "Done",
                                    tint = EmeraldSuccess,
                                    modifier = Modifier.size(18.dp)
                                )
                            } else if (isCurrent) {
                                CircularProgressIndicator(
                                    modifier = Modifier.size(16.dp),
                                    strokeWidth = 2.dp,
                                    color = IndigoPrimary
                                )
                            } else {
                                Box(
                                    modifier = Modifier
                                        .size(16.dp)
                                        .clip(CircleShape)
                                        .background(MaterialTheme.colorScheme.surfaceVariant)
                                )
                            }

                            Text(
                                text = "${step.stepNumber}. ${step.getTitle(lang)}",
                                style = MaterialTheme.typography.bodySmall.copy(
                                    fontWeight = if (isCurrent) FontWeight.Bold else FontWeight.Normal,
                                    color = if (isCurrent) IndigoPrimary else if (isDone) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun ExportZipDialog(
    projectName: String,
    files: List<ProjectFileEntity>,
    onDismiss: () -> Unit
) {
    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 6.dp,
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp)
                .testTag("export_zip_dialog")
        ) {
            Column(modifier = Modifier.padding(20.dp)) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.CloudDone,
                        contentDescription = "Export",
                        tint = EmeraldSuccess
                    )
                    Text(
                        text = "Export Project ZIP: $projectName",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                }

                Spacer(modifier = Modifier.height(12.dp))

                Text(
                    text = "Total files bundled: ${files.size} files (HTML, CSS, JS, PostgreSQL schema, REST API, README)",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(12.dp))

                LazyColumn(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .background(
                            MaterialTheme.colorScheme.surfaceVariant,
                            RoundedCornerShape(8.dp)
                        )
                        .padding(8.dp)
                ) {
                    items(files) { f ->
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 4.dp)
                        ) {
                            Text(
                                text = "📄 ${f.filePath}",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.SemiBold)
                            )
                            Text(
                                text = "${f.content.length} bytes",
                                style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                            )
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                Row(
                    horizontalArrangement = Arrangement.End,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    androidx.compose.material3.Button(
                        onClick = onDismiss,
                        modifier = Modifier.testTag("close_export_btn")
                    ) {
                        Text("Download ZIP Archive")
                    }
                }
            }
        }
    }
}
