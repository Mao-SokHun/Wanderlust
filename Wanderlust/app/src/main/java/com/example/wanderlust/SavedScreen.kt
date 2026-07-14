package com.example.wanderlust

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.DestinationCard
import com.example.wanderlust.data.SessionManager
import com.example.wanderlust.locale.stringLocalized
import com.example.wanderlust.ui.components.DestinationLazyList
import com.example.wanderlust.ui.components.ScreenHeader
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.viewmodel.FavoritesViewModel

@Composable
fun SavedScreen(
    onDestinationClick: (DestinationCard) -> Unit,
    refreshKey: Int = 0,
    onSignIn: () -> Unit,
    onRegister: () -> Unit,
    onAddPlace: () -> Unit = {},
    viewModel: FavoritesViewModel,
) {
    if (!SessionManager.isLoggedIn()) {
        com.example.wanderlust.ui.components.LoginRequiredPanel(
            onSignIn = onSignIn,
            onRegister = onRegister,
        )
        return
    }

    val state = viewModel.uiState

    LaunchedEffect(refreshKey, SessionManager.userId) {
        viewModel.refreshIfNeeded(refreshKey)
    }

    if (state.isLoading && state.places.isEmpty()) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator()
        }
        return
    }

    val title = stringLocalized(R.string.saved_title, R.string.saved_title_kh)
    val subtitle = stringLocalized(R.string.saved_subtitle, R.string.saved_subtitle_kh)

    Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
        Column(Modifier.fillMaxSize()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.background)
                    .padding(horizontal = 16.dp)
                    .padding(top = 4.dp, bottom = 8.dp),
            ) {
                ScreenHeader(title = title, subtitle = subtitle, showBrand = true)
                Button(
                    onClick = onAddPlace,
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 10.dp),
                ) {
                    Text(stringApp(R.string.btn_add_your_place))
                }
                HorizontalDivider(
                    modifier = Modifier.padding(top = 8.dp),
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.25f),
                )
            }

            if (state.places.isEmpty()) {
                Column(
                    Modifier
                        .weight(1f)
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 12.dp),
                ) {
                    StitchGhostCard(Modifier.fillMaxWidth()) {
                        Column(Modifier.padding(16.dp)) {
                            Text(stringApp(R.string.saved_empty))
                            Spacer(Modifier.height(8.dp))
                            Button(onClick = onAddPlace, modifier = Modifier.fillMaxWidth()) {
                                Text(stringApp(R.string.btn_add_your_place))
                            }
                            TextButton(onClick = viewModel::refresh) {
                                Text(stringApp(R.string.btn_retry))
                            }
                        }
                    }
                }
            } else {
                DestinationLazyList(
                    destinations = state.places,
                    onDestinationClick = onDestinationClick,
                    modifier = Modifier
                        .weight(1f)
                        .fillMaxWidth(),
                    contentPadding = androidx.compose.foundation.layout.PaddingValues(
                        start = 16.dp,
                        end = 16.dp,
                        bottom = 108.dp,
                    ),
                    header = {},
                )
            }
        }
    }
}
