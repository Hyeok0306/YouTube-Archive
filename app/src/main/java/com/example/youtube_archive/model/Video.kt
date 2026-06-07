package com.example.youtube_archive.model

import kotlinx.serialization.Serializable

@Serializable
data class Video(
    val id: Long = 0L,
    val videoId: String,
    val title: String,
    val thumbnailUrl: String, // 대화 나누며 통일한 명칭 (Urls)
    // 클라우드 백업 기능을 UI에서 빼더라도, 기존 뷰모델/리포지토리 코드에서
    // 이 필드를 참조해 에러가 나는 것을 막기 위해 기본값 false로 유지합니다.
    val isSyncedWithDrive: Boolean = false
) {
    val videoUrl: String get() = "https://www.youtube.com/watch?v=$videoId"
}

// 💡 중요: MainViewModel이나 Repository가 이 클래스를 import하여 참조하고 있으므로,
// 에러(Unresolved reference 'GoogleUser')가 나지 않도록 형태만 유지해 줍니다.
// 실제 연동은 하지 않으므로 빈 껍데기 값만 다루게 됩니다.
data class GoogleUser(
    val name: String = "게스트 유저",
    val email: String = "guest@example.com",
    val profilePictureUrl: String = ""
)