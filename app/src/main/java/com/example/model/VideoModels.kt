package com.example.model

import java.util.UUID

enum class AspectRatio(val label: String, val ratioWidth: Float, val ratioHeight: Float, val iconDescription: String) {
    ASPECT_9_16("9:16 Reel/TikTok", 9f, 16f, "Vertical TikTok / Reels / Shorts"),
    ASPECT_16_9("16:9 YouTube", 16f, 9f, "Landscape YouTube / Cinema"),
    ASPECT_1_1("1:1 Square", 1f, 1f, "Square Instagram Post"),
    ASPECT_4_5("4:5 Portrait", 4f, 5f, "Portrait Instagram Feed"),
    ASPECT_21_9("21:9 UltraWide", 21f, 9f, "Cinematic Anamorphic")
}

enum class Resolution(val label: String, val width: Int, val height: Int) {
    RES_720P("720p HD", 1280, 720),
    RES_1080P("1080p Full HD", 1920, 1080),
    RES_4K("4K Ultra HD", 3840, 2160)
}

enum class FrameRate(val fps: Int, val label: String) {
    FPS_24(24, "24 fps (Cinema)"),
    FPS_30(30, "30 fps (Standard)"),
    FPS_60(60, "60 fps (Ultra Smooth)")
}

enum class CloudSyncStatus {
    SYNCED,
    SYNCING,
    LOCAL_ONLY,
    ERROR
}

enum class ClipType {
    VIDEO,
    AUDIO,
    IMAGE,
    TEXT,
    OVERLAY_PIP
}

enum class TrackType {
    VIDEO_MAIN,
    VIDEO_PIP,
    AUDIO_MUSIC,
    AUDIO_VOICE,
    SUBTITLE,
    STICKER_FX
}

enum class LutPreset(val displayName: String, val tag: String) {
    NONE("Original", "Natural"),
    TEAL_ORANGE("Teal & Orange", "Hollywood"),
    CYBERPUNK("Cyberpunk Neon", "Sci-Fi"),
    FILM_NOIR("Film Noir B&W", "Classic"),
    GOLDEN_HOUR("Golden Hour", "Sunset Warmth"),
    MATRIX_GREEN("Matrix Code", "Neo Sci-Fi"),
    VINTAGE_8MM("Vintage 8mm", "Retro Film"),
    HDR_VIBRANT("HDR Vibrant", "Hyper-Real"),
    PASTEL_DREAM("Pastel Dream", "Aesthetic")
}

data class ColorGradeSettings(
    val lut: LutPreset = LutPreset.NONE,
    val brightness: Float = 0f,       // -1f to 1f
    val contrast: Float = 1f,         // 0.5f to 2f
    val saturation: Float = 1f,       // 0f to 2f
    val warmth: Float = 0f,           // -1f to 1f
    val vignette: Float = 0f,         // 0f to 1f
    val aiDescription: String? = null
)

data class SubtitleItem(
    val id: String = UUID.randomUUID().toString(),
    val text: String,
    val startMs: Long,
    val endMs: Long,
    val stylePreset: SubtitleStyle = SubtitleStyle.VIRAL_YELLOW
)

enum class SubtitleStyle(val title: String, val fontColorHex: Long, val bgHex: Long) {
    VIRAL_YELLOW("Viral Yellow", 0xFFFFE500, 0xCC000000),
    NEON_CYAN("Neon Glow", 0xFF00E5FF, 0xDD111122),
    MINIMAL_WHITE("Minimal White", 0xFFFFFFFF, 0x88000000),
    PODCAST_BOX("Podcast Highlight", 0xFFFFFFFF, 0xEEFF2A85)
}

data class MediaClip(
    val id: String = UUID.randomUUID().toString(),
    val type: ClipType = ClipType.VIDEO,
    val name: String,
    val drawableResId: Int? = null,
    val durationMs: Long,
    val startInTimelineMs: Long,
    val trimInMs: Long = 0L,
    val trimOutMs: Long = durationMs,
    val speed: Float = 1.0f,
    val volume: Float = 1.0f,
    val colorHex: Long = 0xFF00B0FF,
    val isAiEnhanced: Boolean = false,
    val isBgRemoved: Boolean = false,
    val transitionIn: String = "Cut"
)

data class TimelineTrack(
    val id: String = UUID.randomUUID().toString(),
    val type: TrackType,
    val name: String,
    val isMuted: Boolean = false,
    val isLocked: Boolean = false,
    val isHidden: Boolean = false,
    val clips: List<MediaClip> = emptyList()
)

data class Project(
    val id: String = UUID.randomUUID().toString(),
    val title: String,
    val aspectRatio: AspectRatio = AspectRatio.ASPECT_9_16,
    val resolution: Resolution = Resolution.RES_1080P,
    val frameRate: FrameRate = FrameRate.FPS_30,
    val lastModifiedEpochMs: Long = System.currentTimeMillis(),
    val cloudSyncStatus: CloudSyncStatus = CloudSyncStatus.SYNCED,
    val cloudVersion: Int = 1,
    val tracks: List<TimelineTrack> = emptyList(),
    val subtitles: List<SubtitleItem> = emptyList(),
    val colorGrade: ColorGradeSettings = ColorGradeSettings(),
    val coverDrawableRes: Int? = null,
    val totalDurationMs: Long = 15000L
)

enum class ScreenDestination {
    HOME,
    STUDIO_EDITOR,
    AI_MAGIC_HUB,
    EXPORT_RENDER,
    CLOUD_SYNC,
    TEMPLATES
}

data class RenderProfile(
    val resolution: Resolution = Resolution.RES_1080P,
    val frameRate: FrameRate = FrameRate.FPS_60,
    val bitrateMbps: Int = 24,
    val codec: String = "H.264 High Profile (HW Accel)",
    val audioBitrateKbps: Int = 320
)
