package com.kyhsgeekcode.dereinfo

import android.app.Activity
import android.content.Context
import android.content.Intent

fun launchActivity(context: Context, target:Class<out Activity>){
    val intent = Intent(context,target)
    intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
    context.startActivity(intent)
}