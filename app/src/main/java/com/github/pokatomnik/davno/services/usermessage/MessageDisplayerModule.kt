package com.github.pokatomnik.davno.services.usermessage

import android.content.Context
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@InstallIn(SingletonComponent::class)
@Module
class MessageDisplayerModule {
    @Singleton
    @Provides
    fun provideMessageDisplayer(@ApplicationContext context: Context): MessageDisplayer {
        return ToastDisplayer(context)
    }
}