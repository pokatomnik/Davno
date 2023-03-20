package com.github.pokatomnik.davno.services.path

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class PathModule {
    @Singleton
    @Provides
    fun providePath(): Path = Path()
}