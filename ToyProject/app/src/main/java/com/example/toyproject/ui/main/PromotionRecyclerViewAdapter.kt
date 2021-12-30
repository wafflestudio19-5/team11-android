package com.example.toyproject.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemNotDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class PromotionRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var promotionBoards: List<Board> = listOf()
    inner class PromotionBoardViewHolder(val binding: ItemNotDefaultBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNotDefaultBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromotionBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = promotionBoards[position]
        when(holder){
            is PromotionBoardViewHolder -> {
                holder.binding.apply {
                    notDefaultBoardTitle.text = data.name
                    notDefaultBoardDescription.text = data.description
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return promotionBoards.size
    }

    fun setDefaultBoards(defaultBoards: List<Board>){
        this.promotionBoards = defaultBoards
        this.notifyDataSetChanged()
    }


}