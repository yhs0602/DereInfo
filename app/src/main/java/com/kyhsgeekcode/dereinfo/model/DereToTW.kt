package com.kyhsgeekcode.dereinfo.model

import com.kyhsgeekcode.dereinfo.enums.FlickMode

//None left right up down
fun getTW5Flick(status: Int): FlickMode {
    return when (status) {
        0 -> FlickMode.None
        1 -> FlickMode.Left
        2 -> FlickMode.Right
        100 -> FlickMode.None  // きらりんロボのテーマ
        101 -> FlickMode.Left  // きらりんロボのテーマ
        102 -> FlickMode.Right  // きらりんロボのテーマ
        else -> FlickMode.None
    }
}

//tap hold slide damage hidden
fun getTWMode(mode: Int): TWMode {
    return when (mode) {
        1 -> TWMode.Tap
        2 -> TWMode.Hold
        3 -> TWMode.Slide
        8 -> TWMode.Damage
        else -> TWMode.Tap
    }
}

fun getTWModeGrand(type: Int): Pair<TWMode, FlickMode> {
    return when (type) {
        4 -> Pair(TWMode.Tap, FlickMode.None)
        5 -> Pair(TWMode.Slide, FlickMode.None)
        6 -> Pair(TWMode.Tap, FlickMode.Left)
        7 -> Pair(TWMode.Tap, FlickMode.Right)
        else -> Pair(TWMode.Tap, FlickMode.None)
    }
}

