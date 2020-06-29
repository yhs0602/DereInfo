package com.kyhsgeekcode.dereinfo

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kyhsgeekcode.dereinfo.model.Note
import com.kyhsgeekcode.dereinfo.model.OneDifficulty
import kotlin.math.ceil

class FumenRenderer(
    val context: Context,
    val lane: Int,
    val width: Int = 300,
    val heightPerSec: Int = 600,
    val maxHeight: Int = 6000
) {
    fun render(oneDifficulty: OneDifficulty): Bitmap? {
        if (oneDifficulty.notes == null) {
            Log.d("Renderer", "Notes are null")
            return null
        }
        if (oneDifficulty.notes.isEmpty()) {
            Log.d("Renderer", "Size is 0")
            return null
        }
        val notes: List<Note> = oneDifficulty.notes ?: return null
        val lastTime = notes.maxBy {
            it.time
        }?.time ?: return null
        val totalHeight = heightPerSec * lastTime
        val lines = ceil(totalHeight / maxHeight).toInt()
        val totalWidth = lines * width
        val height = maxHeight
        val widthPerSubLane = width / (lines + 1)
        val bitmap = createBitmap(totalWidth, height)
        Log.d("Renderer", "")
        val canvas = Canvas(bitmap)
        canvas.drawARGB(255, 0, 0, 0)
        val lanePaint = Paint()
        val laneSubPaint = Paint()
        val normalNotePaint = Paint()
        lanePaint.strokeWidth = 5.0f
        lanePaint.color = Color.LTGRAY
        laneSubPaint.strokeWidth = 2.0f
        laneSubPaint.color = Color.WHITE
        normalNotePaint.color = Color.RED
        for (i in 0..lines) {
            val x = i * width.toFloat()
            canvas.drawLine(x, 0.0f, x, height.toFloat(), lanePaint)
            for (j in 1..lane) {
                val xx = x + widthPerSubLane * j
                canvas.drawLine(xx, 0.0f, xx, height.toFloat(), laneSubPaint)
            }
        }
        val pkgName = context.packageName
        for (note in notes) {
            val totalHeightPos = heightPerSec * note.time
            val linenumber = ceil(totalHeightPos / maxHeight).toInt()
            val realWidthPos = linenumber * width + widthPerSubLane * note.endline
            val realHeightPos = maxHeight - totalHeightPos.rem(maxHeight)
            val bitmapName = note.getBitmap().toLowerCase()
            ResourcesCompat.getDrawable(
                context.resources,
                context.resources.getIdentifier(
                    bitmapName,
                    "drawable",
                    pkgName
                ), null
            )?.let {
                canvas.drawBitmap(
                    it.toBitmap(),
                    null,
                    RectF(realWidthPos - 20, realHeightPos - 20, realWidthPos + 20, realHeightPos + 20),
                    normalNotePaint
                )
            } ?: canvas.drawCircle(realWidthPos, realHeightPos, 20.0f, normalNotePaint)
        }
        canvas.drawText(oneDifficulty.lanes.toString(), 0.0f, 10.0f, Paint())
        return bitmap
    }
}
