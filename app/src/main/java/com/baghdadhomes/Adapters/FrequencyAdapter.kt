package com.baghdadhomes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.FrequencyModel
import com.baghdadhomes.R

class FrequencyAdapter(
    private val frequencies: ArrayList<FrequencyModel>,
    private val onItemSelected: (Int) -> Unit
) : RecyclerView.Adapter<FrequencyAdapter.ViewHolder>() {

    private var selectedPosition = RecyclerView.NO_POSITION

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var txtFrequency: TextView = itemView.findViewById(R.id.txtFrequency)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_frequency, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val frequency:FrequencyModel = frequencies.get(position)
        holder.txtFrequency.text = frequency.name
        holder.itemView.isSelected = frequency.isSelected
        val isSelected = frequency.isSelected

        holder.txtFrequency.setTextColor(
            if (isSelected) holder.itemView.context.getColor(R.color.blue)
            else holder.itemView.context.getColor(R.color.black)
        )
        if(isSelected){
            holder.txtFrequency.background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.bg_amenity_selector)
        }else{
            holder.txtFrequency.background = ContextCompat.getDrawable(holder.itemView.context,R.drawable.bg_amenity)

        }

        holder.itemView.setOnClickListener {
          onItemSelected.invoke(position)
        }
    }

    override fun getItemCount(): Int = frequencies.size
}
