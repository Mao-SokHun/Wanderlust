package com.example.wanderlust.ui.screens.saved

import com.example.wanderlust.R

import android.content.Intent
import android.net.Uri
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.viewmodel.AddSavedPlaceViewModel

@Composable
fun AddSavedPlaceScreen(
    onBack: () -> Unit,
    onSaved: (com.example.wanderlust.data.DestinationCard) -> Unit,
    viewModel: AddSavedPlaceViewModel = viewModel(),
) {
    val state = viewModel.uiState
    val context = LocalContext.current

    LaunchedEffect(state.savedDestination) {
        state.savedDestination?.let { dest ->
            viewModel.clearSaved()
            onSaved(dest)
        }
    }

    StickyScrollScreen(
        title = stringApp(R.string.add_saved_place_title),
        onBack = onBack,
        bottomPadding = 24.dp,
    ) {
        Text(
            stringApp(R.string.add_saved_place_hint),
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        Spacer(Modifier.height(12.dp))

        OutlinedTextField(
            value = state.title,
            onValueChange = viewModel::onTitleChange,
            label = { Text(stringApp(R.string.label_place_name)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.location,
            onValueChange = viewModel::onLocationChange,
            label = { Text(stringApp(R.string.label_place_address)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedButton(
            onClick = {
                val q = state.location.ifBlank { state.title }.trim()
                if (q.isNotEmpty()) {
                    val uri = Uri.parse(
                        "https://www.google.com/maps/search/?api=1&query=${Uri.encode(q)}",
                    )
                    context.startActivity(Intent(Intent.ACTION_VIEW, uri))
                }
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = state.title.isNotBlank() || state.location.isNotBlank(),
        ) {
            Text(stringApp(R.string.btn_open_google_maps))
        }
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.mapsLink,
            onValueChange = viewModel::onMapsLinkChange,
            label = { Text(stringApp(R.string.label_maps_link)) },
            modifier = Modifier.fillMaxWidth(),
            placeholder = { Text(stringApp(R.string.hint_maps_link)) },
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.latitude,
            onValueChange = viewModel::onLatitudeChange,
            label = { Text(stringApp(R.string.label_latitude)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.longitude,
            onValueChange = viewModel::onLongitudeChange,
            label = { Text(stringApp(R.string.label_longitude)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            value = state.notes,
            onValueChange = viewModel::onNotesChange,
            label = { Text(stringApp(R.string.label_place_notes)) },
            modifier = Modifier.fillMaxWidth(),
            minLines = 2,
        )

        state.errorMessage?.let {
            Spacer(Modifier.height(8.dp))
            Text(it, color = MaterialTheme.colorScheme.error, style = MaterialTheme.typography.bodySmall)
        }

        Spacer(Modifier.height(16.dp))
        Button(
            onClick = viewModel::save,
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading,
        ) {
            if (state.isLoading) {
                CircularProgressIndicator(modifier = Modifier.height(24.dp))
            } else {
                Text(stringApp(R.string.btn_save_to_my_list))
            }
        }
    }
}
