package com.xcx.sport.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.xcx.sport.data.entity.SportInfo
import com.xcx.sport.data.entity.UserInfo

@Database(entities = [UserInfo::class, SportInfo::class], version = 1, exportSchema = false)
@TypeConverters(SportConverter::class)//一些原来Room不支持的类型需要自定义转换器
abstract class Database : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun sportDao(): SportDao

}
