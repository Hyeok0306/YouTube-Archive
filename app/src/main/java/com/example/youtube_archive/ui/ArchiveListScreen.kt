package com.example.youtube_archive.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.PlayArrow
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

// 임시 데이터 모델 (나중에 Room DB Entity로 교체됩니다)
data class DummyVideo(val id: String, val title: String, val duration: String)

@Composable
fun ArchiveListScreen(
    videoList: List<DummyVideo>,           // 저장된 영상 목록 데이터
    onVideoClick: (String) -> Unit         // 개별 영상 클릭 시 호출될 이벤트 (videoId 전달)
) {
    Column(modifier = Modifier.fillMaxSize()) {
        Text(
            text = "내 아카이브",
            style = MaterialTheme.typography.headlineMedium,
            modifier = Modifier.padding(16.dp)
        )

        // 모프-05: 대량 데이터 표시를 위한 LazyColumn
        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            // 리스트 아이템 렌더링
            items(videoList) { video ->
                ArchiveItemCard(
                    video = video,
                    onClick = { onVideoClick(video.id) }
                )
            }
        }
    }
}

@Composable
fun ArchiveItemCard(video: DummyVideo, onClick: () -> Unit) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Row(
            modifier = Modifier.padding(16.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // 차후 Coil을 활용해 실제 썸네일 이미지로 교체될 영역
            Icon(
                imageVector = Icons.Default.PlayArrow,
                contentDescription = "썸네일 임시",
                modifier = Modifier.size(60.dp)
            )
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(text = video.title, style = MaterialTheme.typography.titleMedium)
                Text(text = "길이: ${video.duration}", style = MaterialTheme.typography.bodyMedium)
            }
        }
    }
}