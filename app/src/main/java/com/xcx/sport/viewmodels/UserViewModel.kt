package com.xcx.sport.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xcx.sport.App
import com.xcx.sport.data.db.UserDao
import com.xcx.sport.data.entity.UserInfo
import com.xcx.sport.data.repo.LoginRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel//constructor() 构造函数，因为添加了注入注解，所以会自动实例化
class UserViewModel @Inject constructor(private val loginRepo: LoginRepo) : ViewModel() {

    fun addInfo(email: String) {
        viewModelScope.launch {//协程执行耗时操作，viewModelScope：CoroutineScope 绑定到此 ViewModel，清除视图模型时，将取消
            loginRepo.addInfo(UserInfo(email, 0, 0.0, 0.0, "男", ""))
        }
    }


    //result括号里面的是返回的参数
    fun update(userInfo: UserInfo, result: (Boolean) -> Unit) {
        viewModelScope.launch {
            result(loginRepo.updateInfo(userInfo) > 0)//大于0时为更新成功
        }
    }

    fun getInfo() = loginRepo.getInfo()//获取用户信息
}