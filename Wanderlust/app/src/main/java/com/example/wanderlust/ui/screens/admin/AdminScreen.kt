package com.example.wanderlust.ui.screens.admin

import com.example.wanderlust.R

import androidx.compose.foundation.background
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.Public
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.example.wanderlust.data.WanderlustImages
import com.example.wanderlust.ui.components.ExperienceCatalogCard
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.ThemeToggleButton
import com.example.wanderlust.ui.components.WanderlustBottomNav
import com.example.wanderlust.ui.components.WanderlustNavTab
import com.example.wanderlust.viewmodel.AdminViewModel

/** Matches stitch …/admin_dashboard_wanderlust_dark/screen.png */
@Composable
fun AdminScreen(
    isDarkTheme: Boolean,
    onToggleTheme: () -> Unit,
    onBack: () -> Unit,
    onOpenBookings: () -> Unit,
    onExportData: () -> Unit,
    onAddTour: () -> Unit,
    onEditTour: () -> Unit,
    onManageUsers: () -> Unit,
    onManageBillingPlans: () -> Unit = {},
    onManagePendingPayments: () -> Unit = {},
    onOpenAnalytics: () -> Unit,
    viewModel: AdminViewModel = viewModel(),
) {
    val state = viewModel.uiState
    val tours = state.activeTours?.toString() ?: "1,284"
    val users = state.users?.toString() ?: "42.8k"

    Box(modifier = Modifier.fillMaxSize()) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(bottom = 72.dp),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 16.dp, vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween,
            ) {
                Icon(Icons.Default.Search, null, tint = MaterialTheme.colorScheme.primary)
                Text("Wanderlust", style = MaterialTheme.typography.titleLarge, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Notifications, null, tint = MaterialTheme.colorScheme.onSurfaceVariant)
                    ThemeToggleButton(isDark = isDarkTheme, onToggle = onToggleTheme, modifier = Modifier.padding(start = 8.dp))
                    TextButton(onClick = onExportData) {
                        Text(
                            stringApp(R.string.admin_export_data),
                            modifier = Modifier
                                .clip(RoundedCornerShape(999.dp))
                                .background(MaterialTheme.colorScheme.primaryContainer)
                                .padding(horizontal = 12.dp, vertical = 6.dp),
                            color = MaterialTheme.colorScheme.onPrimaryContainer,
                        )
                    }
                }
            }

            Column(modifier = Modifier.padding(horizontal = 16.dp)) {
                Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    AdminStatCard(Modifier.weight(1f), Icons.Default.Explore, "+12%", stringApp(R.string.admin_suggested_places), tours, MaterialTheme.colorScheme.tertiary)
                    AdminStatCard(Modifier.weight(1f), Icons.Default.Public, "+5.4k", stringApp(R.string.admin_global_users), users, MaterialTheme.colorScheme.primary)
                }
                Spacer(Modifier.height(12.dp))
                SuggestionsOverviewCard()
                Spacer(Modifier.height(12.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    QuickActionChip(stringApp(R.string.admin_add_tour_chip), Modifier.weight(1f), onAddTour)
                    QuickActionChip(stringApp(R.string.admin_edit_tour_chip), Modifier.weight(1f), onEditTour)
                }
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp), modifier = Modifier.padding(top = 8.dp)) {
                    QuickActionChip(stringApp(R.string.admin_manage_users), Modifier.weight(1f), onManageUsers)
                    QuickActionChip(stringApp(R.string.admin_billing_plans_chip), Modifier.weight(1f), onManageBillingPlans)
                }
                Row(
                    Modifier
                        .fillMaxWidth()
                        .padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    QuickActionChip(stringApp(R.string.admin_payments_chip), Modifier.weight(1f), onManagePendingPayments)
                    QuickActionChip(stringApp(R.string.admin_analytics_chip), Modifier.weight(1f), onOpenAnalytics)
                }

                if (state.isLoading) CircularProgressIndicator(modifier = Modifier.padding(8.dp))
                state.errorMessage?.let {
                    Text(it, color = MaterialTheme.colorScheme.error)
                    TextButton(onClick = viewModel::loadStats) { Text(stringApp(R.string.btn_retry)) }
                }

                Spacer(Modifier.height(16.dp))
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                    Text(stringApp(R.string.admin_recent_saves), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                    Text(
                        stringApp(R.string.admin_view_all),
                        color = MaterialTheme.colorScheme.primary,
                        style = MaterialTheme.typography.labelLarge,
                        modifier = Modifier.clickable(onClick = onOpenBookings),
                    )
                }
                Spacer(Modifier.height(8.dp))
                SavedPlaceRow(WanderlustImages.USER_FELIX, "Felix Arvid", "Angkor Wat", "★ 4.9", "Saved", MaterialTheme.colorScheme.tertiary)
                SavedPlaceRow(WanderlustImages.USER_MAYA, "Maya K. Lee", "Koh Rong", "★ 4.8", "Saved", MaterialTheme.colorScheme.primaryContainer)
                SavedPlaceRow(WanderlustImages.USER_LIAM, "Liam Sterling", "Mondulkiri", "★ 4.7", "Saved", MaterialTheme.colorScheme.tertiary)

                Spacer(Modifier.height(16.dp))
                Text(stringApp(R.string.admin_system_activity), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                ActivityLine("New Tour Listing", "Admin added \"Icelandic Aurora Expedition\"", "2 mins ago", MaterialTheme.colorScheme.primary)
                ActivityLine("User Verified", "Elena Rossi completed ID verification", "1 hour ago", MaterialTheme.colorScheme.tertiary)
                ActivityLine("Catalog Update", "Added new Cambodia place suggestions", "4 hours ago", MaterialTheme.colorScheme.onSurfaceVariant)

                Spacer(Modifier.height(16.dp))
                Text(stringApp(R.string.admin_catalog_highlights), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                ExperienceCatalogCard(WanderlustImages.HERO_MOUNTAINS, "TRENDING", "Angkor Wat", "Most saved this week")
                Spacer(Modifier.height(8.dp))
                ExperienceCatalogCard(WanderlustImages.BEACH, "HIGH RATING", "Koh Rong", "Top coast suggestion")

                Spacer(Modifier.height(16.dp))
                Button(onClick = onBack, modifier = Modifier.fillMaxWidth()) {
                    Text(stringApp(R.string.btn_back))
                }
            }
        }

        WanderlustBottomNav(
            selected = WanderlustNavTab.Home,
            modifier = Modifier.align(Alignment.BottomEnd),
            onHome = onBack,
            onTours = onBack,
            onSaved = onBack,
            onProfile = onBack,
        )
    }
}

@Composable
private fun AdminStatCard(
    modifier: Modifier,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    delta: String,
    label: String,
    value: String,
    deltaColor: Color,
) {
    StitchGhostCard(modifier = modifier) {
        Column(Modifier.padding(14.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                Icon(icon, null, tint = MaterialTheme.colorScheme.primary)
                Text(delta, style = MaterialTheme.typography.labelSmall, color = deltaColor)
            }
            Spacer(Modifier.height(8.dp))
            Text(label, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineMedium, fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun SuggestionsOverviewCard() {
    StitchGhostCard(modifier = Modifier.fillMaxWidth().height(120.dp)) {
        Box(Modifier.fillMaxSize().padding(14.dp)) {
            Column {
                Text(stringApp(R.string.admin_free_suggestions), style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
                Text("18 places ", style = MaterialTheme.typography.titleLarge, color = MaterialTheme.colorScheme.tertiary, fontWeight = FontWeight.Bold)
                Text(stringApp(R.string.admin_cambodia_hint), style = MaterialTheme.typography.bodySmall)
            }
            Row(
                modifier = Modifier.align(Alignment.BottomCenter).fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(6.dp),
                verticalAlignment = Alignment.Bottom,
            ) {
                listOf(32, 24, 40, 28, 56).forEachIndexed { i, h ->
                    Box(
                        Modifier
                            .weight(1f)
                            .height(h.dp)
                            .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                            .background(
                                if (i == 4) MaterialTheme.colorScheme.primaryContainer
                                else MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.25f),
                            ),
                    )
                }
            }
        }
    }
}

@Composable
private fun SavedPlaceRow(avatar: String, name: String, place: String, rating: String, status: String, statusColor: Color) {
    Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
        AsyncImage(avatar, name, Modifier.size(40.dp).clip(CircleShape), contentScale = ContentScale.Crop)
        Column(Modifier.weight(1f).padding(horizontal = 12.dp)) {
            Text(name, fontWeight = FontWeight.Bold)
            Text(place, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Column(horizontalAlignment = Alignment.End) {
            Text(rating)
            Text(status, color = statusColor, style = MaterialTheme.typography.labelSmall)
        }
    }
    HorizontalDivider(color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f))
}

@Composable
private fun ActivityLine(title: String, detail: String, time: String, dotColor: Color) {
    Row(Modifier.padding(vertical = 6.dp)) {
        Box(Modifier.size(10.dp).clip(CircleShape).background(dotColor).align(Alignment.Top))
        Column(Modifier.padding(start = 12.dp)) {
            Text(title, fontWeight = FontWeight.Medium)
            Text(detail, style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(time, style = MaterialTheme.typography.labelSmall, color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f))
        }
    }
}

@Composable
private fun QuickActionChip(
    label: String,
    modifier: Modifier = Modifier,
    onClick: () -> Unit,
) {
    TextButton(
        onClick = onClick,
        modifier = modifier
            .clip(RoundedCornerShape(999.dp))
            .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.2f)),
    ) {
        Text(label, color = MaterialTheme.colorScheme.primary)
    }
}

