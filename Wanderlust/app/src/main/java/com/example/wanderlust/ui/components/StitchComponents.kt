package com.example.wanderlust.ui.components

import androidx.activity.compose.BackHandler
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmark
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.TravelExplore
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Person
import androidx.compose.material.icons.outlined.TravelExplore
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import com.example.wanderlust.locale.stringApp
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.wanderlust.R
import com.example.wanderlust.ui.theme.WanderlustDark
import com.example.wanderlust.ui.theme.WanderlustLight
import kotlin.math.cos
import kotlin.math.roundToInt
import kotlin.math.sin

enum class WanderlustNavTab { Home, Tours, Saved, Profile }

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
                stringApp(R.string.brand_tagline),
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
    onTours: () -> Unit = {},
    onSaved: () -> Unit = {},
    onProfile: () -> Unit = {},
) {
    var expanded by remember { mutableStateOf(false) }
    val dark = MaterialTheme.colorScheme.background.luminance() < 0.5f
    val dockColor = if (dark) {
        WanderlustDark.SurfaceContainerHigh
    } else {
        MaterialTheme.colorScheme.surface
    }
    val menuRotation by animateFloatAsState(
        targetValue = if (expanded) 90f else 0f,
        label = "menuRotation",
    )
    val expandProgress by animateFloatAsState(
        targetValue = if (expanded) 1f else 0f,
        animationSpec = spring(
            dampingRatio = Spring.DampingRatioMediumBouncy,
            stiffness = Spring.StiffnessMediumLow,
        ),
        label = "radialExpand",
    )

    BackHandler(enabled = expanded) { expanded = false }

    val dockPadding = Modifier
        .navigationBarsPadding()
        .padding(end = 16.dp, bottom = 16.dp)

    fun selectTab(action: () -> Unit) {
        expanded = false
        action()
    }

    if (expanded) {
        Box(modifier = Modifier.fillMaxSize()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .clickable(
                        interactionSource = remember { MutableInteractionSource() },
                        indication = null,
                        onClick = { expanded = false },
                    ),
            )
            RadialMenuDock(
                modifier = Modifier
                    .align(Alignment.BottomEnd)
                    .then(dockPadding),
                selected = selected,
                expanded = true,
                expandProgress = expandProgress,
                dockColor = dockColor,
                dark = dark,
                menuRotation = menuRotation,
                onToggle = { expanded = false },
                onHome = { selectTab(onHome) },
                onTours = { selectTab(onTours) },
                onSaved = { selectTab(onSaved) },
                onProfile = { selectTab(onProfile) },
            )
        }
    } else {
        RadialMenuDock(
            modifier = modifier.then(dockPadding),
            selected = selected,
            expanded = false,
            expandProgress = expandProgress,
            dockColor = dockColor,
            dark = dark,
            menuRotation = menuRotation,
            onToggle = { expanded = true },
            onHome = onHome,
            onTours = onTours,
            onSaved = onSaved,
            onProfile = onProfile,
        )
    }
}

private data class RadialMenuEntry(
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector,
    val labelRes: Int,
    val tab: WanderlustNavTab,
    val onClick: () -> Unit,
)

@Composable
private fun RadialMenuDock(
    selected: WanderlustNavTab,
    expanded: Boolean,
    expandProgress: Float,
    dockColor: Color,
    dark: Boolean,
    menuRotation: Float,
    onToggle: () -> Unit,
    onHome: () -> Unit,
    onTours: () -> Unit,
    onSaved: () -> Unit,
    onProfile: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val density = LocalDensity.current
    val radiusPx = with(density) { 108.dp.toPx() }
    val fabSize = 56.dp
    val itemSize = 56.dp
    // Room for quarter-circle fan up-and-left from FAB.
    val canvasSize = 200.dp

    val entries = listOf(
        RadialMenuEntry(
            Icons.Filled.Home,
            Icons.Outlined.Home,
            R.string.nav_home,
            WanderlustNavTab.Home,
            onHome,
        ),
        RadialMenuEntry(
            Icons.Filled.TravelExplore,
            Icons.Outlined.TravelExplore,
            R.string.nav_tours,
            WanderlustNavTab.Tours,
            onTours,
        ),
        RadialMenuEntry(
            Icons.Filled.Bookmark,
            Icons.Outlined.BookmarkBorder,
            R.string.nav_saved,
            WanderlustNavTab.Saved,
            onSaved,
        ),
        RadialMenuEntry(
            Icons.Filled.Person,
            Icons.Outlined.Person,
            R.string.nav_profile,
            WanderlustNavTab.Profile,
            onProfile,
        ),
    )
    // Clock-hand angles: left → upper-left → up (180° … 90°).
    val startAngleDeg = 180f
    val endAngleDeg = 90f

    Box(
        modifier = modifier.size(if (expandProgress > 0.001f) canvasSize else fabSize),
        contentAlignment = Alignment.BottomEnd,
    ) {
        if (expandProgress > 0.001f) {
            entries.forEachIndexed { index, entry ->
                val t = if (entries.size == 1) 0f else index / (entries.size - 1).toFloat()
                val angleDeg = startAngleDeg + (endAngleDeg - startAngleDeg) * t
                val rad = Math.toRadians(angleDeg.toDouble())
                val dist = radiusPx * expandProgress
                // cos/sin in math coords (0° = right, 90° = up); Compose Y grows down.
                val ox = (cos(rad) * dist).roundToInt()
                val oy = (-sin(rad) * dist).roundToInt()
                // Center item on the tip of each "hand" (FAB center is mid of bottom-end fab).
                val fabCenterAdjust = with(density) { ((fabSize - itemSize) / 2).roundToPx() }

                RadialMenuItem(
                    selectedIcon = entry.selectedIcon,
                    unselectedIcon = entry.unselectedIcon,
                    label = stringApp(entry.labelRes),
                    selected = selected == entry.tab,
                    dockColor = dockColor,
                    dark = dark,
                    progress = expandProgress,
                    enabled = expanded && expandProgress > 0.35f,
                    onClick = entry.onClick,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .offset {
                            IntOffset(
                                x = ox + fabCenterAdjust,
                                y = oy + fabCenterAdjust,
                            )
                        },
                )
            }
        }

        Surface(
            shape = CircleShape,
            color = MaterialTheme.colorScheme.primary,
            shadowElevation = if (dark) 8.dp else 10.dp,
            tonalElevation = 0.dp,
            modifier = Modifier
                .size(fabSize)
                .clickable(onClick = onToggle),
        ) {
            Box(contentAlignment = Alignment.Center, modifier = Modifier.fillMaxSize()) {
                Icon(
                    imageVector = if (expanded) Icons.Filled.Close else Icons.Filled.Menu,
                    contentDescription = stringApp(R.string.nav_menu),
                    tint = MaterialTheme.colorScheme.onPrimary,
                    modifier = Modifier
                        .size(26.dp)
                        .graphicsLayer { rotationZ = menuRotation },
                )
            }
        }
    }
}

@Composable
private fun RadialMenuItem(
    selectedIcon: ImageVector,
    unselectedIcon: ImageVector,
    label: String,
    selected: Boolean,
    dockColor: Color,
    dark: Boolean,
    progress: Float,
    enabled: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val primary = MaterialTheme.colorScheme.primary
    val muted = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.75f)
    val contentColor = if (selected) primary else muted

    Surface(
        shape = CircleShape,
        color = dockColor,
        shadowElevation = if (dark) 6.dp else 8.dp,
        tonalElevation = 0.dp,
        modifier = modifier
            .size(56.dp)
            .graphicsLayer {
                alpha = progress
                scaleX = 0.55f + 0.45f * progress
                scaleY = 0.55f + 0.45f * progress
            }
            .then(
                if (enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            ),
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxSize()
                .then(
                    if (selected) {
                        Modifier.background(primary.copy(alpha = 0.12f))
                    } else {
                        Modifier
                    },
                ),
        ) {
            Icon(
                imageVector = if (selected) selectedIcon else unselectedIcon,
                contentDescription = label,
                modifier = Modifier.size(22.dp),
                tint = contentColor,
            )
            Text(
                label,
                style = MaterialTheme.typography.labelSmall.copy(
                    fontFamily = FontFamily.SansSerif,
                    fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                    fontSize = 9.sp,
                    letterSpacing = 0.sp,
                ),
                color = contentColor,
                maxLines = 1,
            )
        }
    }
}

