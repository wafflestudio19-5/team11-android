package com.example.toyproject.ui.main.tableFragment

import android.view.View
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.TableService
import com.example.toyproject.network.dto.table.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TableAddLectureDefaultViewModel@Inject constructor(
    private val service : TableService,
    private val retrofit: Retrofit
)   : ViewModel(){
    lateinit var errorMessage : String

    private val _defaultScheduleGetFlow = MutableSharedFlow<CustomLecture?>()
    val defaultScheduleGetFlow = _defaultScheduleGetFlow.asSharedFlow()


    fun addUserMadeLecture(param : UserMadeLectureAdd) {
        viewModelScope.launch {
            try {
                _defaultScheduleGetFlow.emit(service.addCustomLectureToDefault(param))
            } catch (e : HttpException) {
                _defaultScheduleGetFlow.emit(null)
                errorMessage = e.message()
            }

        }
    }

    fun editLectureInDefault(custom_id : Int, newName : String, memo : String, timeLocation :List<TimeLocationSend>) {
        viewModelScope.launch {
            try {
                _defaultScheduleGetFlow.emit(service.editCustomLectureFromDefault(EditCustomLecture(newName, memo, timeLocation), custom_id))
            } catch(e : HttpException) {
                errorMessage = e.message()
                _defaultScheduleGetFlow.emit(null)
            }
        }
    }
}