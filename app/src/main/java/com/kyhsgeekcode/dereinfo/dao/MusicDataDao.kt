package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.model.MusicDataModel

@Dao
interface MusicDataDao {
    @Query("SELECT * FROM music_data")
    suspend fun getAllMusicData(): List<MusicDataModel>
}