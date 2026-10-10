package com.example.ui.components

import android.graphics.SurfaceTexture
import android.media.MediaPlayer
import android.media.PlaybackParams
import android.net.Uri
import android.os.Build
import android.view.Surface
import android.view.TextureView
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
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.FastForward
import androidx.compose.material.icons.filled.FastRewind
import androidx.compose.material.icons.filled.Pause
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Security
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.graphics.ColorMatrix
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import com.example.R
import com.example.model.AspectRatio as NovaAspectRatio
import com.example.model.ColorGradeSettings
import com.example.model.LutPreset
import com.example.model.Project
import com.example.model.SubtitleItem
import com.example.model.SubtitleStyle
import com.example.model.TrackType
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
    onAddMediaClick: () -> Unit = {},
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
                    fontSize = 13.sp,
                    fontFamily = FontFamily.Monospace,
                    fontWeight = FontWeight.Bold
                )
                Text(
                    text = " / ${formatTimecode(project.totalDurationMs)}",
                    color = TextMuted,
                    fontSize = 11.sp,
                    fontFamily = FontFamily.Monospace
                )
            }

            // Aspect Ratio badge & Safe margins toggle
            Row(verticalAlignment = Alignment.CenterVertically) {
                Surface(
                    shape = RoundedCornerShape(6.dp),
                    color = StudioSurface,
                    modifier = Modifier
                        .clickable { onAspectRatioClick() }
                        .border(1.dp, StudioBorder, RoundedCornerShape(6.dp))
                        .testTag("canvas_aspect_button")
                ) {
                    Row(
                        modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(
                            imageVector = Icons.Default.AspectRatio,
                            contentDescription = "Aspect Ratio",
                            tint = StudioCyan,
                            modifier = Modifier.size(12.dp)
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
                // Active video clip at current timeline position
                val activeVideoClip = remember(project.tracks, currentPositionMs) {
                    project.tracks.find { it.type == TrackType.VIDEO_MAIN }
                        ?.clips
                        ?.find { currentPositionMs in it.startInTimelineMs..(it.startInTimelineMs + it.durationMs) }
                        ?: project.tracks.find { it.type == TrackType.VIDEO_MAIN }?.clips?.firstOrNull()
                }
                val realVideoUri = activeVideoClip?.uriString ?: project.coverUriString
                val hasAnyClips = project.tracks.any { it.clips.isNotEmpty() }

                if (realVideoUri != null) {
                    // Real hardware-accelerated video player with TextureView
                    RealVideoSurfacePlayer(
                        uriString = realVideoUri,
                        isPlaying = isPlaying,
                        currentPositionMs = currentPositionMs,
                        clipStartTimelineMs = activeVideoClip?.startInTimelineMs ?: 0L,
                        trimInMs = activeVideoClip?.trimInMs ?: 0L,
                        playbackSpeed = activeVideoClip?.speed ?: 1.0f,
                        volume = activeVideoClip?.volume ?: 1.0f,
                        modifier = Modifier.fillMaxSize()
                    )
                } else if (!hasAnyClips) {
                    // CapCut-style empty canvas state: prompt user to import their phone video
                    Column(
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(16.dp),
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.Center
                    ) {
                        Box(
                            modifier = Modifier
                                .size(56.dp)
                                .clip(CircleShape)
                                .background(StudioCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.VideoLibrary,
                                contentDescription = null,
                                tint = StudioCyan,
                                modifier = Modifier.size(32.dp)
                            )
                        }
                        Spacer(modifier = Modifier.height(10.dp))
                        Text(
                            text = "No Video Added Yet",
                            color = TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 14.sp
                        )
                        Text(
                            text = "Pick any video from your phone to start editing",
                            color = TextSecondary,
                            fontSize = 11.sp,
                            textAlign = TextAlign.Center
                        )
                        Spacer(modifier = Modifier.height(14.dp))
                        Button(
                            onClick = onAddMediaClick,
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp)
                        ) {
                            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
                            Spacer(modifier = Modifier.width(6.dp))
                            Text("Pick Video from Phone", fontWeight = FontWeight.ExtraBold, fontSize = 12.sp)
                        }
                    }
                } else {
                    // Fallback to sample project thumbnail frame
                    val coverRes = project.coverDrawableRes ?: R.drawable.sample_thumb_cyber_1791582067290
                    Image(
                        painter = painterResource(id = coverRes),
                        contentDescription = "Video Frame",
                        contentScale = ContentScale.Crop,
                        colorFilter = colorFilter,
                        modifier = Modifier.fillMaxSize()
                    )
                }

                // Live Color Grade Filter Overlay over the video view
                if (project.colorGrade.lut != LutPreset.NONE || project.colorGrade.brightness != 0f || project.colorGrade.saturation != 1f || project.colorGrade.contrast != 1f) {
                    val tintColor = when (project.colorGrade.lut) {
                        LutPreset.CYBERPUNK -> Color(0xFF00E5FF).copy(alpha = 0.12f)
                        LutPreset.TEAL_ORANGE -> Color(0xFFFF9100).copy(alpha = 0.12f)
                        LutPreset.GOLDEN_HOUR -> Color(0xFFFFD700).copy(alpha = 0.14f)
                        LutPreset.FILM_NOIR -> Color.Black.copy(alpha = 0.25f)
                        LutPreset.MATRIX_GREEN -> Color(0xFF00E676).copy(alpha = 0.15f)
                        LutPreset.PASTEL_DREAM -> Color(0xFFFF80AB).copy(alpha = 0.10f)
                        else -> Color.Transparent
                    }
                    if (tintColor != Color.Transparent) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                                .background(tintColor)
                        )
                    }
                }

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
                if (!isPlaying && hasAnyClips) {
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

        // Studio Playback Controls Bar
        PlaybackTransportBar(
            isPlaying = isPlaying,
            onTogglePlay = onTogglePlay,
            onStepFrame = onStepFrame,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp, vertical = 6.dp)
        )
    }
}

/**
 * Real Video Surface Player using TextureView & MediaPlayer.
 * Works seamlessly with Jetpack Compose transforms and overlays.
 */
@Composable
private fun RealVideoSurfacePlayer(
    uriString: String,
    isPlaying: Boolean,
    currentPositionMs: Long,
    clipStartTimelineMs: Long,
    trimInMs: Long,
    playbackSpeed: Float,
    volume: Float,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var mediaPlayer by remember { mutableStateOf<MediaPlayer?>(null) }
    var isPrepared by remember { mutableStateOf(false) }

    DisposableEffect(uriString) {
        val player = MediaPlayer()
        isPrepared = false
        try {
            val uri = Uri.parse(uriString)
            try {
                context.contentResolver.openFileDescriptor(uri, "r")?.use { pfd ->
                    player.setDataSource(pfd.fileDescriptor)
                }
            } catch (e: Exception) {
                player.setDataSource(context, uri)
            }

            player.setOnPreparedListener { mp ->
                isPrepared = true
                mp.isLooping = true
                try {
                    mp.setVolume(volume, volume)
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        val params = PlaybackParams().apply { speed = playbackSpeed.coerceIn(0.25f, 4.0f) }
                        mp.playbackParams = params
                    }
                } catch (ignored: Exception) {}
                if (isPlaying) mp.start()
            }
            player.setOnErrorListener { _, _, _ ->
                // Suppress unhandled error popups gracefully
                true
            }
            player.prepareAsync()
            mediaPlayer = player
        } catch (e: Exception) {
            player.release()
        }

        onDispose {
            player.stop()
            player.release()
            mediaPlayer = null
            isPrepared = false
        }
    }

    AndroidView(
        factory = { ctx ->
            TextureView(ctx).apply {
                surfaceTextureListener = object : TextureView.SurfaceTextureListener {
                    override fun onSurfaceTextureAvailable(surface: SurfaceTexture, width: Int, height: Int) {
                        try {
                            mediaPlayer?.setSurface(Surface(surface))
                        } catch (ignored: Exception) {}
                    }
                    override fun onSurfaceTextureSizeChanged(surface: SurfaceTexture, width: Int, height: Int) {}
                    override fun onSurfaceTextureDestroyed(surface: SurfaceTexture): Boolean {
                        try {
                            mediaPlayer?.setSurface(null)
                        } catch (ignored: Exception) {}
                        return true
                    }
                    override fun onSurfaceTextureUpdated(surface: SurfaceTexture) {}
                }
            }
        },
        update = {
            val player = mediaPlayer ?: return@AndroidView
            if (!isPrepared) return@AndroidView

            try {
                if (isPlaying && !player.isPlaying) {
                    player.start()
                } else if (!isPlaying && player.isPlaying) {
                    player.pause()
                }

                val seekTarget = (trimInMs + (currentPositionMs - clipStartTimelineMs)).coerceAtLeast(0L)
                val curPlayerPos = player.currentPosition.toLong()
                if (kotlin.math.abs(curPlayerPos - seekTarget) > 800) {
                    player.seekTo(seekTarget.toInt())
                }
            } catch (ignored: Exception) {}
        },
        modifier = modifier
    )
}

@Composable
private fun PlaybackTransportBar(
    isPlaying: Boolean,
    onTogglePlay: () -> Unit,
    onStepFrame: (Boolean) -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.Center,
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Step back 1 frame
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

        Spacer(modifier = Modifier.width(16.dp))

        // Big Play/Pause Button
        Surface(
            shape = CircleShape,
            color = StudioCyan,
            modifier = Modifier
                .size(44.dp)
                .clickable { onTogglePlay() }
                .testTag("play_pause_button")
        ) {
            Box(contentAlignment = Alignment.Center) {
                Icon(
                    imageVector = if (isPlaying) Icons.Default.Pause else Icons.Default.PlayArrow,
                    contentDescription = if (isPlaying) "Pause" else "Play",
                    tint = Color.Black,
                    modifier = Modifier.size(26.dp)
                )
            }
        }

        Spacer(modifier = Modifier.width(16.dp))

        // Step forward 1 frame
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

@Composable
private fun SubtitleOverlayView(subtitle: SubtitleItem) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(bottom = 44.dp, start = 20.dp, end = 20.dp),
        contentAlignment = Alignment.BottomCenter
    ) {
        val style = subtitle.stylePreset
        val textColor = Color(style.fontColorHex)
        val bgColor = Color(style.bgHex)

        Surface(
            shape = RoundedCornerShape(8.dp),
            color = bgColor,
            shadowElevation = 4.dp
        ) {
            Text(
                text = subtitle.text,
                color = textColor,
                fontSize = 17.sp,
                fontWeight = FontWeight.Black,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(horizontal = 14.dp, vertical = 6.dp)
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

    when (settings.lut) {
        LutPreset.CYBERPUNK -> {
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
            val noir = ColorMatrix()
            noir.setToSaturation(0f)
            matrix.timesAssign(noir)
        }
        else -> Unit
    }

    return ColorFilter.colorMatrix(matrix)
}
