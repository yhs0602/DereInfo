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
    val sync: Boolean = false,
    val tick: Int = 10
) {
    var nextNote: Note? = null // arrayListOf<Note>()

    fun isFlick(): Boolean = flick != FlickMode.None
    fun isLong(): Boolean = twMode == TWMode.Hold
    fun isSlide(): Boolean = twMode == TWMode.Slide
    fun isDamage(): Boolean = twMode == TWMode.Damage
    fun chaos(): Float = abs(startline - endline)

    val cgNoteType: CGNoteType = when {
        isDamage() -> {
            CGNoteType.DAMAGE
        }
        isFlick() -> {
            CGNoteType.FLICK
        }
        isLong() -> {
            CGNoteType.HOLD
        }
        else -> {
            CGNoteType.NORMAL
        }
    }

    fun getBitmap(): String {
        if (isFlick()) {
            return "Flick${flick.name}note"
        }
        if (isSlide()) {
            return "Slidenote"
        }
        if (isLong()) {
            return "Longnote"
        }
        if (isDamage()) {
            return "Damagenote"
        }
        return "Normalnote"
    }

    fun addNext(nextNote: Note) {
        this.nextNote = nextNote //s.add(nextNote)
    }

}


enum class CGNoteType {
    NORMAL,
    HOLD,
    FLICK,
    SLIDE,
    DAMAGE;
}