package com.kyhsgeekcode.dereinfo.model

import com.kyhsgeekcode.dereinfo.enums.CircleType

class SongFilter {
    fun pass(musicInfo: MusicInfo): Boolean {
        val circleType = CircleType.fromInt(musicInfo.circleType)
        //Log.d("SongFilter","musicInfoType:${circleType},permittedType:${permittedType.joinToString()}}")
        if (!permittedType.contains(circleType))
            return false
        if (shouldHaveMasterPlus) {
            if (DereDatabaseService.theInstance.musicInfoIDToStatistic[musicInfo.id]?.containsKey(
                    TW5Difficulty.MasterPlus
                ) == false
            )
                return false
        }
        if (shouldHaveGrand) {
            if (DereDatabaseService.theInstance.musicInfoIDToStatistic[musicInfo.id]?.containsKey(
                    TW5Difficulty.Piano
                ) == false
            )
                return false
        }
        if (shouldHaveSmart) {
            if (DereDatabaseService.theInstance.musicInfoIDToStatistic[musicInfo.id]?.containsKey(
                    TW5Difficulty.Light
                ) == false
            )
                return false
        }

        if (shouldHaveWitch) {
            if (DereDatabaseService.theInstance.musicInfoIDToStatistic[musicInfo.id]?.containsKey(
                    TW5Difficulty.Witch
                ) == false
            )
                return false
        }
        return true
    }

    var shouldHaveWitch: Boolean = false
    var shouldBeStarred: Boolean = false
    var shouldHaveGrand: Boolean = false
    var shouldHaveSmart: Boolean = false
    val permittedType: MutableSet<CircleType> = HashSet()
    fun addFilter(vararg types: CircleType): SongFilter {
        permittedType.clear()
        permittedType.addAll(types)
        return this
    }

    var shouldHaveMasterPlus: Boolean = false
}
