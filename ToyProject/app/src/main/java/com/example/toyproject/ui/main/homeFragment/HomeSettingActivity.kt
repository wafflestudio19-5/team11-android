package com.example.toyproject.ui.main.homeFragment

import android.content.SharedPreferences
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.edit
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityHomeSettingBinding
import dagger.hilt.android.AndroidEntryPoint
import org.json.JSONArray
import javax.inject.Inject

@AndroidEntryPoint
class HomeSettingActivity : AppCompatActivity() {

    @Inject
    lateinit var sharedPreferences: SharedPreferences

    private lateinit var binding : ActivityHomeSettingBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityHomeSettingBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        val defaultJsonArray  = JSONArray()
        for(i in 0..9) defaultJsonArray.put(true)
        val settingArr = sharedPreferences.getString("setting", defaultJsonArray.toString())
        val setting : ArrayList<String> = ArrayList()
        val arrJson = JSONArray(settingArr)
        for(i in 0 until arrJson.length()) {
            setting.add(arrJson.optString(i))
        }
        binding.homeSettingFavoriteCheckbox.isChecked = setting[0]=="true"
        binding.homeSettingIssueCheckbox.isChecked = setting[1]=="true"
        binding.homeSettingHotCheckbox.isChecked = setting[2]=="true"
        binding.homeSettingNewsCheckbox.isChecked = setting[3]=="true"
        binding.homeSettingCareerCheckbox.isChecked = setting[4]=="true"
        binding.homeSettingBookCheckbox.isChecked = setting[5]=="true"
        binding.homeSettingPromotionCheckbox.isChecked = setting[6]=="true"
        binding.homeSettingLectureCheckbox.isChecked = setting[7]=="true"
        binding.homeSettingEatCheckbox.isChecked = setting[8]=="true"
        binding.homeSettingQuestionCheckbox.isChecked = setting[9]=="true"


        // 상단 좌측 뒤로가기 버튼. 체크박스 정보를 모아 MainActivity 에게 전달
        binding.homeSettingBackButton.setOnClickListener {
            val settingList : BooleanArray = booleanArrayOf(
                binding.homeSettingFavoriteCheckbox.isChecked,
                binding.homeSettingIssueCheckbox.isChecked,
                binding.homeSettingHotCheckbox.isChecked,
                binding.homeSettingNewsCheckbox.isChecked,
                binding.homeSettingCareerCheckbox.isChecked,
                binding.homeSettingBookCheckbox.isChecked,
                binding.homeSettingPromotionCheckbox.isChecked,
                binding.homeSettingLectureCheckbox.isChecked,
                binding.homeSettingEatCheckbox.isChecked,
                binding.homeSettingQuestionCheckbox.isChecked
            )
            val jsonArray  = JSONArray()
            for(set in settingList) {
                jsonArray.put(set)
            }
            sharedPreferences.edit {
                putString("setting", jsonArray.toString())
            }
            setResult(RESULT_OK)
            finish()
        }

    }
    // 그냥 뒤로가기 버튼. 체크박스 정보를 모아 MainActivity 에게 전달
    override fun onBackPressed() {
        val settingList : BooleanArray = booleanArrayOf(
            binding.homeSettingFavoriteCheckbox.isChecked,
            binding.homeSettingIssueCheckbox.isChecked,
            binding.homeSettingHotCheckbox.isChecked,
            binding.homeSettingNewsCheckbox.isChecked,
            binding.homeSettingCareerCheckbox.isChecked,
            binding.homeSettingBookCheckbox.isChecked,
            binding.homeSettingPromotionCheckbox.isChecked,
            binding.homeSettingLectureCheckbox.isChecked,
            binding.homeSettingEatCheckbox.isChecked,
            binding.homeSettingQuestionCheckbox.isChecked
        )
        val jsonArray  = JSONArray()
        for(set in settingList) {
            jsonArray.put(set)
        }
        sharedPreferences.edit {
            putString("setting", jsonArray.toString())
        }
        setResult(RESULT_OK)
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
        finish()
    }

}