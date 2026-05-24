package com.example.youtube_archive.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    videoId: String,             // 화면을 그리기 위해 필요한 데이터 (ID)
    onBackClick: () -> Unit      // 상단 뒤로가기 버튼 이벤트
) {
    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("영상 상세 정보") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "뒤로가기")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .padding(paddingValues)
                .padding(16.dp)
                .fillMaxSize()
        ) {
            // 1. 영상 플레이어 영역 (임시 플레이스홀더)
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(200.dp)
            ) {
                // 여기에 차후 유튜브 플레이어 또는 다운로드된 영상 플레이어가 들어갑니다.
            }

            Spacer(modifier = Modifier.height(24.dp))

            // 2. 영상 기본 정보
            Text("전달받은 영상 ID: $videoId", style = MaterialTheme.typography.titleLarge)
            Spacer(modifier = Modifier.height(8.dp))
            Text("여기에 유튜브 Data API로 가져온 설명이 들어갑니다.")

            Spacer(modifier = Modifier.height(24.dp))
            Divider()
            Spacer(modifier = Modifier.height(24.dp))

            // 3. AI 요약 브리핑 영역
            Text("✨ AI 핵심 요약 브리핑", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.primary)
            Spacer(modifier = Modifier.height(8.dp))
            Card(
                modifier = Modifier.fillMaxWidth(),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
            ) {
                Text(
                    text = "API 연동 전 임시 요약 텍스트입니다. 영상의 자막 데이터를 분석하여 여기에 세 줄 요약 등이 표시됩니다.",
                    modifier = Modifier.padding(16.dp)
                )
            }
        }
    }
}