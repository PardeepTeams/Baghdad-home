package com.baghdadhomes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.R

class CommonBottomSheetSelectedAdapter( private val items: List<String>,
                                        private val onItemClick: (String) -> Unit):
    RecyclerView.Adapter<CommonBottomSheetSelectedAdapter.ViewHolder>(){

    inner class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val txtItem: TextView = itemView.findViewById(R.id.txtItem)

        fun bind(item: String) {
            txtItem.text = item
            itemView.setOnClickListener {
                onItemClick(item)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_real_estate, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return  items.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bind(items[position])
    }
}