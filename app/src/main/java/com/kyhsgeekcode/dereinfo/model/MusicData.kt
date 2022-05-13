package com.kyhsgeekcode.dereinfo.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.kyhsgeekcode.dereinfo.enums.CircleType
import com.wanakanajava.WanaKanaJava
import java.io.Serializable

typealias musicID = Int

@Entity(tableName = "music_data")
class MusicData(
    @PrimaryKey val id: Int,
    val name: String,
    val bpm: Int,
    val composer: String,
    val lyricist: String,
    val sound_offset: Int,
    val sound_length: Int,
    val circleType: Int = 4,
    val name_kana: String = ""
) : Serializable {
    override fun toString(): String {
        val lineSeparator = System.lineSeparator()
        return StringBuilder("id:").append(id).append(lineSeparator)
            .append("name:").append(name).append(lineSeparator)
            .append("romanji name:").append(WanaKanaJava.toRomaji(name_kana)).append(lineSeparator)
            .append("bpm:").append(bpm).append(lineSeparator)
            .append("composer:").append(composer).append(lineSeparator)
            .append("lyricist:").append(lyricist).append(lineSeparator)
            .append("offset:").append(sound_offset).append(lineSeparator)
            .append("duration:").append(sound_length).append(lineSeparator)
            .append("type:").append(CircleType.getDesc(circleType)).toString()
    }

    fun getColor(): Int = CircleType.makeRGB(CircleType.getColor(circleType))
}
