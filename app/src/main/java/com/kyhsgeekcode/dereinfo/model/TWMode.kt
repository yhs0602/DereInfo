package com.kyhsgeekcode.dereinfo.model

enum class TWMode(value: Int) {
    Tap(0),
    Hold(1),
    Slide(2),
    Damage(3),
    Hidden(4)
    ;

    companion object {
        fun fromType(type: Int): TWMode = when (type) {
            1 -> Tap
            2 -> Hold
            3 -> Slide
            else -> throw IllegalArgumentException("type=${type}")
        }
    }
}