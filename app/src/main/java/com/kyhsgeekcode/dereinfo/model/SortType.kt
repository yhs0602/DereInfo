package com.kyhsgeekcode.dereinfo.model

enum class SortType(val value: Int) {
    Data(0),
    Alphabetical(1),
    TotalNote(2),
    LongRatio(3),
    FlickRatio(4),
    SlideRatio(5)
    ;

    fun condition(musicInfo: MusicInfo) :Any {
        return when (this) {
            Data -> musicInfo.id
            Alphabetical -> if (musicInfo.nameKana.isBlank()) musicInfo.nameKana else musicInfo.name
            TotalNote -> musicInfo.bpm
            LongRatio -> musicInfo.composer
            FlickRatio -> musicInfo.lyricist
            SlideRatio -> musicInfo.soundLength
        }
    }

    companion object {
        private val values = values();
        fun getByValue(value: Int) = values.firstOrNull { it.value == value }
    }
}