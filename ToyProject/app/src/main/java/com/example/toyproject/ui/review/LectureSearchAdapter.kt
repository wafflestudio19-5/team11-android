package com.example.toyproject.ui.review

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemReviewSearchBinding
import com.example.toyproject.network.LectureInfo

class LectureSearchAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var results: MutableList<LectureInfo> = mutableListOf()
    inner class LectureSearchViewHolder(val binding: ItemReviewSearchBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemReviewSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LectureSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = results[position]
        when(holder){
            is LectureSearchViewHolder ->{
                holder.binding.apply {
                    lectureName.text = data.subject_name
                    professorName.text = data.professor
                    if(data.review!=null) ratingBar.rating = data.review.rating
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return results.size
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, data: LectureInfo, position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    fun setResults(results: MutableList<LectureInfo>){
        this.results.addAll(results)
    }
}