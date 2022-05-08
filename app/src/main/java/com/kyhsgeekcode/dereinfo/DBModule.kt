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
    ): AppDatabase = Room
        .databaseBuilder(context, AppDatabase::class.java, dbFile.canonicalPath)
        .createFromFile(dbFile)
        .build()

    @Singleton
    @Provides
    fun provideCardDataDao(appDatabase: AppDatabase): CardDataDao = appDatabase.cardDataDao()

    @Singleton
    @Provides
    fun provideSkillDataDao(appDatabase: AppDatabase): SkillDataDao = appDatabase.skillDataDao()

    @Singleton
    @Provides
    fun provideLeaderSkillDataDao(appDatabase: AppDatabase): LeaderSkillDataDao =
        appDatabase.leaderSkillDataDao()

    @Singleton
    @Provides
    fun provideSkillBoostTypeDao(appDatabase: AppDatabase): SkillBoostTypeDao =
        appDatabase.skillBoostTypeDao()

    @Singleton
    @Provides
    fun provideSkillMotifValueDao(appDatabase: AppDatabase): SkillMotifValueDao =
        appDatabase.skillMotifValueDao()

    @Singleton
    @Provides
    fun provideSkillMotifValueGrandDao(appDatabase: AppDatabase): SkillMotifValueGrandDao =
        appDatabase.skillMotifValueGrandDao()

    @Singleton
    @Provides
    fun provideSkillLifeValueDao(appDatabase: AppDatabase): SkillLifeValueDao =
        appDatabase.skillLifeValueDao()

    @Singleton
    @Provides
    fun provideSkillLifeValueGrandDao(appDatabase: AppDatabase): SkillLifeValueGrandDao =
        appDatabase.skillLifeValueGrandDao()
}