package com.example.toyproject.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityChangeEmailBinding

class ChangeEmailActivity:AppCompatActivity() {
    private lateinit var binding: ActivityChangeEmailBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding= ActivityChangeEmailBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}