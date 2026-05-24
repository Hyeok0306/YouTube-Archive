package com.example.youtube_archive.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "videos") // 데이터베이스에서 사용할 테이블 이름
data class VideoEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,             // 고유 ID (Room이 자동으로 생성)
    val videoUrl: String,        // 유튜브 주소
    val title: String,           // 영상 제목
    val thumbnailUrl: String     // 썸네일 이미지 주소
)