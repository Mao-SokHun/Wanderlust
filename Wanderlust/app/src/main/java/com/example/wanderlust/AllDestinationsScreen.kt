package com.example.wanderlust

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.DestinationCatalog
import com.example.wanderlust.data.GuestAccess
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.BackTopBar
import com.example.wanderlust.ui.components.CategoryChipRow
import com.example.wanderlust.ui.components.DestinationLazyList
import com.example.wanderlust.ui.components.GuestLoginBanner

@Composable
fun AllDestinationsScreen(
    initialCategory: String? = null,
    onBack: () -> Unit,
    onDestinationClick: (DestinationCard) -> Unit,
    onSignIn: () -> Unit = {},
) {
    var category by remember(initialCategory) { mutableStateOf(initialCategory) }
    val list = remember(category) {
        GuestAccess.limitForGuest(DestinationCatalog.filter(category = category))
    }

    Surface(
        modifier = Modifier.fillMaxSize(),
        color = MaterialTheme.colorScheme.background,
    ) {
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp, bottom = 8.dp),
            ) {
                BackTopBar(
                    stringLocalized(R.string.all_destinations_title, R.string.all_destinations_title_kh),
                    onBack,
                )
                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                )
                Spacer(Modifier.height(8.dp))
                CategoryChipRow(
                    categories = DestinationCatalog.categories,
                    selected = category,
                    onSelect = { category = it },
                )
                Spacer(Modifier.height(10.dp))
                GuestLoginBanner(onSignIn = onSignIn, modifier = Modifier.fillMaxWidth())
            }

            DestinationLazyList(
                destinations = list,
                onDestinationClick = onDestinationClick,
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
                contentPadding = androidx.compose.foundation.layout.PaddingValues(
                    start = 16.dp,
                    end = 16.dp,
                    bottom = 24.dp,
                ),
            )
        }
    }
}
