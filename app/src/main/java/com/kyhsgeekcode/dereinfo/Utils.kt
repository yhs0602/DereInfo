package com.kyhsgeekcode.dereinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Color
import android.util.Log
import androidx.core.database.getBlobOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
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
    if (file == null)
        return false
    if (file.isDirectory)
        return false
    val byteArray = ByteArray(16)
    val fi = file.inputStream()
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

inline fun <reified T> cur2List(cursor: Cursor): List<T> {
    val converters = arrayOf(
        Cursor::getStringOrNull,
        Cursor::getIntOrNull, Cursor::getStringOrNull, Cursor::getFloatOrNull,
        Cursor::getBlobOrNull
    )
    val resultList = ArrayList<T>()
    cursor.moveToFirst()
    while (!cursor.isAfterLast) {
        val totalColumn: Int = cursor.columnCount
        val arr = ArrayList<Any?>()
        for (i in 0 until totalColumn) {
            if (cursor.getColumnName(i) != null) {
                try {
                    arr.add(converters[cursor.getType(i)](cursor, i))
                } catch (e: Exception) {
                    Log.d("CursorToJson", e.message)
                }
            }
            try {
                val theObject: T =
                    T::class.java.constructors[0].newInstance(*arr.toTypedArray()) as T
                resultList.add(theObject)
            } catch (e: Exception) {
                Log.e("Cur", "Error $i", e)
            }
        }
        cursor.moveToNext()
    }
    cursor.close()
    return resultList
}

inline fun <reified T> queryToList(database : SQLiteDatabase, table: String, selection: String? = null, params: Array<String>?=null) : List<T> {
    val fields : Array<String> = T::class.java.fields.map { it.name }.toTypedArray()
    val cursor = database.query(table, fields, selection, params, null, null, null)
    cursor.use { cursor ->
        return cur2List(cursor)
    }
}
