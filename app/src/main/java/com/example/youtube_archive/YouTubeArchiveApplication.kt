package com.example.youtube_archive

import android.app.Application
import com.chaquo.python.Python
import com.chaquo.python.android.AndroidPlatform

class YouTubeArchiveApplication : Application() {
    override fun onCreate() {
        super.onCreate()

        // 1. 앱 실행 시 파이썬 엔진 최초 1회 초기화
        if (!Python.isStarted()) {
            Python.start(AndroidPlatform(this))
        }
    }
}