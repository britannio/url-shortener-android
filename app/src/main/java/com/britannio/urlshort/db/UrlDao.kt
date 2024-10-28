package com.britannio.urlshort.db

import androidx.room.*
import com.britannio.urlshort.data.UrlData
import kotlinx.coroutines.flow.Flow

@Dao
interface UrlDao {
    @Query("SELECT * FROM urldata ORDER BY updatedAt DESC")
    fun getAll(): Flow<List<UrlData>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(url: UrlData)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(urls: List<UrlData>)

    @Query("DELETE FROM urldata")
    suspend fun deleteAll()
}
