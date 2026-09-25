package com.example.ui.screens

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
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Code
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Key
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Storage
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.model.Language
import com.example.ui.components.AppDestination
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.WebForgeViewModel

@Composable
fun WorkspaceSettingsScreen(
    viewModel: WebForgeViewModel,
    language: Language,
    onBack: () -> Unit,
    onNavigate: (AppDestination) -> Unit
) {
    val currentUser by viewModel.currentUser.collectAsState()
    val isBn = language == Language.BN
    var searchQuery by remember { mutableStateOf("") }

    val workspaceName = "${currentUser?.name?.split(" ")?.firstOrNull() ?: "Juwel"}'s Lovable"

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 16.dp)
            .testTag("workspace_settings_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Top Header
        item {
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    shape = CircleShape,
                    color = Color.White,
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                    modifier = Modifier
                        .size(36.dp)
                        .clickable { onBack() }
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.Default.Close,
                            contentDescription = "Close",
                            tint = Color(0xFF374151),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = if (isBn) "ওয়ার্কস্পেস সেটিংস" else "Workspace settings",
                    style = MaterialTheme.typography.titleMedium.copy(
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827),
                        fontSize = 17.sp
                    )
                )
            }
        }

        // Search Settings Input
        item {
            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                placeholder = {
                    Text(
                        if (isBn) "সেটিংস খুঁজুন..." else "Search settings",
                        color = Color(0xFF9CA3AF),
                        fontSize = 14.sp
                    )
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = Color(0xFF9CA3AF),
                        modifier = Modifier.size(18.dp)
                    )
                },
                shape = RoundedCornerShape(12.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = Color.White,
                    unfocusedContainerColor = Color.White,
                    focusedBorderColor = Color(0xFFD1D5DB),
                    unfocusedBorderColor = Color(0xFFE5E7EB)
                ),
                singleLine = true,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(48.dp)
                    .testTag("settings_search_input")
            )
        }

        // Section: Workspace
        item {
            SettingsCategoryHeader(if (isBn) "ওয়ার্কস্পেস" else "Workspace")
            SettingsCard {
                SettingsItemRow(
                    leadingWidget = {
                        Box(
                            modifier = Modifier
                                .size(24.dp)
                                .clip(RoundedCornerShape(6.dp))
                                .background(Color(0xFFB45309)),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = workspaceName.take(1).uppercase(),
                                color = Color.White,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    },
                    title = workspaceName,
                    onClick = {}
                )
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(
                    icon = Icons.Default.CreditCard,
                    title = if (isBn) "প্ল্যান ও ক্রেডিট ব্যবহার (পয়েন্ট প্যানেল)" else "Plans & credit usage",
                    onClick = { onNavigate(AppDestination.BILLING) },
                    highlight = true
                )
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(
                    icon = Icons.Default.Notifications,
                    title = if (isBn) "ব্যবহারের সীমা ও সতর্কতা" else "Usage limits & alerts",
                    onClick = {}
                )
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(
                    icon = Icons.Default.Folder,
                    title = "Slack",
                    onClick = {}
                )
            }
        }

        // Section: Access
        item {
            SettingsCategoryHeader(if (isBn) "অ্যাক্সেস ও সদস্য" else "Access")
            SettingsCard {
                SettingsItemRow(
                    icon = Icons.Default.Person,
                    title = if (isBn) "মানুষ (People)" else "People",
                    onClick = {}
                )
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(
                    icon = Icons.Default.Group,
                    title = if (isBn) "গ্রুপ (Groups)" else "Groups",
                    badge = "Business",
                    onClick = {}
                )
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(
                    icon = Icons.Default.Shield,
                    title = if (isBn) "আইডেন্টিটি (Identity)" else "Identity",
                    badge = "Business",
                    onClick = {}
                )
            }
        }

        // Section: Customization
        item {
            SettingsCategoryHeader(if (isBn) "কাস্টমাইজেশন" else "Customization")
            SettingsCard {
                SettingsItemRow(icon = Icons.Default.Settings, title = "Knowledge", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Code, title = "Skills", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Folder, title = "Templates", badge = "Business", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Palette, title = "Design systems", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Settings, title = "Connector settings", badge = "Business", onClick = {})
            }
        }

        // Section: Build & deploy
        item {
            SettingsCategoryHeader(if (isBn) "বিল্ড ও ডিপ্লয়" else "Build & deploy")
            SettingsCard {
                SettingsItemRow(icon = Icons.Default.Code, title = "Git", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Key, title = "Build secrets", badge = "Enterprise", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Storage, title = "Managed registry", badge = "Enterprise", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Settings, title = "MCP server", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Language, title = "Workspace domains", onClick = {})
            }
        }

        // Section: Security
        item {
            SettingsCategoryHeader(if (isBn) "নিরাপত্তা" else "Security")
            SettingsCard {
                SettingsItemRow(icon = Icons.Default.Security, title = "Privacy & security", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Shield, title = "Security center", badge = "Business", onClick = {})
                HorizontalDivider(color = Color(0xFFF3F4F6))
                SettingsItemRow(icon = Icons.Default.Folder, title = "Audit logs", badge = "Enterprise", onClick = {})
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }
}

@Composable
fun SettingsCategoryHeader(text: String) {
    Text(
        text = text,
        fontSize = 12.sp,
        fontWeight = FontWeight.SemiBold,
        color = Color(0xFF6B7280),
        modifier = Modifier.padding(start = 4.dp, bottom = 4.dp)
    )
}

@Composable
fun SettingsCard(content: @Composable () -> Unit) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
        modifier = Modifier.fillMaxWidth()
    ) {
        Column {
            content()
        }
    }
}

@Composable
fun SettingsItemRow(
    icon: ImageVector? = null,
    leadingWidget: (@Composable () -> Unit)? = null,
    title: String,
    badge: String? = null,
    highlight: Boolean = false,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(horizontal = 14.dp, vertical = 12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (leadingWidget != null) {
                leadingWidget()
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (highlight) IndigoPrimary else Color(0xFF4B5563),
                    modifier = Modifier.size(18.dp)
                )
            }
            Text(
                text = title,
                fontSize = 14.sp,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
                color = if (highlight) IndigoPrimary else Color(0xFF1F2937)
            )
        }

        if (badge != null) {
            Surface(
                color = Color(0xFFEDE9FE),
                shape = RoundedCornerShape(4.dp)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "♥ $badge",
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF7C3AED)
                    )
                }
            }
        }
    }
}
