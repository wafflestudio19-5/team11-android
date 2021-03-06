package com.example.toyproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.Password
import com.example.toyproject.network.UserService
import com.example.toyproject.network.WithdrawalSuccess
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class WithdrawalViewModel @Inject constructor(
    private val service: UserService
): ViewModel(){
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _response = MutableLiveData<WithdrawalSuccess>()
    val response: LiveData<WithdrawalSuccess> = _response

    fun withdrawal(password: String) {
        service.withdrawal(Password(password)).enqueue(object : Callback<WithdrawalSuccess> {
            override fun onFailure(call: Call<WithdrawalSuccess>, t: Throwable) {
                _result.value = t.message
            }
            override fun onResponse(
                call: Call<WithdrawalSuccess>, response: Response<WithdrawalSuccess>
            ) {
                if(response.isSuccessful) {
                    _response.value = response.body()
                }
                else {
                    _result.value = response.errorBody()?.string()!!
                }
            }
        })
    }
}