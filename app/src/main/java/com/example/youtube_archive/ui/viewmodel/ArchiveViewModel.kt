package com.example.youtube_archive.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube_archive.data.local.VideoEntity
import com.example.youtube_archive.data.local.VideoRepository
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArchiveViewModel(private val repository: VideoRepository) : ViewModel() {

    // DB 데이터를 Flow에서 StateFlow로 변환하여 UI가 구독하기 편하게 만듭니다.
    val allVideos: StateFlow<List<VideoEntity>> = repository.allVideos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 영상 추가 함수
    fun addVideo(video: VideoEntity) {
        viewModelScope.launch {
            repository.insertVideo(video)
        }
    }

    // 영상 삭제 함수
    fun deleteVideo(video: VideoEntity) {
        viewModelScope.launch {
            repository.deleteVideo(video)
        }
    }

    // ArchiveViewModel.kt 내부에 추가
    fun addTestVideo() {
        viewModelScope.launch {
            // id를 제외하고 나머지 필드만 채우면 됩니다 (id는 자동 생성됨)
            val testVideo = VideoEntity(
                videoUrl = "https://www.youtube.com/watch?v=dQw4w9WgXcQ",
                title = "테스트 영상: Room DB 연동 확인",
                thumbnailUrl = "https://img.youtube.com/vi/dQw4w9WgXcQ/0.jpg"
            )
            repository.insertVideo(testVideo)
        }
    }
}