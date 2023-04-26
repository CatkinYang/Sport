package com.xcx.sport

import android.annotation.SuppressLint
import android.app.Application
import android.os.Build.VERSION.SDK_INT
import coil.ImageLoader
import coil.ImageLoaderFactory
import coil.decode.GifDecoder
import coil.decode.ImageDecoderDecoder
import com.drake.brv.utils.BRV
import com.tencent.mmkv.MMKV
import com.xcx.sport.data.USERNAME
import dagger.hilt.android.HiltAndroidApp

/**
 * AndroidManifest.xml 需要修改里面的高德key为你自己的，需要修改google-services.json文件为你自己的
 * MMKV 是基于 mmap 内存映射的 key-value 组件 https://github.com/Tencent/MMKV/blob/master/README_CN.md
 * Retrofit有留着但没用到
 * Hilt依赖注入框架 https://developer.android.google.cn/training/dependency-injection/hilt-android?hl=zh-cn
 * Room数据库框架
 * DataBinding和ViewBinding
 * */
@HiltAndroidApp//用于标识应用程序的入口点，并为应用程序提供Hilt框架的依赖注入功能
class App : Application(), ImageLoaderFactory {
    companion object {
        @SuppressLint("StaticFieldLeak")
        var sApplication: Application? = null
        var isConnect = true
        var sUsername = ""
    }

    override fun onCreate() {
        super.onCreate()
        sApplication = this
        MMKV.initialize(this)//MMKV初始化
        sUsername = MMKV.defaultMMKV().decodeString(USERNAME, "")!!//获取保存username
        BRV.modelId = BR.m//BRV的DataBinding初始化
    }

    override fun newImageLoader(): ImageLoader {//Coil的ImageLoader重写，用来加载动态图
        return ImageLoader.Builder(this).crossfade(true).components {
            if (SDK_INT >= 28) {
                add(ImageDecoderDecoder.Factory())
            } else {
                add(GifDecoder.Factory())
            }
        }.build()
    }

}