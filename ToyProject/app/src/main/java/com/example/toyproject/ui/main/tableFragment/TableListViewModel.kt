package com.example.toyproject.ui.main.tableFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.TableService
import com.example.toyproject.network.dto.table.DefaultSchedule
import com.example.toyproject.network.dto.table.Schedule
import com.example.toyproject.network.dto.table.ScheduleListGet
import com.example.toyproject.network.dto.table.Semester
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class TableListViewModel @Inject constructor(
    private val service : TableService,
    private val retrofit: Retrofit
) : ViewModel() {

    lateinit var errorMessage : String


    private val _semesterList = MutableLiveData<MutableList<Schedule>>()
    val semesterList : LiveData<MutableList<Schedule>> = _semesterList
    fun loadScheduleList() {
        viewModelScope.launch {
            _semesterList.value = service.getSchedules().results.toMutableList()
        }
    }



/*
    private val _scheduleListFlow = MutableSharedFlow<ScheduleListGet?>()
    val scheduleListFlow = _scheduleListFlow.asSharedFlow()

    fun loadScheduleList() {
        viewModelScope.launch {
            try {
                val response = service.getSchedules()
                _scheduleListFlow.emit(response)
            }catch(e : HttpException) {
                errorMessage = e.message()
                _scheduleListFlow.emit(null)
            }
        }
    }

 */

}