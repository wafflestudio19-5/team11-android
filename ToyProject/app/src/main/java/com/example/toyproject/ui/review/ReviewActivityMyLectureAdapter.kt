package com.example.toyproject.ui.review

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemMyReviewBinding
import com.example.toyproject.network.dto.table.CustomLecture

class ReviewActivityMyLectureAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lectures : MutableList<CustomLecture> = mutableListOf()

    private lateinit var caller : Caller

    inner class Holder(val binding : ItemMyReviewBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            MY_REVIEW -> {
                val binding =  ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Holder(binding)
            }
            else -> {
                val binding =  ItemMyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Holder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lecture = lectures[position]

        when(holder) {
            is Holder -> {
                holder.binding.apply {
                    lectureName.text = lecture.nickname
                    professorName.text = lecture.professor
                    addMyReviewButton.text = "평가하기" // TODO

                    addMyReviewButton.setOnClickListener {
                        caller.click(lecture.subject_professor)
                    }
                }
            }
        }

    }

    override fun getItemCount(): Int {
        return lectures.size
    }

    override fun getItemViewType(position: Int): Int {
        return MY_REVIEW
    }

    fun setLectures(lectures : MutableList<CustomLecture>) {
        this.lectures = lectures
        notifyDataSetChanged()
    }

    companion object {
        const val MY_REVIEW = 1
    }

    interface Caller {
        fun click(id : Int)
    }
    fun setCaller(caller : Caller) {
        this.caller = caller
    }
}