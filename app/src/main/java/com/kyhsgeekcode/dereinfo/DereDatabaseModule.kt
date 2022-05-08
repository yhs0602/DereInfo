package com.kyhsgeekcode.dereinfo

import com.kyhsgeekcode.dereinfo.model.DereDatabaseService
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class DereDatabaseModule {
    @Singleton
    @Binds
    abstract fun bindDereDatabaseService(): DereDatabaseService
}