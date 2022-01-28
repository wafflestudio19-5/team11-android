package com.example.toyproject.ui.review

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityReviewBinding
import com.example.toyproject.network.dto.table.CustomLecture
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch

@AndroidEntryPoint
class ReviewActivity: AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding

    private val viewModel : ReviewActivityViewModel by viewModels()

    private lateinit var myLectureAdapter: ReviewActivityMyLectureAdapter
    private lateinit var myLectureLinearLayoutManager: LinearLayoutManager

    private lateinit var searchAdapter: LectureSearchAdapter
    private lateinit var searchLinearLayoutManager: LinearLayoutManager

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전환 이펙트 : 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 내 강의평에 표기할 강의들 불러오기 (default 시간표 기준)
        lifecycleScope.launch {
            viewModel.loadMyLecture()
            viewModel.myLectureList.collect {
                if(it==null) {
                    Toast.makeText(this@ReviewActivity, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
                }
                else {
                    val serverLecture = mutableListOf<CustomLecture>()
                    it.forEach { item ->
                        if(item.lecture != null) serverLecture.add(item)
                    }
                    myLectureAdapter.setLectures(serverLecture)

                    if(serverLecture.isNotEmpty()) {
                        binding.lectureMyReviewNothing.visibility = View.GONE
                    }
                }
            }
        }

        // 내 강의평 부분
        myLectureAdapter = ReviewActivityMyLectureAdapter(this)
        myLectureLinearLayoutManager = LinearLayoutManager(this)
        binding.recyclerViewMyReview.apply {
            adapter = myLectureAdapter
            layoutManager = myLectureLinearLayoutManager
        }
        myLectureAdapter.setCaller(object : ReviewActivityMyLectureAdapter.Caller {
            override fun click(id: Int) {
                val intent = Intent(this@ReviewActivity, LectureInfoActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }
        })

        // 검색 부분
        searchAdapter = LectureSearchAdapter(this)
        searchLinearLayoutManager = LinearLayoutManager(this)
        binding.recyclerViewReviewSearch.apply {
            adapter = searchAdapter
            layoutManager = searchLinearLayoutManager
        }


        //과목명으로 검색
        binding.searchReviews.setOnClickListener {
            val keyword = binding.reviewSearchBar.text.toString()
            viewModel.getLectureList(page++, 20, keyword, keyword)
        }

    }

    override fun onBackPressed() {
        // 뒤로 버튼 누르면 아래로 내려가기
        finish()
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
    }
}