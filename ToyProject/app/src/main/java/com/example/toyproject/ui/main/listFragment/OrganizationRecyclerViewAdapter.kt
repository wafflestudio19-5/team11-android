package com.example.toyproject.ui.main.listFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ItemNotDefaultBoardBinding
import com.example.toyproject.network.dto.Board

class OrganizationRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var boards: MutableList<Board> = mutableListOf()

    inner class OrganizationBoardViewHolder(val binding: ItemNotDefaultBoardBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNotDefaultBoardBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return OrganizationBoardViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = boards[position]
        when(holder){
            is OrganizationBoardViewHolder -> {
                holder.binding.apply {
                    notDefaultBoardTitle.text = data.name
                    notDefaultBoardDescription.text = data.description
                    if(data.favorite) boardPinIcon.setImageResource(R.drawable.icn_mcr_board_pin_on)
                    else boardPinIcon.setImageResource(R.drawable.icn_mcr_board_pin_off)
                    boardPinIcon.setOnClickListener{
                        if(data.favorite) itemClickListener.pinClick(it, data, position, true)
                        else itemClickListener.pinClick(it, data, position, false)
                    }
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
        fun pinClick(v: View, data: Board, position: Int, favorite: Boolean)
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