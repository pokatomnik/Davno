package com.github.pokatomnik.davno.services.storage

import com.github.pokatomnik.davno.services.path.Path
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class WebdavStorageModule {
    @Singleton
    @Provides
    fun provideWebDavStorage(path: Path) = WebdavStorage(path)
}