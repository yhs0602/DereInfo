package com.kyhsgeekcode.dereinfo.model

enum class SortType(val value: Int) {
    Data(0),
    Alphabetical(1),
    TotalNote(2),
    LongRatio(3),
    FlickRatio(4),
    SlideRatio(5)
    ;

    companion object {
        private val values = values();
        fun getByValue(value: Int) = values.firstOrNull { it.value == value }
    }
}