package com.kyhsgeekcode.dereinfo

import com.kyhsgeekcode.dereinfo.calc.CGCalcService
import com.kyhsgeekcode.dereinfo.dao.SkillLifeValueDao
import com.kyhsgeekcode.dereinfo.dao.SkillLifeValueGrandDao
import com.kyhsgeekcode.dereinfo.dao.SkillMotifValueDao
import com.kyhsgeekcode.dereinfo.dao.SkillMotifValueGrandDao
import com.kyhsgeekcode.dereinfo.model.DereDatabaseService
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import java.io.File
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DereDatabaseModule {
    @Singleton
    @Provides
    fun provideDereDatabaseService(
        filesDir: File,
        skillLifeValueDao: SkillLifeValueDao,
        skillLifeValueGrandDao: SkillLifeValueGrandDao,
        skillMotifValueDao: SkillMotifValueDao,
        skillMotifValueGrandDao: SkillMotifValueGrandDao,
    ): DereDatabaseService {
        return DereDatabaseService(
            filesDir,
            skillMotifValueDao = skillMotifValueDao,
            skillLifeValueDao = skillLifeValueDao,
            skillMotifValueGrandDao = skillMotifValueGrandDao,
            skillLifeValueGrandDao = skillLifeValueGrandDao
        )
    }

    @Singleton
    @Provides
    fun provideCgCalcService(dereDatabaseService: DereDatabaseService): CGCalcService {
        return CGCalcService(dereDatabaseService)
    }
}