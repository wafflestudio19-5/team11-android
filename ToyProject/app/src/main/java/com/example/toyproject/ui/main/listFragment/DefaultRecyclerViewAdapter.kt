package com.example.toyproject.ui.main.listFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class DefaultRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var defaultBoards: MutableList<Board> = mutableListOf()
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
                    root.setOnClickListener {
                        itemClickListener.onItemClick(root, data, position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return defaultBoards.size
    }

    fun setDefaultBoards(defaultBoards: MutableList<Board>){
        this.defaultBoards = defaultBoards
        this.notifyDataSetChanged()
    }

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Board, position: Int)
    }

    fun setItemClickListener(onItemClickListener : OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    fun resetBoards(){
        this.defaultBoards.clear()
        this.notifyDataSetChanged()
    }


}