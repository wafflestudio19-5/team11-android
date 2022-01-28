package com.example.toyproject.ui.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.LectureInfo
import com.example.toyproject.network.LectureSearch
import com.example.toyproject.network.ReviewService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Response
import javax.inject.Inject
import retrofit2.Callback

@HiltViewModel
class ReviewViewModel @Inject constructor(
    private val service: ReviewService
): ViewModel(){

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _lectureList = MutableLiveData<MutableList<LectureInfo>>()
    val lectureList: LiveData<MutableList<LectureInfo>> = _lectureList

    fun getLectureList(offset: Int, limit: Int, param: String){
        service.getSubjectList(offset, limit, param).clone().enqueue(object: Callback<LectureSearch>{
            override fun onFailure(call: Call<LectureSearch>, t: Throwable){
                _result.value = t.message
            }
            override fun onResponse(
                call: Call<LectureSearch>, response: Response<LectureSearch>
            ){
                if(response.isSuccessful){
                    if(response.body()?.count!! < offset) return
                    _lectureList.value = response.body()?.results!!
                }
                else{
                    _result.value = response.errorBody()?.string()!!
                }
            }
        } )
    }
}