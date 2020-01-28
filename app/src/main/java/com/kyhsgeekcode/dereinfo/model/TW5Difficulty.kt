package com.kyhsgeekcode.dereinfo.model

import android.util.Log

enum class TW5Difficulty {
    Debut,
    Regular,
    Pro,
    Master,
    MasterPlus,
    Light,
    Trick,
    Piano,
    Forte;

    companion object {
        val TAG = "TW5Difficulty"
        fun fromString(value: String): TW5Difficulty = when (value.toLowerCase()) {
            "debut" -> Debut
            "regular" -> Regular
            "pro" -> Pro
            "master" -> Master
            "master+" -> MasterPlus
            "trick" -> Trick
            "light" -> Light
            "piano" -> Piano
            "forte" -> Forte
            "legacy" -> MasterPlus
            else -> Debut
        }

        fun valueOf(value: Int): TW5Difficulty {
            Log.d(TAG, "valueOf Int:${value}")
            return when (value) {
                1 -> Debut
                2 -> Regular
                3 -> Pro
                4 -> Master
                5 -> MasterPlus
                101 -> MasterPlus
                11 -> Light
                12 -> Trick
                21 -> Piano
                22 -> Forte
                else -> Debut
            }
        }
    }
}