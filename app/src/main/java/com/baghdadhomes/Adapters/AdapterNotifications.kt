package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.CheckBox
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.NotificationsList
import com.baghdadhomes.R

class AdapterNotifications(
    var context:Context, var notificationList: ArrayList<NotificationsList>,
    var notificationClick:NotificationClick, var isSelectionEnabled : Boolean):
    RecyclerView.Adapter<AdapterNotifications.ViewHolder>() {

    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
       var tv_title:TextView = itemView.findViewById(R.id.tv_title)
       var tv_body:TextView = itemView.findViewById(R.id.tv_body)
       var imv_clear:ImageView = itemView.findViewById(R.id.imv_clear)
       var imv_check:CheckBox = itemView.findViewById(R.id.imv_check)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.adapter_notifications,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder( holder: ViewHolder, position: Int) {
        if (isSelectionEnabled){
            holder.imv_clear.visibility = View.GONE
            holder.imv_check.visibility = View.VISIBLE
        } else{
            holder.imv_clear.visibility = View.VISIBLE
            holder.imv_check.visibility = View.GONE
        }
        holder.tv_title.text = notificationList.get(position).title
        holder.tv_body.text = notificationList.get(position).description

        holder.imv_check.isChecked = notificationList[position].isSelected == true

        holder.imv_check.isClickable = false

        holder.imv_clear.setOnClickListener {
            notificationClick.onNotificationRemove(notificationList[position], position)
        }

        holder.itemView.setOnClickListener {
            if (isSelectionEnabled){
                notificationList[position].isSelected = notificationList[position].isSelected != true
                notifyItemChanged(position)
            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    interface NotificationClick{
        fun onNotificationRemove(model:NotificationsList, position: Int)
    }
}