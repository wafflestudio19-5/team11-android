package com.example.toyproject.ui.main

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemNotDefaultBoardBinding
import com.example.toyproject.network.dto.Board
import java.lang.IllegalStateException

class GeneralRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var generalBoards: MutableList<Board> = mutableListOf()

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
                    notDefaultBoardDescription.text = data.description
                    root.setOnClickListener {
                        itemClickListener.onItemClick(root, data, position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return generalBoards.size
    }

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Board, position: Int)
    }

    fun setItemClickListener(onItemClickListener : OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    fun setDefaultBoards(defaultBoards: MutableList<Board>){
        this.generalBoards = defaultBoards
        this.notifyDataSetChanged()
    }


}