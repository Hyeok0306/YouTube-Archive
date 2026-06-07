package com.example.youtube_archive.ui.screen

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil3.compose.AsyncImage
import com.example.youtube_archive.ui.viewmodel.VideoDetailViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun VideoDetailScreen(
    videoInput: String,
    onBackClick: () -> Unit,
    viewModel: VideoDetailViewModel = viewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current



    LaunchedEffect(videoInput) {
        viewModel.loadVideoDetailsFromInput(videoInput)
    }

    LaunchedEffect(Unit) {
        viewModel.uiEvent.collect { snackbarHostState.showSnackbar(it) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
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
    ) { padding ->
        when (val state = uiState) {
            is VideoDetailUiState.Loading -> Box(Modifier.fillMaxSize(), Alignment.Center) { CircularProgressIndicator() }
            is VideoDetailUiState.Error -> Box(Modifier.fillMaxSize().padding(padding), Alignment.Center) { Text(state.message) }
            is VideoDetailUiState.Success -> {
                val currentId = viewModel.extractVideoId(videoInput) ?: ""

                Column(Modifier.padding(padding).padding(16.dp)) {
                    Card(modifier = Modifier.fillMaxWidth().height(200.dp)) {
                        AsyncImage(model = state.thumbnailUrl, contentDescription = null, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                    }

                    Spacer(Modifier.height(16.dp))
                    Text(state.title, style = MaterialTheme.typography.titleLarge)
                    Text("비디오 ID: $currentId", style = MaterialTheme.typography.bodySmall, color = MaterialTheme.colorScheme.outline)

                    Spacer(Modifier.height(24.dp))

                    // ⭕ 상세 화면 내부로 삭제 및 저장 버튼 로직 통합
                    if (!state.isSaved) {
                        Button(
                            onClick = { viewModel.saveVideo(currentId, state.title, state.thumbnailUrl) },
                            modifier = Modifier.fillMaxWidth().height(50.dp)
                        ) {
                            Text("보관함에 저장하기")
                        }
                    } else {
                        Column(modifier = Modifier.fillMaxWidth()) {
                            Card(
                                modifier = Modifier.fillMaxWidth().padding(bottom = 8.dp),
                                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.primaryContainer)
                            ) {
                                Text(
                                    text = "📌 이미 보관함에 아카이빙된 영상입니다.",
                                    modifier = Modifier.padding(16.dp),
                                    style = MaterialTheme.typography.bodyMedium
                                )
                            }
                            Button(
                                onClick = { viewModel.deleteVideo(currentId) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.error)
                            ) {
                                Text("보관함에서 삭제")
                            }
                        }
                    }

                    Spacer(Modifier.height(8.dp))

                    Button(
                        onClick = {
                            // ⭕ 외부 브라우저 호출 대신 뷰모델의 파이썬 실행 함수 호출
                            // context를 넘겨야 파이썬이 파일 저장 경로(context.filesDir.path)를 알 수 있습니다.
                            viewModel.startDownload(
                                videoUrl = "https://www.youtube.com/watch?v=$currentId",
                                videoId = currentId,
                                context = context
                            )
                        },
                        modifier = Modifier.fillMaxWidth().height(50.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.secondary)
                    ) {
                        Text("동영상 다운로드 시작")
                    }
                }
            }
        }
    }
}