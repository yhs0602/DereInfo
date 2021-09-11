package com.kyhsgeekcode.dereinfo.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import com.kyhsgeekcode.dereinfo.dao.SongListDao
import com.kyhsgeekcode.dereinfo.entity.SongEntity

@Database(entities = [SongEntity::class], version = 1, exportSchema = false)
abstract class SongDatabase : RoomDatabase() {

    abstract fun songListDao(): SongListDao

    companion object {
        @Volatile
        private var INSTANCE: SongDatabase? = null
        fun getDatabase(
            context: Context,
//            scope: CoroutineScope
        ): SongDatabase {
            // if the INSTANCE is not null, then return it,
            // if it is, then create the database
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    SongDatabase::class.java,
                    "song_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                // return instance
                instance
            }
        }
    }
}
