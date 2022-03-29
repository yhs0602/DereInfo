package com.kyhsgeekcode.dereinfo

object CgssUtil {
    external fun acb2wav(args: Array<String>): Int

    init {
        System.loadLibrary("dereinfo")
    }
}