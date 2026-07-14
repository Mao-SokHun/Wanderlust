package com.example.wanderlust

import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.wanderlust.data.model.AdminTourRequest
import com.example.wanderlust.locale.stringApp
import com.example.wanderlust.ui.components.StickyScrollScreen
import com.example.wanderlust.util.Validation
import com.example.wanderlust.viewmodel.AdminToolsViewModel

@Composable
fun AddTourScreen(
    onBack: () -> Unit,
    viewModel: AdminToolsViewModel = viewModel(),
) {
    val state = viewModel.uiState
    var title by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("Beach") }
    var ratingText by remember { mutableStateOf("4.8") }
    var localError by remember { mutableStateOf<String?>(null) }

    fun formError(): String? {
        return Validation.requireTourTitle(title)
            ?: Validation.requireTourDescription(description)
            ?: run {
                if (category.trim().isEmpty()) {
                    if (com.example.wanderlust.locale.AppLocale.isKhmer) "ត្រូវការប្រភេទ" else "Category is required"
                } else null
            }
            ?: run {
                val rating = ratingText.toDoubleOrNull()
                when {
                    rating == null -> if (com.example.wanderlust.locale.AppLocale.isKhmer) {
                        "ការវាយតម្លៃមិនត្រឹមត្រូវ"
                    } else {
                        "Rating must be a number"
                    }
                    rating !in 0.0..5.0 -> if (com.example.wanderlust.locale.AppLocale.isKhmer) {
                        "ការវាយតម្លៃត្រូវ ០–៥"
                    } else {
                        "Rating must be between 0 and 5"
                    }
                    else -> null
                }
            }
    }

    StickyScrollScreen(
        title = stringApp(R.string.admin_add_tour_title),
        onBack = onBack,
    ) {
        OutlinedTextField(
            title,
            { title = it.take(Validation.TITLE_MAX); localError = null },
            label = { Text(stringApp(R.string.admin_tour_title)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            description,
            { description = it.take(Validation.DESCRIPTION_MAX); localError = null },
            label = { Text(stringApp(R.string.admin_tour_description)) },
            modifier = Modifier.fillMaxWidth(),
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            category,
            { category = it.take(50); localError = null },
            label = { Text(stringApp(R.string.admin_tour_category)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )
        Spacer(Modifier.height(8.dp))
        OutlinedTextField(
            ratingText,
            { ratingText = it.filter { ch -> ch.isDigit() || ch == '.' }; localError = null },
            label = { Text(stringApp(R.string.admin_tour_rating)) },
            modifier = Modifier.fillMaxWidth(),
            singleLine = true,
        )

        Spacer(Modifier.height(12.dp))
        Button(
            onClick = {
                val err = formError()
                if (err != null) {
                    localError = err
                    return@Button
                }
                val rating = ratingText.toDoubleOrNull() ?: 4.5
                viewModel.addTour(
                    AdminTourRequest(
                        title = title.trim(),
                        description = description.trim(),
                        category = category.trim(),
                        rating = rating.coerceIn(0.0, 5.0),
                    ),
                )
            },
            modifier = Modifier.fillMaxWidth(),
            enabled = !state.isLoading && formError() == null,
        ) {
            Text(stringApp(R.string.admin_tour_save))
        }

        (localError ?: state.errorMessage)?.let {
            Text(it, color = MaterialTheme.colorScheme.error, modifier = Modifier.padding(top = 8.dp))
        }
        state.message?.let {
            Text(it, color = MaterialTheme.colorScheme.primary, modifier = Modifier.padding(top = 8.dp))
        }
    }
}
