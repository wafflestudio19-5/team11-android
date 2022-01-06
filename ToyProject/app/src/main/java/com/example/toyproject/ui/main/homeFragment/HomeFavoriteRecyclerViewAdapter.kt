package com.example.toyproject.ui.main.homeFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemHomeFragmentFavoriteBinding
import com.example.toyproject.network.dto.Board
import timber.log.Timber

class HomeFavoriteRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var favoriteNames : MutableList<String> = mutableListOf()
    private var titles : MutableList<String> = mutableListOf()

    inner class FavoriteViewHolder(val binding : ItemHomeFragmentFavoriteBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int {
        return favoriteNames.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding =ItemHomeFragmentFavoriteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return FavoriteViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val board_name = favoriteNames[position]
        val title = titles[position]
        when(holder) {
            is FavoriteViewHolder -> {
                holder.binding.homeFragmentFavoriteBoardName.text = board_name
                holder.binding.homeFragmentFavoriteContent.text = title
            }
        }
    }
    fun setFavoriteBoard(titles : List<String>, names : List<String>) {
        val nameTemp = mutableListOf<String>()
        nameTemp.addAll(names)
        val titleTemp = mutableListOf<String>()
        titleTemp.addAll(titles)
        this.favoriteNames = nameTemp
        this.titles = titleTemp
        this.notifyDataSetChanged()
    }

}