data class ShortenedUrl(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val originalUrl: String,
    val shortPath: String,
    val createdAt: Long = System.currentTimeMillis()
)
