package com.kyhsgeekcode.dereinfo.model

import android.util.Log
import java.util.*

enum class GameMode {
    NORMAL,
    MASTERPLUS,
    WITCH,
    SMART,
    GRAND;

    companion object {
        fun fromLowercase(str: String): GameMode? {
            Log.d("GameMode", str)
            val upper = str.toUpperCase(Locale.ROOT)
            return values().find {
                upper == it.name
            }
        }

        fun fromTabIndex(index: Int): GameMode? = values()[index]
    }
}