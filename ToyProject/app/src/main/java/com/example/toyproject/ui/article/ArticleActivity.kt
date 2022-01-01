package com.example.toyproject.ui.article

import android.content.Context
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.databinding.ActivityArticleBinding
import com.example.toyproject.network.dto.CommentCreate
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
            
            // 댓글을 새로 쓰면 스크롤 맨 위로.
            if(viewModel.reload) binding.nestedScroll.fullScroll(ScrollView.FOCUS_UP)
        })

        // 댓글 입력 버튼 누를 때
        binding.commentButton.setOnClickListener {
            // 키보드 내려주기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.commentButton.windowToken, 0)

            // 입력이 없으면 입력해 주라고 하고, 입력이 있으면 서버에 전송(및 재로드)
            if(binding.commentEditText.text.toString() == "") {
                Toast.makeText(this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                val param = CommentCreate(0, binding.commentEditText.text.toString(), binding.commentAnonymousCheckBox.isChecked)
                viewModel.addComment(boardId, articleId, param)
                binding.commentEditText.text.clear()
            }
        }

    }
}