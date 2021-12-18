package com.example.toyproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivitySignupBinding
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.Signup
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
    private val viewModel : SignupViewModel by viewModels()

    @Inject
    lateinit var service: Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel

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

        // 회원가입 버튼
        binding.loginButton.setOnClickListener {
            val param = Signup(binding.idEdit.text.toString(),
                               binding.passwordEdit.text.toString(),
                               binding.emailEdit.text.toString(),
                               intent.getIntExtra("year", 2022),
                               binding.nicknameEdit.text.toString())
            CoroutineScope(Dispatchers.IO).launch {
                viewModel.signup(param)
            }
        }

        // signup 성공하면 MainActivity 시작하고, 켜져 있는 다른 액티비티(univCertify, login) 끄기
        viewModel.result.observe(this, {
            if(it=="success") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                setResult(RESULT_OK, Intent())
                finish()
            }
            else {
                Toast.makeText(this, "에러 메시지", Toast.LENGTH_LONG).show()
                //TODO : 에러메시지 띄우기
            }
        })
    }
}