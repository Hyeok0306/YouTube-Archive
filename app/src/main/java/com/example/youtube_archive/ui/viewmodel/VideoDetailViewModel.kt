package com.example.youtube_archive.ui.viewmodel

import android.app.Application
import android.content.ContentValues
import android.content.Context
import android.os.Environment
import android.provider.MediaStore
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chaquo.python.Python
import com.example.youtube_archive.data.local.AppDatabase
import com.example.youtube_archive.data.local.VideoRepository
import com.example.youtube_archive.model.Video
import com.example.youtube_archive.ui.screen.VideoDetailUiState
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.json.JSONObject
import java.io.File
import java.io.FileInputStream

// 파이썬 다운로드 진행률 콜백 인터페이스
interface DownloadCallback {
    fun onProgressUpdate(progress: Int)
}

class VideoDetailViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = VideoRepository(AppDatabase.getDatabase(application).videoDao())

    private val _uiState = MutableStateFlow<VideoDetailUiState>(VideoDetailUiState.Loading)
    val uiState: StateFlow<VideoDetailUiState> = _uiState.asStateFlow()

    private val _uiEvent = MutableSharedFlow<String>()
    val uiEvent: SharedFlow<String> = _uiEvent.asSharedFlow()

    // 다운로드 진행률 상태
    private val _downloadProgress = MutableStateFlow(0)
    val downloadProgress: StateFlow<Int> = _downloadProgress.asStateFlow()

    fun extractVideoId(url: String): String? {
        if (url.isBlank()) return null
        if (url.length == 11 && url.matches("[a-zA-Z0-9_-]{11}".toRegex())) {
            return url
        }
        val regex = "(?<=watch\\?v=|/videos/|embed\\/|youtu.be\\/|v\\/|\\/e\\/|watch\\?.*v=)([a-zA-Z0-9_-]{11})".toRegex()
        return regex.find(url)?.value
    }

    fun loadVideoDetailsFromInput(inputUrl: String) {
        val parsedId = extractVideoId(inputUrl)
        if (parsedId != null) {
            loadVideoDetails(parsedId)
        } else {
            viewModelScope.launch {
                _uiState.value = VideoDetailUiState.Error("유효한 유튜브 링크 또는 비디오 ID가 아닙니다.")
            }
        }
    }

    private fun loadVideoDetails(videoId: String) {
        viewModelScope.launch {
            _uiState.value = VideoDetailUiState.Loading
            val result = withContext(Dispatchers.IO) {
                val infoState = fetchVideoInfo(videoId)
                if (infoState is VideoDetailUiState.Success) {
                    val isAlreadySaved = repository.isVideoSaved(videoId)
                    infoState.copy(isSaved = isAlreadySaved)
                } else {
                    infoState
                }
            }
            _uiState.value = result
        }
    }

    private suspend fun fetchVideoInfo(videoId: String): VideoDetailUiState = withContext(Dispatchers.IO) {
        val client = OkHttpClient()
        val url = "https://www.youtube.com/oembed?url=https://www.youtube.com/watch?v=$videoId&format=json"
        val request = Request.Builder().url(url).build()
        try {
            val response = client.newCall(request).execute()
            if (response.isSuccessful) {
                val json = JSONObject(response.body?.string() ?: "")
                VideoDetailUiState.Success(
                    title = json.getString("title"),
                    thumbnailUrl = json.getString("thumbnail_url"),
                    isSaved = false
                )
            } else {
                VideoDetailUiState.Error("영상을 찾을 수 없습니다.")
            }
        } catch (e: Exception) {
            VideoDetailUiState.Error("오류: ${e.message}")
        }
    }

    fun saveVideo(videoId: String, title: String, thumbUrl: String) {
        viewModelScope.launch {
            repository.insertVideo(Video(videoId = videoId, title = title, thumbnailUrl = thumbUrl))
            _uiEvent.emit("보관함에 저장되었습니다!")
            val currentState = _uiState.value
            if (currentState is VideoDetailUiState.Success) {
                _uiState.value = currentState.copy(isSaved = true)
            }
        }
    }

    fun deleteVideo(videoId: String) {
        viewModelScope.launch {
            val isDeleted = repository.deleteVideoById(videoId)
            if (isDeleted) {
                _uiEvent.emit("보관함에서 삭제되었습니다.")
                val currentState = _uiState.value
                if (currentState is VideoDetailUiState.Success) {
                    _uiState.value = currentState.copy(isSaved = false)
                }
            } else {
                _uiEvent.emit("삭제에 실패했습니다.")
            }
        }
    }

    // 파이썬 연동 다운로드 기능
    fun startDownload(videoUrl: String, videoId: String, context: Context) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                _uiEvent.emit("다운로드를 시작합니다...")
                val py = Python.getInstance()
                val module = py.getModule("downloader")

                val callback = object : DownloadCallback {
                    override fun onProgressUpdate(progress: Int) {
                        _downloadProgress.value = progress
                    }
                }

                val resultJson = module.callAttr(
                    "start_download",
                    videoUrl,
                    context.filesDir.path,
                    "video",
                    videoId,
                    callback
                ).toString()

                val result = JSONObject(resultJson)
                if (result.getString("status") == "success") {
                    val filePath = result.getString("file_path")
                    val fileName = File(filePath).name // 파일 이름 추출

                    // ⭕ 다운로드 성공 후 다운로드 폴더로 복사 실행
                    copyFileToDownloads(context, filePath, fileName)

                    _uiEvent.emit("다운로드 완료! 'Download/YouTubeArchive' 폴더를 확인하세요.")
                }
            } catch (e: Exception) {
                _uiEvent.emit("다운로드 실패: ${e.message}")
            }
        }
    }

    fun copyFileToDownloads(context: Context, internalFilePath: String, fileName: String) {
        val inputFile = File(internalFilePath)
        if (!inputFile.exists()) return

        val contentValues = ContentValues().apply {
            put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
            put(MediaStore.MediaColumns.MIME_TYPE, "video/mp4")
            // 다운로드 폴더 안의 YouTubeArchive 폴더로 지정
            put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS + "/YouTubeArchive")
        }

        val resolver = context.contentResolver
        val uri = resolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, contentValues)

        uri?.let { destUri ->
            try {
                resolver.openOutputStream(destUri)?.use { outputStream ->
                    FileInputStream(inputFile).use { inputStream ->
                        inputStream.copyTo(outputStream)
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }
    }
}