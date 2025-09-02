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
import com.baghdadhomes.Models.ProjectCity
import com.baghdadhomes.R
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions


class ProductsCityAdapter(var context:Context,var cityList:ArrayList<ProjectCity>): RecyclerView.Adapter<ProductsCityAdapter.CityViewHolder>() {

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
       return cityList.size
    }

    override fun onBindViewHolder(holder: CityViewHolder, position: Int) {
        val width: Int = holder.cityImage.getWidth()
        val height: Int = holder.cityImage.getHeight()

        holder.cityName.text = cityList.get(position).name

        holder.itemView.setOnClickListener {
            val intent:Intent = Intent(context,CityProductActivity::class.java)
            intent.putExtra("city_id",cityList.get(position).termId.toString())
            intent.putExtra("city_name",cityList.get(position).name.toString())
            context.startActivity(intent)
        }


        if(cityList.get(position).image!=null){
            Glide.with(holder.cityImage.context).load(cityList.get(position).image)
                .placeholder(R.drawable.img_placeholder).apply(
                    RequestOptions()
                        .override(width, height) // Resize image (width x height)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)  // Cache the image for future use
                )
                .into(holder.cityImage)
        }else{
            Glide.with(context).load(R.drawable.img_placeholder). apply(
                RequestOptions()
                    .override(width, height) // Resize image (width x height)
                    .diskCacheStrategy(DiskCacheStrategy.ALL)
                    .priority(Priority.HIGH)// Cache the image for future use
            )
                .placeholder(R.drawable.img_placeholder).into(holder.cityImage)
        }
    }
}