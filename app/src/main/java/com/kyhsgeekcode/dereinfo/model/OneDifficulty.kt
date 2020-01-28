package com.kyhsgeekcode.dereinfo.model

class OneDifficulty(val difficulty: TW5Difficulty, val notes: List<Note>?) {
    var noteCounts = arrayOf(0, 0, 0, 0)
    fun countNotes(): Array<Int> {
        //Total Normal long flick slide
        val res = arrayOf(0, 0, 0, 0)
        for (note in notes!!) {
            var isNormal = true
            if (note.isFlick()) {
                isNormal = false
                res[3]++
            }
            if (note.isLong()) {
                isNormal = false
                res[2]++
            }
            if (note.isSlide()) {
                isNormal = false
                res[4]++
            }
            if (isNormal) {
                res[1]++
            }
            res[0]++
        }
        return res
    }
}