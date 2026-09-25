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
import androidx.compose.material.icons.filled.ExitToApp
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
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
import com.example.data.local.entity.UserEntity
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
    SETTINGS("settings"),
    ADMIN("admin"),
    BILLING("billing")
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun WebForgeTopBar(
    currentLanguage: Language,
    userPlan: SubscriptionPlan,
    currentUser: UserEntity? = null,
    onLanguageChange: (Language) -> Unit,
    onNavigateDestination: (AppDestination) -> Unit,
    onOpenAuth: () -> Unit = {},
    onLogout: () -> Unit = {},
    onOpenProfileDrawer: () -> Unit = {}
) {
    var langMenuExpanded by remember { mutableStateOf(false) }
    var workspaceMenuExpanded by remember { mutableStateOf(false) }

    val userFirstName = currentUser?.name?.split(" ")?.firstOrNull() ?: "Juwel"
    val workspaceLabel = "${userFirstName}'s Lovable"
    val isBn = currentLanguage == Language.BN

    TopAppBar(
        title = {
            // Workspace selector pill (Matches Screenshot 1 & 2)
            Box {
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color.White,
                    shadowElevation = 2.dp,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    modifier = Modifier
                        .clickable { workspaceMenuExpanded = true }
                        .testTag("topbar_workspace_pill")
                ) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 6.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(CircleShape)
                                .background(Color(0xFF9A3412)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = userFirstName.take(1).uppercase(),
                                color = Color.White,
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold
                            )
                        }
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(
                            text = workspaceLabel,
                            fontSize = 13.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = Color(0xFF111827)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = "↕",
                            fontSize = 12.sp,
                            color = Color(0xFF6B7280)
                        )
                    }
                }

                // Workspace Dropdown Menu (Matches Screenshot 2)
                DropdownMenu(
                    expanded = workspaceMenuExpanded,
                    onDismissRequest = { workspaceMenuExpanded = false },
                    modifier = Modifier.width(220.dp)
                ) {
                    // Top Credits item: "♡ Credits: 5 left"
                    DropdownMenuItem(
                        text = {
                            Row(
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(8.dp)
                            ) {
                                Text("♡", fontSize = 16.sp, color = Color(0xFF6B7280))
                                Text(
                                    text = if (isBn) "ক্রেডিট: ${currentUser?.points ?: 5} বাকি" else "Credits: ${currentUser?.points ?: 5} left",
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF111827)
                                )
                            }
                        },
                        onClick = {
                            workspaceMenuExpanded = false
                            onNavigateDestination(AppDestination.BILLING)
                        },
                        modifier = Modifier.testTag("workspace_menu_credits_item")
                    )

                    HorizontalDivider(color = Color(0xFFF3F4F6))

                    // All workspaces header
                    Text(
                        text = if (isBn) "সকল ওয়ার্কস্পেস" else "All workspaces",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280),
                        modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
                    )

                    // Active workspace
                    DropdownMenuItem(
                        text = {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Row(
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.spacedBy(8.dp)
                                ) {
                                    Box(
                                        modifier = Modifier
                                            .size(20.dp)
                                            .clip(RoundedCornerShape(4.dp))
                                            .background(Color(0xFF9A3412)),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(userFirstName.take(1).uppercase(), color = Color.White, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                    }
                                    Text(workspaceLabel, fontSize = 13.sp, fontWeight = FontWeight.SemiBold)
                                }
                                Text("✓", color = Color(0xFF374151), fontSize = 14.sp)
                            }
                        },
                        onClick = { workspaceMenuExpanded = false }
                    )

                    HorizontalDivider(color = Color(0xFFF3F4F6))

                    // Workspace settings
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("⚙", fontSize = 14.sp)
                                Text(if (isBn) "ওয়ার্কস্পেস সেটিংস" else "Workspace settings", fontSize = 13.sp)
                            }
                        },
                        onClick = {
                            workspaceMenuExpanded = false
                            onNavigateDestination(AppDestination.SETTINGS)
                        },
                        modifier = Modifier.testTag("workspace_menu_settings_item")
                    )

                    // New workspace
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("+", fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(if (isBn) "নতুন ওয়ার্কস্পেস" else "New workspace", fontSize = 13.sp)
                            }
                        },
                        onClick = { workspaceMenuExpanded = false }
                    )

                    // Find workspaces
                    DropdownMenuItem(
                        text = {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("🌐", fontSize = 14.sp)
                                Text(if (isBn) "ওয়ার্কস্পেস খুঁজুন" else "Find workspaces", fontSize = 13.sp)
                            }
                        },
                        onClick = { workspaceMenuExpanded = false }
                    )
                }
            }
        },
        actions = {
            // Language selector button
            Box {
                IconButton(
                    onClick = { langMenuExpanded = true },
                    modifier = Modifier.size(34.dp).testTag("topbar_lang_btn")
                ) {
                    Icon(
                        imageVector = Icons.Default.Language,
                        contentDescription = "Language",
                        tint = Color(0xFF4B5563),
                        modifier = Modifier.size(18.dp)
                    )
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
                                    color = if (lang == currentLanguage) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                                )
                            },
                            onClick = {
                                onLanguageChange(lang)
                                langMenuExpanded = false
                            }
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.width(6.dp))

            // User Profile Avatar Button (Matches Screenshot 1 & 2 blue circle "J")
            if (currentUser != null) {
                Surface(
                    shape = CircleShape,
                    color = Color(0xFF2563EB), // Vibrant Lovable blue
                    modifier = Modifier
                        .size(34.dp)
                        .clickable { onOpenProfileDrawer() }
                        .testTag("topbar_profile_avatar_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text(
                            text = userFirstName.take(1).uppercase(),
                            color = Color.White,
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            } else {
                Button(
                    onClick = onOpenAuth,
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2563EB)),
                    shape = RoundedCornerShape(16.dp),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 12.dp, vertical = 6.dp),
                    modifier = Modifier.height(34.dp).testTag("topbar_signin_btn")
                ) {
                    Text(if (isBn) "লগইন" else "Sign In", fontSize = 12.sp, fontWeight = FontWeight.Bold)
                }
            }
        },
        colors = TopAppBarDefaults.topAppBarColors(
            containerColor = Color.Transparent,
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
    // Floating Lovable Pill Bottom Bar (Matches Screenshot 1)
    Box(
        modifier = Modifier
            .fillMaxWidth()
            .padding(bottom = 16.dp),
        contentAlignment = Alignment.Center
    ) {
        Surface(
            shape = RoundedCornerShape(28.dp),
            color = Color(0xFFFCE7F3).copy(alpha = 0.85f), // Soft translucent Lovable pink pill
            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFFBCFE8)),
            shadowElevation = 8.dp,
            modifier = Modifier
                .height(54.dp)
                .testTag("floating_bottom_bar")
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(16.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Home (Selected pill button)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = if (currentDestination == AppDestination.DASHBOARD) Color(0xFF1E1B4B) else Color.Transparent,
                    modifier = Modifier
                        .clickable { onNavigate(AppDestination.DASHBOARD) }
                        .testTag("nav_btn_home")
                ) {
                    Box(
                        modifier = Modifier.padding(horizontal = 18.dp, vertical = 8.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Dashboard,
                            contentDescription = "Home",
                            tint = if (currentDestination == AppDestination.DASHBOARD) Color.White else Color(0xFF4B5563),
                            modifier = Modifier.size(20.dp)
                        )
                    }
                }

                // Grid / Projects icon
                IconButton(
                    onClick = { onNavigate(AppDestination.PROJECTS) },
                    modifier = Modifier.testTag("nav_btn_projects")
                ) {
                    Icon(
                        imageVector = Icons.Default.Layers,
                        contentDescription = "Projects",
                        tint = if (currentDestination == AppDestination.PROJECTS) Color(0xFF1E1B4B) else Color(0xFF4B5563),
                        modifier = Modifier.size(22.dp)
                    )
                }

                // Chat / Workspace icon
                IconButton(
                    onClick = { onNavigate(AppDestination.WORKSPACE) },
                    modifier = Modifier.testTag("nav_btn_chat")
                ) {
                    Icon(
                        imageVector = Icons.Default.Code,
                        contentDescription = "Workspace Chat",
                        tint = if (currentDestination == AppDestination.WORKSPACE) Color(0xFF1E1B4B) else Color(0xFF4B5563),
                        modifier = Modifier.size(22.dp)
                    )
                }
            }
        }
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
