package com.example.youtube_archive.ui

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun SearchScreen(
    searchQuery: String,                  // 상위에서 전달받을 검색어 상태
    onQueryChange: (String) -> Unit,      // 텍스트가 입력될 때 호출할 이벤트
    onSearchClick: () -> Unit             // 검색/다운로드 버튼 클릭 이벤트
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("유튜브 영상 추가", fontSize = 24.sp, modifier = Modifier.padding(bottom = 20.dp))

        // 모프-03에서 배운 OutlinedTextField 사용
        OutlinedTextField(
            value = searchQuery,
            onValueChange = onQueryChange, // 상태 호이스팅 (이벤트 위로 올리기)
            label = { Text("유튜브 URL 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 분석 및 다운로드 버튼
        Button(
            onClick = onSearchClick,
            modifier = Modifier.fillMaxWidth().height(50.dp)
        ) {
            Text("영상 정보 가져오기 및 저장")
        }

        Spacer(modifier = Modifier.height(30.dp))

        // 차후 이곳에 검색된 영상의 썸네일 미리보기가 추가될 수 있습니다.
        Box(
            modifier = Modifier.fillMaxWidth().weight(1f),
            contentAlignment = Alignment.Center
        ) {
            Text("URL을 입력하면 영상 정보가 나타납니다.", color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
    }
}