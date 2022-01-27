package com.example.toyproject.ui.main.tableFragment

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.TableService
import com.example.toyproject.network.dto.table.AddCustomLectureById
import com.example.toyproject.network.dto.table.CustomLecture
import com.example.toyproject.network.dto.table.ServerLectureList
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.HttpException
import javax.inject.Inject

@HiltViewModel
class TableAddLectureServerViewModel @Inject constructor(
    private val service: TableService
) : ViewModel() {

    lateinit var errorMessage : String
    private val _serverLectureGetFlow = MutableSharedFlow<ServerLectureList?>()
    val serverLectureGetFlow = _serverLectureGetFlow.asSharedFlow()

    private val _addServerLectureFlow = MutableSharedFlow<CustomLecture?>()
    val addServerLectureFlow = _addServerLectureFlow.asSharedFlow()


    fun loadServerLecture(offset : Int=20, limit : Int=20, subject_name : String?=null,
                          professor : String?=null, subject_code : String? =null, location : String? = null,
                          department : String? = null, grade : Int? = null,
                          credit : Int? = null, category : String? = null) {
        viewModelScope.launch {
            try {
                _serverLectureGetFlow.emit(service.loadServerLectures(offset, limit, subject_name, professor, subject_code, location, department,
                    grade, credit, category))
            }catch (e : HttpException) {
                errorMessage = e.message()
                _serverLectureGetFlow.emit(null)
            }
        }
    }

    fun addLectureById(id : Int, memo : String="") {
        viewModelScope.launch {
            try {
                _addServerLectureFlow.emit(service.addCustomLectureServer(AddCustomLectureById(id, memo)))
            } catch (e : HttpException) {
                errorMessage = e.message()
                _addServerLectureFlow.emit(null)
            }
        }
    }
}