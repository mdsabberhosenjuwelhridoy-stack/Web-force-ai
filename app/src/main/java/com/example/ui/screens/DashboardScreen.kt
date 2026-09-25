package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
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
import androidx.compose.material.icons.automirrored.filled.Launch
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Launch
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.RocketLaunch
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
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
import com.example.data.local.entity.ProjectEntity
import com.example.data.model.AppStrings
import com.example.data.model.Language
import com.example.data.model.WebsiteType
import com.example.ui.components.AppDestination
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.WebForgeViewModel

@OptIn(ExperimentalLayoutApi::class)
@Composable
fun DashboardScreen(
    viewModel: WebForgeViewModel,
    projects: List<ProjectEntity>,
    language: Language,
    onOpenWorkspace: (Long) -> Unit,
    onNavigate: (AppDestination) -> Unit
) {
    var promptInput by remember {
        mutableStateOf(
            if (language == Language.BN)
                "আমি একটি অনলাইন কাপড়ের দোকানের ওয়েবসাইট চাই। নাম হবে XYZ Fashion। Home, Products, Cart, Checkout, About Us এবং Contact পেজ থাকবে।"
            else
                "Build an online clothing store named XYZ Fashion. Include Home, Products, Cart, Checkout, About Us, and Contact pages with responsive product cards, cart drawer, and order confirmation."
        )
    }

    var selectedCategory by remember { mutableStateOf(WebsiteType.E_COMMERCE) }
    var websiteName by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("dashboard_screen"),
        verticalArrangement = Arrangement.spacedBy(20.dp)
    ) {
        // Hero Prompt Section
        item {
            Card(
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("hero_prompt_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        Box(
                            modifier = Modifier
                                .size(28.dp)
                                .clip(CircleShape)
                                .background(IndigoPrimary.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.AutoAwesome,
                                contentDescription = null,
                                tint = IndigoPrimary,
                                modifier = Modifier.size(16.dp)
                            )
                        }
                        Text(
                            text = if (language == Language.BN) "আপনি কী ধরনের ওয়েবসাইট বানাতে চান?" else "Describe the website you want to build",
                            style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                        )
                    }

                    Spacer(modifier = Modifier.height(12.dp))

                    // Prompt Input Field
                    OutlinedTextField(
                        value = promptInput,
                        onValueChange = { promptInput = it },
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(130.dp)
                            .testTag("main_prompt_input"),
                        placeholder = {
                            Text(
                                AppStrings.get("describe_prompt_hint", language),
                                style = MaterialTheme.typography.bodySmall
                            )
                        },
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = IndigoPrimary,
                            unfocusedBorderColor = MaterialTheme.colorScheme.outline,
                            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f),
                            unfocusedContainerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.3f)
                        ),
                        shape = RoundedCornerShape(12.dp)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    // Optional Site Name
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(10.dp)
                    ) {
                        OutlinedTextField(
                            value = websiteName,
                            onValueChange = { websiteName = it },
                            label = { Text(if (language == Language.BN) "ওয়েবসাইটের নাম (ঐচ্ছিক)" else "Website Name (Optional)", fontSize = 11.sp) },
                            placeholder = { Text("e.g. XYZ Fashion", fontSize = 11.sp) },
                            singleLine = true,
                            modifier = Modifier
                                .weight(1f)
                                .testTag("site_name_input"),
                            shape = RoundedCornerShape(10.dp)
                        )

                        Button(
                            onClick = {
                                if (promptInput.isNotBlank()) {
                                    viewModel.generateWebsite(
                                        prompt = promptInput,
                                        name = websiteName.ifBlank { null },
                                        category = selectedCategory,
                                        onComplete = { newId ->
                                            onOpenWorkspace(newId)
                                        }
                                    )
                                }
                            },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .height(56.dp)
                                .testTag("generate_website_btn")
                        ) {
                            Icon(Icons.Default.RocketLaunch, contentDescription = null, modifier = Modifier.size(18.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text(
                                AppStrings.get("generate_button", language),
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Example Prompts Chips
                    Text(
                        text = if (language == Language.BN) "উদাহরণ প্রম্পটস:" else "Example prompts:",
                        style = MaterialTheme.typography.labelSmall.copy(
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                            fontWeight = FontWeight.Bold
                        )
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    FlowRow(
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        val examples = listOf(
                            WebsiteType.E_COMMERCE,
                            WebsiteType.SPORTS_CRICKET,
                            WebsiteType.RESTAURANT,
                            WebsiteType.SAAS_LANDING,
                            WebsiteType.PORTFOLIO
                        )
                        examples.forEach { cat ->
                            Surface(
                                shape = RoundedCornerShape(8.dp),
                                color = if (selectedCategory == cat) IndigoPrimary.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                                border = androidx.compose.foundation.BorderStroke(
                                    1.dp,
                                    if (selectedCategory == cat) IndigoPrimary else MaterialTheme.colorScheme.outline
                                ),
                                modifier = Modifier
                                    .clickable {
                                        selectedCategory = cat
                                        promptInput = cat.getSamplePrompt(language)
                                    }
                                    .testTag("example_prompt_${cat.id}")
                            ) {
                                Text(
                                    text = "${cat.getTitle(language)} ⚡",
                                    style = MaterialTheme.typography.labelSmall.copy(
                                        fontWeight = FontWeight.Medium,
                                        color = if (selectedCategory == cat) IndigoPrimary else MaterialTheme.colorScheme.onSurface
                                    ),
                                    modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp)
                                )
                            }
                        }
                    }
                }
            }
        }

        // Stats Overview Row
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                StatCard(
                    title = if (language == Language.BN) "মোট প্রজেক্ট" else "Total Projects",
                    value = "${projects.size}",
                    subtitle = if (language == Language.BN) "সম্পূর্ণ তৈরি" else "Fully Generated",
                    color = IndigoPrimary,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (language == Language.BN) "লাইভ ডিপ্লয়" else "Active Deployments",
                    value = "${projects.count { it.isDeployed }}",
                    subtitle = "Edge CDN Live",
                    color = EmeraldSuccess,
                    modifier = Modifier.weight(1f)
                )
                StatCard(
                    title = if (language == Language.BN) "এআই জেনারেশন" else "AI Generations",
                    value = "50 / 50",
                    subtitle = "PRO Plan Ready",
                    color = CyanAccent,
                    modifier = Modifier.weight(1f)
                )
            }
        }

        // Recent Projects Section Header
        item {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = if (language == Language.BN) "সাম্প্রতিক প্রজেক্টসমূহ" else "Recent Projects",
                    style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
                )
                Text(
                    text = if (language == Language.BN) "সব দেখুন" else "View All",
                    style = MaterialTheme.typography.labelMedium.copy(
                        color = IndigoPrimary,
                        fontWeight = FontWeight.Bold
                    ),
                    modifier = Modifier
                        .clickable { onNavigate(AppDestination.PROJECTS) }
                        .testTag("view_all_projects_btn")
                )
            }
        }

        // Project Cards
        if (projects.isEmpty()) {
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(24.dp),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        text = if (language == Language.BN) "এখনও কোনো প্রজেক্ট তৈরি হয়নি। উপরের প্রম্পট দিয়ে শুরু করুন!" else "No projects yet. Enter a prompt above to build your first website!",
                        style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                    )
                }
            }
        } else {
            items(projects) { project ->
                ProjectCardItem(
                    project = project,
                    onOpen = {
                        viewModel.selectProject(project.id)
                        onOpenWorkspace(project.id)
                    },
                    onDuplicate = { viewModel.duplicateProject(project.id) },
                    onDelete = { viewModel.deleteProject(project.id) },
                    onDeploy = {
                        viewModel.selectProject(project.id)
                        viewModel.deployActiveProject(null) {}
                    }
                )
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
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = modifier
    ) {
        Column(modifier = Modifier.padding(14.dp)) {
            Text(title, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant))
            Spacer(modifier = Modifier.height(4.dp))
            Text(value, style = MaterialTheme.typography.headlineSmall.copy(fontWeight = FontWeight.ExtraBold, color = color))
            Spacer(modifier = Modifier.height(2.dp))
            Text(subtitle, style = MaterialTheme.typography.labelSmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant, fontSize = 9.sp))
        }
    }
}

@Composable
fun ProjectCardItem(
    project: ProjectEntity,
    onOpen: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit,
    onDeploy: () -> Unit
) {
    Card(
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = androidx.compose.foundation.BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onOpen() }
            .testTag("project_card_${project.id}")
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(10.dp)
                ) {
                    Box(
                        modifier = Modifier
                            .size(38.dp)
                            .clip(RoundedCornerShape(8.dp))
                            .background(
                                try {
                                    Color(android.graphics.Color.parseColor(project.primaryColor))
                                } catch (_: Throwable) {
                                    IndigoPrimary
                                }
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        Text("⚡", fontSize = 18.sp)
                    }
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

                Surface(
                    color = if (project.isDeployed) EmeraldSuccess.copy(alpha = 0.15f) else MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(8.dp)
                ) {
                    Text(
                        text = if (project.isDeployed) "🟢 Live" else "🟡 Sandbox",
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        style = MaterialTheme.typography.labelSmall.copy(
                            fontWeight = FontWeight.Bold,
                            color = if (project.isDeployed) EmeraldSuccess else MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            Text(
                text = project.description,
                style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                maxLines = 2
            )

            Spacer(modifier = Modifier.height(12.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(horizontalArrangement = Arrangement.spacedBy(4.dp)) {
                    IconButton(
                        onClick = onOpen,
                        modifier = Modifier.size(32.dp).testTag("open_project_${project.id}")
                    ) {
                        Icon(Icons.AutoMirrored.Filled.Launch, contentDescription = "Open", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDuplicate,
                        modifier = Modifier.size(32.dp).testTag("duplicate_project_${project.id}")
                    ) {
                        Icon(Icons.Default.ContentCopy, contentDescription = "Duplicate", modifier = Modifier.size(16.dp))
                    }
                    IconButton(
                        onClick = onDelete,
                        modifier = Modifier.size(32.dp).testTag("delete_project_${project.id}")
                    ) {
                        Icon(Icons.Default.Delete, contentDescription = "Delete", modifier = Modifier.size(16.dp), tint = MaterialTheme.colorScheme.error)
                    }
                }

                Button(
                    onClick = onOpen,
                    colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                    shape = RoundedCornerShape(8.dp),
                    modifier = Modifier.height(36.dp).testTag("builder_open_btn_${project.id}")
                ) {
                    Text("Open Builder", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                }
            }
        }
    }
}
