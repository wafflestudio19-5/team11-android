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


        // 아이디 중복확인 부분
        binding.idCheckButton.setOnClickListener {
            if(!idChecked) {
                Timber.d(binding.idEdit.text.toString())
                viewModel.checkID(binding.idEdit.text.toString())
            }
        }
        viewModel.idCheckResult.observe(this, {
            if(it.check=="True") {
                idChecked = true
                binding.idCheckButton.text = "확인 완료"
            }
            Toast.makeText(this, it.detail, Toast.LENGTH_LONG).show()
        })

        // 이메일 중복확인 부분
        binding.emailCheckButton.setOnClickListener {
            if (!emailChecked) {
                Timber.d(binding.emailEdit.text.toString())
                viewModel.checkEmail(binding.emailEdit.text.toString())
            }
        }
        viewModel.emailCheckResult.observe(this, {
            if(it.check=="True") {
                emailChecked = true
                binding.emailCheckButton.text = "확인 완료"
            }
            Toast.makeText(this, it.detail, Toast.LENGTH_LONG).show()
        })

        // 회원가입 버튼
        binding.loginButton.setOnClickListener {
            if(idChecked && emailChecked) {
                val param = Signup(binding.idEdit.text.toString(),
                    binding.passwordEdit.text.toString(),
                    binding.emailEdit.text.toString(),
                    intent.getIntExtra("year", 2022),
                    binding.nicknameEdit.text.toString(),
                    intent.getStringExtra("university"),
                    "이름"
                )                   // TODO : UI 에서 이름 입력받기
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.signup(param)
                }
            }
            else {
                // TODO : 중복확인 해주세요 메시지 띄우기
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