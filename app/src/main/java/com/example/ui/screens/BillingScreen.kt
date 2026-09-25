package com.example.ui.screens

import androidx.compose.animation.AnimatedVisibility
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
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.KeyboardArrowDown
import androidx.compose.material.icons.filled.KeyboardArrowUp
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Star
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
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
import com.example.data.model.Language
import com.example.ui.theme.CyanAccent
import com.example.ui.theme.EmeraldSuccess
import com.example.ui.theme.IndigoPrimary
import com.example.ui.viewmodel.SubscriptionPlan
import com.example.ui.viewmodel.WebForgeViewModel

data class CreditTier(
    val credits: Int,
    val monthlyPrice: Int,
    val discountBadge: String? = null
)

@Composable
fun BillingScreen(
    viewModel: WebForgeViewModel,
    userPlan: SubscriptionPlan,
    language: Language,
    onBack: () -> Unit = {}
) {
    val adminConfig by viewModel.adminConfig.collectAsState()
    val currentUser by viewModel.currentUser.collectAsState()
    val isBn = language == Language.BN

    val currency = adminConfig?.currency ?: "$"
    val baseProPrice = adminConfig?.proPlanPrice ?: 25.0
    val point100Price = adminConfig?.pointsPricePer100 ?: 5.0
    val dailyFreePoints = adminConfig?.dailyFreePoints ?: 5
    val monthlyBonusPoints = adminConfig?.monthlyBonusPoints ?: 30

    var isYearlyBilling by remember { mutableStateOf(false) }
    var showTopUpDialog by remember { mutableStateOf(false) }
    var selectedProTierIndex by remember { mutableIntStateOf(0) }
    var proTierDropdownExpanded by remember { mutableStateOf(false) }

    val proTiers = listOf(
        CreditTier(100, baseProPrice.toInt()),
        CreditTier(200, (baseProPrice * 2).toInt()),
        CreditTier(400, (baseProPrice * 4).toInt()),
        CreditTier(800, (baseProPrice * 8).toInt()),
        CreditTier(1200, (baseProPrice * 12 * 0.98).toInt(), "Save 2%"),
        CreditTier(2000, (baseProPrice * 20 * 0.96).toInt(), "Save 4%"),
        CreditTier(3000, (baseProPrice * 30 * 0.94).toInt(), "Save 6%"),
        CreditTier(4000, (baseProPrice * 40 * 0.92).toInt(), "Save 8%")
    )

    val currentSelectedTier = proTiers.getOrElse(selectedProTierIndex) { proTiers.first() }

    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .background(Color(0xFFFAFAFA))
            .padding(horizontal = 16.dp)
            .testTag("billing_screen"),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Header (Matches Screenshot 6)
        item {
            Spacer(modifier = Modifier.height(6.dp))
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
                        .testTag("billing_back_btn")
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back",
                            tint = Color(0xFF374151),
                            modifier = Modifier.size(18.dp)
                        )
                    }
                }
                Spacer(modifier = Modifier.width(14.dp))
                Column {
                    Text(
                        text = if (isBn) "প্ল্যান ও ক্রেডিট ব্যবহার" else "Plans & credit usage",
                        style = MaterialTheme.typography.titleMedium.copy(
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827),
                            fontSize = 17.sp
                        )
                    )
                    Text(
                        text = if (isBn) "ইউজার প্যানেল ও পয়েন্ট ম্যানেজমেন্ট" else "User Panel & Credit Management",
                        style = MaterialTheme.typography.labelSmall.copy(color = Color(0xFF6B7280), fontSize = 10.sp)
                    )
                }
            }
        }

        // Card 1: Credits Overview & Daily/Monthly Claim (Matches Screenshot 6)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.fillMaxWidth().testTag("billing_credits_card")
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    // Header row: Credits | 5 left >
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "ক্রেডিট পয়েন্ট" else "Credits",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier.clickable { showTopUpDialog = true }
                        ) {
                            Text(
                                text = "${currentUser?.points ?: 0} " + (if (isBn) "পয়েন্ট বাকি" else "left"),
                                fontSize = 15.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(
                                imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                                contentDescription = null,
                                tint = Color(0xFF9CA3AF),
                                modifier = Modifier.size(18.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Progress Bar
                    val pointsRatio = ((currentUser?.points ?: 0).toFloat() / 100f).coerceIn(0.05f, 1f)
                    LinearProgressIndicator(
                        progress = { pointsRatio },
                        color = Color(0xFF4F46E5),
                        trackColor = Color(0xFFE5E7EB),
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(8.dp)
                            .clip(RoundedCornerShape(4.dp))
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    // Expiry info
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "${currentUser?.points ?: 0} পয়েন্ট মেয়াদ সেপ্টেম্বর ২০২৭" else "${currentUser?.points ?: 0} credits expire on 23 September 2027",
                            fontSize = 11.sp,
                            color = Color(0xFF6B7280)
                        )
                        Surface(
                            shape = RoundedCornerShape(4.dp),
                            color = Color(0xFFF3F4F6),
                            modifier = Modifier.clickable { }
                        ) {
                            Text(
                                text = if (isBn) "বিস্তারিত" else "Details",
                                fontSize = 10.sp,
                                fontWeight = FontWeight.SemiBold,
                                color = Color(0xFF4B5563),
                                modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(12.dp))

                    // Daily build credits with claim button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBn) "প্রতিদিনের ফ্রি পয়েন্ট" else "Daily build credits",
                                fontSize = 13.sp,
                                color = Color(0xFF374151)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(13.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "$dailyFreePoints " + (if (isBn) "বাকি" else "left"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Button(
                                onClick = { viewModel.claimDailyPoints() },
                                colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp).testTag("claim_daily_btn")
                            ) {
                                Text(if (isBn) "ক্লেইম (+$dailyFreePoints)" else "Claim (+$dailyFreePoints)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    // Monthly bonus points with claim button
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = if (isBn) "মাসিক বোনাস পয়েন্ট" else "Monthly bonus credits",
                                fontSize = 13.sp,
                                color = Color(0xFF374151)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Icon(Icons.Default.Info, contentDescription = null, tint = Color(0xFF9CA3AF), modifier = Modifier.size(13.dp))
                        }
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Text(
                                text = "$monthlyBonusPoints " + (if (isBn) "বোনাস" else "bonus"),
                                fontSize = 13.sp,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF111827)
                            )
                            Button(
                                onClick = { viewModel.claimMonthlyBonus() },
                                colors = ButtonDefaults.buttonColors(containerColor = Color(0xFFD97706)),
                                shape = RoundedCornerShape(6.dp),
                                contentPadding = androidx.compose.foundation.layout.PaddingValues(horizontal = 10.dp, vertical = 4.dp),
                                modifier = Modifier.height(28.dp).testTag("claim_monthly_btn")
                            ) {
                                Text(if (isBn) "ক্লেইম (+$monthlyBonusPoints)" else "Claim (+$monthlyBonusPoints)", fontSize = 10.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Need more credits box (Matches Screenshot 6)
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = Color(0xFFF9FAFB),
                        border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                        modifier = Modifier.fillMaxWidth()
                    ) {
                        Row(
                            modifier = Modifier.padding(12.dp),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text(
                                    text = if (isBn) "আরও ক্রেডিট পয়েন্ট চান?" else "Need more credits?",
                                    fontWeight = FontWeight.Bold,
                                    fontSize = 13.sp,
                                    color = Color(0xFF111827)
                                )
                                Text(
                                    text = if (isBn) "প্ল্যান পরিবর্তন করুন বা পয়েন্ট কিনুন।" else "Manage your plan or top up.",
                                    fontSize = 11.sp,
                                    color = Color(0xFF6B7280)
                                )
                            }
                            OutlinedButton(
                                onClick = { showTopUpDialog = true },
                                shape = RoundedCornerShape(8.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF111827)),
                                modifier = Modifier.height(34.dp).testTag("add_credits_btn")
                            ) {
                                Text(if (isBn) "পয়েন্ট কিনুন" else "Add credits", fontSize = 11.sp, fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }
            }
        }

        // Card 2: Usage Graph Card (Matches Screenshot 6)
        item {
            Card(
                shape = RoundedCornerShape(14.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(16.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "ব্যবহারের ইতিহাস" else "Usage",
                            fontSize = 15.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = null,
                            tint = Color(0xFF9CA3AF),
                            modifier = Modifier.size(18.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Visual mini chart representation
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(60.dp)
                            .background(Color(0xFFF9FAFB), RoundedCornerShape(8.dp))
                            .padding(8.dp),
                        contentAlignment = Alignment.BottomCenter
                    ) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.Bottom
                        ) {
                            Text("Aug 27", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                            Box(
                                modifier = Modifier
                                    .width(28.dp)
                                    .height(38.dp)
                                    .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                    .background(IndigoPrimary.copy(alpha = 0.8f))
                            )
                            Text("Sep 25", fontSize = 10.sp, color = Color(0xFF9CA3AF))
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween
                    ) {
                        Text(if (isBn) "গত ৩০ দিনে ব্যবহার" else "Last 30 days", fontSize = 13.sp, color = Color(0xFF374151))
                        Text(
                            text = "${currentUser?.points?.times(2) ?: 10} " + (if (isBn) "ক্রেডিট" else "credits"),
                            fontSize = 13.sp,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF111827)
                        )
                    }

                    Spacer(modifier = Modifier.height(10.dp))

                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(36.dp)
                    ) {
                        Text(if (isBn) "ব্যবহারের বিস্তারিত তথ্য" else "More usage details", fontSize = 12.sp, color = Color(0xFF374151))
                    }
                }
            }
        }

        // Section 3: Change your plan (Matches Screenshot 7, 8, 9, 10, 11, 12, 13)
        item {
            Column {
                Text(
                    text = if (isBn) "প্ল্যান পরিবর্তন করুন" else "Change your plan",
                    fontSize = 17.sp,
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF111827),
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                // Monthly / Yearly toggle (Matches Screenshot 7)
                Surface(
                    shape = RoundedCornerShape(20.dp),
                    color = Color(0xFFF3F4F6),
                    modifier = Modifier.align(Alignment.CenterHorizontally)
                ) {
                    Row(modifier = Modifier.padding(3.dp)) {
                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (!isYearlyBilling) Color.White else Color.Transparent,
                            shadowElevation = if (!isYearlyBilling) 2.dp else 0.dp,
                            modifier = Modifier.clickable { isYearlyBilling = false }
                        ) {
                            Text(
                                text = if (isBn) "মাসিক" else "Monthly",
                                fontSize = 12.sp,
                                fontWeight = FontWeight.Bold,
                                color = if (!isYearlyBilling) Color(0xFF111827) else Color(0xFF6B7280),
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp)
                            )
                        }

                        Surface(
                            shape = RoundedCornerShape(18.dp),
                            color = if (isYearlyBilling) Color.White else Color.Transparent,
                            shadowElevation = if (isYearlyBilling) 2.dp else 0.dp,
                            modifier = Modifier.clickable { isYearlyBilling = true }
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 6.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = if (isBn) "বাৎসরিক " else "Yearly ",
                                    fontSize = 12.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = if (isYearlyBilling) Color(0xFF111827) else Color(0xFF6B7280)
                                )
                                Text(
                                    text = if (isBn) "২ মাস ফ্রি" else "2 months free",
                                    fontSize = 11.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = Color(0xFF7C3AED)
                                )
                            }
                        }
                    }
                }
            }
        }

        // PRO Plan Card (Matches Screenshots 7, 8, 11)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.fillMaxWidth().testTag("billing_pro_card")
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text(
                        text = "Pro",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF111827)
                    )
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBn)
                            "দ্রুত কাজ করার জন্য রিয়েল-টাইমে একসাথে ওয়েবসাইট তৈরির জন্য উপযুক্ত।"
                        else
                            "Designed for fast-moving teams building together in real time.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )

                    Spacer(modifier = Modifier.height(14.dp))

                    val price = if (isYearlyBilling) (currentSelectedTier.monthlyPrice * 0.8).toInt() else currentSelectedTier.monthlyPrice
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text(
                            text = "$currency$price",
                            fontSize = 32.sp,
                            fontWeight = FontWeight.Black,
                            color = Color(0xFF111827)
                        )
                        Text(
                            text = if (isBn) " / প্রতি মাস" else " / month",
                            fontSize = 13.sp,
                            color = Color(0xFF6B7280),
                            modifier = Modifier.padding(bottom = 4.dp, start = 4.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Dropdown for Credits Tier (Matches Screenshots 7, 8, 11)
                    Box(modifier = Modifier.fillMaxWidth()) {
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = Color.White,
                            border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFD1D5DB)),
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { proTierDropdownExpanded = true }
                                .testTag("pro_credit_tier_dropdown")
                        ) {
                            Row(
                                modifier = Modifier.padding(horizontal = 12.dp, vertical = 10.dp),
                                horizontalArrangement = Arrangement.SpaceBetween,
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                Text(
                                    text = "${currentSelectedTier.credits} " + (if (isBn) "মাসিক ক্রেডিট" else "monthly credits"),
                                    fontSize = 13.sp,
                                    fontWeight = FontWeight.Medium,
                                    color = Color(0xFF111827)
                                )
                                Icon(
                                    imageVector = if (proTierDropdownExpanded) Icons.Default.KeyboardArrowUp else Icons.Default.KeyboardArrowDown,
                                    contentDescription = null,
                                    tint = Color(0xFF6B7280),
                                    modifier = Modifier.size(18.dp)
                                )
                            }
                        }

                        DropdownMenu(
                            expanded = proTierDropdownExpanded,
                            onDismissRequest = { proTierDropdownExpanded = false },
                            modifier = Modifier.fillMaxWidth(0.85f)
                        ) {
                            proTiers.forEachIndexed { index, tier ->
                                DropdownMenuItem(
                                    text = {
                                        Row(
                                            modifier = Modifier.fillMaxWidth(),
                                            horizontalArrangement = Arrangement.SpaceBetween,
                                            verticalAlignment = Alignment.CenterVertically
                                        ) {
                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                Text(
                                                    text = "${tier.credits} credits",
                                                    fontWeight = if (index == selectedProTierIndex) FontWeight.Bold else FontWeight.Normal,
                                                    color = if (index == selectedProTierIndex) Color(0xFF7C3AED) else Color(0xFF111827)
                                                )
                                                if (index == 0) {
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                    Surface(color = Color(0xFFF3F4F6), shape = RoundedCornerShape(4.dp)) {
                                                        Text("Current", fontSize = 10.sp, color = Color(0xFF6B7280), modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                    }
                                                }
                                            }

                                            Row(verticalAlignment = Alignment.CenterVertically) {
                                                if (tier.discountBadge != null) {
                                                    Surface(color = Color(0xFFDCFCE7), shape = RoundedCornerShape(4.dp)) {
                                                        Text(tier.discountBadge, fontSize = 10.sp, color = Color(0xFF15803D), fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp))
                                                    }
                                                    Spacer(modifier = Modifier.width(6.dp))
                                                }
                                                Text(
                                                    text = "$currency${tier.monthlyPrice}/mo",
                                                    fontWeight = FontWeight.Bold,
                                                    fontSize = 12.sp,
                                                    color = if (index == selectedProTierIndex) Color(0xFF7C3AED) else Color(0xFF4B5563)
                                                )
                                            }
                                        }
                                    },
                                    onClick = {
                                        selectedProTierIndex = index
                                        proTierDropdownExpanded = false
                                    }
                                )
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(14.dp))

                    // Plan Button: [Your current plan] or [Upgrade]
                    if (currentUser?.plan == "PRO") {
                        OutlinedButton(
                            onClick = {},
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(42.dp),
                            enabled = false
                        ) {
                            Text(if (isBn) "আপনার বর্তমান প্ল্যান" else "Your current plan", color = Color(0xFF6B7280))
                        }
                    } else {
                        Button(
                            onClick = { viewModel.upgradeToProPlan() },
                            colors = ButtonDefaults.buttonColors(containerColor = IndigoPrimary),
                            shape = RoundedCornerShape(8.dp),
                            modifier = Modifier.fillMaxWidth().height(44.dp).testTag("btn_upgrade_pro")
                        ) {
                            Text(
                                text = if (isBn) "প্রো প্ল্যান আপগ্রেড করুন 🚀" else "Upgrade to Pro 🚀",
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    // Unlimited users & Free grants
                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("👥", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBn) "সীমাহীন ইউজার (Unlimited users)" else "Unlimited users", fontSize = 12.sp, color = Color(0xFF374151))
                    }

                    Row(
                        modifier = Modifier.padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text("🎁", fontSize = 14.sp)
                        Spacer(modifier = Modifier.width(8.dp))
                        Text(if (isBn) "ফ্রি গ্রান্টস ও রিওয়ার্ড" else "Free grants ▾", fontSize = 12.sp, color = Color(0xFF374151))
                    }

                    Spacer(modifier = Modifier.height(10.dp))
                    HorizontalDivider(color = Color(0xFFF3F4F6))
                    Spacer(modifier = Modifier.height(10.dp))

                    // Features checklist (Matches Screenshot 8)
                    LovableFeatureCheck("All Free features")
                    LovableFeatureCheck("${currentSelectedTier.credits} Pro credits")
                    LovableFeatureCheck("Credit rollovers")
                    LovableFeatureCheck("On-demand credit top-ups")
                    LovableFeatureCheck("Unlimited webforge.app domains")
                    LovableFeatureCheck("Custom domains & SSL")
                    LovableFeatureCheck("User roles & permissions")
                    LovableFeatureCheck("Per-member credit limits")
                    LovableFeatureCheck("Remove the WebForge badge")
                    LovableFeatureCheck("Email support")
                    LovableFeatureCheck("Design systems")
                }
            }
        }

        // Business Plan Card (Matches Screenshot 9)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Business", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBn) "উন্নত নিয়ন্ত্রণ ও বড় টিমের জন্য প্রিমিয়াম ফিচার।" else "Advanced controls and power features for growing departments",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Row(verticalAlignment = Alignment.Bottom) {
                        Text("$currency 50", fontSize = 32.sp, fontWeight = FontWeight.Black, color = Color(0xFF111827))
                        Text(if (isBn) " / প্রতি মাস" else " / month", fontSize = 13.sp, color = Color(0xFF6B7280), modifier = Modifier.padding(bottom = 4.dp, start = 4.dp))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = { viewModel.upgradeToProPlan() },
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text(if (isBn) "বিজনেস আপগ্রেড করুন" else "Upgrade", fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    }
                    Spacer(modifier = Modifier.height(14.dp))
                    LovableFeatureCheck("All Pro features")
                    LovableFeatureCheck("100 Business credits")
                    LovableFeatureCheck("Team workspace")
                    LovableFeatureCheck("Role-based access")
                    LovableFeatureCheck("SSO & Security center")
                    LovableFeatureCheck("Priority support")
                }
            }
        }

        // Enterprise Card (Matches Screenshot 10)
        item {
            Card(
                shape = RoundedCornerShape(16.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                border = androidx.compose.foundation.BorderStroke(1.dp, Color(0xFFE5E7EB)),
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Text("Enterprise", fontSize = 20.sp, fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    Spacer(modifier = Modifier.height(4.dp))
                    Text(
                        text = if (isBn) "বড় কর্পোরেট ও স্কেলেবল প্রতিষ্ঠানের জন্য।" else "Built for large orgs needing flexibility, scale, and governance.",
                        fontSize = 12.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(14.dp))
                    Text("Platform fee", fontSize = 16.sp, fontWeight = FontWeight.SemiBold, color = Color(0xFF4B5563))
                    Spacer(modifier = Modifier.height(14.dp))
                    OutlinedButton(
                        onClick = {},
                        shape = RoundedCornerShape(8.dp),
                        modifier = Modifier.fillMaxWidth().height(42.dp)
                    ) {
                        Text(if (isBn) "ডেমো বুক করুন" else "Book a demo", fontWeight = FontWeight.Bold, color = Color(0xFF111827))
                    }
                }
            }
            Spacer(modifier = Modifier.height(30.dp))
        }
    }

    // Quick Points Top-up Dialog
    if (showTopUpDialog) {
        androidx.compose.ui.window.Dialog(onDismissRequest = { showTopUpDialog = false }) {
            Surface(
                shape = RoundedCornerShape(16.dp),
                color = Color.White,
                modifier = Modifier.fillMaxWidth().padding(16.dp)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = if (isBn) "পয়েন্ট টপ-আপ প্যাক" else "Top-up Credits",
                            fontSize = 17.sp,
                            fontWeight = FontWeight.Bold
                        )
                        IconButton(onClick = { showTopUpDialog = false }, modifier = Modifier.size(24.dp)) {
                            Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Close", modifier = Modifier.size(16.dp))
                        }
                    }
                    Text(
                        text = if (isBn) "অ্যাডমিন নির্ধারিত মূল্য অনুযায়ী পয়েন্ট রিচার্জ করুন।" else "Purchase additional credits at admin set rates.",
                        fontSize = 11.sp,
                        color = Color(0xFF6B7280)
                    )
                    Spacer(modifier = Modifier.height(16.dp))

                    PointPackRow(
                        title = if (isBn) "১০০ পয়েন্ট প্যাক" else "100 Credits Pack",
                        subtitle = if (isBn) "২০টি ওয়েবসাইট তৈরি করা যাবে" else "Build 20 multi-page websites",
                        points = 100,
                        price = "$currency ${point100Price}",
                        btnText = if (isBn) "কিনুন" else "Buy",
                        onBuy = {
                            viewModel.purchasePointsPack(100, "100 Credits")
                            showTopUpDialog = false
                        }
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    PointPackRow(
                        title = if (isBn) "৫০০ পয়েন্ট প্যাক" else "500 Credits Pack",
                        subtitle = if (isBn) "১০০টি ওয়েবসাইট তৈরি" else "Build 100 multi-page websites",
                        points = 500,
                        price = "$currency ${(point100Price * 4.5).toInt()}",
                        btnText = if (isBn) "কিনুন (-১০%)" else "Buy (-10%)",
                        isHighlight = true,
                        onBuy = {
                            viewModel.purchasePointsPack(500, "500 Credits")
                            showTopUpDialog = false
                        }
                    )
                }
            }
        }
    }
}

@Composable
fun LovableFeatureCheck(text: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 3.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(
            imageVector = Icons.Default.Check,
            contentDescription = null,
            tint = Color(0xFF111827),
            modifier = Modifier.size(15.dp)
        )
        Spacer(modifier = Modifier.width(8.dp))
        Text(
            text = text,
            fontSize = 12.sp,
            color = Color(0xFF374151)
        )
    }
}

@Composable
fun PointPackRow(
    title: String,
    subtitle: String,
    points: Int,
    price: String,
    btnText: String,
    isHighlight: Boolean = false,
    onBuy: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = if (isHighlight) IndigoPrimary.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant,
        border = androidx.compose.foundation.BorderStroke(1.dp, if (isHighlight) IndigoPrimary else MaterialTheme.colorScheme.outlineVariant),
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(12.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                    Text(title, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Surface(color = Color(0xFFF59E0B).copy(alpha = 0.2f), shape = RoundedCornerShape(4.dp)) {
                        Text("+$points", color = Color(0xFFF59E0B), fontSize = 10.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 4.dp))
                    }
                }
                Text(subtitle, fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text(price, fontWeight = FontWeight.Black, fontSize = 12.sp, color = IndigoPrimary, modifier = Modifier.padding(top = 2.dp))
            }

            Button(
                onClick = onBuy,
                colors = ButtonDefaults.buttonColors(containerColor = if (isHighlight) IndigoPrimary else MaterialTheme.colorScheme.primary),
                shape = RoundedCornerShape(6.dp),
                modifier = Modifier.height(34.dp)
            ) {
                Text(btnText, fontSize = 11.sp, fontWeight = FontWeight.Bold)
            }
        }
    }
}

