package com.example.toyproject.ui.board

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityBoardBinding
import dagger.hilt.android.AndroidEntryPoint


@AndroidEntryPoint
class BoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)





    }
}