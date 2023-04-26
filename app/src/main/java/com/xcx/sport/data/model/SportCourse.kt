package com.xcx.sport.data.model

import java.io.Serializable

data class SportCourse(
    val name: String,
    val img: String,
    val url: String,
    val type: Int,
) : Serializable