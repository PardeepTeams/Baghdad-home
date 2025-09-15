package com.baghdadhomes.Adapters

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ImageData
import com.baghdadhomes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class AdapterFloorImages(val context:Context,val imageList: ArrayList<ImageData>,var selectImage:InterfaceSelectFllorImage):
    RecyclerView.Adapter<AdapterFloorImages.ViewHolder>() {

     class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var el_main: RelativeLayout = itemView.findViewById<RelativeLayout>(R.id.el_main)
        var image_property: ImageView = itemView.findViewById<ImageView>(R.id.image_property)
        var imv_remove: ImageView = itemView.findViewById<ImageView>(R.id.imv_remove)
    }

    interface InterfaceSelectFllorImage {
        fun onSelectFloorImage(position: Int)
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view =
            LayoutInflater.from(context).inflate(R.layout.adapter_upload_images, parent, false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return imageList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val width = holder.image_property.width
        val height = holder.image_property.height
        if (imageList[position].url != null && !imageList[position].url.isEmpty()) {
            try {
                if (imageList[position].url != null) {
                    try {
                        Glide.with(context).load(imageList[position].url).placeholder(R.drawable.img_placeholder).apply(RequestOptions().override(width, height)
                            .priority(Priority.HIGH)
                            .centerCrop()
                            .diskCacheStrategy(DiskCacheStrategy.ALL)).thumbnail(Glide.with(context).load(imageList[position].url).override(width, height)).into(holder.image_property)
                    } catch (e: Exception) {
                        Log.d("exception", e.toString())
                    }

                }
            } catch (e: Exception) {
                e.printStackTrace()
                //handle exception
            }
        } else {
            holder.image_property.setImageDrawable(context.getDrawable(R.drawable.ic_upload_images))
        }
        holder.imv_remove.setOnClickListener { selectImage.onSelectFloorImage(position) }
    }
}