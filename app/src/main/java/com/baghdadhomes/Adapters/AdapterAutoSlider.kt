package com.baghdadhomes.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.baghdadhomes.Models.BannerData
import com.baghdadhomes.R

class AdapterAutoSlider(
    private val context: Context,
    private val arrayList: ArrayList<BannerData>)
    : RecyclerView.Adapter<AdapterAutoSlider.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var iv_auto_image_slider: ImageView = itemView.findViewById(R.id.iv_auto_image_slider)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val inflate =
            LayoutInflater.from(parent.context).inflate(R.layout.image_slider_layout_item, parent, false)
        return ViewHolder(inflate)
    }

    override fun onBindViewHolder(viewHolder: ViewHolder, position: Int) {
        //val imageView = viewHolder.itemView.findViewById<ImageView>(R.id.iv_auto_image_slider)
        if (arrayList.size > 0) {
            Glide.with(context).load(arrayList[position].slider_image)
                .placeholder(R.drawable.img_placeholder).apply(
                    RequestOptions()
                        .override(600, 600) // Resize image (width x height)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache the image for future use
                )
                .into(viewHolder.iv_auto_image_slider)
        } else {
            Glide.with(context).load(R.drawable.img_placeholder)
                .placeholder(R.drawable.img_placeholder)
                .apply(
                    RequestOptions()
                        .override(600, 600)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                ).into(viewHolder.iv_auto_image_slider)
        }

        viewHolder.itemView.setOnClickListener {
            try {
                if (!arrayList[position].url.isNullOrEmpty()) {
                    val intent = Intent(Intent.ACTION_VIEW, Uri.parse(arrayList[position].url))
                    context.startActivity(intent)
                }
            } catch (e : Exception){
                e.localizedMessage
            }
        }
    }

    override fun getItemCount(): Int {
        return if (arrayList.size > 0) {
            arrayList.size
        } else {
            1
        }
    }

}