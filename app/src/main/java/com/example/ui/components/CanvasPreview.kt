package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
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
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.BlendMode
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AspectRatio as NovaAspectRatio
import com.example.model.ColorGradeSettings
import com.example.model.LutPreset
import com.example.model.Project
import com.example.model.SubtitleItem
import com.example.model.SubtitleStyle
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioMagenta
import com.example.ui.theme.StudioPlayheadRed
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary

@Composable
fun CanvasPreview(
    project: Project,
    currentPositionMs: Long,
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onStepFrame: (Boolean) -> Unit,
    onAspectRatioClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    var showSafeGuides by remember { mutableStateOf(false) }

    // Active subtitle at playhead
    val activeSubtitle = remember(project.subtitles, currentPositionMs) {
        project.subtitles.find { currentPositionMs in it.startMs..it.endMs }
    }

    // Color matrix calculation based on ColorGradeSettings
    val colorFilter = remember(project.colorGrade) {
        createLutColorFilter(project.colorGrade)
    }

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioDarkBg),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Studio Monitor Header
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Timecode display: MM:SS:FF
            Row(verticalAlignment = Alignment.CenterVertically) {
                Box(
                    modifier = Modifier
                        .size(8.dp)
                        .clip(CircleShape)
                        .background(if (isPlaying) StudioPlayheadRed else StudioCyan)
                )
                Spacer(modifier = Modifier.width(6.dp))
                Text(
                    text = formatTimecode(currentPositionMs),
                    color = TextPrimary,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp
                )
                Text(
                    text = " / ${formatTimecode(project.totalDurationMs)}",
                    color = TextMuted,
                    fontFamily = FontFamily.Monospace,
                    fontSize = 12.sp
                )
            }

            // Aspect Ratio badge & Safe guide button
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioSurface,
                    modifier = Modifier
                        .clickable { onAspectRatioClick() }
                        .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
                        .testTag("aspect_ratio_pill")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Aspect Ratio",
                            tint = StudioCyan,
                            modifier = Modifier.size(14.dp)
                        )
                        Spacer(modifier = Modifier.width(4.dp))
                        Text(
                            text = project.aspectRatio.label.takeWhile { it != ' ' },
                            color = StudioCyan,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.SemiBold
                        )
                    }
                }

                Spacer(modifier = Modifier.width(6.dp))

                IconButton(
                    onClick = { showSafeGuides = !showSafeGuides },
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.Security,
                        contentDescription = "Safe Margin Guides",
                        tint = if (showSafeGuides) StudioCyan else TextMuted,
                        modifier = Modifier.size(16.dp)
                    )
                }
            }
        }

        // Viewport Frame
        BoxWithConstraints(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
                .padding(horizontal = 8.dp, vertical = 2.dp),
            contentAlignment = Alignment.Center
        ) {
            val aspect = project.aspectRatio.ratioWidth / project.aspectRatio.ratioHeight

            Box(
                modifier = Modifier
                    .aspectRatio(aspect, matchHeightConstraintsFirst = true)
                    .fillMaxSize()
                    .clip(RoundedCornerShape(8.dp))
                    .background(Color.Black)
                    .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                    .clickable { onTogglePlay() },
                contentAlignment = Alignment.Center
            ) {
                // Background video frame thumbnail
                val coverRes = project.coverDrawableRes ?: R.drawable.sample_thumb_cyber_1791582067290
                Image(
                    painter = painterResource(id = coverRes),
                    contentDescription = "Video Frame",
                    contentScale = ContentScale.Crop,
                    colorFilter = colorFilter,
                    modifier = Modifier.fillMaxSize()
                )

                // Optional Vignette overlay
                if (project.colorGrade.vignette > 0f) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.radialGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = (project.colorGrade.vignette * 0.8f).coerceIn(0f, 0.95f))
                                    )
                                )
                            )
                    )
                }

                // Social Safe Guides Overlay (Reels / TikTok icons margin preview)
                if (showSafeGuides) {
                    SafeMarginOverlay()
                }

                // Dynamic Live Subtitle Renderer
                activeSubtitle?.let { sub ->
                    SubtitleOverlayView(subtitle = sub)
                }

                // Play / Pause central indicator flash when paused
                if (!isPlaying) {
                    Box(
                        modifier = Modifier
                            .size(52.dp)
                            .clip(CircleShape)
                            .background(Color.Black.copy(alpha = 0.55f))
                            .border(1.dp, StudioCyan.copy(alpha = 0.5f), CircleShape),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.PlayArrow,
                            contentDescription = "Play",
                            tint = StudioCyan,
                            modifier = Modifier.size(32.dp)
                        )
                    }
                }
            }
        }

        // Transport Controls Mini Bar
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 4.dp),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {
            IconButton(
                onClick = { onStepFrame(false) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FastRewind,
                    contentDescription = "Previous Frame",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            Surface(
                shape = CircleShape,
                color = StudioCyan,
                modifier = Modifier
                    .size(42.dp)
                    .clickable { onTogglePlay() }
                    .testTag("play_pause_button")
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(
                        imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                        contentDescription = if (isPlaying) "Pause" else "Play",
                        tint = Color.Black,
                        modifier = Modifier.size(24.dp)
                    )
                }
            }

            Spacer(modifier = Modifier.width(12.dp))

            IconButton(
                onClick = { onStepFrame(true) },
                modifier = Modifier.size(36.dp)
            ) {
                Icon(
                    imageVector = Icons.Default.FastForward,
                    contentDescription = "Next Frame",
                    tint = TextSecondary,
                    modifier = Modifier.size(20.dp)
                )
            }
        }
    }
}

@Composable
private fun SubtitleOverlayView(subtitle: SubtitleItem) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 36.dp, start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val style = subtitle.stylePreset
        val textColor = Color(style.fontColorHex)
        val bgColor = Color(style.bgHex)

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = bgColor,
            shadowElevation = 4.dp,
            modifier = Modifier.border(
                1.dp,
                if (style == SubtitleStyle.NEON_CYAN) StudioCyan else Color.Transparent,
                RoundedCornerShape(8.dp)
            )
        ) {
            Text(
                text = subtitle.text,
                color = textColor,
                fontSize = 15.sp,
                fontWeight = FontWeight.ExtraBold,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp)
            )
        }
    }
}

@Composable
private fun SafeMarginOverlay() {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .border(1.dp, StudioCyan.copy(alpha = 0.4f))
            .padding(top = 40.dp, bottom = 60.dp, end = 44.dp, start = 12.dp)
            .border(1.dp, StudioMagenta.copy(alpha = 0.5f))
    ) {
        Text(
            text = "SAFE ACTION ZONE",
            color = StudioCyan.copy(alpha = 0.7f),
            fontSize = 9.sp,
            fontWeight = FontWeight.Bold,
            modifier = Modifier
                .align(Alignment.TopStart)
                .padding(4.dp)
        )
    }
}

fun formatTimecode(ms: Long): String {
    val totalSeconds = ms / 1000
    val minutes = totalSeconds / 60
    val seconds = totalSeconds % 60
    val frames = ((ms % 1000) / 33.3f).toInt()
    return String.format("%02d:%02d:%02d", minutes, seconds, frames)
}

private fun createLutColorFilter(settings: ColorGradeSettings): ColorFilter? {
    val matrix = ColorMatrix()

    // Contrast & Brightness
    val c = settings.contrast
    val b = settings.brightness * 255f
    val scale = c
    val translate = (1f - c) * 128f + b

    val cmValues = floatArrayOf(
        scale, 0f, 0f, 0f, translate,
        0f, scale, 0f, 0f, translate,
        0f, 0f, scale, 0f, translate,
        0f, 0f, 0f, 1f, 0f
    )
    matrix.set(ColorMatrix(cmValues))

    // Preset color tints
    when (settings.lut) {
        LutPreset.CYBERPUNK -> {
            // Boost cyan and magenta
            val cyber = ColorMatrix(
                floatArrayOf(
                    1.2f, 0f, 0.3f, 0f, 10f,
                    0f, 1.1f, 0.3f, 0f, 5f,
                    0.2f, 0.4f, 1.4f, 0f, 30f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            matrix.timesAssign(cyber)
        }
        LutPreset.TEAL_ORANGE -> {
            // Orange highlights, teal shadows
            val tealOrange = ColorMatrix(
                floatArrayOf(
                    1.3f, 0.1f, 0f, 0f, 15f,
                    0f, 1.1f, 0.2f, 0f, 0f,
                    0f, 0.2f, 1.3f, 0f, 10f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            matrix.timesAssign(tealOrange)
        }
        LutPreset.GOLDEN_HOUR -> {
            val warm = ColorMatrix(
                floatArrayOf(
                    1.3f, 0.1f, 0f, 0f, 20f,
                    0.1f, 1.15f, 0f, 0f, 10f,
                    0f, 0f, 0.85f, 0f, -10f,
                    0f, 0f, 0f, 1f, 0f
                )
            )
            matrix.timesAssign(warm)
        }
        LutPreset.FILM_NOIR -> {
            // Monochromatic grayscale
            val noir = ColorMatrix()
            noir.setToSaturation(0f)
            matrix.timesAssign(noir)
        }
        else -> Unit
    }

    return ColorFilter.colorMatrix(matrix)
}
