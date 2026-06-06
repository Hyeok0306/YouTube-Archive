package com.example.youtube_archive.ui.viewmodel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.example.youtube_archive.data.local.VideoRepository

// 🧠 ViewModel에 Repository를 주입하기 위한 전용 팩토리 클래스
class MainViewModelFactory(
    private val repository: VideoRepository
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
            return MainViewModel(repository) as T
        }
        throw IllegalArgumentException("알 수 없는 ViewModel 클래스입니다.")
    }
}