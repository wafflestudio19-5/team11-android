package com.example.toyproject.ui.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.LectureInfo
import com.example.toyproject.network.ReviewService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class LectureInfoViewModel @Inject constructor(
    private val service: ReviewService
): ViewModel(){
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _lectureInfo = MutableLiveData<LectureInfo>()
    val lectureInfo: LiveData<LectureInfo> = _lectureInfo

    fun getLectureInfo(id: Int){
        service.getLectureInfo(id).clone().enqueue(object : Callback<LectureInfo> {
            override fun onFailure(call: Call<LectureInfo>, t: Throwable){
                _result.value = t.message
            }
            override fun onResponse(
                call: Call<LectureInfo>, response: Response<LectureInfo>
            ){
                if(response.isSuccessful){
                    _lectureInfo.value = response.body()
                }
                else{
                    _result.value = response.errorBody()?.string()!!
                }
            }
        })
    }
}