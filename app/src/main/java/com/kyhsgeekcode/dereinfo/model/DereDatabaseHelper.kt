package com.kyhsgeekcode.dereinfo.model

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.SparseIntArray
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import java.io.File

//This allows access to dere database
class DereDatabaseHelper(context: Context) {
    val manifestFile: File
    val fumensDBFile: File
    val fumenFolder: File

    val musicIDToInfo: MutableMap<Int, MusicInfo> = HashMap()
    val fumenIDToMusicID: SparseIntArray = SparseIntArray()

    init {
        val datadir = context.getExternalFilesDir(null).parentFile.parentFile
        val dereFilesDir = File(datadir, "jp.co.bandainamcoent.BNEI0242/files/")
        manifestFile = File(dereFilesDir, "manifest/").listFiles()[0]
        fumenFolder = File(dereFilesDir, "a/")
        var maxlen = 0L
        var fumensDBFileTmp: File? = null
        for (file in fumenFolder.listFiles()) {
            val len = file.length();
            if (len > maxlen) {
                maxlen = len
                fumensDBFileTmp = file
                if (maxlen > 10000000)
                    break
            }
        }
        fumensDBFile = fumensDBFileTmp ?: error("No fumen file found")
    }

    fun parseDatabases(publisher: (Int, Int) -> Unit, onFinish: () -> Unit) {
        val fumensDB =
            SQLiteDatabase.openDatabase(fumensDBFile.path, null, SQLiteDatabase.OPEN_READONLY)

        val manifestDB =
            SQLiteDatabase.openDatabase(manifestFile.path, null, SQLiteDatabase.OPEN_READONLY)

        val cursorLiveData =
            fumensDB.query(
                "live_data",
                arrayOf("id", "music_data_id"),
                null,
                null,
                null,
                null,
                null
            )

        val musicDataIdIndex = cursorLiveData.getColumnIndex("music_data_id")
        val liveDataIdIndex = cursorLiveData.getColumnIndex("id")
        val circleTypeIndex = cursorLiveData.getColumnIndex("circle_type")
        val totalCount = cursorLiveData.count
        var currentCount = 0
        while (cursorLiveData.moveToNext()) {
            currentCount++
            publisher(currentCount, totalCount)
            val musicDataId = cursorLiveData.getInt(musicDataIdIndex)
            val cursorMusicData = fumensDB.query(
                "music_data",
                arrayOf(
                    "id",
                    "name",
                    "bpm",
                    "composer",
                    "lyricist",
                    "sound_offset",
                    "sound_length"
                ),
                "id=?",
                arrayOf(musicDataId.toString()),
                null,
                null,
                null
            )
            val musicNameIndex = cursorMusicData.getColumnIndex("name")
            val name = cursorMusicData.getString(musicNameIndex)
            val composerIndex = cursorMusicData.getColumnIndex("composer")
            val composer = cursorMusicData.getString(composerIndex)
            val bpmIndex = cursorMusicData.getColumnIndex("bpm")
            val bpm = cursorMusicData.getInt(bpmIndex)
            val lyricistIndex = cursorMusicData.getColumnIndex("lyricist")
            val lyricist = cursorMusicData.getString(lyricistIndex)
            val soundOffsetIndex = cursorMusicData.getColumnIndex("sound_offset")
            val soundOffset = cursorMusicData.getInt(soundOffsetIndex)
            val soundLengthIndex = cursorMusicData.getColumnIndex("sound_length")
            val soundLength = cursorMusicData.getInt(soundLengthIndex)
            cursorMusicData.close()
            val liveDataId = cursorLiveData.getInt(liveDataIdIndex)
            val circleType = cursorLiveData.getInt(circleTypeIndex)
            fumenIDToMusicID[liveDataId] = musicDataId
            musicIDToInfo[musicDataId] =
                MusicInfo(
                    musicDataId,
                    name,
                    bpm,
                    composer,
                    lyricist,
                    soundOffset,
                    soundLength,
                    circleType
                )
        }
        cursorLiveData.close()
        onFinish()
    }

    val indexToFumenFile: MutableMap<Int, File> = HashMap()
    fun indexFumens() {
        var cursorFumens: Cursor? = null
        for (file in fumenFolder.listFiles()) {
            try {
                val fumenDB =
                    SQLiteDatabase.openDatabase(file!!.path, null, SQLiteDatabase.OPEN_READONLY)
                cursorFumens = fumenDB.query("blobs", arrayOf("name"), null, null, null, null, null)
                while (cursorFumens.moveToNext()) {
                    var name = cursorFumens.getString(0)
                    if (!name[name.length - 5].isDigit())
                        continue
                    name = name.substring(13)
                    name = name.substringBefore('.')
                    val musicIndex = Integer.parseInt(name.substringBefore('.'))
//                val difficulty = Integer.parseInt(name.substringAfter('_'))
                    indexToFumenFile[musicIndex] = file
                    break
                }
            } catch (e: SQLException) {
                continue
            } finally {
                cursorFumens?.close()
            }
        }
    }

    //5개를 파싱해라.
    fun parseFumen(musicIndex: Int): OneMusic {
        val fumenFile = indexToFumenFile[musicIndex]
        val fumenDB =
            SQLiteDatabase.openDatabase(fumenFile!!.path, null, SQLiteDatabase.OPEN_READONLY)
        val cursorFumens =
            fumenDB.query("blobs", arrayOf("name", "data"), null, null, null, null, null)
        val difficulties: MutableMap<Int, OneDifficulty> = HashMap()
        val info = musicIDToInfo[musicIndex] ?: MusicInfo(
            0,
            "Error occurred",
            192,
            "System",
            "Unknown",
            0,
            1
        )
        while (cursorFumens.moveToNext()) {
            var name = cursorFumens.getString(0)
            if (!name[name.length - 5].isDigit())
                continue
            name = name.substring(13)
            name = name.substringBefore('.')
            val difficulty = Integer.parseInt(name.substringAfter('_'))
            val fumenStr = cursorFumens.getBlob(1).toString()
            val notes = parseDereFumen(fumenStr, info)
            difficulties[difficulty] =
                OneDifficulty(difficulty, notes)
        }
        cursorFumens.close()
        return OneMusic(difficulties, info)
    }

    private fun parseDereFumen(
        fumenStr: String,
        musicInfo: MusicInfo
    ): List<Note> {
        val parsedFumen = csvReader().readAllWithHeader(fumenStr)
        val prevIDs = HashMap<Int, Int>()
        val longnoteIDs = HashMap<Float, Int>()
        val notes = ArrayList<Note>()
        var prevID = 0
        var idd = 0
        for (row in parsedFumen) {
            prevID = 0
            var gid = row["groupId"]!!.toInt()
            var mode = row["type"]!!.toInt()
            if (mode > 3)
                continue
            idd++
            var twMode = getTWMode(mode)
            val endpos = row["finishPos"]!!.toFloat()
            val flick =
                getTW5Flick(row["status"]!!.toInt())
            if (gid == 0) {
                //...
            } else {
                if (prevIDs.containsKey(gid)) {
                    prevID = prevIDs[gid]!!
                } else {
                    //...
                }
                prevIDs[gid] = idd
            }
            if (longnoteIDs.containsKey(endpos)) {
                //롱노트 중이었다면 해제한다. 자신의 prev를 그 롱노트로 설정한다.
                prevID = longnoteIDs[endpos]!!
                twMode = 1
                longnoteIDs.remove(endpos)
            } else if (mode == 2) {
                //롱노트 중이 아니었고 자신이 롱노트라면 등록한다.
                prevID = 0
                longnoteIDs[endpos] = idd
            }
            //롱노트 중도 아니었고 자신도 롱노트가 아니다
            if ((mode == 1) and (flick == 0)) {
                prevID = 0
            }
            notes.add(
                Note(
                    idd,
                    0,
                    getColor(musicInfo.circleType),
                    twMode,
                    flick,
                    row["sec"]!!.toFloat(),
                    1.0f,
                    row["startPos"]!!.toFloat(),
                    endpos,
                    arrayOf(prevID)
                )
            )
        }
        return notes
    }


    fun parseFumens() {
        for (file in fumenFolder.listFiles()) {
            var cursorFumens: Cursor? = null
            try {
                val fumenDB =
                    SQLiteDatabase.openDatabase(file!!.path, null, SQLiteDatabase.OPEN_READONLY)
                cursorFumens =
                    fumenDB.query("blobs", arrayOf("name", "data"), null, null, null, null, null)
                val color = arrayOf(0, 0, 0, 0)
                while (cursorFumens.moveToNext()) {
                    parseOne(cursorFumens, color)
                }
            } catch (e: SQLException) {
                continue
            } finally {
                cursorFumens?.close()
            }
        }
    }

    private fun parseOne(cursorFumens: Cursor, color: Array<Int>) {
        var name = cursorFumens.getString(0)
        if (!name[name.length - 5].isDigit())
            return
        name = name.substring(13)
        name = name.substringBefore('.')
        val musicIndex = Integer.parseInt(name.substringBefore('.'))
        val difficulty = Integer.parseInt(name.substringAfter('_'))
        val fumenStr = cursorFumens.getBlob(1).toString()
        val parsedFumen = csvReader().readAllWithHeader(fumenStr)

        val prevIDs = HashMap<Int, Int>()
        val longnoteIDs = HashMap<Float, Int>()
        val notes = ArrayList<Note>()
        var prevID = 0
        var idd = 0
        for (row in parsedFumen) {
            prevID = 0
            var gid = row["groupId"]!!.toInt()
            var mode = row["type"]!!.toInt()
            if (mode > 3)
                continue
            idd++
            var twMode = getTWMode(mode)
            val endpos = row["finishPos"]!!.toFloat()
            val flick =
                getTW5Flick(row["status"]!!.toInt())
            if (gid == 0) {
                //...
            } else {
                if (prevIDs.containsKey(gid)) {
                    prevID = prevIDs[gid]!!
                } else {
                    //...
                }
                prevIDs[gid] = idd
            }
            if (longnoteIDs.containsKey(endpos)) {
                //롱노트 중이었다면 해제한다. 자신의 prev를 그 롱노트로 설정한다.
                prevID = longnoteIDs[endpos]!!
                twMode = 1
                longnoteIDs.remove(endpos)
            } else if (mode == 2) {
                //롱노트 중이 아니었고 자신이 롱노트라면 등록한다.
                prevID = 0
                longnoteIDs[endpos] = idd
            }
            //롱노트 중도 아니었고 자신도 롱노트가 아니다
            if ((mode == 1) and (flick == 0)) {
                prevID = 0
            }
            notes.add(
                Note(
                    idd,
                    0,
                    color,
                    twMode,
                    flick,
                    row["sec"]!!.toFloat(),
                    1.0f,
                    row["startPos"]!!.toFloat(),
                    endpos,
                    arrayOf(prevID)
                )
            )
        }
    }
}