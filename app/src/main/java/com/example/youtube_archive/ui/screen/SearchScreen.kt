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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SegmentedButton
import androidx.compose.material3.SegmentedButtonDefaults
import androidx.compose.material3.SingleChoiceSegmentedButtonRow
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.example.youtube_archive.model.Video
import com.example.youtube_archive.ui.components.ArchiveItemCard // ⭕ 보관함 카드 컴포넌트 임포트
import com.example.youtube_archive.ui.viewmodel.SearchViewModel
import kotlinx.coroutines.flow.collectLatest

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SearchScreen(
    viewModel: SearchViewModel = viewModel(),
    onVideoClick: (String) -> Unit = {} // ⭕ 상세 화면 이동을 위한 내비게이션 람다 추가
) {
    var searchQuery by remember { mutableStateOf("") }
    val searchResults by viewModel.searchResults.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val searchMode by viewModel.searchMode.collectAsState()

    val snackbarHostState = remember { SnackbarHostState() }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collectLatest { message ->
            snackbarHostState.showSnackbar(message)
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) }
    ) { paddingValues ->
        val options = listOf("키워드 검색", "URL 직접 입력")

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text("유튜브 영상 추가", fontSize = 24.sp, modifier = Modifier.padding(bottom = 16.dp))

            SingleChoiceSegmentedButtonRow(
                modifier = Modifier.fillMaxWidth().padding(bottom = 16.dp)
            ) {
                options.forEachIndexed { index, label ->
                    SegmentedButton(
                        shape = SegmentedButtonDefaults.itemShape(index = index, count = options.size),
                        selected = index == searchMode,
                        onClick = {
                            viewModel.setSearchMode(index)
                            searchQuery = ""
                        }
                    ) {
                        Text(label)
                    }
                }
            }

            OutlinedTextField(
                value = searchQuery,
                onValueChange = { searchQuery = it },
                label = { Text(if (searchMode == 0) "유튜브 검색어 입력" else "유튜브 URL 입력") },
                modifier = Modifier.fillMaxWidth()
            )

            Spacer(modifier = Modifier.height(16.dp))

            Button(
                onClick = {
                    if (searchMode == 0) {
                        viewModel.searchVideos(searchQuery)
                    } else {
                        viewModel.addVideoFromUrl(searchQuery)
                    }
                },
                modifier = Modifier.fillMaxWidth().height(50.dp),
                enabled = searchQuery.isNotBlank() && !isLoading
            ) {
                Text(if (isLoading) "처리 중..." else (if (searchMode == 0) "검색하기" else "영상 추가하기"))
            }

            Spacer(modifier = Modifier.height(20.dp))

            // 결과 리스트 표시 영역
            if (searchResults.isEmpty() && !isLoading) {
                Box(modifier = Modifier.fillMaxWidth().weight(1f), contentAlignment = Alignment.Center) {
                    Text(
                        text = if (searchMode == 0) "검색어를 입력하세요." else "URL을 입력하고 영상 추가를 누르세요.",
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            } else {
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f)) {
                    items(searchResults) { item ->
                        val videoId = item.id.videoId ?: "unknown"

                        // ⭕ [핵심 작업]: API 데이터 규격을 ArchiveItemCard가 요구하는 로컬 Video 모델로 즉석 변환
                        val mappedVideo = Video(
                            videoId = videoId,
                            title = item.snippet.title,
                            thumbnailUrl = item.snippet.thumbnails.high.url // 최상위 hq 해상도 바인딩
                        )

                        // ⭕ 아카이브 컴포넌트 재사용 적용
                        ArchiveItemCard(
                            video = mappedVideo,
                            onClick = {
                                // 카드를 클릭하면 상세 보기 정보 화면(videoInput)으로 이동하게 연결
                                onVideoClick(videoId)
                            },
                            onDownloadClick = { /* 상세 화면 내부에서 동작하도록 분리했으므로 여기선 비워둠 */ }
                        )
                    }
                }
            }
        }
    }
}