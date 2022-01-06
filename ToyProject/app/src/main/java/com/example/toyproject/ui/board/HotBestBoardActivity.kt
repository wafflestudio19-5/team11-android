package com.example.toyproject.ui.board

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ActivityHotBestBoardBinding
import com.example.toyproject.network.dto.MyArticle
import com.example.toyproject.ui.article.ArticleActivity
import com.example.toyproject.ui.article.ArticleMakeActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class HotBestBoardActivity: AppCompatActivity() {

    private lateinit var binding: ActivityHotBestBoardBinding
    private val viewModel: HotBestBoardViewModel by viewModels()

    private lateinit var boardAdapter: HotBestBoardAdapter
    private lateinit var boardLayoutManager: LinearLayoutManager

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityHotBestBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardAdapter = HotBestBoardAdapter()
        boardLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewArticle.apply {
            adapter = boardAdapter
            layoutManager= boardLayoutManager
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        binding.boardName.text = intent.getStringExtra("board_name")

        viewModel.listSize.observe(this, {
            if(it==0) {
                binding.noArticleView.visibility = View.VISIBLE
                binding.recyclerViewArticle.visibility = View.GONE
            }
        })

        binding.articleFullMoreButton.setOnClickListener {
            val array = arrayOf("새로고침")
            val builder = AlertDialog.Builder(this)
            builder.setItems(array) {a, which ->
                val selected = array[which]
                // TODO (다른 선택지들)
                when(selected){
                    "새로고침" -> {
                        Handler(Looper.getMainLooper()).postDelayed({
                            boardAdapter.resetArticles()
                            page = 0
                            if (page == 0 && intent.getStringExtra("board_interest") == "hot") viewModel.getArticleList(
                                page++,
                                20,
                                "hot"
                            )
                            else if (page == 0 && intent.getStringExtra("board_interest") == "best") viewModel.getArticleList(
                                page++,
                                20,
                                "best"
                            )
                        }, 1500)
                    }
                }
                //Toast.makeText(this, selected, Toast.LENGTH_SHORT).show()
            }
            val dialog = builder.create()
            dialog.show()
        }

        boardAdapter.setItemClickListener(object: HotBestBoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: MyArticle, position: Int) {
                Intent(this@HotBestBoardActivity, ArticleActivity::class.java).apply{
                    putExtra("board_id", data.board_id)
                    putExtra("article_id", data.id)
                    putExtra("board_name", intent.getStringExtra("board_name"))
                }.run{startActivity(this)}
            }
        })

        if(intent.getStringExtra("board_interest")=="best") binding.boardAnnouncement.text= "공감을 50개 이상 받은 게시물 랭킹입니다."


        if(page==0 && intent.getStringExtra("board_interest") == "hot") viewModel.getArticleList(page++, 20, "hot")
        else if(page==0 && intent.getStringExtra("board_interest") == "best") viewModel.getArticleList(page++, 20, "best")

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

                    when(intent.getStringExtra("board_interest")){
                        "hot" -> viewModel.getArticleList(20*(page++), 20, "hot")
                        "best" -> viewModel.getArticleList(20*(page++), 20, "best")
                        else -> {}
                    }
                }
            }
        })

    }

}