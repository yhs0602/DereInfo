package com.kyhsgeekcode.dereinfo

import android.util.Log
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

typealias providerType<Key, Value> = (Key) -> Value

class FileDataCache<Key, Value>(
    val file: File,
    val provider: providerType<Key, Value>
) where Key : Serializable, Value : Serializable {
    private lateinit var dataMap: HashMap<Key, Value> // = HashMap()
    init {
        try {
            ObjectInputStream(file.inputStream()).use {
                dataMap = it.readObject() as HashMap<Key, Value>
                it.close()
            }
        } catch (e: Exception) {
            Log.e("Cache load", "Er", e)
        } finally {
            if(!this::dataMap.isInitialized) {
                dataMap = HashMap()
            }
        }
    }

    private val providers = ArrayList<providerType<Key, Value>>()

    fun get(key: Key): Value {
        if (!dataMap.containsKey(key))
            dataMap[key] = provider(key)
        return dataMap[key]!!
    }

    fun refreshByLevel(key: Key, level: Int): Value {
        val newVal = providers[level](key)
        dataMap[key] = newVal
        return newVal
    }

    fun saveTo() {
        try {
            ObjectOutputStream(file.outputStream()).use {
                it.writeObject(dataMap)
                it.close()
            }
        } catch (e: Exception) {
            Log.e("Cache Save", "", e)
        }
    }
}
