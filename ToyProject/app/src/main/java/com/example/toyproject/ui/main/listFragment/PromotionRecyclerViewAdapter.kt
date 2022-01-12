package com.example.toyproject.ui.main.listFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemNotDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class PromotionRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var boards: MutableList<Board> = mutableListOf()

    inner class PromotionBoardViewHolder(val binding: ItemNotDefaultBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNotDefaultBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return PromotionBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = boards[position]
        when(holder){
            is PromotionBoardViewHolder -> {
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

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Board, position: Int)
    }

    fun setItemClickListener(onItemClickListener : OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    fun setBoards(boards: MutableList<Board>){
        this.boards = boards
        this.notifyDataSetChanged()
    }

    fun resetBoards(){
        this.boards.clear()
        this.notifyDataSetChanged()
    }


}