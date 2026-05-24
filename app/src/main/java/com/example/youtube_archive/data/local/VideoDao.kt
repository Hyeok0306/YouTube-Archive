package com.example.youtube_archive.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    // 저장된 모든 영상 불러오기 (최신순)
    @Query("SELECT * FROM videos ORDER BY id DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    // 영상 저장
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertVideo(video: VideoEntity)

    // 영상 삭제
    @Delete
    suspend fun deleteVideo(video: VideoEntity)
}