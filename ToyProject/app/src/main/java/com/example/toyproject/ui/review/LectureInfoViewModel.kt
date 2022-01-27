package com.example.toyproject.ui.review

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.*
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

    private val _reviewResult = MutableLiveData<String>()
    val reviewResult: LiveData<String> = _reviewResult

    private val _postReviewResult = MutableLiveData<String>()
    val postReviewResult: LiveData<String> = _postReviewResult

    private val _lectureInfo = MutableLiveData<LectureInfo>()
    val lectureInfo: LiveData<LectureInfo> = _lectureInfo

    private val _reviewList = MutableLiveData<MutableList<Review>>()
    val reviewList: LiveData<MutableList<Review>> = _reviewList

    private val _review = MutableLiveData<Review>()
    val review: LiveData<Review> = _review

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

    fun getReviews(id: Int){
        service.getReviewList(id).clone().enqueue(object : Callback<ReviewList> {
            override fun onFailure(call: Call<ReviewList>, t: Throwable){
                _reviewResult.value = t.message
            }
            override fun onResponse(
                call: Call<ReviewList>, response: Response<ReviewList>
            ){
                if(response.isSuccessful){
                    _reviewList.value = response.body()?.reviews
                }
                else{
                    _reviewResult.value = response.errorBody()?.string()!!
                }
            }
        })
    }

    fun postReview(id: Int, param: CreateReview){
        service.postReview(id, param).clone().enqueue(object : Callback<Review> {
            override fun onFailure(call: Call<Review>, t: Throwable){
                _postReviewResult.value = t.message
            }
            override fun onResponse(
                call: Call<Review>, response: Response<Review>
            ){
                if(response.isSuccessful){
                    _review.value = response.body()
                }
                else{
                    _postReviewResult.value = response.errorBody()?.string()!!
                }
            }
        })
    }
}