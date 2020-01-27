package com.kyhsgeekcode.dereinfo.model

//None left right up down
fun getTW5Flick(status: Int): Int {
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
fun getTWMode(mode: Int): Int {
    return when (mode) {
        1 -> TWMode.Tap
        2 -> TWMode.Hold
        3 -> TWMode.Slide
        else -> TWMode.Tap
    }
}

