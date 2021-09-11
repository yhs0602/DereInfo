package com.kyhsgeekcode.dereinfo.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "songs")
data class SongEntity(
    @PrimaryKey
    val id: Int,
    val name: String,
    val bpm: Int,
    val composer: String,
    val lyricist: String,
    val soundOffset: Int,
    val soundLength: Int,
    val circleType: Int = 4,
    val nameKana: String = ""
)
