package com.baghdadhomes.Adapters

import android.view.LayoutInflater
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ProjectData
import com.baghdadhomes.R

class ProjectsNameAdapter(
    private val onClick: (ProjectData) -> Unit   // ✅ send full ProjectData instead of just String
) : RecyclerView.Adapter<ProjectsNameAdapter.VH>() {

    private val items = mutableListOf<ProjectData>()

    fun submit(data: List<ProjectData>) {
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

    class VH(private val tv: TextView, val cb: (ProjectData) -> Unit) :
        RecyclerView.ViewHolder(tv) {
        fun bind(item: ProjectData) {
            tv.text = item.postTitle ?: ""   // ✅ safe null handling
            tv.setOnClickListener { cb(item) }
        }
    }
}
