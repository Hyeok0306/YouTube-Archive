package com.example.youtube_archive.data.local

import kotlinx.coroutines.flow.Flow

class VideoRepository(private val videoDao: VideoDao) {

    // UI(ViewModel)에 전달할 데이터의 통로
    val allVideos: Flow<List<VideoEntity>> = videoDao.getAllVideos()

    suspend fun insert(video: VideoEntity) {
        videoDao.insertVideo(video)
    }

    suspend fun delete(video: VideoEntity) {
        videoDao.deleteVideo(video)
    }
}