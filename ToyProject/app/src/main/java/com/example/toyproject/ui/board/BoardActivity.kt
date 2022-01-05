package com.example.toyproject.ui.board

import android.content.Intent
import android.os.Bundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardAdapter = BoardAdapter()
        boardLayoutManager = LinearLayoutManager(this)

        binding.backArrow.setOnClickListener {
            finish()
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

        binding.searchIcon.setOnClickListener {
            Intent(this@BoardActivity, ArticleSearchActivity::class.java).apply{
                putExtra("board_id", intent.getIntExtra("board_id", 0))
            }.run{startActivity(this)}
        }

        binding.boardName.text = intent.getStringExtra("board_name")

        boardAdapter.setItemClickListener(object: BoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Article, position: Int) {
                Intent(this@BoardActivity, ArticleActivity::class.java).apply{

                }.run{startActivity(this)}
            }
        })

        binding.makeArticleButton.setOnClickListener {
            Intent(this@BoardActivity, ArticleMakeActivity::class.java).apply{
                putExtra("board_id", intent.getIntExtra("board_id", 0))
            }.run{startActivity(this)}
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
}