package com.example.toyproject.ui.main.tableFragment

import android.annotation.SuppressLint
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.MotionEvent
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableMakeScheduleBinding
import dagger.hilt.android.AndroidEntryPoint
import android.view.ViewGroup
import android.widget.Toast
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch


@AndroidEntryPoint
class TableMakeActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTableMakeScheduleBinding
    private val viewModel : TableMakeViewModel by viewModels()

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전환 이펙트 : 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        binding = ActivityTableMakeScheduleBinding.inflate(layoutInflater)
        setContentView(binding.root)



        // 학기 spinner 설정
        val semesters = resources.getStringArray(R.array.semesters)
        val mSpinner = ArrayAdapter(this, R.layout.item_table_make_semester, semesters)

        binding.makeNewScheduleSpinner.adapter = mSpinner
        binding.makeNewScheduleSpinner.setSelection(4)
        binding.makeNewScheduleSpinner.onItemSelectedListener = object :  AdapterView.OnItemSelectedListener {
            override fun onItemSelected(p0: AdapterView<*>?, p1: View?, p2: Int, p3: Long) {
                p1?.background = null
            }
            override fun onNothingSelected(p0: AdapterView<*>?) {}
        }

        binding.makeBoardButton.setOnClickListener {
            val title = binding.tableMakeNewTitle.text.toString()
            val selected = binding.makeNewScheduleSpinner.selectedItem.toString()
            val year = selected.split("년")[0].toInt()
            val season = seasonStringToInt(selected.split("년 ")[1])

            CoroutineScope(Dispatchers.Main).launch {
                viewModel.makeSchedule(title, year, season)
                viewModel.scheduleMakeFlow.collect {
                    if(it==null) {
                        Toast.makeText(this@TableMakeActivity, viewModel.errorMessage, Toast.LENGTH_SHORT).show()
                    }
                    else {
                        val resultIntent = Intent()
                        resultIntent.putExtra("id", it.id)
                        resultIntent.putExtra("year", it.year)
                        resultIntent.putExtra("season", it.season)
                        resultIntent.putExtra("title", it.name)
                        setResult(RESULT_OK, resultIntent)
                        finish()
                        // 끝낼 땐 아래로 내려가기
                        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
                    }
                }
            }
        }

        binding.tableMakeCloseButton.setOnClickListener {
            finish()
            // 끝낼 땐 아래로 내려가기
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
        }
    }

    private fun seasonStringToInt(season : String) : Int {
        return when(season) {
            "겨울학기" -> 4
            "여름학기" -> 3
            "2학기" -> 2
            else -> 1
        }
    }



    override fun onBackPressed() {
        finish()
        // 끝낼 땐 아래로 내려가기
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
    }


}
