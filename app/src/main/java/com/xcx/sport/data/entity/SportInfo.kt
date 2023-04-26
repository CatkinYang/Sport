package com.xcx.sport.data.entity

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.amap.api.maps.model.LatLng
import kotlinx.parcelize.Parcelize

@Parcelize
@Entity("sport_info")
data class SportInfo(
    val time: String,//时间
    val distance: Double,//距离（公里）
    val speed: String,//配速
    val date: String,//日期
    val content: String,//内容
    val username: String,
    val title: String = "",//标题
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val line: List<LatLng> = listOf(),//线路
) : Parcelable


