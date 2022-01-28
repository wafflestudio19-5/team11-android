package com.example.toyproject.ui.main.notifyFragment

import android.graphics.Color
import android.text.Layout
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.example.toyproject.R
import com.example.toyproject.databinding.ItemNotificationBinding
import com.example.toyproject.network.dto.MessageRoom
import com.example.toyproject.network.dto.Notification

class NotifyRecyclerViewAdapter : RecyclerView.Adapter<RecyclerView.ViewHolder>(){

    private var notifications: MutableList<Notification> = mutableListOf()
    inner class NotificationViewHolder(val binding: ItemNotificationBinding) : RecyclerView.ViewHolder(binding.root)

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        val binding = ItemNotificationBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return NotificationViewHolder(binding)
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        val data = notifications[position]
        when(holder){
            is NotificationViewHolder -> {
                holder.binding.apply{
                    notificationBoard.text = data.board_name
                    notificationContent.text = data.text
                    notificationTime.text = data.created_at
                    if(!data.unread) itemNotification.setBackgroundColor(Color.parseColor("#FFFFFF"))
                    root.setOnClickListener {
                        itemClickListener.onItemClick(root, data, position)
                    }
                }
            }
        }
    }

    override fun getItemCount(): Int {
        return notifications.size
    }

    fun setNotification(notifications: MutableList<Notification>){
        this.notifications = notifications
        this.notifyDataSetChanged()
    }

    fun resetNotification(){
        this.notifications.clear()
        this.notifyDataSetChanged()
    }

    private lateinit var itemClickListener: OnItemClickListener

    interface OnItemClickListener {
        fun onItemClick(v: View, data: Notification, position: Int)
    }

    fun setItemClickListener(onItemClickListener: OnItemClickListener){
        this.itemClickListener = onItemClickListener
    }


}