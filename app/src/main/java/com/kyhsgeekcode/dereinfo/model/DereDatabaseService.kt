package com.kyhsgeekcode.dereinfo.model

import android.database.Cursor
import android.database.SQLException
import android.database.sqlite.SQLiteDatabase
import android.util.Log
import androidx.core.text.isDigitsOnly
import com.github.doyaaaaaken.kotlincsv.dsl.csvReader
import com.kyhsgeekcode.dereinfo.FileDataCache
import com.kyhsgeekcode.dereinfo.cardunit.*
import com.kyhsgeekcode.dereinfo.checkIfDatabase
import com.kyhsgeekcode.dereinfo.dao.SkillLifeValueDao
import com.kyhsgeekcode.dereinfo.dao.SkillLifeValueGrandDao
import com.kyhsgeekcode.dereinfo.dao.SkillMotifValueDao
import com.kyhsgeekcode.dereinfo.dao.SkillMotifValueGrandDao
import com.kyhsgeekcode.dereinfo.dereclient.AssetDownloader
import com.kyhsgeekcode.dereinfo.enums.CircleType.Companion.getColor
import com.kyhsgeekcode.dereinfo.enums.FlickMode
import com.kyhsgeekcode.dereinfo.loadObject
import com.kyhsgeekcode.dereinfo.saveObject
import timber.log.Timber
import java.io.BufferedOutputStream
import java.io.File
import java.io.FileOutputStream
import java.nio.charset.Charset
import java.util.zip.ZipEntry
import java.util.zip.ZipOutputStream
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.collections.set


//This allows access to dere database
@Singleton
class DereDatabaseService @Inject constructor(
    filesDir: File,
    private val skillMotifValueDao: SkillMotifValueDao,
    private val skillLifeValueDao: SkillLifeValueDao,
    private val skillMotifValueGrandDao: SkillMotifValueGrandDao,
    private val skillLifeValueGrandDao: SkillLifeValueGrandDao,
) {
    val TAG = "DereDBHelper"

    //    val manifestFile: File
    var fumensDBFile: File = File("")
    val fumenFolder: File = File(filesDir, "a/")
    val musicFolder: File = File(filesDir, "l/")

    var musicIDToInfo: MutableMap<Int, MusicData> = HashMap()
    var musicNumberToMusicID =
        HashMap<Int, Int>()  //SerializableSparseIntArray = SerializableSparseIntArray()
    var musicIDTomusicNumber = HashMap<Int, Int>() //  = SerializableSparseIntArray()

    //        val datadir =
    //            "/sdcard/Android/data/" //context.getExternalFilesDir(null)!!.parentFile.parentFile
    //        manifestFile = File(dereFilesDir, "manifest/").listFiles()[0]
    //    val dereFilesDir = File(datadir, "jp.co.bandainamcoent.BNEI0242/files/")
    suspend fun prepareDatabase() {
        AssetDownloader.download()
        try {
            loadFumenDBFileFromCache()
        } catch (e: Exception) {
            searchMainDB()
        }
        initSkillAndLeaderSkillData()
    }

    private fun searchMainDB() {
        Timber.d("fumenFolder:$fumenFolder")
        val fumensDBFileTmp = fumenFolder.listFiles()?.find {
            it.length() > 1000_0000
        }
        fumensDBFile = fumensDBFileTmp ?: error("No fumen file found")
    }

    private fun parseDatabases(publisher: (Int, Int, MusicData, String?) -> Unit) {
        musicNumberToMusicID.clear()
        musicIDTomusicNumber.clear()
        musicIDToInfo.clear()

        val fumensDB =
            SQLiteDatabase.openDatabase(fumensDBFile.path, null, SQLiteDatabase.OPEN_READONLY)

//        val manifestDB =
//            SQLiteDatabase.openDatabase(manifestFile.path, null, SQLiteDatabase.OPEN_READONLY)

        val cursorLiveData =
            fumensDB.rawQuery(
                "SELECT id,music_data_id,circle_type FROM live_data WHERE end_date='' AND prp_flag=1",
                null
            )

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
                    "sound_length",
                    "name_kana"
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
            val nameKana = cursorMusicData.getString(7)
            cursorMusicData.close()
            val liveDataId = cursorLiveData.getInt(liveDataIdIndex)
            val circleType = cursorLiveData.getInt(circleTypeIndex)
            musicNumberToMusicID[liveDataId] = musicDataId
            musicIDTomusicNumber[musicDataId] = liveDataId
            //Log.w(TAG, "musicIDToMusicNumber[${musicDataId}]=${liveDataId}")
            val musicData = MusicData(
                musicDataId,
                name,
                bpm,
                composer,
                lyricist,
                soundOffset,
                soundLength,
                circleType,
                nameKana
            )
            musicIDToInfo[musicDataId] = musicData
            currentCount++
            publisher(totalCount, currentCount, musicData, null)
        }
        cursorLiveData.close()
        fumensDB.close()
    }

    var musicNumberToFumenFile: MutableMap<Int, File> = HashMap()
    fun indexFumens(publisher: (Int, Int, MusicData?, String?) -> Unit) {
        musicNumberToFumenFile.clear()
        var cursorFumens: Cursor? = null
        val fileList = fumenFolder.listFiles()
        for (fileWithIndex in fileList.withIndex()) {
            val file = fileWithIndex.value
            publisher(fileList.size, fileWithIndex.index, null, null)
            if (!checkIfDatabase(file)) {
                //Log.d(TAG, "Skip file")
                continue
            }
            var fumenDB: SQLiteDatabase? = null
            try {
                //Log.d(TAG, "Oh file")
                fumenDB =
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
            } catch (e: SQLException) {
                Log.e(TAG, "indexFumen ${file.name}", e)
                continue
            } finally {
                fumenDB?.close()
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
    val mainDBFileCacheFile = File(filesDir, mainDBFileCachename)
    val musicInfoFilename = "musicIDToInfo.dat"
    val indexToFumenFileFilename = "indexToFumenFile.dat"
    val musicInfoFile = File(filesDir, musicInfoFilename)
    val indexToFumenFileFile = File(filesDir, indexToFumenFileFilename)
    val musicNumberToMusicIDFileName = "musicNumberToMusicID.dat"
    val musicNumberToMusicIDFile = File(filesDir, musicNumberToMusicIDFileName)
    val musicIDToMusicNumberFileName = "musicIDToMusicNumber.dat"
    val musicIDToMusicNumberFile = File(filesDir, musicIDToMusicNumberFileName)
    val musicInfoIDToStatisticFileName = "musicInfoIDToStatistic.dat"
    val musicInfoIDToStatisticFile = File(filesDir, musicInfoIDToStatisticFileName)

    private fun saveToCache() {
//        searchMainDB()
//        parseDatabases()
//        fumensDBFile.delete()
        musicInfoFile.delete()
        indexToFumenFileFile.delete()
        musicNumberToMusicIDFile.delete()
        musicIDToMusicNumberFile.delete()
        musicInfoIDToStatisticFile.delete()
        saveObject(mainDBFileCacheFile, fumensDBFile)
        saveObject(musicInfoFile, musicIDToInfo)
        saveObject(indexToFumenFileFile, musicNumberToFumenFile)
        saveObject(musicNumberToMusicIDFile, musicNumberToMusicID)
        saveObject(musicIDToMusicNumberFile, musicIDTomusicNumber)
        saveObject(musicInfoIDToStatisticFile, musicInfoIDToStatistic)
    }

    private fun loadFromCache(): Boolean {
        try {
            loadFromCache_()
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

    private fun loadFromCache_() {
        loadFumenDBFileFromCache()
        musicIDToInfo = loadObject(musicInfoFile) as MutableMap<Int, MusicData>
        musicNumberToFumenFile = loadObject(indexToFumenFileFile) as MutableMap<Int, File>
        musicNumberToMusicID =
            loadObject(musicNumberToMusicIDFile) as HashMap<Int, Int> //as SerializableSparseIntArray
        musicIDTomusicNumber =
            loadObject(musicIDToMusicNumberFile) as HashMap<Int, Int> //SerializableSparseIntArray
        musicInfoIDToStatistic =
            loadObject(musicInfoIDToStatisticFile) as HashMap<Int, FumenStatistic>
    }

    private fun loadFumenDBFileFromCache() {
        fumensDBFile = loadObject(mainDBFileCacheFile) as File
    }

    suspend fun refreshCache(
        publisher: (Int, Int, MusicData?, String?) -> Unit,
        onFinish: () -> Unit
    ): Boolean = load(true, publisher, onFinish)

    suspend fun load(
        refresh: Boolean = false,
        publisher: (Int, Int, MusicData?, String?) -> Unit,
        onFinish: () -> Unit
    ): Boolean {
        try {
            if (loadFromCache() && !refresh) {
                for (musicInfo in musicIDToInfo.values.withIndex()) {
                    publisher(musicIDToInfo.size, musicInfo.index, musicInfo.value, null)
                }
            } else {
                publisher(100, 0, null, "Parsing main database...")
                parseDatabases(publisher)
                publisher(100, 0, null, "Selecting fumens...")
                indexFumens(publisher)
                publisher(100, 0, null, "Counting notes...")
                countFumens(publisher)
                publisher(100, 50, null, "Saving")
                saveToCache()
                publisher(100, 100, null, "Done")
            }
            Timber.d("size of databases:" + musicIDToInfo.size)
            Timber.d("Number of fumens:" + musicNumberToFumenFile.size)
            onFinish()
        } catch (e: java.lang.Exception) {
            Timber.e(e, "Error load")
            return false
        }
        return true
    }

//    fun peekFumens(musicNumber: Int): OneMusic {
//        Log.d(
//            TAG,
//            "musicIndex : ${musicNumber},indexToFumenFile size:${musicNumberToFumenFile.size}"
//        )
//        val fumenFile = musicNumberToFumenFile[musicNumber]
//        Log.d(TAG, "fumenFile:${fumenFile?.name}")
//        if (fumenFile == null)
//            throw java.lang.RuntimeException()
//        val fumenDB =
//            SQLiteDatabase.openDatabase(fumenFile!!.path, null, SQLiteDatabase.OPEN_READONLY)
//        val cursorFumens =
//            fumenDB.query("blobs", arrayOf("name"), null, null, null, null, null)
//        val difficulties: MutableMap<TW5Difficulty, OneDifficulty> = HashMap()
//        val info = musicIDToInfo[musicNumber] ?: MusicInfo(
//            0,
//            "Error occurred",
//            192,
//            "System",
//            "Unknown",
//            0,
//            1
//        )
//        while (cursorFumens.moveToNext()) {
//            var name = cursorFumens.getString(0)
//            if (!name[name.length - 5].isDigit())
//                continue
//            Log.d(TAG, "name:${name}")
//            name = name.substring(13)
//            name = name.substringBefore('.')
//            val maybeDifficulty = name.substringAfter('_')
//            if (!maybeDifficulty.isDigitsOnly()) {
//                Log.d(TAG, "name:${name} continue")
//                continue
//            }
//            val difficulty = Integer.parseInt(maybeDifficulty)
//            //Log.d(TAG, "difficulty:${difficulty}")
//            val twDifficulty = TW5Difficulty.valueOf(difficulty)
//            difficulties[twDifficulty] = OneDifficulty(twDifficulty, null)
//        }
//        cursorFumens.close()
//        return OneMusic(difficulties, info)
//    }

    var musicInfoIDToStatistic = HashMap<Int, FumenStatistic>()
    fun countFumens(publisher: (Int, Int, MusicData?, String?) -> Unit) {
        musicInfoIDToStatistic.clear()
        for (musicInfo in musicIDToInfo.values.withIndex()) {
            musicInfoIDToStatistic[musicInfo.value.id] = countFumen(musicInfo.value)
            publisher(musicIDToInfo.size, musicInfo.index, null, null)
        }
    }

    //Difficulty와 통계를 같이 낸다.
    fun countFumen(musicData: MusicData): FumenStatistic {
        val fumensDB =
            SQLiteDatabase.openDatabase(fumensDBFile.path, null, SQLiteDatabase.OPEN_READONLY)
        val musicNumber = musicIDTomusicNumber[musicData.id]
        val fumenFile = musicNumberToFumenFile[musicNumber]
        if (fumenFile == null) {
            Log.e(TAG, "fumenfile for id${musicData.id}(${musicData.name}) is null")
            return HashMap()
        }
        val fumenDB =
            SQLiteDatabase.openDatabase(fumenFile.path, null, SQLiteDatabase.OPEN_READONLY)
        val cursorFumens =
            fumenDB.query("blobs", arrayOf("name", "data"), null, null, null, null, null)
        val result: MutableMap<TW5Difficulty, OneStatistic> = HashMap()
        while (cursorFumens.moveToNext()) {
            val name = cursorFumens.getString(0)
            val parsedName = parseFumenName(name) ?: continue
            val musicNumber = parsedName.first
            val difficulty = parsedName.second
            val cursorLiveData = fumensDB.query(
                "live_detail",
                arrayOf("level_vocal"),
                "live_data_id=? AND difficulty_type=?",
                arrayOf(musicNumber.toString(), difficulty.value.toString()),
                null,
                null,
                null
            )
            val fumenStr = cursorFumens.getBlob(1).toString(Charsets.UTF_8)
            val rawNotes = csvReader().readAllWithHeader(fumenStr)
            //Log.d(TAG, "len:${fumenStr.length}")
            //Log.d(TAG, "rawNotes:${rawNotes.size}")
            val resultOne = HashMap<StatisticIndex, Float>()
            if (cursorLiveData.moveToNext()) {
                val density = cursorLiveData.getInt(0)
                resultOne[StatisticIndex.Level] = density.toFloat()
            } else {
                resultOne[StatisticIndex.Level] = -1.0f//error
            }
            cursorLiveData.close()
            val counter = HashMap<StatisticIndex, Int>()
            for (rawNote in rawNotes) {
                val type = rawNote["type"]!!.toInt()
                if (type > 7)
                    continue
                val status = rawNote["status"]!!.toInt()
                val modeAndFlick = getModeAndFlick(type, status)
                val mode = modeAndFlick.first
                val flick = modeAndFlick.second
                counter[StatisticIndex.Total] = (counter[StatisticIndex.Total] ?: 0) + 1
                //시간도 계산?
                //7초 11초 9초 나오겠지. 6/7 9/11 7.5/9
                val index = StatisticIndex.makeIndex(mode, flick)
                counter[index] = (counter[index] ?: 0) + 1
                val actIndices = StatisticIndex.makeIndex(index, rawNote["sec"]!!.toFloat())
                for (actIndex in actIndices)
                    counter[actIndex] = (counter[actIndex] ?: 0) + 1

                val totalIndices =
                    StatisticIndex.makeIndex(StatisticIndex.Total, rawNote["sec"]!!.toFloat())
                for (totalIndex in totalIndices)
                    counter[totalIndex] = (counter[totalIndex] ?: 0) + 1

            }
            //Log.d(TAG, "Counter:${counter}")
            resultOne[StatisticIndex.Total] = (counter[StatisticIndex.Total] ?: 0).toFloat()
            for (index in StatisticIndex.values()) {
                if (index == StatisticIndex.Total) continue
                if (index == StatisticIndex.Level) continue
                resultOne[index] =
                    ((counter[index] ?: 0).toFloat() / (counter[StatisticIndex.Total]
                        ?: 1).toFloat()) * 100.0f
            }
            result[difficulty] = resultOne
        }
        cursorFumens.close()
        fumenDB.close()
        //Log.d(TAG, "Result:$result")
        return result
    }

    fun parseFumenName(name: String): Pair<Int, TW5Difficulty>? {
        if (!name[name.length - 5].isDigit())
            return null
        var subname = name.substring(13)
        subname = subname.substringBefore('.')
        val maybeDifficulty = subname.substringAfter('_')
        if (!maybeDifficulty.isDigitsOnly()) {
            Log.d(TAG, "name:${name} continue")
            return null
        }
        val twDifficulty = TW5Difficulty.valueOf(Integer.parseInt(maybeDifficulty))
        val musicIndex = Integer.parseInt(subname.split('/')[0])
        return Pair(musicIndex, twDifficulty)
    }

    fun getModeAndFlick(type: Int, status: Int): Pair<TWMode, FlickMode> = when (type) {
        4 -> Pair(TWMode.Tap, FlickMode.None)
        5 -> Pair(TWMode.Slide, FlickMode.None)
        6 -> Pair(TWMode.Tap, FlickMode.Left)
        7 -> Pair(TWMode.Tap, FlickMode.Right)
        8 -> Pair(TWMode.Damage, FlickMode.None)
        else -> Pair(TWMode.fromType(type), FlickMode.fromStatus(status))
    }

    // Music ID
    val parsedFumenCache: FileDataCache<Pair<musicID, TW5Difficulty>, OneMusic> =
        FileDataCache(filesDir.resolve("parsedFumen").apply {
            createNewFile()
        }) { key ->
            val musicInfo = musicIDToInfo[key.first] ?: return@FileDataCache null
            parseFumen(musicInfo, key.second)
        }


    //5개를 파싱해라.
    fun parseFumen(musicData: MusicData, wantedDifficulty: TW5Difficulty): OneMusic? {
        val fumenFile =
            musicNumberToFumenFile[musicIDTomusicNumber[musicData.id]] ?: return null//wrong
        val fumenDB =
            SQLiteDatabase.openDatabase(fumenFile.path, null, SQLiteDatabase.OPEN_READONLY)
        val cursorFumens =
            fumenDB.query("blobs", arrayOf("name", "data"), null, null, null, null, null)
        val difficulties: MutableMap<TW5Difficulty, OneDifficultyData> = HashMap()
        val info = musicData
        while (cursorFumens.moveToNext()) {
            var name = cursorFumens.getString(0)
            if (!name[name.length - 5].isDigit())
                continue
            name = name.substring(13)
            name = name.substringBefore('.')
            val maybeDifficulty = name.substringAfter('_')
            if (!maybeDifficulty.isDigitsOnly())
                continue
            val difficulty = Integer.parseInt(maybeDifficulty)
            val twDifficulty = TW5Difficulty.valueOf(difficulty)
            if (wantedDifficulty != twDifficulty)
                continue
            val fumenStr = cursorFumens.getBlob(1).toString(
                Charset.defaultCharset()
            )
//            Log.d(TAG, "FumenStr: $fumenStr")
            val notes: List<Note>
            if (twDifficulty == TW5Difficulty.Piano || twDifficulty == TW5Difficulty.Forte)
                notes = parseDereFumenGrand(fumenStr, info)
            else if (twDifficulty == TW5Difficulty.Witch)
                notes = parseDereFumenWitch(fumenStr, info)
            else
                notes = parseDereFumen(fumenStr, info)
            difficulties[twDifficulty] = OneDifficultyData(twDifficulty, notes)
        }
        cursorFumens.close()
        return OneMusic(difficulties, info)
    }

    private fun parseDereFumenWitch(fumenStr: String, info: MusicData): List<Note> {
        val parsedFumen = csvReader().readAllWithHeader(fumenStr)
//        Log.d(TAG, "parsing Fumen; size ${parsedFumen.size}, fumenStr $fumenStr")

        val prevIDs = HashMap<Int, Int>()
        val longnoteIDs = HashMap<Float, Int>()
        val IDToNotes = HashMap<Int, Note>()
        val notes = ArrayList<Note>()
        var prevID = 0
        var idd = 0
        for (row in parsedFumen) {
            prevID = 0
            val gid = row["groupId"]!!.toInt()
            val mode = row["type"]!!.toInt()
            val visible = row["visible"]?.toIntOrNull() ?: 100
            if (visible < 0)
                continue
            if (mode > 3 && mode != 8)
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
                twMode = TWMode.Hold
                longnoteIDs.remove(endpos)
            } else if (mode == 2) {
                //롱노트 중이 아니었고 자신이 롱노트라면 등록한다.
                prevID = 0
                longnoteIDs[endpos] = idd
            }
            //롱노트 중도 아니었고 자신도 롱노트가 아니다
            else if ((mode == 1) and (IDToNotes[prevID]?.isFlick() == false) /*and (flick == FlickMode.None)*/) {
                prevID = 0
            }
            val theNote = Note(
                idd,
                0,
                getColor(info.circleType),
                twMode,
                flick,
                row["sec"]!!.toFloat(),
                1.0f,
                row["startPos"]!!.toFloat(),
                endpos,
                arrayOf(prevID),
                row["sync"]?.toInt() == 1
            )
            IDToNotes[idd] = theNote
            for (id in theNote.previds) {
                IDToNotes[id]?.addNext(theNote)
            }
            notes.add(theNote)
        }
        return notes
    }

    // Long note has no group ID.
    // Flick note without group ID finishes a previous long note.
    private fun parseDereFumen(
        fumenStr: String,
        musicData: MusicData
    ): List<Note> {
        val parsedFumen = csvReader().readAllWithHeader(fumenStr)
//        Log.d(TAG, "parsing Fumen; size ${parsedFumen.size}, fumenStr $fumenStr")

        val prevIDs = HashMap<Int, Int>()
        val longnoteIDs = HashMap<Float, Int>()
        val IDToNote = HashMap<Int, Note>()
        val notes = ArrayList<Note>()
        var prevID = 0
        var currentNoteId = 0
        for (row in parsedFumen) {
            prevID = 0
            val groupId = row["groupId"]!!.toInt()
            val mode = row["type"]!!.toInt()
            if (mode > 3 && mode != 8)
                continue
            currentNoteId++
            var twMode = getTWMode(mode)
            val endpos = row["finishPos"]!!.toFloat()
            val flick = getTW5Flick(row["status"]!!.toInt())

            if (groupId != 0) {
                if (prevIDs.containsKey(groupId)) {
                    prevID = prevIDs[groupId]!!
//                    prevIDs.remove(groupId)
                } else {
                    //...
                }
                prevIDs[groupId] = currentNoteId
            }
            if (longnoteIDs.containsKey(endpos)) {
                //롱노트 중이었다면 해제한다. 자신의 prev를 그 롱노트로 설정한다.
                prevID = longnoteIDs[endpos]!!
                twMode = TWMode.Hold
                longnoteIDs.remove(endpos)
            } else if (mode == 2) {
                //롱노트 중이 아니었고 자신이 롱노트라면 등록한다.
                prevID = 0
                longnoteIDs[endpos] = currentNoteId
            }
            // 내가 일반 노트고 기존이 플릭이 아니고 자신도 롱노트가 아니다
            else if ((mode == 1) and (IDToNote[prevID]?.isFlick() == false) /*and (flick == FlickMode.None)*/) { // ((mode == 1) and (IDToNote[prevID]?.isFlick() == false) /*and (flick == FlickMode.None)*/) {
//                prevID = 0
            }
            val theNote = Note(
                currentNoteId,
                0,
                getColor(musicData.circleType),
                twMode,
                flick,
                row["sec"]!!.toFloat(),
                1.0f,
                row["startPos"]!!.toFloat(),
                endpos,
                arrayOf(prevID),
                row["sync"]?.toInt() == 1
            )
            IDToNote[currentNoteId] = theNote
            for (id in theNote.previds) {
                IDToNote[id]?.addNext(theNote)
            }
            notes.add(theNote)
            //...
        }
        return notes
    }

    private fun parseDereFumenGrand(
        fumenStr: String,
        musicData: MusicData
    ): List<Note> {
        val parsedFumen = csvReader().readAllWithHeader(fumenStr)
        Timber.d("parsing Fumen; size " + parsedFumen.size + ", fumenStr " + fumenStr)

        val prevIDs = HashMap<Int, Int>()
        val longnoteIDs = HashMap<Float, Int>()
        val IDToNotes = HashMap<Int, Note>()
        val notes = ArrayList<Note>()
        var prevID = 0
        var idd = 0
        for (row in parsedFumen) {
            prevID = 0
            var gid = row["groupId"]!!.toInt()
            var mode = row["type"]!!.toInt()
            if (mode > 8)
                continue
            idd++
            val twModePair = getTWModeGrand(mode)
            var twMode = twModePair.first
            val flick = twModePair.second
            val endpos = row["finishPos"]!!.toFloat()
            val width = row["status"]!!.toInt()
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
                twMode = TWMode.Tap
                longnoteIDs.remove(endpos)
            } else if (mode == 2) {
                //롱노트 중이 아니었고 자신이 롱노트라면 등록한다.
                prevID = 0
                longnoteIDs[endpos] = idd
            }
            //롱노트 중도 아니었고 자신도 롱노트가 아니다
            else if ((mode == 1) and (flick == FlickMode.None)) {
                prevID = 0
            }
            val theNote = Note(
                idd,
                width,
                getColor(musicData.circleType),
                twMode,
                flick,
                row["sec"]!!.toFloat(),
                1.0f,
                row["startPos"]!!.toFloat(),
                endpos,
                arrayOf(prevID),
                row["sync"]?.toInt() == 1
            )
            IDToNotes[idd] = theNote
            for (id in theNote.previds) {
                IDToNotes[id]?.addNext(theNote)
            }
            notes.add(theNote)
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
            val flick = getTW5Flick(row["status"]!!.toInt())
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
                twMode = TWMode.Tap
                longnoteIDs.remove(endpos)
            } else if (mode == 2) {
                //롱노트 중이 아니었고 자신이 롱노트라면 등록한다.
                prevID = 0
                longnoteIDs[endpos] = idd
            }
            //롱노트 중도 아니었고 자신도 롱노트가 아니다
            if ((mode == 1) and (flick == FlickMode.None)) {
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

    //
    private suspend fun initSkillAndLeaderSkillData() {
        Timber.d("QueryToList Test start")
//        val fumensDB =
//            SQLiteDatabase.openDatabase(fumensDBFile.path, null, SQLiteDatabase.OPEN_READONLY)
        motifModels = skillMotifValueDao.getAllSkillMotifValue()
        motifModelsGrand =
            skillMotifValueGrandDao.getAllSkillMotifValueGrand() // queryToList(fumensDB, "skill_motif_value_grand")
        lifeSparkleModels =
            skillLifeValueDao.getAllSkillLifeValue() // queryToList(fumensDB, "skill_life_value")
        lifeSparkleModelsGrand =
            skillLifeValueGrandDao.getAllSkillLifeValueGrand() // queryToList(fumensDB, "skill_life_value_grand")
        Timber.d("QueryToList Test end")
    }

    fun motifBonus(appeal: Int, type: Int, isGrand: Boolean = false): Int {
        val model: ISkillMotifValueModel
        model = if (isGrand) {
            motifModelsGrand.first {
                appeal >= it.motif_value
            }
        } else {
            motifModels.first {
                appeal >= it.motif_value
            }
        }
        return when (type) {
            1 -> model.type_01_value
            2 -> model.type_02_value
            else -> model.type_01_value
        }
    }

    fun lifeSparkleBonus(life: Int, type: Int, isGrand: Boolean = false): Int {
        val model: ISkillLifeValueModel = if (isGrand) {
            lifeSparkleModelsGrand.first {
                life >= it.life_value
            }
        } else {
            lifeSparkleModels.first {
                life >= it.life_value
            }
        }
        return when (type) {
            1 -> model.type_01_value
            2 -> model.type_02_value
            else -> model.type_01_value
        }
    }


    suspend fun exportTW(
        musicList: List<MusicData>,
        difficulties: List<TW5Difficulty>,
        fileOutputStream: FileOutputStream,
        progressHandler: suspend (Int, String?) -> Unit
    ) {
        val bos = BufferedOutputStream(fileOutputStream)
        val zos = ZipOutputStream(bos)
        var count = 0
        musicList.forEach { mi ->
            difficulties.forEach { diffi ->
                val oneDifficulty = parsedFumenCache[Pair(
                    mi.id,
                    diffi
                )]?.difficulties?.get(diffi)
                oneDifficulty?.toJson(mi, this)?.run {
                    val fileName = "${mi.id}___${mi.name}___${diffi.name}".replace("/", "-")
                        .replace("\\n", "-")
                    zos.putNextEntry(ZipEntry(fileName))
                    byteInputStream().copyTo(zos)
                    zos.closeEntry()
                } ?: run {
                    Timber.d("No such difficulty exists: " + diffi.name)
                    progressHandler(count, "No ${diffi.name} of ${mi.name}")
                }
            }
            progressHandler(count, mi.name)
            count++
        }
        zos.close()
    }

    fun getSkillData(skillId: Int): SkillModel? {
        TODO("Not yet implemented")
    }

    fun getLeaderSkillData(leaderSkillId: Int): LeaderSkillModel? {
        TODO("Not yet implemented")
    }


    lateinit var motifModels: List<SkillMotifValueModel>
    lateinit var motifModelsGrand: List<SkillMotifValueGrandModel>
    lateinit var lifeSparkleModels: List<ISkillLifeValueModel>
    lateinit var lifeSparkleModelsGrand: List<ISkillLifeValueModel>
}

