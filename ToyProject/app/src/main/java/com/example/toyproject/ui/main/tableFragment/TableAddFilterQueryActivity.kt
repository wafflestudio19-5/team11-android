package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.content.SharedPreferences
import android.os.Bundle
import android.view.KeyEvent
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.RadioButton
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableAddFilterQueryBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject
import org.json.JSONObject
import android.content.Context.MODE_PRIVATE
import android.view.WindowManager
import android.widget.Toast
import androidx.core.view.children
import androidx.core.view.get
import org.json.JSONException
import java.lang.reflect.Type

@AndroidEntryPoint
class TableAddFilterQueryActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding : ActivityTableAddFilterQueryBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전환 효과: 오른쪽에서 나오고, 배경은 까만색으로 fade out
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_hold_fade_out)

        binding = ActivityTableAddFilterQueryBinding.inflate(layoutInflater)
        setContentView(binding.root)

        val queryEditText = binding.tableAddFilterQueryEdittext
        val queryHistoryList = binding.tableAddFilterQueryHistory

        // sharedPreference 에서 검색 기록 가져오기
        val historyHashMap = hashMapOf<String, String>()
        try {
            val history : String = sharedPreferences.getString("query_history", null)!!
            val jsonObject = JSONObject(history)
            val keysItr = jsonObject.keys()
            while(keysItr.hasNext()) {
                val key = keysItr.next()
                val value = jsonObject.getString(key)
                historyHashMap[key] = value
            }
        } catch (e: NullPointerException) {
            binding.tableAddFilterQueryNothing.visibility = View.VISIBLE
            binding.tableAddFilterQuerySomething.visibility = View.GONE
        }
        // 검색 기록 채우기
        historyHashMap.keys.forEach { item ->
            val view = TableAddFilterHistoryItemView(this, item, historyHashMap[item]!!)
            queryHistoryList.addView(view, 0)
            // 검색 기록 뷰 클릭 리스너 설정
            view.setOnClickListener {
                sharedPreferences.edit {
                    this.putString("filter_query_text", item)
                    this.putString("filter_query_field", historyHashMap[item]!!)
                }
                setResult(RESULT_OK)
                finish()
                // 오른쪽으로 퇴장, 새 창은 fade in
                overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
            }
        }


        // 기존 검색하던 검색어 가져오기
        try {
            queryEditText.setText(sharedPreferences.getString("filter_query_text", null)!!)
            queryEditText.setSelection(queryEditText.length())
            val field = sharedPreferences.getString("filter_query_field", null)!!
            binding.tableAddFilterQueryRadio.children.forEach { child ->
                if((child as RadioButton).text.toString() == field) {
                    child.isChecked = true
                }
            }
        } catch (n : NullPointerException) {
            (binding.tableAddFilterQueryRadio.get(0) as RadioButton).isChecked = true
        }

        // editText 에 포커스, 키보드 올리기
        queryEditText.requestFocus()
        window.setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(queryEditText,InputMethodManager.SHOW_IMPLICIT)


        // 뒤로가기 누르면 키보드 숨기고, focus 제거하기
        queryEditText.set(object : CustomEditTextForQuery.HideKeyboard {
            override fun hide() {
                imm.hideSoftInputFromWindow(queryEditText.windowToken,0)
            }
        })

        // 우상단 X 버튼 (editText 클리어)
        binding.tableAddServerLectureClearButton.setOnClickListener {
            queryEditText.setText("")
        }

        // 좌상단 뒤로가기 버튼
        binding.tableAddServerLectureCloseButton.setOnClickListener {
            setResult(RESULT_CANCELED)
            finish()
            // 오른쪽으로 퇴장, 새 창은 fade in
            overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
        }

        // 검색 기록 삭제 버튼
        binding.tableAddFilterDeleteHistory.setOnClickListener {
            while(queryHistoryList.childCount==1) {
                queryHistoryList.removeViewAt(0)
            }
            sharedPreferences.edit {
                this.remove("query_history")
            }
            historyHashMap.clear()
            binding.tableAddFilterQueryNothing.visibility = View.VISIBLE
            binding.tableAddFilterQuerySomething.visibility = View.GONE
        }

        // editText 에서 엔터 키 입력했을 때
       val radioGroup = binding.tableAddFilterQueryRadio
        queryEditText.setOnKeyListener(object : View.OnKeyListener {
            override fun onKey(p0: View?, p1: Int, p2: KeyEvent?): Boolean {
                if(p1==KeyEvent.KEYCODE_ENTER) {
                    if(queryEditText.text.isEmpty()) return true
                    val field = findViewById<RadioButton>(radioGroup.checkedRadioButtonId).text.toString()
                    historyHashMap[queryEditText.text.toString()] = field

                    val params = mutableMapOf<Any?, Any?>()
                    historyHashMap.keys.forEach { key ->
                        params[key] = historyHashMap[key] }
                    val jsonObject = JSONObject(params).toString()
                    sharedPreferences.edit {
                        this.putString("query_history", jsonObject)
                        this.putString("filter_query_text", queryEditText.text.toString())
                        this.putString("filter_query_field", field)
                    }
                    setResult(RESULT_OK)
                    finish()
                    // 오른쪽으로 퇴장, 새 창은 fade in
                    overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
                }
                return false
            }
        })

    }

    override fun onBackPressed() {
        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        imm.hideSoftInputFromWindow(binding.tableAddFilterQueryEdittext.windowToken, 0)
        setResult(RESULT_CANCELED)
        finish()
        // 오른쪽으로 퇴장, 새 창은 fade in
        overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
    }
}

