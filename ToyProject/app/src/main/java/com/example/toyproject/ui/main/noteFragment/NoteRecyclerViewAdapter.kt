package com.example.toyproject.ui.main.noteFragment

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemGetNoteBinding
import com.example.toyproject.databinding.ItemIntroNoteBinding
import com.example.toyproject.databinding.ItemNoteBinding
import com.example.toyproject.databinding.ItemSendNoteBinding
import com.example.toyproject.network.dto.Message
import com.example.toyproject.network.dto.MessageRoom
import java.lang.IllegalStateException

class NoteRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var messages: MutableList<Message> = mutableListOf()
    inner class GetMessageViewHolder(val binding: ItemGetNoteBinding) : RecyclerView.ViewHolder(binding.root)
    inner class SendMessageViewHolder(val binding: ItemSendNoteBinding) : RecyclerView.ViewHolder(binding.root)
    inner class IntroMessageViewHolder(val binding: ItemIntroNoteBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType){
            VIEW_TYPE_GET -> {
                val binding = ItemGetNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                GetMessageViewHolder(binding)
            }
            VIEW_TYPE_SEND -> {
                val binding = ItemSendNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SendMessageViewHolder(binding)
            }
            VIEW_TYPE_INTRO -> {
                val binding = ItemIntroNoteBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                IntroMessageViewHolder(binding)
            }
            else -> throw IllegalStateException("error")
        }


    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = messages[position]
        when(holder){
            is GetMessageViewHolder ->{
                holder.binding.apply{
                    noteContent.text = data.text
                    noteTime.text = data.created_at
                }
            }
            is SendMessageViewHolder ->{
                holder.binding.apply{
                    noteContent.text = data.text
                    noteTime.text = data.created_at
                }
            }
            is IntroMessageViewHolder ->{
                holder.binding.apply{
                    noteContent.text = data.text
                    noteTime.text = data.created_at
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return messages.size
    }

    override fun getItemViewType(position: Int): Int {
        return when(messages[position].type) {
            "받은 쪽지" -> VIEW_TYPE_GET
            "안내" -> VIEW_TYPE_INTRO
            "보낸 쪽지" -> VIEW_TYPE_SEND
            else -> -1
        }

    }

    fun setMessage(messages : MutableList<Message>){
        this.messages = messages
        this.notifyDataSetChanged()
    }

    fun resetMessages(){
        messages.clear()
        this.notifyDataSetChanged()
    }

    companion object{
        const val VIEW_TYPE_GET = 0
        const val VIEW_TYPE_SEND = 1
        const val VIEW_TYPE_INTRO = 2
    }


}