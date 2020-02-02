package com.kyhsgeekcode.dereinfo.model

import android.util.SparseIntArray
import java.text.DecimalFormat

operator fun SparseIntArray.set(key: Int, value: Int) {
    put(key, value)
}

fun Double.format(digits: Int) = "%.${digits}f".format(this)
fun Float.format(digits: Int) = "%.${digits}f".format(this)
fun Double.formatClean(digits: Int): String = DecimalFormat("#.##").format(this)
fun Float.formatClean(digits: Int): String = DecimalFormat("#.##").format(this)
fun Float.formatCleanPercent(digits: Int): String {
    val clean = formatClean(digits)
    if (clean.contains('.'))
        return "$clean%"
    return clean
}
