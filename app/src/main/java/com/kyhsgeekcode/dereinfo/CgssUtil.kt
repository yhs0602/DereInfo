package com.kyhsgeekcode.dereinfo

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File

object CgssUtil {
    suspend fun convertAllMusics(
        rootDir: File,
        outDir: File,
        callback: suspend (String, Int) -> Unit
    ) =
        withContext(Dispatchers.IO) {
            assert(rootDir.isDirectory)
            assert(outDir.isDirectory)
            val allFiles = rootDir.listFiles()
            rootDir.listFiles()?.forEach { file ->
                val outPath = convertAcbToWav(file, outDir)
                callback(outPath, allFiles?.size ?: 0)
            }
        }

    private fun convertAcbToWav(file: File, outDir: File): String {
        Timber.d("Outdir: ${outDir.path}")
        outDir.deleteRecursively()
        outDir.mkdirs()
        val result = acb2wav(
            outDir.path,
            arrayOf("acb2wavs", file.path, "-n") // , "-a", key1, "-b", key2
        )
        Timber.d("acb2wav ${file.path}: $result")
        return outDir.listFiles()?.first()?.listFiles()?.first()?.listFiles()?.first()?.path ?: ""
    }

    private external fun acb2wav(outDir: String, args: Array<String>): Int

    init {
        System.loadLibrary("dereinfo")
    }
}