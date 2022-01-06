package com.example.toyproject.ui.main.homeFragment

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemHomeFragmentIssueBinding
import com.example.toyproject.network.dto.MyArticle

class HomeIssueRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var issueArticles: MutableList<MyArticle> = mutableListOf()
    inner class IssueViewHolder(val binding : ItemHomeFragmentIssueBinding) : RecyclerView.ViewHolder(binding.root)

    override fun getItemCount(): Int {
        return issueArticles.size
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemHomeFragmentIssueBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return IssueViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = issueArticles[position]
        when(holder) {
            is IssueViewHolder -> {
                holder.binding.commentNumber.text = data.comment_count.toString()
                holder.binding.likeNumber.text = data.like_count.toString()
                holder.binding.articleContent.text = data.text
                holder.binding.articleWrittenTime.text = data.f_created_at
                holder.binding.profileNickname.text = data.user_nickname
                holder.binding.articleTitle.text = data.title
                // holder.binding.profileImage TODO
            }
        }
    }

    fun setIssue(issueArticles: MutableList<MyArticle>) {
        this.issueArticles = issueArticles
        this.notifyDataSetChanged()
    }

}