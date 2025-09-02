package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.baghdadhomes.R

class AdapterAutoSliderDetailPage(
    private val context: Context,
    private val arrayList: ArrayList<String>
) : RecyclerView.Adapter<AdapterAutoSliderDetailPage.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_auto_image_slider: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
        var img_watermark: ImageView = itemView.findViewById(R.id.img_watermark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.layout_detail_page_slider, parent,false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (arrayList.size > 0) {
            //waterMark.setVisibility(View.VISIBLE);
            Glide.with(context).load(arrayList[position]).placeholder(R.drawable.img_placeholder)
                .apply(
                    RequestOptions()
                        .override(600, 600)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                ). thumbnail(Glide.with(context).load(arrayList[position]).
                override(600, 600))
                .into(holder.iv_auto_image_slider)
        } else {
            //waterMark.setVisibility(View.GONE);
            Glide.with(context).load(R.drawable.img_placeholder)
                .apply(
                    RequestOptions()
                        .override(600, 600)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .placeholder(R.drawable.img_placeholder).into(holder.iv_auto_image_slider)
        }
    }

    override fun getItemCount(): Int {
        return if (arrayList.size > 0){
            arrayList.size
        } else {
            1
        }
    }

}
