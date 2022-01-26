package com.example.toyproject.ui.main.tableFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.TableService
import com.example.toyproject.network.dto.table.CustomLecture
import com.example.toyproject.network.dto.table.Schedule
import com.example.toyproject.network.dto.table.ScheduleCreate
import com.google.api.Http
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class TableMakeViewModel@Inject constructor(
    private val service : TableService,
    private val retrofit: Retrofit
)  : ViewModel(){
    lateinit var errorMessage : String

    private val _scheduleMakeFlow = MutableSharedFlow<Schedule?>()
    val scheduleMakeFlow = _scheduleMakeFlow.asSharedFlow()

    fun makeSchedule(name : String, year : Int, season : Int) {
        viewModelScope.launch {
            try {
                _scheduleMakeFlow.emit(service.makeSchedule(ScheduleCreate(name, year, season)))
            } catch (e : HttpException) {
                errorMessage = e.message()
                _scheduleMakeFlow.emit(null)
            }
        }
    }

}