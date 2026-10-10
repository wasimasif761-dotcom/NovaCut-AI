package com.example.data

import com.example.R
import com.example.model.AspectRatio
import com.example.model.ClipType
import com.example.model.CloudSyncStatus
import com.example.model.ColorGradeSettings
import com.example.model.FrameRate
import com.example.model.LutPreset
import com.example.model.MediaClip
import com.example.model.Project
import com.example.model.Resolution
import com.example.model.SubtitleItem
import com.example.model.SubtitleStyle
import com.example.model.TimelineTrack
import com.example.model.TrackType
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import java.util.UUID

object ProjectRepository {

    private val _projects = MutableStateFlow<List<Project>>(emptyList())
    val projects: StateFlow<List<Project>> = _projects.asStateFlow()

    init {
        seedInitialProjects()
    }

    private fun seedInitialProjects() {
        val cyberProject = Project(
            id = "proj-cyberpunk-001",
            title = "Cyberpunk Neo Reel",
            aspectRatio = AspectRatio.ASPECT_9_16,
            resolution = Resolution.RES_1080P,
            frameRate = FrameRate.FPS_60,
            coverDrawableRes = R.drawable.sample_thumb_cyber_1791582067290,
            cloudSyncStatus = CloudSyncStatus.SYNCED,
            cloudVersion = 4,
            totalDurationMs = 12000L,
            colorGrade = ColorGradeSettings(
                lut = LutPreset.CYBERPUNK,
                contrast = 1.3f,
                saturation = 1.4f,
                warmth = -0.2f,
                vignette = 0.35f,
                aiDescription = "Cyberpunk Neon: Boosted cyan highlights and violet shadow balance."
            ),
            tracks = listOf(
                TimelineTrack(
                    id = "track-vid-main",
                    type = TrackType.VIDEO_MAIN,
                    name = "Main Video",
                    clips = listOf(
                        MediaClip(
                            id = "clip-c1",
                            name = "Neon Tokyo Blvd",
                            drawableResId = R.drawable.sample_thumb_cyber_1791582067290,
                            durationMs = 6000L,
                            startInTimelineMs = 0L,
                            trimInMs = 0L,
                            trimOutMs = 6000L,
                            colorHex = 0xFF00E5FF
                        ),
                        MediaClip(
                            id = "clip-c2",
                            name = "Flying Cars Transition",
                            drawableResId = R.drawable.sample_thumb_cyber_1791582067290,
                            durationMs = 6000L,
                            startInTimelineMs = 6000L,
                            trimInMs = 0L,
                            trimOutMs = 6000L,
                            colorHex = 0xFFFF2A85
                        )
                    )
                ),
                TimelineTrack(
                    id = "track-aud-bg",
                    type = TrackType.AUDIO_MUSIC,
                    name = "Synthwave Beat",
                    clips = listOf(
                        MediaClip(
                            id = "clip-a1",
                            type = ClipType.AUDIO,
                            name = "Synth Bass 128 BPM",
                            durationMs = 12000L,
                            startInTimelineMs = 0L,
                            trimInMs = 0L,
                            trimOutMs = 12000L,
                            volume = 0.85f,
                            colorHex = 0xFF7C4DFF
                        )
                    )
                ),
                TimelineTrack(
                    id = "track-sub",
                    type = TrackType.SUBTITLE,
                    name = "AI Captions",
                    clips = listOf(
                        MediaClip(
                            id = "clip-t1",
                            type = ClipType.TEXT,
                            name = "Auto Subtitles",
                            durationMs = 12000L,
                            startInTimelineMs = 0L,
                            colorHex = 0xFFFFD700
                        )
                    )
                )
            ),
            subtitles = listOf(
                SubtitleItem(
                    text = "🚀 Welcome to the Future of Editing",
                    startMs = 500L,
                    endMs = 3500L,
                    stylePreset = SubtitleStyle.VIRAL_YELLOW
                ),
                SubtitleItem(
                    text = "⚡ 60 FPS Render with Neural AI",
                    startMs = 3800L,
                    endMs = 7500L,
                    stylePreset = SubtitleStyle.NEON_CYAN
                ),
                SubtitleItem(
                    text = "🔥 Ready for TikTok & Reels",
                    startMs = 7800L,
                    endMs = 11500L,
                    stylePreset = SubtitleStyle.VIRAL_YELLOW
                )
            )
        )

        val sunsetProject = Project(
            id = "proj-sunset-002",
            title = "Golden Coast Cinematic",
            aspectRatio = AspectRatio.ASPECT_16_9,
            resolution = Resolution.RES_4K,
            frameRate = FrameRate.FPS_24,
            coverDrawableRes = R.drawable.sample_thumb_sunset_1791582093769,
            cloudSyncStatus = CloudSyncStatus.SYNCED,
            cloudVersion = 2,
            totalDurationMs = 15000L,
            colorGrade = ColorGradeSettings(
                lut = LutPreset.GOLDEN_HOUR,
                contrast = 1.15f,
                saturation = 1.3f,
                warmth = 0.4f,
                vignette = 0.25f,
                aiDescription = "Golden Hour: Volumetric sunset radiance and amber ocean tones."
            ),
            tracks = listOf(
                TimelineTrack(
                    id = "track-vid-sunset",
                    type = TrackType.VIDEO_MAIN,
                    name = "Landscape Master",
                    clips = listOf(
                        MediaClip(
                            id = "clip-s1",
                            name = "Cliffside Horizon",
                            drawableResId = R.drawable.sample_thumb_sunset_1791582093769,
                            durationMs = 8000L,
                            startInTimelineMs = 0L,
                            colorHex = 0xFFFF9100
                        ),
                        MediaClip(
                            id = "clip-s2",
                            name = "Ocean Waves Crest",
                            drawableResId = R.drawable.sample_thumb_sunset_1791582093769,
                            durationMs = 7000L,
                            startInTimelineMs = 8000L,
                            colorHex = 0xFFFF6D00
                        )
                    )
                ),
                TimelineTrack(
                    id = "track-aud-nature",
                    type = TrackType.AUDIO_MUSIC,
                    name = "Cinematic Ambient",
                    clips = listOf(
                        MediaClip(
                            id = "clip-as1",
                            type = ClipType.AUDIO,
                            name = "Ocean Swell & Strings",
                            durationMs = 15000L,
                            startInTimelineMs = 0L,
                            volume = 0.9f,
                            colorHex = 0xFF00B0FF
                        )
                    )
                )
            ),
            subtitles = listOf(
                SubtitleItem(
                    text = "🌊 Chasing the Pacific Horizon",
                    startMs = 1000L,
                    endMs = 5000L,
                    stylePreset = SubtitleStyle.MINIMAL_WHITE
                ),
                SubtitleItem(
                    text = "✨ Golden hour never looked this crisp",
                    startMs = 5500L,
                    endMs = 10500L,
                    stylePreset = SubtitleStyle.MINIMAL_WHITE
                )
            )
        )

        _projects.value = listOf(cyberProject, sunsetProject)
    }

    fun getProjectById(id: String): Project? {
        return _projects.value.find { it.id == id }
    }

    fun saveProject(project: Project) {
        val current = _projects.value.toMutableList()
        val index = current.indexOfFirst { it.id == project.id }
        if (index >= 0) {
            current[index] = project.copy(
                lastModifiedEpochMs = System.currentTimeMillis()
            )
        } else {
            current.add(0, project)
        }
        _projects.value = current
    }

    fun createNewProject(
        title: String,
        aspectRatio: AspectRatio = AspectRatio.ASPECT_9_16,
        coverRes: Int? = R.drawable.sample_thumb_cyber_1791582067290
    ): Project {
        val newProj = Project(
            id = "proj-" + UUID.randomUUID().toString().take(8),
            title = title,
            aspectRatio = aspectRatio,
            resolution = Resolution.RES_1080P,
            frameRate = FrameRate.FPS_30,
            coverDrawableRes = coverRes,
            cloudSyncStatus = CloudSyncStatus.SYNCED,
            tracks = listOf(
                TimelineTrack(
                    id = "track-main-" + UUID.randomUUID().toString().take(4),
                    type = TrackType.VIDEO_MAIN,
                    name = "Main Video",
                    clips = listOf(
                        MediaClip(
                            id = "clip-init-1",
                            name = "Initial Clip",
                            drawableResId = coverRes,
                            durationMs = 8000L,
                            startInTimelineMs = 0L,
                            colorHex = 0xFF00E5FF
                        )
                    )
                ),
                TimelineTrack(
                    id = "track-audio-" + UUID.randomUUID().toString().take(4),
                    type = TrackType.AUDIO_MUSIC,
                    name = "Soundtrack",
                    clips = listOf(
                        MediaClip(
                            id = "clip-audio-1",
                            type = ClipType.AUDIO,
                            name = "Atmosphere Track",
                            durationMs = 8000L,
                            startInTimelineMs = 0L,
                            colorHex = 0xFF7C4DFF
                        )
                    )
                )
            ),
            subtitles = listOf(
                SubtitleItem(
                    text = "Tap to edit captions or use AI Magic ✨",
                    startMs = 500L,
                    endMs = 4000L,
                    stylePreset = SubtitleStyle.VIRAL_YELLOW
                )
            )
        )
        saveProject(newProj)
        return newProj
    }

    fun deleteProject(id: String) {
        _projects.value = _projects.value.filterNot { it.id == id }
    }

    fun duplicateProject(id: String): Project? {
        val existing = getProjectById(id) ?: return null
        val copyProj = existing.copy(
            id = "proj-" + UUID.randomUUID().toString().take(8),
            title = "${existing.title} (Copy)",
            lastModifiedEpochMs = System.currentTimeMillis()
        )
        saveProject(copyProj)
        return copyProj
    }
}
