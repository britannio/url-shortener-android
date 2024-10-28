package com.britannio.urlshort.db

import androidx.room.*
import com.britannio.urlshort.data.UrlData
import kotlinx.coroutines.flow.Flow

@Dao
interface UrlDao {
    @Query("SELECT * FROM urldata ORDER BY createdAt DESC")
    fun getAllUrls(): Flow<List<UrlData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrl(url: UrlData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUrls(urls: List<UrlData>)
}
