package com.xcx.sport.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import androidx.room.Insert
import com.xcx.sport.App
import com.xcx.sport.data.entity.SportInfo
import com.xcx.sport.data.repo.SportRepo
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SportViewModel @Inject constructor(private val repo: SportRepo) : ViewModel() {


    fun saveSportHistory(sportInfo: SportInfo, result: (Boolean) -> Unit) {
        viewModelScope.launch {
            result(repo.saveSportHistory(sportInfo) > 0)
        }
    }


    //        network.saveSportHistory(sportInfo).catch { Response(false, it.message ?: "", -1) }.single()
//         flow { emit(network.saveSportHistory(sportInfo)) }.flowOn(Dispatchers.IO).catch {
//            emit(
//                Response(false, it.message ?: "")
//            )
//        }.single()

    fun delete(sportInfo: SportInfo, result: (Boolean) -> Unit) {
        viewModelScope.launch {
            result(repo.delete(sportInfo) > 0)
        }
    }

    fun getMyList() = repo.getMyList()

}