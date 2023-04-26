package com.xcx.sport.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import com.amap.api.location.AMapLocationClient
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.MapView
import com.amap.api.maps.model.PolylineOptions
import com.xcx.sport.App
import com.xcx.sport.BR
import com.xcx.sport.R
import com.xcx.sport.data.TAG
import com.xcx.sport.data.entity.SportInfo
import com.xcx.sport.databinding.FragmentSportHistoryDetailBinding
import com.xcx.sport.viewmodels.StateHolder
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

@AndroidEntryPoint
class SportHistoryDetailFragment : Fragment() {
    private val states by viewModels<States>()
    private lateinit var mMapView: MapView
    private lateinit var binding: FragmentSportHistoryDetailBinding

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(
            layoutInflater,
            R.layout.fragment_sport_history_detail,
            container,
            false
        )
        onInitData()
        onOutput()
        onInput()
        return binding.root
    }

    private fun onInitData() {
        arguments?.let {
            val parcelable: SportInfo = it.getParcelable("data")!!
            states.sportInfo = parcelable
            mMapView = binding.root.findViewById(R.id.map)
            if (parcelable.title.isEmpty()) {//如果有标题就是计划，没有就是跑步的记录
                val list = states.sportInfo.line
                Log.i(TAG, "onInitData_list.size: ${list.size}")
                AMapLocationClient.updatePrivacyShow(requireContext(), true, true)
                AMapLocationClient.updatePrivacyAgree(requireContext(), true)
                if (list.isNotEmpty())
                    mMapView.map.let { a ->
                        //将地图移动到定位点
                        a.animateCamera(CameraUpdateFactory.changeLatLng(list[0]))
                        //设置缩放级别
                        a.moveCamera(CameraUpdateFactory.zoomTo(18f))
                        a.moveCamera(CameraUpdateFactory.changeTilt(0f))
                        val polylineOptions = PolylineOptions()
                        polylineOptions.color(requireContext().getColor(R.color.purple_700))
                        polylineOptions.width(20f)
                        polylineOptions.useGradient(true)
                        polylineOptions.points = list
                        a.addPolyline(
                            polylineOptions
                        )
                    }
            } else {
                binding.map.visibility = View.GONE
            }
            binding.setVariable(BR.m, states.sportInfo)
        }
    }

    private fun onOutput() {
    }

    private fun onInput() {
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mMapView.onCreate(savedInstanceState)
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        mMapView.onSaveInstanceState(outState)
    }

    override fun onResume() {
        super.onResume()
        mMapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        mMapView.onPause()
    }

    override fun onDestroyView() {
        CoroutineScope(Dispatchers.Default).launch {
            mMapView.onDestroy()
        }
        super.onDestroyView()
    }


    class States : StateHolder() {
        val username = App.sUsername
        lateinit var sportInfo: SportInfo
    }
}