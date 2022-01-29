package com.example.toyproject.ui.review

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.databinding.ItemExamInfoBinding
import com.example.toyproject.network.Information

class InformationAdapter: RecyclerView.Adapter<RecyclerView.ViewHolder>() {

    private var information: MutableList<Information> = mutableListOf()

    inner class InformationViewHolder(val binding: ItemExamInfoBinding): RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemExamInfoBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return InformationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = information[position]
        when(holder){
            is InformationViewHolder -> {
                holder.binding.apply {
                    examTitle.text = data.test_type.toString()
                    lectureSemester.text = data.year.toString() + "년 " + data.season + " 수강자"
                    examStrategy.text = data.strategy
                    questionType.text = data.test_method
                    questionExample.text = data.problems.joinToString("\n")
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return information.size
    }

    fun setInformation(info: MutableList<Information>){
        this.information.addAll(info)
    }
}