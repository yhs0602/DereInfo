package com.kyhsgeekcode.dereinfo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "live_data")
data class LiveDataModel(
    @PrimaryKey val id: Int,
    val music_data_id: Int,
    val difficulty_1: Int,
    val difficulty_2: Int,
    val difficulty_3: Int,
    val difficulty_4: Int,
    val circle_type: Int,
    val type: Int,
    val sp_type: Int,
    val difficulty_5: Int,
    val difficulty_101: Int,
    val difficulty_11: Int,
    val difficulty_12: Int,
    val difficulty_21: Int,
    val difficulty_22: Int,
    val difficulty_6: Int,
)