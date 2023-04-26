package com.xcx.sport.ui.view

import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import com.xcx.sport.R
import com.xcx.sport.databinding.PopupSportSaveBinding
import com.xcx.sport.databinding.PopupSportTaskBinding


class TaskPopup(context: Context) : CenterPopupView(context) {
    private lateinit var save: (title: String, content: String) -> Unit
    private lateinit var bind: PopupSportTaskBinding
    override fun getImplLayoutId(): Int {
        return R.layout.popup_sport_task
    }

    override fun onCreate() {
        super.onCreate()
        bind = PopupSportTaskBinding.bind(popupImplView)
        bind.btnCancel.setOnClickListener { dismiss() }
        bind.btnConfirm.setOnClickListener {
            val toString = bind.edtTitle.text.toString()
            val toString1 = bind.edtContent.text.toString()
            if (toString.isEmpty() || toString1.isEmpty()) {
                return@setOnClickListener
            }
            save(toString, toString1)
            dismiss()
        }
    }


    fun setOnConfirm(onClickListener: (title: String, content: String) -> Unit): TaskPopup {
        save = onClickListener
        return this
    }


}