package com.xcx.sport.ui.fragment

import android.util.Log
import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ToastUtils
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.kunminx.architecture.ui.state.State
import com.tencent.mmkv.MMKV
import com.xcx.sport.App
import com.xcx.sport.BR
import com.xcx.sport.R
import com.xcx.sport.data.TAG
import com.xcx.sport.data.USERNAME
import com.xcx.sport.ui.BaseFragment
import com.xcx.sport.ui.bind.ClickProxy
import com.xcx.sport.viewmodels.StateHolder
import com.xcx.sport.viewmodels.UserViewModel

//继承了BaseFragment
class LoginFragment : BaseFragment() {
    //val 为不可变常量，var 为变量
    private val states by viewModels<LoginStates>()//by 委托生成实体类
    private val clickProxy by lazy { ClickProxy() }//by lazy 延迟初始化
    private lateinit var auth: FirebaseAuth//lateinit var 延迟实例化

    //by 委托生成实体类 activityViewModels不同于viewModels，它的作用域限定为activity，也就是在同个activity下的fragment可以用到同个实例
    private val viewModel by activityViewModels<UserViewModel>()

    override fun onInitData() {
        super.onInitData()
        auth = Firebase.auth
    }

    override fun onOutput() {
    }

    private fun signIn(email: String, password: String) {
        states.show.set(true)
        auth.signInWithEmailAndPassword(email, password)//使用了firebase-auth进行登录,密码最少6位
            .addOnCompleteListener(requireActivity()) { task ->
                states.show.set(false)
                if (task.isSuccessful) {
                    // Sign in success, update UI with the signed-in user's information
                    val user = auth.currentUser
                    Log.i(TAG, "signIn: $user")
                    val email1 = states.name.get()!!
                    App.sUsername = email1
                    MMKV.defaultMMKV().encode(USERNAME, email)//MMKV
                    viewModel.addInfo(email1)
                    nav().popBackStack()//尝试弹出控制器的后退堆栈
                    ToastUtils.showShort("登录成功")
                } else {
                    // If sign in fails, display a message to the user.
                    ToastUtils.showShort("用户名或密码错误")
                }
            }
    }


    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(
            R.layout.fragment_login,
            BR.state,
            states
        )//DataBinding 数据绑定，绑定数据到XML中
            .addBindingParam(BR.click, clickProxy)
    }


    override fun onInput() {
        //点击事件，通过控件id区分
        clickProxy.setOnClickListener { v ->
            when (v.id) {
                R.id.tv_register -> nav().navigate(R.id.action_loginFragment_to_registerFragment)//跳转到registerFragment页面
                R.id.btn_login -> {
                    if (states.name.get()!!.isEmpty() && states.password.get()!!//判断用户名和密码是否为空
                            .isEmpty()
                    ) return@setOnClickListener
                    signIn(states.name.get()!!, states.password.get()!!)
                }
            }
        }
    }

    //StateHolder用来保存界面数据
    class LoginStates : StateHolder() {
        val name = State("")//State可以实现响应式
        val password = State("")
        val show = State(false)//是否显示加载条
    }
}
