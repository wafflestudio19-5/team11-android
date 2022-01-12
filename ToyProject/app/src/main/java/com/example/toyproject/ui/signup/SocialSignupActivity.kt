package com.example.toyproject.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivitySocialsignupBinding
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.RegisterSocial
import com.example.toyproject.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class SocialSignupActivity: AppCompatActivity() {
    private lateinit var binding: ActivitySocialsignupBinding
    private val viewModel : SocialSignupViewModel by viewModels()

    @Inject
    lateinit var service: Service

    private lateinit var socialType : String

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivitySocialsignupBinding.inflate(layoutInflater)
        setContentView(binding.root)
        viewModel

        var nicknameChecked = false
        var nicknameTemp = "" // 중복확인 한 후에, 다시 nickname 수정하는 것 탐지 용도

        socialType = intent.getStringExtra("socialType").toString()
        val admissionYear = intent.getIntExtra("admission_year", 2022)


        // nickname editText 변화 탐지
        binding.nicknameEdit.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString() != nicknameTemp) {
                    nicknameChecked = false
                    binding.nicknameCheckButton.text = "중복확인"
                }
            }
        })


        // 닉네임 중복확인 부분
        binding.nicknameCheckButton.setOnClickListener {
            if(!nicknameChecked) {
                nicknameTemp = binding.nicknameEdit.text.toString()
                viewModel.checkNickname(nicknameTemp)
            }
            else {
                Toast.makeText(this, "사용 가능한 닉네임입니다.", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.nicknameCheckResult.observe(this, {
            if(it.check) {
                nicknameChecked = true
                binding.nicknameCheckButton.text = "확인 완료"
                Toast.makeText(this, it.detail, Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
            }
        })

        // 회원가입 버튼
        binding.loginButton.setOnClickListener {
            if(nicknameChecked) {
                val access_token = intent.getStringExtra("access_token")
                val name = binding.nameEdit.text.toString()
                val email = intent.getStringExtra("email")
                val university = intent.getStringExtra("university")
                val year = admissionYear
                val nickname = binding.nicknameEdit.text.toString()
                val param = RegisterSocial(access_token, name, email, university, year, nickname)
                viewModel.socialSignup(param, socialType)
            }
            else {
                Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
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
                Toast.makeText(this, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
            }
        })
    }
}