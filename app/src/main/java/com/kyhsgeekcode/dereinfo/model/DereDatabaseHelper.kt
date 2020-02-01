package com.kyhsgeekcode.dereinfo.model

import android.content.Context
import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.text.isDigitsOnly
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.kyhsgeekcode.dereinfo.checkIfDatabase
import com.kyhsgeekcode.dereinfo.loadObject
import com.kyhsgeekcode.dereinfo.model.CircleType.getColor
import com.kyhsgeekcode.dereinfo.saveObject
import java.io.File
import kotlin.collections.ArrayList
import kotlin.collections.HashMap
import kotlin.collections.List
import kotlin.collections.MutableMap
import kotlin.collections.set
import kotlin.collections.withIndex


//This allows access to dere database
class DereDatabaseHelper(context: Context) {
    companion object {
        lateinit var theInstance: DereDatabaseHelper
    }

    val TAG = "DereDBHelper"
    val manifestFile: File
    var fumensDBFile: File = File("")
    val fumenFolder: File

    var musicIDToInfo: MutableMap<Int, MusicInfo> = HashMap()
    var musicNumberToMusicID = HashMap<Int,Int>()  //SerializableSparseIntArray = SerializableSparseIntArray()
    var musicIDTomusicNumber = HashMap<Int,Int>() //  = SerializableSparseIntArray()

    init {
        val datadir = context.getExternalFilesDir(null).parentFile.parentFile
        val dereFilesDir = File(datadir, "jp.co.bandainamcoent.BNEI0242/files/")
        manifestFile = File(dereFilesDir, "manifest/").listFiles()[0]
        fumenFolder = File(dereFilesDir, "a/")
        try {
            loadFumenDBFileFromCache()
        } catch (e: Exception) {
            searchMainDB()
        }
    }

    private fun searchMainDB() {
        var maxlen = 0L
        var fumensDBFileTmp: File? = null
        for (file in fumenFolder.listFiles()) {
            //Log.d(TAG, file.name)
            val len = file.length()
            if (len > maxlen) {
                if (len > 10000000) {
                    maxlen = len
                    fumensDBFileTmp = file
                    break
                }
            }
            //Log.d(TAG, """$len""")
        }
        Log.d(TAG, "maxlen=${maxlen / 1000}")
        fumensDBFile = fumensDBFileTmp ?: error("No fumen file found")
    }

    suspend fun parseDatabases(publisher: (Int, Int, MusicInfo) -> Unit) {
        musicNumberToMusicID.clear()
        musicIDTomusicNumber.clear()
        musicIDToInfo.clear()

        val fumensDB =
            SQLiteDatabase.openDatabase(fumensDBFile.path, null, SQLiteDatabase.OPEN_READONLY)

        val manifestDB =
            SQLiteDatabase.openDatabase(manifestFile.path, null, SQLiteDatabase.OPEN_READONLY)

        val cursorLiveData =
            fumensDB.rawQuery("SELECT id,music_data_id,circle_type FROM live_data WHERE end_date='' AND prp_flag=1",null)

//            fumensDB.query(
//                "live_data",
//                arrayOf("id", "music_data_id", "circle_type"),
//                "end_date=? AND prp_flag=?",
//                arrayOf("''", "1"),
//                null,
//                null,
//                null
//            )

        val musicDataIdIndex = cursorLiveData.getColumnIndex("music_data_id")
        val liveDataIdIndex = cursorLiveData.getColumnIndex("id")
        val circleTypeIndex = cursorLiveData.getColumnIndex("circle_type")
        val totalCount = cursorLiveData.count
        var currentCount = 0
        while (cursorLiveData.moveToNext()) {
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
            cursorMusicData.moveToFirst()
//            Log.d(TAG, cursorMusicData.getColumnName(0))
//            Log.d(TAG, cursorMusicData.getColumnName(1))
//            Log.d(TAG, cursorMusicData.getColumnName(2))
//            Log.d(TAG, cursorMusicData.getColumnName(3))
//            Log.d(TAG, cursorMusicData.getColumnName(4))
//            Log.d(TAG, cursorMusicData.getColumnName(5))

            val musicNameIndex = cursorMusicData.getColumnIndex("name")
            val name = cursorMusicData.getString(1)
            val composerIndex = cursorMusicData.getColumnIndex("composer")
            val composer = cursorMusicData.getString(3)
            val bpmIndex = cursorMusicData.getColumnIndex("bpm")
            val bpm = cursorMusicData.getInt(2)
            val lyricistIndex = cursorMusicData.getColumnIndex("lyricist")
            val lyricist = cursorMusicData.getString(4)
            val soundOffsetIndex = cursorMusicData.getColumnIndex("sound_offset")
            val soundOffset = cursorMusicData.getInt(5)
            val soundLengthIndex = cursorMusicData.getColumnIndex("sound_length")
            val soundLength = cursorMusicData.getInt(6)
            cursorMusicData.close()
            val liveDataId = cursorLiveData.getInt(liveDataIdIndex)
            val circleType = cursorLiveData.getInt(circleTypeIndex)
            musicNumberToMusicID[liveDataId] = musicDataId
            musicIDTomusicNumber[musicDataId] = liveDataId
            //Log.w(TAG, "musicIDToMusicNumber[${musicDataId}]=${liveDataId}")
            val musicInfo = MusicInfo(
                musicDataId,
                name,
                bpm,
                composer,
                lyricist,
                soundOffset,
                soundLength,
                circleType
            )
            musicIDToInfo[musicDataId] = musicInfo
            currentCount++
            publisher(totalCount, currentCount, musicInfo)
        }
        cursorLiveData.close()
        fumensDB.close()
    }

    var musicNumberToFumenFile: MutableMap<Int, File> = HashMap()
    fun indexFumens(publisher: (Int, Int, MusicInfo?) -> Unit) {
        musicNumberToFumenFile.clear()
        var cursorFumens: Cursor? = null
        val fileList = fumenFolder.listFiles()
        for (fileWithIndex in fileList.withIndex()) {
            val file = fileWithIndex.value
            publisher(fileList.size, fileWithIndex.index, null)
            if (!checkIfDatabase(file)) {
                //Log.d(TAG, "Skip file")
                continue
            }
            try {
                //Log.d(TAG, "Oh file")
                val fumenDB =
                    SQLiteDatabase.openDatabase(file!!.path, null, SQLiteDatabase.OPEN_READONLY)
                cursorFumens = fumenDB.query("blobs", arrayOf("name"), null, null, null, null, null)
                while (cursorFumens.moveToNext()) {
                    var name = cursorFumens.getString(0)
                    if (!name[name.length - 5].isDigit())
                        continue
                    name = name.substring(13)
                    name = name.substringBefore('.')
                    val musicIndex = Integer.parseInt(name.split('/')[0])
                   // Log.d(TAG, "musicIndex ${musicIndex}, ${file.name}")
//                val difficulty = Integer.parseInt(name.substringAfter('_'))
                    musicNumberToFumenFile[musicIndex] = file
                    break
                }
                fumenDB.close()
            } catch (e: SQLException) {
                Log.e(TAG, "indexFumen",e)
                continue
            } finally {
                cursorFumens?.close()
            }
        }

        //check validity(debug)
//        for(item in musicIDToInfo.values) {
//            val musicNumber = musicIDTomusicNumber[item.id]
//            //Log.w(TAG, "Item.id:${item.id}, musicNumber:${musicNumber}")
//            val file = musicNumberToFumenFile[musicNumber]
//            if(file != null)
//                Log.w(TAG,"id:${item.id},name:${item.name},file:${file?.name}")
//        }
    }


    val mainDBFileCachename = "maindb.dat"
    val mainDBFileCacheFile = File(context.filesDir, mainDBFileCachename)
    val musicInfoFilename = "musicIDToInfo.dat"
    val indexToFumenFileFilename = "indexToFumenFile.dat"
    val musicInfoFile = File(context.filesDir, musicInfoFilename)
    val indexToFumenFileFile = File(context.filesDir, indexToFumenFileFilename)
    val musicNumberToMusicIDFileName = "musicNumberToMusicID.dat"
    val musicNumberToMusicIDFile = File(context.filesDir, musicNumberToMusicIDFileName)
    val musicIDToMusicNumberFileName = "musicIDToMusicNumber.dat"
    val musicIDToMusicNumberFile = File(context.filesDir, musicIDToMusicNumberFileName)


    private fun saveToCache(context: Context) {
//        searchMainDB()
//        parseDatabases()
        fumensDBFile.delete()
        musicInfoFile.delete()
        indexToFumenFileFile.delete()
        musicNumberToMusicIDFile.delete()
        musicIDToMusicNumberFile.delete()
        saveObject(mainDBFileCacheFile, fumensDBFile)
        saveObject(musicInfoFile, musicIDToInfo)
        saveObject(indexToFumenFileFile, musicNumberToFumenFile)
        saveObject(musicNumberToMusicIDFile, musicNumberToMusicID)
        saveObject(musicIDToMusicNumberFile, musicIDTomusicNumber)
    }

    private fun loadFromCache(context: Context): Boolean {
        try {
            loadFromCache_(context)
            if (musicIDToInfo.isEmpty())
                return false
            if (musicNumberToFumenFile.isEmpty())
                return false
            return true
        } catch (e: Exception) {
            Log.e(TAG, "error loading", e)
            return false
        }
    }

    private fun loadFromCache_(context: Context) {
        loadFumenDBFileFromCache()
        musicIDToInfo = loadObject(musicInfoFile) as MutableMap<Int, MusicInfo>
        musicNumberToFumenFile = loadObject(indexToFumenFileFile) as MutableMap<Int, File>
        musicNumberToMusicID = loadObject(musicNumberToMusicIDFile) as HashMap<Int,Int> //as SerializableSparseIntArray
        musicIDTomusicNumber = loadObject(musicIDToMusicNumberFile) as HashMap<Int, Int> //SerializableSparseIntArray
    }

    private fun loadFumenDBFileFromCache() {
        fumensDBFile = loadObject(mainDBFileCacheFile) as File
    }

    suspend fun refreshCache(
        context: Context,
        publisher: (Int, Int, MusicInfo?) -> Unit,
        onFinish: () -> Unit
    ): Boolean = load(context, true, publisher, onFinish)

    suspend fun load(
        context: Context,
        refresh: Boolean = false,
        publisher: (Int, Int, MusicInfo?) -> Unit,
        onFinish: () -> Unit
    ): Boolean {
        try {
            if (loadFromCache(context) && !refresh) {
                for (musicInfo in musicIDToInfo.values.withIndex()) {
                    publisher(musicIDToInfo.size, musicInfo.index, musicInfo.value)
                }
            } else {
                //publisher(100,0,null)

                parseDatabases(publisher)
                indexFumens(publisher)
                publisher(100, 50, null)
                saveToCache(context)
                publisher(100, 100, null)
            }
            Log.d(TAG, "size of databases:${musicIDToInfo.size}")
            Log.d(TAG, "Number of fumens:${musicNumberToFumenFile.size}")
            onFinish()
        } catch (e: java.lang.Exception) {
            Log.e(TAG, "Error load", e)
            return false
        }
        return true
    }


    //5개를 파싱해라.

    fun peekFumens(musicNumber: Int): OneMusic {
        Log.d(
            TAG,
            "musicIndex : ${musicNumber},indexToFumenFile size:${musicNumberToFumenFile.size}"
        )
        val fumenFile = musicNumberToFumenFile[musicNumber]
        Log.d(TAG, "fumenFile:${fumenFile?.name}")
        if(fumenFile==null)
            throw java.lang.RuntimeException()
        val fumenDB =
            SQLiteDatabase.openDatabase(fumenFile!!.path, null, SQLiteDatabase.OPEN_READONLY)
        val cursorFumens =
            fumenDB.query("blobs", arrayOf("name"), null, null, null, null, null)
        val difficulties: MutableMap<TW5Difficulty, OneDifficulty> = HashMap()
        val info = musicIDToInfo[musicNumber] ?: MusicInfo(
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
            Log.d(TAG, "name:${name}")
            name = name.substring(13)
            name = name.substringBefore('.')
            val maybeDifficulty = name.substringAfter('_')
            if(!maybeDifficulty.isDigitsOnly()) {
                Log.d(TAG,"name:${name} continue")
                continue
            }
            val difficulty = Integer.parseInt(maybeDifficulty)
            //Log.d(TAG, "difficulty:${difficulty}")
            val twDifficulty = TW5Difficulty.valueOf(difficulty)
            difficulties[twDifficulty] = OneDifficulty(twDifficulty, null)
        }
        cursorFumens.close()
        return OneMusic(difficulties, info)
    }

    fun parseFumen(music: OneMusic, wantedDifficulty: TW5Difficulty): OneMusic {
        val fumenFile = musicNumberToFumenFile[music.musicInfo.id]
        val fumenDB =
            SQLiteDatabase.openDatabase(fumenFile!!.path, null, SQLiteDatabase.OPEN_READONLY)
        val cursorFumens =
            fumenDB.query("blobs", arrayOf("name", "data"), null, null, null, null, null)
        val difficulties: MutableMap<TW5Difficulty, OneDifficulty> = HashMap()
        val info = music.musicInfo
        while (cursorFumens.moveToNext()) {
            var name = cursorFumens.getString(0)
            if (!name[name.length - 5].isDigit())
                continue
            name = name.substring(13)
            name = name.substringBefore('.')
            val difficulty = Integer.parseInt(name.substringAfter('_'))
            val twDifficulty = TW5Difficulty.valueOf(difficulty)
            if (wantedDifficulty != twDifficulty)
                continue
            val fumenStr = cursorFumens.getBlob(1).toString()
            val notes = parseDereFumen(fumenStr, info)
            difficulties[twDifficulty] = OneDifficulty(twDifficulty, notes)
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