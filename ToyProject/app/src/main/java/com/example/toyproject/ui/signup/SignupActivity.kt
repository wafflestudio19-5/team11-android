package com.example.toyproject.ui.signup

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivitySignupBinding
import com.example.toyproject.network.Service
import com.example.toyproject.network.dto.Signup
import com.example.toyproject.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
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
        var idTemp = ""         // 중복확인 한 후에, 다시 id 수정하는 것 탐지 용도
        var emailChecked = false
        var emailTemp = ""      // 중복확인 한 후에, 다시 email 수정하는 것 탐지 용도
        var nicknameChecked = false
        var nicknameTemp = "" // 중복확인 한 후에, 다시 nickname 수정하는 것 탐지 용도


        // id editText 변화 탐지
        binding.idEdit.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString() != idTemp) {
                    idChecked = false
                    binding.idCheckButton.text = "중복확인"
                }
            }
        })

        // email editText 변화 탐지
        binding.emailEdit.addTextChangedListener(object : TextWatcher{
            override fun afterTextChanged(p0: Editable?) {
            }
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                if(p0.toString() != emailTemp) {
                    emailChecked = false
                    binding.emailCheckButton.text = "중복확인"
                }
            }
        })

        // nickname editText 변화 탐지
        binding.nicknameEdit.addTextChangedListener(object : TextWatcher{
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

        // 아이디 중복확인 부분
        binding.idCheckButton.setOnClickListener {
            if(!idChecked) {
                idTemp = binding.idEdit.text.toString()
                viewModel.checkID(idTemp)
            }
            else {
                Toast.makeText(this, "사용 가능한 아이디입니다.", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.idCheckResult.observe(this, {
            if(it.check==true) {
                idChecked = true
                binding.idCheckButton.text = "확인 완료"
                Toast.makeText(this, it.detail, Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
            }
        })

        // 이메일 중복확인 부분
        binding.emailCheckButton.setOnClickListener {
            if (!emailChecked) {
                emailTemp = binding.emailEdit.text.toString()
                viewModel.checkEmail(emailTemp)
            }
            else {
                Toast.makeText(this, "사용 가능한 이메일입니다.", Toast.LENGTH_SHORT).show()
            }
        }
        viewModel.emailCheckResult.observe(this, {
            if(it.check==true) {
                emailChecked = true
                binding.emailCheckButton.text = "확인 완료"
                Toast.makeText(this, it.detail, Toast.LENGTH_LONG).show()
            }
            else {
                Toast.makeText(this, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
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
            if(idChecked && emailChecked && nicknameChecked) {
                val param = Signup(binding.idEdit.text.toString(),
                    binding.passwordEdit.text.toString(),
                    binding.emailEdit.text.toString(),
                    intent.getIntExtra("year", 2022),
                    binding.nicknameEdit.text.toString(),
                    intent.getStringExtra("university"),
                    binding.nameEdit.text.toString()
                )
                CoroutineScope(Dispatchers.IO).launch {
                    viewModel.signup(param)
                }
            }
            else {
                if(!idChecked) {
                    Toast.makeText(this, "아이디 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }
                else if(!emailChecked) {
                    Toast.makeText(this, "이메일 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }
                else if(!nicknameChecked) {
                    Toast.makeText(this, "닉네임 중복확인을 해주세요.", Toast.LENGTH_SHORT).show()
                }
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