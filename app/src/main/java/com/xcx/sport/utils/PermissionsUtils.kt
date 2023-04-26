package com.xcx.sport.utils

import android.content.Context
import android.location.LocationManager
import android.widget.Toast
import com.hjq.permissions.OnPermissionCallback
import com.hjq.permissions.XXPermissions
import com.xcx.sport.R


object PermissionsUtils {

    /**
     * request for access
     */
    fun openPermissions(
        context: Context,
        vararg permissions: String,
        onGranted: ((permissions: List<String>, all: Boolean) -> Unit)? = null,
        callback: OnPermissionCallback? = null,
    ) {
        XXPermissions.with(context)
            .permission(permissions)
            .request(
                callback ?: object : OnPermissionCallback {
                    override fun onGranted(permissions: List<String>, all: Boolean) {
                        onGranted?.invoke(permissions, all)
                    }

                    override fun onDenied(permissions: List<String>, never: Boolean) {
                        if (never) {
                            Toast.makeText(
                                context,
                                R.string.permissions_on_denied,
                                Toast.LENGTH_SHORT
                            ).show()
                            // 如果是被永久拒绝就跳转到应用权限系统设置页面
                            XXPermissions.startPermissionActivity(context, permissions)
                        } else {
                            Toast.makeText(
                                context,
                                R.string.permissions_on_denied,
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            )
    }

    //是否有开启定位
    fun isLocServiceEnable(context: Context): Boolean {
        val locationManager = context.getSystemService(Context.LOCATION_SERVICE) as LocationManager
        return locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)
    }
}
