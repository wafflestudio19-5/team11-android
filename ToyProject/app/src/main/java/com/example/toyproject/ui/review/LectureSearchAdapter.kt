package com.example.toyproject.ui.review

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ItemReviewSearchBinding
import com.example.toyproject.network.LectureInfo

class LectureSearchAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private lateinit var clicker : Clicker

    private var searchResults: MutableList<LectureInfo> = mutableListOf()
    inner class LectureSearchViewHolder(val binding: ItemReviewSearchBinding) : RecyclerView.ViewHolder(binding.root)


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemReviewSearchBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LectureSearchViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = searchResults[position]
        when(holder){
            is LectureSearchViewHolder ->{
                holder.binding.apply {
                    lectureName.text = data.subject_name
                    professorName.text = data.professor
                    // if(data.review!=null) ratingBar.rating = data.review.rating
                    // 별점
                    val stars = arrayOf(serverLectureStar1, serverLectureStar1, serverLectureStar2,
                        serverLectureStar3, serverLectureStar4, serverLectureStar5)
                    stars.forEachIndexed { idx, star  ->
                        if(data.review!=null && idx <= data.review.rating) {
                            star.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icn_e_rating_star_yellow))
                        }
                        else star.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icn_e_rating_star_gray400))
                    }

                    itemReviewSearchLayout.setOnClickListener {
                        clicker.click(data.id)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return searchResults.size
    }

    interface OnItemClickListener{
        fun onItemClick(v: View, data: LectureInfo, position: Int)
    }

    private lateinit var itemClickListener: OnItemClickListener

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }

    fun setResults(results: MutableList<LectureInfo>){
        this.searchResults = results
        notifyDataSetChanged()
    }
    fun addResult(results: MutableList<LectureInfo>) {
        this.searchResults.addAll(results)
        notifyDataSetChanged()
    }
    fun clearResults() {
        this.searchResults.clear()
        notifyDataSetChanged()
    }
    interface Clicker {
        fun click(id : Int)
    }
    fun setClicker(clicker : Clicker) {
        this.clicker = clicker
    }
}