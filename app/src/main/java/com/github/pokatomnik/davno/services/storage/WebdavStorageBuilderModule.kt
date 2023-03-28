package com.github.pokatomnik.davno.services.storage

import com.github.pokatomnik.davno.services.logger.DavnoLogger
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WebdavStorageBuilderModule {
    @Singleton
    @Provides
    fun provideWebdavStorageBuilder(logger: DavnoLogger): WebdavStorageBuilder {
        return WebdavStorageBuilder(logger)
    }
}