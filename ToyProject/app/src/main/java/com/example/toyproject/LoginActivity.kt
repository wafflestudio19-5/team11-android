package com.example.toyproject

import android.content.Intent
import android.os.Bundle
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityLoginBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class LoginActivity:AppCompatActivity() {
    private lateinit var binding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)


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