package com.example.toyproject.ui.board

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemLoadingBinding
import com.example.toyproject.databinding.ItemTitleContentArticleBinding
import com.example.toyproject.network.dto.Article
import java.lang.IllegalStateException

class BoardAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var articles: MutableList<Article> = mutableListOf()

    inner class BoardViewHolder(val binding: ItemTitleContentArticleBinding) : RecyclerView.ViewHolder(binding.root)
    inner class LoadingViewHolder(val binding: ItemLoadingBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when (viewType){
            VIEW_TYPE_BOARD -> {
                val binding = ItemTitleContentArticleBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                BoardViewHolder(binding)
            }
            VIEW_TYPE_LOADING -> {
                val binding = ItemLoadingBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                LoadingViewHolder(binding)
            }
            else -> throw IllegalStateException("ViewType must be 0 or 1")
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = articles[position]
        when(holder){
            is BoardViewHolder -> {
                holder.binding.apply {
                    articleTitle.text = data.title
                    articleContent.text = data.text
                    articleWriter.text = data.user_nickname
                    if(data.image_count==0){
                        articleImageIcon.visibility = View.INVISIBLE
                    }else{
                        articleImageIcon.visibility = View.VISIBLE
                        articleImageNumber.text = data.image_count.toString()
                    }
                    articleCommentNumber.text = data.comment_count.toString()
                    articleLikeNumber.text = data.like_count.toString()
                    articleWrittenTime.text = data.f_created_at + " | "
                    root.setOnClickListener {
                        itemClickListener.onItemClick(root, data, position)
                    }
                }
            }
            else -> {

            }

        }
    }
    override fun getItemCount(): Int {
        return articles.size
    }

    override fun getItemViewType(position: Int): Int {
        return if(articles[position].user_nickname=="") VIEW_TYPE_LOADING
        else VIEW_TYPE_BOARD
    }

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Article, position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener : OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    fun deleteLoading(){
        articles.removeAt(articles.lastIndex)
    }

    fun setArticles(articles: MutableList<Article>){
        val temp : MutableList<Article> = mutableListOf()
        temp.addAll(articles)
        this.articles.addAll(temp)
        //this.articles.add(Article(0, "", "", "", 0, 0, 0, "" , ""))
    }

    fun resetArticles(){
        articles.clear()
        this.notifyDataSetChanged()
    }


    companion object {
        const val VIEW_TYPE_BOARD = 0
        const val VIEW_TYPE_LOADING = 1
    }
}