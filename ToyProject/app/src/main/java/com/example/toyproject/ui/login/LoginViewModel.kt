package com.example.toyproject.ui.login

import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.*
import com.example.toyproject.ui.univsearch.UnivSearchActivity
import com.example.toyproject.ui.univsearch.UnivSearchActivity_GeneratedInjector
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.launch
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

    // 구글 로그인 서버와 통신
    private val _googleLoginResult  = MutableLiveData<String>()
    val googleLoginResult : LiveData<String> = _googleLoginResult

    // 카카오 로그인도 같은 방식
    private val _kakaoLoginResult = MutableLiveData<String>()
    val kakaoLoginResult : LiveData<String> = _kakaoLoginResult

    // 소셜 회원가입용 정보
    lateinit var registerInfoEmail : String
    lateinit var registerInfoToken : String

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

    fun googleLogin(param : LoginSocial, email : String) {
        service.googleLogin(param).enqueue(object : Callback<LoginSocialResponse>{
            override fun onFailure(call: Call<LoginSocialResponse>, t: Throwable) {
                _googleLoginResult.value = "fail"
                errorMessage = "다시 시도해 주세요."
            }
            override fun onResponse(
                call: Call<LoginSocialResponse>,
                response: Response<LoginSocialResponse>
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
                            if(error?.non_field_errors != null) {
                                // TODO : "구글 계정으로 회원가입 하시겠습니까?" 창 띄우기
                                registerInfoEmail = email
                                registerInfoToken = param.access_token
                                _googleLoginResult.value = "register"
                            }
                            else {
                                _googleLoginResult.value = "fail"
                            }
                        } catch (e: Exception) {
                            errorMessage = response.errorBody()?.string()!!
                        }
                    }
                    else {
                        _googleLoginResult.value = "fail"
                        errorMessage = "다시 시도해 주세요."
                    }

                }
            }
        })
    }

    fun kakaoLogin(param : LoginSocial, email : String?) {
        service.kakaoLogin(param).clone().enqueue(object : Callback<LoginSocialResponse>{
            override fun onFailure(call: Call<LoginSocialResponse>, t: Throwable) {
                Timber.d("로그인 실패")
            }
            override fun onResponse(
                call: Call<LoginSocialResponse>,
                response: Response<LoginSocialResponse>
            ) {
                // 카카오 로그인 성공
                if(response.isSuccessful) {
                    sharedPreferences.edit {
                        this.putString("token", response.body()!!.token)
                    }
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
                            // non_field_error 가 왔으면 우리 서버에 그 카카오 계정이 등록되어있지 않은 상태. 회원가입 진행
                            if(error?.non_field_errors != null) {
                                // TODO : "카카오 계정으로 회원가입 하시겠습니까?" 창 띄우기
                                registerInfoEmail = email.toString()
                                registerInfoToken = param.access_token
                                _kakaoLoginResult.value = "register"
                            }
                            // 아니면, 통신 에러 및 기타 에러
                            else {
                                _kakaoLoginResult.value = "fail"
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

    fun fcmToken(token: String){
        viewModelScope.launch {
            service.fcmToken(token)
        }
    }
}