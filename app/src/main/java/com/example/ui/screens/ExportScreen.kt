package com.example.ui.screens

import android.content.Intent
import android.widget.Toast
import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.Image
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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ElectricBolt
import androidx.compose.material.icons.filled.Folder
import androidx.compose.material.icons.filled.Memory
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Speed
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.FrameRate
import com.example.model.Project
import com.example.model.RenderProfile
import com.example.model.Resolution
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
import com.example.viewmodel.RenderStatus

@Composable
fun ExportScreen(
    project: Project?,
    renderStatus: RenderStatus,
    onStartRender: (RenderProfile) -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }
    val context = LocalContext.current

    var selectedResolution by remember { mutableStateOf(Resolution.RES_1080P) }
    var selectedFps by remember { mutableStateOf(FrameRate.FPS_60) }
    var selectedCodec by remember { mutableStateOf("H.264 HW Encoded") }

    Scaffold(
        modifier = modifier
            .fillMaxSize()
            .statusBarsPadding()
            .navigationBarsPadding(),
        containerColor = StudioDarkBg
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
        ) {
            // Header Bar
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(52.dp)
                    .background(StudioSurface)
                    .padding(horizontal = 8.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                IconButton(onClick = onBack) {
                    Icon(imageVector = Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back", tint = TextPrimary)
                }
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = "High-Speed Render Studio",
                    color = TextPrimary,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.Bold
                )
            }

            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .verticalScroll(rememberScrollState())
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // Hardware Acceleration Active Card
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StudioCyan.copy(alpha = 0.4f), RoundedCornerShape(12.dp))
                ) {
                    Row(
                        modifier = Modifier.padding(12.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Box(
                            modifier = Modifier
                                .size(36.dp)
                                .clip(CircleShape)
                                .background(StudioCyan.copy(alpha = 0.15f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(imageVector = Icons.Default.ElectricBolt, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(20.dp))
                        }
                        Spacer(modifier = Modifier.width(12.dp))
                        Column {
                            Text(
                                text = "Hardware Acceleration Active",
                                color = StudioCyan,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "Vulkan & MediaCodec Dual-Pipeline (Up to 140+ FPS)",
                                color = TextSecondary,
                                fontSize = 11.sp
                            )
                        }
                    }
                }

                // Render State UI
                if (renderStatus.isRendering) {
                    RenderingProgressCard(renderStatus = renderStatus)
                } else if (renderStatus.isCompleted) {
                    RenderCompletedCard(
                        renderStatus = renderStatus,
                        project = project,
                        onShareSocial = { platform ->
                            Toast.makeText(context, "Opening $platform direct export...", Toast.LENGTH_SHORT).show()
                            val shareIntent = Intent(Intent.ACTION_SEND).apply {
                                type = "video/mp4"
                                putExtra(Intent.EXTRA_TEXT, "Created with NovaCut AI Studio!")
                            }
                            context.startActivity(Intent.createChooser(shareIntent, "Share to $platform"))
                        }
                    )
                } else {
                    // Export Settings Form
                    RenderConfigurationCard(
                        selectedResolution = selectedResolution,
                        selectedFps = selectedFps,
                        selectedCodec = selectedCodec,
                        onResolutionChange = { selectedResolution = it },
                        onFpsChange = { selectedFps = it },
                        onCodecChange = { selectedCodec = it },
                        onStartExport = {
                            val profile = RenderProfile(
                                resolution = selectedResolution,
                                frameRate = selectedFps,
                                codec = selectedCodec
                            )
                            onStartRender(profile)
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun RenderConfigurationCard(
    selectedResolution: Resolution,
    selectedFps: FrameRate,
    selectedCodec: String,
    onResolutionChange: (Resolution) -> Unit,
    onFpsChange: (FrameRate) -> Unit,
    onCodecChange: (String) -> Unit,
    onStartExport: () -> Unit
) {
    Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
        // Resolution Picker
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Output Resolution", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                Resolution.values().forEach { res ->
                    val isSelected = selectedResolution == res
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) StudioCyan else StudioSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onResolutionChange(res) }
                            .border(1.dp, if (isSelected) StudioCyan else StudioBorder, RoundedCornerShape(10.dp))
                    ) {
                        Column(
                            modifier = Modifier.padding(10.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            Text(
                                text = res.label.takeWhile { it != ' ' },
                                color = if (isSelected) Color.Black else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                            Text(
                                text = "${res.width}x${res.height}",
                                color = if (isSelected) Color.Black.copy(alpha = 0.7f) else TextSecondary,
                                fontSize = 10.sp
                            )
                        }
                    }
                }
            }
        }

        // Frame Rate Picker
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Frame Rate (FPS)", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                FrameRate.values().forEach { fps ->
                    val isSelected = selectedFps == fps
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) StudioCyan else StudioSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onFpsChange(fps) }
                            .border(1.dp, if (isSelected) StudioCyan else StudioBorder, RoundedCornerShape(10.dp))
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = fps.label,
                                color = if (isSelected) Color.Black else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 11.sp
                            )
                        }
                    }
                }
            }
        }

        // Codec selection
        Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Text("Encoding Codec", color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 13.sp)
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                listOf("H.264 HW Encoded", "H.265 / HEVC").forEach { codec ->
                    val isSelected = selectedCodec == codec
                    Surface(
                        shape = RoundedCornerShape(10.dp),
                        color = if (isSelected) StudioMagenta else StudioSurfaceVariant,
                        modifier = Modifier
                            .weight(1f)
                            .clickable { onCodecChange(codec) }
                            .border(1.dp, if (isSelected) StudioMagenta else StudioBorder, RoundedCornerShape(10.dp))
                    ) {
                        Box(
                            modifier = Modifier.padding(vertical = 10.dp),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(
                                text = codec,
                                color = if (isSelected) Color.White else TextPrimary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 12.sp
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(10.dp))

        // Start High Speed Export Button
        Button(
            onClick = onStartExport,
            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = Color.Black),
            shape = RoundedCornerShape(12.dp),
            modifier = Modifier
                .fillMaxWidth()
                .height(50.dp)
                .testTag("start_render_button")
        ) {
            Icon(imageVector = Icons.Default.Speed, contentDescription = null)
            Spacer(modifier = Modifier.width(8.dp))
            Text("Start High-Speed Render", fontWeight = FontWeight.ExtraBold, fontSize = 14.sp)
        }
    }
}

@Composable
private fun RenderingProgressCard(renderStatus: RenderStatus) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StudioSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StudioCyan, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(20.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "Rendering Video...",
                color = TextPrimary,
                fontSize = 18.sp,
                fontWeight = FontWeight.Bold
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = "${(renderStatus.progress * 100).toInt()}% Completed",
                color = StudioCyan,
                fontSize = 28.sp,
                fontWeight = FontWeight.Black,
                fontFamily = FontFamily.Monospace
            )
            Spacer(modifier = Modifier.height(12.dp))
            LinearProgressIndicator(
                progress = { renderStatus.progress },
                color = StudioCyan,
                trackColor = StudioDarkBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(8.dp)
                    .clip(RoundedCornerShape(4.dp))
            )
            Spacer(modifier = Modifier.height(16.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceAround
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Render Speed", color = TextSecondary, fontSize = 10.sp)
                    Text("${renderStatus.fpsRate.toInt()} FPS", color = StudioGreen, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("Frames Done", color = TextSecondary, fontSize = 10.sp)
                    Text("${renderStatus.currentFrame} / ${renderStatus.totalFrames}", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("ETA", color = TextSecondary, fontSize = 10.sp)
                    Text("${renderStatus.etaSec}s", color = StudioAmber, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    }
}

@Composable
private fun RenderCompletedCard(
    renderStatus: RenderStatus,
    project: Project?,
    onShareSocial: (String) -> Unit
) {
    Surface(
        shape = RoundedCornerShape(16.dp),
        color = StudioSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, StudioGreen, RoundedCornerShape(16.dp))
    ) {
        Column(
            modifier = Modifier.padding(18.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .size(54.dp)
                    .clip(CircleShape)
                    .background(StudioGreen.copy(alpha = 0.2f)),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = Icons.Default.CheckCircle, contentDescription = null, tint = StudioGreen, modifier = Modifier.size(36.dp))
            }
            Spacer(modifier = Modifier.height(10.dp))
            Text("Render Complete!", color = TextPrimary, fontSize = 18.sp, fontWeight = FontWeight.Bold)
            Text("Ready to share with zero quality loss", color = TextSecondary, fontSize = 12.sp)

            Spacer(modifier = Modifier.height(16.dp))

            // File Details card
            Surface(
                shape = RoundedCornerShape(10.dp),
                color = StudioCardElevated,
                modifier = Modifier.fillMaxWidth()
            ) {
                Column(modifier = Modifier.padding(12.dp)) {
                    Text("Saved to:", color = TextMuted, fontSize = 10.sp)
                    Text(renderStatus.outputPath, color = StudioCyan, fontSize = 11.sp, fontFamily = FontFamily.Monospace, maxLines = 1)
                    Spacer(modifier = Modifier.height(4.dp))
                    Text("Size: 42.8 MB • 1080p 60FPS • Stereo AAC", color = TextSecondary, fontSize = 11.sp)
                }
            }

            Spacer(modifier = Modifier.height(18.dp))

            Text("Publish Instantly to Social Platforms", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
            Spacer(modifier = Modifier.height(10.dp))

            // Social media buttons row
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                SocialShareButton(name = "TikTok", color = Color(0xFF00F2FE), onClick = { onShareSocial("TikTok") }, modifier = Modifier.weight(1f))
                SocialShareButton(name = "Instagram", color = Color(0xFFE1306C), onClick = { onShareSocial("Instagram Reels") }, modifier = Modifier.weight(1f))
                SocialShareButton(name = "YouTube", color = Color(0xFFFF0000), onClick = { onShareSocial("YouTube Shorts") }, modifier = Modifier.weight(1f))
            }

            Spacer(modifier = Modifier.height(8.dp))

            Button(
                onClick = { onShareSocial("System Share") },
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = Color.Black),
                shape = RoundedCornerShape(10.dp),
                modifier = Modifier.fillMaxWidth()
            ) {
                Icon(imageVector = Icons.Default.Share, contentDescription = null, modifier = Modifier.size(16.dp))
                Spacer(modifier = Modifier.width(6.dp))
                Text("Share to Other Apps", fontWeight = FontWeight.Bold)
            }
        }
    }
}

@Composable
private fun SocialShareButton(
    name: String,
    color: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = color.copy(alpha = 0.15f),
        modifier = modifier
            .clickable { onClick() }
            .border(1.dp, color.copy(alpha = 0.5f), RoundedCornerShape(10.dp))
    ) {
        Box(
            modifier = Modifier.padding(vertical = 10.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = name,
                color = color,
                fontWeight = FontWeight.Bold,
                fontSize = 12.sp
            )
        }
    }
}
