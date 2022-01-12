package com.example.toyproject.ui.profile

import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R
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

        // 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

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
                setResult(RESULT_OK)
                finish()
            }
        })

        viewModel.result.observe(this,{
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

    }

    override fun onBackPressed() {
        setResult(RESULT_OK)
        finish()
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
    }
}