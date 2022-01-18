package com.example.toyproject.ui.main.tableFragment

import android.annotation.SuppressLint
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

        binding.tableMakeCloseButton.setOnClickListener {
            finish()
            // 끝낼 땐 아래로 내려가기
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
        }
    }



    override fun onBackPressed() {
        finish()
        // 끝낼 땐 아래로 내려가기
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
    }


}
