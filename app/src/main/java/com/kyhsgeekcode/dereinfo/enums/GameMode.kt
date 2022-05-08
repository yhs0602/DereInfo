package com.kyhsgeekcode.dereinfo.enums

import timber.log.Timber
import java.util.*

enum class GameMode {
    NORMAL,
    MASTERPLUS,
    WITCH,
    SMART,
    GRAND;

    companion object {
        fun fromLowercase(str: String): GameMode? {
            Timber.d(str)
            val upper = str.uppercase(Locale.ROOT)
            return values().find {
                upper == it.name
            }
        }

        fun fromTabIndex(index: Int): GameMode? = values()[index]
    }
}