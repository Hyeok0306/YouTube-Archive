package com.example.youtube_archive.ui.screen

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.youtube_archive.ui.components.ArchiveItemCard
import com.example.youtube_archive.ui.viewmodel.ArchiveViewModel

@Composable
fun ArchiveListScreen(
    // viewModel() 함수를 사용해 ViewModel을 주입받습니다.
    viewModel: ArchiveViewModel = viewModel(),
    onVideoClick: (String) -> Unit
) {
    // ViewModel의 StateFlow를 Compose의 State로 변환하여 관찰합니다.
    val videoList by viewModel.allVideos.collectAsState(initial = emptyList())

    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "내 아카이브",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // 데이터 추가 테스트 버튼
        Button(
            onClick = { viewModel.addTestVideo() },
            modifier = Modifier.padding(horizontal = 16.dp)
        ) {
            Text("데이터 추가 테스트")
        }

        // 리스트 UI
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // key를 추가하여 성능 최적화
            items(items = videoList, key = { it.id }) { video ->
                ArchiveItemCard(
                    video = video,
                    onClick = { onVideoClick(video.videoUrl) }
                )
            }
        }
    }
}