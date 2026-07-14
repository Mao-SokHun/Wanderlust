package com.example.wanderlust.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wanderlust.R
import com.example.wanderlust.ui.theme.WanderlustDark
import com.example.wanderlust.ui.theme.WanderlustLight

enum class WanderlustNavTab { Home, Explore, Saved, Profile }

/** Matches the baked-in backdrop of [R.drawable.logo] so light halos do not show. */
private val LogoTileBackground = Color(0xFF121212)

enum class WanderlustLogoStyle {
    /** Paper-plane mark only — headers and nav brand */
    Icon,
    /** Full square mark with wordmark — splash and profile */
    Badge,
}

@Composable
private fun logoTileColor(): Color {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    return if (dark) {
        LogoTileBackground
    } else {
        MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.35f)
    }
}

/** Circular profile avatar — initials when [displayName] is set, else logo badge. */
@Composable
fun ProfileAvatar(
    modifier: Modifier = Modifier,
    size: Dp = 96.dp,
    displayName: String? = null,
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val initials = remember(displayName) {
        displayName
            ?.trim()
            ?.split(Regex("\\s+"))
            ?.filter { it.isNotBlank() }
            ?.take(2)
            ?.mapNotNull { it.firstOrNull()?.uppercaseChar()?.toString() }
            ?.joinToString("")
            ?.takeIf { it.isNotBlank() }
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(logoTileColor())
            .then(
                if (!dark) {
                    Modifier.border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.outlineVariant,
                        shape = CircleShape,
                    )
                } else {
                    Modifier
                },
            ),
        contentAlignment = Alignment.Center,
    ) {
        if (initials != null) {
            Text(
                initials,
                style = MaterialTheme.typography.headlineSmall.copy(
                    fontSize = (size.value * 0.32f).sp,
                    fontWeight = FontWeight.Bold,
                ),
                color = MaterialTheme.colorScheme.primary,
            )
        } else {
            WanderlustLogo(
                size = size * 0.78f,
                style = WanderlustLogoStyle.Badge,
                contentDescription = "Profile photo",
            )
        }
    }
}

@Composable
fun WanderlustLogo(
    modifier: Modifier = Modifier,
    size: Dp = 40.dp,
    style: WanderlustLogoStyle = WanderlustLogoStyle.Icon,
    contentDescription: String = "Wanderlust",
) {
    val corner = when (style) {
        WanderlustLogoStyle.Icon -> size * 0.28f
        WanderlustLogoStyle.Badge -> size * 0.22f
    }
    Box(
        modifier = modifier
            .size(size)
            .clip(RoundedCornerShape(corner))
            .background(logoTileColor()),
        contentAlignment = Alignment.Center,
    ) {
        Image(
            painter = painterResource(R.drawable.logo),
            contentDescription = contentDescription,
            modifier = Modifier
                .fillMaxSize()
                .graphicsLayer {
                    when (style) {
                        WanderlustLogoStyle.Icon -> {
                            scaleX = 2.35f
                            scaleY = 2.35f
                            translationY = size.toPx() * 0.06f
                        }
                        WanderlustLogoStyle.Badge -> {
                            scaleX = 1.05f
                            scaleY = 1.05f
                        }
                    }
                },
            contentScale = if (style == WanderlustLogoStyle.Icon) {
                ContentScale.Crop
            } else {
                ContentScale.Fit
            },
            alignment = if (style == WanderlustLogoStyle.Icon) {
                Alignment.TopCenter
            } else {
                Alignment.Center
            },
        )
    }
}

@Composable
fun ThemeToggleButton(
    isDark: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    IconButton(onClick = onToggle, modifier = modifier) {
        Icon(
            imageVector = if (isDark) Icons.Default.LightMode else Icons.Default.DarkMode,
            contentDescription = if (isDark) "Light mode" else "Dark mode",
            tint = MaterialTheme.colorScheme.primary,
        )
    }
}

@Composable
fun WanderlustBrand(modifier: Modifier = Modifier) {
    Row(modifier = modifier, verticalAlignment = Alignment.CenterVertically) {
        WanderlustLogo(size = 36.dp, style = WanderlustLogoStyle.Icon)
        Spacer(Modifier.width(10.dp))
        Column {
            Text(
                "Wanderlust",
                style = MaterialTheme.typography.titleLarge,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.primary,
            )
            Text(
                stringResource(R.string.brand_tagline),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f),
                letterSpacing = 0.6.sp,
            )
        }
    }
}

@Composable
fun StitchGlassCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val bg = if (dark) WanderlustDark.GlassBg else WanderlustLight.GlassBg
    val border = if (dark) WanderlustDark.GhostBorder else WanderlustLight.GhostBorder
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = bg,
        border = BorderStroke(1.dp, border),
        content = content,
    )
}

@Composable
fun StitchGhostCard(
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val border = if (dark) WanderlustDark.GhostBorder else WanderlustLight.GhostBorder
    Surface(
        modifier = modifier,
        shape = RoundedCornerShape(12.dp),
        color = MaterialTheme.colorScheme.surfaceVariant,
        border = BorderStroke(1.dp, border),
        content = content,
    )
}

/** Image tour card — same style as Admin “Experience Catalog Highlights”. */
@Composable
fun ExperienceCatalogCard(
    imageUrl: String,
    badge: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
    height: androidx.compose.ui.unit.Dp = 140.dp,
) {
    Box(modifier = modifier.fillMaxWidth().height(height).clip(RoundedCornerShape(14.dp))) {
        AsyncImage(imageUrl, title, Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
        Box(
            Modifier.fillMaxSize().background(
                Brush.verticalGradient(listOf(Color.Transparent, Color.Black.copy(alpha = 0.75f))),
            ),
        )
        Column(Modifier.align(Alignment.BottomStart).padding(12.dp)) {
            Text(
                badge,
                modifier = Modifier
                    .clip(RoundedCornerShape(999.dp))
                    .background(MaterialTheme.colorScheme.primaryContainer)
                    .padding(horizontal = 8.dp, vertical = 2.dp),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onPrimaryContainer,
            )
            Text(title, color = Color.White, fontWeight = FontWeight.Bold)
            Text(subtitle, color = Color.White.copy(alpha = 0.85f), style = MaterialTheme.typography.bodySmall)
        }
    }
}

@Composable
fun WanderlustBottomNav(
    selected: WanderlustNavTab,
    modifier: Modifier = Modifier,
    onHome: () -> Unit = {},
    onExplore: () -> Unit = {},
    onSaved: () -> Unit = {},
    onProfile: () -> Unit = {},
) {
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val barColor = if (dark) {
        WanderlustDark.SurfaceContainerHigh.copy(alpha = 0.96f)
    } else {
        Color.White.copy(alpha = 0.97f)
    }
    val barBorder = if (dark) {
        WanderlustDark.GhostBorder
    } else {
        MaterialTheme.colorScheme.outlineVariant.copy(alpha = 0.55f)
    }

    Box(
        modifier = modifier
            .fillMaxWidth()
            .navigationBarsPadding()
            .padding(horizontal = 14.dp, vertical = 10.dp),
    ) {
        Surface(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(28.dp),
            color = barColor,
            shadowElevation = if (dark) 8.dp else 14.dp,
            tonalElevation = 0.dp,
            border = BorderStroke(1.dp, barBorder),
        ) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 4.dp, vertical = 6.dp),
                horizontalArrangement = Arrangement.SpaceEvenly,
                verticalAlignment = Alignment.CenterVertically,
            ) {
                BottomNavItem(
                    selectedIcon = Icons.Filled.Home,
                    unselectedIcon = Icons.Outlined.Home,
                    label = stringResource(R.string.nav_home),
                    selected = selected == WanderlustNavTab.Home,
                    onClick = onHome,
                    modifier = Modifier.weight(1f),
                )
                BottomNavItem(
                    selectedIcon = Icons.Filled.Explore,
                    unselectedIcon = Icons.Outlined.Explore,
                    label = stringResource(R.string.nav_explore),
                    selected = selected == WanderlustNavTab.Explore,
                    onClick = onExplore,
                    modifier = Modifier.weight(1f),
                )
                BottomNavItem(
                    selectedIcon = Icons.Filled.Bookmark,
                    unselectedIcon = Icons.Outlined.BookmarkBorder,
                    label = stringResource(R.string.nav_saved),
                    selected = selected == WanderlustNavTab.Saved,
                    onClick = onSaved,
                    modifier = Modifier.weight(1f),
                )
                BottomNavItem(
                    selectedIcon = Icons.Filled.Person,
                    unselectedIcon = Icons.Outlined.Person,
                    label = stringResource(R.string.nav_profile),
                    selected = selected == WanderlustNavTab.Profile,
                    onClick = onProfile,
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}

@Composable
private fun BottomNavItem(
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.72f)
    val pillBg = MaterialTheme.colorScheme.primaryContainer.copy(alpha = if (selected) 0.28f else 0f)

    val contentColor by androidx.compose.animation.animateColorAsState(
        targetValue = if (selected) primary else muted,
        label = "navContent",
    )
    val backgroundColor by androidx.compose.animation.animateColorAsState(
        targetValue = pillBg,
        label = "navPill",
    )
    val scale by androidx.compose.animation.core.animateFloatAsState(
        targetValue = if (selected) 1.06f else 1f,
        label = "navScale",
    )

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = modifier
            .clip(RoundedCornerShape(22.dp))
            .background(backgroundColor)
            .clickable(onClick = onClick)
            .padding(horizontal = 4.dp, vertical = 8.dp)
            .graphicsLayer {
                scaleX = scale
                scaleY = scale
            },
    ) {
        Box(
            modifier = Modifier
                .size(width = 48.dp, height = 28.dp)
                .clip(RoundedCornerShape(14.dp))
                .background(
                    if (selected) {
                        Brush.horizontalGradient(
                            listOf(
                                MaterialTheme.colorScheme.primaryContainer.copy(alpha = 0.55f),
                                MaterialTheme.colorScheme.primary.copy(alpha = 0.18f),
                            ),
                        )
                    } else {
                        Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                    },
                ),
            contentAlignment = Alignment.Center,
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = contentColor,
            )
        }
        Spacer(Modifier.height(2.dp))
        Text(
            label,
            style = MaterialTheme.typography.labelSmall.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                letterSpacing = 0.15.sp,
            ),
            color = contentColor,
            maxLines = 1,
        )
    }
}
