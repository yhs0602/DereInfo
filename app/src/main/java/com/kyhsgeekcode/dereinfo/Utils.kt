package com.kyhsgeekcode.dereinfo

import android.app.Activity
import android.content.ContentValues
import android.content.Context
import android.content.Intent
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.graphics.Bitmap
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import androidx.core.database.getBlobOrNull
import androidx.core.database.getFloatOrNull
import androidx.core.database.getIntOrNull
import androidx.core.database.getStringOrNull
import androidx.core.text.isDigitsOnly
import timber.log.Timber
import java.io.*
import java.io.File.separator
import kotlin.math.abs
import kotlin.math.roundToInt
import kotlin.reflect.KParameter
import kotlin.reflect.KProperty

//val Any.TAG: String
//    get() {
//        val tag = javaClass.simpleName
//        return if (tag.length <= 23) tag else tag.substring(0, 23)
//    }


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
    'S'.code.toByte(),
    'Q'.code.toByte(),
    'L'.code.toByte(),
    'i'.code.toByte(),
    't'.code.toByte(),
    'e'.code.toByte(),
    ' '.code.toByte(),
    'f'.code.toByte(),
    'o'.code.toByte(),
    'r'.code.toByte(),
    'm'.code.toByte(),
    'a'.code.toByte(),
    't'.code.toByte(),
    ' '.code.toByte(),
    '3'.code.toByte(),
    0
)


fun checkIfDatabase(file: File): Boolean {
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

fun Float.equalsDelta(other: Float?) = abs(this - (other ?: Float.NaN)) < 0.000001


fun saveImage(bitmap: Bitmap, context: Context, folderName: String) {
    if (Build.VERSION.SDK_INT >= 29) {
        val values = contentValues()
        values.put(MediaStore.Images.Media.RELATIVE_PATH, "Pictures/" + folderName)
        values.put(MediaStore.Images.Media.IS_PENDING, true)
        // RELATIVE_PATH and IS_PENDING are introduced in API 29.

        val uri: Uri? =
            context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
        if (uri != null) {
            saveImageToStream(bitmap, context.contentResolver.openOutputStream(uri))
            values.put(MediaStore.Images.Media.IS_PENDING, false)
            context.contentResolver.update(uri, values, null, null)
        }
    } else {
        val directory =
            File(Environment.getExternalStorageDirectory().toString() + separator + folderName)
        directory.mkdirs()
        // getExternalStorageDirectory is deprecated in API 29

        if (!directory.exists()) {
            directory.mkdirs()
        }
        val fileName = System.currentTimeMillis().toString() + ".png"
        val file = File(directory, fileName)
        saveImageToStream(bitmap, FileOutputStream(file))
        val values = contentValues()
        values.put(MediaStore.Images.Media.DATA, file.absolutePath)
        // .DATA is deprecated in API 29
        context.contentResolver.insert(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, values)
    }
}

private fun contentValues(): ContentValues {
    val values = ContentValues()
    values.put(MediaStore.Images.Media.MIME_TYPE, "image/png")
    values.put(MediaStore.Images.Media.DATE_ADDED, System.currentTimeMillis() / 1000)
    values.put(MediaStore.Images.Media.DATE_TAKEN, System.currentTimeMillis())
    return values
}

private fun saveImageToStream(bitmap: Bitmap, outputStream: OutputStream?) {
    if (outputStream != null) {
        try {
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outputStream)
            outputStream.close()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }
}


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
                Timber.d(e, "CurToList")
            }
        }
//        Log.w(
//            "CurToList",
//            "paramToVal is " + paramToVal.entries.joinToString("/") { "${it.component1().name} = ${it.component2()}" })
        try {
            val theObject: T =
                T::class.constructors.toTypedArray()[0].callBy(paramToVal)
            resultList.add(theObject)
        } catch (e: Exception) {
            Timber.e(e, "Error ")
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
    Timber.d("QueryToList Test 1")
    val fields /*: Array<String>*/ = T::class.members.filter {
        it is KProperty<*>
    }.map { it.name }.filter { it ->
        !(it.startsWith("component") && it.replace("component", "")
            .isDigitsOnly() || arrayOf(
            "copy",
            "equals",
            "hashCode",
            "toString",
            "TAG"
        ).contains(it))
    }.toTypedArray()
    Timber.d(fields.joinToString(","))
    val cursor = database.query(table, fields, selection, params, null, null, null)
    cursor.use {
        return cur2List(it)
    }
}
