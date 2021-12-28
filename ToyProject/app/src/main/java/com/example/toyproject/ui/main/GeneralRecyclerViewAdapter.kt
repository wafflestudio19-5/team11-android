package com.example.toyproject.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemNotDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class GeneralRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var generalBoards: List<Board> = listOf()
    inner class GeneralBoardViewHolder(val binding: ItemNotDefaultBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNotDefaultBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return GeneralBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = generalBoards[position]
        when(holder){
            is GeneralBoardViewHolder -> {
                holder.binding.apply {
                    notDefaultBoardTitle.text = data.name
                    notDefaultBoardDescription.text = data.desc
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return generalBoards.size
    }

    fun setDefaultBoards(defaultBoards: List<Board>){
        this.generalBoards = defaultBoards
        this.notifyDataSetChanged()
    }


}