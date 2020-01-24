package com.kyhsgeekcode.dereinfo

import android.util.SparseIntArray

operator fun SparseIntArray.set(key:Int, value:Int) {
    put(key,value)
}