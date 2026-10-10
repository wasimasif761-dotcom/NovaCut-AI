package com.example.viewmodel

import android.content.Context
import android.net.Uri
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.ai.NovaAiEngine
import com.example.ai.StoryboardScene
import com.example.data.ProjectRepository
import com.example.media.RealVideoEngine
import com.example.model.AspectRatio
import com.example.model.CloudSyncStatus
import com.example.model.ColorGradeSettings
import com.example.model.LutPreset
import com.example.model.MediaClip
import com.example.model.Project
import com.example.model.RenderProfile
import com.example.model.ScreenDestination
import com.example.model.SubtitleItem
import com.example.model.SubtitleStyle
import com.example.model.TimelineTrack
import com.example.model.TrackType
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import java.util.UUID

enum class DrawerTab {
    NONE,
    AI_TOOLS,
    COLOR_LUT,
    TEXT_CAPTIONS,
    AUDIO,
    SPEED_RAMP,
    TRANSITIONS
}

data class RenderStatus(
    val isRendering: Boolean = false,
    val progress: Float = 0f,
    val currentFrame: Int = 0,
    val totalFrames: Int = 900,
    val fpsRate: Float = 0f,
    val etaSec: Int = 0,
    val isCompleted: Boolean = false,
    val outputPath: String = "",
    val fileSizeFormatted: String = "0 MB",
    val shareableUriString: String? = null,
    val galleryUriString: String? = null
)

data class EditorUiState(
    val currentScreen: ScreenDestination = ScreenDestination.HOME,
    val projects: List<Project> = emptyList(),
    val currentProject: Project? = null,
    val isPlaying: Boolean = false,
    val currentPositionMs: Long = 0L,
    val selectedTrackId: String? = null,
    val selectedClipId: String? = null,
    val activeDrawer: DrawerTab = DrawerTab.NONE,
    val isAiProcessing: Boolean = false,
    val aiStatusMessage: String = "",
    val storyboardResult: List<StoryboardScene>? = null,
    val renderStatus: RenderStatus = RenderStatus(),
    val cloudStorageUsedGb: Float = 3.8f,
    val cloudStorageTotalGb: Float = 15.0f,
    val isCloudSyncing: Boolean = false
)

class EditorViewModel : ViewModel() {

    private val _uiState = MutableStateFlow(EditorUiState())
    val uiState: StateFlow<EditorUiState> = _uiState.asStateFlow()

    private var playbackJob: Job? = null
    private var renderJob: Job? = null

    init {
        viewModelScope.launch {
            ProjectRepository.projects.collect { projectList ->
                val current = _uiState.value.currentProject
                val updatedCurrent = if (current != null) {
                    projectList.find { it.id == current.id } ?: current
                } else {
                    projectList.firstOrNull()
                }
                _uiState.value = _uiState.value.copy(
                    projects = projectList,
                    currentProject = updatedCurrent
                )
            }
        }
    }

    fun navigateTo(destination: ScreenDestination) {
        if (_uiState.value.isPlaying) {
            pause()
        }
        _uiState.value = _uiState.value.copy(
            currentScreen = destination,
            activeDrawer = DrawerTab.NONE
        )
    }

    fun openProject(project: Project) {
        pause()
        _uiState.value = _uiState.value.copy(
            currentProject = project,
            currentPositionMs = 0L,
            selectedClipId = project.tracks.firstOrNull()?.clips?.firstOrNull()?.id,
            selectedTrackId = project.tracks.firstOrNull()?.id,
            currentScreen = ScreenDestination.STUDIO_EDITOR,
            activeDrawer = DrawerTab.NONE
        )
    }

    fun createNewProject(title: String, aspectRatio: AspectRatio) {
        val proj = ProjectRepository.createNewProject(title, aspectRatio)
        openProject(proj)
    }

    fun duplicateProject(projectId: String) {
        ProjectRepository.duplicateProject(projectId)
    }

    fun deleteProject(projectId: String) {
        ProjectRepository.deleteProject(projectId)
        if (_uiState.value.currentProject?.id == projectId) {
            val next = ProjectRepository.projects.value.firstOrNull()
            _uiState.value = _uiState.value.copy(
                currentProject = next,
                currentScreen = ScreenDestination.HOME
            )
        }
    }

    fun togglePlay() {
        if (_uiState.value.isPlaying) {
            pause()
        } else {
            play()
        }
    }

    fun play() {
        playbackJob?.cancel()
        val project = _uiState.value.currentProject ?: return
        val maxDuration = project.totalDurationMs.coerceAtLeast(1000L)

        if (_uiState.value.currentPositionMs >= maxDuration) {
            _uiState.value = _uiState.value.copy(currentPositionMs = 0L)
        }

        _uiState.value = _uiState.value.copy(isPlaying = true)

        playbackJob = viewModelScope.launch {
            val intervalMs = 33L // ~30 fps update
            while (isActive && _uiState.value.isPlaying) {
                delay(intervalMs)
                val newPos = _uiState.value.currentPositionMs + intervalMs
                if (newPos >= maxDuration) {
                    _uiState.value = _uiState.value.copy(
                        currentPositionMs = maxDuration,
                        isPlaying = false
                    )
                    break
                } else {
                    _uiState.value = _uiState.value.copy(currentPositionMs = newPos)
                }
            }
        }
    }

    fun pause() {
        playbackJob?.cancel()
        _uiState.value = _uiState.value.copy(isPlaying = false)
    }

    fun seekTo(positionMs: Long) {
        val project = _uiState.value.currentProject ?: return
        val clamped = positionMs.coerceIn(0L, project.totalDurationMs)
        _uiState.value = _uiState.value.copy(currentPositionMs = clamped)
    }

    fun stepFrame(forward: Boolean) {
        val stepMs = 33L
        val current = _uiState.value.currentPositionMs
        val newPos = if (forward) current + stepMs else (current - stepMs).coerceAtLeast(0L)
        seekTo(newPos)
    }

    fun selectClip(trackId: String, clipId: String) {
        _uiState.value = _uiState.value.copy(
            selectedTrackId = trackId,
            selectedClipId = clipId
        )
    }

    fun openDrawer(tab: DrawerTab) {
        _uiState.value = _uiState.value.copy(
            activeDrawer = if (_uiState.value.activeDrawer == tab) DrawerTab.NONE else tab
        )
    }

    fun closeDrawer() {
        _uiState.value = _uiState.value.copy(activeDrawer = DrawerTab.NONE)
    }

    fun setAspectRatio(ratio: AspectRatio) {
        val project = _uiState.value.currentProject ?: return
        val updated = project.copy(aspectRatio = ratio)
        updateAndSaveProject(updated)
    }

    fun updateColorGrade(settings: ColorGradeSettings) {
        val project = _uiState.value.currentProject ?: return
        val updated = project.copy(colorGrade = settings)
        updateAndSaveProject(updated)
    }

    fun splitClipAtPlayhead() {
        val project = _uiState.value.currentProject ?: return
        val playhead = _uiState.value.currentPositionMs

        val selectedClipId = _uiState.value.selectedClipId

        val newTracks = project.tracks.map { track ->
            val clipToSplit = track.clips.find {
                if (selectedClipId != null) it.id == selectedClipId
                else playhead in it.startInTimelineMs until (it.startInTimelineMs + it.durationMs)
            }

            if (clipToSplit != null) {
                val clipStart = clipToSplit.startInTimelineMs
                val offset = playhead - clipStart
                if (offset in 500L until (clipToSplit.durationMs - 500L)) {
                    val part1 = clipToSplit.copy(
                        durationMs = offset,
                        trimOutMs = clipToSplit.trimInMs + offset
                    )
                    val part2 = clipToSplit.copy(
                        id = UUID.randomUUID().toString(),
                        name = "${clipToSplit.name} Part 2",
                        startInTimelineMs = playhead,
                        durationMs = clipToSplit.durationMs - offset,
                        trimInMs = clipToSplit.trimInMs + offset,
                        trimOutMs = clipToSplit.trimOutMs
                    )

                    val updatedClips = mutableListOf<MediaClip>()
                    for (c in track.clips) {
                        if (c.id == clipToSplit.id) {
                            updatedClips.add(part1)
                            updatedClips.add(part2)
                        } else {
                            updatedClips.add(c)
                        }
                    }
                    track.copy(clips = updatedClips)
                } else {
                    track
                }
            } else {
                track
            }
        }

        updateAndSaveProject(project.copy(tracks = newTracks))
    }

    fun deleteSelectedClip() {
        val project = _uiState.value.currentProject ?: return
        val clipId = _uiState.value.selectedClipId ?: return

        val newTracks = project.tracks.map { track ->
            track.copy(clips = track.clips.filterNot { it.id == clipId })
        }
        updateAndSaveProject(project.copy(tracks = newTracks))
        _uiState.value = _uiState.value.copy(selectedClipId = null)
    }

    fun duplicateSelectedClip() {
        val project = _uiState.value.currentProject ?: return
        val clipId = _uiState.value.selectedClipId ?: return

        val newTracks = project.tracks.map { track ->
            val match = track.clips.find { it.id == clipId }
            if (match != null) {
                val copyClip = match.copy(
                    id = UUID.randomUUID().toString(),
                    name = "${match.name} (Copy)",
                    startInTimelineMs = match.startInTimelineMs + match.durationMs
                )
                val list = track.clips.toMutableList()
                val idx = list.indexOf(match)
                list.add(idx + 1, copyClip)
                track.copy(clips = list)
            } else {
                track
            }
        }
        val maxDuration = newTracks.flatMap { it.clips }
            .maxOfOrNull { it.startInTimelineMs + it.durationMs } ?: project.totalDurationMs
        updateAndSaveProject(project.copy(tracks = newTracks, totalDurationMs = maxDuration))
    }

    fun updateClipSpeed(speed: Float) {
        val project = _uiState.value.currentProject ?: return
        val clipId = _uiState.value.selectedClipId ?: return

        val newTracks = project.tracks.map { track ->
            track.copy(clips = track.clips.map {
                if (it.id == clipId) it.copy(speed = speed) else it
            })
        }
        updateAndSaveProject(project.copy(tracks = newTracks))
    }

    fun updateClipVolume(volume: Float) {
        val project = _uiState.value.currentProject ?: return
        val clipId = _uiState.value.selectedClipId ?: return

        val newTracks = project.tracks.map { track ->
            track.copy(clips = track.clips.map {
                if (it.id == clipId) it.copy(volume = volume) else it
            })
        }
        updateAndSaveProject(project.copy(tracks = newTracks))
    }

    fun addSubtitle(text: String, style: SubtitleStyle) {
        val project = _uiState.value.currentProject ?: return
        val start = _uiState.value.currentPositionMs
        val end = (start + 3000L).coerceAtMost(project.totalDurationMs)
        val item = SubtitleItem(
            text = text,
            startMs = start,
            endMs = end,
            stylePreset = style
        )
        val updated = project.copy(subtitles = project.subtitles + item)
        updateAndSaveProject(updated)
    }

    // AI Operations
    fun generateAiAutoCaptions(style: SubtitleStyle = SubtitleStyle.VIRAL_YELLOW) {
        val project = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAiProcessing = true,
                aiStatusMessage = "Analyzing audio frequencies & word timestamps..."
            )
            val captions = NovaAiEngine.generateAutoCaptions(project.totalDurationMs, style)
            val updated = project.copy(subtitles = captions)
            updateAndSaveProject(updated)
            _uiState.value = _uiState.value.copy(
                isAiProcessing = false,
                aiStatusMessage = "Auto-captions generated with viral sync!"
            )
        }
    }

    fun applyAiSmartCut() {
        val project = _uiState.value.currentProject ?: return
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAiProcessing = true,
                aiStatusMessage = "Detecting silence gaps and high-energy peaks..."
            )
            val videoTrack = project.tracks.find { it.type == TrackType.VIDEO_MAIN }
            if (videoTrack != null) {
                val cutClips = NovaAiEngine.generateSmartCut(videoTrack.clips)
                val newTracks = project.tracks.map {
                    if (it.id == videoTrack.id) it.copy(clips = cutClips) else it
                }
                val newDuration = cutClips.sumOf { it.durationMs }
                val updated = project.copy(tracks = newTracks, totalDurationMs = newDuration)
                updateAndSaveProject(updated)
            }
            _uiState.value = _uiState.value.copy(
                isAiProcessing = false,
                aiStatusMessage = "Smart Cut applied: Dead air eliminated!"
            )
        }
    }

    fun applyPromptColorGrade(prompt: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAiProcessing = true,
                aiStatusMessage = "Computing neural color matrix for '$prompt'..."
            )
            val settings = NovaAiEngine.computeAiColorGrade(prompt)
            updateColorGrade(settings)
            _uiState.value = _uiState.value.copy(
                isAiProcessing = false,
                aiStatusMessage = "Cinematic LUT applied: ${settings.aiDescription}"
            )
        }
    }

    fun generateStoryboard(topic: String) {
        viewModelScope.launch {
            _uiState.value = _uiState.value.copy(
                isAiProcessing = true,
                aiStatusMessage = "Generating shot list & viral hook outline for '$topic'..."
            )
            val scenes = NovaAiEngine.generateScriptStoryboard(topic)
            _uiState.value = _uiState.value.copy(
                isAiProcessing = false,
                storyboardResult = scenes,
                aiStatusMessage = "Script & Storyboard generated successfully!"
            )
        }
    }

    fun importMediaFromUris(context: Context, uris: List<Uri>) {
        val project = _uiState.value.currentProject ?: return
        if (uris.isEmpty()) return

        val newClips = mutableListOf<MediaClip>()
        val mainVideoTrack = project.tracks.find { it.type == TrackType.VIDEO_MAIN }
        var currentOffset = mainVideoTrack?.clips?.maxOfOrNull { it.startInTimelineMs + it.durationMs } ?: 0L

        uris.forEach { uri ->
            val info = RealVideoEngine.extractMediaInfo(context, uri)
            val clip = MediaClip(
                id = UUID.randomUUID().toString(),
                name = info.title,
                uriString = uri.toString(),
                durationMs = info.durationMs,
                startInTimelineMs = currentOffset,
                trimInMs = 0L,
                trimOutMs = info.durationMs,
                colorHex = 0xFF00E5FF
            )
            newClips.add(clip)
            currentOffset += info.durationMs
        }

        val updatedTracks = project.tracks.map { track ->
            if (track.type == TrackType.VIDEO_MAIN) {
                track.copy(clips = track.clips + newClips)
            } else {
                track
            }
        }

        val maxDur = updatedTracks.flatMap { it.clips }
            .maxOfOrNull { it.startInTimelineMs + it.durationMs } ?: currentOffset
        val updatedProj = project.copy(
            tracks = updatedTracks,
            totalDurationMs = maxDur.coerceAtLeast(1000L),
            coverUriString = newClips.firstOrNull()?.uriString ?: project.coverUriString
        )
        updateAndSaveProject(updatedProj)
        selectClip(updatedTracks.first().id, newClips.first().id)
    }

    fun createNewProjectFromUris(context: Context, uris: List<Uri>, title: String, aspectRatio: AspectRatio) {
        val firstClipInfo = if (uris.isNotEmpty()) RealVideoEngine.extractMediaInfo(context, uris.first()) else null
        val newProj = ProjectRepository.createNewProject(
            title = title,
            aspectRatio = aspectRatio
        )
        val projWithCover = newProj.copy(
            coverUriString = uris.firstOrNull()?.toString()
        )
        openProject(projWithCover)
        if (uris.isNotEmpty()) {
            importMediaFromUris(context, uris)
        }
    }

    // High Speed Real Video Render & Muxing Engine
    fun startExport(context: Context, profile: RenderProfile) {
        renderJob?.cancel()
        val project = _uiState.value.currentProject ?: return
        val totalSec = (project.totalDurationMs) / 1000f
        val totalFrames = (totalSec * profile.frameRate.fps).toInt().coerceAtLeast(180)

        _uiState.value = _uiState.value.copy(
            renderStatus = RenderStatus(
                isRendering = true,
                progress = 0f,
                currentFrame = 0,
                totalFrames = totalFrames,
                fpsRate = 120f,
                etaSec = 4,
                isCompleted = false
            ),
            currentScreen = ScreenDestination.EXPORT_RENDER
        )

        renderJob = viewModelScope.launch {
            val result = RealVideoEngine.exportProjectVideo(context, project) { progress ->
                val curFrame = (progress * totalFrames).toInt()
                val eta = ((1f - progress) * 4f).toInt()
                _uiState.value = _uiState.value.copy(
                    renderStatus = _uiState.value.renderStatus.copy(
                        progress = progress,
                        currentFrame = curFrame,
                        fpsRate = 135f + (Math.random() * 10 - 5).toFloat(),
                        etaSec = eta
                    )
                )
            }

            _uiState.value = _uiState.value.copy(
                renderStatus = _uiState.value.renderStatus.copy(
                    isRendering = false,
                    isCompleted = true,
                    progress = 1.0f,
                    currentFrame = totalFrames,
                    outputPath = result.filePath,
                    fileSizeFormatted = result.fileSizeFormatted,
                    shareableUriString = result.shareableUri.toString(),
                    galleryUriString = result.galleryUri?.toString()
                )
            )
        }
    }

    fun triggerCloudSync() {
        viewModelScope.launch {
            val proj = _uiState.value.currentProject ?: return@launch
            _uiState.value = _uiState.value.copy(isCloudSyncing = true)
            val syncingProj = proj.copy(cloudSyncStatus = CloudSyncStatus.SYNCING)
            updateAndSaveProject(syncingProj)

            delay(1200)

            val syncedProj = syncingProj.copy(
                cloudSyncStatus = CloudSyncStatus.SYNCED,
                cloudVersion = proj.cloudVersion + 1
            )
            updateAndSaveProject(syncedProj)
            _uiState.value = _uiState.value.copy(isCloudSyncing = false)
        }
    }

    private fun updateAndSaveProject(project: Project) {
        ProjectRepository.saveProject(project)
        _uiState.value = _uiState.value.copy(currentProject = project)
    }
}
