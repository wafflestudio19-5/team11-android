package com.example.toyproject.ui.main.homeFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ItemRecentReviewForHomeBinding
import com.example.toyproject.network.RecentReviewItem

class HomeRecentRecyclerViewAdapter(private val context: Context) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var recentReview = mutableListOf<RecentReviewItem>()
    private lateinit var clicker : Clicker

    inner class Holder(val binding : ItemRecentReviewForHomeBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            REVIEW -> {
                val binding = ItemRecentReviewForHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Holder(binding)
            }
            else -> {
                val binding = ItemRecentReviewForHomeBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Holder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val review = recentReview[position]

        when(holder) {
            is Holder -> {
                holder.binding.apply {

                    lectureInfo.text = buildLectureInfoString(review.subject_name, review.professor)
                    reviewDetail.text = review.comment

                    val stars = arrayOf(serverLectureStar1, serverLectureStar1, serverLectureStar2,
                        serverLectureStar3, serverLectureStar4, serverLectureStar5)
                    stars.forEachIndexed { idx, star  ->
                        if(idx <= review.rating) {
                            star.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icn_e_rating_star_yellow))
                        }
                        else star.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.icn_e_rating_star_gray400))
                    }

                    recentReviewLayout.setOnClickListener {
                        clicker.click(review.subject_professor_id)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return recentReview.size
    }

    override fun getItemViewType(position: Int): Int {
        return REVIEW
    }

    fun setReview(reviews : MutableList<RecentReviewItem>) {
        this.recentReview = reviews
        notifyDataSetChanged()
    }
    fun addReview(reviews : MutableList<RecentReviewItem>) {
        this.recentReview.addAll(reviews)
        notifyDataSetChanged()
    }
    fun clearReview() {
        this.recentReview.clear()
        notifyDataSetChanged()
    }
    private fun buildLectureInfoString(title : String, professor : String?) : String{
        val sBuilder = StringBuilder().append(title)
        if(professor!=null) {
            sBuilder.append(" : ")
                .append(professor)
        }
        return sBuilder.toString()
    }

    interface Clicker {
        fun click(id : Int)
    }

    companion object {
        const val REVIEW = 1
    }
}