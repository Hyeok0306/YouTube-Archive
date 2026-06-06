package com.example.youtube_archive.ui.screen

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.example.youtube_archive.ui.viewmodel.MainViewModel

@Composable
fun MainDemoScreen(viewModel: MainViewModel) {
    val user by viewModel.userState.collectAsState()
    val searchResult by viewModel.searchResult.collectAsState()
    val archiveList by viewModel.archiveList.collectAsState()
    val syncLog by viewModel.syncLog.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()

    var urlInput by remember { mutableStateOf("https://youtu.be/dQw4w9WgXcQ") }

    Column(modifier = Modifier.fillMaxSize().padding(16.dp)) {
        // 유저 섹션
        Button(onClick = { if(user == null) viewModel.login() else viewModel.logout() }, modifier = Modifier.fillMaxWidth()) {
            Text(if(user == null) "구글 로그인" else "${user!!.name} 로그아웃")
        }

        OutlinedTextField(value = urlInput, onValueChange = { urlInput = it }, label = { Text("URL 입력") }, modifier = Modifier.fillMaxWidth())
        Button(onClick = { viewModel.processYoutubeUrl(urlInput) }, modifier = Modifier.fillMaxWidth()) { Text("변환") }

        searchResult?.let { video ->
            Text("추출된 ID: ${video.videoId}", style = MaterialTheme.typography.bodySmall)
            AsyncImage(model = video.thumbnailUri, contentDescription = null, modifier = Modifier.height(100.dp))
            Button(onClick = { viewModel.saveToArchive() }) { Text("내부 저장소에 저장") }
        }

        Text("보관함 리스트", style = MaterialTheme.typography.headlineSmall, modifier = Modifier.padding(top = 16.dp))
        Button(onClick = { viewModel.syncWithDrive() }, enabled = user != null) { Text("클라우드 동기화") }

        Text(syncLog, color = MaterialTheme.colorScheme.primary)

        LazyColumn {
            items(archiveList) { video ->
                Row(modifier = Modifier.padding(8.dp), verticalAlignment = Alignment.CenterVertically) {
                    Text(video.title, modifier = Modifier.weight(1f))
                    Text(if(video.isSyncedWithDrive) "백업됨" else "로컬", color = if(video.isSyncedWithDrive) Color.Green else Color.Gray)
                }
            }
        }
    }
}