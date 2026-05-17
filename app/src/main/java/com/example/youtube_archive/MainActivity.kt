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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.youtube_archive.ui.ArchiveListScreen
import com.example.youtube_archive.ui.DummyVideo
import com.example.youtube_archive.ui.SearchScreen
import com.example.youtube_archive.ui.VideoDetailScreen
import kotlinx.serialization.Serializable

// Route 정의
@Serializable object ArchiveList
@Serializable object Search
@Serializable data class VideoDetail(val videoId: String)

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            YouTubeArchiveApp()
        }
    }
}

@Composable
fun YouTubeArchiveApp() {
    val navController = rememberNavController()
    var selectedTab by remember { mutableStateOf(0) }

    Scaffold(
        bottomBar = {
            // 하단 탭 바
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; navController.navigate(ArchiveList) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Home, contentDescription = null) },
                    label = { Text("아카이브") }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; navController.navigate(Search) { launchSingleTop = true } },
                    icon = { Icon(Icons.Default.Search, contentDescription = null) },
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
            // 1. 목록 화면
            composable<ArchiveList> {
                // 더미 데이터 리스트
                val dummyData = listOf(
                    DummyVideo("vid1", "모바일 프로그래밍 1강", "45:00"),
                    DummyVideo("vid2", "Jetpack Compose 기초", "12:30")
                )
                ArchiveListScreen(
                    videoList = dummyData,
                    onVideoClick = { videoId ->
                        navController.navigate(VideoDetail(videoId))
                    }
                )
            }

            // 2. 검색 화면
            composable<Search> {
                // 임시로 화면 내부에 State를 둡니다 (나중엔 ViewModel로 이동)
                var urlText by remember { mutableStateOf("") }

                SearchScreen(
                    searchQuery = urlText,
                    onQueryChange = { urlText = it },
                    onSearchClick = {
                        // 검색 로직 실행 (지금은 빈 칸)
                        println("검색 클릭됨: $urlText")
                    }
                )
            }

            // 3. 상세 화면
            composable<VideoDetail> { backStackEntry ->
                val detail: VideoDetail = backStackEntry.toRoute()
                VideoDetailScreen(
                    videoId = detail.videoId,
                    onBackClick = { navController.popBackStack() } // 뒤로가기 동작
                )
            }
        }
    }
}