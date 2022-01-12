package com.example.toyproject.ui.board

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ActivityBoardBinding
import com.example.toyproject.network.dto.Article
import com.example.toyproject.ui.article.ArticleActivity
import com.example.toyproject.ui.article.ArticleMakeActivity
import com.example.toyproject.ui.article.ArticleSearchActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class BoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityBoardBinding
    private val viewModel: BoardViewModel by viewModels()

    private lateinit var boardAdapter: BoardAdapter
    private lateinit var boardLayoutManager: LinearLayoutManager

    private var page = 0
    private var isMine: Boolean = false
    private var isFavorite: Boolean = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // BoardActivity 는 오른쪽에서 나오고, 배경은 까만색으로 fade out
        overridePendingTransition(R.anim.slide_in_right, R.anim.slide_hold_fade_out)


        boardAdapter = BoardAdapter()
        boardLayoutManager = LinearLayoutManager(this)

        // 뒤로가기 화살표 눌렀을 때 슬라이드 애니메이션 적용 + finish()
        binding.backArrow.setOnClickListener {
            finish()
            overridePendingTransition(R.anim.slide_nothing, R.anim.slide_out_left)
        }

        binding.recyclerViewArticle.apply {
            adapter = boardAdapter
            layoutManager= boardLayoutManager
        }

        viewModel.listSize.observe(this, {
            if(it==0) {
                binding.noArticleView.visibility = View.VISIBLE
                binding.recyclerViewArticle.visibility = View.GONE
            }
        })

        binding.boardName.text = intent.getStringExtra("board_name")
        viewModel.getBoardInfo(intent.getIntExtra("board_id", 0))

        viewModel.isMine.observe(this, {
            isMine = it
        })

        viewModel.isFavorite.observe(this, {
            isFavorite = it
        })

        viewModel.university.observe(this,{
            binding.university.text = it
        })

        binding.refreshLayout.setOnRefreshListener {
            page = 0
            boardAdapter.resetArticles()
            Handler(Looper.getMainLooper()).postDelayed({


                if(page==0) viewModel.getArticleList(intent.getIntExtra("board_id", 0), page++, 20) },
                100)
            binding.refreshLayout.isRefreshing = false
        }

        binding.refreshLayout.setColorSchemeResources(R.color.PrimaryVariant)

        binding.searchIcon.setOnClickListener {
            Intent(this@BoardActivity, ArticleSearchActivity::class.java).apply{
                putExtra("board_id", intent.getIntExtra("board_id", 0))
            }.run{startActivity(this)}
        }

        // "RESULT_OK" : 새로고침 (글 삭제, 새 글 작성)
        val resultListener =
            registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
                if(it.resultCode == RESULT_OK) {

                    page = 0
                    boardAdapter.resetArticles()
                    Handler(Looper.getMainLooper()).postDelayed({
                        if(page==0) viewModel.getArticleList(intent.getIntExtra("board_id", 0), page++, 20) },
                        100)
                    binding.refreshLayout.isRefreshing = false

                }
            }

        binding.articleFullMoreButton.setOnClickListener {
            val array = if(isMine) {
                arrayOf("새로고침", "글 쓰기", "게시판 삭제")
            } else {
                arrayOf("새로고침", "글 쓰기", "즐겨찾기에 추가")
            }
            val builder = AlertDialog.Builder(this)
            builder.setItems(array) {a, which ->
                val selected = array[which]
                // TODO (다른 선택지들)
                when(selected){
                    "글 쓰기" -> {
                        Intent(this@BoardActivity, ArticleMakeActivity::class.java).apply{
                            putExtra("board_id", intent.getIntExtra("board_id", 0))
                            putExtra("board_name", intent.getStringExtra("board_name"))
                        }.run{
                            resultListener.launch(this)
                        }
                    }
                    "게시판 삭제" -> {
                        viewModel.deleteBoard(intent.getIntExtra("board_id", 0))
                        Handler(Looper.getMainLooper()).postDelayed({
                            setResult(RESULT_OK)
                            finish()
                        }, 1000)
                    }
                    "새로고침" -> {
                        Handler(Looper.getMainLooper()).postDelayed({boardAdapter.resetArticles()
                            page = 0
                            if(page==0) viewModel.getArticleList(intent.getIntExtra("board_id", 0), page++, 20) }, 1500)
                    }
                    "즐겨찾기에 추가" -> {
                        viewModel.addFavoriteBoard(intent.getIntExtra("board_id", 0))
                    }
                }
                Toast.makeText(this, selected, Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()
        }

        boardAdapter.setItemClickListener(object: BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Article, position: Int) {
                Intent(this@BoardActivity, ArticleActivity::class.java).apply{
                    putExtra("board_id", intent.getIntExtra("board_id", 0))
                    putExtra("article_id", data.id)
                    putExtra("board_name", intent.getStringExtra("board_name"))
                }.run{
                    resultListener.launch(this)
                }
            }
        })
        binding.makeArticleButton.setOnClickListener {
            Intent(this@BoardActivity, ArticleMakeActivity::class.java).apply{
                putExtra("board_id", intent.getIntExtra("board_id", 0))
                putExtra("board_name", intent.getStringExtra("board_name"))
            }.run{
                resultListener.launch(this)
            }
        }

        if(page==0) viewModel.getArticleList(intent.getIntExtra("board_id", 0), page++, 20)

        viewModel.articleList.observe(this, {
            boardAdapter.setArticles(it)
            boardAdapter.notifyItemRemoved((page-1)*20)
//            boardAdapter.notifyItemRangeInserted((page-1) * 20, boardAdapter.itemCount)
            boardAdapter.notifyDataSetChanged()
        })

        binding.recyclerViewArticle.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                if(!binding.recyclerViewArticle.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount){

                    //boardAdapter.deleteLoading()
//                    val runnable = Runnable {
//
//                        boardAdapter.notifyItemRemoved(itemTotalCount)
//                    }
//                    recyclerView.post(runnable)

                    viewModel.getArticleList(intent.getIntExtra("board_id", 0), 20*(page++), 20)
                }
            }
        })
    }

    override fun onBackPressed() {
        finish()
        // BoardActivity 는 오른쪽으로 퇴장, 새 창은 fade in
        overridePendingTransition(R.anim.slide_hold_fade_in, R.anim.slide_out_left)
    }
}