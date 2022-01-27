package com.example.toyproject.ui.main.tableFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.TableService
import com.example.toyproject.network.dto.table.*
import com.google.api.Http
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class TableFragmentViewModel@Inject constructor(
    private val service: TableService
) : ViewModel() {
    lateinit var errorMessage : String

    private val _defaultScheduleGetFlow = MutableSharedFlow<DefaultSchedule?>()
    val defaultScheduleGetFlow = _defaultScheduleGetFlow.asSharedFlow()

    private val _defaultScheduleLecturesFlow = MutableSharedFlow<DefaultScheduleLectureList?>()
    val defaultScheduleLecturesFlow = _defaultScheduleLecturesFlow.asSharedFlow()

    private val _scheduleCreateFlow = MutableSharedFlow<DefaultSchedule?>()
    val scheduleCreateFlow = _scheduleCreateFlow.asSharedFlow()

    private val _scheduleDeleteFlow = MutableSharedFlow<Boolean>()
    val scheduleDeleteFlow = _scheduleDeleteFlow.asSharedFlow()

    private val _editLectureMemoNick = MutableSharedFlow<CustomLecture?>()
    val editLectureMemoNick = _editLectureMemoNick.asSharedFlow()

    fun loadDefaultSchedule() {
        viewModelScope.launch {
            try {
                _defaultScheduleGetFlow.emit(service.getDefaultSchedule())
            } catch(e : HttpException) {
                _defaultScheduleGetFlow.emit(null)
            }
        }
    }

    fun createSchedule(param : ScheduleCreate) {
        viewModelScope.launch {
            try {
                _scheduleCreateFlow.emit(service.createSchedule(param))
            } catch(e : HttpException) {
                if(e.code()==409) {
                    errorMessage = e.message()
                    _scheduleCreateFlow.emit(null)
                }
            }
        }
    }

    fun deleteLecture(id : Int) {
        viewModelScope.launch {
            try {
                service.deleteCustomLectureFromDefault(id)
                _scheduleDeleteFlow.emit(true)
            } catch (e : HttpException) {
                errorMessage = e.message()
                _scheduleDeleteFlow.emit(false)
            }
        }
    }

    fun loadDefaultScheduleLectures() {
        viewModelScope.launch {
            try {
                _defaultScheduleLecturesFlow.emit(service.getDefaultScheduleLectures())
            }catch (e : HttpException) {
                errorMessage = e.message()
                _defaultScheduleLecturesFlow.emit(null)
            }
        }
    }

    fun editMemo(param : EditCustomLecture, id : Int) {
        viewModelScope.launch {
            try {
                _editLectureMemoNick.emit(service.editLectureMemoNick(param, id))
            } catch (e : HttpException) {
                errorMessage = e.message()
                _editLectureMemoNick.emit(null)
            }
        }
    }

    fun loadLecturesById(scheduleId : Int) {
        viewModelScope.launch {
            try {
                _defaultScheduleLecturesFlow.emit(service.loadLecturesById(scheduleId))
            }catch (e : HttpException) {
                errorMessage = e.message()
                _defaultScheduleLecturesFlow.emit(null)
            }

        }
    }
}