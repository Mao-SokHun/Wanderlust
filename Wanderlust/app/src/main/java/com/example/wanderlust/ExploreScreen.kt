package com.example.wanderlust

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.DestinationCatalog
import com.example.wanderlust.data.GuestAccess
import com.example.wanderlust.data.toDestinationCard
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.CategoryChipRow
import com.example.wanderlust.ui.components.GuestLoginBanner
import com.example.wanderlust.ui.components.ScreenHeader
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.viewmodel.ExploreViewModel

@Composable
fun ExploreScreen(
    initialQuery: String = "",
    initialCategory: String? = null,
    onTourClick: (DestinationCard) -> Unit,
    onSignIn: () -> Unit = {},
    viewModel: ExploreViewModel,
) {
    val state = viewModel.uiState
    var localQuery by remember(initialQuery) { mutableStateOf(initialQuery.ifBlank { state.searchQuery }) }

    LaunchedEffect(initialQuery) {
        if (initialQuery.isNotBlank() && state.searchQuery != initialQuery) {
            viewModel.updateSearch(initialQuery)
            viewModel.loadTours(showSpinner = false)
        }
    }

    LaunchedEffect(initialCategory) {
        if (initialCategory != null && state.selectedCategory != initialCategory) {
            viewModel.selectCategory(initialCategory)
        }
    }

    val listItems = remember(state.tours, state.isOfflineData, localQuery, state.selectedCategory) {
        val raw = if (state.tours.isNotEmpty()) {
            state.tours.map { it.toDestinationCard() }
        } else if (!state.isLoading) {
            DestinationCatalog.filter(state.selectedCategory, localQuery)
        } else {
            emptyList()
        }
        GuestAccess.limitForGuest(raw)
    }

    Column(Modifier.fillMaxSize()) {
        com.example.wanderlust.ui.components.DestinationLazyList(
            destinations = listItems,
            onDestinationClick = onTourClick,
            modifier = Modifier.weight(1f),
            header = {
                Column(Modifier.padding(horizontal = 16.dp)) {
                    Spacer(Modifier.height(4.dp))
                    ScreenHeader(
                        title = stringLocalized(R.string.explore_title, R.string.explore_title_kh),
                        subtitle = stringLocalized(R.string.explore_subtitle, R.string.explore_subtitle_kh),
                    )
                    Spacer(Modifier.height(12.dp))
                    OutlinedTextField(
                        value = localQuery,
                        onValueChange = {
                            localQuery = it
                            viewModel.updateSearch(it)
                        },
                        modifier = Modifier.fillMaxWidth(),
                        placeholder = { Text(stringLocalized(R.string.home_where_go, R.string.home_where_go_kh)) },
                        leadingIcon = { Icon(Icons.Default.Search, contentDescription = null) },
                        singleLine = true,
                    )
                    TextButton(
                        onClick = {
                            viewModel.updateSearch(localQuery)
                            viewModel.search()
                        },
                        modifier = Modifier.align(Alignment.End),
                    ) {
                        Text(stringResource(R.string.btn_search))
                    }
                    CategoryChipRow(
                        categories = DestinationCatalog.categories,
                        selected = state.selectedCategory,
                        onSelect = viewModel::selectCategory,
                    )
                    if (state.isLoading) {
                        CircularProgressIndicator(Modifier.padding(vertical = 8.dp))
                    }
                    if (state.errorMessage != null) {
                        StitchGhostCard(Modifier.fillMaxWidth()) {
                            Column(Modifier.padding(12.dp)) {
                                Text(state.errorMessage, color = MaterialTheme.colorScheme.error)
                                TextButton(onClick = { viewModel.loadTours() }) {
                                    Text(stringResource(R.string.btn_retry))
                                }
                            }
                        }
                        Spacer(Modifier.height(8.dp))
                    }
                    if (state.isOfflineData && state.tours.isNotEmpty()) {
                        Text(
                            stringLocalized(R.string.explore_sample_tours, R.string.explore_sample_tours),
                            style = MaterialTheme.typography.labelMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                        Spacer(Modifier.height(8.dp))
                    }
                    GuestLoginBanner(onSignIn = onSignIn, modifier = Modifier.fillMaxWidth())
                    Spacer(Modifier.height(8.dp))
                }
            },
        )
    }
}
