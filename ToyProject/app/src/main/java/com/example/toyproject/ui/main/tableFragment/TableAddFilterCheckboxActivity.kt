package com.example.toyproject.ui.main.tableFragment

import android.content.SharedPreferences
import android.os.Bundle
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableFilterCheckboxBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import kotlin.text.StringBuilder

@AndroidEntryPoint
class TableAddFilterCheckboxActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding : ActivityTableFilterCheckboxBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전환 이펙트 : 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        binding = ActivityTableFilterCheckboxBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val mode = intent.getStringExtra("mode")
        val checkDefaultInfo = sharedPreferences.getString(mode, null)!!.split("-")
        var boxes = arrayListOf<TableAddFilterCheckboxItemView>()

        // 학년 필터
        if(mode=="filter_year") {
            boxes = arrayListOf(TableAddFilterCheckboxItemView(this, "1학년"),
                TableAddFilterCheckboxItemView(this, "2학년"),
                TableAddFilterCheckboxItemView(this, "3학년"),
                TableAddFilterCheckboxItemView(this, "4학년"),
                TableAddFilterCheckboxItemView(this, "기타"))
        }
        else if(mode=="filter_type") {
            boxes = arrayListOf(TableAddFilterCheckboxItemView(this, "전선"),
                TableAddFilterCheckboxItemView(this, "교양"),
                TableAddFilterCheckboxItemView(this, "일선"),
                TableAddFilterCheckboxItemView(this, "논문"),
                TableAddFilterCheckboxItemView(this, "전필"),
                TableAddFilterCheckboxItemView(this, "교직"))
        }
        else if(mode=="filter_credit") {
            boxes = arrayListOf(TableAddFilterCheckboxItemView(this, "0학점"),
                TableAddFilterCheckboxItemView(this, "0.5학점"),
                TableAddFilterCheckboxItemView(this, "1학점"),
                TableAddFilterCheckboxItemView(this, "1.5학점"),
                TableAddFilterCheckboxItemView(this, "2학점"),
                TableAddFilterCheckboxItemView(this, "2.5학점"),
                TableAddFilterCheckboxItemView(this, "3학점"),
                TableAddFilterCheckboxItemView(this, "3.5학점"),
                TableAddFilterCheckboxItemView(this, "4학점 이상"))
        }

        // 가져온 정보대로 뷰 채우고, 클릭 리스너 설정
        checkDefaultInfo.forEachIndexed { idx, string ->
            boxes[idx].checkBox.isChecked = string.toBoolean()
        }
        boxes.forEach{ item->
            binding.checkboxLayout.addView(item)
            item.setOnClickListener {
                item.checkBox.isChecked = !item.checkBox.isChecked
            }
        }

        // 완료 버튼 누르면, 체크박스들 정보 취합
        binding.tableFilterCheckboxSet.setOnClickListener {
            var oneChecked = false
            boxes.forEach { item ->
                oneChecked = oneChecked || item.checkBox.isChecked
            }
            if(!oneChecked) {
                if(mode=="filter_year")  Toast.makeText(this, "학년을 1개 이상 선택해주세요", Toast.LENGTH_SHORT).show()
                if(mode=="filter_type")  Toast.makeText(this, "구분을 1개 이상 선택해주세요", Toast.LENGTH_SHORT).show()
                if(mode=="filter_credit")  Toast.makeText(this, "학점을 1개 이상 선택해주세요", Toast.LENGTH_SHORT).show()
                return@setOnClickListener
            }

            val sBuilder = StringBuilder()
            boxes.forEachIndexed { idx, bool ->
                if(idx==boxes.size-1) sBuilder.append("${bool.checkBox.isChecked}")
                else sBuilder.append("${bool.checkBox.isChecked}-")
            }
            sharedPreferences.edit {
                this.putString(mode, sBuilder.toString())
            }
            setResult(RESULT_OK)
            onBackPressed()
        }

        // 전체 선택 버튼
        binding.tableFilterCheckboxSelectAll.setOnClickListener {
            boxes.forEachIndexed { idx, item ->
                item.checkBox.isChecked = true
            }
        }

        // 전체 취소 버튼
        binding.tableFilterCheckboxDeleteAll.setOnClickListener {
            boxes.forEachIndexed { idx, item ->
                item.checkBox.isChecked = false
            }
        }

        // X 버튼
        binding.tableFilterCheckboxCloseButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        // 뒤로 버튼 누르면 아래로 내려가기
        finish()
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
    }
}