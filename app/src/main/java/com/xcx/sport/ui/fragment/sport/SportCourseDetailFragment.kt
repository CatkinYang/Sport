package com.xcx.sport.ui.fragment.sport

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import com.just.agentweb.AgentWeb
import com.xcx.sport.R
import com.xcx.sport.data.TAG
import com.xcx.sport.data.model.SportCourse
import com.xcx.sport.databinding.FragmentSportCourseDetailBinding
import com.xcx.sport.ui.BaseFragment
import com.xcx.sport.ui.BaseViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint

class SportCourseDetailFragment : BaseViewBindingFragment<FragmentSportCourseDetailBinding>() {
    override fun viewBinding(container: ViewGroup?): FragmentSportCourseDetailBinding {
        return FragmentSportCourseDetailBinding.inflate(layoutInflater)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (arguments != null) {
            val serializable = requireArguments().getSerializable("data") as SportCourse
            AgentWeb.with(this)
                .setAgentWebParent(b.llCourseDetail, LinearLayout.LayoutParams(-1, -1))
                .useDefaultIndicator().createAgentWeb().ready()
                .go(serializable.url)
        }
    }

}