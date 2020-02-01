package com.kyhsgeekcode.dereinfo.model

class SongFilter {
    fun pass(musicInfo: MusicInfo): Boolean {
        val circleType = CircleType.fromInt(musicInfo.circleType)
        //Log.d("SongFilter","musicInfoType:${circleType},permittedType:${permittedType.joinToString()}}")
        if (!permittedType.contains(circleType))
            return false
        return true
    }

    val permittedType: MutableSet<CircleType> = HashSet()
    fun addFilter(vararg types: CircleType): SongFilter {
        permittedType.clear()
        permittedType.addAll(types)
        return this
    }
}