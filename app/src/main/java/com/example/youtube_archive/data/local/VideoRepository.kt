package com.example.youtube_archive.data.local

import com.example.youtube_archive.BuildConfig
import com.example.youtube_archive.data.remote.RetrofitClient
import com.example.youtube_archive.data.remote.YouTubeSearchItem
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext

// 기존에 작성하셨던 코드가 대략 이런 형태일 것입니다.
class VideoRepository(private val videoDao: VideoDao) {

    // ==========================================
    // 1. Local DB (Room) 기능 - 2주차에 작성한 코드
    // ==========================================
    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()

    suspend fun insertVideo(video: VideoEntity) {
        videoDao.insertVideo(video)
    }

    suspend fun deleteVideo(video: VideoEntity) {
        videoDao.deleteVideo(video)
    }

    // ==========================================
    // 2. Remote API (YouTube 검색) 기능 - ⭐ 3주차 추가 코드
    // ==========================================
    private val apiService = RetrofitClient.apiService

    suspend fun searchYouTubeVideos(query: String): List<YouTubeSearchItem> {
        return withContext(Dispatchers.IO) {
            try {
                // BuildConfig에서 가짜(또는 진짜) API 키 불러오기
                val apiKey = BuildConfig.YOUTUBE_API_KEY

                val response = apiService.searchVideos(
                    query = query,
                    apiKey = apiKey
                )
                response.items
            } catch (e: Exception) {
                e.printStackTrace()
                emptyList() // 에러 시 빈 리스트 반환
            }
        }
    }
}