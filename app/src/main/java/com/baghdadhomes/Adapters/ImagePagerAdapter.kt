package com.baghdadhomes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions

class ImagePagerAdapter(private val imageUrls: List<String>) :
    RecyclerView.Adapter<ImagePagerAdapter.ImageViewHolder>() {

    inner class ImageViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        val imageView: ImageView = view.findViewById(R.id.imageItem)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ImageViewHolder {
        val view = LayoutInflater.from(parent.context)
            .inflate(R.layout.item_image, parent, false)
        return ImageViewHolder(view)
    }

    override fun onBindViewHolder(holder: ImageViewHolder, position: Int) {

        Glide.with(holder.imageView.context).load(imageUrls.get(position))
            .placeholder(R.drawable.img_placeholder).apply(
                RequestOptions()
                    .override(600, 600) // Resize image (width x height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache the image for future use
            )
            .into(holder.imageView)
    }

    override fun getItemCount(): Int =1
}
