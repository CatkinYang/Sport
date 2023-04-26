package com.xcx.sport.ui.bind

import android.view.View


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
