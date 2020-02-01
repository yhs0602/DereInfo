package com.kyhsgeekcode.dereinfo.model

import android.graphics.Color

enum class CircleType {
    All,
    Cute,
    Cool,
    Passion
    ;
    companion object {
        fun fromInt(circleType: Int) : CircleType = when(circleType) {
            1->Cute
            2->Cool
            3->Passion
            4->All
            else->throw IllegalArgumentException("CircleType should be in 1~4")
        }

        fun getDesc(circleType: Int): String = when (circleType) {
            1 -> "Cute"
            2 -> "Cool"
            3 -> "Passion"
            4 -> "All"
            else -> "Unknown(${circleType})"
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

        fun makeRGB(color: Array<Int>): Int =
            Color.argb(color[3], color[0], color[1], color[2])
    }
}

