package com.britannio.urlshort.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity
data class UrlData(
    @PrimaryKey(autoGenerate = true)
    val id: Int = 0,
    val originalUrl: String,
    val shortPath: String,
    val shortenedUrl: String,
    val createdAt: Long = System.currentTimeMillis()
)
