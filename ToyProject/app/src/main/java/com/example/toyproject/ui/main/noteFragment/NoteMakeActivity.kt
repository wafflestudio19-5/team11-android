package com.example.toyproject.ui.main.noteFragment

import android.os.Bundle
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityNoteMakeBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class NoteMakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityNoteMakeBinding
    private val viewModel: NoteMakeViewModel by viewModels()

    private var messageContent: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityNoteMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        overridePendingTransition(R.anim.slide_in_down, R.anim.slide_nothing)

        binding.noteText.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun afterTextChanged(p0: Editable?) {
                messageContent = binding.noteText.text.toString()
            }
        })

        binding.apply{
            makeNoteButton.setOnClickListener{
                if(messageContent == ""){
                    Toast.makeText(this@NoteMakeActivity, "내용을 입력해주세요.", Toast.LENGTH_LONG).show()
                } else {
                    if(intent.getIntExtra("id", -1)== -1) viewModel.createMessage(intent.getIntExtra("message_room_id", 0), messageContent)
                    else {
                        if(intent.getStringExtra("from") == "comment") viewModel.createStartCommentMessage(intent.getIntExtra("id", 0), messageContent)
                        else viewModel.createStartArticleMessage(intent.getIntExtra("id", 0), messageContent)
                    }
                    finish()
                }
            }
        }

        binding.backButton.setOnClickListener {
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