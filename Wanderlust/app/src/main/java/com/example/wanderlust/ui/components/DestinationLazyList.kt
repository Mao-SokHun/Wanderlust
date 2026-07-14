package com.example.wanderlust.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.example.wanderlust.data.DestinationCard

@Composable
fun DestinationLazyList(
    destinations: List<DestinationCard>,
    onDestinationClick: (DestinationCard) -> Unit,
    modifier: Modifier = Modifier,
    contentPadding: PaddingValues = PaddingValues(bottom = 80.dp),
    header: @Composable () -> Unit = {},
) {
    LazyColumn(
        modifier = modifier.fillMaxSize(),
        contentPadding = contentPadding,
        verticalArrangement = Arrangement.spacedBy(10.dp),
    ) {
        item(key = "header") {
            header()
            Spacer(Modifier.height(4.dp))
        }
        items(
            items = destinations,
            key = { it.id },
        ) { dest ->
            DestinationListCard(
                destination = dest,
                onClick = { onDestinationClick(dest) },
            )
        }
    }
}
