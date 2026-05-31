package com.example.youtube_archive.data.remote

import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitClient {
    private const val BASE_URL = "https://www.googleapis.com/youtube/v3/"

    // 통신할 때 로그를 찍어주는 도구 (에러 원인 파악에 필수!)
    private val loggingInterceptor = HttpLoggingInterceptor().apply {
        level = HttpLoggingInterceptor.Level.BODY
    }

    private val client = OkHttpClient.Builder()
        .addInterceptor(loggingInterceptor)
        .build()

    val apiService: YouTubeApiService by lazy {
        Retrofit.Builder()
            .baseUrl(BASE_URL)
            .client(client) // 로깅 클라이언트 장착
            .addConverterFactory(GsonConverterFactory.create()) // JSON -> Kotlin 객체 자동 변환
            .build()
            .create(YouTubeApiService::class.java)
    }
}