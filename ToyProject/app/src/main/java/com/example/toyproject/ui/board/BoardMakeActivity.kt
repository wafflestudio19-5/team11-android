package com.example.toyproject.ui.board

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.os.PersistableBundle
import android.text.Editable
import android.text.TextWatcher
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityBoardMakeBinding
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class BoardMakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardMakeBinding
    private val viewModel: BoardMakeViewModel by viewModels()

    private var letAnonymous: Boolean = true
    private var boardName: String = ""
    private var boardDesc: String = ""
    private var error : String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.backButton.setOnClickListener {
            finish()
        }

        binding.newBoardTitle.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                boardName = binding.newBoardTitle.text.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })

        binding.newBoardDescription.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                boardDesc = binding.newBoardDescription.text.toString()
            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
        })



        binding.apply{
            makeBoardButton.setOnClickListener {
                if(boardName == ""){

                    Toast.makeText(this@BoardMakeActivity, "이름을 입력해주세요.", Toast.LENGTH_LONG).show()

                } else {

                    viewModel.createBoard(
                        boardName,
                        boardDesc,
                        letAnonymous
                    )

                    Handler(Looper.getMainLooper()).postDelayed({if(error == "error") {
                        Toast.makeText(this@BoardMakeActivity, "중복된 이름의 게시판이 있습니다.", Toast.LENGTH_LONG).show()
                    }
                    else {
                        Timber.d("error: " + error)
                        finish()
                    }}, 1000)


                    viewModel.error.observe(this@BoardMakeActivity, {
                        error = it
                    })




                }
            }
        }

        binding.switch1.setOnCheckedChangeListener { _, isChecked ->
            letAnonymous = isChecked
        }

        binding.backButton.setOnClickListener {
            finish()
        }



    }


}