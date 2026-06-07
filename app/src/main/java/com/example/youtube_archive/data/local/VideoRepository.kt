package com.example.youtube_archive.data.local

import com.example.youtube_archive.BuildConfig
import com.example.youtube_archive.data.remote.RetrofitClient
import com.example.youtube_archive.data.remote.YouTubeSearchItem
import com.example.youtube_archive.model.GoogleUser
import com.example.youtube_archive.model.Video
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map

class VideoRepository(
    private val videoDao: VideoDao
) {
    // [진짜 기능] 1. 기기 내부 Room DB에서 데이터 실시간 관찰 (Flow 매핑)
    val allVideos: Flow<List<Video>> = videoDao.getAllVideos().map { entityList ->
        entityList.map { entity ->
            Video(
                id = entity.id,
                videoId = entity.videoId,
                title = entity.title,
                thumbnailUrl = entity.thumbnailUrl,
                isSyncedWithDrive = false // 기본값은 로컬 전용
            )
        }
    }

    // [진짜 기능] 2. 기기 내부 Room DB에 영상 저장
    suspend fun insertVideo(video: Video) {
        val entity = VideoEntity(
            videoId = video.videoId,
            title = video.title,
            thumbnailUrl = video.thumbnailUrl
        )
        videoDao.insertVideo(entity)
    }

    // [진짜 기능] 3. 영상 삭제
    suspend fun deleteVideo(video: Video) {
        val entity = VideoEntity(
            id = video.id,
            videoId = video.videoId,
            title = video.title,
            thumbnailUrl = video.thumbnailUrl
        )
        videoDao.deleteVideo(entity)
    }

    // [진짜 기능] 4. 정규식 기반 유튜브 ID 파싱 로직
    fun extractVideoId(url: String): String? {
        val regex = "^(?:https?:\\/\\/)?(?:www\\.)?(?:youtube\\.com\\/(?:[^\\/\\n\\s]+\\/\\S+\\/|(?:v|e(?:mbed)?)\\/|\\S*?[?&]v=)|youtu\\.be\\/)([a-zA-Z0-9_-]{11})".toRegex()
        return regex.find(url)?.groupValues?.get(1)
    }

    // [진짜 기능] 5. 유튜브 API 검색 (Retrofit 사용)
    suspend fun searchYouTubeVideos(query: String): List<YouTubeSearchItem> {
        return try {
            val response = RetrofitClient.apiService.searchVideos(
                query = query,
                apiKey = BuildConfig.YOUTUBE_API_KEY
            )
            response.items
        } catch (e: Exception) {
            e.printStackTrace()
            emptyList()
        }
    }

    // [가상 가상] 6. 구글 로그인 시뮬레이션
    suspend fun signInWithGoogle(): GoogleUser {
        delay(1200)
        return GoogleUser(
            name = "이성혁",
            email = "Hyeok0306@gmail.com",
            profilePictureUrl = "https://www.gstatic.com/images/branding/product/2x/avatar_anonymous_96x96dp.png"
        )
    }

    // [가상 가상] 7. 구글 드라이브 백업 시뮬레이션
    suspend fun syncDatabaseWithGoogleDrive(videos: List<Video>): Flow<String> = flow {
        emit("구글 드라이브 클라우드 인프라 접근 중...")
        delay(1000)
        emit("SQLite 데이터베이스 파일 파일 스트림 복사 중...")
        delay(1200)
        emit("구글 드라이브 'YouTube-Archive-Backup/' 경로 업로드 완료!")
    }

    suspend fun isVideoSaved(videoId: String): Boolean {
        // 개수가 0보다 크면 이미 저장되어 있는 것(true), 0이면 없는 것(false)
        return videoDao.isVideoSaved(videoId) > 0
    }

    // VideoRepository.kt 내부에 추가
    suspend fun deleteVideoById(videoId: String): Boolean {
        // 삭제된 행의 개수가 0보다 크면 성공(true), 아니면 실패(false)
        return videoDao.deleteVideoById(videoId) > 0
    }
}
