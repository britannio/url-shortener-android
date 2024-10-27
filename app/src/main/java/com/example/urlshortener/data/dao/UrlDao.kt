@Dao
interface UrlDao {
    @Query("SELECT * FROM shortened_urls ORDER BY createdAt DESC")
    fun getAllUrls(): Flow<List<ShortenedUrl>>
    
    @Insert
    suspend fun insertUrl(url: ShortenedUrl)
    
    @Delete
    suspend fun deleteUrl(url: ShortenedUrl)
    
    @Query("SELECT * FROM shortened_urls WHERE shortPath = :shortPath")
    suspend fun getUrlByShortPath(shortPath: String): ShortenedUrl?
}
