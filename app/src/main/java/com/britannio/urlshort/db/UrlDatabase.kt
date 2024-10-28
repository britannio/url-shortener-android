package com.britannio.urlshort.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.britannio.urlshort.data.UrlData

@Database(entities = [UrlData::class], version = 1)
abstract class UrlDatabase : RoomDatabase() {
    abstract fun urlDao(): UrlDao
}
