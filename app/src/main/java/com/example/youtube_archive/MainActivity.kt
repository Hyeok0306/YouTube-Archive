package com.example.youtube_archive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.ViewModelProvider
import com.example.youtube_archive.data.local.AppDatabase
import com.example.youtube_archive.data.local.VideoRepository
import com.example.youtube_archive.ui.YouTubeArchiveApp
import com.example.youtube_archive.ui.theme.YouTubeArchiveTheme
import com.example.youtube_archive.ui.viewmodel.MainViewModel
import com.example.youtube_archive.ui.viewmodel.MainViewModelFactory

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 1. 로컬 DB 및 레포지토리 초기화
        val database = AppDatabase.getDatabase(applicationContext)
        val repository = VideoRepository(database.videoDao())

        // 2. 뷰모델 팩토리 생성 (MainViewModelFactory 사용)
        val viewModelFactory = MainViewModelFactory(repository)

        // 3. 뷰모델 생성 (MainViewModel 사용)
        val mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            YouTubeArchiveTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    // 4. MainViewModel을 YouTubeArchiveApp으로 전달
                    YouTubeArchiveApp(viewModel = mainViewModel)
                }
            }
        }
    }
}
