package com.kyhsgeekcode.dereinfo

import androidx.room.Database
import androidx.room.RoomDatabase
import com.kyhsgeekcode.dereinfo.cardunit.CardData
import com.kyhsgeekcode.dereinfo.dao.*

@Database(
    entities = [CardData::class],
    version = 1,
    exportSchema = false
)
abstract class AppDatabase : RoomDatabase() {
    abstract fun cardDataDao(): CardDataDao
    abstract fun skillDataDao(): SkillDataDao
    abstract fun leaderSkillDataDao(): LeaderSkillDataDao
    abstract fun skillBoostTypeDao(): SkillBoostTypeDao
    abstract fun skillMotifValueDao(): SkillMotifValueDao
    abstract fun skillMotifValueGrandDao(): SkillMotifValueGrandDao
    abstract fun skillLifeValueDao(): SkillLifeValueDao
    abstract fun skillLifeValueGrandDao(): SkillLifeValueGrandDao
}