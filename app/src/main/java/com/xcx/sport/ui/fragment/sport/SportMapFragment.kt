package com.xcx.sport.ui.fragment.sport

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Color
import android.hardware.Sensor
import android.hardware.SensorEvent
import android.hardware.SensorEventListener
import android.hardware.SensorManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.NavHostFragment
import com.amap.api.location.AMapLocation
import com.amap.api.location.AMapLocationClient
import com.amap.api.location.AMapLocationClientOption
import com.amap.api.location.AMapLocationListener
import com.amap.api.maps.*
import com.amap.api.maps.LocationSource.OnLocationChangedListener
import com.amap.api.maps.model.LatLng
import com.amap.api.maps.model.MyLocationStyle
import com.amap.api.maps.model.PolylineOptions
import com.blankj.utilcode.util.*
import com.lxj.xpopup.XPopup
import com.xcx.sport.App
import com.xcx.sport.R
import com.xcx.sport.data.TAG
import com.xcx.sport.data.entity.SportInfo
import com.xcx.sport.databinding.FragmentSportMapBinding
import com.xcx.sport.ui.view.SportSavePopup
import com.xcx.sport.utils.PathSmoothTool
import com.xcx.sport.utils.PermissionsUtils
import com.xcx.sport.viewmodels.SportViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.DecimalFormat
import java.util.*
import kotlin.math.sqrt


@AndroidEntryPoint
class SportMapFragment : Fragment(), AMapLocationListener, LocationSource,
    AMap.OnMapScreenShotListener, SensorEventListener {
    private var _binding: FragmentSportMapBinding? = null
    private val binding
        get() = _binding!!

    //    private val sportHistoryRequester by viewModels<SportHistoryRequester>()
    private val sportViewModel by viewModels<SportViewModel>()

    private lateinit var mMapView: MapView
    private var aMap: AMap? = null

    //定位需要的声明
    private var mLocationClient: AMapLocationClient? = null //定位发起端
    private lateinit var mLocationOption: AMapLocationClientOption //定位参数
    private var mListener: OnLocationChangedListener? = null //定位监听器

    //标识，用于判断是否只显示一次定位信息和用户重新定位
    private var isFirstLoc = true

    //上一次定位位置和当前定位位置，用于计算距离
    private var lastLatLng: LatLng? = null
    private lateinit var polylineOptions: PolylineOptions
    private lateinit var pathSmoothTool: PathSmoothTool

    //上一次定位位置和当前定位位置，用于计算距离
    private lateinit var nowLatLng: LatLng

    //此次运动总距离
    private var distanceThisTime = 0

    //开始时间
    private var startTime: Long = 0
    private val mHandler = Handler(Looper.getMainLooper())
    private var mRunnable: MyRunnable? = null
    private val decimalFormat: DecimalFormat = DecimalFormat("0.00") //构造方法的字符格式这里如果小数不足2位,会以0补足.

    private inner class MyRunnable : Runnable {
        override fun run() {
            //获得持续秒数
            startTime++
            //将持续秒数转化为HH:mm:ss并显示到控件
            _binding?.cmPasstime?.text = formatSeconds(startTime)
//            binding.cmPasstime.text = formatSeconds(startTime)
            mHandler.postDelayed(this, 1000)
        }
    }

    //等待两次位置变化 使位置更准确
    private var count = 0
    private var showDisKM = 0.0//公里数
    private var distribution = 0.0//配速
    private lateinit var popupView: SportSavePopup
    private lateinit var sensorManager: SensorManager
    private lateinit var accelerometer: Sensor
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        if (!PermissionsUtils.isLocServiceEnable(requireContext())) {
            ToastUtils.showShort("没有开启定位")
            NavHostFragment.findNavController(this).popBackStack()
        }
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View {
        _binding = DataBindingUtil.inflate(
            inflater, R.layout.fragment_sport_map, container, false
        )
        binding.lifecycleOwner = viewLifecycleOwner
        mMapView = binding.mapView
        binding.sportContent.keepScreenOn = true
        binding.tvFinish.setOnClickListener {
            popupView = SportSavePopup(requireContext()).setOnConfirm {
                sportViewModel.saveSportHistory(
                    SportInfo(
                        binding.cmPasstime.text.toString(),
                        showDisKM,
                        decimalFormat.format(distribution),
                        TimeUtils.date2String(Date(), "yyyy/MM/dd"),
                        it,
                        username = App.sUsername,
                        line = polylineOptions.points
                    )
                ) { b ->
                    if (b) {
                        ToastUtils.showShort(getString(R.string.save_success))
                        unBind()
                        NavHostFragment.findNavController(this).popBackStack()
                    } else {
                        ToastUtils.showShort(getString(R.string.save_fail))
                    }
                }

            }
            val popup = XPopup.Builder(requireContext()).asCustom(popupView)
            popup.show()

        }
        binding.tvPause.setOnClickListener {
            binding.tvPause.visibility = View.GONE
            binding.tvResume.visibility = View.VISIBLE
            unBind()
        }
        binding.tvResume.setOnClickListener {
            binding.tvResume.visibility = View.GONE
            binding.tvPause.visibility = View.VISIBLE
            // TODO:开始
            sensorManager.unregisterListener(this)
            startLocation()
        }
        setUpMap()
        initPolyline()
        // 注册加速度传感器
        sensorManager = requireContext().getSystemService(Context.SENSOR_SERVICE) as SensorManager
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER)
        sensorManager.registerListener(this, accelerometer, SensorManager.SENSOR_DELAY_NORMAL)
        return binding.root
    }


    override fun onSensorChanged(event: SensorEvent?) {
        // 如果手机被摇晃，则停止播放音乐并关闭服务
        val x = event!!.values[0]
        val y = event!!.values[1]
        val z = event!!.values[2]
        val sqrt = sqrt((x * x + y * y + z * z).toDouble())
        Log.i(TAG, "onSensorChanged: $sqrt")
        // TODO: 模拟器测试时数值为10
        if (sqrt > 25) {
            Log.i(TAG, "onSensorChanged:START ")
            // TODO:开始
            sensorManager.unregisterListener(this)
            startLocation()
            binding.tvResume.visibility = View.GONE
            binding.tvPause.visibility = View.VISIBLE
        }
    }

    override fun onAccuracyChanged(sensor: Sensor?, accuracy: Int) {
    }

    private fun unBind() {
        if (null != mRunnable) {
            mHandler.removeCallbacks(mRunnable!!)
            mRunnable = null
        }
        if (mLocationClient == null) return
        mLocationClient!!.stopLocation()
        mLocationClient!!.unRegisterLocationListener(this)
        mLocationClient!!.onDestroy()
        mLocationClient = null
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        if (!NetworkUtils.isConnected()) {
            ToastUtils.showShort("当前没网络，无法上传数据,请连接网络后重新进入")
            return
        }
        mMapView.onCreate(savedInstanceState)

//        setUpMap()
//        initPolyline()
//        startLocation()
    }


    private fun initPolyline() {
        polylineOptions = PolylineOptions()
        polylineOptions.color(requireContext().getColor(R.color.purple_700))
        polylineOptions.width(20f)
        polylineOptions.useGradient(true)
        pathSmoothTool = PathSmoothTool()
        pathSmoothTool.intensity = 4
    }


    override fun onLocationChanged(p0: AMapLocation?) {
        if (null == p0) {
            return
        }
        if (p0.errorCode == 0) {
            //先暂时获得经纬度信息，并将其记录在List中
            LogUtils.d(
                TAG, "纬度信息为${p0.latitude} 经度信息为${p0.longitude}"
            )
            //定位成功
            updateLocation(p0)
        } else {
            val errText = "定位失败," + p0.errorCode + ": " + p0.errorInfo
            LogUtils.e("AmapErr", errText)
        }
    }

    private fun updateLocation(aMapLocation: AMapLocation) {
        //避免前两次定位不准
        if (count > 0) {
            count -= 1
            return
        }
//        val nowSpeed: Float = aMapLocation.speed //获取速度
        //如果不是第一次定位，则把上次定位信息传给lastLatLng
        // TODO:  瞬移优化
        if (!isFirstLoc) {
//            lastLatLng = if (AMapUtils.calculateLineDistance(nowLatLng, lastLatLng).toInt() < 100) {
            lastLatLng = if (aMapLocation.accuracy <= 20) {
                nowLatLng
            } else {
                //定位出现问题，如突然瞬移，则取消此次定位修改
                Log.i(TAG, "updateLocation:此次计算距离差异过大，取消此次修改 ")
//                ToastUtils.showShort("此次计算距离差异过大，取消此次修改")
                return
            }
        }
        val latitude: Double = aMapLocation.latitude //获取纬度
        val longitude: Double = aMapLocation.longitude //获取经度
        //新位置
        nowLatLng = LatLng(latitude, longitude)
        //路径添加当前位置 如果数量有两个以上，进行轨迹平滑优化
        val points = polylineOptions.points
        if (points.size > 1) nowLatLng = pathSmoothTool.kalmanFilterPoint(points.last(), nowLatLng)
//        path.add(nowLatLng)
        polylineOptions.add(nowLatLng)
        //绘制路径
        aMap!!.addPolyline(
            polylineOptions
//            PolylineOptions().addAll(path).width(10f).color(Color.argb(255, 255, 0, 0))
        )


        //如果不是第一次定位，就计算距离
        if (!isFirstLoc) {
            val tempDistance = AMapUtils.calculateLineDistance(nowLatLng, lastLatLng).toInt()
            //计算总距离
            distanceThisTime += tempDistance
            //将总距离显示到控件
            showDisKM = distanceThisTime / 1000.0
            binding.tvMileage.text = showDisKM.toString()
            //计算配速
            // TODO: 平均
            distribution = startTime / 60 / showDisKM
//            Log.i(TAG, "nowSpeed: $nowSpeed")
//            if (nowSpeed <= 1) {
//                binding.tvSpeed.text = "--.--"
//            } else {
            binding.tvSpeed.text = decimalFormat.format(distribution)
//            }
        }
        //将地图移动到定位点
        //aMap?.animateCamera(CameraUpdateFactory.changeLatLng(nowLatLng))
        aMap?.moveCamera(CameraUpdateFactory.newLatLng(nowLatLng))
        //点击定位按钮 能够将地图的中心移动到定位点
        mListener?.onLocationChanged(aMapLocation)
        if (isFirstLoc) {
            //设置缩放级别
            aMap?.moveCamera(CameraUpdateFactory.zoomTo(18f))
            aMap?.moveCamera(CameraUpdateFactory.changeTilt(0f))
            isFirstLoc = false
        }
    }

    override fun activate(p0: OnLocationChangedListener?) {
        mListener = p0
        try {
            startLocation()
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    override fun deactivate() {
        mListener = null
        if (mLocationClient != null) {
            mLocationClient!!.stopLocation()
            mLocationClient!!.onDestroy()
        }
        mLocationClient = null
    }

    private fun setUpMap() {
        if (aMap == null) aMap = mMapView.map
        aMap?.let {
            it.setLocationSource(this) // 设置定位监听
            // 自定义系统定位小蓝点
            val myLocationStyle = MyLocationStyle()
            //        myLocationStyle.myLocationIcon(BitmapDescriptorFactory
//                .fromResource(R.drawable.mylocation_point));// 设置小蓝点的图标
            myLocationStyle.strokeColor(Color.TRANSPARENT) // 设置圆形的边框颜色
            myLocationStyle.radiusFillColor(Color.argb(0, 0, 0, 0)) // 设置圆形的填充颜色
            // myLocationStyle.anchor(int,int)//设置小蓝点的锚点
            // 设置定位的类型为定位模式 ，定位、且将视角移动到地图中心点，定位点依照设备方向旋转，并且会跟随设备移动。
            myLocationStyle.myLocationType(MyLocationStyle.LOCATION_TYPE_LOCATION_ROTATE)
            //        myLocationStyle.interval(interval);//设置发起定位请求的时间间隔
//        myLocationStyle.showMyLocation(true);//设置是否显示定位小蓝点，true 显示，false不显示
            myLocationStyle.strokeWidth(1.0f) // 设置圆形的边框粗细
            it.myLocationStyle = myLocationStyle
            it.uiSettings.isMyLocationButtonEnabled = false // 设置默认定位按钮是否显示
            it.uiSettings.isZoomControlsEnabled = false // 设置默认缩放按钮是否显示
            it.uiSettings.isCompassEnabled = false // 设置默认指南针是否显示
            it.isMyLocationEnabled = true // 设置为true表示显示定位层并可触发定位，false表示隐藏定位层并不可触发定位，默认是false
        }
    }

    private fun startLocation() {
        Log.i(TAG, "startLocation: $isFirstLoc")
//        if (mLocationClient == null) {
        if (!isFirstLoc) {
            if (mRunnable == null) {
                mRunnable = MyRunnable()
            }
            mHandler.postDelayed(mRunnable!!, 0)
        }
        AMapLocationClient.updatePrivacyShow(requireContext(), true, true)
        AMapLocationClient.updatePrivacyAgree(requireContext(), true)
        mLocationClient = AMapLocationClient(requireContext())
        //设置定位属性
        mLocationOption = AMapLocationClientOption()
        mLocationOption.locationMode =
            AMapLocationClientOption.AMapLocationMode.Hight_Accuracy //可选，设置定位模式，可选的模式有高精度、仅设备、仅网络。默认为高精度模式
        mLocationOption.isGpsFirst = false //可选，设置是否gps优先，只在高精度模式下有效。默认关闭
        mLocationOption.httpTimeOut = 30000 //可选，设置网络请求超时时间。默认为30秒。在仅设备模式下无效
        mLocationOption.interval = 2000 //可选，设置定位间隔。默认为2秒
        mLocationOption.isNeedAddress = false //可选，设置是否返回逆地理地址信息。默认是true
        mLocationOption.isOnceLocation = isFirstLoc //可选，设置是否单次定位。默认是false
        mLocationOption.isOnceLocationLatest =
            false //可选，设置是否等待wifi刷新，默认为false.如果设置为true,会自动变为单次定位，持续定位时不要使用
        AMapLocationClientOption.setLocationProtocol(AMapLocationClientOption.AMapLocationProtocol.HTTP) //可选， 设置网络请求的协议。可选HTTP或者HTTPS。默认为HTTP
        mLocationOption.isSensorEnable = true //可选，设置是否使用传感器。默认是false
        mLocationOption.isWifiScan =
            true //可选，设置是否开启wifi扫描。默认为true，如果设置为false会同时停止主动刷新，停止以后完全依赖于系统刷新，定位位置可能存在误差
        mLocationOption.isLocationCacheEnable = true //可选，设置是否使用缓存定位，默认为true
        mLocationOption.isMockEnable = false//模拟
        mLocationOption.geoLanguage =
            AMapLocationClientOption.GeoLanguage.ZH //可选，设置逆地理信息的语言，默认值为默认语言（根据所在地区选择语言）
        mLocationClient!!.setLocationOption(mLocationOption)
        // 设置定位监听
        mLocationClient!!.setLocationListener(this)
        //开始定位
        mLocationClient!!.startLocation()
//        }
    }


    private fun formatSeconds(seconds: Long): String {
        val hh = if (seconds / 3600 > 9) (seconds / 3600).toString() else "0${seconds / 3600}"
        val mm =
            if (seconds % 3600 / 60 > 9) (seconds % 3600 / 60).toString() else "0" + seconds % 3600 / 60
        val ss =
            if (seconds % 3600 % 60 > 9) (seconds % 3600 % 60).toString() else "0" + seconds % 3600 % 60
        return "$hh:$mm:$ss"
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
        binding.unbind()
        _binding = null
        sensorManager.unregisterListener(this)
    }

    override fun onDestroy() {
        super.onDestroy()
//        mMapView.onDestroy()
        if (null != mRunnable) {
            mHandler.removeCallbacks(mRunnable!!)
            mRunnable = null
        }
    }

    override fun onMapScreenShot(p0: Bitmap?) {
        CoroutineScope(Dispatchers.IO).launch {
            ImageUtils.save2Album(
                com.xcx.sport.utils.MapUtils.getMapAndViewScreenShot(
                    p0!!, binding.sportContent, mMapView, binding.llShow
                ), Bitmap.CompressFormat.JPEG
            )

        }
    }

    override fun onMapScreenShot(p0: Bitmap?, p1: Int) {
    }


}