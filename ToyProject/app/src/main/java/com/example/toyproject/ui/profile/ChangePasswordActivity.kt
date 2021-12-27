package com.example.toyproject.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity:AppCompatActivity() {
    private lateinit var binding:ActivityChangePasswordBinding
    private val viewModel: PasswordViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //TODO: 비밀번호 조건에 맞는지 판단하는 코드 필요
        binding.changePwButton.setOnClickListener{
            if(binding.newPassword.text != binding.passwordConfirm.text){
                Toast.makeText(this, "비밀번호가 불일치합니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.changePassword(binding.newPassword.text.toString())
            }
        }

        viewModel.response.observe(this, {
            if(it.success){
                finish()
            }
            else{
                Toast.makeText(this, it.detail, Toast.LENGTH_SHORT).show()
            }
        })

        viewModel.result.observe(this,{
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })
    }
}