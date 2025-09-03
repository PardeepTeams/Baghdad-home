package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ModelPropertyBedrooms
import com.baghdadhomes.R


class AdapterFloorBedrooms (var context: Context, var list : ArrayList<ModelPropertyBedrooms>, var onAreaItemClickInterface: AreaItemClickInterface)
    : RecyclerView.Adapter<AdapterFloorBedrooms.ViewHolder>(){
    class  ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var tvArea : TextView = itemView.findViewById(R.id.tvArea)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_areas_item,parent,false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.tvArea.text = "${list[position].bedrooms ?: "0"} ${context.getString(R.string.bedroom)}"
        if (list[position].isSelected == true) {
            holder.tvArea.background = ContextCompat.getDrawable(context, R.drawable.bg_area_selected)
            holder.tvArea.setTextColor( ContextCompat.getColor(context, R.color.blue))
        } else {
            holder.tvArea.background = ContextCompat.getDrawable(context, R.drawable.bg_area_item)
            holder.tvArea.setTextColor( ContextCompat.getColor(context, R.color.darkGrey))
        }

        holder.itemView.setOnClickListener {
            onAreaItemClickInterface.onAreaItemClick(position)
        }
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    interface AreaItemClickInterface {
        fun onAreaItemClick(position: Int)
    }
}