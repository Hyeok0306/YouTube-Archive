package com.example.youtube_archive.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,         // Room DB용 고유 자동 증가 키
    val videoId: String,      // 유튜브 영상 ID
    val title: String,        // 영상 제목
    val thumbnailUrl: String  // 썸네일 URL (통일된 명칭)
)