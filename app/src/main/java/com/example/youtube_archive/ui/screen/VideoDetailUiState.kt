package com.example.youtube_archive.ui.screen

// ⭕ UI의 현재 "상태"를 정의하는 독립 가방 클래스
sealed class VideoDetailUiState {
    object Loading : VideoDetailUiState()
    data class Success(val title: String, val thumbnailUrl: String, val isSaved: Boolean) : VideoDetailUiState()
    data class Error(val message: String) : VideoDetailUiState()
}