package com.example.wanderlust.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.locale.localizedCategory
import com.example.wanderlust.locale.localizedLocation
import com.example.wanderlust.locale.localizedTitle

@Composable
fun DestinationTitleBlock(
    destination: DestinationCard,
    titleStyle: androidx.compose.ui.text.TextStyle = MaterialTheme.typography.titleMedium,
    maxTitleLines: Int = 2,
) {
    Text(
        destination.localizedTitle(),
        style = titleStyle,
        fontWeight = FontWeight.SemiBold,
        maxLines = maxTitleLines,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun DestinationLocationLine(destination: DestinationCard) {
    Text(
        destination.localizedLocation(),
        style = MaterialTheme.typography.bodySmall,
        color = MaterialTheme.colorScheme.onSurfaceVariant,
        maxLines = 2,
        overflow = TextOverflow.Ellipsis,
    )
}

@Composable
fun CategoryBadgeText(
    destination: DestinationCard,
    color: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.onPrimaryContainer,
) {
    Text(
        destination.localizedCategory().uppercase(),
        style = MaterialTheme.typography.labelSmall,
        color = color,
    )
}
