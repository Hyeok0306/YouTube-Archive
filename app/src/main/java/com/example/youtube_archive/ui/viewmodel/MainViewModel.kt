package com.example.youtube_archive.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import com.example.youtube_archive.data.local.VideoRepository
import com.example.youtube_archive.model.GoogleUser
import com.example.youtube_archive.model.Video
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

// 💡 팩토리나 주입을 통해 Repository를 전달받는 구조에 맞게 수정
class MainViewModel(private val repository: VideoRepository) : ViewModel() {

    init {
        // 앱 시작 시, ViewModel이 생성되면서 자동 체크
        checkAndInitializeDownloader()
    }

    private fun checkAndInitializeDownloader() {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val py = Python.getInstance()
                val module = py.getModule("downloader")
                // 파이썬 엔진 내 업데이트 함수 호출
                module.callAttr("update_yt_dlp")
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }

    private val _userState = MutableStateFlow<GoogleUser?>(null)
    val userState: StateFlow<GoogleUser?> = _userState.asStateFlow()

    private val _searchResult = MutableStateFlow<Video?>(null)
    val searchResult: StateFlow<Video?> = _searchResult.asStateFlow()

    private val _syncLog = MutableStateFlow<String>("")
    val syncLog: StateFlow<String> = _syncLog.asStateFlow()

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // ☁️ 구글 드라이브 가상 동기화 성공 목록을 관리하기 위한 상태 변수
    private val _syncedVideoIds = MutableStateFlow<Set<String>>(emptySet())

    // 📱 [진짜 Room DB 데이터 연동]
    // 로컬 데이터베이스와 클라우드 상태를 결합하여 UI용 리스트 최종 반환
    val archiveList: StateFlow<List<Video>> = repository.allVideos
        .combine(_syncedVideoIds) { localVideos, syncedIds ->
            localVideos.map { video ->
                video.copy(isSyncedWithDrive = syncedIds.contains(video.videoId))
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun login() {
        viewModelScope.launch {
            _isLoading.value = true
            _userState.value = repository.signInWithGoogle()
            _isLoading.value = false
        }
    }

    fun logout() {
        _userState.value = null
    }

    fun processYoutubeUrl(inputUrl: String) {
        val extractedId = repository.extractVideoId(inputUrl)
        if (extractedId != null) {
            val thumbnailUrl = "https://img.youtube.com/vi/$extractedId/maxresdefault.jpg"
            _searchResult.value = Video(
                videoId = extractedId,
                title = "아카이빙된 영상 ($extractedId)",
                thumbnailUrl = thumbnailUrl
            )
        } else {
            _searchResult.value = null
        }
    }

    // 💾 [진짜 Room DB 물리 저장 명령 전송]
    fun saveToArchive() {
        _searchResult.value?.let { video ->
            viewModelScope.launch {
                // 실제 스마트폰 기기 내부 Room SQLite 에 영구 저장 실행
                repository.insertVideo(video)
                _syncLog.value = "📱 기기 내부 저장소(Room DB SQLite 파일)에 물리적 저장이 완료되었습니다."
            }
        }
    }

    fun syncWithDrive() {
        viewModelScope.launch {
            repository.syncDatabaseWithGoogleDrive(archiveList.value).collect { log ->
                _syncLog.value = log
                if (log.contains("완료")) {
                    // 동기화가 끝나면 가상 상태 셋에 추가하여 배지 갱신
                    val currentIds = archiveList.value.map { it.videoId }.toSet()
                    _syncedVideoIds.value = _syncedVideoIds.value + currentIds
                }
            }
        }
    }
}