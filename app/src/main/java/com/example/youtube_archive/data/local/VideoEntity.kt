import androidx.room.Entity
import androidx.room.PrimaryKey

// data/local/entity/VideoEntity.kt

@Entity(tableName = "video_table")
data class VideoEntity(
    @PrimaryKey(autoGenerate = true) // 1. 자동으로 1, 2, 3... 증가하는 번호 생성
    val id: Long = 0,               // 2. 이게 진짜 기본 키가 됩니다.

    val videoId: String,            // 3. 이제 중복이 허용됩니다.
    val title: String,
    val thumbnailUrl: String,
    // ... 나머지 필드들
)