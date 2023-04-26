package com.xcx.sport.data.repo

import com.xcx.sport.App
import com.xcx.sport.data.db.SportDao
import com.xcx.sport.data.entity.SportInfo
import com.xcx.sport.data.network.RetrofitNetwork
import javax.inject.Inject

/**
 * 存储库类负责以下任务：
 *  向应用的其余部分公开数据。
 *  集中处理数据变化。
 *  解决多个数据源之间的冲突。
 *  对应用其余部分的数据源进行抽象化处理。
 *  包含业务逻辑。
 * */
class SportRepo @Inject constructor(
    private val network: RetrofitNetwork, private val dao: SportDao,
) {

    suspend fun saveSportHistory(sportInfo: SportInfo) = dao.save(sportInfo)
    suspend fun delete(sportInfo: SportInfo) = dao.delete(sportInfo)

    fun getMyList() = dao.get(App.sUsername)

    //        network.saveSportHistory(sportInfo).catch { Response(false, it.message ?: "", -1) }.single()
//         flow { emit(network.saveSportHistory(sportInfo)) }.flowOn(Dispatchers.IO).catch {
//            emit(
//                Response(false, it.message ?: "")
//            )
//        }.single()


//    suspend fun getList(): List<SportInfo> {
//        return flow { emit(network.getSportHistoryList(App.sUsername)) }.catch {
//            emit(Response(false, "", emptyList()))
//        }.single().data!!
//    }
//
//    suspend fun getCommunityList(): List<SportInfo> {
//        return flow { emit(network.getCommunity()) }.catch {
//            emit(Response(false, "", emptyList()))
//        }.single().data!!
//    }
//
//    suspend fun upload(sportHistoryId: Int, file: File) {
//        val builder = MultipartBody.Builder().setType(MultipartBody.FORM) //表单类型
//        val requestFile: RequestBody = RequestBody.create("image/*".toMediaTypeOrNull(), file)
//        builder.addFormDataPart("file", file.name, requestFile)
//        val map = HashMap<String, RequestBody>()
//        val r = RequestBody.create(
//            "text/plain".toMediaTypeOrNull(),
//            sportHistoryId.toString()
//        )
//        map["id"] = r
//        network.upload(map, builder.build().part(0))
//    }
//
}