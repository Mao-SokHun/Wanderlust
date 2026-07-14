package com.example.wanderlust.locale

import androidx.annotation.StringRes
import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.data.DestinationCard

/** English-only UI strings (kmId kept for call-site compatibility). */
@Composable
fun stringLocalized(@StringRes enId: Int, @StringRes kmId: Int): String = stringResource(enId)

@Composable
fun DestinationCard.localizedTitle(): String = title

@Composable
fun DestinationCard.localizedLocation(): String = location

@Composable
fun DestinationCard.localizedDescription(): String = description

@Composable
fun DestinationCard.localizedCategory(): String = category
