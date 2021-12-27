package com.example.toyproject.ui.profile

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityUserBinding

class UserActivity: AppCompatActivity() {
    private lateinit var binding: ActivityUserBinding
    private val viewModel: UserViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?){
        super.onCreate(savedInstanceState)
        binding = ActivityUserBinding.inflate(layoutInflater)
        setContentView(binding.root)

        lateinit var nickname: String
        lateinit var email: String

        viewModel.getUser()

        viewModel.result.observe(this, {
            Toast.makeText(this, it, Toast.LENGTH_LONG).show()
        })

        viewModel.profile.observe(this, {
            nickname = it.nickname.toString()
            email = it.email.toString()
            binding.userId.text = it.id
            "${it.name} / ${it.nickname}/n${it.university} ${it.admissionYear?.minus(2000)}학번".also { binding.userProfile.text = it }
        })

        binding.changeEmail.setOnClickListener{
            val intent = Intent(this, ChangeEmailActivity::class.java)
            startActivity(intent)
            setResult(RESULT_OK, Intent())
        }

        binding.changeNick.setOnClickListener{
            val intent = Intent(this, ChangeNicknameActivity::class.java)
            intent.putExtra("nickname", nickname)
            startActivity(intent)
            setResult(RESULT_OK, Intent())
        }

        binding.changePw.setOnClickListener{
            val intent = Intent(this, ChangePasswordActivity::class.java)
            intent.putExtra("email", email)
            startActivity(intent)
            setResult(RESULT_OK, Intent())
        }
    }
}