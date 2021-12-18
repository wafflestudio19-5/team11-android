package com.example.toyproject

import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.RegisterCheck
import com.example.toyproject.network.dto.Signup
import com.example.toyproject.network.dto.SignupResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class SignupViewModel @Inject constructor(
    private val service: Service,
    private val retrofit: Retrofit
): ViewModel() {

    @Inject
    lateinit var sharedPreferences : SharedPreferences

    private val _result  = MutableLiveData<String>()
    val result : LiveData<String> = _result

    private val _idCheckResult  = MutableLiveData<RegisterCheck>()
    val idCheckResult : LiveData<RegisterCheck> = _idCheckResult

    private val _emailCheckResult  = MutableLiveData<RegisterCheck>()
    val emailCheckResult : LiveData<RegisterCheck> = _emailCheckResult

    fun signup(param : Signup) {
        service.register(param).clone().enqueue(object : Callback<SignupResponse> {
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {
                TODO("Not yet implemented")
            }
            override fun onResponse(
                call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if(response.isSuccessful) {
                    sharedPreferences.edit {
                        putString("token", response.body()!!.token)
                        _result.value = "success"
                    }
                }
                else {
                    _result.value = "fail"
                }
            }
        })
    }

    fun checkID(user_id : String) {
        service.checkId(user_id).clone().enqueue(object : Callback<RegisterCheck>{
            override fun onFailure(call: Call<RegisterCheck>, t: Throwable) {

            }
            override fun onResponse(call: Call<RegisterCheck>, response: Response<RegisterCheck>) {
                if(response.isSuccessful) {
                    _idCheckResult.value = response.body()
                }
                else {
                    // TODO : response.errorBody() 출력하기
                }
            }
        })
    }

    fun checkEmail(email: String) {
        service.checkEmail(email).clone().enqueue(object : Callback<RegisterCheck>{
            override fun onFailure(call: Call<RegisterCheck>, t: Throwable) {
                // TODO : 예상 밖 에러 처리
            }

            override fun onResponse(call: Call<RegisterCheck>, response: Response<RegisterCheck>) {
                if(response.isSuccessful){
                    _emailCheckResult.value = response.body()
                }
                else {
                    // TODO : response.errorBody() 출력하기
                }

            }
        })
    }
}