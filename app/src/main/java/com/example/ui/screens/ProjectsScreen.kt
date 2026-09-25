package com.example.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.data.local.entity.ProjectEntity
import com.example.data.model.Language
import com.example.ui.viewmodel.WebForgeViewModel

@Composable
fun ProjectsScreen(
    viewModel: WebForgeViewModel,
    projects: List<ProjectEntity>,
    language: Language,
    onOpenWorkspace: (Long) -> Unit
) {
    var searchFilter by remember { mutableStateOf("") }

    val filtered = projects.filter {
        it.name.contains(searchFilter, ignoreCase = true) ||
                it.category.contains(searchFilter, ignoreCase = true) ||
                it.description.contains(searchFilter, ignoreCase = true)
    }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp)
            .testTag("projects_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            Column {
                Text(
                    text = if (language == Language.BN) "আমার প্রজেক্টসমূহ" else "My WebForge Projects",
                    style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold)
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = if (language == Language.BN) "আপনার সমস্ত তৈরি করা ওয়েবসাইট পরিচালনা ও এডিট করুন।" else "Manage, preview, duplicate, deploy, or export any of your saved websites.",
                    style = MaterialTheme.typography.bodySmall.copy(color = MaterialTheme.colorScheme.onSurfaceVariant)
                )

                Spacer(modifier = Modifier.height(14.dp))

                OutlinedTextField(
                    value = searchFilter,
                    onValueChange = { searchFilter = it },
                    placeholder = { Text("Search by name, type, or keyword...") },
                    leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                    singleLine = true,
                    shape = RoundedCornerShape(12.dp),
                    modifier = Modifier.fillMaxWidth().testTag("projects_search_input")
                )
            }
        }

        if (filtered.isEmpty()) {
            item {
                Text(
                    text = "No projects matching '$searchFilter'",
                    style = MaterialTheme.typography.bodyMedium.copy(color = MaterialTheme.colorScheme.onSurfaceVariant),
                    modifier = Modifier.padding(24.dp)
                )
            }
        } else {
            items(filtered) { project ->
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
