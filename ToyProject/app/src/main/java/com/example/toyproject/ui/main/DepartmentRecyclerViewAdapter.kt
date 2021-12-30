package com.example.toyproject.ui.main

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemNotDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class DepartmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var departmentBoards: List<Board> = listOf()
    inner class DepartmentBoardViewHolder(val binding: ItemNotDefaultBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNotDefaultBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return DepartmentBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = departmentBoards[position]
        when(holder){
            is DepartmentBoardViewHolder -> {
                holder.binding.apply {
                    notDefaultBoardTitle.text = data.name
                    notDefaultBoardDescription.text = data.description
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return departmentBoards.size
    }

    fun setDefaultBoards(defaultBoards: List<Board>){
        this.departmentBoards = defaultBoards
        this.notifyDataSetChanged()
    }


}