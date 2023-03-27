package com.github.pokatomnik.davno.services.clipboard

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DavnoClipboardsModule {
    @Singleton
    @Provides
    fun provideClipboards(): Clipboards {
        return Clipboards()
    }
}