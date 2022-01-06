package com.example.toyproject.ui.main.homeFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemHomeFragmentFavoriteBinding
import com.example.toyproject.network.dto.Board

class HomeFavoriteRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var favorites : MutableList<Board> = mutableListOf()
    inner class FavoriteViewHolder(val binding : ItemHomeFragmentFavoriteBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int {
        return favorites.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =ItemHomeFragmentFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = favorites[position]
        when(holder) {
            is FavoriteViewHolder -> {
                holder.binding.homeFragmentFavoriteBoardName.text = "OO게시판"
                holder.binding.homeFragmentFavoriteContent.text = "게시판 첫 글 내용"
            }
        }
    }

    fun setFavorites(favorites : MutableList<Board>) {
        this.favorites = favorites
        this.notifyDataSetChanged()
    }

}