package com.example.toyproject.ui.main.homeFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemHomeFragmentHotBinding
import com.example.toyproject.network.dto.MyArticle

class HomeHotRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var hotArticles: MutableList<MyArticle> = mutableListOf()
    inner class HotViewHolder(val binding: ItemHomeFragmentHotBinding) : RecyclerView.ViewHolder(binding.root)


    override fun getItemCount(): Int {
        return hotArticles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemHomeFragmentHotBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return HotViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = hotArticles[position]
        when(holder) {
            is HotViewHolder -> {
                holder.binding.articleCommentNumber.text = data.comment_count.toString()
                holder.binding.articleLikeNumber.text = data.like_count.toString()
                holder.binding.articleTitle.text = data.title
                holder.binding.articleWrittenTime.text = data.f_created_at
            }
        }
    }

    fun setHotArticles(hotArticles: MutableList<MyArticle>) {
        this.hotArticles = hotArticles
        this.notifyDataSetChanged()
    }

}