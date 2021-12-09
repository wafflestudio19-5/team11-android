package com.example.toyproject

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityMainBinding

class LoginActivity:AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}