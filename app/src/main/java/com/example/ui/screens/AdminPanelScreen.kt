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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AdminPanelSettings
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Campaign
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Group
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Paid
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PriceCheck
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.Tune
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Tab
import androidx.compose.material3.TabRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
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
import com.example.data.local.entity.AdminConfigEntity
import com.example.data.local.entity.UserEntity
import com.example.data.model.Language
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.WebForgeViewModel

@Composable
fun AdminPanelScreen(
    viewModel: WebForgeViewModel,
    adminConfig: AdminConfigEntity?,
    language: Language
) {
    val allUsers by viewModel.allUsers.collectAsState()
    val isBn = language == Language.BN

    var selectedAdminTab by remember { mutableIntStateOf(0) } // 0: Users, 1: Point Pricing & AI Config

    var configState by remember(adminConfig) {
        mutableStateOf(adminConfig ?: AdminConfigEntity())
    }

    var apiKeyInput by remember(configState.customApiKey) {
        mutableStateOf(configState.customApiKey)
    }

    var broadcastNoticeInput by remember(configState.broadcastNotice) {
        mutableStateOf(configState.broadcastNotice)
    }

    var selectedModel by remember(configState.geminiModel) {
        mutableStateOf(configState.geminiModel)
    }
    var modelDropdownOpen by remember { mutableStateOf(false) }

    // Admin Point Pricing Inputs
    var point100PriceInput by remember(configState.pointsPricePer100) {
        mutableStateOf(configState.pointsPricePer100.toString())
    }
    var proPriceInput by remember(configState.proPlanPrice) {
        mutableStateOf(configState.proPlanPrice.toString())
    }
    var dailyRewardInput by remember(configState.dailyFreePoints) {
        mutableStateOf(configState.dailyFreePoints.toString())
    }
    var monthlyBonusInput by remember(configState.monthlyBonusPoints) {
        mutableStateOf(configState.monthlyBonusPoints.toString())
    }
    var pointsPerGenInput by remember(configState.pointsPerGeneration) {
        mutableStateOf(configState.pointsPerGeneration.toString())
    }
    var currencyInput by remember(configState.currency) {
        mutableStateOf(configState.currency)
    }
    var currencyDropdownOpen by remember { mutableStateOf(false) }

    // User Search & Dialog
    var userSearchQuery by remember { mutableStateOf("") }
    var showAddUserDialog by remember { mutableStateOf(false) }

    val filteredUsers = remember(allUsers, userSearchQuery) {
        if (userSearchQuery.isBlank()) allUsers
        else allUsers.filter {
            it.name.contains(userSearchQuery, ignoreCase = true) ||
            it.email.contains(userSearchQuery, ignoreCase = true) ||
            it.role.contains(userSearchQuery, ignoreCase = true)
        }
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("admin_panel_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header
        item {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(10.dp)
            ) {
                Box(
                    modifier = Modifier
                        .size(42.dp)
                        .clip(CircleShape)
                        .background(IndigoPrimary.copy(alpha = 0.2f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.AdminPanelSettings,
                        contentDescription = "Admin",
                        tint = IndigoPrimary,
                        modifier = Modifier.size(24.dp)
                    )
                }
                Column {
                    Text(
                        text = if (isBn) "অ্যাডমিন সুপারভাইজর কন্ট্রোল সেন্টার" else "Admin Supervisor Console",
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                    )
                    Text(
                        text = if (isBn) "পয়েন্টের দাম, ফ্রি রিওয়ার্ড কোটা ও ইউজার ম্যানেজমেন্ট" else "Points Pricing, Reward Quotas & User Governance",
                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        }

        // Section Tabs
        item {
            TabRow(
                selectedTabIndex = selectedAdminTab,
                containerColor = MaterialTheme.colorScheme.surfaceVariant,
                contentColor = IndigoPrimary,
                modifier = Modifier.clip(RoundedCornerShape(10.dp))
            ) {
                Tab(
                    selected = selectedAdminTab == 0,
                    onClick = { selectedAdminTab = 0 },
                    text = {
                        Text(
                            text = if (isBn) "👥 ইউজার ও পয়েন্ট (${allUsers.size})" else "👥 Users (${allUsers.size})",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_users")
                )
                Tab(
                    selected = selectedAdminTab == 1,
                    onClick = { selectedAdminTab = 1 },
                    text = {
                        Text(
                            text = if (isBn) "🪙 পয়েন্টের দাম ও এআই সেটিংস" else "🪙 Point Pricing & AI Config",
                            fontWeight = FontWeight.Bold,
                            fontSize = 12.sp
                        )
                    },
                    modifier = Modifier.testTag("admin_tab_pricing")
                )
            }
        }

        // Executive Metrics Grid
        item {
            Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isBn) "মোট ইউজার" else "Total Users",
                        value = "${allUsers.size.coerceAtLeast(configState.activeUsers)}",
                        subtitle = if (isBn) "অ্যাক্টিভ একাউন্ট" else "Active Accounts",
                        color = IndigoPrimary,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = if (isBn) "১০০ পয়েন্টের দাম" else "100 Pts Price",
                        value = "${configState.currency} ${configState.pointsPricePer100}",
                        subtitle = if (isBn) "অ্যাডমিনের দাম" else "Admin Set",
                        color = Color(0xFFF59E0B),
                        modifier = Modifier.weight(1f)
                    )
                }
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    StatCard(
                        title = if (isBn) "প্রো প্ল্যানের দাম" else "Pro Plan Price",
                        value = "${configState.currency} ${configState.proPlanPrice.toInt()}/mo",
                        subtitle = if (isBn) "আনলিমিটেড অ্যাক্সেস" else "Unlimited AI",
                        color = EmeraldSuccess,
                        modifier = Modifier.weight(1f)
                    )
                    StatCard(
                        title = if (isBn) "ডেইলি ফ্রি পয়েন্ট" else "Daily Free Pts",
                        value = "${configState.dailyFreePoints} Pts",
                        subtitle = if (isBn) "মাসে ৩০ বোনাস" else "30 Monthly Bonus",
                        color = CyanAccent,
                        modifier = Modifier.weight(1f)
                    )
                }
            }
        }

        if (selectedAdminTab == 0) {
            // ==================== USER & POINT MANAGEMENT SECTION ====================
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Group, contentDescription = null, tint = IndigoPrimary)
                                Column {
                                    Text(
                                        text = if (isBn) "ইউজার তালিকা ও পয়েন্ট কন্ট্রোল" else "User Registry & Points Control",
                                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                    )
                                    Text(
                                        text = if (isBn) "এডমিন যেকোনো ইউজারের পয়েন্ট বাড়াতে ও রোল পরিবর্তন করতে পারেন" else "Admin can add points, change roles and toggle status",
                                        style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    )
                                }
                            }

                            Button(
                                onClick = { showAddUserDialog = true },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(8.dp),
                                modifier = Modifier.height(34.dp).testTag("admin_btn_add_user")
                            ) {
                                Icon(Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(4.dp))
                                Text(if (isBn) "নতুন ইউজার" else "Add User", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = userSearchQuery,
                            onValueChange = { userSearchQuery = it },
                            placeholder = { Text(if (isBn) "নাম, ইমেইল বা রোল দিয়ে খুঁজুন..." else "Search users by name, email, or role...") },
                            leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("admin_user_search_input"),
                            shape = RoundedCornerShape(10.dp)
                        )
                    }
                }
            }

            // User Cards List
            items(filteredUsers, key = { it.id }) { user ->
                UserItemCard(
                    user = user,
                    isBn = isBn,
                    onToggleRole = { viewModel.toggleUserRole(user) },
                    onToggleStatus = { viewModel.toggleUserStatus(user) },
                    onChangePlan = { newPlan -> viewModel.changeUserPlan(user, newPlan) },
                    onAddPoints = { amount -> viewModel.adminAddPoints(user.id, amount) },
                    onDelete = { viewModel.deleteUser(user.id) }
                )
            }

        } else {
            // ==================== POINT PRICING & AI CONFIG SECTION ====================
            // Point Pricing Card
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFF59E0B).copy(alpha = 0.5f)),
                    modifier = Modifier.fillMaxWidth().testTag("admin_point_pricing_card")
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.PriceCheck, contentDescription = null, tint = Color(0xFFF59E0B))
                            Column {
                                Text(
                                    text = if (isBn) "পয়েন্টের মূল্য ও সাবস্ক্রিপশন ফি নির্ধারণ" else "Point Pricing & Subscription Governance",
                                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                                )
                                Text(
                                    text = if (isBn) "এখানে নির্ধারিত মূল্য ইউজার প্রো সাবস্ক্রিপশন কিনতে গেলে দেখতে পাবে" else "Pricing set here is visible to all users when purchasing Pro",
                                    style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                                )
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Currency Selector
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = if (isBn) "কারেন্সি নির্বাচন করুন:" else "Select Currency:",
                                style = MaterialTheme.typography.labelMedium.copy(fontWeight = FontWeight.Bold)
                            )
                            Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (currencyInput == "USD") IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { currencyInput = "USD" }
                                ) {
                                    Text(
                                        text = "$ USD",
                                        color = if (currencyInput == "USD") Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                                Surface(
                                    shape = RoundedCornerShape(6.dp),
                                    color = if (currencyInput == "BDT") IndigoPrimary else MaterialTheme.colorScheme.surfaceVariant,
                                    modifier = Modifier.clickable { currencyInput = "BDT" }
                                ) {
                                    Text(
                                        text = "৳ BDT",
                                        color = if (currencyInput == "BDT") Color.White else MaterialTheme.colorScheme.onSurface,
                                        fontWeight = FontWeight.Bold,
                                        fontSize = 11.sp,
                                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        // 100 Points Price
                        OutlinedTextField(
                            value = point100PriceInput,
                            onValueChange = { point100PriceInput = it },
                            label = { Text(if (isBn) "১০০ পয়েন্টের মূল্য ($currencyInput)" else "100 Points Pack Price ($currencyInput)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_points_100_price"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Pro Plan Monthly Price
                        OutlinedTextField(
                            value = proPriceInput,
                            onValueChange = { proPriceInput = it },
                            label = { Text(if (isBn) "প্রো সাবস্ক্রিপশন প্ল্যানের মাসিক মূল্য ($currencyInput)" else "Pro Subscription Monthly Price ($currencyInput)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_pro_plan_price"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Daily Free Points
                        OutlinedTextField(
                            value = dailyRewardInput,
                            onValueChange = { dailyRewardInput = it },
                            label = { Text(if (isBn) "প্রতিদিনের ফ্রি পয়েন্ট (ডিফল্ট: ৫)" else "Daily Free Points (Default: 5)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_daily_free_points"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Monthly Bonus Points
                        OutlinedTextField(
                            value = monthlyBonusInput,
                            onValueChange = { monthlyBonusInput = it },
                            label = { Text(if (isBn) "প্রতিমাসের বোনাস পয়েন্ট (ডিফল্ট: ৩০)" else "Monthly Bonus Points (Default: 30)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_monthly_bonus_points"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(10.dp))

                        // Points Cost Per Generation
                        OutlinedTextField(
                            value = pointsPerGenInput,
                            onValueChange = { pointsPerGenInput = it },
                            label = { Text(if (isBn) "প্রতিটি ওয়েবসাইট তৈরিতে খরচ পয়েন্ট (ডিফল্ট: ৫)" else "Points Cost Per Website Generation (Default: 5)") },
                            singleLine = true,
                            modifier = Modifier.fillMaxWidth().testTag("input_points_per_gen"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                val p100 = point100PriceInput.toDoubleOrNull() ?: 5.0
                                val pPro = proPriceInput.toDoubleOrNull() ?: 19.0
                                val dRew = dailyRewardInput.toIntOrNull() ?: 5
                                val mBon = monthlyBonusInput.toIntOrNull() ?: 30
                                val pGen = pointsPerGenInput.toIntOrNull() ?: 5

                                viewModel.updateAdminPointPricing(
                                    pointsPricePer100 = p100,
                                    proPlanPrice = pPro,
                                    dailyReward = dRew,
                                    monthlyBonus = mBon,
                                    pointsPerGen = pGen,
                                    currency = currencyInput
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("save_point_pricing_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "পয়েন্টের দাম ও নিয়মাবলী সংরক্ষণ করুন" else "Save Point Pricing & Rules", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            // AI Model Configuration
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.AutoAwesome, contentDescription = null, tint = IndigoPrimary)
                            Text(
                                text = "AI Model Engine & Override",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        Text(
                            text = "WebForge integrates directly with Gemini AI models with automatic fallback to high-performance local multi-page generation.",
                            style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Box {
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                                modifier = Modifier.fillMaxWidth()
                            ) {
                                Row(
                                    modifier = Modifier.fillMaxWidth().padding(12.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text(selectedModel, fontWeight = FontWeight.Bold)
                                    Button(
                                        onClick = { modelDropdownOpen = true },
                                        colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                        shape = RoundedCornerShape(6.dp),
                                        modifier = Modifier.height(30.dp)
                                    ) {
                                        Text("Change", fontSize = 10.sp)
                                    }
                                }
                            }

                            DropdownMenu(expanded = modelDropdownOpen, onDismissRequest = { modelDropdownOpen = false }) {
                                listOf("gemini-3.5-flash", "gemini-3.1-pro-preview", "gemini-flash-latest").forEach { model ->
                                    DropdownMenuItem(
                                        text = { Text(model) },
                                        onClick = {
                                            selectedModel = model
                                            modelDropdownOpen = false
                                        }
                                    )
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        OutlinedTextField(
                            value = apiKeyInput,
                            onValueChange = { apiKeyInput = it },
                            label = { Text("Custom Gemini API Key Override", fontSize = 11.sp) },
                            placeholder = { Text("Leave empty to use AI Studio Secrets") },
                            modifier = Modifier.fillMaxWidth().testTag("admin_api_key_input"),
                            shape = RoundedCornerShape(8.dp)
                        )
                    }
                }
            }

            // Broadcast Banner Announcement Editor
            item {
                Card(
                    shape = RoundedCornerShape(16.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.Campaign, contentDescription = null, tint = EmeraldSuccess)
                            Text(
                                text = if (isBn) "গ্লোবাল ব্রডকাস্ট অ্যানাউন্সমেন্ট" else "Global Broadcast Announcement",
                                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                            )
                        }

                        Spacer(modifier = Modifier.height(10.dp))

                        OutlinedTextField(
                            value = broadcastNoticeInput,
                            onValueChange = { broadcastNoticeInput = it },
                            label = { Text("Broadcast Message to All Users", fontSize = 11.sp) },
                            modifier = Modifier.fillMaxWidth().testTag("broadcast_notice_input"),
                            shape = RoundedCornerShape(8.dp)
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = {
                                viewModel.updateAdminConfig(
                                    configState.copy(
                                        geminiModel = selectedModel,
                                        customApiKey = apiKeyInput,
                                        broadcastNotice = broadcastNoticeInput
                                    )
                                )
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("save_admin_config_btn")
                        ) {
                            Icon(Icons.Default.Save, contentDescription = null)
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(if (isBn) "ব্রডকাস্ট নোটিশ সেভ করুন" else "Save Broadcast Notice", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }
        }
    }

    // Add User Dialog
    if (showAddUserDialog) {
        AdminAddUserDialog(
            isBn = isBn,
            onDismiss = { showAddUserDialog = false },
            onAddUser = { name, email, password, role, plan ->
                viewModel.register(name, email, password, role, plan)
                showAddUserDialog = false
            }
        )
    }
}

@Composable
fun UserItemCard(
    user: UserEntity,
    isBn: Boolean,
    onToggleRole: () -> Unit,
    onToggleStatus: () -> Unit,
    onChangePlan: (String) -> Unit,
    onAddPoints: (Int) -> Unit,
    onDelete: () -> Unit
) {
    var planMenuOpen by remember { mutableStateOf(false) }

    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(
            1.dp,
            if (user.role.uppercase() == "ADMIN") IndigoPrimary.copy(alpha = 0.5f) else MaterialTheme.colorScheme.outlineVariant
        ),
        modifier = Modifier.fillMaxWidth().testTag("user_card_${user.id}")
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    Box(
                        modifier = Modifier
                            .size(40.dp)
                            .clip(CircleShape)
                            .background(if (user.role.uppercase() == "ADMIN") IndigoPrimary else CyanAccent),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(
                            text = user.name.take(1).uppercase(),
                            color = Color.White,
                            fontWeight = FontWeight.Bold,
                            fontSize = 16.sp
                        )
                    }
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                            Text(user.name, style = MaterialTheme.typography.titleSmall.copy(fontWeight = FontWeight.Bold))
                            if (user.role.uppercase() == "ADMIN") {
                                Surface(color = IndigoPrimary.copy(alpha = 0.15f), shape = RoundedCornerShape(4.dp)) {
                                    Text("ADMIN 🛡️", color = IndigoPrimary, fontSize = 9.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp))
                                }
                            }
                        }
                        Text(user.email, style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
                    }
                }

                IconButton(onClick = onDelete, modifier = Modifier.size(32.dp)) {
                    Icon(imageVector = Icons.Default.Delete, contentDescription = "Delete", tint = MaterialTheme.colorScheme.error.copy(alpha = 0.8f), modifier = Modifier.size(18.dp))
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Points Row & Admin Add Points Buttons
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Surface(
                    color = Color(0xFFF59E0B).copy(alpha = 0.15f),
                    shape = RoundedCornerShape(6.dp)
                ) {
                    Text(
                        text = "🪙 ${user.points} Points",
                        color = Color(0xFFF59E0B),
                        fontWeight = FontWeight.Bold,
                        fontSize = 11.sp,
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
                    )
                }

                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = { onAddPoints(50) },
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("+50 Pts", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { onAddPoints(100) },
                        modifier = Modifier.height(28.dp),
                        shape = RoundedCornerShape(6.dp)
                    ) {
                        Text("+100 Pts", fontSize = 9.sp, fontWeight = FontWeight.Bold)
                    }
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Actions: Role, Plan, Status
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    OutlinedButton(
                        onClick = onToggleRole,
                        shape = RoundedCornerShape(6.dp),
                        modifier = Modifier.height(30.dp)
                    ) {
                        Text(if (user.role.uppercase() == "ADMIN") "Make Client" else "Make Admin", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                    }

                    Box {
                        Surface(
                            shape = RoundedCornerShape(6.dp),
                            color = when (user.plan.uppercase()) {
                                "PRO" -> IndigoPrimary.copy(alpha = 0.15f)
                                "BUSINESS" -> CyanAccent.copy(alpha = 0.15f)
                                else -> MaterialTheme.colorScheme.surfaceVariant
                            },
                            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outline),
                            modifier = Modifier.height(30.dp).clickable { planMenuOpen = true }
                        ) {
                            Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.padding(horizontal = 8.dp)) {
                                Text("Plan: ${user.plan.uppercase()} ▾", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }

                        DropdownMenu(expanded = planMenuOpen, onDismissRequest = { planMenuOpen = false }) {
                            listOf("FREE", "PRO", "BUSINESS").forEach { p ->
                                DropdownMenuItem(
                                    text = { Text(p) },
                                    onClick = {
                                        onChangePlan(p)
                                        planMenuOpen = false
                                    }
                                )
                            }
                        }
                    }
                }

                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(
                        text = if (user.isActive) (if (isBn) "সক্রিয়" else "Active") else (if (isBn) "ব্লক" else "Blocked"),
                        fontSize = 11.sp,
                        fontWeight = FontWeight.SemiBold,
                        color = if (user.isActive) EmeraldSuccess else MaterialTheme.colorScheme.error
                    )
                    Spacer(modifier = Modifier.width(6.dp))
                    Switch(
                        checked = user.isActive,
                        onCheckedChange = { onToggleStatus() },
                        colors = SwitchDefaults.colors(checkedThumbColor = EmeraldSuccess)
                    )
                }
            }
        }
    }
}

@Composable
fun AdminAddUserDialog(
    isBn: Boolean,
    onDismiss: () -> Unit,
    onAddUser: (String, String, String, String, String) -> Unit
) {
    var name by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var role by remember { mutableStateOf("USER") }
    var plan by remember { mutableStateOf("PRO") }

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = MaterialTheme.colorScheme.surface,
            border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
            modifier = Modifier.fillMaxWidth().padding(16.dp)
        ) {
            Column(modifier = Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = if (isBn) "নতুন ইউজার যোগ করুন" else "Create New User",
                        style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                    )
                    IconButton(onClick = onDismiss, modifier = Modifier.size(28.dp)) {
                        Icon(Icons.Default.Close, contentDescription = null)
                    }
                }

                OutlinedTextField(
                    value = name,
                    onValueChange = { name = it },
                    label = { Text(if (isBn) "ইউজারের নাম" else "Name") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = email,
                    onValueChange = { email = it },
                    label = { Text(if (isBn) "ইমেইল" else "Email") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                OutlinedTextField(
                    value = password,
                    onValueChange = { password = it },
                    label = { Text(if (isBn) "পাসওয়ার্ড" else "Password") },
                    singleLine = true,
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(8.dp)
                )

                Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedButton(
                        onClick = { role = "USER" },
                        colors = if (role == "USER") ButtonDefaults.outlinedButtonColors(containerColor = IndigoPrimary.copy(alpha = 0.2f)) else ButtonDefaults.outlinedButtonColors(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("User (Client)", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                    OutlinedButton(
                        onClick = { role = "ADMIN" },
                        colors = if (role == "ADMIN") ButtonDefaults.outlinedButtonColors(containerColor = CyanAccent.copy(alpha = 0.2f)) else ButtonDefaults.outlinedButtonColors(),
                        modifier = Modifier.weight(1f),
                        shape = RoundedCornerShape(8.dp)
                    ) {
                        Text("Admin 🛡️", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                    }
                }

                Button(
                    onClick = {
                        if (name.isNotBlank() && email.isNotBlank() && password.isNotBlank()) {
                            onAddUser(name, email, password, role, plan)
                        }
                    },
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.fillMaxWidth().height(44.dp)
                ) {
                    Text(if (isBn) "সংরক্ষণ করুন" else "Create User", fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}

@Composable
fun StatCard(
    title: String,
    value: String,
    subtitle: String,
    color: Color,
    modifier: Modifier = Modifier
) {
    Card(
        shape = RoundedCornerShape(12.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, color.copy(alpha = 0.3f)),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, fontSize = 11.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, fontSize = 18.sp, fontWeight = FontWeight.Bold, color = color)
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, fontSize = 10.sp, color = color.copy(alpha = 0.8f))
        }
    }
}

