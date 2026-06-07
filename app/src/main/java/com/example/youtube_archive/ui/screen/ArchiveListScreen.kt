package com.example.youtube_archive.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.youtube_archive.data.local.AppDatabase
import com.example.youtube_archive.data.local.VideoRepository
import com.example.youtube_archive.ui.components.ArchiveItemCard
import com.example.youtube_archive.ui.viewmodel.ArchiveViewModel
import com.example.youtube_archive.ui.viewmodel.ArchiveViewModelFactory

@Composable
fun ArchiveListScreen(
    onVideoClick: (String) -> Unit,
    // ViewModelFactory를 사용하여 ViewModel을 올바르게 초기화합니다.
    viewModel: ArchiveViewModel = viewModel(
        factory = ArchiveViewModelFactory(
            VideoRepository(
                AppDatabase.getDatabase(LocalContext.current.applicationContext).videoDao()
            )
        )
    )
) {
    // ViewModel의 StateFlow를 Compose의 State로 변환하여 관찰합니다.
    val videoList by viewModel.allVideos.collectAsStateWithLifecycle(initialValue = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "내 아카이브",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // 리스트 UI
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // ⭕ 성능 최적화 및 중복 추가 멈춤 현상을 막기 위해 key를 'it.id'로 수정했습니다.
            items(
                items = videoList,
                key = { it.id }
            ) { video ->
                // ⭕ 새롭게 업데이트된 다운로드 콜백 파라미터를 연동했습니다.
                ArchiveItemCard(
                    video = video,
                    onClick = { onVideoClick(video.videoId) },
                    onDownloadClick = { targetVideo ->
                        // 뷰모델에 정의된 다운로드 시작 함수를 호출합니다.
                        // (만약 뷰모델의 함수명이 다르다면 성혁 학생 프로젝트 내 뷰모델의 다운로드 함수명으로 바꿔주세요!)
                        viewModel.startVideoDownload(targetVideo)
                    }
                )
            }
        }
    }
}