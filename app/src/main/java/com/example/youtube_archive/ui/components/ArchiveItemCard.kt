package com.example.youtube_archive.ui.components


import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
// import androidx.compose.material3.Button // 주석 처리
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
// import androidx.compose.material3.HorizontalDivider // 주석 처리
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
// import androidx.compose.runtime.getValue // 주석 처리
// import androidx.compose.runtime.mutableStateOf // 주석 처리
// import androidx.compose.runtime.remember // 주석 처리
// import androidx.compose.runtime.setValue // 주석 처리
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
// ⭕ 아래 import 문으로 교체하세요
import coil3.compose.AsyncImage
import com.example.youtube_archive.model.Video

@Composable
fun ArchiveItemCard(
    video: Video,
    onClick: () -> Unit,
    onDownloadClick: (Video) -> Unit
) {
    // var isExpanded by remember { mutableStateOf(false) } // 주석 처리

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .animateContentSize()
            .clickable {
                // isExpanded = !isExpanded // 주석 처리
                onClick() // 내비게이션 기능만 유지
            },
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth()) {
            // [상단] 영상 정보 영역
            Row(modifier = Modifier.padding(16.dp)) {
                AsyncImage(
                    model = video.thumbnailUrl,
                    contentDescription = "영상 썸네일",
                    modifier = Modifier.size(80.dp),
                    contentScale = ContentScale.Crop
                )
                Spacer(modifier = Modifier.width(16.dp))
                Text(
                    text = video.title,
                    style = MaterialTheme.typography.titleMedium
                )
            }

            /* // [하단] 확장 영역 (버튼) - 모두 주석 처리
            if (isExpanded) {
                HorizontalDivider(modifier = Modifier.padding(horizontal = 16.dp))
                Button(
                    onClick = { onDownloadClick(video) },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    Text("동영상 다운로드 시작")
                }
            }
            */
        }
    }
}