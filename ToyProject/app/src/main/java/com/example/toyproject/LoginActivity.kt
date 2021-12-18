package com.example.toyproject

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityLoginBinding
import com.example.toyproject.network.dto.Login
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class LoginActivity:AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding
    private val viewModel : LoginViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel

        // 로그인 버튼 눌렀을 때
        binding.loginButton.setOnClickListener {
            val param = Login(binding.emailEdit.text.toString(), binding.passwordEdit.text.toString())
            viewModel.login(param)
        }
        viewModel.result.observe(this, {
            if(it=="success") {
                val intent = Intent(this, MainActivity::class.java)
                startActivity(intent)
                finish()
            }
            else {
                Toast.makeText(this, "에러 메시지", Toast.LENGTH_LONG).show()
                // TODO : 에러 메시지 띄우기
            }
        })

        // signup 이 완료되지 않았으면 뒤로가기 버튼으로 다시 이 화면으로 돌아올 수 있고, 이후 과정이 모두 끝날 때 이 액티비티를 종료
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    finish()
                }
            }
        binding.signupButton.setOnClickListener {
            val intent = Intent(this, UnivCertifyActivity::class.java)
            resultListener.launch(intent)
        }
    }
}