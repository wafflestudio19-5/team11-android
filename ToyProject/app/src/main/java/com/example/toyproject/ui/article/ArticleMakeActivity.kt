package com.example.toyproject.ui.article

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.CheckBox
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityArticleMakeBinding
import com.example.toyproject.ui.board.BoardActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleMakeActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleMakeBinding
    private val viewModel: ArticleMakeViewModel by viewModels()

    private var articleTitleTemp: String = ""
    private var articleTextTemp: String = ""
    private var boardId: Int = 0
    private var isAnonymous: Boolean = false
    private var isQuestion: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleMakeBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardId = intent.getIntExtra("board_id", 0)

        binding.backButton.setOnClickListener {
            finish()
        }


        binding.articleTitle.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                articleTitleTemp = binding.articleTitle.text.toString()
            }
        })

        binding.articleText.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun afterTextChanged(p0: Editable?) {
                articleTextTemp = binding.articleText.text.toString()
            }
        })

        binding.apply{
            makeArticleButton.setOnClickListener{
                if(articleTitleTemp==""){
                    Toast.makeText(this@ArticleMakeActivity, "제목을 입력해주세요.", Toast.LENGTH_LONG).show()
                } else if(articleTextTemp==""){
                    Toast.makeText(this@ArticleMakeActivity, "내용을 입력해주세요.", Toast.LENGTH_LONG).show()
                } else{
                    viewModel.createArticle(boardId, articleTitleTemp, articleTextTemp, isAnonymous, isQuestion)
                    finish()
                }
            }
        }

    }

    fun onCheckboxClicked(view: View){
        if(view is CheckBox){
            val checked: Boolean = view.isChecked

            when(view.id){
                R.id.anonymous_checkbox -> {
                    isAnonymous = checked
                    Toast.makeText(this@ArticleMakeActivity, "익명 박스 클릭" + isAnonymous, Toast.LENGTH_LONG).show()

                }
                R.id.question_checkbox ->{
                    if(checked) binding.questionLayout.visibility = View.VISIBLE
                    isQuestion = checked
                    Toast.makeText(this@ArticleMakeActivity, "질문 박스 클릭" + isQuestion, Toast.LENGTH_LONG).show()

                }
            }
        }
    }


}