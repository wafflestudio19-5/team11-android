package com.example.toyproject.ui.article

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ActivityArticleSearchBinding
import com.example.toyproject.network.dto.Article
import com.example.toyproject.ui.board.ArticleSearchAdapter
import com.example.toyproject.ui.board.BoardAdapter
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ArticleSearchActivity : AppCompatActivity() {

    private lateinit var binding: ActivityArticleSearchBinding
    private val viewModel: ArticleSearchViewModel by viewModels()

    private lateinit var articleSearchAdapter: ArticleSearchAdapter
    private lateinit var articleLayoutManager: LinearLayoutManager

    private var page = 0
    private var searchKeyword: String = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityArticleSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)

        articleSearchAdapter = ArticleSearchAdapter()
        articleLayoutManager = LinearLayoutManager(this)

        binding.recyclerViewCareerBoard.apply{
            adapter = articleSearchAdapter
            layoutManager = articleLayoutManager
        }

        binding.backArrow.setOnClickListener {
            finish()
        }

        viewModel.listSize.observe(this, {
            if(it==0) {
                binding.linearLayout.visibility = View.VISIBLE
                binding.recyclerViewCareerBoard.visibility = View.GONE
                binding.textNoResult.text = "검색 결과가 없습니다"
            } else{
                binding.linearLayout.visibility = View.GONE
                binding.recyclerViewCareerBoard.visibility = View.VISIBLE
            }
        })

        articleSearchAdapter.setItemClickListener(object: ArticleSearchAdapter.OnItemClickListener{
            override fun onItemClick(v: View, data: Article, position: Int) {
                Intent(this@ArticleSearchActivity, ArticleActivity::class.java).apply{

                }.run{startActivity(this)}
            }
        })

        binding.articleSearchBar.addTextChangedListener(object: TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                searchKeyword = binding.articleSearchBar.text.toString()

                if(page==0) viewModel.getArticleList(intent.getIntExtra("board_id", 0), page++, 20, searchKeyword)

                viewModel.articleList.observe(this@ArticleSearchActivity, {
                    articleSearchAdapter.setArticles(it)
                    articleSearchAdapter.notifyItemRemoved((page-1)*20)
                    articleSearchAdapter.notifyDataSetChanged()
                })
            }

            override fun afterTextChanged(p0: Editable?) {



            }
        })

        binding.recyclerViewCareerBoard.addOnScrollListener(object : RecyclerView.OnScrollListener(){
            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int){
                super.onScrolled(recyclerView, dx, dy)

                val lastVisibleItemPosition =
                    (recyclerView.layoutManager as LinearLayoutManager?)!!.findLastCompletelyVisibleItemPosition()
                val itemTotalCount = recyclerView.adapter!!.itemCount-1

                if(!binding.recyclerViewCareerBoard.canScrollVertically(1) && lastVisibleItemPosition == itemTotalCount){

                    viewModel.getArticleList(intent.getIntExtra("board_id", 0), 20*(page++), 20, searchKeyword)
                }
            }
        })
    }
}