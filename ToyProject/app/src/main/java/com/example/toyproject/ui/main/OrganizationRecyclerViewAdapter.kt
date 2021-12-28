package com.example.toyproject.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemNotDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class OrganizationRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var organizationBoards: List<Board> = listOf()
    inner class OrganizationBoardViewHolder(val binding: ItemNotDefaultBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNotDefaultBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrganizationBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = organizationBoards[position]
        when(holder){
            is OrganizationBoardViewHolder -> {
                holder.binding.apply {
                    notDefaultBoardTitle.text = data.name
                    notDefaultBoardDescription.text = data.desc
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return organizationBoards.size
    }

    fun setDefaultBoards(defaultBoards: List<Board>){
        this.organizationBoards = defaultBoards
        this.notifyDataSetChanged()
    }


}