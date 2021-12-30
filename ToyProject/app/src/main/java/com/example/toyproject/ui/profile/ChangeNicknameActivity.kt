package com.example.toyproject.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityChangeNicknameBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ChangeNicknameActivity:AppCompatActivity() {
    private lateinit var binding:ActivityChangeNicknameBinding
    private val viewModel: NicknameViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangeNicknameBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.nicknameInputLayout.hint = intent.getStringExtra("nickname")

        binding.changeNickButton.setOnClickListener{
            if(binding.newNickname.text.toString() == binding.nicknameInputLayout.hint){
                Toast.makeText(this, "기존의 닉네임과 동일합니다.", Toast.LENGTH_SHORT).show()
            }
            else{
                viewModel.changeNickname(binding.newNickname.text.toString())
            }
        }

        viewModel.response.observe(this, {
            if(it.success==true){
                val intent = Intent(this, UserActivity::class.java)
                startActivity(intent)
                setResult(RESULT_OK, Intent())
                finish()
            }
        })

        viewModel.result.observe(this,{
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

    }
}