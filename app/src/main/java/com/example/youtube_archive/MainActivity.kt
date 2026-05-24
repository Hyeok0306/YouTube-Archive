package com.example.youtube_archive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import androidx.room.Room
import com.example.youtube_archive.data.local.AppDatabase
import com.example.youtube_archive.data.local.VideoRepository
import com.example.youtube_archive.ui.screen.ArchiveListScreen
import com.example.youtube_archive.ui.screen.SearchScreen
import com.example.youtube_archive.ui.screen.VideoDetailScreen
import com.example.youtube_archive.ui.viewmodel.ArchiveViewModel
import com.example.youtube_archive.ui.viewmodel.ArchiveViewModelFactory
import kotlinx.serialization.Serializable

@Serializable object ArchiveList
@Serializable object Search
@Serializable data class VideoDetail(val videoId: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {

        super.onCreate(savedInstanceState)

        // 1. Room 데이터베이스 생성
        val db = Room.databaseBuilder(
            applicationContext,
            AppDatabase::class.java, "video_db"
        ).build()

        // 2. Repository 및 Factory 준비
        val repository = VideoRepository(db.videoDao())
        val viewModelFactory = ArchiveViewModelFactory(repository)

        setContent {
            // 3. ViewModel 주입
            val archiveViewModel: ArchiveViewModel = viewModel(factory = viewModelFactory)

            YouTubeArchiveApp(archiveViewModel)
        }
    }
}

@Composable
fun YouTubeArchiveApp(viewModel: ArchiveViewModel) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; navController.navigate(ArchiveList) },
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text("아카이브") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; navController.navigate(Search) },
                    icon = { Icon(Icons.Default.Search, null) },
                    label = { Text("추가") }
                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ArchiveList,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<ArchiveList> {
                // ViewModel을 전달하여 실제 DB 데이터를 보여줌
                ArchiveListScreen(
                    viewModel = viewModel,
                    onVideoClick = { url -> navController.navigate(VideoDetail(url)) }
                )
            }
            composable<Search> {
                // 1. 검색어를 임시로 저장할 상태 변수 만들기
                var searchQuery by remember { mutableStateOf("") }

                // 2. SearchScreen에 필요한 파라미터 전달하기
                SearchScreen(
                    searchQuery = searchQuery,
                    onQueryChange = { newQuery ->
                        searchQuery = newQuery // 사용자가 글자를 입력할 때마다 상태 업데이트
                    },
                    onSearchClick = {
                        // 검색 버튼을 눌렀을 때 실행될 로직 (나중에 API 연동 시 사용)
                        println("검색 테스트: $searchQuery")
                    }
                )
            }
            composable<VideoDetail> { backStackEntry ->
                val detail: VideoDetail = backStackEntry.toRoute()
                VideoDetailScreen(
                    videoId = detail.videoId,
                    onBackClick = { navController.popBackStack() }
                )
            }
        }
    }
}