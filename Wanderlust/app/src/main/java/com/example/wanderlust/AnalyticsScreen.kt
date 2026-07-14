package com.example.wanderlust

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.viewmodel.AdminToolsViewModel

@Composable
fun AnalyticsScreen(
    onBack: () -> Unit,
    viewModel: AdminToolsViewModel = viewModel(),
) {
    val state = viewModel.uiState
    LaunchedEffect(Unit) { viewModel.loadAnalytics() }
    val analytics = state.analytics

    StickyScrollScreen(
        title = stringApp(R.string.analytics_title),
        onBack = onBack,
    ) {
        when {
            state.errorMessage != null -> {
                Text(state.errorMessage!!, color = MaterialTheme.colorScheme.error)
            }
            analytics == null -> {
                Text(stringApp(R.string.analytics_loading), color = MaterialTheme.colorScheme.onSurfaceVariant)
            }
            else -> {
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(stringApp(R.string.analytics_metric_tours), analytics.tours.toString(), Modifier.weight(1f))
                    MetricCard(stringApp(R.string.analytics_metric_users), analytics.users.toString(), Modifier.weight(1f))
                }
                Spacer(Modifier.height(10.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    MetricCard(stringApp(R.string.analytics_metric_avg_rating), analytics.averageRating.toString(), Modifier.weight(1f))
                    MetricCard(stringApp(R.string.analytics_metric_top_category), analytics.topCategory, Modifier.weight(1f))
                }

                Spacer(Modifier.height(16.dp))
                Text(stringApp(R.string.analytics_quick_insight), fontWeight = FontWeight.Bold, style = MaterialTheme.typography.titleMedium)
                StitchGhostCard(modifier = Modifier.fillMaxWidth().padding(top = 8.dp)) {
                    Column(Modifier.padding(12.dp)) {
                        Text(
                            stringApp(R.string.analytics_insight_body, analytics.topCategory),
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(10.dp))
                        Row(
                            Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(6.dp),
                            verticalAlignment = Alignment.Bottom,
                        ) {
                            listOf(20, 32, 28, 40, 36).forEach { value ->
                                Spacer(
                                    Modifier
                                        .weight(1f)
                                        .height(value.dp)
                                        .clip(RoundedCornerShape(topStart = 4.dp, topEnd = 4.dp))
                                        .background(MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.4f)),
                                )
                            }
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    StitchGhostCard(modifier = modifier) {
        Column(Modifier.padding(12.dp)) {
            Text(title, style = MaterialTheme.typography.labelMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text(value, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold)
        }
    }
}
