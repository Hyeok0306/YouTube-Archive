package com.example.youtube_archive.data.remote

import retrofit2.http.GET
import retrofit2.http.Query

interface YouTubeApiService {
    @GET("search")
    suspend fun searchVideos(
        @Query("part") part: String = "snippet",
        @Query("q") query: String,            // 사용자가 입력할 검색어
        @Query("type") type: String = "video", // 비디오만 검색
        @Query("maxResults") maxResults: Int = 10, // 가져올 개수
        @Query("key") apiKey: String          // YouTube API Key
    ): YouTubeSearchResponse
}