package com.xcx.sport.data.repo

import com.xcx.sport.App
import com.xcx.sport.data.db.UserDao
import com.xcx.sport.data.entity.UserInfo
import com.xcx.sport.data.network.RetrofitNetwork
import javax.inject.Inject

/**
 * 存储库类负责以下任务：
 *  向应用的其余部分公开数据。
 *  集中处理数据变化。
 *  解决多个数据源之间的冲突。
 *  对应用其余部分的数据源进行抽象化处理。
 *  包含业务逻辑。
 * */
class LoginRepo @Inject constructor(
    private val network: RetrofitNetwork, private val userDao: UserDao,
) {

    suspend fun addInfo(userInfo: UserInfo) = userDao.addInfo(userInfo)

    suspend fun updateInfo(userInfo: UserInfo) =
        userDao.updateInfo(userInfo)

    fun getInfo() = userDao.getInfo(App.sUsername)

}