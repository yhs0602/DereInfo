package com.kyhsgeekcode.dereinfo.model

import kotlin.math.abs

class Note(
    var id: Int,
    var size: Int,
    var color: Array<Int>,
    val twMode: TWMode,
    var flick: FlickMode,
    val time: Float,
    val speed: Float,
    val startline: Float,
    val endline: Float,
    val previds: Array<Int>,
    val tick: Int = 10
) {
    fun isFlick(): Boolean = flick != FlickMode.None
    fun isLong(): Boolean = twMode == TWMode.Hold
    fun isSlide(): Boolean = twMode == TWMode.Slide
    fun chaos(): Float = abs(startline - endline)
}
