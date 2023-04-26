package com.xcx.sport.data.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity("userinfo")
data class UserInfo(
    @PrimaryKey
    val email: String,
    val age: Int,
    val height: Double,
    val weight: Double,
    val sex: String,
    val city: String,
):java.io.Serializable