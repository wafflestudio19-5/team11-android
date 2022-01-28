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
    var highlighted = -1

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

                    if(lecture.professor == null || lecture.professor.isEmpty()) serverLectureInstructor.visibility = View.GONE
                    else serverLectureInstructor.text = lecture.professor

                    if(lecture.time == null || lecture.time.isEmpty()) serverLectureTime.visibility = View.GONE
                    else serverLectureTime.text = lecture.time

                    if(lecture.location==null || lecture.location.isEmpty()) serverLectureLocation.visibility = View.GONE
                    else serverLectureLocation.text = lecture.location

                    if(lecture.detail==null) serverLectureItemDetail.visibility = View.GONE
                    else serverLectureItemDetail.text = buildDetailText(lecture.detail)

                    serverLectureClassCode.text = buildClassNum(lecture.number)

                    sBuilder.append(lecture.grade)
                    sBuilder.append("학년")
                    serverLectureYear.text = sBuilder.toString()
                    sBuilder.clear()

                    serverLectureType.text = lecture.category

                    sBuilder.append(lecture.credit)
                    sBuilder.append("학점")
                    serverLectureCredit.text = sBuilder.toString()
                    sBuilder.clear()

                    serverLectureCode.text = lecture.subject_code

                    if(lecture.level.isEmpty()) serverLectureItemLevel.visibility = View.GONE
                    else serverLectureItemLevel.text = buildTypeText(lecture.level)

                    if(lecture.method.isEmpty()) serverLectureItemFeature.visibility=View.GONE
                    else serverLectureItemFeature.text = buildFeatureText(lecture.method)

                    serverLectureQuotaNum.text = lecture.quota.toString()
                    serverLecturePickedNum.text = lecture.people.toString()

                    if(lecture.rate==0)

                    if(position==highlighted) {
                        serverLectureItemMoreLayout.visibility = View.VISIBLE
                        serverLectureItemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_yellow))
                    }
                    else {
                        serverLectureItemMoreLayout.visibility = View.GONE
                        serverLectureItemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.Background))
                    }

                    serverLectureItemLayout.setOnClickListener {
                        if(serverLectureItemMoreLayout.visibility==View.GONE) {
                            highlighted = position
                            serverLectureItemMoreLayout.visibility =View.VISIBLE
                            serverLectureItemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.light_yellow))
                            lectureClickListener.removeShadow()
                            lectureClickListener.onItemClick(it, lecture, position)
                            notifyDataSetChanged()
                        }
                        else {
                            highlighted = -1
                            lectureClickListener.removeShadow()
                            serverLectureItemMoreLayout.visibility =View.GONE
                            serverLectureItemLayout.setBackgroundColor(ContextCompat.getColor(context, R.color.Background))
                            notifyDataSetChanged()
                        }
                    }
                    serverLectureItemAdd.setOnClickListener {
                        lectureClickListener.addItem(serverLectureItemLayout, it, lecture, position)
                    }
                    serverLectureItemRating.setOnClickListener {
                        lectureClickListener.showReview(lecture.subject_professor)
                    }
                    serverLectureItemSyllabus.setOnClickListener {
                        lectureClickListener.openSyllabus(lecture.url)
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
    fun buildClassNum(i : Int) : String{
        return when {
            i<10 -> "00$i"
            i<100 -> "0$i"
            else -> i.toString()
        }
    }

    private fun buildFeatureText(s : String) : String {
        return StringBuilder()
                .append("형태: ")
                .append(s)
                .toString()
    }
    private fun buildTypeText(s : String) : String {
        return StringBuilder()
            .append("수강대상: ")
            .append(s)
            .toString()
    }

    private fun buildDetailText(s : String) : String {
        return StringBuilder()
            .append("비고: ")
            .append(s)
            .toString()
    }

    fun setLectures(list : MutableList<Lecture>) {
        this.lectureList = list
        notifyDataSetChanged()
    }
    fun clearLectures() {
        this.lectureList.clear()
        notifyDataSetChanged()
    }

    fun addMoreLectures(list : MutableList<Lecture>) {
        this.lectureList.addAll(list)
        notifyDataSetChanged()
    }

    private lateinit var lectureClickListener: OnLectureClickListener
    interface OnLectureClickListener {
        fun onItemClick(v: View, data: Lecture, position: Int)
        fun removeShadow()
        fun addItem(parent : View, v: View, lecture: Lecture, position: Int)
        fun showReview(id : Int)
        fun openSyllabus(url : String)
    }

    fun setItemClickListener(onLectureClickListener: OnLectureClickListener){
        this.lectureClickListener = onLectureClickListener
    }
    companion object {
        const val VIEW_TYPE_LECTURE = 0
    }

}