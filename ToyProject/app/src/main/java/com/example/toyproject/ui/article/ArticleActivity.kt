package com.example.toyproject.ui.article

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import com.example.toyproject.databinding.ActivityArticleBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private val viewModel : ArticleViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.articleFullBoardName.text = intent.getStringExtra("board_name").toString()

        val boardId = intent.getIntExtra("board_id", 0)
        val articleId = intent.getIntExtra("article_id", 0)
        viewModel.getArticle(boardId, articleId)

        viewModel.result.observe(this, {
            binding.articleFullWriterNickname.text = it.user_nickname
            binding.articleFullWrittenTime.text = it.created_at
            binding.articleFullTitle.text = it.title
            binding.articleFullContent.text = it.text
            binding.articleFullLikeNumber.text = it.like_count.toString()
            binding.articleFullCommentNumber.text = it.comment_count.toString()
//            binding.articleFullScrapNumber.text = it

        })

    }
}