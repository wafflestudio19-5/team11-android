package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.network.dto.table.Semester
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.toyproject.R
import java.lang.StringBuilder


class TableListAdapter(private val context: Context) :
    RecyclerView.Adapter<TableListAdapter.Holder>() {
    private lateinit var clicker: Clicker

    private val match = arrayOf("","1학기", "2학기", "여름학기", "겨울학기")

    private var list: MutableList<Semester> = mutableListOf()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): TableListAdapter.Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_table_list_semester_cell, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: TableListAdapter.Holder, position: Int) {
        holder.bind(list[position])
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun setSemesters(list : MutableList<Semester>) {
        this.list = list
        notifyDataSetChanged()
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val title = itemView?.findViewById<TextView>(R.id.table_list_semester_title)

        fun bind(semester: Semester) {
            val stringBuilder = StringBuilder()
            stringBuilder.append(semester.year.toString())
            stringBuilder.append("년 ")
            stringBuilder.append(match[semester.season])
            title?.text = stringBuilder.toString()

            val recyclerView = itemView.findViewById<RecyclerView>(R.id.table_list_semester_recycler_view)
            val layoutManager = LinearLayoutManager(context)

            val mAdapter = TableListCellAdapter(context, semester.schedules.toMutableList())
            recyclerView.adapter = mAdapter
            recyclerView.layoutManager = layoutManager
            mAdapter.setClicker(object : TableListCellAdapter.Clicker {
                override fun click(title : String) {
                    clicker.click(title, semester.year, semester.season)
                }
            })
            // recyclerView.setHasFixedSize(true)
        }
    }

    interface Clicker {
        fun click(title : String, year : Int, season : Int)
    }
    fun setClicker(clicker: Clicker) {
        this.clicker = clicker
    }



}