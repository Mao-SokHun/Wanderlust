package com.example.wanderlust

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.data.model.AdminTourRequest
import com.example.wanderlust.data.model.Tour
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.viewmodel.AdminToolsViewModel

@Composable
fun EditTourScreen(
    onBack: () -> Unit,
    viewModel: AdminToolsViewModel = viewModel(),
) {
    val state = viewModel.uiState
    var selected by remember { mutableStateOf<Tour?>(null) }
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("") }
    var ratingText by remember { mutableStateOf("4.8") }

    LaunchedEffect(Unit) { viewModel.loadTours() }

    StickyScrollScreen(
        title = stringApp(R.string.admin_edit_tour_title),
        onBack = onBack,
    ) {
        if (state.tours.isEmpty()) {
            Text(stringApp(R.string.admin_tour_none), color = MaterialTheme.colorScheme.onSurfaceVariant)
        } else {
            Text(stringApp(R.string.admin_tour_select), fontWeight = FontWeight.SemiBold)
            Spacer(Modifier.height(8.dp))
            state.tours.take(5).forEach { tour ->
                StitchGhostCard(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = 8.dp)
                        .clickable {
                            selected = tour
                            title = tour.title
                            description = tour.description
                            category = tour.category
                            ratingText = tour.rating.toString()
                        },
                ) {
                    Column(Modifier.padding(12.dp)) {
                        Text(tour.title, fontWeight = FontWeight.SemiBold)
                        Text("${tour.category} • ★ ${tour.rating}", color = MaterialTheme.colorScheme.onSurfaceVariant)
                    }
                }
            }
        }

        selected?.let { tour ->
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(title, { title = it }, label = { Text(stringApp(R.string.admin_tour_title)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(description, { description = it }, label = { Text(stringApp(R.string.admin_tour_description)) }, modifier = Modifier.fillMaxWidth())
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(category, { category = it }, label = { Text(stringApp(R.string.admin_tour_category)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(8.dp))
            OutlinedTextField(ratingText, { ratingText = it }, label = { Text(stringApp(R.string.admin_tour_rating)) }, modifier = Modifier.fillMaxWidth(), singleLine = true)
            Spacer(Modifier.height(12.dp))
            Button(
                onClick = {
                    val rating = ratingText.toDoubleOrNull() ?: tour.rating
                    viewModel.updateTour(
                        id = tour.id,
                        request = AdminTourRequest(
                            title = title,
                            description = description,
                            category = category,
                            rating = rating.coerceIn(0.0, 5.0),
                        ),
                    )
                },
                modifier = Modifier.fillMaxWidth(),
            ) {
                Text(stringApp(R.string.admin_tour_update))
            }
        }

        state.message?.let {
            Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
        }
        state.errorMessage?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
