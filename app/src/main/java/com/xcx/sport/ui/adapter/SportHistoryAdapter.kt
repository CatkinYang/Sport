package com.xcx.sport.ui.adapter

import com.drake.brv.BindingAdapter
import com.xcx.sport.R
import com.xcx.sport.data.entity.SportInfo

class SportHistoryAdapter(list: List<SportInfo>) : BindingAdapter() {
    init {
        models = list
        clickThrottle = 200//点击防抖动200毫秒
        addType<SportInfo>(R.layout.rv_item_sport_history)//添加item的类型，要对应xml里绑定的类型
    }
}