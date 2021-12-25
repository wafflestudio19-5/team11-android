package com.example.toyproject

import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.ErrorMessage
import com.example.toyproject.network.dto.Login
import com.example.toyproject.network.dto.SignupResponse
import com.example.toyproject.network.dto.parsing
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
}