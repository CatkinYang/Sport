package com.xcx.sport.ui

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.viewbinding.ViewBinding
import dagger.hilt.android.AndroidEntryPoint

/**
 * 一个ViewBinding的Fragment抽象类
 * */
abstract class BaseViewBindingFragment<T : ViewBinding> : Fragment() {

    private var _b: T? = null
    val b: T
        get() = _b!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View? {
        _b = viewBinding(container)
        return _b?.root
    }

    protected abstract fun viewBinding(container: ViewGroup?): T

    override fun onDestroyView() {
        super.onDestroyView()
        _b = null
    }


}