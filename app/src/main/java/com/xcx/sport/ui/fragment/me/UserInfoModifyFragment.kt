package com.xcx.sport.ui.fragment.me

import androidx.fragment.app.activityViewModels
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.ToastUtils
import com.github.gzuliyujiang.wheelpicker.AddressPicker
import com.github.gzuliyujiang.wheelpicker.OptionPicker
import com.github.gzuliyujiang.wheelpicker.annotation.AddressMode
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.kunminx.architecture.ui.state.State
import com.xcx.sport.App
import com.xcx.sport.BR
import com.xcx.sport.R
import com.xcx.sport.ui.BaseFragment
import com.xcx.sport.ui.bind.ClickProxy
import com.xcx.sport.viewmodels.StateHolder
import com.xcx.sport.viewmodels.UserViewModel

class UserInfoModifyFragment : BaseFragment() {
    private val states by viewModels<States>()
    private val clickProxy by lazy { ClickProxy() }

    //    private val requester by viewModels<LoginRequester>()
    private val userViewModel by activityViewModels<UserViewModel>()

    override fun onInitData() {
        arguments?.let {
            val data = it.getSerializable("data") as com.xcx.sport.data.entity.UserInfo
            states.age.set(data.age.toString())
            states.city.set(data.city)
            states.sex.set(data.sex)
            states.height.set(data.height.toString())
            states.weight.set(data.weight.toString())
        }
    }

    override fun onOutput() {
//        requester.output(this) {
//            ToastUtils.showShort("保存成功")
//            nav().popBackStack()
//        }
    }

    override fun onInput() {
        clickProxy.setOnClickListener { v ->
            when (v.id) {
                R.id.ll_city -> {
                    AddressPicker(requireActivity()).apply {
                        setAddressMode(AddressMode.PROVINCE_CITY)
                        setOnAddressPickedListener { province, city, _ ->
                            states.city.set(province.name + "," + city.name)
                        }
                    }.show()
                }
                R.id.ll_sex -> {
                    OptionPicker(requireActivity()).apply {
                        setData(states.sexList)
                        setTitle("性别")
                        setOnOptionPickedListener { _, item ->
                            states.sex.set(item as String)
                        }
                    }.show()
                }
                else -> {//保存
                    userViewModel.update(
                        com.xcx.sport.data.entity.UserInfo(
                            App.sUsername,
                            states.age.get()!!.toInt(),
                            states.height.get()!!.toDouble(),
                            states.weight.get()!!.toDouble(),
                            states.sex.get()!!,
                            states.city.get()!!,
                        )
                    ) {
                        if (it) {
                            ToastUtils.showShort(getString(R.string.save_success))
                        } else {
                            ToastUtils.showShort(getString(R.string.save_fail))
                        }
                    }
//                    requester.input(
//                        LoginIntent.UpdateUserInfo(
//                            UserInfo(
//                                App.sUsername,
//                                states.sex.get()!!,
//                                states.city.get()!!,
//                                states.age.get()!!.toInt(),
//                                states.height.get()!!.toDouble(),
//                                states.weight.get()!!.toDouble()
//                            )
//                        )
//                    )
                }
            }
        }
    }


    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_user_info_modify, BR.state, states)
            .addBindingParam(BR.click, clickProxy)
    }

    class States : StateHolder() {
        val sex = State("")
        val city = State("")
        val age = State("0")
        val height = State("0")
        val weight = State("0")
        val sexList = listOf("man", "women")
    }
}