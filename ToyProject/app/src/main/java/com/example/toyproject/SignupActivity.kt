package com.example.toyproject

import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivitySignupBinding
import com.example.toyproject.network.Service
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber
import java.lang.Exception
import javax.inject.Inject

@AndroidEntryPoint
class SignupActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySignupBinding

    @Inject
    lateinit var service: Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)

        var idChecked = false
        var emailChecked = false
        var nicknameChecked = false

        binding.idCheckButton.setOnClickListener {
            if(!idChecked) {
                try {
                    val result = service.checkId(binding.idEdit.text.toString())
                    if (result.check) {
                        idChecked = true
                        binding.idCheckButton.text = "확인 완료"
                    }
                    Toast.makeText(this@SignupActivity, result.detail, Toast.LENGTH_LONG).show()
                } catch (e : Exception) {
                    Timber.d(e)
                }
            }
        }
        binding.emailCheckButton.setOnClickListener {
            if (!emailChecked) {
                try {
                    val result = service.checkEmail(binding.emailEdit.text.toString())
                    if (result.check) {
                        emailChecked = true
                        binding.emailCheckButton.text = "확인 완료"
                    }
                    Toast.makeText(this@SignupActivity, result.detail, Toast.LENGTH_LONG).show()
                } catch (e: Exception) {
                    Timber.d(e)
                }
            }
        }
        binding.loginButton.setOnClickListener {


        }
    }
}