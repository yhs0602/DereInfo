package com.kyhsgeekcode.dereinfo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

object CgssUtil {
    suspend fun convertAllMusics(rootDir: File, outDir: File) = withContext(Dispatchers.IO) {
        assert(rootDir.isDirectory)
        assert(outDir.isDirectory)
        rootDir.listFiles()?.forEach { file ->
            convertAcbToWav(file, outDir)
        }
    }

    fun convertAcbToWav(file: File, outDir: File) {
        val result = acb2wav(
            outDir.path,
            arrayOf("acb2wavs", file.path, "-a", "f27e3b22", "-b", "3657", "-prependId")
        )
        Timber.d("acb2wav ${file.path}: $result")
    }

    private external fun acb2wav(outDir: String, args: Array<String>): Int

    init {
        System.loadLibrary("dereinfo")
    }
}