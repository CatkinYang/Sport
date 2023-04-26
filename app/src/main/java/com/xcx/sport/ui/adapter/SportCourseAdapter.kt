package com.xcx.sport.ui.adapter

import com.drake.brv.BindingAdapter
import com.xcx.sport.R
import com.xcx.sport.data.model.SportCourse

//RecyclerView的DataBinding适配器，通过第三方库BRV实现
class SportCourseAdapter(list: List<SportCourse>) : BindingAdapter() {

    init {
        models = list
        addType<SportCourse>(R.layout.rv_item_sport_course)//添加item的类型，要对应xml里绑定的类型
    }
}