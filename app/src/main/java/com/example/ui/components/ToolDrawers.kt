package com.example.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.GraphicEq
import androidx.compose.material.icons.filled.Hearing
import androidx.compose.material.icons.filled.Palette
import androidx.compose.material.icons.filled.Psychology
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material.icons.filled.Subtitles
import androidx.compose.material.icons.filled.TextFields
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Slider
import androidx.compose.material3.SliderDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.model.ColorGradeSettings
import com.example.model.LutPreset
import com.example.model.SubtitleStyle
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

@Composable
fun ToolDrawersSheet(
    activeTab: DrawerTab,
    isAiProcessing: Boolean,
    aiStatusMessage: String,
    colorGrade: ColorGradeSettings,
    clipSpeed: Float,
    clipVolume: Float,
    onClose: () -> Unit,
    onUpdateColorGrade: (ColorGradeSettings) -> Unit,
    onApplyAiCaptions: (SubtitleStyle) -> Unit,
    onApplySmartCut: () -> Unit,
    onApplyAiColorPrompt: (String) -> Unit,
    onGenerateStoryboard: (String) -> Unit,
    onUpdateSpeed: (Float) -> Unit,
    onUpdateVolume: (Float) -> Unit,
    onAddSubtitle: (String, SubtitleStyle) -> Unit,
    modifier: Modifier = Modifier
) {
    if (activeTab == DrawerTab.NONE) return

    Surface(
        shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp),
        color = StudioSurfaceVariant,
        shadowElevation = 8.dp,
        modifier = modifier
            .fillMaxWidth()
            .border(1.dp, StudioBorder, RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp))
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp)
        ) {
            // Header
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    val (icon, title, color) = when (activeTab) {
                        DrawerTab.AI_TOOLS -> Triple(Icons.Default.AutoAwesome, "AI Magic Suite", StudioMagenta)
                        DrawerTab.COLOR_LUT -> Triple(Icons.Default.Palette, "Color Grading & LUTs", StudioCyan)
                        DrawerTab.TEXT_CAPTIONS -> Triple(Icons.Default.Subtitles, "Auto-Captions & Subtitles", StudioAmber)
                        DrawerTab.AUDIO -> Triple(Icons.Default.GraphicEq, "Audio & Voice AI", StudioGreen)
                        DrawerTab.SPEED_RAMP -> Triple(Icons.Default.Speed, "Speed Ramping & Curve", StudioCyan)
                        else -> Triple(Icons.Default.AutoAwesome, "Tools", StudioCyan)
                    }

                    Box(
                        modifier = Modifier
                            .size(28.dp)
                            .clip(CircleShape)
                            .background(color.copy(alpha = 0.2f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(imageVector = icon, contentDescription = null, tint = color, modifier = Modifier.size(16.dp))
                    }
                    Spacer(modifier = Modifier.width(8.dp))
                    Text(
                        text = title,
                        color = TextPrimary,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Bold
                    )
                }

                IconButton(
                    onClick = onClose,
                    modifier = Modifier.size(28.dp)
                ) {
                    Icon(imageVector = Icons.Default.Close, contentDescription = "Close", tint = TextSecondary)
                }
            }

            Spacer(modifier = Modifier.height(10.dp))

            // Loading status banner
            if (isAiProcessing) {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .background(StudioCardElevated)
                        .padding(10.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(20.dp),
                        strokeWidth = 2.dp,
                        color = StudioCyan
                    )
                    Spacer(modifier = Modifier.width(10.dp))
                    Text(
                        text = aiStatusMessage,
                        color = StudioCyan,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.Medium
                    )
                }
                Spacer(modifier = Modifier.height(10.dp))
            }

            when (activeTab) {
                DrawerTab.AI_TOOLS -> AiToolsPanel(
                    onApplyAiCaptions = onApplyAiCaptions,
                    onApplySmartCut = onApplySmartCut,
                    onApplyColorPrompt = onApplyAiColorPrompt,
                    onGenerateStoryboard = onGenerateStoryboard
                )
                DrawerTab.COLOR_LUT -> ColorLutPanel(
                    settings = colorGrade,
                    onUpdate = onUpdateColorGrade
                )
                DrawerTab.SPEED_RAMP -> SpeedRampPanel(
                    currentSpeed = clipSpeed,
                    onSpeedChange = onUpdateSpeed
                )
                DrawerTab.AUDIO -> AudioMixPanel(
                    currentVolume = clipVolume,
                    onVolumeChange = onUpdateVolume
                )
                DrawerTab.TEXT_CAPTIONS -> CaptionsPanel(
                    onApplyAiCaptions = onApplyAiCaptions,
                    onAddCustomSubtitle = onAddSubtitle
                )
                else -> Unit
            }
        }
    }
}

@Composable
private fun AiToolsPanel(
    onApplyAiCaptions: (SubtitleStyle) -> Unit,
    onApplySmartCut: () -> Unit,
    onApplyColorPrompt: (String) -> Unit,
    onGenerateStoryboard: (String) -> Unit
) {
    var promptText by remember { mutableStateOf("") }
    var topicText by remember { mutableStateOf("") }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        // Quick 1-Tap AI Actions
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = StudioCardElevated,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onApplyAiCaptions(SubtitleStyle.VIRAL_YELLOW) }
                    .border(1.dp, StudioMagenta.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .testTag("ai_auto_caption_card")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("✨ Auto-Captions", color = StudioMagenta, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Viral TikTok word sync", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Surface(
                shape = RoundedCornerShape(10.dp),
                color = StudioCardElevated,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onApplySmartCut() }
                    .border(1.dp, StudioCyan.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
                    .testTag("ai_smart_cut_card")
            ) {
                Column(modifier = Modifier.padding(10.dp)) {
                    Text("⚡ Smart Cut", color = StudioCyan, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                    Text("Remove silent pauses", color = TextSecondary, fontSize = 11.sp)
                }
            }
        }

        // AI Prompt Color Grading
        Text("AI Natural Language Color Grade", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = promptText,
                onValueChange = { promptText = it },
                placeholder = { Text("e.g. Cyberpunk neon rain, Warm sunset", fontSize = 11.sp, color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StudioCyan,
                    unfocusedBorderColor = StudioBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_color_prompt_input"),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (promptText.isNotBlank()) onApplyColorPrompt(promptText)
                    else onApplyColorPrompt("Cyberpunk neon")
                },
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("apply_color_prompt_btn")
            ) {
                Text("Grade", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }

        // AI Storyboard Generator
        Text("AI Script to Storyboard (Topic)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically
        ) {
            OutlinedTextField(
                value = topicText,
                onValueChange = { topicText = it },
                placeholder = { Text("e.g. 5 Productivity Hacks, Tokyo Travel", fontSize = 11.sp, color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StudioMagenta,
                    unfocusedBorderColor = StudioBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier
                    .weight(1f)
                    .testTag("ai_storyboard_topic_input"),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(8.dp))
            Button(
                onClick = {
                    if (topicText.isNotBlank()) onGenerateStoryboard(topicText)
                    else onGenerateStoryboard("Viral Reel Hook")
                },
                colors = ButtonDefaults.buttonColors(containerColor = StudioMagenta, contentColor = Color.White),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.testTag("generate_storyboard_btn")
            ) {
                Text("Generate", fontWeight = FontWeight.Bold, fontSize = 12.sp)
            }
        }
    }
}

@Composable
private fun ColorLutPanel(
    settings: ColorGradeSettings,
    onUpdate: (ColorGradeSettings) -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        // LUT selector row
        LazyRow(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
            items(LutPreset.values()) { lut ->
                val isSelected = settings.lut == lut
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) StudioCyan else StudioCardElevated,
                    modifier = Modifier
                        .clickable { onUpdate(settings.copy(lut = lut)) }
                        .border(1.dp, if (isSelected) StudioCyan else StudioBorder, RoundedCornerShape(8.dp))
                        .testTag("lut_${lut.name.lowercase()}")
                ) {
                    Column(
                        modifier = Modifier.padding(horizontal = 10.dp, vertical = 6.dp),
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Text(
                            text = lut.displayName,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                        Text(
                            text = lut.tag,
                            color = if (isSelected) Color.Black.copy(alpha = 0.7f) else TextSecondary,
                            fontSize = 9.sp
                        )
                    }
                }
            }
        }

        // Contrast Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Contrast (${String.format("%.1f", settings.contrast)}x)", color = TextSecondary, fontSize = 11.sp)
            Slider(
                value = settings.contrast,
                onValueChange = { onUpdate(settings.copy(contrast = it)) },
                valueRange = 0.5f..2.0f,
                modifier = Modifier.width(180.dp),
                colors = SliderDefaults.colors(thumbColor = StudioCyan, activeTrackColor = StudioCyan)
            )
        }

        // Saturation Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Saturation (${String.format("%.1f", settings.saturation)}x)", color = TextSecondary, fontSize = 11.sp)
            Slider(
                value = settings.saturation,
                onValueChange = { onUpdate(settings.copy(saturation = it)) },
                valueRange = 0.0f..2.0f,
                modifier = Modifier.width(180.dp),
                colors = SliderDefaults.colors(thumbColor = StudioCyan, activeTrackColor = StudioCyan)
            )
        }

        // Vignette Slider
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Vignette (${(settings.vignette * 100).toInt()}%)", color = TextSecondary, fontSize = 11.sp)
            Slider(
                value = settings.vignette,
                onValueChange = { onUpdate(settings.copy(vignette = it)) },
                valueRange = 0.0f..1.0f,
                modifier = Modifier.width(180.dp),
                colors = SliderDefaults.colors(thumbColor = StudioMagenta, activeTrackColor = StudioMagenta)
            )
        }
    }
}

@Composable
private fun SpeedRampPanel(
    currentSpeed: Float,
    onSpeedChange: (Float) -> Unit
) {
    val presets = listOf(0.25f, 0.5f, 1.0f, 1.5f, 2.0f, 4.0f)
    var aiInterpolation by remember { mutableStateOf(true) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Text("Playback Speed: ${currentSpeed}x", color = StudioCyan, fontWeight = FontWeight.Bold, fontSize = 14.sp)

        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            presets.forEach { speed ->
                val isSelected = currentSpeed == speed
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) StudioCyan else StudioCardElevated,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { onSpeedChange(speed) }
                        .border(1.dp, if (isSelected) StudioCyan else StudioBorder, RoundedCornerShape(8.dp))
                ) {
                    Box(modifier = Modifier.padding(vertical = 8.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = "${speed}x",
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontWeight = FontWeight.Bold,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(StudioCardElevated)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("AI Frame Interpolation", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("Ultra-smooth 120fps slow-motion without jitter", color = TextSecondary, fontSize = 10.sp)
            }
            Switch(
                checked = aiInterpolation,
                onCheckedChange = { aiInterpolation = it },
                colors = SwitchDefaults.colors(checkedThumbColor = StudioCyan, checkedTrackColor = StudioCyan.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
private fun AudioMixPanel(
    currentVolume: Float,
    onVolumeChange: (Float) -> Unit
) {
    var aiNoiseReduction by remember { mutableStateOf(true) }
    var autoDucking by remember { mutableStateOf(false) }

    Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text("Volume: ${(currentVolume * 100).toInt()}%", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
            Slider(
                value = currentVolume,
                onValueChange = onVolumeChange,
                valueRange = 0f..2f,
                modifier = Modifier.width(200.dp),
                colors = SliderDefaults.colors(thumbColor = StudioGreen, activeTrackColor = StudioGreen)
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(StudioCardElevated)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Studio Voice Denoise (AI)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("Removes hum, fan, and room reverb", color = TextSecondary, fontSize = 10.sp)
            }
            Switch(
                checked = aiNoiseReduction,
                onCheckedChange = { aiNoiseReduction = it },
                colors = SwitchDefaults.colors(checkedThumbColor = StudioGreen, checkedTrackColor = StudioGreen.copy(alpha = 0.3f))
            )
        }

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(8.dp))
                .background(StudioCardElevated)
                .padding(horizontal = 10.dp, vertical = 6.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text("Auto-Ducking", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text("Automatically lowers BGM during speech", color = TextSecondary, fontSize = 10.sp)
            }
            Switch(
                checked = autoDucking,
                onCheckedChange = { autoDucking = it },
                colors = SwitchDefaults.colors(checkedThumbColor = StudioGreen, checkedTrackColor = StudioGreen.copy(alpha = 0.3f))
            )
        }
    }
}

@Composable
private fun CaptionsPanel(
    onApplyAiCaptions: (SubtitleStyle) -> Unit,
    onAddCustomSubtitle: (String, SubtitleStyle) -> Unit
) {
    var newCaptionText by remember { mutableStateOf("") }
    var selectedStyle by remember { mutableStateOf(SubtitleStyle.VIRAL_YELLOW) }

    Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
        Text("Caption Typography Preset", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(6.dp)
        ) {
            SubtitleStyle.values().forEach { style ->
                val isSelected = selectedStyle == style
                Surface(
                    shape = RoundedCornerShape(8.dp),
                    color = if (isSelected) Color(style.fontColorHex) else StudioCardElevated,
                    modifier = Modifier
                        .weight(1f)
                        .clickable { selectedStyle = style }
                        .border(1.dp, StudioBorder, RoundedCornerShape(8.dp))
                ) {
                    Box(modifier = Modifier.padding(vertical = 6.dp), contentAlignment = Alignment.Center) {
                        Text(
                            text = style.title,
                            color = if (isSelected) Color.Black else TextPrimary,
                            fontSize = 10.sp,
                            fontWeight = FontWeight.Bold,
                            maxLines = 1
                        )
                    }
                }
            }
        }

        Button(
            onClick = { onApplyAiCaptions(selectedStyle) },
            colors = ButtonDefaults.buttonColors(containerColor = StudioAmber, contentColor = Color.Black),
            shape = RoundedCornerShape(8.dp),
            modifier = Modifier
                .fillMaxWidth()
                .testTag("generate_auto_captions_btn")
        ) {
            Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Generate All Auto-Captions with AI", fontWeight = FontWeight.Bold, fontSize = 12.sp)
        }

        Text("Or Add Manual Text Overlay", color = TextSecondary, fontSize = 11.sp)
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            OutlinedTextField(
                value = newCaptionText,
                onValueChange = { newCaptionText = it },
                placeholder = { Text("Enter caption text...", fontSize = 11.sp, color = TextMuted) },
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = StudioAmber,
                    unfocusedBorderColor = StudioBorder,
                    focusedTextColor = TextPrimary,
                    unfocusedTextColor = TextPrimary
                ),
                shape = RoundedCornerShape(8.dp),
                modifier = Modifier.weight(1f),
                singleLine = true
            )
            Spacer(modifier = Modifier.width(6.dp))
            Button(
                onClick = {
                    if (newCaptionText.isNotBlank()) {
                        onAddCustomSubtitle(newCaptionText, selectedStyle)
                        newCaptionText = ""
                    }
                },
                colors = ButtonDefaults.buttonColors(containerColor = StudioSurfaceVariant),
                shape = RoundedCornerShape(8.dp)
            ) {
                Text("Add", color = TextPrimary, fontSize = 12.sp)
            }
        }
    }
}
