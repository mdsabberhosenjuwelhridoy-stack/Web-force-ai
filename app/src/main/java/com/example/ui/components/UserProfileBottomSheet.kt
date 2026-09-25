package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.ExitToApp
import androidx.compose.material.icons.automirrored.filled.HelpOutline
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Inbox
import androidx.compose.material.icons.filled.Link
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.rememberModalBottomSheetState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.local.entity.UserEntity
import com.example.data.model.Language
import com.example.ui.theme.IndigoPrimary

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun UserProfileBottomSheet(
    currentUser: UserEntity,
    language: Language,
    onDismiss: () -> Unit,
    onNavigate: (AppDestination) -> Unit,
    onClaimDaily: () -> Unit,
    onLogout: () -> Unit
) {
    val sheetState = rememberModalBottomSheetState(skipPartiallyExpanded = true)
    val isBn = language == Language.BN
    val firstName = currentUser.name.split(" ").firstOrNull() ?: currentUser.name

    ModalBottomSheet(
        onDismissRequest = onDismiss,
        sheetState = sheetState,
        containerColor = Color.White,
        shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
        modifier = Modifier.testTag("user_profile_bottom_sheet")
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 20.dp, vertical = 8.dp)
                .verticalScroll(rememberScrollState())
        ) {
            // User Header Row (Matches Screenshot 5)
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(14.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(46.dp)
                        .clip(CircleShape)
                        .background(Color(0xFF2563EB)),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = firstName.take(1).uppercase(),
                        color = Color.White,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                Column {
                    Text(
                        text = currentUser.name,
                        fontSize = 16.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Text(
                        text = currentUser.email,
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            // Inbox >
            DrawerNavRow(icon = Icons.Default.Inbox, label = if (isBn) "ইনবক্স" else "Inbox", hasArrow = true, onClick = {})
            // What's new >
            DrawerNavRow(icon = Icons.Default.Notifications, label = if (isBn) "নতুন ফিচারসমূহ" else "What's new", hasArrow = true, onClick = {})

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(8.dp))

            // Profile
            DrawerNavRow(icon = Icons.Default.Person, label = if (isBn) "প্রোফাইল" else "Profile", onClick = {})
            // Account settings
            DrawerNavRow(icon = Icons.Default.Settings, label = if (isBn) "অ্যাকাউন্ট সেটিংস" else "Account settings", onClick = {
                onDismiss()
                onNavigate(AppDestination.SETTINGS)
            })
            // Connectors
            DrawerNavRow(icon = Icons.Default.Link, label = if (isBn) "কানেক্টরস" else "Connectors", onClick = {})

            // Get free credits (Matches Screenshot 5!)
            DrawerNavRow(
                leadingEmoji = "🎁",
                label = if (isBn) "ফ্রি ক্রেডিট ক্লেইম করুন (দৈনিক ৫ ও মাসিক ৩০)" else "Get free credits",
                highlight = true,
                badge = "${currentUser.points} Pts",
                onClick = {
                    onDismiss()
                    onNavigate(AppDestination.BILLING)
                }
            )

            // Plans & credit usage (User Panel)
            DrawerNavRow(
                icon = Icons.Default.CreditCard,
                label = if (isBn) "ইউজার প্যানেল (প্ল্যান ও ক্রেডিট ব্যবহার)" else "Plans & credit usage",
                onClick = {
                    onDismiss()
                    onNavigate(AppDestination.BILLING)
                }
            )

            // Admin Panel (Admin Console)
            if (currentUser.role.uppercase() == "ADMIN") {
                DrawerNavRow(
                    icon = Icons.Default.AdminPanelSettings,
                    label = if (isBn) "🛡️ অ্যাডমিন প্যানেল (এডমিন কন্ট্রোল)" else "🛡️ Admin Panel (Control)",
                    highlight = true,
                    onClick = {
                        onDismiss()
                        onNavigate(AppDestination.ADMIN)
                    }
                )
            }

            Spacer(modifier = Modifier.height(8.dp))
            HorizontalDivider(color = Color(0xFFF3F4F6))
            Spacer(modifier = Modifier.height(8.dp))

            // Support >
            DrawerNavRow(icon = Icons.AutoMirrored.Filled.HelpOutline, label = if (isBn) "সহায়তা" else "Support", hasArrow = true, onClick = {})
            // Documentation >
            DrawerNavRow(icon = Icons.Default.Description, label = if (isBn) "ডকুমেন্টেশন" else "Documentation", hasArrow = true, onClick = {})
            // Appearance >
            DrawerNavRow(icon = Icons.Default.Palette, label = if (isBn) "থিম ও ডিসপ্লে" else "Appearance", hasArrow = true, onClick = {})

            // Log out
            DrawerNavRow(
                icon = Icons.AutoMirrored.Filled.ExitToApp,
                label = if (isBn) "লগ আউট" else "Log out",
                isDestructive = true,
                onClick = {
                    onDismiss()
                    onLogout()
                }
            )

            Spacer(modifier = Modifier.height(24.dp))
        }
    }
}

@Composable
fun DrawerNavRow(
    icon: ImageVector? = null,
    leadingEmoji: String? = null,
    label: String,
    hasArrow: Boolean = false,
    highlight: Boolean = false,
    isDestructive: Boolean = false,
    badge: String? = null,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 11.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(14.dp)
        ) {
            if (leadingEmoji != null) {
                Text(leadingEmoji, fontSize = 16.sp)
            } else if (icon != null) {
                Icon(
                    imageVector = icon,
                    contentDescription = null,
                    tint = if (isDestructive) Color(0xFFEF4444) else if (highlight) IndigoPrimary else Color(0xFF374151),
                    modifier = Modifier.size(19.dp)
                )
            }

            Text(
                text = label,
                fontSize = 14.sp,
                fontWeight = if (highlight) FontWeight.Bold else FontWeight.Medium,
                color = if (isDestructive) Color(0xFFEF4444) else if (highlight) IndigoPrimary else Color(0xFF1F2937)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(4.dp)) {
            if (badge != null) {
                Surface(
                    color = Color(0xFFFEF3C7),
                    shape = RoundedCornerShape(4.dp)
                ) {
                    Text(
                        text = badge,
                        fontSize = 10.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFFB45309),
                        modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                    )
                }
            }

            if (hasArrow) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                    contentDescription = null,
                    tint = Color(0xFF9CA3AF),
                    modifier = Modifier.size(18.dp)
                )
            }
        }
    }
}
