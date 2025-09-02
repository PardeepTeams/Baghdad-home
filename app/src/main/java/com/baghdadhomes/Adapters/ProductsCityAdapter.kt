package com.baghdadhomes.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Activities.CityProductActivity
import com.baghdadhomes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class ProductsCityAdapter(var context:Context,var imageList:ArrayList<String>): RecyclerView.Adapter<ProductsCityAdapter.CityViewHolder>() {

    class CityViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        val cityImage: ImageView = itemView.findViewById(R.id.cityImage)
        val cityName: TextView = itemView.findViewById(R.id.cityName)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): CityViewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.products_city_items, parent, false)
        return CityViewHolder(view)
    }

    override fun getItemCount(): Int {
       return imageList.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val width: Int = holder.cityImage.getWidth()
        val height: Int = holder.cityImage.getHeight()

        holder.itemView.setOnClickListener {
            context.startActivity(Intent(context,CityProductActivity::class.java))
        }
        Glide.with(context).load(imageList.get(position)).placeholder(R.drawable.img_placeholder)
            .apply(
                RequestOptions().override(300, 300)
                    .priority(Priority.HIGH)
                    .centerCrop().diskCacheStrategy
                        (DiskCacheStrategy.ALL)
            ).thumbnail(Glide.with(context).load(imageList.get(position)).override(width, height)).
        into(holder.cityImage)
    }
}