package com.example.toyproject.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemNotDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class CareerRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var careerBoards: List<Board> = listOf()
    inner class CareerBoardViewHolder(val binding: ItemNotDefaultBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNotDefaultBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CareerBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = careerBoards[position]
        when(holder){
            is CareerBoardViewHolder -> {
                holder.binding.apply {
                    notDefaultBoardTitle.text = data.name
                    notDefaultBoardDescription.text = data.description
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return careerBoards.size
    }

    fun setDefaultBoards(defaultBoards: List<Board>){
        this.careerBoards = defaultBoards
        this.notifyDataSetChanged()
    }


}