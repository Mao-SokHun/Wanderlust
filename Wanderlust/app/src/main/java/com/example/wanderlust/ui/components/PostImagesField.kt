package com.example.wanderlust.ui.components

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AddPhotoAlternate
import androidx.compose.material.icons.filled.Close
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.wanderlust.R
import com.example.wanderlust.data.repository.MediaUploadRepository
import com.example.wanderlust.locale.stringApp
import kotlinx.coroutines.launch

const val MAX_LISTING_IMAGES = 40

/**
 * Multi-image gallery for business post forms.
 * Picks from gallery → uploads to Cloudinary via API → stores HTTPS URLs (max 40).
 */
@Composable
fun PostImagesField(
    imageUrls: List<String>,
    onImageUrlsChange: (List<String>) -> Unit,
    modifier: Modifier = Modifier,
    maxImages: Int = MAX_LISTING_IMAGES,
) {
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    val repo = remember { MediaUploadRepository() }
    var uploading by remember { mutableStateOf(false) }
    var error by remember { mutableStateOf<String?>(null) }

    val remaining = (maxImages - imageUrls.size).coerceAtLeast(0)
    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia(maxItems = 10),
    ) { uris ->
        if (uris.isEmpty()) return@rememberLauncherForActivityResult
        val take = uris.take(remaining)
        if (take.isEmpty()) return@rememberLauncherForActivityResult
        scope.launch {
            uploading = true
            error = null
            repo.uploadImages(context, take)
                .onSuccess { urls ->
                    onImageUrlsChange((imageUrls + urls).distinct().take(maxImages))
                }
                .onFailure { error = it.message }
            uploading = false
        }
    }

    Column(modifier.fillMaxWidth(), verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text(
            stringApp(R.string.pkg_images_title),
            style = MaterialTheme.typography.titleSmall,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            stringApp(R.string.pkg_images_hint, imageUrls.size, maxImages),
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )

        if (imageUrls.isNotEmpty()) {
            Row(
                Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState()),
                horizontalArrangement = Arrangement.spacedBy(8.dp),
            ) {
                imageUrls.forEachIndexed { index, url ->
                    Box(
                        Modifier
                            .size(96.dp)
                            .clip(RoundedCornerShape(12.dp))
                            .background(MaterialTheme.colorScheme.surfaceVariant),
                    ) {
                        AsyncImage(
                            model = url,
                            contentDescription = null,
                            contentScale = ContentScale.Crop,
                            modifier = Modifier.fillMaxSize(),
                        )
                        IconButton(
                            onClick = {
                                onImageUrlsChange(imageUrls.filterIndexed { i, _ -> i != index })
                            },
                            modifier = Modifier
                                .align(Alignment.TopEnd)
                                .padding(2.dp)
                                .size(28.dp)
                                .background(
                                    MaterialTheme.colorScheme.scrim.copy(alpha = 0.55f),
                                    CircleShape,
                                ),
                        ) {
                            Icon(
                                Icons.Default.Close,
                                contentDescription = stringApp(R.string.pkg_images_remove),
                                tint = MaterialTheme.colorScheme.onPrimary,
                                modifier = Modifier.size(16.dp),
                            )
                        }
                    }
                }
            }
        }

        OutlinedButton(
            onClick = {
                if (remaining <= 0 || uploading) return@OutlinedButton
                picker.launch(
                    PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly),
                )
            },
            enabled = remaining > 0 && !uploading,
            modifier = Modifier.fillMaxWidth().height(48.dp),
        ) {
            if (uploading) {
                CircularProgressIndicator(
                    modifier = Modifier
                        .size(20.dp)
                        .padding(end = 8.dp),
                    strokeWidth = 2.dp,
                )
                Text(stringApp(R.string.pkg_images_uploading))
            } else {
                Icon(
                    Icons.Default.AddPhotoAlternate,
                    contentDescription = null,
                    modifier = Modifier.padding(end = 8.dp),
                )
                Text(
                    if (remaining <= 0) {
                        stringApp(R.string.pkg_images_full)
                    } else {
                        stringApp(R.string.pkg_images_add)
                    },
                )
            }
        }

        error?.let {
            Text(
                it,
                color = MaterialTheme.colorScheme.error,
                style = MaterialTheme.typography.bodySmall,
            )
        }
    }
}

