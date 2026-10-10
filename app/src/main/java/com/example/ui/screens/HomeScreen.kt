package com.example.ui.screens

import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.AutoAwesome
import androidx.compose.material.icons.filled.CloudDone
import androidx.compose.material.icons.filled.CloudSync
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material.icons.filled.VideoLibrary
import androidx.compose.material.icons.filled.ViewCarousel
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.R
import com.example.model.AspectRatio
import com.example.model.CloudSyncStatus
import com.example.model.Project
import com.example.model.ScreenDestination
import com.example.ui.components.formatTimecode
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

@Composable
fun HomeScreen(
    projects: List<Project>,
    cloudUsedGb: Float,
    cloudTotalGb: Float,
    isCloudSyncing: Boolean,
    onOpenProject: (Project) -> Unit,
    onCreateNewProject: (String, AspectRatio) -> Unit,
    onImportVideosAndCreateProject: (List<Uri>) -> Unit,
    onDuplicateProject: (String) -> Unit,
    onDeleteProject: (String) -> Unit,
    onOpenCloudSync: () -> Unit,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var showNewProjectDialog by remember { mutableStateOf(false) }

    // CapCut-style Gallery Video Picker
    val videoPickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.PickMultipleVisualMedia()
    ) { uris ->
        if (uris.isNotEmpty()) {
            uris.forEach { uri ->
                try {
                    context.contentResolver.takePersistableUriPermission(
                        uri,
                        Intent.FLAG_GRANT_READ_URI_PERMISSION
                    )
                } catch (ignored: Exception) {}
            }
            onImportVideosAndCreateProject(uris)
        }
    }

    Scaffold(
        modifier = modifier.fillMaxSize(),
        containerColor = StudioDarkBg,
        floatingActionButton = {
            FloatingActionButton(
                onClick = {
                    videoPickerLauncher.launch(
                        PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                    )
                },
                containerColor = StudioCyan,
                contentColor = Color.Black,
                shape = RoundedCornerShape(16.dp),
                modifier = Modifier.testTag("fab_new_project")
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 16.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Add, contentDescription = "New Project")
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("New Project", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                }
            }
        }
    ) { paddingValues ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues),
            contentPadding = PaddingValues(bottom = 90.dp)
        ) {
            // Top App Bar
            item {
                HomeTopBar(
                    isCloudSyncing = isCloudSyncing,
                    onOpenCloudSync = onOpenCloudSync
                )
            }

            // CapCut-Style Main Action Hub
            item {
                CapCutImportActionHub(
                    onImportPhoneVideo = {
                        videoPickerLauncher.launch(
                            PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                        )
                    },
                    onBlankProject = { showNewProjectDialog = true }
                )
            }

            // Cloud Sync Quota Status Widget
            item {
                CloudSyncBannerWidget(
                    usedGb = cloudUsedGb,
                    totalGb = cloudTotalGb,
                    isSyncing = isCloudSyncing,
                    onClick = onOpenCloudSync
                )
            }

            // Trending Creator Templates
            item {
                SectionHeader(title = "Cinematic Sample Presets", icon = Icons.Default.ViewCarousel)
                TemplatesCarousel(
                    onSelectTemplate = { title, aspect ->
                        onCreateNewProject(title, aspect)
                    }
                )
            }

            // Recent Projects
            item {
                SectionHeader(title = "Your Projects (${projects.size})", icon = Icons.Default.VideoLibrary)
            }

            if (projects.isEmpty()) {
                item {
                    EmptyProjectsState(
                        onImportClick = {
                            videoPickerLauncher.launch(
                                PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.VideoOnly)
                            )
                        }
                    )
                }
            } else {
                items(projects, key = { it.id }) { project ->
                    ProjectCardItem(
                        project = project,
                        onClick = { onOpenProject(project) },
                        onDuplicate = { onDuplicateProject(project.id) },
                        onDelete = { onDeleteProject(project.id) }
                    )
                }
            }
        }
    }

    if (showNewProjectDialog) {
        NewProjectDialog(
            onDismiss = { showNewProjectDialog = false },
            onCreate = { title, aspect ->
                showNewProjectDialog = false
                onCreateNewProject(title, aspect)
            }
        )
    }
}

@Composable
private fun HomeTopBar(
    isCloudSyncing: Boolean,
    onOpenCloudSync: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Row(verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .size(38.dp)
                    .clip(RoundedCornerShape(10.dp))
                    .background(
                        Brush.linearGradient(
                            listOf(StudioCyan, StudioMagenta)
                        )
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Movie,
                    contentDescription = null,
                    tint = Color.Black,
                    modifier = Modifier.size(22.dp)
                )
            }
            Spacer(modifier = Modifier.width(10.dp))
            Column {
                Text(
                    text = "NovaCut AI",
                    color = TextPrimary,
                    fontSize = 19.sp,
                    fontWeight = FontWeight.Black
                )
                Text(
                    text = "PRO VIDEO STUDIO",
                    color = StudioCyan,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    letterSpacing = 1.sp
                )
            }
        }

        // Cloud sync status button
        Surface(
            shape = RoundedCornerShape(20.dp),
            color = StudioSurfaceVariant,
            modifier = Modifier
                .clickable { onOpenCloudSync() }
                .border(1.dp, StudioBorder, RoundedCornerShape(20.dp))
        ) {
            Row(
                modifier = Modifier.padding(horizontal = 10.dp, vertical = 5.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = if (isCloudSyncing) Icons.Default.CloudSync else Icons.Default.CloudDone,
                    contentDescription = "Cloud Status",
                    tint = if (isCloudSyncing) StudioAmber else StudioCyan,
                    modifier = Modifier.size(14.dp)
                )
                Spacer(modifier = Modifier.width(4.dp))
                Text(
                    text = if (isCloudSyncing) "Syncing..." else "Cloud Sync",
                    color = TextSecondary,
                    fontSize = 11.sp,
                    fontWeight = FontWeight.Medium
                )
            }
        }
    }
}

/**
 * CapCut-Style Hero Action Hub:
 * 1. Big glowing "+ New Project" card that opens Phone Gallery to pick real videos!
 * 2. Secondary "Blank Canvas" and "Preset" options.
 */
@Composable
private fun CapCutImportActionHub(
    onImportPhoneVideo: () -> Unit,
    onBlankProject: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp),
        verticalArrangement = Arrangement.spacedBy(10.dp)
    ) {
        // Giant "+ New Project" Card (CapCut style)
        Surface(
            shape = RoundedCornerShape(16.dp),
            color = StudioSurface,
            modifier = Modifier
                .fillMaxWidth()
                .clickable { onImportPhoneVideo() }
                .border(1.5.dp, StudioCyan, RoundedCornerShape(16.dp))
                .testTag("capcut_new_project_card")
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.radialGradient(
                            colors = listOf(
                                StudioCyan.copy(alpha = 0.22f),
                                StudioDarkBg
                            )
                        )
                    )
                    .padding(20.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Box(
                        modifier = Modifier
                            .size(54.dp)
                            .clip(CircleShape)
                            .background(StudioCyan),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Default.Add,
                            contentDescription = "New Project",
                            tint = Color.Black,
                            modifier = Modifier.size(34.dp)
                        )
                    }

                    Spacer(modifier = Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Text(
                                text = "New Project",
                                color = TextPrimary,
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Black
                            )
                            Spacer(modifier = Modifier.width(6.dp))
                            Surface(
                                shape = RoundedCornerShape(4.dp),
                                color = StudioMagenta
                            ) {
                                Text(
                                    text = "SELECT VIDEO",
                                    color = Color.White,
                                    fontSize = 8.sp,
                                    fontWeight = FontWeight.Black,
                                    modifier = Modifier.padding(horizontal = 4.dp, vertical = 1.dp)
                                )
                            }
                        }
                        Text(
                            text = "Tap to pick videos from your phone gallery to start editing immediately",
                            color = TextSecondary,
                            fontSize = 11.sp
                        )
                    }
                }
            }
        }

        // Quick secondary action pills
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.spacedBy(10.dp)
        ) {
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StudioSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onBlankProject() }
                    .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.AutoAwesome, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Blank Canvas", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("Custom aspect ratio", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }

            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StudioSurfaceVariant,
                modifier = Modifier
                    .weight(1f)
                    .clickable { onImportPhoneVideo() }
                    .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            ) {
                Row(
                    modifier = Modifier.padding(vertical = 12.dp, horizontal = 14.dp),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = StudioMagenta, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(8.dp))
                    Column {
                        Text("Import & Cut", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                        Text("From device storage", color = TextMuted, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun CloudSyncBannerWidget(
    usedGb: Float,
    totalGb: Float,
    isSyncing: Boolean,
    onClick: () -> Unit
) {
    Surface(
        shape = RoundedCornerShape(12.dp),
        color = StudioSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 4.dp)
            .clickable { onClick() }
            .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
    ) {
        Column(modifier = Modifier.padding(12.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(imageVector = Icons.Default.CloudSync, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(16.dp))
                    Spacer(modifier = Modifier.width(6.dp))
                    Text("Cloud Sync & Multi-Device Backup", color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                }
                Text("${usedGb} GB / ${totalGb} GB", color = TextSecondary, fontSize = 11.sp)
            }
            Spacer(modifier = Modifier.height(6.dp))
            LinearProgressIndicator(
                progress = { (usedGb / totalGb).coerceIn(0f, 1f) },
                color = StudioCyan,
                trackColor = StudioDarkBg,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(4.dp)
                    .clip(RoundedCornerShape(2.dp))
            )
        }
    }
}

@Composable
private fun SectionHeader(title: String, icon: androidx.compose.ui.graphics.vector.ImageVector) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(imageVector = icon, contentDescription = null, tint = StudioCyan, modifier = Modifier.size(16.dp))
        Spacer(modifier = Modifier.width(8.dp))
        Text(text = title, color = TextPrimary, fontSize = 14.sp, fontWeight = FontWeight.Bold)
    }
}

@Composable
private fun TemplatesCarousel(
    onSelectTemplate: (String, AspectRatio) -> Unit
) {
    val templates = listOf(
        Triple("Viral TikTok Hook", AspectRatio.ASPECT_9_16, R.drawable.sample_thumb_cyber_1791582067290),
        Triple("YouTube Cinematic Vlog", AspectRatio.ASPECT_16_9, R.drawable.sample_thumb_sunset_1791582093769),
        Triple("Instagram Aesthetic 4:5", AspectRatio.ASPECT_4_5, R.drawable.sample_thumb_cyber_1791582067290)
    )

    LazyRow(
        contentPadding = PaddingValues(horizontal = 16.dp),
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items(templates) { (title, aspect, thumbRes) ->
            Surface(
                shape = RoundedCornerShape(12.dp),
                color = StudioSurfaceVariant,
                modifier = Modifier
                    .width(180.dp)
                    .clickable { onSelectTemplate(title, aspect) }
                    .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            ) {
                Column {
                    Image(
                        painter = painterResource(id = thumbRes),
                        contentDescription = title,
                        contentScale = ContentScale.Crop,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(100.dp)
                    )
                    Column(modifier = Modifier.padding(8.dp)) {
                        Text(text = title, color = TextPrimary, fontWeight = FontWeight.Bold, fontSize = 12.sp, maxLines = 1)
                        Text(text = aspect.label, color = StudioCyan, fontSize = 10.sp)
                    }
                }
            }
        }
    }
}

@Composable
private fun ProjectCardItem(
    project: Project,
    onClick: () -> Unit,
    onDuplicate: () -> Unit,
    onDelete: () -> Unit
) {
    var showMenu by remember { mutableStateOf(false) }

    Surface(
        shape = RoundedCornerShape(12.dp),
        color = StudioSurfaceVariant,
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 6.dp)
            .clickable { onClick() }
            .border(1.dp, StudioBorder, RoundedCornerShape(12.dp))
            .testTag("project_item_${project.id}")
    ) {
        Row(
            modifier = Modifier.padding(10.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Thumbnail
            Box(
                modifier = Modifier
                    .size(72.dp, 48.dp)
                    .clip(RoundedCornerShape(6.dp))
                    .background(Color.Black),
                contentAlignment = Alignment.Center
            ) {
                val coverRes = project.coverDrawableRes ?: R.drawable.sample_thumb_cyber_1791582067290
                Image(
                    painter = painterResource(id = coverRes),
                    contentDescription = null,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier.fillMaxSize()
                )
                Text(
                    text = formatTimecode(project.totalDurationMs),
                    color = Color.White,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier
                        .align(Alignment.BottomEnd)
                        .padding(2.dp)
                        .background(Color.Black.copy(alpha = 0.6f))
                )
            }

            Spacer(modifier = Modifier.width(12.dp))

            // Project Info
            Column(modifier = Modifier.weight(1f)) {
                Text(
                    text = project.title,
                    color = TextPrimary,
                    fontWeight = FontWeight.Bold,
                    fontSize = 13.sp,
                    maxLines = 1
                )
                Spacer(modifier = Modifier.height(2.dp))
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Text(text = project.aspectRatio.label.takeWhile { it != ' ' }, color = StudioCyan, fontSize = 10.sp, fontWeight = FontWeight.SemiBold)
                    Text(text = " • ${project.tracks.sumOf { it.clips.size }} clips", color = TextSecondary, fontSize = 10.sp)
                }
            }

            // Options menu
            Box {
                IconButton(onClick = { showMenu = true }) {
                    Icon(imageVector = Icons.Default.MoreVert, contentDescription = "Options", tint = TextMuted)
                }
                DropdownMenu(
                    expanded = showMenu,
                    onDismissRequest = { showMenu = false },
                    modifier = Modifier.background(StudioSurface)
                ) {
                    DropdownMenuItem(
                        text = { Text("Duplicate Project", color = TextPrimary) },
                        onClick = {
                            showMenu = false
                            onDuplicate()
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Delete Project", color = StudioMagenta) },
                        onClick = {
                            showMenu = false
                            onDelete()
                        }
                    )
                }
            }
        }
    }
}

@Composable
private fun EmptyProjectsState(onImportClick: () -> Unit) {
    Column(
        modifier = Modifier
            .fillMaxWidth()
            .padding(32.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Icon(imageVector = Icons.Default.Movie, contentDescription = null, tint = TextMuted, modifier = Modifier.size(48.dp))
        Spacer(modifier = Modifier.height(10.dp))
        Text("No Projects Found", color = TextPrimary, fontWeight = FontWeight.Bold)
        Text("Select any video from your phone to start editing like CapCut", color = TextSecondary, fontSize = 12.sp)
        Spacer(modifier = Modifier.height(16.dp))
        Button(
            onClick = onImportClick,
            colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = Color.Black)
        ) {
            Icon(imageVector = Icons.Default.Add, contentDescription = null, modifier = Modifier.size(16.dp))
            Spacer(modifier = Modifier.width(6.dp))
            Text("Pick Video from Phone", fontWeight = FontWeight.Bold)
        }
    }
}

@Composable
private fun NewProjectDialog(
    onDismiss: () -> Unit,
    onCreate: (String, AspectRatio) -> Unit
) {
    var title by remember { mutableStateOf("New Studio Project") }
    var selectedAspect by remember { mutableStateOf(AspectRatio.ASPECT_9_16) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Blank Project", color = TextPrimary, fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                OutlinedTextField(
                    value = title,
                    onValueChange = { title = it },
                    label = { Text("Project Title") },
                    colors = OutlinedTextFieldDefaults.colors(
                        focusedBorderColor = StudioCyan,
                        unfocusedBorderColor = StudioBorder,
                        focusedTextColor = TextPrimary,
                        unfocusedTextColor = TextPrimary
                    ),
                    modifier = Modifier.fillMaxWidth()
                )

                Text("Aspect Ratio Format", color = TextSecondary, fontSize = 12.sp, fontWeight = FontWeight.SemiBold)
                Column(verticalArrangement = Arrangement.spacedBy(6.dp)) {
                    AspectRatio.values().forEach { aspect ->
                        val isSelected = selectedAspect == aspect
                        Surface(
                            shape = RoundedCornerShape(8.dp),
                            color = if (isSelected) StudioCyan.copy(alpha = 0.2f) else StudioCardElevated,
                            modifier = Modifier
                                .fillMaxWidth()
                                .clickable { selectedAspect = aspect }
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
                                    fontSize = 12.sp,
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
            }
        },
        confirmButton = {
            Button(
                onClick = { onCreate(title.ifBlank { "Untitled Project" }, selectedAspect) },
                colors = ButtonDefaults.buttonColors(containerColor = StudioCyan, contentColor = Color.Black)
            ) {
                Text("Create & Edit", fontWeight = FontWeight.Bold)
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel", color = TextSecondary)
            }
        },
        containerColor = StudioSurface
    )
}
