package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(tableName = "projects")
data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val slug: String,
    val description: String,
    val category: String, // e.g. E_COMMERCE, SPORTS_CRICKET, RESTAURANT
    val primaryColor: String = "#4F46E5",
    val language: String = "en",
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis(),
    val deployedUrl: String? = null,
    val isDeployed: Boolean = false,
    val deploymentStatus: String = "IDLE", // IDLE, BUILDING, DEPLOYED, FAILED
    val customDomain: String? = null,
    val sslActive: Boolean = true,
    val buildLogs: String = ""
)

@Entity(
    tableName = "project_files",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId"), Index(value = ["projectId", "filePath"], unique = true)]
)
data class ProjectFileEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val filePath: String, // e.g. "index.html", "products.html", "styles.css", "app.js", "schema.sql", "api.ts"
    val fileType: String, // "HTML", "CSS", "JS", "SQL", "TS", "JSON", "MD"
    val content: String,
    val updatedAt: Long = System.currentTimeMillis()
)

@Entity(
    tableName = "chat_messages",
    foreignKeys = [
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["id"],
            childColumns = ["projectId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("projectId")]
)
data class ChatMessageEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val projectId: Long,
    val sender: String, // "user", "ai", "system"
    val message: String,
    val timestamp: Long = System.currentTimeMillis(),
    val suggestedActions: String = "" // comma separated suggestions
)

@Entity(tableName = "admin_config")
data class AdminConfigEntity(
    @PrimaryKey
    val id: Int = 1,
    val geminiModel: String = "gemini-3.5-flash",
    val customApiKey: String = "",
    val activeUsers: Int = 142,
    val totalGenerations: Int = 538,
    val monthlyRevenue: Double = 12450.00,
    val freePlanGenerationsLimit: Int = 5,
    val proPlanGenerationsLimit: Int = 50,
    val businessPlanGenerationsLimit: Int = 9999,
    val broadcastNotice: String = "⚡ WebForge AI 2.0 Engine is live! Fast multi-page website generation is active.",
    // Point Pricing & Rules controlled by Admin
    val dailyFreePoints: Int = 5,
    val monthlyBonusPoints: Int = 30,
    val pointsPerGeneration: Int = 5,
    val pointsPricePer100: Double = 5.0,
    val proPlanPrice: Double = 19.0,
    val businessPlanPrice: Double = 49.0,
    val currency: String = "USD"
)

@Entity(
    tableName = "users",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val email: String,
    val passwordHash: String,
    val role: String = "USER", // "ADMIN", "USER", "DEVELOPER"
    val plan: String = "FREE", // "FREE", "PRO", "BUSINESS"
    val points: Int = 10,
    val lastDailyClaim: Long = 0L,
    val lastMonthlyClaim: Long = 0L,
    val authProvider: String = "EMAIL", // "EMAIL", "GOOGLE"
    val avatarColor: String = "#6366F1",
    val isActive: Boolean = true,
    val createdAt: Long = System.currentTimeMillis()
)
