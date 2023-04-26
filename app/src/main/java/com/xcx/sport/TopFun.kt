package com.xcx.sport

import com.xcx.sport.data.dto.Response
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.flowOn
import kotlinx.coroutines.flow.single

/**
 * 复用的一个函数，对数据进行获取和错误捕获，最后返回一个Response
 * 这里需要了解Flow的作用
 * */
suspend fun <T> flowSingle(emitValue: Response<T>): Response<T> {
    return flow { emit(emitValue) }//发送一个值
        .flowOn(Dispatchers.IO).catch { emit(Response(false, it.message ?: "")) }//捕获错误，并发送一个值
        .single()//获取值
}