package com.xcx.sport.data.db

import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
//依赖注入用的，具体要看官网的Hilt
@Module
@InstallIn(SingletonComponent::class)
object DaoModule {

    @Provides
    fun providesUserDao(
        database: Database,
    ): UserDao = database.userDao()

    @Provides
    fun providesSportDao(
        database: Database,
    ): SportDao = database.sportDao()
}