package com.example.toyproject.ui.article

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemCommentBinding
import com.example.toyproject.network.dto.Comment

class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private var comments: MutableList<Comment> = mutableListOf()

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return CommentViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = comments[position]
        when(holder) {
            is CommentViewHolder -> {
                holder.binding.commentContent.text = data.text
                holder.binding.commentNickname.text = data.user_nickname
                // holder.binding.commentProfileImage
                holder.binding.commentWrittenTime.text = data.created_at
                // TODO : 작성자 본인일 시 표기
            }
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    fun setComments(comments: MutableList<Comment>){
        this.comments = comments
    }
}