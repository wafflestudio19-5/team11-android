package com.example.toyproject.ui.main.tableFragment

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableListBinding
import com.example.toyproject.network.dto.table.Semester
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableListActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTableListBinding
    private val viewModel: TableListViewModel by viewModels()

    private lateinit var tableListAdapter: TableListAdapter
    private lateinit var tableListLayoutManager: LinearLayoutManager


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전환 이펙트: 오른쪽에서 나오고, 배경은 까만색으로 fade out
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_hold_fade_out)

        binding = ActivityTableListBinding.inflate(layoutInflater)
        setContentView(binding.root)


        // 시간표 리스트 표시할 recyclerView
        tableListAdapter = TableListAdapter(this)
        tableListLayoutManager = LinearLayoutManager(this)
        binding.tableListRecyclerView.apply {
            adapter = tableListAdapter
            layoutManager = tableListLayoutManager
        }
        // 시간표 리스트 서버에 요청
        viewModel.loadScheduleList()
        // 시간표 리스트 불러왔으면 적용
        viewModel.semesterList.observe(this, {
            tableListAdapter.setSemesters(it)
        })


        // 뒤로가기 버튼
        binding.tableListBackButton.setOnClickListener {
            finish()
            // 전환 이펙트 : 오른쪽으로 퇴장, 새 창은 fade in
            overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
        }
    }

    override fun onBackPressed() {
        finish()
        // 전환 이펙트 : 오른쪽으로 퇴장, 새 창은 fade in
        overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
    }




}