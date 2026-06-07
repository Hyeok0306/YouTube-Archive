package com.example.youtube_archive.ui

import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.toRoute
import com.example.youtube_archive.ui.screen.ArchiveListScreen
import com.example.youtube_archive.ui.screen.MainDemoScreen
import com.example.youtube_archive.ui.screen.SearchScreen
import com.example.youtube_archive.ui.screen.VideoDetailScreen
import com.example.youtube_archive.ui.viewmodel.MainViewModel
import kotlinx.serialization.Serializable

@Serializable object ArchiveList
@Serializable object Search
@Serializable data class VideoDetail(val videoId: String)
@Serializable object DriveDemo

@Composable
fun YouTubeArchiveApp(viewModel: MainViewModel) {
    val navController = rememberNavController()
    var selectedTab by remember { mutableIntStateOf(0) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                NavigationBarItem(
                    selected = selectedTab == 0,
                    onClick = { selectedTab = 0; navController.navigate(ArchiveList) },
                    icon = {},
                    label = { Text("보관함", style = if(selectedTab==0) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium) }
                )
                NavigationBarItem(
                    selected = selectedTab == 1,
                    onClick = { selectedTab = 1; navController.navigate(Search) },
                    icon = {},
                    label = { Text("추가", style = if(selectedTab==1) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium) }
                )
//                NavigationBarItem(
//                    selected = selectedTab == 2,
//                    onClick = { selectedTab = 2; navController.navigate(DriveDemo) },
//                    icon = {},
//                    label = { Text("클라우드", style = if(selectedTab==2) MaterialTheme.typography.titleMedium else MaterialTheme.typography.bodyMedium) }
//                )
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = ArchiveList,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable<ArchiveList> {
                ArchiveListScreen(onVideoClick = { videoId -> navController.navigate(VideoDetail(videoId)) })
            }

            // ⭕ [원인 해결 부분] SearchScreen에 클릭 이벤트를 연결하여 상세 정보 화면으로 내비게이션 이동 트리거
            composable<Search> {
                SearchScreen(
                    onVideoClick = { clickedVideoId ->
                        // 검색 결과 카드를 터치하면 해당 영상의 ID를 실어서 VideoDetail 화면으로 이동시킵니다.
                        navController.navigate(VideoDetail(clickedVideoId))
                    }
                )
            }

            composable<VideoDetail> { backStackEntry ->
                val detail: VideoDetail = backStackEntry.toRoute()
                VideoDetailScreen(
                    videoInput = detail.videoId,
                    onBackClick = { navController.popBackStack() }
                )
            }

            composable<DriveDemo> {
                MainDemoScreen(viewModel = viewModel)
            }
        }
    }
}
