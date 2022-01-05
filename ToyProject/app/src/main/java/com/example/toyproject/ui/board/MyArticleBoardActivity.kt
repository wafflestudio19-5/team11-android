package com.example.toyproject.ui.board

import android.content.Intent
import android.os.Bundle
import android.os.PersistableBundle
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ActivityMyArticleBoardBinding
import com.example.toyproject.network.dto.Article
import com.example.toyproject.network.dto.MyArticle
import com.example.toyproject.ui.article.ArticleActivity
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MyArticleBoardActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMyArticleBoardBinding
    private val viewModel: MyArticleBoardViewModel by viewModels()

    private lateinit var boardAdapter: MyArticleBoardAdapter
    private lateinit var boardLayoutManager: LinearLayoutManager

    private var page = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMyArticleBoardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        boardAdapter = MyArticleBoardAdapter()
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

        boardAdapter.setItemClickListener(object: MyArticleBoardAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: MyArticle, position: Int) {
                Intent(this@MyArticleBoardActivity, ArticleActivity::class.java).apply{
                    putExtra("board_id", data.board_id)
                    putExtra("article_id", data.id)
                    putExtra("board_name", intent.getStringExtra("board_name"))
                }.run{startActivity(this)}
            }
        })


        if(page==0 && intent.getStringExtra("board_interest") == "article") viewModel.getArticleList(page++, 20, "article")
        else if(page==0 && intent.getStringExtra("board_interest") == "comment") viewModel.getArticleList(page++, 20, "comment")
        else if(page==0 && intent.getStringExtra("board_interest") == "scrap") viewModel.getArticleList(page++, 20, "scrap")

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
                        "article" -> viewModel.getArticleList(20*(page++), 20, "article")
                        "comment" -> viewModel.getArticleList(20*(page++), 20, "comment")
                        "scrap" -> viewModel.getArticleList(20*(page++), 20, "scrap")
                        else -> {}}

                }
            }
        })




    }


}