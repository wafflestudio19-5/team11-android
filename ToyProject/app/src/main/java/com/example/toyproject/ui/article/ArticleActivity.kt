package com.example.toyproject.ui.article

import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.os.Bundle
import android.view.KeyEvent
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.get
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityArticleBinding
import com.example.toyproject.network.dto.Article
import com.example.toyproject.network.dto.Comment
import com.example.toyproject.network.dto.CommentCreate
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import timber.log.Timber

@AndroidEntryPoint
class ArticleActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleBinding
    private val viewModel : ArticleViewModel by viewModels()

    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentLayoutManager: LinearLayoutManager

    private var commentParent : Int= 0

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
            binding.articleFullLikeNumber.text = it.like_count.toString()
            binding.articleFullCommentNumber.text = it.comment_count.toString()
            binding.articleFullScrapNumber.text = it.scrap_count.toString()
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
                // 댓글이면 commentParent = 0, 대댓글이면 commentParent = parent
                val param = CommentCreate(commentParent, binding.commentEditText.text.toString(), binding.commentAnonymousCheckBox.isChecked)
                viewModel.addComment(boardId, articleId, param)
                binding.commentEditText.text.clear()
                binding.commentView[commentAdapter.getItemPosition(commentParent)].setBackgroundColor(Color.parseColor("#FFFFFF"))
                commentParent = 0
            }
        }

        // 대댓글 작성 버튼 누를때
        commentAdapter.setItemClickListener(object: CommentAdapter.OnCommentClickListener{
            override fun onCommentClick(parent : Int) {
                val dialog = LayoutInflater.from(this@ArticleActivity).inflate(R.layout.item_comment_sub_dialog, null)
                val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                    .setView(dialog)
                    .setNegativeButton("취소") { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }
                    .setPositiveButton("확인") { dialogInterface, i ->
                        // 원래 빨갰던 댓글은 하얗게 되돌리고,대댓글의 parent 가 될 댓글을 빨갛게 만들고, 키보드 올리기
                        binding.commentView[commentAdapter.getItemPosition(commentParent)].setBackgroundColor(Color.parseColor("#FFFFFF"))
                        commentParent = parent
                        binding.commentView[commentAdapter.getItemPosition(parent)].setBackgroundColor(Color.parseColor("#FFD2D2"))
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                        binding.commentEditText.requestFocus()
                    }
                mBuilder.show()
            }
        })

    }
    // 대댓글 작성중에 뒤로가기 누르면 취소(parent 하얗게 되돌리기)
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if(event?.keyCode==KeyEvent.KEYCODE_BACK) {
            binding.commentView[commentAdapter.getItemPosition(commentParent)].setBackgroundColor(Color.parseColor("#FFFFFF"))
            commentParent = 0
        }
        return super.dispatchKeyEvent(event)
    }
}