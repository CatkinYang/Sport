package com.xcx.sport.ui.fragment

import android.os.Bundle
import android.util.Log
import android.view.View
import android.view.ViewGroup
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.maps.AMap
import com.amap.api.maps.CameraUpdateFactory
import com.amap.api.maps.model.CameraPosition
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MarkerOptions
import com.amap.api.services.core.AMapException
import com.amap.api.services.core.LatLonPoint
import com.amap.api.services.core.PoiItem
import com.amap.api.services.poisearch.PoiResult
import com.amap.api.services.poisearch.PoiSearch
import com.xcx.sport.databinding.FragmentCommunityBinding
import com.xcx.sport.ui.BaseViewBindingFragment
import dagger.hilt.android.AndroidEntryPoint

/**
 * 教程
 * */
@AndroidEntryPoint
class CommunityFragment : BaseViewBindingFragment<FragmentCommunityBinding>() {
    override fun viewBinding(container: ViewGroup?): FragmentCommunityBinding {
        return FragmentCommunityBinding.inflate(layoutInflater)
    }

    private val aMap: AMap
        get() {
            return b.mapView.map
        }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        b.mapView.onCreate(savedInstanceState)
        val locationClient = AMapLocationClient(requireContext())
        locationClient.setLocationListener { location ->
            val cameraPosition = CameraPosition.Builder()
                .target(LatLng(location.latitude, location.longitude))
                .zoom(15f)
                .build()

            aMap.moveCamera(CameraUpdateFactory.newCameraPosition(cameraPosition))
            query(location)
        }

        val locationOption = AMapLocationClientOption()
        locationOption.locationMode = AMapLocationClientOption.AMapLocationMode.Hight_Accuracy
        locationOption.isOnceLocation = true
        locationClient.setLocationOption(locationOption)
        locationClient.startLocation()
//

    }

    override fun onResume() {
        super.onResume()
        b.mapView.onResume()
    }

    override fun onPause() {
        super.onPause()
        b.mapView.onPause()
    }

    override fun onDestroyView() {
        b.mapView.onDestroy()
        super.onDestroyView()
    }

    private fun query(location: AMapLocation) {
        // TODO: 搜索关键词
        val query = PoiSearch.Query("运动场馆", "", "")
        val poiSearch = PoiSearch(requireContext(), query)
        poiSearch.bound =
            PoiSearch.SearchBound(LatLonPoint(location.latitude, location.longitude), 50000)//单位米
        poiSearch.setOnPoiSearchListener(object : PoiSearch.OnPoiSearchListener {
            override fun onPoiSearched(result: PoiResult?, errorCode: Int) {
                if (errorCode == AMapException.CODE_AMAP_SUCCESS) {
                    Log.i("TAG", "onPoiSearched: ${result?.pois?.size}")
                    result?.pois?.forEach { poi ->
                        val markerOptions = MarkerOptions()
                            .position(LatLng(poi.latLonPoint.latitude, poi.latLonPoint.longitude))
                            .title(poi.title)
                            .snippet(poi.snippet)
                        aMap.addMarker(markerOptions)
                    }
                }
            }

            override fun onPoiItemSearched(result: PoiItem?, errorCode: Int) {}
        })

        poiSearch.searchPOIAsyn()
    }
}