package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ModelPropertyPriceTable
import com.baghdadhomes.R

class AdapterPriceItems(var context : Context, var list : ArrayList<ModelPropertyPriceTable>) : RecyclerView.Adapter<AdapterPriceItems.ViewHolder>() {
    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var tvPrice : TextView = itemView.findViewById(R.id.tvPrice)
        var tvText : TextView = itemView.findViewById(R.id.tvText)
        var view : View = itemView.findViewById(R.id.view)
    }

    override fun onCreateViewHolder(
        parent: ViewGroup,
        viewType: Int
    ): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_price_item,parent,false))
    }

    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        holder.tvPrice.text = list[position].price ?: "0"
        holder.tvText.text = list[position].text ?: context.getString(R.string.total_payment)
        if (position == (list.size-1)) {
            holder.view.visibility = View.GONE
        } else {
            holder.view.visibility = View.VISIBLE
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}