package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.baghdadhomes.Models.ReelsData
import com.baghdadhomes.R

class AdapterStoriesAgency (var context: Context, var list: ArrayList<ReelsData>, var onClick : StoryClick)
    : RecyclerView.Adapter<AdapterStoriesAgency.ViewHolder>(){

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        var imgPreviewVideo : ImageView = itemView.findViewById(R.id.imgPreviewVideo)
        var tvTitle : TextView = itemView.findViewById(R.id.tvTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_agecny_reels,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(list[position].video.orEmpty())
            .thumbnail(Glide.with(context).load(list.get(position).video).override(300, 300))
            .placeholder(R.drawable.img_placeholder).into(holder.imgPreviewVideo)

        holder.tvTitle.text = list[position].title.orEmpty()

        holder.itemView.setOnClickListener {
            onClick.onStoryClick(position)
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

    interface StoryClick{
        fun onStoryClick(position: Int)
    }
}