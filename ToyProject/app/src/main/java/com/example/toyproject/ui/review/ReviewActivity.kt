package com.example.toyproject.ui.review

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ActivityReviewBinding
import com.example.toyproject.network.LectureInfo
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ReviewActivity: AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding
    private val viewModel: ReviewViewModel by viewModels()

    private lateinit var searchAdapter: LectureSearchAdapter
    private lateinit var searchLayoutManager: LinearLayoutManager

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityReviewBinding.inflate(layoutInflater)
        setContentView(binding.root)

        searchAdapter = LectureSearchAdapter()
        searchLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewReviewSearch.apply {
            adapter = searchAdapter
            layoutManager = searchLayoutManager
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        //과목명으로 검색
        binding.searchButton.setOnClickListener {
            val keyword = binding.reviewSearchBar.text.toString()
            viewModel.getLectureList(page++, 20, keyword)
        }

        viewModel.result.observe(this){
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        }

        viewModel.lectureList.observe(this){
            binding.linearLayout.visibility= GONE
            if(it.size==0){
                binding.notFoundText.visibility=VISIBLE
            }
            searchAdapter.setResults(it)
            searchAdapter.notifyItemRemoved((page-1)*20)
            searchAdapter.notifyDataSetChanged()
        }

        binding.recyclerViewReviewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                super.onScrolled(recyclerView, dx, dy)
                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                if(!binding.recyclerViewReviewSearch.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount){

                    viewModel.getLectureList(20*(page++), 20, binding.reviewSearchBar.text.toString())
                }

            }
        })

        searchAdapter.setItemClickListener(object: LectureSearchAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: LectureInfo, position: Int) {
                Intent(this@ReviewActivity, LectureInfoActivity::class.java).apply{
                    putExtra("id", data.id)
                }.run{startActivity(this)
                }
            }
        })
    }
}