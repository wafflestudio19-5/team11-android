package com.example.toyproject.ui.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemLectureReviewBinding
import com.example.toyproject.network.Review

class LectureReviewAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var reviews: MutableList<Review> = mutableListOf()

    inner class ReviewViewHolder(val binding: ItemLectureReviewBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemLectureReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ReviewViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = reviews[position]
        when(holder){
            is ReviewViewHolder -> {
                holder.binding.apply {
                    ratingBar.rating = data.rating.toFloat()
                    lectureSemester.text = data.year.toString() + "년 "  + data.season + " 수강자"
                    reviewDetail.text = data.comment
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return reviews.size
    }

    fun setReviews(reviews: MutableList<Review>){
        this.reviews.addAll(reviews)
    }

}