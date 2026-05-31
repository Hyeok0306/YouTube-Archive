package com.example.youtube_archive.data.remote

import com.google.gson.annotations.SerializedName

// 전체 검색 응답
data class YouTubeSearchResponse(
    @SerializedName("items") val items: List<YouTubeSearchItem>
)

// 개별 영상 아이템
data class YouTubeSearchItem(
    @SerializedName("id") val id: VideoId,
    @SerializedName("snippet") val snippet: VideoSnippet
)

// 영상 ID
data class VideoId(
    @SerializedName("videoId") val videoId: String? // 채널 등 다른 결과가 섞일 수 있으므로 nullable
)

// 영상 상세 정보 (제목, 썸네일 등)
data class VideoSnippet(
    @SerializedName("title") val title: String,
    @SerializedName("thumbnails") val thumbnails: Thumbnails
)

data class Thumbnails(
    @SerializedName("high") val high: ThumbnailInfo
)

data class ThumbnailInfo(
    @SerializedName("url") val url: String
)