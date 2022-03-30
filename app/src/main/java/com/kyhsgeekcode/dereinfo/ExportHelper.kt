package com.kyhsgeekcode.dereinfo

import android.content.Context
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.util.zip.Deflater
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream

object ExportHelper {
    suspend fun exportMusic(
        context: Context,
        musicFolder: File,
        fileOutputStream: FileOutputStream,
        progressHandler: suspend (Int, Int, String?) -> Unit // progress, total, message
    ) {
        val bos = BufferedOutputStream(fileOutputStream)
        val zos = ZipOutputStream(bos)
        zos.setLevel(Deflater.BEST_COMPRESSION)
        var count = 0
        val outTmpFolder = context.cacheDir
        CgssUtil.convertAllMusics(musicFolder, outDir = outTmpFolder) { converted, all_count ->
            val convertedFile = File(converted)
            val fileName = convertedFile.name
            zos.putNextEntry(ZipEntry(fileName).apply { method = Deflater.DEFLATED })
            convertedFile.inputStream().use {
                it.copyTo(zos)
            }
            zos.closeEntry()
            progressHandler(count, all_count, convertedFile.name)
            count++
        }
        zos.close()
        bos.close()
    }
}