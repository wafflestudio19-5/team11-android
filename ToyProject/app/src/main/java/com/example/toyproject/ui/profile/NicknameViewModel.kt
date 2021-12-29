package com.example.toyproject.ui.profile

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.ChangeSuccess
import com.example.toyproject.network.Nickname
import com.example.toyproject.network.UserService
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import javax.inject.Inject

@HiltViewModel
class NicknameViewModel @Inject constructor(
    private val service: UserService,
): ViewModel(){
    private val _result = MutableLiveData<String>()
    val result: LiveData<String> = _result

    private val _response = MutableLiveData<ChangeSuccess>()
    val response: LiveData<ChangeSuccess> = _response

    fun changeNickname(nickname: String) {
        service.changeNick(Nickname(nickname)).enqueue(object : Callback<ChangeSuccess> {
            override fun onFailure(call: Call<ChangeSuccess>, t: Throwable) {
                _result.value = t.message
            }
            override fun onResponse(
                call: Call<ChangeSuccess>, response: Response<ChangeSuccess>
            ) {
                if(response.isSuccessful) {
                    _response.value = response.body()
                }
                else {
                    _response.value = response.body()
                }
            }
        })
    }
}