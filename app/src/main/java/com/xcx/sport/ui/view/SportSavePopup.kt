package com.xcx.sport.ui.view

import android.content.Context
import com.lxj.xpopup.core.CenterPopupView
import com.xcx.sport.R
import com.xcx.sport.databinding.PopupSportSaveBinding


class SportSavePopup(context: Context) : CenterPopupView(context) {
    private lateinit var save: (String) -> Unit
    private lateinit var bind: PopupSportSaveBinding//通过ViewBinding 绑定视图
    override fun getImplLayoutId(): Int {
        return R.layout.popup_sport_save
    }

    override fun onCreate() {
        super.onCreate()
        bind = PopupSportSaveBinding.bind(popupImplView)
        bind.btnCancel.setOnClickListener { dismiss() }
        bind.btnConfirm.setOnClickListener {
            save(bind.edtContent.text.toString())
            dismiss()
        }
    }


    fun setOnConfirm(onClickListener: (String) -> Unit): SportSavePopup {
        save = onClickListener
        return this
    }


}