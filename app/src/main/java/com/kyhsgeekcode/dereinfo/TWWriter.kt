package com.kyhsgeekcode.dereinfo

import com.google.gson.GsonBuilder
import com.kyhsgeekcode.dereinfo.model.DereDatabaseService
import com.kyhsgeekcode.dereinfo.model.MusicInfo
import com.kyhsgeekcode.dereinfo.model.OneDifficultyData
import com.kyhsgeekcode.dereinfo.model.StatisticIndex

class TWWriter(val musicInfo: MusicInfo, val oneDifficultyData: OneDifficultyData) {
    val statistic =
        DereDatabaseService.theInstance.musicInfoIDToStatistic[musicInfo.id]?.get(oneDifficultyData.difficulty)

    fun write(): String {
        val builder = StringBuilder()

        val twFile = TWFile(
            TWFile.TWMetadata(
                oneDifficultyData.difficulty.ordinal,
                musicInfo.composer,
                "Deresute",
                statistic?.get(StatisticIndex.Level)?.toInt() ?: 1,
            )
        )
        for (note in oneDifficultyData.notes!!) {
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
        val gson = GsonBuilder().setPrettyPrinting().create()
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