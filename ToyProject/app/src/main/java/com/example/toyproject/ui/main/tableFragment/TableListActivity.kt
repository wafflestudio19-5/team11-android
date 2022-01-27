package com.example.toyproject.ui.main.tableFragment

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableListBinding
import com.example.toyproject.network.dto.table.Schedule
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
        tableListAdapter.setClicker(object : TableListAdapter.Clicker {
            override fun click(title : String, year : Int, season : Int, scheduleId : Int) {
                val resultIntent = Intent()
                resultIntent.putExtra("year", year)
                resultIntent.putExtra("season", season)
                resultIntent.putExtra("title", title)
                resultIntent.putExtra("id", scheduleId)
                setResult(99, resultIntent)
                finish()
                // 전환 이펙트 : 오른쪽으로 퇴장, 새 창은 fade in
                overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
            }
        })

        // 시간표 리스트 가져오기
        // loadScheduleList()
        viewModel.loadScheduleList()
        viewModel.semesterList.observe(this, {
            val sortSemester = hashMapOf<YearSemesterPair, MutableList<Schedule>>()
            it.forEach { schedule ->
                if(!sortSemester.containsKey(YearSemesterPair(schedule.year, schedule.season))) {
                    sortSemester[YearSemesterPair(schedule.year, schedule.season)] = mutableListOf(schedule)
                }
                else sortSemester[YearSemesterPair(schedule.year, schedule.season)]!!.add(schedule)
            }
            val semesterList = mutableListOf<Semester>()
            sortSemester.keys.sorted().forEach { semester ->
                semesterList.add(Semester(semester.year, semester.semester,
                    sortSemester[semester]!!.toList()))
            }
            tableListAdapter.setSemesters(semesterList)
        })

        // "RESULT_OK" : 새 시간표 만듬
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {
                    val intent = Intent()
                    intent.putExtra("id", it.data!!.getIntExtra("id", 0))
                    intent.putExtra("year", it.data!!.getIntExtra("year", 0))
                    intent.putExtra("season", it.data!!.getIntExtra("season", 0))
                    intent.putExtra("title", it.data!!.getStringExtra("title"))
                    setResult(99, intent)
                    finish()
                    // 전환 이펙트 : 오른쪽으로 퇴장, 새 창은 fade in
                    overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
                }
            }

        // 시간표 생성 버튼
        binding.tableListAddTable.setOnClickListener {
            val intent = Intent(this, TableMakeActivity::class.java)
            resultListener.launch(intent)
        }

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

/* Flow 오류?
    private fun loadScheduleList() {
        viewModel.loadScheduleList()
        CoroutineScope(Dispatchers.Main).launch {
            viewModel.scheduleListFlow.collect {
                if(it==null) {
                    Toast.makeText(this@TableListActivity, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
                }
                else {
                    val sortSemester = hashMapOf<YearSemesterPair, MutableList<Schedule>>()
                    it.results.forEach { schedule ->
                        if(!sortSemester.containsKey(YearSemesterPair(schedule.year, schedule.season))) {
                            sortSemester[YearSemesterPair(schedule.year, schedule.season)] = mutableListOf(schedule)
                        }
                        else sortSemester[YearSemesterPair(schedule.year, schedule.season)]!!.add(schedule)
                    }
                    val semesterList = mutableListOf<Semester>()
                    sortSemester.keys.sorted().forEach { semester ->
                        semesterList.add(Semester(semester.year, semester.semester,
                            sortSemester[YearSemesterPair(semester.year, semester.semester)]!!.toList()))
                    }
                    tableListAdapter.setSemesters(semesterList)
                }
            }
        }
    }

 */
}

class YearSemesterPair(val year : Int, val semester : Int) : Comparable<YearSemesterPair> {
    override fun compareTo(other: YearSemesterPair): Int {
        when {
            this.year > other.year -> return -1
            this.year < other.year -> return 1
            else -> {
                when (this.semester) {
                    1 -> {
                        return when(other.semester) {
                            1 -> -1
                            else ->  1
                        }
                    }
                    2 -> {
                        return when(other.semester) {
                            2 -> 0
                            4 -> 1
                            else ->  -1
                        }
                    }
                    3 -> {
                        return when(other.semester) {
                            3 -> 0
                            1 -> -1
                            else -> 1
                        }
                    }
                    else -> {
                        return when(other.semester) {
                            4 -> 0
                            else -> -1
                        }
                    }
                }
            }
        }
    }
}