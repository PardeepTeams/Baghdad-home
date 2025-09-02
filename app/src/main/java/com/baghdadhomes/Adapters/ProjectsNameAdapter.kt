package com.baghdadhomes.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.R


class ProjectsNameAdapter(
        private val onClick: (String) -> Unit
    ) : RecyclerView.Adapter<ProjectsNameAdapter.VH>() {

        private val items = mutableListOf<String>()

        fun submit(data: List<String>) {
            items.clear()
            items.addAll(data)
            notifyDataSetChanged()
        }

        override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): VH {
            val tv = LayoutInflater.from(parent.context)
                .inflate(R.layout.bottom_sheet_projects_items, parent, false) as TextView
            return VH(tv, onClick)
        }

        override fun onBindViewHolder(holder: VH, position: Int) = holder.bind(items[position])
        override fun getItemCount(): Int = items.size

        class VH(private val tv: TextView, val cb: (String) -> Unit) :
            RecyclerView.ViewHolder(tv) {
            fun bind(text: String) {
                tv.text = text
                tv.setOnClickListener { cb(text) }
            }
        }


}