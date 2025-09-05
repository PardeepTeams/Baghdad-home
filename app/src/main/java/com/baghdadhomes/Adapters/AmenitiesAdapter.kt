package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.AmenityModel
import com.baghdadhomes.R

class AmenitiesAdapter(var context:Context, private val amenities: ArrayList<AmenityModel>): RecyclerView.Adapter<AmenitiesAdapter.ViewHolder>() {

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        val imgAmenity: ImageView = itemView.findViewById(R.id.imgAmenity)
        val txtAmenity: TextView = itemView.findViewById(R.id.txtAmenity)
        val lin_main: LinearLayout = itemView.findViewById(R.id.lin_main)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.adapter_amenities,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return amenities.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val amenity: AmenityModel = amenities.get(position)
        holder.txtAmenity.text = amenity.name
        holder.imgAmenity.setImageResource(amenity.icon)
        holder.itemView.isSelected = amenity.isSelected

        if(amenity.isSelected){
            holder.txtAmenity.setTextColor(ContextCompat.getColor(context,R.color.blue))
            holder.lin_main.background = ContextCompat.getDrawable(context,R.drawable.bg_amenity_selector)

        }else{
            holder.txtAmenity.setTextColor(ContextCompat.getColor(context,R.color.blackNew))
            holder.lin_main.background = ContextCompat.getDrawable(context,R.drawable.bg_amenity)
        }

        holder.itemView.setOnClickListener {
            amenity.isSelected = !amenity.isSelected

        }
    }
}