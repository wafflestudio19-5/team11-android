package com.example.toyproject.ui.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.LectureInfo
import com.example.toyproject.network.LectureSearch
import com.example.toyproject.network.ReviewService
import com.example.toyproject.network.TableService
import com.example.toyproject.network.dto.table.CustomLecture
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.HttpException
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class ReviewActivityViewModel @Inject constructor(
    private val service : ReviewService,
    private val tableService : TableService
) : ViewModel() {

    lateinit var errorMessage : String

    private val _myLectureList = MutableSharedFlow<MutableList<CustomLecture>?>()
    val myLectureList = _myLectureList.asSharedFlow()

    private val _searchedLectureList = MutableLiveData<LectureSearch>()
    val searchedLectureList : LiveData<LectureSearch> = _searchedLectureList




    fun loadMyLecture() {
        viewModelScope.launch {
            try {
                _myLectureList.emit(tableService.getDefaultScheduleLectures().custom_lectures.toMutableList())
            } catch (e : HttpException) {
                errorMessage = e.message()
                _myLectureList.emit(null)
            }
        }
    }

    fun getLectureList(offset: Int, limit: Int, keyword: String, professor : String){
        viewModelScope.launch {
            val byKeyword = service.getSubjectList(offset, limit, keyword, null)
            val byProfessor = service.getSubjectList(offset, limit, null, professor)
            byKeyword.results.addAll(byProfessor.results)

            _searchedLectureList.value = byKeyword
        }
    }
}