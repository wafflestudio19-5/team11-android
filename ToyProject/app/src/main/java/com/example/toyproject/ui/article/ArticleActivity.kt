package com.example.toyproject.ui.article

import android.app.Dialog
import android.content.Context
import android.content.DialogInterface
import android.graphics.Color
import android.graphics.Point
import android.os.Build
import android.os.Bundle
import android.view.*
import android.view.inputmethod.InputMethodManager
import android.widget.ScrollView
import android.widget.TextView
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

    private var subCommentON : Boolean = false

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

        var is_mine = false
        viewModel.result.observe(this, {
            is_mine = it.is_mine
            binding.articleFullWriterNickname.text = it.user_nickname
            binding.articleFullWrittenTime.text = it.created_at
            binding.articleFullTitle.text = it.title
            binding.articleFullContent.text = it.text
            binding.articleFullLikeNumber.text = it.like_count.toString()
            binding.articleFullCommentNumber.text = it.comment_count.toString()
            binding.articleFullLikeNumber.text = it.like_count.toString()
            binding.articleFullCommentNumber.text = it.comment_count.toString()
            binding.articleFullScrapNumber.text = it.scrap_count.toString()
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
                if(subCommentON) {
                    // 대댓글이면, parent 설정해서 param 만들고, 빨갛게 했던 거 하얗게 되돌리기
                    val param = CommentCreate(commentParent, binding.commentEditText.text.toString(), binding.commentAnonymousCheckBox.isChecked)
                    viewModel.addComment(boardId, articleId, param)
                    binding.commentEditText.text.clear()
                    binding.commentView[commentAdapter.getItemPosition(commentParent)].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    commentParent = 0
                }
                else {
                    // 그냥 댓글이면 평소처럼
                    val param = CommentCreate(0, binding.commentEditText.text.toString(), binding.commentAnonymousCheckBox.isChecked)
                    viewModel.addComment(boardId, articleId, param)
                    binding.commentEditText.text.clear()
                }

            }
        }

        // 댓글 좋아요 눌렀을때 좋아요 수 refresh 및 에러 시 응답 출력
        viewModel.likeResult.observe(this, {
            when (it) {
                "success_article" -> {
                    viewModel.getArticle(boardId, articleId)
                    Toast.makeText(this, "이 글을 공감하였습니다", Toast.LENGTH_SHORT).show()
                }
                "success_comment" -> {
                    viewModel.getArticle(boardId, articleId)
                    Toast.makeText(this, "이 댓글을 공감하였습니다", Toast.LENGTH_SHORT).show()
                }
                "success_scrap" -> {
                    viewModel.getArticle(boardId, articleId)
                    Toast.makeText(this, "이 글을 스크랩하였습니다", Toast.LENGTH_SHORT).show()
                }
                else -> {
                    // 에러 메시지 출력
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        })
        // 댓글 창의 itemView 각각과의 소통 인터페이스
        commentAdapter.setItemClickListener(object: CommentAdapter.OnCommentClickListener{
            // 대댓글 작성 버튼 누를때
            override fun onCommentClick(parent : Int) {
                val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                    .setTitle("대댓글을 작성하시겠습니까?")
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
                        subCommentON = true
                    }
                val dialog = mBuilder.create()
                dialog.show()
            }
            // 댓글&대댓글 좋아요 누를 때
            override fun onCommentLikeClick(id : Int) {
                if(id==-1) {
                    Toast.makeText(this@ArticleActivity, "내가 쓴 댓글은 공감할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
                else {
                    val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                        .setTitle("이 댓글을 공감하시겠습니까?")
                        .setNegativeButton("취소") { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }
                        .setPositiveButton("확인") { dialogInterface, i ->
                            viewModel.likeComment(id)
                        }
                    val dialog = mBuilder.create()
                    dialog.show()
                }
            }
            // ... 버튼 누를 때
            override fun onCommentMore(id: Int, mine: Boolean, sub: Boolean) {
                val array = if (!mine) {
                    if(sub) {
                        arrayOf("쪽지 보내기", "신고")
                    } else {
                        arrayOf("대댓글 알림 켜기", "쪽지 보내기", "신고")
                    }
                } else {
                    if(sub) {
                        arrayOf("삭제")
                    } else {
                        arrayOf("대댓글 알림 켜기", "삭제")
                    }
                }
                val builder = AlertDialog.Builder(this@ArticleActivity)
                builder.setItems(array) { _, which ->
                    val selected = array[which]
                    // TODO
                }
                val dialog = builder.create()
                dialog.show()
            }
        })
        // 왼쪽 상단 뒤로가기 버튼
        binding.articleBackButton.setOnClickListener {
            finish()
        }
        // 오른쪽 상단 ... 버튼
        binding.articleFullMoreButton.setOnClickListener {
            val array = if(is_mine) {
                arrayOf("새로고침", "삭제", "URL 공유")
            } else {
                arrayOf("새로고침", "쪽지 보내기", "신고", "URL 공유")
            }
            val builder = AlertDialog.Builder(this)
            builder.setItems(array) {a, which ->
                val selected = array[which]
                // TODO
                Toast.makeText(this, selected, Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()
        }
        // 게시글 좋아요 버튼
        binding.articleFullLikeButton.setOnClickListener {
            val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                .setTitle("이 글을 공감하시겠습니까?")
                .setNegativeButton("취소") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("확인") { dialogInterface, i ->
                    viewModel.likeArticle(articleId)
                }
            val dialog = mBuilder.create()
            dialog.show()
        }
        binding.articleFullScrapButton.setOnClickListener {
            val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                .setTitle("이 글을 스크랩하시겠습니까?")
                .setNegativeButton("취소") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("확인") { dialogInterface, i ->
                    viewModel.scrapArticle(articleId)
                }
            val dialog = mBuilder.create()
            dialog.show()
        }
    }
    // 대댓글 작성중에 뒤로가기 누르면 취소(parent 하얗게 되돌리기)
    override fun dispatchKeyEvent(event: KeyEvent?): Boolean {
        if(subCommentON) {
            if(event?.keyCode==KeyEvent.KEYCODE_BACK) {
                binding.commentView[commentAdapter.getItemPosition(commentParent)].setBackgroundColor(Color.parseColor("#FFFFFF"))
                commentParent = 0
                subCommentON = false
            }
        }
        return super.dispatchKeyEvent(event)
    }
}