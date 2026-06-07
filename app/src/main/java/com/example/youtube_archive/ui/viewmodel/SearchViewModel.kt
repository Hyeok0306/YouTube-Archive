package com.example.youtube_archive.ui.viewmodel

import android.app.Application
import android.util.Log
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.youtube_archive.data.local.AppDatabase
import com.example.youtube_archive.data.local.VideoRepository
import com.example.youtube_archive.data.remote.YouTubeSearchItem
import com.example.youtube_archive.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject

class SearchViewModel(application: Application) : AndroidViewModel(application) {
    private val database = AppDatabase.getDatabase(application)
    private val repository = VideoRepository(database.videoDao())

    private val _searchResults = MutableStateFlow<List<YouTubeSearchItem>>(emptyList())
    val searchResults: StateFlow<List<YouTubeSearchItem>> = _searchResults.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ⭕ 결과 메시지 전달용 SharedFlow
    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent = _uiEvent.asSharedFlow()

    private val _searchMode = MutableStateFlow(0)
    val searchMode: StateFlow<Int> = _searchMode.asStateFlow()

    fun setSearchMode(mode: Int) {
        _searchMode.value = mode
        _searchResults.value = emptyList()
    }

    fun searchVideos(query: String) {
        if (query.isBlank()) return
        viewModelScope.launch {
            _isLoading.value = true
            val finalQuery = if (_searchMode.value == 1) extractVideoId(query) ?: query else query
            val results = repository.searchYouTubeVideos(finalQuery)
            _searchResults.value = results.distinctBy { it.id.videoId }
            _isLoading.value = false
        }
    }

    fun addVideoFromUrl(url: String) {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                // ⭕ 네트워크 통신은 IO 스레드에서 수행
                val result = withContext(Dispatchers.IO) {
                    val client = OkHttpClient()
                    val oEmbedUrl = "https://www.youtube.com/oembed?url=$url&format=json"
                    val request = Request.Builder().url(oEmbedUrl).build()
                    val response = client.newCall(request).execute()

                    if (!response.isSuccessful) return@withContext null

                    val responseBody = response.body?.string() ?: return@withContext null
                    val json = JSONObject(responseBody)

                    val title = json.getString("title")
                    val thumbnailUrl = json.getString("thumbnail_url")
                    val videoId = extractVideoId(url) ?: return@withContext null

                    Video(videoId = videoId, title = title, thumbnailUrl = thumbnailUrl)
                }

                if (result != null) {
                    repository.insertVideo(result)
                    _uiEvent.emit("영상 추가 성공: ${result.title}") // ⭕ 성공 이벤트
                } else {
                    _uiEvent.emit("추가 실패: 잘못된 URL이거나 정보를 찾을 수 없습니다.")
                }
            } catch (e: Exception) {
                _uiEvent.emit("네트워크 오류 발생")
                Log.e("SearchViewModel", "Error: ${e.message}")
            } finally {
                _isLoading.value = false
            }
        }
    }

    private fun extractVideoId(url: String): String? {
        val regex = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/)([a-zA-Z0-9_-]{11})".toRegex()
        return regex.find(url)?.value
    }
}