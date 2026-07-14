package com.example.wanderlust

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.GuestLoginBanner
import com.example.wanderlust.ui.components.NearbyPlacesExplorer
import com.example.wanderlust.ui.components.ScreenHeader
import com.example.wanderlust.viewmodel.NearbyPlacesViewModel

@Suppress("UNUSED_PARAMETER")
@Composable
fun HomeScreen(
    onDestinationClick: (DestinationCard) -> Unit,
    onExploreAll: () -> Unit,
    onSearch: (String) -> Unit,
    onCategoryExplore: (String?) -> Unit = {},
    onSignIn: () -> Unit = {},
    onPlaceSaved: () -> Unit = {},
) {
    val nearbyVm: NearbyPlacesViewModel = viewModel()
    val focusManager = LocalFocusManager.current
    val query = nearbyVm.uiState.searchQuery
    val searchShape = RoundedCornerShape(28.dp)

    Column(
        Modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(horizontal = 16.dp),
    ) {
        Spacer(Modifier.height(4.dp))
        ScreenHeader(
            title = stringLocalized(R.string.home_headline, R.string.home_headline_kh),
            subtitle = stringLocalized(R.string.home_subtitle, R.string.home_subtitle_kh),
        )
        Spacer(Modifier.height(12.dp))
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = searchShape,
            color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.65f),
            shadowElevation = 1.dp,
        ) {
            OutlinedTextField(
                value = query,
                onValueChange = nearbyVm::onSearchQueryChange,
                modifier = Modifier.fillMaxWidth(),
                placeholder = {
                    Text(
                        stringLocalized(R.string.home_where_go, R.string.home_where_go_kh),
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f),
                    )
                },
                leadingIcon = {
                    Icon(
                        Icons.Default.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.primary,
                    )
                },
                trailingIcon = {
                    if (query.isNotBlank()) {
                        IconButton(
                            onClick = {
                                nearbyVm.clearSearch()
                                focusManager.clearFocus()
                            },
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringResource(R.string.nearby_search_clear),
                                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            )
                        }
                    }
                },
                singleLine = true,
                shape = searchShape,
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(
                    onSearch = {
                        focusManager.clearFocus()
                        nearbyVm.submitSearch()
                    },
                ),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.55f),
                    unfocusedContainerColor = Color.Transparent,
                    disabledContainerColor = Color.Transparent,
                    focusedBorderColor = MaterialTheme.colorScheme.primary.copy(alpha = 0.45f),
                    unfocusedBorderColor = Color.Transparent,
                    cursorColor = MaterialTheme.colorScheme.primary,
                ),
            )
        }
        Spacer(Modifier.height(10.dp))
        NearbyPlacesExplorer(
            viewModel = nearbyVm,
            onSignIn = onSignIn,
            onPlaceSaved = onPlaceSaved,
        )
        Spacer(Modifier.height(12.dp))
        GuestLoginBanner(onSignIn = onSignIn, modifier = Modifier.fillMaxWidth())
        Spacer(Modifier.height(96.dp))
    }
}
