package com.example.toyproject.ui.main.homeFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemHomeFragmentFavoriteBinding
import com.example.toyproject.network.dto.Article
import com.example.toyproject.network.dto.Board
import com.example.toyproject.ui.board.BoardAdapter
import timber.log.Timber

class HomeFavoriteRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var favoriteBoardIds : MutableList<Int> = mutableListOf()
    private var favoriteArticleIds : MutableList<Int> = mutableListOf()
    private var favoriteBoardNames : MutableList<String> = mutableListOf()
    private var titles : List<String> = listOf()

    inner class FavoriteViewHolder(val binding : ItemHomeFragmentFavoriteBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int {
        return titles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =ItemHomeFragmentFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val boardId = favoriteBoardIds[position]
        val articleId = favoriteArticleIds[position]
        val boardName = favoriteBoardNames[position]
        Timber.d(favoriteBoardIds.toString()+favoriteArticleIds.toString()+ favoriteBoardNames.toString()+titles.toString())

        when(holder) {
            is FavoriteViewHolder -> {
                holder.binding.homeFragmentFavoriteBoardName.text = boardName
                holder.binding.homeFragmentFavoriteContent.text = titles[position]

                holder.binding.apply {
                    root.setOnClickListener {
                        itemClickListener.onItemClick(root, boardId, articleId, boardName , position)
                    }
                }
            }
        }
    }
    fun setFavoriteBoard(titles : List<String>, board_id : MutableList<Int>, article_id : MutableList<Int>, board_name : MutableList<String>) {
        this.titles = titles
        this.favoriteBoardIds = board_id
        this.favoriteArticleIds = article_id
        this.favoriteBoardNames = board_name
        this.notifyDataSetChanged()
    }


    // 아이템 클릭 부분
    interface OnItemClickListener {
        fun onItemClick(v: View, board_id : Int, article_id : Int, board_name : String, position: Int)
    }
    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener : OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }
}