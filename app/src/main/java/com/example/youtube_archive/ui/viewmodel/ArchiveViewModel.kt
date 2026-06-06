package com.example.youtube_archive.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube_archive.data.local.VideoRepository
import com.example.youtube_archive.model.Video
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class ArchiveViewModel(private val repository: VideoRepository) : ViewModel() {

    // DB 데이터를 Flow에서 StateFlow로 변환하여 UI가 구독하기 편하게 만듭니다.
    // VideoEntity 대신 Video 모델을 사용하도록 변경합니다.
    val allVideos: StateFlow<List<Video>> = repository.allVideos
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    // 영상 추가 함수 (Video 모델 사용)
    fun addVideo(video: Video) {
        viewModelScope.launch {
            repository.insertVideo(video)
        }
    }

    // 영상 삭제 함수 (Video 모델 사용)
    fun deleteVideo(video: Video) {
        viewModelScope.launch {
            repository.deleteVideo(video)
        }
    }

    // ArchiveViewModel.kt 내부에 추가
    fun addTestVideo() {
        viewModelScope.launch {
            val testVideo = Video(
                videoId = "dQw4w9WgXcQ",
                title = "테스트 영상: Room DB 연동 확인",
                thumbnailUri = "https://img.youtube.com/vi/dQw4w9WgXcQ/0.jpg"
            )
            repository.insertVideo(testVideo)
        }
    }
}
