package com.example.toyproject.ui.review

import android.content.Intent
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityReviewBinding
import com.example.toyproject.network.dto.table.CustomLecture
import com.example.toyproject.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ReviewActivity: AppCompatActivity() {
    private lateinit var binding: ActivityReviewBinding

    private val viewModel : ReviewActivityViewModel by viewModels()

    private lateinit var myLectureAdapter: ReviewActivityMyLectureAdapter
    private lateinit var myLectureLinearLayoutManager: LinearLayoutManager

    private lateinit var searchAdapter: LectureSearchAdapter
    private lateinit var searchLinearLayoutManager: LinearLayoutManager

    private lateinit var recentAdapter: ReviewRecentAdapter
    private lateinit var recentLinearLayoutManager: LinearLayoutManager



    private var page = 0
    private var recentPage = 0


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
        // editText 에서 엔터 누르면 바로 검색되게 하기
        binding.reviewSearchBar.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                when(p1) {
                    KeyEvent.KEYCODE_ENTER -> {
                        search()
                        return false
                    }
                    else ->
                        return false
                }
            }
        })
        // 검색 결과 item 클릭
        searchAdapter.setClicker(object : LectureSearchAdapter.Clicker {
            override fun click(id: Int) {
                val intent = Intent(this@ReviewActivity, LectureInfoActivity::class.java)
                intent.putExtra("id", id)
                startActivity(intent)
            }
        })
        // 검색 결과 observe
        viewModel.searchedLectureList.observe(this) {
            if (it.count != 0) {
                binding.notFoundText.visibility = View.GONE
                binding.lectureReviewLoading.visibility = View.GONE
            }
            else binding.notFoundText.visibility = View.VISIBLE

            if(page==0) {
                searchAdapter.setResults(it.results)
                searchAdapter.notifyItemRemoved((page++) * 20)
                binding.lectureReviewLoading.visibility = View.GONE
            }
            else {
                binding.notFoundText.visibility = View.GONE
                binding.lectureReviewLoading.visibility = View.GONE
                searchAdapter.addResult(it.results)
            }
        }
        //과목명으로 검색
        binding.lectureReviewSearchButton.setOnClickListener {
            search()
        }
        // 검색 recyclerView 스크롤 (페이지네이션?)
        binding.recyclerViewReviewSearch.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val keyword = binding.reviewSearchBar.text.toString()

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                var flag = true
                if(!binding.recyclerViewReviewSearch.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount){
                    viewModel.getLectureList((page++)*20, 20, keyword, keyword)
                }
            }
        })

        // 최근 강의평
        recentAdapter = ReviewRecentAdapter(this)
        recentLinearLayoutManager = LinearLayoutManager(this)
        binding.recyclerViewRecentReview.apply {
            adapter= recentAdapter
            layoutManager = recentLinearLayoutManager
        }
        // 최상단 툴바
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    viewModel.getRecentReview(0, 20)
                }
            }
        // 최근 강의평 아이템 클릭
        recentAdapter.setClicker(object : ReviewRecentAdapter.Clicker {
            override fun click(id: Int) {
                val intent = Intent(this@ReviewActivity, LectureInfoActivity::class.java)
                intent.putExtra("id", id)
                resultListener.launch(intent)
            }
        })
        // 최근 강의평 첫 20개 불러오기
        viewModel.getRecentReview(0, 20)
        // 페이지네이션
        binding.recyclerViewRecentReview.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                if(!binding.recyclerViewRecentReview.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount){
                    viewModel.getRecentReview((recentPage++)*20, 20)
                }
            }
        })
        // 최근 강의평 observe
        viewModel.recentReviewList.observe(this) {
            if(recentPage==0) {
                recentAdapter.setReview(it.toMutableList())
                recentPage++
            }
            else {
                Timber.d("가가가가가가가 $recentPage" )
                recentAdapter.addReview(it.toMutableList())
            }
        }


        // 검색 시 나오는 뒤로가기 버튼
        binding.tableAddServerLectureSearchBackButton.setOnClickListener {
            binding.tableAddServerLectureSearchBackButtonLayout.visibility = View.GONE
            binding.activityReviewTitle.text = "강의평가"
            binding.reviewSearchBar.text.clear()

            binding.searchReviews.visibility = View.VISIBLE
            binding.myLectureLayout.visibility = View.VISIBLE
            binding.recentReviewLayout.visibility = View.VISIBLE
            binding.recyclerViewReviewSearch.visibility= View.GONE
            binding.notFoundText.visibility = View.GONE
            binding.lectureReviewLoading.visibility = View.GONE
        }


        // 우상단 X 버튼
        binding.tableAddServerLectureCloseButton.setOnClickListener {
            // 뒤로 버튼 누르면 아래로 내려가기
            finish()
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
        }

    }
    private fun search() {
        if(binding.reviewSearchBar.text.length<2) {
            AlertDialog.Builder(this)
                .setMessage("검색어를 두 글자 이상 입력해주세요")
                .setPositiveButton("확인") { dialogInterface, _ ->
                    dialogInterface.dismiss()
                }
                .create()
                .show()
            return
        }
        else {
            searchAdapter.clearResults()
            page = 0
            binding.notFoundText.visibility = View.GONE
            binding.lectureReviewLoading.visibility = View.VISIBLE

            binding.activityReviewTitle.text = "강의평가 검색"
            binding.searchReviews.visibility = View.GONE
            binding.myLectureLayout.visibility = View.GONE
            binding.recentReviewLayout.visibility = View.GONE
            binding.recyclerViewReviewSearch.visibility= View.VISIBLE
            binding.tableAddServerLectureSearchBackButtonLayout.visibility = View.VISIBLE

            val keyword = binding.reviewSearchBar.text.toString()
            viewModel.getLectureList(0, 20, keyword, keyword)
            binding.searchReviews.visibility = View.VISIBLE
        }
    }

    override fun onBackPressed() {
        // 보통 상태에서 뒤로가기 -> 액티비티 종료
        if(binding.tableAddServerLectureSearchBackButtonLayout.visibility==View.GONE) {
            // 뒤로 버튼 누르면 아래로 내려가기
            finish()
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
        }
        // 검색 상태에서 뒤로가기 -> 액티비티 종료 x, 그냥 검색 창 닫기
        else {
            binding.tableAddServerLectureSearchBackButton.visibility = View.GONE
            binding.activityReviewTitle.text = "강의평가"
            binding.reviewSearchBar.text.clear()

            binding.myLectureLayout.visibility = View.VISIBLE
            binding.recentReviewLayout.visibility = View.VISIBLE
            binding.recyclerViewReviewSearch.visibility= View.GONE
            binding.tableAddServerLectureSearchBackButtonLayout.visibility = View.GONE
            binding.notFoundText.visibility = View.GONE
            binding.lectureReviewLoading.visibility = View.GONE
        }

    }
}