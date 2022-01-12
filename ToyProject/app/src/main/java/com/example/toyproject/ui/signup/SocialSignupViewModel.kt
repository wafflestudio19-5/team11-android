package com.example.toyproject.ui.signup

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.*
import dagger.hilt.android.lifecycle.HiltViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class SocialSignupViewModel @Inject constructor(
    private val service: Service,
    private val retrofit: Retrofit
): ViewModel() {

    @Inject
    lateinit var sharedPreferences : SharedPreferences

    private val _result  = MutableLiveData<String>()
    val result : LiveData<String> = _result

    private val _nicknameCheckResult  = MutableLiveData<RegisterCheck>()
    val nicknameCheckResult : LiveData<RegisterCheck> = _nicknameCheckResult

    lateinit var errorMessage : String


    fun socialSignup(param : RegisterSocial, type : String) {
        lateinit var response : Call<RegisterSocialResponse>
        if(type=="kakao")  response = service.kakaoRegister(param).clone()
        else if(type=="fire") response = service.googleRegister(param).clone()

        response.enqueue(object : Callback<RegisterSocialResponse> {
            override fun onFailure(call: Call<RegisterSocialResponse>, t: Throwable) {
                // TODO
            }

            override fun onResponse(
                call: Call<RegisterSocialResponse>,
                response: Response<RegisterSocialResponse>
            ) {
                if(response.isSuccessful) {
                    sharedPreferences.edit {
                        putString("token", response.body()!!.token)
                        _result.value = "success"
                    }
                }
                else {
                    if(response.errorBody() != null) {
                        try {
                            val error = retrofit.responseBodyConverter<ErrorMessage>(
                                ErrorMessage::class.java,
                                ErrorMessage::class.java.annotations
                            ).convert(response.errorBody())
                            errorMessage = parsing(error)
                        } catch (e:Exception) {
                            errorMessage = response.errorBody()?.string()!!
                        }
                    }
                    _result.value = "fail"
                }
            }
        })
    }

    fun checkNickname(nickname : String) {
        service.checkNickname(nickname).clone().enqueue(object : Callback<RegisterCheck>{
            override fun onFailure(call: Call<RegisterCheck>, t: Throwable) {
                // TODO : 예상 밖 에러 처리
            }

            override fun onResponse(call: Call<RegisterCheck>, response: Response<RegisterCheck>) {
                if(response.isSuccessful){
                    _nicknameCheckResult.value = response.body()
                }
                else {
                    if(response.errorBody() != null) {
                        try {
                            val error = retrofit.responseBodyConverter<ErrorMessage>(
                                ErrorMessage::class.java,
                                ErrorMessage::class.java.annotations
                            ).convert(response.errorBody())
                            errorMessage = parsing(error)
                        } catch (e:Exception) {
                            errorMessage = response.errorBody()?.string()!!
                        }
                    }
                    _result.value = "fail"
                }
            }
        })
    }
}