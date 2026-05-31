package com.example.youtube_archive.ui.viewmodel

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube_archive.data.local.AppDatabase
import com.example.youtube_archive.data.local.VideoRepository
import com.example.youtube_archive.data.remote.YouTubeSearchItem
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = VideoRepository(database.videoDao())

    private val _searchResults = MutableStateFlow<List<YouTubeSearchItem>>(emptyList())
    val searchResults: StateFlow<List<YouTubeSearchItem>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // 💡 추가된 부분: 현재 검색 모드 (0: 키워드 검색, 1: URL 입력)
    private val _searchMode = MutableStateFlow(0)
    val searchMode: StateFlow<Int> = _searchMode.asStateFlow()

    fun setSearchMode(mode: Int) {
        _searchMode.value = mode
        _searchResults.value = emptyList() // 탭을 바꿀 때 기존 검색 결과 초기화
    }

    fun searchVideos(query: String) {
        if (query.isBlank()) return

        viewModelScope.launch {
            _isLoading.value = true

            // URL 모드일 경우 주소에서 ID만 추출, 키워드 모드일 경우 그대로 사용
            val finalQuery = if (_searchMode.value == 1) {
                extractVideoIdFromUrl(query) ?: query // 추출 실패 시 원본 문자열로 검색 시도
            } else {
                query
            }

            val results = repository.searchYouTubeVideos(finalQuery)
            _searchResults.value = results
            _isLoading.value = false
        }
    }

    // 💡 유튜브 URL에서 영상 고유 ID만 추출하는 정규식 함수
    private fun extractVideoIdFromUrl(url: String): String? {
        // youtu.be/xxx 형식이나 youtube.com/watch?v=xxx 형식 모두 처리
        val regex = "(?<=v=|v\\/|vi=|vi\\/|youtu.be\\/|\\/v\\/|\\/e\\/|watch\\?v=|&v=)([^#\\&\\?]*).*".toRegex()
        val matchResult = regex.find(url)
        return matchResult?.groups?.get(1)?.value
    }
}