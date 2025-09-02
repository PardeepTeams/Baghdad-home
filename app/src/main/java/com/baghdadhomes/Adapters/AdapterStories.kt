package com.baghdadhomes.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.baghdadhomes.Activities.VideoViewActivity
import com.baghdadhomes.Models.ReelResult
import com.baghdadhomes.Models.ReelsData
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.DelicateCoroutinesApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.GlobalScope
import kotlinx.coroutines.launch


class AdapterStories(var context: Context, var list: ArrayList<ReelsData>, var onClick : StoryClick)
    : RecyclerView.Adapter<AdapterStories.ViewHolder>(){
    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        var imgPreviewVideo : CircleImageView = itemView.findViewById(R.id.imgPreviewVideo)
        var rlBg : RelativeLayout = itemView.findViewById(R.id.rlBg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_stories,parent,false))
    }

    @OptIn(DelicateCoroutinesApi::class)
    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        holder.itemView.setOnClickListener {
           // onClick.onStoryClick(position)
            GlobalScope.launch(Dispatchers.Main) {
                val model = ReelResult(0, "", true, list)
                Constants.reelsModel = model
                val intent = Intent(context, VideoViewActivity::class.java)
                intent.putExtra("position", position)
                context.startActivity(intent)
            }
        }

        holder.imgPreviewVideo.setImageResource(R.drawable.img_placeholder)
        if (list.get(position).status == "0"){
            holder.rlBg.background = ContextCompat.getDrawable(context,R.drawable.bg_gradient_circle)
        } else{
            holder.rlBg.background = ContextCompat.getDrawable(context,R.drawable.bg_story_seen)
        }

        Glide.with(context).load(list.get(position).video_thumbnail)
            .apply(RequestOptions()
                .centerCrop()
                .diskCacheStrategy(DiskCacheStrategy.ALL))
            .placeholder(R.drawable.img_placeholder).into(holder.imgPreviewVideo)

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