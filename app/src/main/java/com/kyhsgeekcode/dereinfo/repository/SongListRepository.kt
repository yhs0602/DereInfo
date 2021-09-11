package com.kyhsgeekcode.dereinfo.repository

import androidx.lifecycle.LiveData
import com.kyhsgeekcode.dereinfo.database.SongDatabase
import com.kyhsgeekcode.dereinfo.entity.SongEntity

class SongListRepository(mDatabase: SongDatabase) {

    private val songListDao = mDatabase.songListDao()
    val allSongs: LiveData<List<SongEntity>> = songListDao.getAlphabetizedSongs()

    companion object{
        private var sInstance: SongListRepository? = null
        fun getInstance(database: SongDatabase): SongListRepository {
            return sInstance
                ?: synchronized(this){
                    val instance = SongListRepository(database)
                    sInstance = instance
                    instance
                }
        }
    }
}