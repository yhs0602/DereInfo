package com.kyhsgeekcode.dereinfo

import android.graphics.Bitmap
import com.kyhsgeekcode.dereinfo.model.Note

class FumenRenderer(val lane: Int, val width: Int = 300, val heightPerSec: Int = 600) {
    fun render(notes: List<Note>): Bitmap? {
        val lastTime = notes.maxBy {
            it.time
        }?.time ?: return null
        return null
    }
}
