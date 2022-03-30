package com.kyhsgeekcode.dereinfo

import android.util.Log
import timber.log.Timber
import java.io.File
import java.io.ObjectInputStream
import java.io.ObjectOutputStream
import java.io.Serializable

typealias providerType<Key, Value> = (Key) -> Value?

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
            Timber.e(e, "Er")
        } finally {
            if (!this::dataMap.isInitialized) {
                dataMap = HashMap()
            }
        }
    }

    private val providers = ArrayList<providerType<Key, Value>>()

    operator fun get(key: Key): Value? {
        freeMemory()
        if (!dataMap.containsKey(key))
            dataMap[key] = provider(key) ?: return null
        return dataMap[key]!!
    }

    private fun freeMemory() {
        val runtime = Runtime.getRuntime()
        val usedMemInMB = (runtime.totalMemory() - runtime.freeMemory()) / 1048576L
        val maxHeapSizeInMB = runtime.maxMemory() / 1048576L
        val availHeapSizeInMB = maxHeapSizeInMB - usedMemInMB
        if (availHeapSizeInMB < 2) {
            Timber.d("Freeing memory; $usedMemInMB MB used; max $maxHeapSizeInMB avail $availHeapSizeInMB")
            // TODO: Evict
            dataMap.clear()
        }
    }

    fun refreshByLevel(key: Key, level: Int): Value? {
        val newVal = providers[level](key)
        dataMap[key] = newVal ?: return null
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
