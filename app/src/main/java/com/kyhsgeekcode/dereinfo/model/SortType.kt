package com.kyhsgeekcode.dereinfo.model

enum class SortType(val value: Int) {
    Data(0),
    Alphabetical(1),
    TotalNote(2),
    LongRatio(3),
    FlickRatio(4),
    SlideRatio(5),
    Long7(6),
    Long9(7),
    Long11(8),
    Flick7(9),
    Flick9(10),
    Flick11(11),
    Slide7(12),
    Slide9(13),
    Slide11(14),
    Total7(15),
    Total9(16),
    Total11(17)
    ;

    fun condition(musicInfo: MusicInfo, difficulty: TW5Difficulty): Any {
        val statistic =
            DereDatabaseHelper.theInstance.musicInfoIDToStatistic[musicInfo.id]?.get(difficulty)
        return when (this) {
            Data -> musicInfo.id
            Alphabetical -> if (musicInfo.nameKana.isBlank()) musicInfo.nameKana else musicInfo.name
            TotalNote -> statistic?.get(StatisticIndex.Total) ?: 0.0f
            LongRatio -> statistic?.get(StatisticIndex.Long) ?: 0.0f
            FlickRatio -> statistic?.get(StatisticIndex.Flick) ?:0.0f
            SlideRatio -> statistic?.get(StatisticIndex.Slide) ?:0.0f
            Long7 -> statistic?.get(StatisticIndex.Long7) ?: 0.0f
            Long9 -> statistic?.get(StatisticIndex.Long9) ?: 0.0f
            Long11 -> statistic?.get(StatisticIndex.Long11) ?: 0.0f
            Flick7 -> statistic?.get(StatisticIndex.Flick7) ?: 0.0f
            Flick9 -> statistic?.get(StatisticIndex.Flick9) ?: 0.0f
            Flick11 -> statistic?.get(StatisticIndex.Flick11) ?: 0.0f
            Slide7 -> statistic?.get(StatisticIndex.Slide7) ?: 0.0f
            Slide9 -> statistic?.get(StatisticIndex.Slide9) ?: 0.0f
            Slide11 -> statistic?.get(StatisticIndex.Slide11) ?: 0.0f
            Total7 -> statistic?.get(StatisticIndex.Total7) ?: 0.0f
            Total9 -> statistic?.get(StatisticIndex.Total9) ?: 0.0f
            Total11 -> statistic?.get(StatisticIndex.Total11) ?: 0.0f

        }
    }

    fun hasStatisticCondition(): Boolean = when (this) {
        Data -> false
        Alphabetical -> false
        TotalNote -> true
        LongRatio -> true
        FlickRatio -> true
        SlideRatio -> true
        else -> true
    }

    fun getStatisticIndex(): StatisticIndex = when (this) {
        Data -> StatisticIndex.Total
        Alphabetical -> StatisticIndex.Total
        TotalNote -> StatisticIndex.Total
        LongRatio -> StatisticIndex.Long
        FlickRatio -> StatisticIndex.Flick
        SlideRatio -> StatisticIndex.Slide
        Long7 -> StatisticIndex.Long7
        Long9 -> StatisticIndex.Long9
        Long11 -> StatisticIndex.Long11
        Flick7 -> StatisticIndex.Flick7
        Flick9 -> StatisticIndex.Flick9
        Flick11 -> StatisticIndex.Flick11
        Slide7 -> StatisticIndex.Slide7
        Slide9 -> StatisticIndex.Slide9
        Slide11 -> StatisticIndex.Slide11
        Total7 -> StatisticIndex.Total7
        Total9 -> StatisticIndex.Total9
        Total11 -> StatisticIndex.Total11
    }

    companion object {
        private val values = values();
        fun getByValue(value: Int) = values.firstOrNull { it.value == value } ?: Data
    }
}
