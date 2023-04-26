package com.xcx.sport.data.db

import androidx.room.TypeConverter
import com.amap.api.maps.model.LatLng
import com.blankj.utilcode.util.GsonUtils
import com.google.gson.reflect.TypeToken

/**
 * 保存经纬度
 * 把对象转成json保存
 * */
class SportConverter {
    @TypeConverter
    fun fromJson(json: String): List<LatLng> {
        return GsonUtils.fromJson(json, object : TypeToken<List<LatLng>>() {}.type)
    }

    @TypeConverter
    fun dataToJson(list: List<LatLng>): String {
        return GsonUtils.toJson(list)
    }

}