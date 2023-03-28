package com.github.pokatomnik.davno.services.logger

import com.github.pokatomnik.davno.services.db.DavnoDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class LoggerModule {
    @Singleton
    @Provides
    fun provideLogger(
        davnoDatabase: DavnoDatabase
    ): DavnoLogger {
        return DavnoLogger(
            logsDAO = davnoDatabase.logsDAO()
        )
    }
}