package com.kyhsgeekcode.dereinfo.model

import com.wanakanajava.WanaKanaJava
import java.io.Serializable

typealias musicID = Int

class MusicInfo(
    val id: Int,
    val name: String,
    val bpm: Int,
    val composer: String,
    val lyricist: String,
    val soundOffset: Int,
    val soundLength: Int,
    val circleType: Int = 4,
    val nameKana: String = ""
) : Serializable {
    override fun toString(): String {
        val lineSeparator = System.lineSeparator()
        return StringBuilder("id:").append(id).append(lineSeparator)
            .append("name:").append(name).append(lineSeparator)
            .append("romanji name:").append(WanaKanaJava.toRomaji(nameKana)).append(lineSeparator)
            .append("bpm:").append(bpm).append(lineSeparator)
            .append("composer:").append(composer).append(lineSeparator)
            .append("lyricist:").append(lyricist).append(lineSeparator)
            .append("offset:").append(soundOffset).append(lineSeparator)
            .append("duration:").append(soundLength).append(lineSeparator)
            .append("type:").append(CircleType.getDesc(circleType)).toString()
    }

    fun getColor(): Int = CircleType.makeRGB(CircleType.getColor(circleType))
}
