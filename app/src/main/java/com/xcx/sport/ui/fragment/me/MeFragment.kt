package com.xcx.sport.ui.fragment.me

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.viewModels
import com.blankj.utilcode.util.TimeUtils
import com.blankj.utilcode.util.ToastUtils
import com.kunminx.architecture.ui.page.DataBindingConfig
import com.kunminx.architecture.ui.state.State
import com.lxj.xpopup.XPopup
import com.tencent.mmkv.MMKV
import com.xcx.sport.App
import com.xcx.sport.BR
import com.xcx.sport.R
import com.xcx.sport.data.TAG
import com.xcx.sport.data.USERNAME
import com.xcx.sport.data.entity.SportInfo
import com.xcx.sport.data.entity.UserInfo
import com.xcx.sport.ui.BaseFragment
import com.xcx.sport.ui.adapter.SportHistoryAdapter
import com.xcx.sport.ui.bind.ClickProxy
import com.xcx.sport.ui.view.TaskPopup
import com.xcx.sport.viewmodels.SportViewModel
import com.xcx.sport.viewmodels.StateHolder
import com.xcx.sport.viewmodels.UserViewModel
import java.util.*

class MeFragment : BaseFragment() {
    private val meStates by viewModels<MeStates>()
    private val clickProxy by lazy { ClickProxy() }
    private val adapter by lazy { SportHistoryAdapter(emptyList()) }
    private val userViewModel by viewModels<UserViewModel>()
    private val sportViewModel by viewModels<SportViewModel>()


    override fun onOutput() {
        //当数据库里的数据发生变化，会重新发送值
        sportViewModel.getMyList().observe(viewLifecycleOwner) {
            Log.i(TAG, "getMyList: ")
            //为空显示空的图片
            val empty = it.isEmpty()
            meStates.empty.set(empty)
            if (!empty) {
                adapter.models = it
                val mapValues = it.filter { a -> a.title.isEmpty() }.groupBy { a -> a.date }
                    .mapValues { b -> b.value.sumOf { c -> c.distance } }
                meStates.map.set(mapValues)
                Log.i(TAG, "onOutput: ${mapValues.values}")
            } else {
                adapter.models = emptyList()
                meStates.map.set(emptyMap())
            }
        }
        userViewModel.getInfo().observe(viewLifecycleOwner) {
            meStates.userInfo.set(it)
        }
    }

    override fun onInput() {
        //没登录
        if (App.sUsername.isEmpty()) {
            nav().navigate(R.id.action_meFragment_to_loginFragment)
            Log.i(TAG, "MeFragment App.sUsername.isEmpty")
            return
        }
        adapter.onClick(R.id.item_sport_history, R.id.img_delete) {
            if (it == R.id.item_sport_history) {
                val bundle = Bundle()
                bundle.putParcelable("data", getModel())//跳转到详情页并附带数据
                nav().navigate(R.id.action_meFragment_to_sportHistoryDetailFragment, bundle)
            } else {
                sportViewModel.delete(getModel()) { b ->
                    if (!b) {//如果删除不成功
                        ToastUtils.showShort(getString(R.string.delete_fail))
                    }
                }

            }
        }
        clickProxy.setOnClickListener {
            when (it.id) {
                R.id.btn_modify -> {
                    val bundle = Bundle()
                    bundle.putSerializable("data", meStates.userInfo.get())
                    nav().navigate(R.id.action_meFragment_to_userInfoModifyFragment, bundle)
                }
                R.id.floatingActionButton -> {
                    val popup = XPopup.Builder(requireContext())//新增Task的弹窗
                        .asCustom(TaskPopup(requireContext()).setOnConfirm { t, c ->
                            val sportInfo = SportInfo(
                                "",
                                0.0,
                                "",
                                TimeUtils.date2String(Date(), "yyyy/MM/dd"),
                                c,
                                username = App.sUsername,
                                t
                            )
                            sportViewModel.saveSportHistory(sportInfo) { b ->
                                if (b) {//保存成功时添加到适配器并刷新列表
                                    adapter.mutable.add(0, sportInfo)
                                    adapter.notifyDataSetChanged()
                                    ToastUtils.showShort(getString(R.string.save_success))
                                } else {
                                    ToastUtils.showShort(getString(R.string.save_fail))
                                }
                            }

                        })
                    popup.show()
                }
                else -> {//退出登录
                    App.sUsername = ""
                    MMKV.defaultMMKV().remove(USERNAME)
                    nav().navigate(R.id.action_meFragment_to_loginFragment)
                }
            }
        }
    }


    override fun getDataBindingConfig(): DataBindingConfig {
        return DataBindingConfig(R.layout.fragment_me, BR.state, meStates).addBindingParam(
            BR.click, clickProxy
        ).addBindingParam(BR.adapter, adapter)
    }


    class MeStates : StateHolder() {
        val userInfo = State(UserInfo("", 0, 0.0, 0.0, "", ""))
        val empty = State(false)
        val map = State(emptyMap<String, Double>())
    }

}