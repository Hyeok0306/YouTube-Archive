package com.example.youtube_archive.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.youtube_archive.ui.viewmodel.SearchViewModel

@OptIn(ExperimentalMaterial3Api::class) // SegmentedButton 사용을 위한 어노테이션
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel()
) {
    var searchQuery by remember { mutableStateOf("") }

    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchMode by viewModel.searchMode.collectAsState()

    // 상단 탭 옵션
    val options = listOf("키워드 검색", "URL 직접 입력")

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(16.dp),
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text("유튜브 영상 추가", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

        // 💡 모프-05에서 배운 SingleChoiceSegmentedButtonRow 적용
        SingleChoiceSegmentedButtonRow(
            modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
        ) {
            options.forEachIndexed { index, label ->
                SegmentedButton(
                    shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                    selected = index == searchMode,
                    onClick = {
                        viewModel.setSearchMode(index)
                        searchQuery = "" // 탭을 바꾸면 입력창 비우기
                    }
                ) {
                    Text(label)
                }
            }
        }

        // 입력창 (모드에 따라 라벨 텍스트 변경)
        OutlinedTextField(
            value = searchQuery,
            onValueChange = { searchQuery = it },
            label = { Text(if (searchMode == 0) "유튜브 검색어 입력" else "유튜브 URL 입력") },
            modifier = Modifier.fillMaxWidth()
        )

        Spacer(modifier = Modifier.height(16.dp))

        // 검색 버튼
        Button(
            onClick = { viewModel.searchVideos(searchQuery) },
            modifier = Modifier.fillMaxWidth().height(50.dp),
            enabled = !isLoading
        ) {
            Text(if (isLoading) "불러오는 중..." else "영상 정보 가져오기")
        }

        Spacer(modifier = Modifier.height(20.dp))

        // 결과 화면 표시 영역
        if (searchResults.isEmpty() && !isLoading) {
            Box(
                modifier = Modifier.fillMaxWidth().weight(1f),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = if (searchMode == 0) "검색어를 입력하면 영상 정보가 나타납니다." else "URL을 입력하면 영상 정보가 나타납니다.",
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                items(searchResults) { item ->
                    Card(
                        modifier = Modifier.padding(vertical = 4.dp).fillMaxWidth(),
                        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surfaceVariant)
                    ) {
                        Column(modifier = Modifier.padding(12.dp)) {
                            Text(text = "제목: ${item.snippet.title}", style = MaterialTheme.typography.titleMedium)
                            Spacer(modifier = Modifier.height(4.dp))
                            Text(text = "비디오 ID: ${item.id.videoId ?: "ID 없음"}", style = MaterialTheme.typography.bodyMedium)
                        }
                    }
                }
            }
        }
    }
}
