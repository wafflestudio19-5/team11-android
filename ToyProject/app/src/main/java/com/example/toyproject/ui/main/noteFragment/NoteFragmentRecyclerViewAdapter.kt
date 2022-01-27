package com.example.toyproject.ui.main.noteFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemNoteBinding
import com.example.toyproject.network.dto.*

class NoteFragmentRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var messageRooms: MutableList<MessageRoom> = mutableListOf()
    inner class MessageViewHolder(val binding: ItemNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return MessageViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = messageRooms[position]
        when(holder){
            is MessageViewHolder ->{
                holder.binding.apply{
                    noteWriter.text = data.user_nickname
                    noteContent.text = data.last_message.text
                    noteTime.text = data.last_message.created_at
                    if(data.unread_count == 0) unreadMessage.text = ""
                    else unreadMessage.text = "+" + data.unread_count
                    root.setOnClickListener {
                        itemClickListener.onItemClick(root, data, position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messageRooms.size
    }

    fun setMessageRoom(messageRooms : MutableList<MessageRoom>){
        this.messageRooms = messageRooms
        this.notifyDataSetChanged()
    }

    fun resetMessageRoom(){
        messageRooms.clear()
        this.notifyDataSetChanged()
    }

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(v: View, data: MessageRoom, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

}