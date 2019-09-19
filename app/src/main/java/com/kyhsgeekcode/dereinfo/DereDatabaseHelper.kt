package com.kyhsgeekcode.dereinfo

import android.content.Context
import android.database.sqlite.SQLiteDatabase
import android.os.Environment
import java.io.File

//This allows access to dere database
class DereDatabaseHelper() {
    constructor(context: Context):this()  {
        val datadir = context.getExternalFilesDir(null).parentFile.parentFile
        val dereFilesDir = File(datadir,"jp.co.bandainamcoent.BNEI0242/files/")
        val manifestFile = File(dereFilesDir,"manifest/").listFiles()[0]
        val fumenFolder = File(dereFilesDir,"a/")
        var maxlen = 0L
        var fumensDBfile: File? = null
        for (file in fumenFolder.listFiles())
        {
            val len = file.length();
            if(len>maxlen)
            {
                maxlen = len
                fumensDBfile = file
                if(maxlen > 10000000)
                    break
            }
        }
        val fumensDB = SQLiteDatabase.openDatabase(fumensDBfile!!.path, null, SQLiteDatabase.OPEN_READONLY)

        val manifestDB =  SQLiteDatabase.openDatabase(manifestFile.path,null,SQLiteDatabase.OPEN_READONLY)

        val cursorLiveData = fumensDB.query("live_data", arrayOf("music_data_id"),null,null,null,null,null)

        val music_data_id_index = cursorLiveData.getColumnIndex("music_data_id")

        while (cursorLiveData.moveToNext()) {
            val music_data_id = cursorLiveData.getInt(music_data_id_index)
            val cursorMusicData = fumensDB.query("music_data",arrayOf("id","name","bpm","composer","lyricist","sound_offset","sound_length"),"id=?",arrayOf(music_data_id.toString()),null,null,null)
            val music_name_index = cursorMusicData.getColumnIndex("name")
            val name = cursorMusicData.getString(music_name_index)
            val composer_index = cursorMusicData.getColumnIndex("composer")
            val composer = cursorMusicData.getString(composer_index)

        }

    }
}