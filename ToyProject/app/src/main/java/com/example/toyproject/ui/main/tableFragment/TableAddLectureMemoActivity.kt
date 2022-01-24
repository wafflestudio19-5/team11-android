package com.example.toyproject.ui.main.tableFragment

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityTableAddLectureMemoBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class TableAddLectureMemoActivity : AppCompatActivity() {

    private lateinit var binding : ActivityTableAddLectureMemoBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // 전환 이펙트 : 실행할 때 아래에서 올라오도록
        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        binding = ActivityTableAddLectureMemoBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.tableAddLectureMemoEdittext.setText(intent.getStringExtra("memo"))

        binding.addLectureMemoButton.setOnClickListener {
            // 완료 버튼 누르면 작성한 editText 내용 전달
            val intent = Intent()
            intent.putExtra("memo", binding.tableAddLectureMemoEdittext.text.toString())
            setResult(RESULT_OK, intent)
            finish()
            // 끝낼 땐 아래로 내려가기
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
        }

        binding.tableAddLectureMemoCloseButton.setOnClickListener {
            onBackPressed()
        }
    }

    override fun onBackPressed() {
        setResult(RESULT_CANCELED)
        finish()
        // 끝낼 땐 아래로 내려가기
        overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_up)
    }
}