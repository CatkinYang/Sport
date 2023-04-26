package com.xcx.sport.data.dto

data class Response<T>(
    val success: Boolean,
    val message: String,
    val data: T?
) {
    constructor(success: Boolean, message: String) : this(success, message, null)
}