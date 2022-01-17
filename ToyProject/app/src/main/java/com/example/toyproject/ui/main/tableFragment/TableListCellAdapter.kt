package com.example.toyproject.ui.main.tableFragment

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.View.GONE
import android.view.View.VISIBLE
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ItemTableListBinding
import com.example.toyproject.network.dto.table.Schedule

class TableListCellAdapter(private val context: Context, private val List: MutableList<Schedule>) :
    RecyclerView.Adapter<TableListCellAdapter.Holder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): Holder {
        val view = LayoutInflater.from(context).inflate(R.layout.item_table_list, parent, false)
        return Holder(view)
    }

    override fun onBindViewHolder(holder: Holder, position: Int) {
        holder.bind(List[position])
    }

    override fun getItemCount(): Int {
        return List.size
    }

    inner class Holder(itemView: View?) : RecyclerView.ViewHolder(itemView!!) {
        private val title = itemView?.findViewById<TextView>(R.id.item_table_list_title)
        private val base = itemView?.findViewById<TextView>(R.id.item_table_list_base)

        fun bind(schedule: Schedule) {
            title?.text = schedule.name
            if(schedule.is_default)  base?.visibility = VISIBLE
            else base?.visibility = GONE
        }
    }

}
