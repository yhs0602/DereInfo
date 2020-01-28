package com.kyhsgeekcode.dereinfo

import android.app.Activity
import android.content.Context
import android.content.Intent
import java.io.*

fun launchActivity(context: Context, target:Class<out Activity>){
    val intent = Intent(context,target)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    context.startActivity(intent)
}

fun saveObject(file: File, target:Any) {
    val outputStream = ObjectOutputStream(FileOutputStream(file))
    outputStream.writeObject(target)
    outputStream.flush()
    outputStream.close()
}

fun loadObject(file: File) : Any {
    val inputStream = ObjectInputStream(FileInputStream(file))
    val result = inputStream.readObject()
    inputStream.close()
    return result
}