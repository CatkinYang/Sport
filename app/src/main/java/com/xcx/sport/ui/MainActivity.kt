package com.xcx.sport.ui

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.blankj.utilcode.util.NetworkUtils
import com.blankj.utilcode.util.ToastUtils
import com.hjq.permissions.Permission
import com.xcx.sport.App
import com.xcx.sport.R
import com.xcx.sport.data.TAG
import com.xcx.sport.databinding.ActivityMainBinding
import com.xcx.sport.utils.PermissionsUtils
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    private lateinit var navController: NavController
    private lateinit var appBarConfiguration: AppBarConfiguration

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        val binding = ActivityMainBinding.inflate(layoutInflater)
        App.isConnect = NetworkUtils.isConnected()
        setContentView(binding.root)
        setSupportActionBar(binding.appBarMain.toolbar)//设置标题栏
        //权限
        PermissionsUtils.openPermissions(this,
//            Permission.MANAGE_EXTERNAL_STORAGE,
            Permission.ACCESS_FINE_LOCATION,
            Permission.ACCESS_COARSE_LOCATION,
            onGranted = { _, _ ->

            })
        //网络状态
        NetworkUtils.registerNetworkStatusChangedListener(object :
            NetworkUtils.OnNetworkStatusChangedListener {
            override fun onDisconnected() {
                ToastUtils.showShort(getString(R.string.disconnect))
                App.isConnect = false
            }

            override fun onConnected(networkType: NetworkUtils.NetworkType) {
                App.isConnect = networkType != NetworkUtils.NetworkType.NETWORK_NO
                ToastUtils.showShort(getString(R.string.connect))
            }
        })
        //Navigation设置
        navController = findNavController(R.id.nav_host_fragment_content_main)
        //监听当前页面
        navController.addOnDestinationChangedListener {
                _: NavController,
                destination: NavDestination,
                _: Bundle?,
            ->
            val id = destination.id
            Log.i(TAG, "addOnDestinationChangedListener: ${destination.label}")
            if (id == R.id.loginFragment) {//如果是登录页面就隐藏标题栏
                binding.appBarMain.toolbar.visibility = View.GONE
            } else {
                binding.appBarMain.toolbar.visibility = View.VISIBLE
            }
        }
        //使用 navController 和  顶级目标设置 ActionBar
        appBarConfiguration = AppBarConfiguration(
            setOf(R.id.sportFragment, R.id.communityFragment, R.id.meFragment, R.id.loginFragment),
            drawerLayout = binding.drawerLayout
        )
        //设置标题栏随导航的页面改变
        setupActionBarWithNavController(navController, appBarConfiguration)
        //侧边栏选择时跳转需要用的导航控制器
        binding.navView.setupWithNavController(navController)
    }


    override fun onSupportNavigateUp(): Boolean {
        return navController.navigateUp(appBarConfiguration)   //返回
    }
}