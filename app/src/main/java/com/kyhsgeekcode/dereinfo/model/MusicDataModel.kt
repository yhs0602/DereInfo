package com.kyhsgeekcode.dereinfo.model

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "music_data")
data class MusicDataModel(
    @PrimaryKey val id: Int,
    val name: String,
    val bpm: Int,
    val composer: String,
    val lyricist: String,
    val sound_offset: Int,
    val sound_length: Int,
    val name_kana: String,
)