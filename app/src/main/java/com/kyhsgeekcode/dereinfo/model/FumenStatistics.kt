package com.kyhsgeekcode.dereinfo.model

import kotlin.math.ceil

enum class StatisticIndex {
    Level,
    Total,
    Normal,
    Long,
    Flick,
    Slide,
    Damage,

    Total7,
    Total9,
    Total11,

    Normal7,
    Normal9,
    Normal11,

    Long7,
    Long9,
    Long11,

    Flick7,
    Flick9,
    Flick11,

    Slide7,
    Slide9,
    Slide11,

    Damage7,
    Damage9,
    Damage11;

    companion object {
        fun makeIndex(type: String, time: Int = 0): StatisticIndex {
//            if (type.equals("total", true)) {
//                return Total
//            }
            if (time == 0) {
                return valueOf(type)
            }
            return valueOf("$type$time")
        }

        fun makeIndex(mode: TWMode, flick: FlickMode): StatisticIndex {
            return if (flick == FlickMode.None) when (mode) {
                TWMode.Tap -> Normal
                TWMode.Hold -> Long
                TWMode.Slide -> Slide
                TWMode.Damage -> Damage
                else -> throw IllegalArgumentException()
            }
            else Flick
        }

        fun makeIndex(base: StatisticIndex, time: Float): List<StatisticIndex> {
            val result = ArrayList<StatisticIndex>()
            val timings = when (base) {
                Total, Normal -> getTimingHighChance(time)
                else -> getTimingMiddleChance(time)
            }
            //무효
//            if(base == Total || base == Normal)
//                return null
            for (timing: Int in timings)
                result.add(valueOf("${base.name}$timing"))
            return result
        }


        private fun getTimingMiddleChance(time: Float): List<Int> {
            val result = ArrayList<Int>()
            for (timing in arrayOf(7, 9, 11)) {
                val rem = time.rem(timing)
                val upper = 1.5 * (ceil(timing / 1.5) - 1)
                if (rem in 0.0..upper) {
                    result.add(timing)
                }
            }
            return result
        }

        private fun getTimingHighChance(time: Float): List<Int> {
            val result = ArrayList<Int>()
            for (timing in arrayOf(7, 9, 11)) {
                val rem = time.rem(timing)
                val upper = 1.5 * (ceil(timing / 2.0) - 1)
                if (rem in 0.0..upper) {
                    result.add(timing)
                }
            }
            return result
        }

    }
}


typealias OneStatistic = Map<StatisticIndex, Float>
typealias FumenStatistic = Map<TW5Difficulty, OneStatistic>
