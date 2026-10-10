package com.example.ui.screens

import androidx.activity.compose.BackHandler
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
import androidx.compose.material.icons.filled.Cloud
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.Computer
import androidx.compose.material.icons.filled.Devices
import androidx.compose.material.icons.filled.History
import androidx.compose.material.icons.filled.PhoneAndroid
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Tablet
import androidx.compose.material.icons.filled.VerifiedUser
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.SwitchDefaults
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
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
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

@Composable
fun CloudSyncScreen(
    usedGb: Float,
    totalGb: Float,
    isSyncing: Boolean,
    onTriggerSync: () -> Unit,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    var autoSyncWifi by remember { mutableStateOf(true) }
    var uploadRawProxies by remember { mutableStateOf(true) }
    var keepOfflineCache by remember { mutableStateOf(true) }

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
                    text = "Cloud Sync & Multi-Device",
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
                // Account & Plan Card
                Surface(
                    shape = RoundedCornerShape(14.dp),
                    color = StudioSurfaceVariant,
                    modifier = Modifier
                        .fillMaxWidth()
                        .border(1.dp, StudioBorder, RoundedCornerShape(14.dp))
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("CREATOR CLOUD PRO", color = StudioCyan, fontSize = 11.sp, fontWeight = FontWeight.Black)
                                Text("halwajalebi38@gmail.com", color = TextPrimary, fontSize = 15.sp, fontWeight = FontWeight.Bold)
                            }
                            Surface(
                                shape = RoundedCornerShape(12.dp),
                                color = StudioGreen.copy(alpha = 0.2f)
                            ) {
                                Row(
                                    modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Icon(imageVector = Icons.Default.VerifiedUser, contentDescription = null, tint = StudioGreen, modifier = Modifier.size(14.dp))
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Text("PRO ACTIVE", color = StudioGreen, fontSize = 10.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(14.dp))

                        // Storage Bar
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            Text("Cloud Storage Used", color = TextSecondary, fontSize = 12.sp)
                            Text("${String.format("%.1f", usedGb)} GB / ${totalGb.toInt()} GB", color = StudioCyan, fontSize = 12.sp, fontWeight = FontWeight.Bold)
                        }
                        Spacer(modifier = Modifier.height(6.dp))
                        LinearProgressIndicator(
                            progress = { (usedGb / totalGb).coerceIn(0f, 1f) },
                            color = StudioCyan,
                            trackColor = StudioDarkBg,
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(6.dp)
                                .clip(RoundedCornerShape(3.dp))
                        )

                        Spacer(modifier = Modifier.height(14.dp))

                        Button(
                            onClick = onTriggerSync,
                            enabled = !isSyncing,
                            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = Color.Black),
                            shape = RoundedCornerShape(10.dp),
                            modifier = Modifier
                                .fillMaxWidth()
                                .testTag("trigger_cloud_sync_btn")
                        ) {
                            if (isSyncing) {
                                CircularProgressIndicator(modifier = Modifier.size(16.dp), color = Color.Black, strokeWidth = 2.dp)
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Synchronizing Projects...", fontWeight = FontWeight.Bold)
                            } else {
                                Icon(imageVector = Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Sync All Projects Now", fontWeight = FontWeight.Bold)
                            }
                        }
                    }
                }

                // Connected Devices List
                Text("Connected Multi-Device Fleet", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    DeviceRowItem(
                        icon = Icons.Default.PhoneAndroid,
                        deviceName = "Google Pixel 9 Pro",
                        status = "Current Device • Active Now",
                        isCurrent = true
                    )
                    DeviceRowItem(
                        icon = Icons.Default.Computer,
                        deviceName = "Apple Mac Studio (M3 Ultra)",
                        status = "Desktop Studio • Synced 3 min ago",
                        isCurrent = false
                    )
                    DeviceRowItem(
                        icon = Icons.Default.Tablet,
                        deviceName = "iPad Pro 13-inch",
                        status = "Companion Editor • Synced 1 hour ago",
                        isCurrent = false
                    )
                }

                // Version History Snapshots
                Text("Recent Project Cloud Snapshots", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    SnapshotRowItem(version = "v4", desc = "Multi-track color grade & 4K master export", time = "Today 2:38 PM")
                    SnapshotRowItem(version = "v3", desc = "Added AI Auto-Captions with viral styling", time = "Today 1:12 PM")
                    SnapshotRowItem(version = "v2", desc = "Smart Cut applied (Removed 4s dead silence)", time = "Yesterday")
                }

                // Sync Preferences
                Text("Cloud Preferences", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                Surface(
                    shape = RoundedCornerShape(12.dp),
                    color = StudioSurfaceVariant,
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Column(modifier = Modifier.padding(12.dp), verticalArrangement = Arrangement.spacedBy(10.dp)) {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Auto-sync over Wi-Fi", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Seamless background synchronization", color = TextSecondary, fontSize = 10.sp)
                            }
                            Switch(
                                checked = autoSyncWifi,
                                onCheckedChange = { autoSyncWifi = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = StudioCyan, checkedTrackColor = StudioCyan.copy(alpha = 0.3f))
                            )
                        }

                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Column {
                                Text("Generate RAW Proxy Previews", color = TextPrimary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                                Text("Fast editing on mobile, full-res on desktop", color = TextSecondary, fontSize = 10.sp)
                            }
                            Switch(
                                checked = uploadRawProxies,
                                onCheckedChange = { uploadRawProxies = it },
                                colors = SwitchDefaults.colors(checkedThumbColor = StudioCyan, checkedTrackColor = StudioCyan.copy(alpha = 0.3f))
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
private fun DeviceRowItem(
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    deviceName: String,
    status: String,
    isCurrent: Boolean
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = StudioSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .border(1.dp, if (isCurrent) StudioCyan.copy(alpha = 0.5f) else StudioBorder, RoundedCornerShape(10.dp))
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Box(
                modifier = Modifier
                    .size(36.dp)
                    .clip(CircleShape)
                    .background(if (isCurrent) StudioCyan.copy(alpha = 0.2f) else StudioCardElevated),
                contentAlignment = Alignment.Center
            ) {
                Icon(imageVector = icon, contentDescription = null, tint = if (isCurrent) StudioCyan else TextSecondary, modifier = Modifier.size(20.dp))
            }
            Spacer(modifier = Modifier.width(12.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = deviceName, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                Text(text = status, color = if (isCurrent) StudioCyan else TextSecondary, fontSize = 11.sp)
            }
        }
    }
}

@Composable
private fun SnapshotRowItem(
    version: String,
    desc: String,
    time: String
) {
    Surface(
        shape = RoundedCornerShape(10.dp),
        color = StudioSurfaceVariant,
        modifier = Modifier.fillMaxWidth()
    ) {
        Row(
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            Surface(
                shape = RoundedCornerShape(6.dp),
                color = StudioCardElevated
            ) {
                Text(
                    text = version,
                    color = StudioCyan,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Black,
                    modifier = Modifier.padding(horizontal = 6.dp, vertical = 2.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column(modifier = Modifier.weight(1f)) {
                Text(text = desc, color = TextPrimary, fontWeight = FontWeight.SemiBold, fontSize = 12.sp)
                Text(text = time, color = TextMuted, fontSize = 10.sp)
            }
            Text("Restore", color = StudioCyan, fontWeight = FontWeight.Bold, fontSize = 11.sp, modifier = Modifier.clickable { /* Restore snapshot */ })
        }
    }
}
