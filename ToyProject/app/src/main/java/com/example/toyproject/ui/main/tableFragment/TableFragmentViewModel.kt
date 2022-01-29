package com.example.toyproject.ui.main.tableFragment

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.TableService
import com.example.toyproject.network.dto.table.*
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class TableFragmentViewModel@Inject constructor(
    private val service: TableService
) : ViewModel() {
    lateinit var errorMessage : String

    private val _defaultScheduleGetFlow = MutableSharedFlow<DefaultSchedule?>()
    val defaultScheduleGetFlow = _defaultScheduleGetFlow.asSharedFlow()

    private val _defaultScheduleLectures = MutableLiveData<DefaultScheduleLectureList>()
    val defaultScheduleLectures : LiveData<DefaultScheduleLectureList> = _defaultScheduleLectures

//    private val _defaultScheduleLecturesFlow = MutableSharedFlow<DefaultScheduleLectureList?>()
//    val defaultScheduleLecturesFlow = _defaultScheduleLecturesFlow.asSharedFlow()

    private val _scheduleCreateFlow = MutableSharedFlow<DefaultSchedule?>()
    val scheduleCreateFlow = _scheduleCreateFlow.asSharedFlow()

    private val _lectureDeleteFlow = MutableSharedFlow<Boolean>()
    val lectureDeleteFlow = _lectureDeleteFlow.asSharedFlow()

    private val _editLectureMemoNick = MutableSharedFlow<CustomLecture?>()
    val editLectureMemoNick = _editLectureMemoNick.asSharedFlow()

    private val _deleteScheduleFlow = MutableSharedFlow<Boolean>()
    val deleteScheduleFlow = _deleteScheduleFlow.asSharedFlow()

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

    fun deleteScheduleById(id : Int) {
        viewModelScope.launch {
            try {
                service.deleteScheduleById(id)
                _deleteScheduleFlow.emit(true)
            } catch(e : HttpException) {
                errorMessage = e.message()
                _deleteScheduleFlow.emit(false)
            }
        }
    }
    fun deleteScheduleDefault() {
        viewModelScope.launch {
            try {
                service.deleteScheduleDefault()
                _deleteScheduleFlow.emit(true)
            } catch(e : HttpException) {
                errorMessage = e.message()
                _deleteScheduleFlow.emit(false)
            }
        }
    }


    fun deleteLecture(id : Int) {
        viewModelScope.launch {
            try {
                service.deleteCustomLectureFromDefault(id)
                _lectureDeleteFlow.emit(true)
            } catch (e : HttpException) {
                errorMessage = e.message()
                _lectureDeleteFlow.emit(false)
            }
        }
    }

    fun loadDefaultScheduleLectures() {
        viewModelScope.launch {
            try {
                _defaultScheduleLectures.value = service.getDefaultScheduleLectures()
                // _defaultScheduleLecturesFlow.emit(service.getDefaultScheduleLectures())
            }catch (e : HttpException) {
                errorMessage = e.message()
                // _defaultScheduleLecturesFlow.emit(null)
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
                _defaultScheduleLectures.value = service.loadLecturesById(scheduleId)
                //_defaultScheduleLecturesFlow.emit(service.loadLecturesById(scheduleId))
            }catch (e : HttpException) {
                errorMessage = e.message()
                // _defaultScheduleLecturesFlow.emit(null)
            }

        }
    }
}