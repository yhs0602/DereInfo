package com.kyhsgeekcode.dereinfo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kyhsgeekcode.dereinfo.cardunit.*
import com.kyhsgeekcode.dereinfo.dao.*
import com.kyhsgeekcode.dereinfo.model.LiveDataModel
import com.kyhsgeekcode.dereinfo.model.MusicDataModel

@Database(
    entities = [
        CardDataModel::class,
        SkillModel::class,
        LeaderSkillModel::class,
        SkillBoostModel::class,
        SkillLifeValueModel::class,
        SkillLifeValueGrandModel::class,
        SkillMotifValueModel::class,
        SkillMotifValueGrandModel::class,
        LiveDataModel::class,
        MusicDataModel::class
    ],
    version = 1,
    exportSchema = false
)
abstract class DeresuteDatabase : RoomDatabase() {
    abstract fun cardDataDao(): CardDataDao
    abstract fun skillDataDao(): SkillDataDao
    abstract fun leaderSkillDataDao(): LeaderSkillDataDao
    abstract fun skillBoostTypeDao(): SkillBoostTypeDao
    abstract fun skillMotifValueDao(): SkillMotifValueDao
    abstract fun skillMotifValueGrandDao(): SkillMotifValueGrandDao
    abstract fun skillLifeValueDao(): SkillLifeValueDao
    abstract fun skillLifeValueGrandDao(): SkillLifeValueGrandDao
    abstract fun musicDataDao(): MusicDataDao
    abstract fun liveDataDao(): LiveDataDao
}