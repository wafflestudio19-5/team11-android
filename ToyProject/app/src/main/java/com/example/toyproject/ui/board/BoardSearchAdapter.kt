package com.example.toyproject.ui.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemSearchBoardBinding
import com.example.toyproject.network.dto.Board

class BoardSearchAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var boards: List<Board> = listOf()
    inner class BoardSearchViewHolder(val binding: ItemSearchBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemSearchBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BoardSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = boards[position]
        when(holder){
            is BoardSearchViewHolder -> {
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
        return boards.size
    }

    fun setBoards(boards: List<Board>){
        this.boards = boards
        this.notifyDataSetChanged()
    }

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Board, position: Int)
    }

    fun setItemClickListener(onItemClickListener : OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }


}