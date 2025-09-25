package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.PropertyPricePlan
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R

class AdapterPropertyPrice(val context:Context,var list : ArrayList<PropertyPricePlan>):
    RecyclerView.Adapter<AdapterPropertyPrice.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var tvPrice : TextView = itemView.findViewById(R.id.tvPrice)
        var tvText : TextView = itemView.findViewById(R.id.tvText)
        var view : View = itemView.findViewById(R.id.view)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.layout_price_item, parent, false)
        )
    }

    override fun getItemCount(): Int {
       return list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvPrice.text = list[position].price ?: "0"
        if(PreferencesService.instance.getLanguage().equals("ar")){
            holder.tvText.text = list[position].title
        }else{
            holder.tvText.text = list[position].title_english
        }


        if (position == (list.size-1)) {
            holder.view.visibility = View.GONE
        } else {
            holder.view.visibility = View.VISIBLE
        }
    }
}