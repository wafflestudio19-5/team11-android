package com.example.toyproject.ui.login

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
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject


@HiltViewModel
class LoginViewModel @Inject constructor(
    private val service: Service,
    private val retrofit: Retrofit
) : ViewModel() {

    @Inject
    lateinit var sharedPreferences : SharedPreferences

    private val _result  = MutableLiveData<String>()
    val result : LiveData<String> = _result

    private val _tokenResult  = MutableLiveData<String>()
    val tokenResult : LiveData<String> = _tokenResult

    // 로그인 버튼 누르면, 구글 계정에서 access token 받아서 먼저 로그인을 시도하고,
    // 서버에서 로그인에 실패했으면(=신규 유저이면) 팝업을 띄우고 자동 register
    private val _googleLoginResult  = MutableLiveData<String>()
    val googleLoginResult : LiveData<String> = _googleLoginResult

    // 카카오 로그인도 같은 방식
    private val _kakaoLoginResult = MutableLiveData<String>()
    val kakaoLoginResult : LiveData<String> = _kakaoLoginResult

    lateinit var errorMessage : String

    fun loginToken() {
        service.loginToken().clone().enqueue(object : Callback<SignupResponse> {
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {

            }
            override fun onResponse(call: Call<SignupResponse>, response: Response<SignupResponse>) {
                if(response.isSuccessful) {
                    _tokenResult.value = "success"
                }
                else {
                    if(response.errorBody() != null) {
                        try {
                            val error = retrofit.responseBodyConverter<ErrorMessage>(
                                ErrorMessage::class.java,
                                ErrorMessage::class.java.annotations
                            ).convert(response.errorBody())
                            errorMessage = parsing(error)
                        } catch (e: Exception) {
                            errorMessage = response.errorBody()?.string()!!
                        }
                    }
                    _tokenResult.value = "fail"
                }
            }
        })
    }

    fun login(param : Login) {
        service.login(param).enqueue(object : Callback<SignupResponse>{
            override fun onFailure(call: Call<SignupResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<SignupResponse>,
                response: Response<SignupResponse>
            ) {
                if (response.isSuccessful) {
                    sharedPreferences.edit {
                        putString("token", response.body()!!.token)
                        _result.value = "success"
                    }
                    Timber.d(sharedPreferences.getString("token", "no token"))
                }
                else {
                    if(response.errorBody() != null) {
                        try {
                            val error = retrofit.responseBodyConverter<ErrorMessage>(
                                ErrorMessage::class.java,
                                ErrorMessage::class.java.annotations
                            ).convert(response.errorBody())
                            errorMessage = parsing(error)
                        } catch (e: Exception) {
                            errorMessage = response.errorBody()?.string()!!
                        }
                    }
                    _result.value = "fail"
                }
            }
        } )
    }

    fun googleLogin(param : LoginSocial) {
        service.googleLogin(param).enqueue(object : Callback<LoginSocialResponse>{
            override fun onFailure(call: Call<LoginSocialResponse>, t: Throwable) {

            }

            override fun onResponse(
                call: Call<LoginSocialResponse>,
                response: Response<LoginSocialResponse>
            ) {
                if(response.isSuccessful) {
                    _googleLoginResult.value = "success"
                }
                else {
                    if(response.errorBody() != null) {
                        try {
                            val error = retrofit.responseBodyConverter<ErrorMessage>(
                                ErrorMessage::class.java,
                                ErrorMessage::class.java.annotations
                            ).convert(response.errorBody())
                            errorMessage = parsing(error)
                            if(error?.non_field_errors != null) {
                                _result.value = "register"
                            }
                            else {
                                _result.value = "fail"
                            }
                        } catch (e: Exception) {
                            errorMessage = response.errorBody()?.string()!!
                        }
                    }
                    else {
                        _result.value = "fail"
                        errorMessage = "다시 시도해 주세요."
                    }

                }
            }
        })
    }

    fun kakaoLogin(param : LoginSocial) {
        service.kakaoLogin(param).clone().enqueue(object : Callback<LoginSocialResponse>{
            override fun onFailure(call: Call<LoginSocialResponse>, t: Throwable) {
                // TODO
            }

            override fun onResponse(
                call: Call<LoginSocialResponse>,
                response: Response<LoginSocialResponse>
            ) {
                // 카카오 로그인 성공
                if(response.isSuccessful) {
                    _kakaoLoginResult.value = "success"
                }
                else {
                    // 카카오 로그인 실패 : case1->첫 로그인  case2->기타 에러(통신 에러, 카카오 계정 에러 등등)
                    if(response.errorBody()!=null) {
                        try {
                            val error = retrofit.responseBodyConverter<ErrorMessage>(
                                ErrorMessage::class.java,
                                ErrorMessage::class.java.annotations
                            ).convert(response.errorBody())
                            errorMessage = parsing(error)
                            // non_field_error 가 왔으면 우리 서버에 그 카카오 계정이 등록되어있지 않은 상태.
                            // 아니면, 통신 에러 및 기타 에러
                            if(error?.non_field_errors != null) {
                                _result.value = "register"
                            }
                            else {
                                _result.value = "fail"
                            }
                        } catch (e: Exception) {
                            errorMessage = response.errorBody()?.string()!!
                        }
                    }
                    else {
                        _kakaoLoginResult.value = "fail"
                        errorMessage = "다시 시도해 주세요."
                    }
                }
            }
        })
    }
}