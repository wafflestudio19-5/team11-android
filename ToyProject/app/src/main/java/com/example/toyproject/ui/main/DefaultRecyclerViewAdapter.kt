package com.example.toyproject.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class DefaultRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var defaultBoards: List<Board> = listOf()
    inner class DefaultBoardViewHolder(val binding: ItemDefaultBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemDefaultBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DefaultBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = defaultBoards[position]
        when(holder){
            is DefaultBoardViewHolder -> {
                holder.binding.apply {
                    defaultBoardType.text = data.name
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return defaultBoards.size
    }

    fun setDefaultBoards(defaultBoards: List<Board>){
        this.defaultBoards = defaultBoards
        this.notifyDataSetChanged()
    }


}