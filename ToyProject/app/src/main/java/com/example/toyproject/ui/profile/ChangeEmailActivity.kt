package com.example.toyproject.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityChangeEmailBinding

class ChangeEmailActivity:AppCompatActivity() {
    private lateinit var binding: ActivityChangeEmailBinding
    private val viewModel: EmailViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.emailInputLayout.hint = intent.getStringExtra("email")

        binding.changeEmailButton.setOnClickListener{
            if(binding.newEmail.text == binding.emailInputLayout.hint){
                Toast.makeText(this, "기존의 이메일과 동일합니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.changeEmail(binding.newEmail.text.toString())
            }
        }
        //이렇게 되면 비번입력이 의미가 있나
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