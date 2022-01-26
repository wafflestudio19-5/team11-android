package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ItemTableAddFilterBinding
import com.example.toyproject.databinding.ItemTableServerLectureBinding
import com.example.toyproject.databinding.ItemTitleContentArticleBinding
import com.example.toyproject.network.dto.table.Lecture
import com.example.toyproject.network.dto.table.Semester
import com.example.toyproject.ui.board.BoardAdapter

class TableAddLectureServerAdapter(private val context: Context): RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var lectureList: MutableList<Lecture> = mutableListOf()

    inner class Holder(val binding : ItemTableServerLectureBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return when(viewType) {
            VIEW_TYPE_LECTURE -> {
                val binding = ItemTableServerLectureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Holder(binding)
            }
            else -> {
                val binding = ItemTableServerLectureBinding.inflate(LayoutInflater.from(parent.context), parent, false)
                Holder(binding)
            }
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val lecture = lectureList[position]
        when(holder) {
            is Holder -> {
                holder.binding.apply {
                    val sBuilder = StringBuilder()

                    serverLectureTitle.text = lecture.subject_name
                    serverLectureInstructor.text = lecture.professor
                    serverLectureTime.text = lecture.time                // TODO ?
                    serverLectureLocation.text = lecture.location        // TODO ?

                    sBuilder.append(lecture.year)
                    sBuilder.append("학년")
                    serverLectureYear.text = sBuilder.toString()
                    sBuilder.clear()

                    serverLectureType.text = lecture.category

                    sBuilder.append(lecture.credit)
                    sBuilder.append("학점")
                    serverLectureCredit.text = sBuilder.toString()
                    sBuilder.clear()

                    serverLectureCode.text = lecture.subject_code

                    serverLectureItemMoreLayout.visibility = View.GONE
                    serverLectureItemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.Background))
                    serverLectureItemLayout.setOnClickListener {
                        lectureClickListener.onItemClick(it, lecture, position)
                    }
                    serverLectureItemAdd.setOnClickListener {
                        lectureClickListener.addItem(serverLectureItemLayout, it, lecture, position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return lectureList.size
    }

    override fun getItemViewType(position: Int): Int {
        return VIEW_TYPE_LECTURE
    }

    fun setLectures(list : MutableList<Lecture>) {
        this.lectureList = list
    }

    private lateinit var lectureClickListener: OnLectureClickListener
    interface OnLectureClickListener {
        fun onItemClick(v: View, data: Lecture, position: Int)
        fun addItem(parent : View, v: View, lecture: Lecture, position: Int)
    }

    fun setItemClickListener(onLectureClickListener: OnLectureClickListener){
        this.lectureClickListener = onLectureClickListener
    }
    companion object {
        const val VIEW_TYPE_LECTURE = 0
    }

}