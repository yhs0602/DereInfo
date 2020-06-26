package com.kyhsgeekcode.dereinfo.model

//Todo: support legacy (101) properly
enum class TW5Difficulty(val value: Int) {
    Debut(1),
    Regular(2),
    Pro(3),
    Master(4),
    MasterPlus(5),
    Light(11),
    Trick(12),
    Piano(21),
    Forte(22);

    companion object {
        val TAG = "TW5Difficulty"
        fun fromString(value: String): TW5Difficulty = when (value.toLowerCase()) {
            "debut" -> Debut
            "regular" -> Regular
            "pro" -> Pro
            "master" -> Master
            "master+" -> MasterPlus
            "trick" -> Trick
            "light" -> Light
            "piano" -> Piano
            "forte" -> Forte
            "legacy" -> MasterPlus
            else -> Debut
        }

        fun valueOf(value: Int): TW5Difficulty {
            //Log.d(TAG, "valueOf Int:${value}")
            return when (value) {
                1 -> Debut
                2 -> Regular
                3 -> Pro
                4 -> Master
                5 -> MasterPlus
                101 -> MasterPlus
                11 -> Light
                12 -> Trick
                21 -> Piano
                22 -> Forte
                else -> Debut
            }
        }

        fun fromIndex(value: Int): TW5Difficulty {
            return when (value) {
                0->Debut
                1->Regular
                2->Pro
                3->Master
                4->MasterPlus
                5->Light
                6->Trick
                7->Piano
                8->Forte
                else -> throw IllegalArgumentException()
            }
        }
    }
}
