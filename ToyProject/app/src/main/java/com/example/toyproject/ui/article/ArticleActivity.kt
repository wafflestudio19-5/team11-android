package com.example.toyproject.ui.article

import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.os.Bundle
import android.os.Handler
import android.os.Looper
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
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityArticleBinding
import com.example.toyproject.network.dto.CommentCreate
import com.example.toyproject.ui.board.BoardActivity
import com.example.toyproject.ui.board.SwipeDismissBaseActivity
import com.example.toyproject.ui.main.MainActivity
import dagger.hilt.android.AndroidEntryPoint
import org.w3c.dom.Text
import timber.log.Timber
import java.net.URL
import java.util.*


// 오른쪽으로 밀어서 닫을 수 있는 액티비티. TODO : 애니메이션 적용
// AppCompatActivity() 지워주고 SwipeDismissBaseActivity()를 상속시켜 주면 된다.
@AndroidEntryPoint
class ArticleActivity : SwipeDismissBaseActivity() {

    private lateinit var binding: ActivityArticleBinding
    private val viewModel : ArticleViewModel by viewModels()

    private lateinit var articleImageAdapter: ArticleImageAdapter
    private lateinit var articleImageLayoutManager: LinearLayoutManager
    private lateinit var commentAdapter: CommentAdapter
    private lateinit var commentLayoutManager: LinearLayoutManager

    // 대댓글 작성 시 parent
    private var commentParent : Int= 0
    // 대댓글 작성 모드
    private var subCommentON : Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleImageAdapter = ArticleImageAdapter(this)
        articleImageLayoutManager = LinearLayoutManager(this).also { it.orientation = LinearLayoutManager.HORIZONTAL }
        commentAdapter = CommentAdapter(this)
        commentLayoutManager = LinearLayoutManager(this)
        binding.articleImageView.apply {
            adapter = articleImageAdapter
            layoutManager = articleImageLayoutManager
        }
        binding.commentView.apply {
            adapter = commentAdapter
            layoutManager = commentLayoutManager
        }

        binding.articleFullBoardName.text = intent.getStringExtra("board_name").toString()
        val boardId = intent.getIntExtra("board_id", 0)
        val articleId = intent.getIntExtra("article_id", 0)
        viewModel.getArticle(boardId, articleId)

        var isMine = false
        var hasScraped = false
        // 게시글 내용물들 다 불러왔으면 채워넣기
        viewModel.result.observe(this, {
            //게시글 작성자 프로필 이미지 불러오기
            val credentials: BasicAWSCredentials
            val key = getString(R.string.AWS_ACCESS_KEY_ID)
            val secret = getString(R.string.AWS_SECRET_ACCESS_KEY)
            if(it.user_image!=""){
                val objectKey = it.user_image.substring(52)
                credentials = BasicAWSCredentials(key, secret)
                val s3 = AmazonS3Client(
                    credentials, Region.getRegion(
                        Regions.AP_NORTHEAST_2
                    )
                )
                val expires = Date(Date().getTime() + 1000 * 60) // 1 minute to expire
                val generatePresignedUrlRequest =
                    GeneratePresignedUrlRequest("team11bucket", objectKey) //generating the signatured url
                generatePresignedUrlRequest.expiration = expires
                val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
                Glide.with(this)
                    .setDefaultRequestOptions(
                        RequestOptions()
                            .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                            .placeholder(R.drawable.anonymous_photo)
                            .fitCenter()
                    )
                    .load(url.toString())
                    .into(findViewById(R.id.article_full_writer_profile))
            }

            isMine = it.is_mine
            hasScraped = it.has_scraped
            binding.articleFullWriterNickname.text = it.user_nickname
            binding.articleFullWrittenTime.text = it.created_at
            binding.articleFullTitle.text = it.title
            binding.articleFullContent.text = it.text
            binding.articleFullLikeNumber.text = it.like_count.toString()
            binding.articleFullCommentNumber.text = it.comment_count.toString()
            binding.articleFullLikeNumber.text = it.like_count.toString()
            binding.articleFullCommentNumber.text = it.comment_count.toString()
            binding.articleFullScrapNumber.text = it.scrap_count.toString()
            // binding.articleFullScrapButton.setBackgroundResource(R.drawable.background_gray) // TODO
            // binding.articleFullScrapButton.background = R.drawable.base
            commentAdapter.setComments(it.comments)
            commentAdapter.notifyDataSetChanged()
            articleImageAdapter.setImages(it.images)
            articleImageAdapter.notifyDataSetChanged()
            // 댓글을 새로 쓰면 스크롤 맨 위로. (잘 작동 안하는듯)
            if(viewModel.reload) binding.nestedScroll.fullScroll(ScrollView.FOCUS_UP)
        })

        // 댓글 입력 버튼 누를 때
        binding.commentButton.setOnClickListener {
            // 키보드 내려주기
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(binding.commentButton.windowToken, 0)

            // 입력이 없으면 입력해 주라고 알림 띄우고, 입력이 있으면 서버에 전송(및 재로드)
            if(binding.commentEditText.text.toString() == "") {
                Toast.makeText(this, "댓글을 입력해주세요", Toast.LENGTH_SHORT).show()
            }
            else {
                // 댓글이면 commentParent = 0, 대댓글이면 commentParent = parent
                if(subCommentON) {
                    // 대댓글이면, parent 따로 입력해서 param 만들고, viewModel 로 정보 전달
                    val param = CommentCreate(commentParent, binding.commentEditText.text.toString(), binding.commentAnonymousCheckBox.isChecked)
                    viewModel.addComment(boardId, articleId, param)
                    binding.commentEditText.text.clear()
                    binding.commentView[commentAdapter.getItemPosition(commentParent)].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    commentParent = 0
                }
                else {
                    // 그냥 댓글이면 평소처럼.
                    val param = CommentCreate(0, binding.commentEditText.text.toString(), binding.commentAnonymousCheckBox.isChecked)
                    viewModel.addComment(boardId, articleId, param)
                    binding.commentEditText.text.clear()
                }

            }
        }

        // 댓글 및 게시물 좋아요 눌렀을때 좋아요 수 refresh 및 에러 시 응답 출력
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
                else -> {
                    // 에러 메시지 출력
                    Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
                }
            }
        })
        // 스크랩 결과 띄우기
        viewModel.scrapResult.observe(this, {
            viewModel.getArticle(boardId, articleId)
            Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
        })

        // 댓글 창의 itemView 각각과의 소통 인터페이스
        commentAdapter.setItemClickListener(object: CommentAdapter.OnCommentClickListener{
            // 대댓글 작성 버튼 누를때
            override fun onCommentClick(parent : Int) {
                val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                    .setMessage("대댓글을 작성하시겠습니까?")
                   .setNegativeButton("취소") { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }
                    .setPositiveButton("확인") { dialogInterface, i ->
                        // 원래 빨갰던 댓글은 하얗게 되돌리고,대댓글의 parent 가 될 댓글을 빨갛게 만들고, 키보드 올리기
                        binding.commentView[commentAdapter.getItemPosition(commentParent)].setBackgroundColor(Color.parseColor("#FFFFFF"))
                        commentParent = parent
                        binding.commentView[commentAdapter.getItemPosition(parent)].setBackgroundColor(Color.parseColor("#FFE0E0"))
                        val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
                        imm.toggleSoftInput(InputMethodManager.SHOW_FORCED, InputMethodManager.HIDE_IMPLICIT_ONLY)
                        binding.commentEditText.requestFocus()
                        subCommentON = true
                    }
                val dialog = mBuilder.create()
                dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
                dialog.show()
            }
            // 댓글&대댓글 좋아요 누를 때
            override fun onCommentLikeClick(id : Int) {
                if(id==-1) {
                    Toast.makeText(this@ArticleActivity, "내가 쓴 댓글은 공감할 수 없습니다", Toast.LENGTH_SHORT).show()
                }
                else {
                    val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                        .setMessage("이 댓글을 공감하시겠습니까?")
                        .setNegativeButton("취소") { dialogInterface, i ->
                            dialogInterface.dismiss()
                        }
                        .setPositiveButton("확인") { dialogInterface, i ->
                            viewModel.likeComment(id)
                        }
                    val dialog = mBuilder.create()
                    dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
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
                    when(array[which]) {
                        "삭제" -> {
                            val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                                .setMessage("삭제하시겠습니까?")
                                .setNegativeButton("취소") { dialogInterface, i ->
                                    dialogInterface.dismiss()
                                }
                                .setPositiveButton("확인") { dialogInterface, i ->
                                    viewModel.deleteComment(boardId, articleId, id)
                                }
                            val dialog = mBuilder.create()
                            dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
                            dialog.show()
                        }
                        // TODO (다른 선택지들)
                    }
                }
                val dialog = builder.create()
                dialog.show()
            }
        })
        // 왼쪽 상단 뒤로가기 버튼
        binding.articleBackButton.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_fade_away, R.anim.slide_out_left)
        }
        // 오른쪽 상단 ... 버튼
        binding.articleFullMoreButton.setOnClickListener {
            val array = if(isMine) {
                arrayOf("새로고침", "삭제", "URL 공유")
            } else {
                arrayOf("새로고침", "쪽지 보내기", "신고", "URL 공유")
            }
            val builder = AlertDialog.Builder(this)
            builder.setItems(array) {a, which ->
                val selected = array[which]
                // TODO (다른 선택지들)
                when(selected){
                    "삭제" -> {
                        val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                            .setMessage("삭제하시겠습니까?")
                            .setNegativeButton("취소") { dialogInterface, i ->
                                dialogInterface.dismiss()
                            }
                            .setPositiveButton("확인") { dialogInterface, i ->
                                viewModel.deleteArticle(boardId, articleId)
                            }
                        val dialog = mBuilder.create()
                        dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
                        dialog.show()
                    }
                }
            }
            val dialog = builder.create()
            dialog.show()
        }
        // 게시글 좋아요 버튼
        binding.articleFullLikeButton.setOnClickListener {
            if(isMine) {
                Toast.makeText(this, "자신의 게시글은 공감할 수 없습니다.", Toast.LENGTH_SHORT).show()
            }
            else {
                val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                    .setMessage("이 글을 공감하시겠습니까?")
                    .setNegativeButton("취소") { dialogInterface, i ->
                        dialogInterface.dismiss()
                    }
                    .setPositiveButton("확인") { dialogInterface, i ->
                        viewModel.likeArticle(articleId)
                    }
                val dialog = mBuilder.create()
                dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
                dialog.show()
            }
        }
        // 게시글 스크랩 버튼
        // TODO : "스크랩 취소" 아이콘 만들고, 그걸로 구별해야 한다.
        binding.articleFullScrapButton.setOnClickListener {
            lateinit var title : String
            if(!hasScraped) title = "이 글을 스크랩하시겠습니까?"
            else title = "스크랩을 취소하시겠습니까"
            val mBuilder = AlertDialog.Builder(this@ArticleActivity)
                .setMessage(title)
                .setNegativeButton("취소") { dialogInterface, i ->
                    dialogInterface.dismiss()
                }
                .setPositiveButton("확인") { dialogInterface, i ->
                    viewModel.scrapArticle(articleId)
                }
            val dialog = mBuilder.create()
            dialog.findViewById<TextView>(android.R.id.message)?.textSize = 13f
            dialog.show()
        }
        // 댓글 삭제 결과 출력
        viewModel.deleteResult.observe(this, {
            viewModel.getArticle(boardId, articleId)
            if(it!="success") {
                // 에러 출력
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })
        // 게시글 삭제 결과 출력
        viewModel.deleteArticleResult.observe(this, {
            if(it=="success") {
                // 잘 삭제 됐으면 액티비티 종료, 그리고 BoardActvity 가 새로고침 할 수 있게 result 설정
                setResult(RESULT_OK)
                finish()
            }
            else {
                // 삭제 과정에 오류가 있으면 출력
                Toast.makeText(this, it, Toast.LENGTH_SHORT).show()
            }
        })

        // 대댓글 작성중에 뒤로가기 누르면 취소(parent 하얗게 되돌리기)
        binding.commentEditText.caller(object : CustomEditText.CustomEditToActivity {
            override fun call() {
                if(subCommentON) {
                    binding.commentView[commentAdapter.getItemPosition(commentParent)].setBackgroundColor(Color.parseColor("#FFFFFF"))
                    commentParent = 0
                    subCommentON = false
                }
            }
        })
    }

    override fun onBackPressed() {
        super.onBackPressed()
        finish()
        overridePendingTransition(R.anim.slide_fade_away, R.anim.slide_out_left)
    }
}