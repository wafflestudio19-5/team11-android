package com.example.toyproject.ui.article

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemCommentBinding
import com.example.toyproject.databinding.ItemCommentSubBinding
import com.example.toyproject.network.dto.Comment
import com.example.toyproject.ui.board.BoardAdapter

class CommentAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

    private var comments: MutableList<Comment> = mutableListOf()

    inner class CommentViewHolder(val binding: ItemCommentBinding) : RecyclerView.ViewHolder(binding.root)
    inner class SubCommentViewHolder(val binding : ItemCommentSubBinding) : RecyclerView.ViewHolder(binding.root)

    private lateinit var commentClickListener: OnCommentClickListener

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {

        return when(viewType) {
            VIEW_TYPE_COMMENT ->  {
                val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CommentViewHolder(binding)
            }
            VIEW_TYPE_SUBCOMMENT -> {
                val binding = ItemCommentSubBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                SubCommentViewHolder(binding)
            }
            else -> {
                val binding = ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                CommentViewHolder(binding)
            }
        }
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
                holder.binding.commentSubcommentButton.setOnClickListener {
                    commentClickListener.onCommentClick(data.id)
                    holder.binding.bottomBorder.setBackgroundColor(0)
                }
            }
            is SubCommentViewHolder -> {
                holder.binding.commentWrittenTime.text = data.created_at
                holder.binding.commentContent.text = data.text
                holder.binding.commentContent.text = data.text
            }
        }
    }

    override fun getItemCount(): Int {
        return comments.size
    }

    override fun getItemViewType(position: Int): Int {
        if(comments[position].is_subcomment) {
            return VIEW_TYPE_SUBCOMMENT
        }
        else {
            return VIEW_TYPE_COMMENT
        }
    }

    fun setComments(comments: MutableList<Comment>){
        val list : MutableList<Comment> = mutableListOf()
        val iterator = comments.iterator()
        while(iterator.hasNext()) {
            val temp = iterator.next()
            if(!temp.is_subcomment) {
                list.add(temp)
                val iterator2 = comments.iterator()
                while(iterator2.hasNext()) {
                    val temp2 = iterator2.next()
                    if(temp2.parent==temp.id && temp2.is_subcomment) {
                        list.add(temp2)
                    }
                }
            }
        }
        this.comments = list
    }

    interface OnCommentClickListener {
        fun onCommentClick(parent : Int)
    }

    fun setItemClickListener(onCommentClickListener : OnCommentClickListener) {
        this.commentClickListener = onCommentClickListener
    }

    fun makeRed(id : Int) {
        
    }

    companion object {
        const val VIEW_TYPE_COMMENT = 0
        const val VIEW_TYPE_SUBCOMMENT = 1
    }
}