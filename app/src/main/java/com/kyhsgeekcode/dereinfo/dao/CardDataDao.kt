package com.kyhsgeekcode.dereinfo.dao

import androidx.room.Dao
import androidx.room.Query
import com.kyhsgeekcode.dereinfo.cardunit.CardDataModel

@Dao
interface CardDataDao {
    @Query("SELECT * FROM card_data")
    suspend fun getAllCardData(): List<CardDataModel>
}