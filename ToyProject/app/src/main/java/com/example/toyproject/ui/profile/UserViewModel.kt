package com.example.toyproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.ChangeSuccess
import com.example.toyproject.network.ProfileImage
import com.example.toyproject.network.UserService
import com.example.toyproject.network.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import javax.inject.Inject

@HiltViewModel
class UserViewModel @Inject constructor(
    private val service: UserService
): ViewModel(){

    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _imageResult = MutableLiveData<String>()
    val imageResult: LiveData<String> = _imageResult

    private val _response = MutableLiveData<ChangeSuccess>()
    val response: LiveData<ChangeSuccess> = _response

    private val _profile = MutableLiveData<UserResponse>()
    val profile: LiveData<UserResponse> = _profile

    fun getUser() {
        service.getUserProfile().clone().enqueue(object : Callback<UserResponse> {
            override fun onFailure(call: Call<UserResponse>, t: Throwable) {
                _result.value = t.message
            }
            override fun onResponse(
                call: Call<UserResponse>, response: Response<UserResponse>
            ) {
                if(response.isSuccessful) {
                    _profile.value = response.body()
                }
                else {
                    _result.value = "연결에 실패했습니다."
                }
            }
        })
    }

    fun putImage(image: ProfileImage){
        service.changeImage(image).enqueue(object : Callback<ChangeSuccess> {
            override fun onFailure(call: Call<ChangeSuccess>, t: Throwable) {
                _imageResult.value = t.message
            }
            override fun onResponse(
                call: Call<ChangeSuccess>, response: Response<ChangeSuccess>
            ) {
                if(response.isSuccessful) {
                    _response.value = response.body()
                }
                else {
                    _imageResult.value = response.errorBody()?.string()!!
                }
            }
        })
    }

}