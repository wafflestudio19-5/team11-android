package com.example.toyproject.ui.article

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.databinding.ActivityArticleBinding
import com.example.toyproject.ui.board.BoardAdapter
import com.example.toyproject.ui.board.CommentAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private val viewModel : ArticleViewModel by viewModels()

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentLayoutManager: LinearLayoutManager

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        commentAdapter = CommentAdapter()
        commentLayoutManager = LinearLayoutManager(this)
        binding.commentView.apply {
            adapter = commentAdapter
            layoutManager = commentLayoutManager
        }

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
            commentAdapter.setComments(it.comments)
            commentAdapter.notifyDataSetChanged()

        })

    }
}