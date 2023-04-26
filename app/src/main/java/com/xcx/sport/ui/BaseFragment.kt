package com.xcx.sport.ui

import android.os.Bundle
import android.view.View
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.kunminx.architecture.ui.page.DataBindingFragment
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
abstract class BaseFragment : DataBindingFragment() {

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        onInitData()
        onOutput()
        onInput()
    }

    override fun initViewModel() {
    }

    protected open fun onInitData() {}
    protected abstract fun onOutput()
    protected abstract fun onInput()


    protected fun nav(): NavController {
        return NavHostFragment.findNavController(this)
    }

}