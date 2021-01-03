package com.kyhsgeekcode.dereinfo

import android.util.SparseArray
import java.io.IOException
import java.io.ObjectInputStream
import java.io.ObjectOutputStream

typealias SerializableSparseIntArray = SerializableSparseArray<Int>

class SerializableSparseArray<E> : SparseArray<E>, java.io.Serializable {

    constructor() : super()
    constructor(capacity: Int) : super(capacity)

    /**
     * This method is private but it is called using reflection by java
     * serialization mechanism. It overwrites the default object serialization.
     *
     * <br></br><br></br>**IMPORTANT**
     * The access modifier for this method MUST be set to **private** otherwise [java.io.StreamCorruptedException]
     * will be thrown.
     *
     * @param oos
     * the stream the data is stored into
     * @throws IOException
     * an exception that might occur during data storing
     */
    @Throws(IOException::class)
    private fun writeObject(oos: ObjectOutputStream) {
        val data = arrayOfNulls<Any>(size())
        for (i in data.indices) {
            val pair = Pair<Int, E>(keyAt(i), valueAt(i))
            data[i] = pair
        }
        oos.writeObject(data)
    }

    /**
     * This method is private but it is called using reflection by java
     * serialization mechanism. It overwrites the default object serialization.
     *
     * <br></br><br></br>**IMPORTANT**
     * The access modifier for this method MUST be set to **private** otherwise [java.io.StreamCorruptedException]
     * will be thrown.
     *
     * @param oos
     * the stream the data is read from
     * @throws IOException
     * an exception that might occur during data reading
     * @throws ClassNotFoundException
     * this exception will be raised when a class is read that is
     * not known to the current ClassLoader
     */
    @Throws(IOException::class, ClassNotFoundException::class)
    private fun readObject(ois: ObjectInputStream) {
        val data = ois.readObject() as Array<Any>
        for (i in data.indices) {
            val pair = data[i] as Pair<Int, E>
            this.append(pair.first, pair.second)
        }
        return
    }

    companion object {
        private const val serialVersionUID = 824056059663678000L
    }
}