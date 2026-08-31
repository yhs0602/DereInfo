package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.model.LiveDataModel

@Dao
interface LiveDataDao {
    @Query("SELECT * FROM live_data")
    suspend fun getAllLiveData(): List<LiveDataModel>
}