package com.kyhsgeekcode.dereinfo.model

import com.google.gson.GsonBuilder

class Fumen(val notes: List<Note>) {
    fun toTWStr(): String {
        val gson = GsonBuilder().create()
        val twObject = HashMap<String, Any>()
        twObject["version"] = 2
        val metadata = HashMap<String, Any>()
        metadata["level"] = 4
        metadata["artist"] = "Deresute"
        metadata["mapper"] = "original"
        metadata["densitiy"] = 29
        metadata["bpm"] = intArrayOf(100)
        metadata["bpmQueue"] = intArrayOf(100)
        metadata["beats"] = intArrayOf(100)
        metadata["beatsQueue"] = intArrayOf(100)
        twObject["metadata"] = metadata
        val notesArrayList = ArrayList<HashMap<String, Any>>()
        for (noteI in notes.withIndex()) {
            val note = noteI.value
            val noteObject = HashMap<String, Any>()
            noteObject["ID"] = noteI.index + 1
            noteObject["Size"] = 0
            noteObject["Color"] = note.color
            noteObject["Mode"] = note.twMode
            noteObject["Flick"] = note.flick
            noteObject["Time"] = note.time
            noteObject["Speed"] = 1.0
            noteObject["StartLine"] = note.startline
            noteObject["EndLine"] = note.endline
            noteObject["PrevIDs"] = note.previds
            noteObject["YPos"] = 100
            notesArrayList.add(noteObject)
        }
        twObject["notes"] = notesArrayList
        return gson.toJson(twObject)
    }
}
