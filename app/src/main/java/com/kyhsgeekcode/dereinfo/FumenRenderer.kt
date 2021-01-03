package com.kyhsgeekcode.dereinfo

import android.content.Context
import android.graphics.*
import android.util.Log
import androidx.core.content.res.ResourcesCompat
import androidx.core.graphics.createBitmap
import androidx.core.graphics.drawable.toBitmap
import com.kyhsgeekcode.dereinfo.model.Note
import com.kyhsgeekcode.dereinfo.model.OneDifficultyData
import kotlin.math.ceil


class FumenRenderer(
    val context: Context,
    val lane: Int,
    val width: Int = 300,
    val heightPerSec: Int = 600,
    val maxHeight: Int = 7500
) {
    fun render(oneDifficultyData: OneDifficultyData): Bitmap? {
        if (oneDifficultyData.notes == null) {
            Log.d("Renderer", "Notes are null")
            return null
        }
        if (oneDifficultyData.notes.isEmpty()) {
            Log.d("Renderer", "Size is 0")
            return null
        }
        val isGrand = oneDifficultyData.difficulty.isGrandMode()
        val notes: List<Note> = oneDifficultyData.notes
        val lastTime = notes.maxByOrNull {
            it.time
        }?.time ?: return null
        val totalHeight = heightPerSec * lastTime
        val lines = ceil(totalHeight / maxHeight).toInt()
        val totalWidth = lines * width
        val height = maxHeight
        val widthPerSubLane = width.toFloat() / (lane + 1)
        val bitmap = createBitmap(totalWidth, height)
        Log.d("Renderer", "")
        val canvas = Canvas(bitmap)
        canvas.drawARGB(255, 0, 0, 0)
        val lanePaint = Paint()
        val laneSubPaint = Paint()
        val normalNotePaint = Paint()
        val connectPaint = Paint()
        val syncPaint = Paint()
        val grandPaint = Paint()

        lanePaint.strokeWidth = 6.0f
        lanePaint.color = Color.LTGRAY
        laneSubPaint.strokeWidth = 1.0f
        laneSubPaint.color = Color.WHITE
        normalNotePaint.color = Color.RED
        connectPaint.color = Color.LTGRAY
        connectPaint.strokeWidth = 16.0f
        syncPaint.color = Color.WHITE
        syncPaint.strokeWidth = 3.0f

        grandPaint.color = Color.RED
        grandPaint.strokeWidth = 5.0f
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
            fun calcPos(theNote: Note): Triple<Float, Float, Int> {
                val totalHeightPos = heightPerSec * theNote.time
                val linenumber = (totalHeightPos / maxHeight).toInt()
                val realWidthPos = calcNoteX(linenumber, widthPerSubLane, theNote)
                val realHeightPos = calcNoteY(totalHeightPos)
                return Triple(realWidthPos, realHeightPos, linenumber)
            }

            val realX: Float
            val realY: Float
            val coord = calcPos(note)
            realX = coord.first
            realY = coord.second
            if (note.isFlick() || note.isSlide() || note.isLong()) {
                note.nextNote?.let {
                    val nextNote = it//s[0]
                    val nextNoteCoord = calcPos(nextNote)
                    var leftDy = (nextNote.time - note.time) * heightPerSec
                    if (coord.third < nextNoteCoord.third) {
                        // stop & cut and continue
                        val dxdy =
                            (widthPerSubLane * (nextNote.endline - note.endline)).rem(width.toFloat()) / leftDy
                        var beforeX = realX
                        val destX = nextNoteCoord.first
                        var afterX: Float = beforeX + realY * dxdy
                        canvas.drawLine(
                            realX,
                            realY,
                            afterX,
                            0.0f,
                            connectPaint
                        )
                        leftDy -= realY
                        beforeX = afterX + width
                        while (leftDy >= maxHeight) {
                            afterX = beforeX + maxHeight * dxdy
                            canvas.drawLine(
                                beforeX,
                                maxHeight.toFloat(),
                                afterX,
                                0.0f,
                                connectPaint
                            )
                            leftDy -= maxHeight
                            beforeX = afterX + width
                        }
                        canvas.drawLine(
                            beforeX, // x dest just before
                            maxHeight.toFloat(),
                            nextNoteCoord.first,
                            nextNoteCoord.second,
                            connectPaint
                        )
                    } else {
                        canvas.drawLine(
                            realX,
                            realY,
                            nextNoteCoord.first,
                            nextNoteCoord.second,
                            connectPaint
                        )
                    }
                }
            }
//            if(note.isLong() || note.isSlide()) {
//
//            }
            if (note.sync) {
                if (note.id < notes.size && notes[note.id].sync) {
                    val syncedNote = notes[note.id]
                    if (syncedNote.time.equalsDelta(note.time)) {
                        val syncedCoord = calcPos(syncedNote)
                        canvas.drawLine(
                            realX,
                            realY,
                            syncedCoord.first,
                            syncedCoord.second,
                            syncPaint
                        )
                    }
                }
            }
            val bitmapName = note.getBitmap().toLowerCase()
            if (isGrand) {
                val noteWidth = note.size * widthPerSubLane / 2
                val rect = RectF(realX - noteWidth, realY - 10, realX + noteWidth, realY + 10)
                val grandBitmapName = bitmapName + "grand"
                ResourcesCompat.getDrawable(
                    context.resources,
                    context.resources.getIdentifier(
                        grandBitmapName,
                        "drawable",
                        pkgName
                    ), null
                )?.let {
                    canvas.drawBitmap(
                        it.toBitmap(),
                        null,
                        rect,
                        grandPaint
                    )
                } ?: drawBorderedRect(
                    canvas,
                    rect,
                    6f,
                    6f,
                    grandPaint,
                    Color.RED
                )
            } else {
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
                        RectF(
                            realX - 20,
                            realY - 20,
                            realX + 20,
                            realY + 20
                        ),
                        normalNotePaint
                    )
                } ?: canvas.drawCircle(realX, realY, 20.0f, normalNotePaint)
            }
        }
//        canvas.drawText(oneDifficulty.lanes.toString(), 0.0f, 10.0f, Paint())
        return bitmap
    }

    private fun drawBorderedRect(
        canvas: Canvas,
        rectF: RectF,
        f1: Float,
        fl1: Float,
        paint: Paint,
        color: Int
    ) {
        paint.style = Paint.Style.FILL
        paint.color = color
        canvas.drawRoundRect(rectF, f1, fl1, paint)

        // border
        paint.style = Paint.Style.STROKE
        paint.color = Color.WHITE
        canvas.drawRoundRect(rectF, f1, fl1, paint)
    }

    private fun calcNoteY(totalHeightPos: Float) = maxHeight - totalHeightPos.rem(maxHeight)

    private fun calcNoteX(
        linenumber: Int,
        widthPerSubLane: Float,
        note: Note
    ) = linenumber * width + widthPerSubLane * note.endline


}
