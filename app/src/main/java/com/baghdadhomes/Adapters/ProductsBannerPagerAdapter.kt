package com.baghdadhomes.Adapters

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.annotation.NonNull
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ProjectSlider
import com.baghdadhomes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class ProductsBannerPagerAdapter(var bannerList:ArrayList<ProjectSlider>) :
    RecyclerView.Adapter<ProductsBannerPagerAdapter.ProductsBannerViewHolder>() {


    class ProductsBannerViewHolder(@NonNull itemView: View) : RecyclerView.ViewHolder(itemView) {
        var imageView: ImageView = itemView.findViewById(R.id.bannerImage)
        var img_watermark: ImageView = itemView.findViewById(R.id.img_watermark)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductsBannerViewHolder {
        val view: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.products_banner_pager_item, parent, false)
        return ProductsBannerViewHolder(view)
    }

    override fun getItemCount(): Int {
        return bannerList.size;
    }

    override fun onBindViewHolder(holder: ProductsBannerViewHolder, position: Int) {
        if(!bannerList.get(position).sliderImage.isNullOrEmpty()){
            Glide.with(holder.imageView.context)
                .load(bannerList[position].sliderImage)
                .placeholder(R.drawable.img_placeholder)
                .apply(
                    RequestOptions()
                        .override(600, 600)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .thumbnail(
                    Glide.with(holder.imageView.context)
                        .load(bannerList[position].sliderImage) // ✅ Use URL
                        .override(600, 600)
                )
                .into(holder.imageView)

            holder.img_watermark.visibility = View.VISIBLE
        }else{
            Glide.with(holder.imageView.context)
                .load("")
                .placeholder(R.drawable.img_placeholder)
                .apply(
                    RequestOptions()
                        .override(600, 600)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                )
                .thumbnail(
                    Glide.with(holder.imageView.context)
                        .load(bannerList[position].sliderImage) // ✅ Use URL
                        .override(600, 600)
                )
                .into(holder.imageView)

            holder.img_watermark.visibility = View.GONE
        }

    }
}