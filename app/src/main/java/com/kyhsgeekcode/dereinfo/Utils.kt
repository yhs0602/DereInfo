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
import androidx.core.text.isDigitsOnly
import java.io.*
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.reflect.KParameter
import kotlin.reflect.full.declaredMembers


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

fun Float.equalsDelta(other: Float?) = abs(this - (other?: Float.NaN)) < 0.000001


inline fun <reified T> cur2List(cursor: Cursor): List<T> {
    val converters = arrayOf(
        Cursor::getStringOrNull,
        Cursor::getIntOrNull, Cursor::getFloatOrNull, Cursor::getStringOrNull,
        Cursor::getBlobOrNull
    )
    val resultList = ArrayList<T>()
    cursor.moveToFirst()
    val nameToParmaeter =
        T::class.constructors.toTypedArray()[0].parameters.associateBy({ it.name }, { it })
    val totalColumn = cursor.columnCount
    val iToParameter = HashMap<Int, KParameter>()
    for (i in 0 until totalColumn) {
        iToParameter[i] = nameToParmaeter[cursor.getColumnName(i) ?: ""] ?: return emptyList()
    }
    while (!cursor.isAfterLast) {
        val paramToVal = HashMap<KParameter, Any?>()
        for (i in 0 until totalColumn) {
            try {
                paramToVal[iToParameter[i]!!] = converters[cursor.getType(i)](cursor, i)
            } catch (e: Exception) {
                Log.d("CursorToJson", e.message)
            }
        }
        Log.w(
            "CurToList",
            "paramToVal is " + paramToVal.entries.joinToString("/") { "${it.component1().name} = ${it.component2()}" })
        try {
            val theObject: T =
                T::class.constructors.toTypedArray()[0].callBy(paramToVal)
            resultList.add(theObject)
        } catch (e: Exception) {
            Log.e("Cur", "Error ", e)
        }
        cursor.moveToNext()
    }
    cursor.close()
    return resultList
}

inline fun <reified T> queryToList(
    database: SQLiteDatabase,
    table: String,
    selection: String? = null,
    params: Array<String>? = null
): List<T> {
    Log.d("QueryToList", "QueryToList Test 1")
    val fields /*: Array<String>*/ = T::class.members.map { it.name }.filter { it ->
        !(it.startsWith("component") && it.replace("component", "")
            .isDigitsOnly() || arrayOf("copy", "equals", "hashCode", "toString").contains(it))
    }.toTypedArray()
    Log.d("QueryToList", "${fields.joinToString(",")}")
    val cursor = database.query(table, fields, selection, params, null, null, null)
    cursor.use {
        return cur2List(it)
    }
}
