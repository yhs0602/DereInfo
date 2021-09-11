package com.kyhsgeekcode.dereinfo.dao

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.entity.SongEntity

@Dao
interface SongListDao {

    @Query("SELECT * from songs ORDER BY name ASC")
    fun getAlphabetizedSongs(): LiveData<List<SongEntity>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insert(songEntity: SongEntity)

    @Query("DELETE FROM songs")
    suspend fun deleteAll()
}