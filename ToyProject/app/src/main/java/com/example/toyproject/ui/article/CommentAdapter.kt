package com.example.toyproject.ui.article

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.amazonaws.auth.BasicAWSCredentials
import com.amazonaws.regions.Region
import com.amazonaws.regions.Regions
import com.amazonaws.services.s3.AmazonS3Client
import com.amazonaws.services.s3.model.GeneratePresignedUrlRequest
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.example.toyproject.R
import com.example.toyproject.databinding.ItemCommentBinding
import com.example.toyproject.databinding.ItemCommentSubBinding
import com.example.toyproject.network.dto.Comment
import com.example.toyproject.ui.board.BoardAdapter
import java.net.URL
import java.util.*
import kotlin.reflect.jvm.internal.impl.descriptors.PossiblyInnerType

class CommentAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>()  {

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
                //profile image
                val credentials: BasicAWSCredentials
                val key = holder.itemView.context.getString(R.string.AWS_ACCESS_KEY_ID)
                val secret = holder.itemView.context.getString(R.string.AWS_SECRET_ACCESS_KEY)
                if(data.user_image!=""&&data.user_image!=null){
                    val objectKey = data.user_image.substring(52)
                    credentials = BasicAWSCredentials(key, secret)
                    val s3 = AmazonS3Client(
                        credentials, Region.getRegion(
                            Regions.AP_NORTHEAST_2
                        )
                    )
                    val expires = Date(Date().getTime() + 1000 * 60) // 1 minute to expire
                    val generatePresignedUrlRequest =
                        GeneratePresignedUrlRequest("team11bucket", objectKey) //generating the signatured url
                    generatePresignedUrlRequest.expiration = expires
                    val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
                    holder.apply {
                        Glide.with(context)
                            .setDefaultRequestOptions(
                                RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .placeholder(R.drawable.anonymous_photo)
                                    .fitCenter()
                            )
                            .load(url.toString())
                            .into(itemView.findViewById(R.id.comment_profile_image))
                    }
                }


                holder.binding.commentContent.text = data.text
                holder.binding.commentNickname.text = data.user_nickname
                holder.binding.commentWrittenTime.text = data.created_at
                // 좋아요가 있을 때만 visible 로 바꾸기
                if(data.like_count!=0) {
                    holder.binding.itemCommentLikeImage.visibility = View.VISIBLE
                    holder.binding.itemCommentLikeNum.visibility = View.VISIBLE
                    holder.binding.itemCommentLikeNum.text = data.like_count.toString()
                }
                // 첫 댓글은 topBorder 없음
                if(position==0) {
                    holder.binding.topBorder.visibility = View.INVISIBLE
                }
                if(data.user_nickname=="익명(글쓴이)"){
                    holder.binding.commentNickname.setTextColor(Color.parseColor(context.getString(R.string.color_my_comment)))
                }
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
                //profile image
                val credentials: BasicAWSCredentials
                val key = holder.itemView.context.getString(R.string.AWS_ACCESS_KEY_ID)
                val secret = holder.itemView.context.getString(R.string.AWS_SECRET_ACCESS_KEY)
                if(data.user_image!=""&&data.user_image!=null){
                    val objectKey = data.user_image.substring(52)
                    credentials = BasicAWSCredentials(key, secret)
                    val s3 = AmazonS3Client(
                        credentials, Region.getRegion(
                            Regions.AP_NORTHEAST_2
                        )
                    )
                    val expires = Date(Date().getTime() + 1000 * 60) // 1 minute to expire
                    val generatePresignedUrlRequest =
                        GeneratePresignedUrlRequest("team11bucket", objectKey) //generating the signatured url
                    generatePresignedUrlRequest.expiration = expires
                    val url: URL = s3.generatePresignedUrl(generatePresignedUrlRequest)
                    holder.apply {
                        Glide.with(context)
                            .setDefaultRequestOptions(
                                RequestOptions()
                                    .diskCacheStrategy(DiskCacheStrategy.AUTOMATIC)
                                    .placeholder(R.drawable.anonymous_photo)
                                    .fitCenter()
                            )
                            .load(url.toString())
                            .into(itemView.findViewById(R.id.comment_profile_image))
                    }
                }


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

    // 댓글, 대댓글 recyclerView 와 activity 의 소통을 위한 interface
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