package com.example.youtube_archive.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: Int = 0,
    val videoId: String,
    val title: String,
    val thumbnailUri: String,
    val isSyncedWithDrive: Boolean = false // UI 렌더링용 플래그
) {
    val videoUrl: String get() = "https://www.youtube.com/watch?v=$videoId"
}

data class GoogleUser(
    val name: String,
    val email: String,
    val profilePictureUrl: String
)