package com.example.toyproject.ui.article

import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemCommentBinding
import com.example.toyproject.databinding.ItemCommentSubBinding
import com.example.toyproject.network.dto.Comment
import com.example.toyproject.ui.board.BoardAdapter
import kotlin.reflect.jvm.internal.impl.descriptors.PossiblyInnerType

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
            // 댓글 item
            is CommentViewHolder -> {
                holder.binding.commentContent.text = data.text
                holder.binding.commentNickname.text = data.user_nickname
                holder.binding.commentWrittenTime.text = data.created_at
                // 좋아요가 있을 때만 visible 로 바꾸기
                if(data.like_count!=0) {
                    holder.binding.itemCommentLikeImage.visibility = View.VISIBLE
                    holder.binding.itemCommentLikeNum.visibility = View.VISIBLE
                    holder.binding.itemCommentLikeNum.text = data.like_count.toString()
                }
                // TODO : 작성자 본인일 시 표기
                holder.binding.commentSubcommentButton.setOnClickListener {
                    // 댓글에 대댓글 다는 버튼 누를 때
                    commentClickListener.onCommentClick(data.id)
                }
                holder.binding.commentLikeButton.setOnClickListener {
                    if(data.is_mine) {
                        // 내 댓글에 좋아요 누르면 컷
                        commentClickListener.onCommentLikeClick(-1)
                    }
                    else {
                        // 댓글 좋아요 누를때
                        commentClickListener.onCommentLikeClick(data.id)
                    }
                }
                holder.binding.commentMoreButton.setOnClickListener {
                    // 댓글 ... 눌렀을 때
                    if(data.is_mine) {
                        // 내 댓글의 ... 버튼
                        commentClickListener.onCommentMore(data.id, mine=true, sub=false)
                    }
                    else {
                        // 남의 댓글의 ... 버튼
                        commentClickListener.onCommentMore(data.id, mine=false, sub=false)
                    }
                }
            }
            // 대댓글 item
            is SubCommentViewHolder -> {
                holder.binding.commentWrittenTime.text = data.created_at
                holder.binding.commentContent.text = data.text
                holder.binding.commentNickname.text = data.user_nickname
                // 좋아요가 있을 때만 visible 로 바꾸기
                if(data.like_count!=0) {
                    holder.binding.itemCommentLikeImage.visibility = View.VISIBLE
                    holder.binding.itemCommentLikeNum.visibility = View.VISIBLE
                    holder.binding.itemCommentLikeNum.text = data.like_count.toString()
                }
                holder.binding.commentLikeButton.setOnClickListener {
                    if(data.is_mine) {
                        // 내 대댓글에 좋아요 누르면 컷
                        commentClickListener.onCommentLikeClick(-1)
                    }
                    else {
                        // 대댓글에 좋아요 누를때
                        commentClickListener.onCommentLikeClick(data.id)
                    }
                }
                holder.binding.commentMoreButton.setOnClickListener {
                    // 대댓글 ... 눌렀을 때
                    if(data.is_mine) {
                        // 내 대댓글의 ...
                        commentClickListener.onCommentMore(data.id, mine=true, sub=true)
                    }
                    else {
                        // 남의 대댓글의 ...
                        commentClickListener.onCommentMore(data.id, mine=false, sub=true)
                    }
                }
            }
        }
    }

    // comment 개수 가져오기
    override fun getItemCount(): Int {
        return comments.size
    }

    // 댓글, 대댓글 다른 view 적용
    override fun getItemViewType(position: Int): Int {
        if(comments[position].is_subcomment) {
            return VIEW_TYPE_SUBCOMMENT
        }
        else {
            return VIEW_TYPE_COMMENT
        }
    }

    // 댓글, 대댓글 순서대로 정렬
    fun setComments(comments: MutableList<Comment>){
        val list : MutableList<Comment> = mutableListOf()
        val iterator = comments.iterator()
        while(iterator.hasNext()) {
            val temp = iterator.next()
            if(!temp.is_subcomment) {
                list.add(temp)
                val iterator2 = comments.iterator()
                val subList : MutableList<Comment> = mutableListOf()
                while(iterator2.hasNext()) {
                    val temp2 = iterator2.next()
                    if(temp2.parent==temp.id && temp2.is_subcomment) {
                        subList.add(temp2)
                    }
                }
                subList.sortBy { it.created_at }
                list.addAll(subList)
            }
        }
        this.comments = list
    }

    // 댓글, 대댓글 recyclerView 와의 소통을 위한 interface
    interface OnCommentClickListener {
        fun onCommentClick(parent : Int)
        fun onCommentLikeClick(id : Int)
        fun onCommentMore(id : Int, mine : Boolean, sub : Boolean)
    }
    fun setItemClickListener(onCommentClickListener : OnCommentClickListener) {
        this.commentClickListener = onCommentClickListener
    }

    // 댓글 객체의 id 로 adapter 에서의 position 찾기
    fun getItemPosition(id : Int) : Int {
        for(idx in 0 until comments.size) {
            if(comments[idx].id==id){
                return idx
            }
        }
        return 0
    }


    companion object {
        const val VIEW_TYPE_COMMENT = 0
        const val VIEW_TYPE_SUBCOMMENT = 1
    }
}