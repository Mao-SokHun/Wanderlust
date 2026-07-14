package com.example.wanderlust

import androidx.annotation.StringRes
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.Policy
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.wanderlust.ui.components.StitchGhostCard
import com.example.wanderlust.ui.components.StickyScrollScreen

enum class LegalDocumentType {
    PrivacyPolicy,
    TermsOfService,
}

private data class LegalSection(
    @StringRes val titleRes: Int,
    @StringRes val bodyRes: Int,
)

@Composable
fun LegalDocumentScreen(
    type: LegalDocumentType,
    onBack: () -> Unit,
) {
    val titleRes = when (type) {
        LegalDocumentType.PrivacyPolicy -> R.string.privacy_policy_title
        LegalDocumentType.TermsOfService -> R.string.terms_title
    }
    val introRes = when (type) {
        LegalDocumentType.PrivacyPolicy -> R.string.legal_intro_privacy
        LegalDocumentType.TermsOfService -> R.string.legal_intro_terms
    }
    val highlightsRes = when (type) {
        LegalDocumentType.PrivacyPolicy -> listOf(
            R.string.legal_highlight_no_sell,
            R.string.legal_highlight_account_data,
            R.string.legal_highlight_location_maps,
        )
        LegalDocumentType.TermsOfService -> listOf(
            R.string.legal_highlight_cambodia_only,
            R.string.legal_highlight_no_booking,
            R.string.legal_highlight_sign_in_full,
        )
    }
    val sections = when (type) {
        LegalDocumentType.PrivacyPolicy -> listOf(
            LegalSection(R.string.legal_section_overview, R.string.legal_privacy_overview),
            LegalSection(R.string.legal_section_data_collect, R.string.legal_privacy_data_collect),
            LegalSection(R.string.legal_section_data_use, R.string.legal_privacy_data_use),
            LegalSection(R.string.legal_section_storage, R.string.legal_privacy_storage),
            LegalSection(R.string.legal_section_rights, R.string.legal_privacy_rights),
        )
        LegalDocumentType.TermsOfService -> listOf(
            LegalSection(R.string.legal_section_overview, R.string.legal_terms_overview),
            LegalSection(R.string.legal_section_terms_service, R.string.legal_terms_service),
            LegalSection(R.string.legal_section_terms_account, R.string.legal_terms_account),
            LegalSection(R.string.legal_section_terms_use, R.string.legal_terms_use),
            LegalSection(R.string.legal_section_terms_changes, R.string.legal_terms_changes),
        )
    }
    val headerIcon = when (type) {
        LegalDocumentType.PrivacyPolicy -> Icons.Default.Policy
        LegalDocumentType.TermsOfService -> Icons.Default.Description
    }

    StickyScrollScreen(
        title = stringApp(titleRes),
        onBack = onBack,
    ) {
        StitchGhostCard(Modifier.fillMaxWidth()) {
            Column(
                Modifier
                    .fillMaxWidth()
                    .padding(18.dp),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Icon(
                    headerIcon,
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.height(44.dp),
                )
                Spacer(Modifier.height(10.dp))
                Text(
                    stringApp(titleRes),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(6.dp))
                Text(
                    stringApp(R.string.legal_last_updated),
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
                Spacer(Modifier.height(12.dp))
                Text(
                    stringApp(introRes),
                    style = MaterialTheme.typography.bodyLarge,
                    color = MaterialTheme.colorScheme.onSurface,
                    lineHeight = 24.sp,
                    modifier = Modifier.fillMaxWidth(),
                )
            }
        }

        Spacer(Modifier.height(12.dp))

        StitchGhostCard(Modifier.fillMaxWidth()) {
            Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
                Text(
                    stringApp(R.string.legal_section_summary),
                    style = MaterialTheme.typography.titleSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.primary,
                )
                Spacer(Modifier.height(10.dp))
                highlightsRes.forEach { res ->
                    LegalHighlightRow(stringApp(res))
                    Spacer(Modifier.height(8.dp))
                }
            }
        }

        sections.forEachIndexed { index, section ->
            Spacer(Modifier.height(12.dp))
            StitchGhostCard(Modifier.fillMaxWidth()) {
                LegalSectionContent(
                    index = index + 1,
                    title = stringApp(section.titleRes),
                    body = stringApp(section.bodyRes),
                )
            }
        }

        Spacer(Modifier.height(16.dp))
        StitchGhostCard(Modifier.fillMaxWidth()) {
            Text(
                stringApp(R.string.legal_footer_note),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                lineHeight = 22.sp,
                modifier = Modifier.padding(16.dp),
            )
        }
    }
}

@Composable
private fun LegalHighlightRow(text: String) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.Top,
    ) {
        Box(
            modifier = Modifier
                .padding(top = 2.dp)
                .size(22.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                Icons.Default.Check,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onPrimaryContainer,
                modifier = Modifier.size(14.dp),
            )
        }
        Text(
            text,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurface,
            lineHeight = 22.sp,
            modifier = Modifier
                .weight(1f)
                .padding(start = 10.dp),
        )
    }
}

@Composable
private fun LegalSectionContent(
    index: Int,
    title: String,
    body: String,
) {
    Column(Modifier.padding(horizontal = 16.dp, vertical = 14.dp)) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(28.dp)
                    .clip(CircleShape)
                    .background(MaterialTheme.colorScheme.primary),
                contentAlignment = Alignment.Center,
            ) {
                Text(
                    index.toString(),
                    style = MaterialTheme.typography.labelLarge,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onPrimary,
                )
            }
            Text(
                title,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(start = 12.dp),
            )
        }
        Spacer(Modifier.height(10.dp))
        Text(
            body,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 22.sp,
        )
    }
}
