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
import com.xcx.sport.BR
import com.xcx.sport.R
import com.xcx.sport.data.TAG
import com.xcx.sport.ui.BaseFragment
import com.xcx.sport.ui.bind.ClickProxy
import com.xcx.sport.viewmodels.StateHolder
import com.xcx.sport.viewmodels.UserViewModel

class RegisterFragment : BaseFragment() {
    private val states by viewModels<RegisterStates>()
    private val viewModel by activityViewModels<UserViewModel>()
    private val clickProxy by lazy { ClickProxy() }
    private lateinit var auth: FirebaseAuth
    override fun onInitData() {
        super.onInitData()
        auth = Firebase.auth
    }

    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(
            R.layout.fragment_register, BR.state, states
        ).addBindingParam(BR.click, clickProxy)
    }


    override fun onOutput() {
    }


    override fun onInput() {
        clickProxy.setOnClickListener { v ->
            when (v.id) {
                R.id.tv_login -> nav().popBackStack()
                R.id.btn_register -> {
                    val email = states.name.get()!!
                    if (email.isEmpty()) {
                        return@setOnClickListener
                    }
                    val password = states.password.get()
                    val password2 = states.password2.get()
                    if (password == password2) {
                        auth.createUserWithEmailAndPassword(
                            email,
                            password!!
                        )//使用了firebase-auth进行注册,密码最少6位
                            .addOnCompleteListener(requireActivity()) { task ->
                                Log.i(TAG, "onInput: ${task.exception}")
                                if (task.isSuccessful) {
                                    viewModel.addInfo(email)
                                    nav().popBackStack()
                                    ToastUtils.showShort("注册成功！")
                                } else {
                                    ToastUtils.showShort(
                                        task.exception?.message ?: "注册失败"
                                    )//三目运算符如果前面为空就显示 注册失败
                                }
                            }
                    } else {
                        ToastUtils.showShort("两次密码不一致")
                    }
                }
            }
        }
    }


    class RegisterStates : StateHolder() {
        val name = State("")
        val password = State("")
        val password2 = State("")
    }

}