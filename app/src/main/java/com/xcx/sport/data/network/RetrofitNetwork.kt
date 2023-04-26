package com.xcx.sport.data.network

import com.xcx.sport.data.dto.LoginDTO
import com.xcx.sport.data.dto.Response
import com.xcx.sport.data.entity.SportInfo
import com.xcx.sport.data.entity.UserInfo
import kotlinx.coroutines.flow.flow
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory
import retrofit2.http.*
import java.util.concurrent.TimeUnit
import javax.inject.Singleton

/**
 * 没用到，但是我保留了下来，提供给你们使用
 * */


//模拟器Wifi中的网关地址
const val BaseUrl = "http://10.0.2.2:8080"
//const val BaseUrl = "http://192.168.110.33:8080"

private interface RetrofitNetworkApi {
    ///////////////////////////////////////////////////////////////////////////
// 用户
///////////////////////////////////////////////////////////////////////////
    @POST(value = "login")
    suspend fun login(@Body postBody: LoginDTO): Response<String>

    @GET("userinfo/{username}")
    suspend fun userinfo(@Path("username") username: String): Response<UserInfo>

    @POST("userinfo")
    suspend fun updateUserinfo(@Body userInfo: UserInfo): Response<String>

    @POST("register")
    suspend fun register(@Body loginDTO: LoginDTO): Response<String>


    ///////////////////////////////////////////////////////////////////////////
// 运动信息
///////////////////////////////////////////////////////////////////////////
    @POST(value = "sport/save")
    suspend fun saveSportHistory(@Body sportInfo: SportInfo): Response<Int>

    @GET(value = "sport/history/{username}")
    suspend fun getSportHistoryList(@Path("username") username: String): Response<List<SportInfo>>

    @GET(value = "community")
    suspend fun getCommunity(): Response<List<SportInfo>>


//    @POST("upload")
//    @Multipart
//    suspend fun upload(
//        @PartMap map: Map<String, @JvmSuppressWildcards RequestBody>,
//        @Part img: MultipartBody.Part,
//    ): Response<List<Message>>

}


@Singleton
class RetrofitNetwork {
    private val networkApi = Retrofit.Builder().baseUrl(BaseUrl).client(
        OkHttpClient.Builder().connectTimeout(20, TimeUnit.SECONDS)
            .readTimeout(20, TimeUnit.SECONDS).writeTimeout(20, TimeUnit.SECONDS)
//            .connectionSpecs(Collections.singletonList(spec))
            .addInterceptor(HttpLoggingInterceptor().apply {
                setLevel(HttpLoggingInterceptor.Level.BODY)
            }).build()
    ).addConverterFactory(GsonConverterFactory.create()).build()
        .create(RetrofitNetworkApi::class.java)

    suspend fun login(loginDTO: LoginDTO) = networkApi.login(loginDTO)
    suspend fun userinfo(username: String) = networkApi.userinfo(username)
    suspend fun updateUserinfo(userInfo: UserInfo) = networkApi.updateUserinfo(userInfo)
    suspend fun register(loginDTO: LoginDTO) = networkApi.register(loginDTO)


    suspend fun saveSportHistory(sportInfo: SportInfo) = flow {
        val saveSportHistory = networkApi.saveSportHistory(sportInfo)
        emit(saveSportHistory)
    }

    suspend fun getSportHistoryList(username: String) = networkApi.getSportHistoryList(username)
    suspend fun getCommunity() = networkApi.getCommunity()

//    suspend fun upload(
//        map: Map<String, RequestBody>,
//        img: MultipartBody.Part,
//    ) = networkApi.upload(map, img)

}