package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Redo
import androidx.compose.material.icons.automirrored.filled.Undo
import androidx.compose.material.icons.filled.AspectRatio
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCut
import androidx.compose.material.icons.filled.FileUpload
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.AspectRatio
import com.example.model.ColorGradeSettings
import com.example.model.Project
import com.example.model.SubtitleStyle
import com.example.ui.components.CanvasPreview
import com.example.ui.components.MultiTrackTimeline
import com.example.ui.components.ToolDrawersSheet
import com.example.ui.theme.StudioAmber
import com.example.ui.theme.StudioBorder
import com.example.ui.theme.StudioCardElevated
import com.example.ui.theme.StudioCyan
import com.example.ui.theme.StudioDarkBg
import com.example.ui.theme.StudioGreen
import com.example.ui.theme.StudioMagenta
import com.example.ui.theme.StudioSurface
import com.example.ui.theme.StudioSurfaceVariant
import com.example.ui.theme.TextMuted
import com.example.ui.theme.TextPrimary
import com.example.ui.theme.TextSecondary
import com.example.viewmodel.DrawerTab
import com.example.viewmodel.EditorUiState
import com.example.viewmodel.EditorViewModel

@Composable
fun EditorScreen(
    uiState: EditorUiState,
    viewModel: EditorViewModel,
    onBackToHome: () -> Unit,
    onGoToExport: () -> Unit,
    onGoToCloud: () -> Unit,
    modifier: Modifier = Modifier
) {
    val project = uiState.currentProject ?: return

    BackHandler {
        if (uiState.activeDrawer != DrawerTab.NONE) {
            viewModel.closeDrawer()
        } else {
            onBackToHome()
        }
    }

    var showAspectDialog by remember { mutableStateOf(false) }

    val selectedClip = remember(project, uiState.selectedClipId) {
        project.tracks.flatMap { it.clips }.find { it.id == uiState.selectedClipId }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(StudioDarkBg)
            .statusBarsPadding()
            .navigationBarsPadding()
    ) {
        // Top Studio Bar
        StudioHeaderBar(
            title = project.title,
            isSyncing = uiState.isCloudSyncing,
            onBack = onBackToHome,
            onCloudClick = onGoToCloud,
            onExportClick = onGoToExport
        )

        // Upper: Video Monitor Canvas
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(1.05f)
        ) {
            CanvasPreview(
                project = project,
                currentPositionMs = uiState.currentPositionMs,
                isPlaying = uiState.isPlaying,
                onTogglePlay = { viewModel.togglePlay() },
                onStepFrame = { viewModel.stepFrame(it) },
                onAspectRatioClick = { showAspectDialog = true },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Active Drawer Panel (animated slide)
        AnimatedVisibility(
            visible = uiState.activeDrawer != DrawerTab.NONE,
            enter = slideInVertically(initialOffsetY = { it }),
            exit = slideOutVertically(targetOffsetY = { it })
        ) {
            ToolDrawersSheet(
                activeTab = uiState.activeDrawer,
                isAiProcessing = uiState.isAiProcessing,
                aiStatusMessage = uiState.aiStatusMessage,
                colorGrade = project.colorGrade,
                clipSpeed = selectedClip?.speed ?: 1.0f,
                clipVolume = selectedClip?.volume ?: 1.0f,
                onClose = { viewModel.closeDrawer() },
                onUpdateColorGrade = { viewModel.updateColorGrade(it) },
                onApplyAiCaptions = { viewModel.generateAiAutoCaptions(it) },
                onApplySmartCut = { viewModel.applyAiSmartCut() },
                onApplyAiColorPrompt = { viewModel.applyPromptColorGrade(it) },
                onGenerateStoryboard = { viewModel.generateStoryboard(it) },
                onUpdateSpeed = { viewModel.updateClipSpeed(it) },
                onUpdateVolume = { viewModel.updateClipVolume(it) },
                onAddSubtitle = { text, style -> viewModel.addSubtitle(text, style) }
            )
        }

        // Lower: Multi-Track Timeline
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .weight(0.95f)
        ) {
            MultiTrackTimeline(
                project = project,
                currentPositionMs = uiState.currentPositionMs,
                selectedClipId = uiState.selectedClipId,
                selectedTrackId = uiState.selectedTrackId,
                onSeekTo = { viewModel.seekTo(it) },
                onSelectClip = { trackId, clipId -> viewModel.selectClip(trackId, clipId) },
                onSplitClip = { viewModel.splitClipAtPlayhead() },
                onDeleteClip = { viewModel.deleteSelectedClip() },
                onDuplicateClip = { viewModel.duplicateSelectedClip() },
                onOpenSpeed = { viewModel.openDrawer(DrawerTab.SPEED_RAMP) },
                onOpenAudio = { viewModel.openDrawer(DrawerTab.AUDIO) },
                modifier = Modifier.fillMaxSize()
            )
        }

        // Bottom Studio Dock (Tool categories)
        StudioBottomDock(
            activeDrawer = uiState.activeDrawer,
            onTabClick = { tab -> viewModel.openDrawer(tab) },
            onAspectClick = { showAspectDialog = true }
        )
    }

    if (showAspectDialog) {
        AspectRatioDialog(
            currentAspect = project.aspectRatio,
            onDismiss = { showAspectDialog = false },
            onSelect = {
                showAspectDialog = false
                viewModel.setAspectRatio(it)
            }
        )
    }
}

@Composable
private fun StudioHeaderBar(
    title: String,
    isSyncing: Boolean,
    onBack: () -> Unit,
    onCloudClick: () -> Unit,
    onExportClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(52.dp)
            .background(StudioSurface)
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            IconButton(
                onClick = onBack,
                modifier = Modifier.testTag("editor_back_button")
            ) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                    contentDescription = "Back",
                    tint = TextPrimary
                )
            }
            Text(
                text = title,
                color = TextPrimary,
                fontSize = 14.sp,
                fontWeight = FontWeight.Bold,
                maxLines = 1,
                modifier = Modifier.width(110.dp)
            )
        }

        Row(verticalAlignment = Alignment.CenterVertically) {
            // Cloud Sync Indicator
            IconButton(onClick = onCloudClick) {
                Icon(
                    imageVector = if (isSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                    contentDescription = "Cloud Status",
                    tint = if (isSyncing) StudioCyan else StudioGreen,
                    modifier = Modifier.size(20.dp)
                )
            }

            // Undo / Redo
            IconButton(onClick = { /* Undo simulated */ }) {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Undo,
                    contentDescription = "Undo",
                    tint = TextSecondary,
                    modifier = Modifier.size(18.dp)
                )
            }

            // High Speed Export Button
            Button(
                onClick = onExportClick,
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .testTag("export_studio_button")
            ) {
                Icon(imageVector = Icons.Default.FileUpload, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(4.dp))
                Text("Export", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun StudioBottomDock(
    activeDrawer: DrawerTab,
    onTabClick: (DrawerTab) -> Unit,
    onAspectClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(StudioSurface)
            .border(1.dp, StudioBorder)
            .padding(horizontal = 4.dp),
        horizontalArrangement = Arrangement.SpaceAround,
        verticalAlignment = Alignment.CenterVertically
    ) {
        DockItem(
            icon = Icons.Default.AutoAwesome,
            label = "AI Magic",
            isActive = activeDrawer == DrawerTab.AI_TOOLS,
            accentColor = StudioMagenta,
            onClick = { onTabClick(DrawerTab.AI_TOOLS) },
            testTag = "dock_ai_magic"
        )
        DockItem(
            icon = Icons.Default.Palette,
            label = "LUT / Filters",
            isActive = activeDrawer == DrawerTab.COLOR_LUT,
            accentColor = StudioCyan,
            onClick = { onTabClick(DrawerTab.COLOR_LUT) },
            testTag = "dock_color_lut"
        )
        DockItem(
            icon = Icons.Default.Subtitles,
            label = "Captions",
            isActive = activeDrawer == DrawerTab.TEXT_CAPTIONS,
            accentColor = StudioAmber,
            onClick = { onTabClick(DrawerTab.TEXT_CAPTIONS) },
            testTag = "dock_captions"
        )
        DockItem(
            icon = Icons.Default.GraphicEq,
            label = "Audio",
            isActive = activeDrawer == DrawerTab.AUDIO,
            accentColor = StudioGreen,
            onClick = { onTabClick(DrawerTab.AUDIO) },
            testTag = "dock_audio"
        )
        DockItem(
            icon = Icons.Default.Speed,
            label = "Speed",
            isActive = activeDrawer == DrawerTab.SPEED_RAMP,
            accentColor = StudioCyan,
            onClick = { onTabClick(DrawerTab.SPEED_RAMP) },
            testTag = "dock_speed"
        )
        DockItem(
            icon = Icons.Default.AspectRatio,
            label = "Format",
            isActive = false,
            accentColor = TextPrimary,
            onClick = onAspectClick,
            testTag = "dock_aspect"
        )
    }
}

@Composable
private fun DockItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    label: String,
    isActive: Boolean,
    accentColor: Color,
    onClick: () -> Unit,
    testTag: String
) {
    Column(
        modifier = Modifier
            .clip(RoundedCornerShape(8.dp))
            .clickable { onClick() }
            .padding(horizontal = 6.dp, vertical = 4.dp)
            .testTag(testTag),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(
            imageVector = icon,
            contentDescription = label,
            tint = if (isActive) accentColor else TextSecondary,
            modifier = Modifier.size(20.dp)
        )
        Spacer(modifier = Modifier.height(2.dp))
        Text(
            text = label,
            color = if (isActive) accentColor else TextSecondary,
            fontSize = 10.sp,
            fontWeight = if (isActive) FontWeight.Bold else FontWeight.Normal
        )
    }
}

@Composable
private fun AspectRatioDialog(
    currentAspect: AspectRatio,
    onDismiss: () -> Unit,
    onSelect: (AspectRatio) -> Unit
) {
    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Canvas Aspect Ratio", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                AspectRatio.values().forEach { aspect ->
                    val isSelected = currentAspect == aspect
                    Surface(
                        shape = RoundedCornerShape(8.dp),
                        color = if (isSelected) StudioCyan.copy(alpha = 0.2f) else StudioCardElevated,
                        modifier = Modifier
                            .fillMaxWidth()
                            .clickable { onSelect(aspect) }
                            .border(1.dp, if (isSelected) StudioCyan else StudioBorder, RoundedCornerShape(8.dp))
                    ) {
                        Row(
                            modifier = Modifier.padding(10.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = aspect.label,
                                color = if (isSelected) StudioCyan else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp,
                                modifier = Modifier.weight(1f)
                            )
                            Text(
                                text = aspect.iconDescription,
                                color = TextMuted,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text("Done", color = StudioCyan)
            }
        },
        containerColor = StudioSurface
    )
}
