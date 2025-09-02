package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.baghdadhomes.R

class AdapterCompaniesSlider(private val context: Context,var imageList:ArrayList<String>) :
    RecyclerView.Adapter<AdapterCompaniesSlider.ViewHolder>() {

    class ViewHolder(itemView: View) :RecyclerView. ViewHolder(itemView) {
        var iv_auto_image_slider: ImageView  = itemView.findViewById(R.id.iv_auto_image_slider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, parent,false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        // viewHolder.iv_auto_image_slider.setImageDrawable(context.resources.getDrawable(R.drawable.img_company))
        if (imageList.size > 0){
            Glide.with(context).load(imageList.get(position)).placeholder(R.drawable.img_placeholder).into(viewHolder.iv_auto_image_slider)
        } else {
            Glide.with(context).load(R.drawable.img_placeholder).placeholder(R.drawable.img_placeholder).into(viewHolder.iv_auto_image_slider)
        }
    }

    override fun getItemCount(): Int {
        return if (imageList.size > 0){
            imageList.size
        } else {
            1
        }
    }
}