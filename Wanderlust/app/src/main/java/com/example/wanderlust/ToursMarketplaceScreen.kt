package com.example.wanderlust

import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.data.repository.TourRepositoryProvider
import com.example.wanderlust.data.toDestinationCard
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.DestinationListCard
import com.example.wanderlust.ui.components.GuestLoginBanner
import com.example.wanderlust.ui.components.ScreenHeader
import com.example.wanderlust.ui.components.WanderlustBrand
import kotlinx.coroutines.launch

@Composable
fun ToursMarketplaceScreen(
    onTourClick: (DestinationCard) -> Unit,
    onOpenBusinessStudio: () -> Unit,
    onSignIn: () -> Unit = {},
) {
    val focusManager = LocalFocusManager.current
    val scope = rememberCoroutineScope()
    var query by remember { mutableStateOf("") }
    var listingType by remember { mutableStateOf<String?>(null) }
    var minRating by remember { mutableStateOf<Double?>(null) }
    var maxPrice by remember { mutableStateOf<Double?>(null) }
    var sort by remember { mutableStateOf("newest") }
    var radiusKm by remember { mutableStateOf<Double?>(null) }
    var tours by remember { mutableStateOf<List<DestinationCard>>(emptyList()) }
    var topTours by remember { mutableStateOf<List<DestinationCard>>(emptyList()) }
    var loading by remember { mutableStateOf(true) }
    var error by remember { mutableStateOf<String?>(null) }
    val isBusiness = SessionManager.userRole == "BUSINESS" || SessionManager.isAdmin()
    // Phnom Penh center as default near-sort / radius anchor
    val anchorLat = 11.5564
    val anchorLng = 104.9282

    fun reload() {
        scope.launch {
            loading = true
            error = null
            val useGeo = sort == "distance" || radiusKm != null
            TourRepositoryProvider.instance.getTours(
                search = query.takeIf { it.isNotBlank() },
                listingType = listingType,
                minRating = minRating,
                maxPrice = maxPrice,
                sort = sort,
                lat = if (useGeo) anchorLat else null,
                lng = if (useGeo) anchorLng else null,
                radiusKm = radiusKm,
            ).onSuccess { list ->
                tours = list.map { it.toDestinationCard() }
                loading = false
            }.onFailure {
                error = it.message
                loading = false
            }
            TourRepositoryProvider.instance.getTours(
                listingType = listingType,
                sort = "rating",
                top = 1,
                limit = 8,
                minRating = 3.5,
            ).onSuccess { list ->
                topTours = list.map { it.toDestinationCard() }
            }
        }
    }

    LaunchedEffect(listingType, minRating, maxPrice, sort, radiusKm) { reload() }

    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        contentPadding = PaddingValues(start = 16.dp, end = 16.dp, bottom = 110.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item {
            Spacer(Modifier.height(8.dp))
            WanderlustBrand()
            Spacer(Modifier.height(14.dp))
            ScreenHeader(
                title = stringLocalized(R.string.tours_market_title, R.string.tours_market_title_kh),
                subtitle = stringLocalized(R.string.tours_market_sub, R.string.tours_market_sub_kh),
                showBrand = false,
            )
            Spacer(Modifier.height(12.dp))
            CompactSearchField(
                value = query,
                onValueChange = { query = it },
                placeholder = stringLocalized(R.string.tours_market_search, R.string.tours_market_search_kh),
                onClear = {
                    query = ""
                    focusManager.clearFocus()
                    reload()
                },
                onSearch = {
                    focusManager.clearFocus()
                    reload()
                },
            )
            Spacer(Modifier.height(8.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = listingType == null,
                    onClick = { listingType = null },
                    label = {
                        Text(stringLocalized(R.string.tours_filter_all, R.string.tours_filter_all_kh))
                    },
                )
                FilterChip(
                    selected = listingType == "TOUR",
                    onClick = { listingType = "TOUR" },
                    label = {
                        Text(stringLocalized(R.string.tours_filter_tours, R.string.tours_filter_tours_kh))
                    },
                )
                FilterChip(
                    selected = listingType == "VEHICLE",
                    onClick = { listingType = "VEHICLE" },
                    label = {
                        Text(
                            stringLocalized(
                                R.string.tours_filter_vehicles,
                                R.string.tours_filter_vehicles_kh,
                            ),
                        )
                    },
                )
                FilterChip(
                    selected = minRating != null,
                    onClick = { minRating = if (minRating == null) 4.0 else null },
                    label = { Text(stringLocalized(R.string.tours_filter_rating, R.string.tours_filter_rating_kh)) },
                )
                FilterChip(
                    selected = maxPrice != null,
                    onClick = { maxPrice = if (maxPrice == null) 50.0 else null },
                    label = {
                        Text(
                            stringLocalized(
                                R.string.tours_price_under_50,
                                R.string.tours_price_under_50_kh,
                            ),
                        )
                    },
                )
                FilterChip(
                    selected = radiusKm != null,
                    onClick = { radiusKm = if (radiusKm == null) 25.0 else null },
                    label = {
                        Text(stringLocalized(R.string.tours_radius_25, R.string.tours_radius_25_kh))
                    },
                )
            }
            Spacer(Modifier.height(4.dp))
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                FilterChip(
                    selected = sort == "newest",
                    onClick = { sort = "newest" },
                    label = {
                        Text(stringLocalized(R.string.tours_sort_newest, R.string.tours_sort_newest_kh))
                    },
                )
                FilterChip(
                    selected = sort == "rating",
                    onClick = { sort = "rating" },
                    label = {
                        Text(stringLocalized(R.string.tours_sort_rating, R.string.tours_sort_rating_kh))
                    },
                )
                FilterChip(
                    selected = sort == "price_asc",
                    onClick = { sort = "price_asc" },
                    label = {
                        Text(
                            stringLocalized(
                                R.string.tours_sort_price_low,
                                R.string.tours_sort_price_low_kh,
                            ),
                        )
                    },
                )
                FilterChip(
                    selected = sort == "price_desc",
                    onClick = { sort = "price_desc" },
                    label = {
                        Text(
                            stringLocalized(
                                R.string.tours_sort_price_high,
                                R.string.tours_sort_price_high_kh,
                            ),
                        )
                    },
                )
                FilterChip(
                    selected = sort == "distance",
                    onClick = { sort = "distance" },
                    label = {
                        Text(stringLocalized(R.string.tours_sort_near, R.string.tours_sort_near_kh))
                    },
                )
            }
            if (isBusiness) {
                Spacer(Modifier.height(10.dp))
                Button(onClick = onOpenBusinessStudio, modifier = Modifier.fillMaxWidth()) {
                    Text(stringLocalized(R.string.business_open_studio, R.string.business_open_studio_kh))
                }
            }
            GuestLoginBanner(onSignIn = onSignIn, modifier = Modifier.fillMaxWidth().padding(top = 8.dp))
            if (topTours.isNotEmpty()) {
                Spacer(Modifier.height(12.dp))
                Text(
                    stringLocalized(R.string.tours_top_title, R.string.tours_top_title_kh),
                    fontWeight = FontWeight.Bold,
                    style = MaterialTheme.typography.titleMedium,
                )
                Spacer(Modifier.height(8.dp))
                LazyRow(horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                    items(topTours, key = { "top-${it.id}" }) { dest ->
                        Column(Modifier.width(260.dp)) {
                            DestinationListCard(destination = dest, onClick = { onTourClick(dest) })
                        }
                    }
                }
                Spacer(Modifier.height(8.dp))
            }
            if (loading) {
                CircularProgressIndicator(modifier = Modifier.padding(16.dp))
            }
            error?.let {
                Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(vertical = 8.dp))
            }
            if (!loading && tours.isEmpty() && error == null) {
                Text(
                    stringLocalized(R.string.tours_market_empty, R.string.tours_market_empty_kh),
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 12.dp),
                )
            }
        }
        items(tours, key = { it.id }) { dest ->
            DestinationListCard(destination = dest, onClick = { onTourClick(dest) })
        }
    }
}
