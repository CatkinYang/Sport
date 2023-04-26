package com.xcx.sport.ui.bind

import android.view.View
import androidx.databinding.BindingAdapter
import android.widget.TextView

//用于给控件设置点击事件
class ClickProxy : View.OnClickListener {

    var listener: View.OnClickListener? = null

    fun setOnClickListener(listener: View.OnClickListener?) {
        this.listener = listener
    }

    override fun onClick(view: View) {
        listener!!.onClick(view)
    }
}
