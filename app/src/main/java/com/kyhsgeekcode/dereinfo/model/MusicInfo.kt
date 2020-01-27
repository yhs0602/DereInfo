package com.kyhsgeekcode.dereinfo.model

class MusicInfo(
    val id: Int,
    val name: String,
    val bpm: Int,
    val composer: String,
    val lyricist: String,
    val soundOffset: Int,
    val soundLength: Int,
    val circleType: Int = 4
) {
    override fun toString(): String {
        val lineSeparator = System.lineSeparator()
        return StringBuilder("name:").append(name).append(lineSeparator)
            .append("bpm:").append(bpm).append(lineSeparator)
            .append("composer:").append(composer).append(lineSeparator)
            .append("lyricist:").append(lyricist).append(lineSeparator)
            .append("duration:").append(soundLength).append(lineSeparator)
            .append("type:").append(CircleType.getDesc(circleType)).toString()
    }
}