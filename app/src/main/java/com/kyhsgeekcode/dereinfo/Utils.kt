package com.kyhsgeekcode.dereinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.graphics.Color
import java.io.*
import kotlin.math.abs
import kotlin.math.roundToInt

fun launchActivity(context: Context, target: Class<out Activity>) {
    val intent = Intent(context, target)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    context.startActivity(intent)
}

fun saveObject(file: File, target: Any) {
    val outputStream = ObjectOutputStream(FileOutputStream(file))
    outputStream.writeObject(target)
    outputStream.flush()
    outputStream.close()
}

fun loadObject(file: File): Any {
    val inputStream = ObjectInputStream(FileInputStream(file))
    val result = inputStream.readObject()
    inputStream.close()
    return result
}

val sqliteHeader = byteArrayOf(
    'S'.toByte(),
    'Q'.toByte(),
    'L'.toByte(),
    'i'.toByte(),
    't'.toByte(),
    'e'.toByte(),
    ' '.toByte(),
    'f'.toByte(),
    'o'.toByte(),
    'r'.toByte(),
    'm'.toByte(),
    'a'.toByte(),
    't'.toByte(),
    ' '.toByte(),
    '3'.toByte(),
    0
)

fun checkIfDatabase(file: File): Boolean {
    if(file==null)
        return false
    if(file.isDirectory)
        return false
    val byteArray = ByteArray(16)
    val fi =  file.inputStream()
    fi.read(byteArray, 0, 16)
    fi.close()
    return byteArray contentEquals sqliteHeader
}

fun manipulateColor(color: Int, factor: Float): Int {
    val a: Int = Color.alpha(color)
    val r = (Color.red(color) * factor).roundToInt()
    val g = (Color.green(color) * factor).roundToInt()
    val b = (Color.blue(color) * factor).roundToInt()
    return Color.argb(
        a,
        r.coerceAtMost(255),
        g.coerceAtMost(255),
        b.coerceAtMost(255)
    )
}

fun Float.equalsDelta(other: Float) = abs(this - other) < 0.000001
