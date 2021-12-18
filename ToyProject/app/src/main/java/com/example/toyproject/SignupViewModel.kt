package com.example.toyproject

import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.Signup
import com.example.toyproject.network.dto.SignupResponse
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.runBlocking
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject


@HiltViewModel
class SignupViewModel @Inject constructor(
    private val service: Service
): ViewModel() {

    @Inject
    lateinit var sharedPreferences : SharedPreferences

    private val _result  = MutableLiveData<String>()
    val result : LiveData<String> = _result

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
}