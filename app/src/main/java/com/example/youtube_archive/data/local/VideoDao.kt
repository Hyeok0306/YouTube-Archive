package com.example.youtube_archive.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface VideoDao {
    @Query("SELECT * FROM videos ORDER BY id DESC")
    fun getAllVideos(): Flow<List<VideoEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertVideo(video: VideoEntity)

    @Delete
    suspend fun deleteVideo(video: VideoEntity)
    // VideoDao.kt 에 추가할 쿼리문
    @Query("SELECT COUNT(*) FROM videos WHERE videoId = :videoId")
    suspend fun isVideoSaved(videoId: String): Int

    // VideoDao.kt 내부에 추가
    @Query("DELETE FROM videos WHERE videoId = :videoId")
    suspend fun deleteVideoById(videoId: String): Int
}