package com.kyhsgeekcode.dereinfo

import android.content.Context
import androidx.room.Room
import com.kyhsgeekcode.dereinfo.dao.*
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
object DBModule {
    @Singleton
    @Provides
    fun provideDatabase(
        @ApplicationContext context: Context,
        dbFile: File
    ): DeresuteDatabase = Room
        .databaseBuilder(context, DeresuteDatabase::class.java, dbFile.canonicalPath)
        .createFromFile(dbFile)
        .build()

    @Singleton
    @Provides
    fun provideCardDataDao(deresuteDatabase: DeresuteDatabase): CardDataDao = deresuteDatabase.cardDataDao()

    @Singleton
    @Provides
    fun provideSkillDataDao(deresuteDatabase: DeresuteDatabase): SkillDataDao = deresuteDatabase.skillDataDao()

    @Singleton
    @Provides
    fun provideLeaderSkillDataDao(deresuteDatabase: DeresuteDatabase): LeaderSkillDataDao =
        deresuteDatabase.leaderSkillDataDao()

    @Singleton
    @Provides
    fun provideSkillBoostTypeDao(deresuteDatabase: DeresuteDatabase): SkillBoostTypeDao =
        deresuteDatabase.skillBoostTypeDao()

    @Singleton
    @Provides
    fun provideSkillMotifValueDao(deresuteDatabase: DeresuteDatabase): SkillMotifValueDao =
        deresuteDatabase.skillMotifValueDao()

    @Singleton
    @Provides
    fun provideSkillMotifValueGrandDao(deresuteDatabase: DeresuteDatabase): SkillMotifValueGrandDao =
        deresuteDatabase.skillMotifValueGrandDao()

    @Singleton
    @Provides
    fun provideSkillLifeValueDao(deresuteDatabase: DeresuteDatabase): SkillLifeValueDao =
        deresuteDatabase.skillLifeValueDao()

    @Singleton
    @Provides
    fun provideSkillLifeValueGrandDao(deresuteDatabase: DeresuteDatabase): SkillLifeValueGrandDao =
        deresuteDatabase.skillLifeValueGrandDao()
}