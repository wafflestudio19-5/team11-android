package com.example.toyproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.ChangeSuccess
import com.example.toyproject.network.CompareResult
import com.example.toyproject.network.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class UnivCertifyViewModel @Inject constructor(
    private val service: UserService
): ViewModel(){
    private val _result = MutableLiveData<Int>()
    val result: LiveData<Int> = _result

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    private val _resultComp = MutableLiveData<CompareResult>()
    val resultComp: LiveData<CompareResult> = _resultComp

    private val _errorComp = MutableLiveData<String>()
    val errorComp: LiveData<String> = _errorComp

    fun sendCode(email: String) {
        service.sendEmailCode(email).clone().enqueue(object : Callback<Void> {
            override fun onFailure(call: Call<Void>, t: Throwable) {
                _error.value = t.message
            }
            override fun onResponse(
                call: Call<Void>, response: Response<Void>
            ) {
                if(response.isSuccessful) {
                    _result.value = response.code()
                }
                else {
                    _error.value = response.errorBody()?.string()!!
                }
            }
        })
    }

    fun compareCode(email: String, code: Int){
        service.compareCode(email, code).clone().enqueue(object : Callback<CompareResult> {
            override fun onFailure(call: Call<CompareResult>, t: Throwable) {
                _errorComp.value = t.message
            }
            override fun onResponse(
                call: Call<CompareResult>, response: Response<CompareResult>
            ) {
                if(response.isSuccessful) {
                    _resultComp.value = response.body()
                }
                else {
                    _errorComp.value = response.errorBody()?.string()!!
                }
            }
        })
    }
}