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

        // ✅ Python.start 관련 코드는 이제 Application 클래스에 있으므로 삭제!

        val database = AppDatabase.getDatabase(applicationContext)
        val repository = VideoRepository(database.videoDao())
        val viewModelFactory = MainViewModelFactory(repository)
        val mainViewModel = ViewModelProvider(this, viewModelFactory)[MainViewModel::class.java]

        setContent {
            YouTubeArchiveTheme {
                Surface(modifier = Modifier.fillMaxSize(), color = MaterialTheme.colorScheme.background) {
                    YouTubeArchiveApp(viewModel = mainViewModel)
                }
            }
        }
    }
}