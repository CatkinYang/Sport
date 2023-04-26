package com.xcx.sport.data.db

import androidx.lifecycle.LiveData
import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import com.xcx.sport.data.entity.SportInfo

@Dao
interface SportDao {
    @Insert
    suspend fun save(sportInfo: SportInfo): Long

    @Delete
    suspend fun delete(sportInfo: SportInfo): Int

    @Query("select * from sport_info where username=:email order by date desc")
    fun get(email: String): LiveData<List<SportInfo>>//这种方式当数值改变时会重新通知观察者


}