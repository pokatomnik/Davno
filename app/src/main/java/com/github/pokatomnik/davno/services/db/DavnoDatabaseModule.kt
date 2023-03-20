package com.github.pokatomnik.davno.services.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class DavnoDatabaseModule {
    @Singleton
    @Provides
    fun provideDavnoDatabase(@ApplicationContext context: Context): DavnoDatabase {
        return Room
            .databaseBuilder(context, DavnoDatabase::class.java, "davno.db")
            .fallbackToDestructiveMigration()
            .build()
    }
}