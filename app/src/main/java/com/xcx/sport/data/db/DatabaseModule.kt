package com.xcx.sport.data.db

import android.content.Context
import androidx.room.Room
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

//依赖注入用的，具体要看官网的Hilt
@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {
    @Provides
    @Singleton
    fun providesDiaryDatabase(
        @ApplicationContext context: Context,
    ): Database = Room.databaseBuilder(
        context,
        Database::class.java,
        "sport-database"
    ).build()
}