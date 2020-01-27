package com.kyhsgeekcode.dereinfo.model

import android.graphics.Color

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

fun getColor(circleType: Int): Array<Int> {
    return when (circleType) {
        1 -> arrayOf(255, 0x74, 0x77, 255)        // red
        2 -> arrayOf(0x53, 0x6A, 0xDE, 255)        // blue
        3 -> arrayOf(0xFA, 0xCC, 0x43, 255)      // yellow
        4 -> arrayOf(0xC7, 0xF9, 0xF4, 255)     // white
        else -> arrayOf(0, 0, 0, 255)
    }
}

fun makeRGB(color: Array<Int>): Int = Color.argb(color[3],color[0],color[1],color[2])
