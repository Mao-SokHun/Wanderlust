package com.example.wanderlust

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wanderlust.data.TravelTip
import com.example.wanderlust.data.TravelTipsCatalog
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.CategoryChipRow
import com.example.wanderlust.ui.components.ScreenHeader
import com.example.wanderlust.ui.components.WanderlustBrand

/**
 * Travel tips for Cambodia — advice only.
 * Not a places catalog (removed) and not app Help Center.
 */
@Composable
fun TipsScreen() {
    val focusManager = LocalFocusManager.current
    var query by remember { mutableStateOf("") }
    var category by remember { mutableStateOf<String?>(null) }
    val tips = remember(query, category) {
        TravelTipsCatalog.filter(category, query)
    }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "header") {
            Column {
                Spacer(Modifier.height(8.dp))
                WanderlustBrand()
                Spacer(Modifier.height(14.dp))
                ScreenHeader(
                    title = stringLocalized(R.string.tips_title, R.string.tips_title_kh),
                    subtitle = stringLocalized(R.string.tips_subtitle, R.string.tips_subtitle_kh),
                    showBrand = false,
                )
                Spacer(Modifier.height(12.dp))
                CompactSearchField(
                    value = query,
                    onValueChange = { query = it },
                    placeholder = stringLocalized(R.string.tips_search_hint, R.string.tips_search_hint_kh),
                    onClear = {
                        query = ""
                        focusManager.clearFocus()
                    },
                    onSearch = { focusManager.clearFocus() },
                )
                Spacer(Modifier.height(10.dp))
                CategoryChipRow(
                    categories = TravelTipsCatalog.categories,
                    selected = category,
                    onSelect = { category = it },
                )
                Spacer(Modifier.height(8.dp))
                Text(
                    stringApp(R.string.tips_count, tips.size),
                    style = MaterialTheme.typography.labelMedium.copy(
                        fontFamily = FontFamily.SansSerif,
                        fontWeight = FontWeight.Medium,
                    ),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                if (tips.isEmpty()) {
                    Text(
                        stringLocalized(R.string.tips_empty, R.string.tips_empty_kh),
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(vertical = 12.dp),
                    )
                }
            }
        }

        items(tips, key = { it.id }) { tip ->
            TipCard(tip)
        }
    }
}

@Composable
private fun TipCard(tip: TravelTip) {
    Surface(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.45f),
        tonalElevation = 0.dp,
        shadowElevation = 0.dp,
    ) {
        Column(Modifier.padding(14.dp)) {
            Text(
                tip.category.uppercase(),
                style = MaterialTheme.typography.labelSmall.copy(
                    fontWeight = FontWeight.SemiBold,
                    letterSpacing = 0.6.sp,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                tip.title,
                style = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.SemiBold),
                color = MaterialTheme.colorScheme.onSurface,
            )
            Spacer(Modifier.height(6.dp))
            Text(
                tip.body,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}
