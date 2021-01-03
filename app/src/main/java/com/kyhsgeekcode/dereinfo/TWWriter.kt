package com.kyhsgeekcode.dereinfo

import com.google.gson.Gson
import com.kyhsgeekcode.dereinfo.model.DereDatabaseHelper
import com.kyhsgeekcode.dereinfo.model.MusicInfo
import com.kyhsgeekcode.dereinfo.model.OneDifficulty
import com.kyhsgeekcode.dereinfo.model.StatisticIndex

class TWWriter(val musicInfo: MusicInfo, val oneDifficulty: OneDifficulty) {
    val statistic =
        DereDatabaseHelper.theInstance.musicInfoIDToStatistic[musicInfo.id]?.get(oneDifficulty.difficulty)

    fun write(): String {
        val builder = StringBuilder()

        val twFile = TWFile(
            TWFile.TWMetadata(
                oneDifficulty.difficulty.ordinal,
                musicInfo.composer,
                "Deresute",
                statistic?.get(StatisticIndex.Level)?.toInt() ?: 1,
            )
        )
        for (note in oneDifficulty.notes!!) {
            twFile.notes.add(
                TWFile.TWNote(
                    note.id,
                    note.size,
                    note.color,
                    note.twMode.ordinal,
                    note.flick.ordinal,
                    note.time,
                    note.speed,
                    note.startline,
                    note.endline,
                    note.previds
                )
            )
        }
        val gson = Gson()
        return gson.toJson(twFile)
    }
}

class TWFile(val metadata: TWMetadata) {
    class TWMetadata(
        val level: Int,
        val artist: String,
        val mapper: String,
        val density: Int
    )

    class TWNote(
        val ID: Int,
        val Size: Int,
        val Color: Array<Int>,
        val Mode: Int,
        val Flick: Int,
        val Time: Float,
        val Speed: Float,
        val StartLine: Float,
        val EndLine: Float,
        val PrevIDs: Array<Int>
    )

    val version: Int = 2
    val notes: MutableList<TWNote> = ArrayList()

}