package com.example.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.VolumeUp
import androidx.compose.material3.Divider
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ClipType
import com.example.model.MediaClip
import com.example.model.Project
import com.example.model.TimelineTrack
import com.example.model.TrackType
import com.example.ui.theme.AudioTrackColor
import com.example.ui.theme.FxTrackColor
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioMagenta
import com.example.ui.theme.StudioPlayheadRed
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.SubtitleTrackColor
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.ui.theme.VideoTrackColor
import kotlin.math.roundToInt

private const val PIXELS_PER_SECOND = 40f // Timeline scale

@Composable
fun MultiTrackTimeline(
    project: Project,
    currentPositionMs: Long,
    selectedClipId: String?,
    selectedTrackId: String?,
    onSeekTo: (Long) -> Unit,
    onSelectClip: (String, String) -> Unit,
    onSplitClip: () -> Unit,
    onDeleteClip: () -> Unit,
    onDuplicateClip: () -> Unit,
    onOpenSpeed: () -> Unit,
    onOpenAudio: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scrollState = rememberScrollState()
    val totalSeconds = (project.totalDurationMs / 1000f).coerceAtLeast(15f)
    val timelineWidthDp = (totalSeconds * PIXELS_PER_SECOND).dp

    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(StudioSurface)
            .border(1.dp, StudioBorder)
    ) {
        // Quick Action Bar on top of timeline
        TimelineActionBar(
            hasClipSelected = selectedClipId != null,
            onSplitClip = onSplitClip,
            onDeleteClip = onDeleteClip,
            onDuplicateClip = onDuplicateClip,
            onOpenSpeed = onOpenSpeed,
            onOpenAudio = onOpenAudio
        )

        HorizontalDivider(color = StudioBorder, thickness = 1.dp)

        // Ruler + Tracks scroll area
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1f)
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .horizontalScroll(scrollState)
                    .width(timelineWidthDp + 200.dp)
            ) {
                // Time Ruler
                TimeRuler(
                    totalSeconds = totalSeconds,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(26.dp)
                )

                HorizontalDivider(color = StudioBorder, thickness = 1.dp)

                // Render Tracks
                Box(modifier = Modifier.fillMaxWidth()) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalArrangement = Arrangement.spacedBy(6.dp)
                    ) {
                        project.tracks.forEach { track ->
                            TrackRow(
                                track = track,
                                selectedClipId = selectedClipId,
                                onSelectClip = { clipId -> onSelectClip(track.id, clipId) }
                            )
                        }
                    }

                    // Interactive Scrubbing Layer
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .pointerInput(project.totalDurationMs) {
                                detectDragGestures { change, dragAmount ->
                                    change.consume()
                                    val currentSec = currentPositionMs / 1000f
                                    val deltaSec = dragAmount.x / PIXELS_PER_SECOND
                                    val newMs = ((currentSec + deltaSec) * 1000f).toLong()
                                    onSeekTo(newMs.coerceIn(0L, project.totalDurationMs))
                                }
                            }
                    )

                    // Scrubber Playhead Line
                    val playheadOffsetDp = ((currentPositionMs / 1000f) * PIXELS_PER_SECOND).dp
                    PlayheadIndicator(
                        modifier = Modifier
                            .offset { IntOffset(playheadOffsetDp.roundToPx(), 0) }
                            .fillMaxHeight()
                    )
                }
            }
        }
    }
}

@Composable
private fun TimelineActionBar(
    hasClipSelected: Boolean,
    onSplitClip: () -> Unit,
    onDeleteClip: () -> Unit,
    onDuplicateClip: () -> Unit,
    onOpenSpeed: () -> Unit,
    onOpenAudio: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(StudioDarkBg)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(
            horizontalArrangement = Arrangement.spacedBy(4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Split Action Button
            TimelineActionItem(
                icon = Icons.Default.ContentCut,
                label = "Split",
                tint = StudioCyan,
                enabled = true,
                onClick = onSplitClip,
                testTag = "timeline_split_button"
            )

            // Speed Action
            TimelineActionItem(
                icon = Icons.Default.Speed,
                label = "Speed",
                tint = TextPrimary,
                enabled = hasClipSelected,
                onClick = onOpenSpeed,
                testTag = "timeline_speed_button"
            )

            // Volume / Audio Action
            TimelineActionItem(
                icon = Icons.Default.VolumeUp,
                label = "Audio",
                tint = TextPrimary,
                enabled = hasClipSelected,
                onClick = onOpenAudio,
                testTag = "timeline_audio_button"
            )

            // Duplicate Action
            TimelineActionItem(
                icon = Icons.Default.ContentCopy,
                label = "Duplicate",
                tint = TextPrimary,
                enabled = hasClipSelected,
                onClick = onDuplicateClip,
                testTag = "timeline_duplicate_button"
            )

            // Delete Action
            TimelineActionItem(
                icon = Icons.Default.Delete,
                label = "Delete",
                tint = if (hasClipSelected) StudioPlayheadRed else TextMuted,
                enabled = hasClipSelected,
                onClick = onDeleteClip,
                testTag = "timeline_delete_button"
            )
        }

        // Snap indicator badge
        Surface(
            shape = RoundedCornerShape(10.dp),
            color = StudioSurfaceVariant,
            modifier = Modifier.border(1.dp, StudioBorder, RoundedCornerShape(10.dp))
        ) {
            Text(
                text = "MAGNETIC SNAP",
                color = StudioCyan,
                fontSize = 10.sp,
                fontWeight = FontWeight.Bold,
                modifier = Modifier.padding(horizontal = 8.dp, vertical = 3.dp)
            )
        }
    }
}

@Composable
private fun TimelineActionItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    tint: Color,
    enabled: Boolean,
    onClick: () -> Unit,
    testTag: String
) {
    Surface(
        shape = RoundedCornerShape(8.dp),
        color = if (enabled) StudioSurfaceVariant else Color.Transparent,
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable(enabled = enabled) { onClick() }
            .testTag(testTag)
    ) {
        Row(
            modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Icon(
                imageVector = icon,
                contentDescription = label,
                tint = if (enabled) tint else TextMuted,
                modifier = Modifier.size(15.dp)
            )
            Spacer(modifier = Modifier.width(4.dp))
            Text(
                text = label,
                color = if (enabled) tint else TextMuted,
                fontSize = 11.sp,
                fontWeight = FontWeight.SemiBold
            )
        }
    }
}

@Composable
private fun TimeRuler(
    totalSeconds: Float,
    modifier: Modifier = Modifier
) {
    Box(
        modifier = modifier
            .background(StudioDarkBg)
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val stepSec = 1
            val maxSec = totalSeconds.toInt() + 5

            for (sec in 0..maxSec step stepSec) {
                val x = sec * PIXELS_PER_SECOND * density
                val isMajor = sec % 2 == 0
                val tickHeight = if (isMajor) 14.dp.toPx() else 8.dp.toPx()
                val tickColor = if (isMajor) StudioCyan.copy(alpha = 0.8f) else StudioBorder

                drawLine(
                    color = tickColor,
                    start = Offset(x, size.height - tickHeight),
                    end = Offset(x, size.height),
                    strokeWidth = 1.5f
                )
            }
        }

        // Draw numbers row
        Row(
            modifier = Modifier.fillMaxSize()
        ) {
            val maxSec = totalSeconds.toInt() + 5
            for (sec in 0..maxSec step 2) {
                val offset = (sec * PIXELS_PER_SECOND).dp
                Box(modifier = Modifier.offset(x = offset + 2.dp)) {
                    Text(
                        text = "${sec}s",
                        color = TextSecondary,
                        fontSize = 10.sp,
                        fontFamily = FontFamily.Monospace,
                        fontWeight = FontWeight.Bold
                    )
                }
            }
        }
    }
}

@Composable
private fun TrackRow(
    track: TimelineTrack,
    selectedClipId: String?,
    onSelectClip: (String) -> Unit
) {
    val trackBg = when (track.type) {
        TrackType.VIDEO_MAIN -> VideoTrackColor
        TrackType.AUDIO_MUSIC, TrackType.AUDIO_VOICE -> AudioTrackColor
        TrackType.SUBTITLE -> SubtitleTrackColor
        else -> FxTrackColor
    }

    Box(
        modifier = Modifier
            .fillMaxWidth()
            .height(44.dp)
            .background(StudioDarkBg.copy(alpha = 0.6f))
    ) {
        track.clips.forEach { clip ->
            val startDp = ((clip.startInTimelineMs / 1000f) * PIXELS_PER_SECOND).dp
            val widthDp = ((clip.durationMs / 1000f) * PIXELS_PER_SECOND).dp.coerceAtLeast(30.dp)
            val isSelected = clip.id == selectedClipId

            Surface(
                shape = RoundedCornerShape(6.dp),
                color = trackBg.copy(alpha = 0.85f),
                modifier = Modifier
                    .offset(x = startDp)
                    .width(widthDp)
                    .fillMaxHeight()
                    .padding(vertical = 2.dp)
                    .border(
                        width = if (isSelected) 2.dp else 1.dp,
                        color = if (isSelected) StudioCyan else StudioBorder.copy(alpha = 0.5f),
                        shape = RoundedCornerShape(6.dp)
                    )
                    .clickable { onSelectClip(clip.id) }
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(horizontal = 6.dp),
                    contentAlignment = Alignment.CenterStart
                ) {
                    if (track.type == TrackType.AUDIO_MUSIC || track.type == TrackType.AUDIO_VOICE) {
                        AudioWaveformVisual()
                    }

                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = clip.name,
                            color = Color.White,
                            fontSize = 11.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )

                        if (clip.speed != 1.0f) {
                            Text(
                                text = "${clip.speed}x",
                                color = StudioCyan,
                                fontSize = 10.sp,
                                fontWeight = FontWeight.ExtraBold
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun AudioWaveformVisual() {
    Canvas(
        modifier = Modifier
            .fillMaxSize()
            .padding(vertical = 4.dp)
    ) {
        val barWidth = 3f
        val gap = 2f
        val count = (size.width / (barWidth + gap)).toInt()

        for (i in 0 until count) {
            val hRatio = ((kotlin.math.sin(i * 0.45) * 0.4 + kotlin.math.cos(i * 0.2) * 0.3 + 0.5)).toFloat()
            val barHeight = (size.height * hRatio).coerceIn(4f, size.height)
            val startY = (size.height - barHeight) / 2
            val x = i * (barWidth + gap)

            drawLine(
                color = Color.White.copy(alpha = 0.35f),
                start = Offset(x, startY),
                end = Offset(x, startY + barHeight),
                strokeWidth = barWidth
            )
        }
    }
}

@Composable
private fun PlayheadIndicator(modifier: Modifier = Modifier) {
    Column(
        modifier = modifier.width(2.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        // Red Top Scrubber Head
        Box(
            modifier = Modifier
                .size(10.dp)
                .clip(CircleShape)
                .background(StudioPlayheadRed)
        )
        // Red Vertical Needle
        Box(
            modifier = Modifier
                .width(2.dp)
                .fillMaxHeight()
                .background(StudioPlayheadRed)
        )
    }
}
