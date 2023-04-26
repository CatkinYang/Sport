package com.xcx.sport.data.db

import androidx.lifecycle.LiveData
import androidx.room.*
import com.xcx.sport.data.entity.UserInfo

@Dao
interface UserDao {
    //通过注解执行sql操作
    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun addInfo(userInfo: UserInfo)//suspend 代表执行的函数是挂起函数，
    // 是一种特殊的函数，可以在执行过程中暂停并在稍后恢复执行。在协程中使用挂起函数可以避免阻塞线程并实现非阻塞的异步操作。

    @Update
    suspend fun updateInfo(userInfo: UserInfo): Int

    @Query("Select * from userinfo where email=:email")
    fun getInfo(email: String): LiveData<UserInfo>//这种方式当数值改变时会重新通知观察者

}