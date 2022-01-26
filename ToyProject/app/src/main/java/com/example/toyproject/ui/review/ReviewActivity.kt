package com.example.toyproject.ui.review

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityReviewBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewActivity: AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        //임시로 lectureinfoactivity랑 연결해놓음
        binding.backArrow.setOnClickListener {
            Intent(this, LectureInfoActivity::class.java).apply{

            }.run{startActivity(this)}
        }
    }
}