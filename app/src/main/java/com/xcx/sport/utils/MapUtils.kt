package com.xcx.sport.utils

import android.graphics.Bitmap
import android.graphics.Canvas
import android.view.View
import android.view.ViewGroup
import com.amap.api.maps.MapView

object MapUtils {
    /**
     * 组装地图截图和其他View截图，需要注意的是目前提供的方法限定为MapView与其他View在同一个ViewGroup下
     * @param    bitmap             地图截图回调返回的结果
     * @param   viewContainer      MapView和其他要截图的View所在的父容器ViewGroup
     * @param   mapView            MapView控件
     * @param   views              其他想要在截图中显示的控件
     */
    fun getMapAndViewScreenShot(
        bitmap: Bitmap,
        viewContainer: ViewGroup,
        mapView: MapView,
        vararg views: View
    ): Bitmap {
        val width = viewContainer.width
        val height = viewContainer.height
        val screenBitmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(screenBitmap)
        canvas.drawBitmap(bitmap, mapView.left.toFloat(), mapView.top.toFloat(), null)
        for (view in views) {
            view.isDrawingCacheEnabled = true
            canvas.drawBitmap(view.drawingCache, view.left.toFloat(), view.top.toFloat(), null)
        }
        return screenBitmap
    }
}
