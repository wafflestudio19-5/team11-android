package com.example.toyproject.ui.article

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityArticleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)
    }
}