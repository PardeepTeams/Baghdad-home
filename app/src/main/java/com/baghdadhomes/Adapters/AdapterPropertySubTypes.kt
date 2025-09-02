package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.PropertySubTypesModel
import com.baghdadhomes.R

class AdapterPropertySubTypes(var context: Context, var list : ArrayList<PropertySubTypesModel>, var onClick : SubTypeClick)
    : RecyclerView.Adapter<AdapterPropertySubTypes.ViewHolder>(){

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        var rlOutside : RelativeLayout = itemView.findViewById(R.id.rlOutside)
        var imgType : ImageView = itemView.findViewById(R.id.imgType)
        var tvTypeName : TextView = itemView.findViewById(R.id.tvTypeName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_sub_types,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.imgType.setImageDrawable(list.get(position).image)
        holder.tvTypeName.text = list.get(position).name

        if (list.get(position).isSelected == true){
            holder.rlOutside.background = ContextCompat.getDrawable(context,R.drawable.bg_outline_blue)
            holder.imgType.drawable.setTint(ContextCompat.getColor(context,R.color.skyBlue))
            holder.tvTypeName.setTextColor(ContextCompat.getColor(context, R.color.skyBlue))
        } else{
            holder.rlOutside.background = ContextCompat.getDrawable(context,R.drawable.bg_outline_solid)
            holder.imgType.drawable.setTint(ContextCompat.getColor(context,R.color.grey))
            holder.tvTypeName.setTextColor(ContextCompat.getColor(context, R.color.grey))
        }

        holder.itemView.setOnClickListener {
            onClick.onSubTypeClick(position)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface SubTypeClick{
        fun onSubTypeClick(position: Int)
    }
}