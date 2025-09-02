package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.R

  class AdapterCityProperties(var context: Context): RecyclerView.Adapter<AdapterCityProperties.ViewHolder>() {


     class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_bookmark: ImageView = itemView.findViewById<ImageView>(R.id.img_bookmark)
        var imv_property: ImageView = itemView.findViewById<ImageView>(R.id.imv_property)
        var img_premimum: ImageView = itemView.findViewById<ImageView>(R.id.img_premimum)
        var tv_details: TextView = itemView.findViewById<TextView>(R.id.tv_details)
        var tv_title: TextView = itemView.findViewById<TextView>(R.id.tv_title)
        var tv_area_property: TextView = itemView.findViewById<TextView>(R.id.tv_area_property)
        var tv_width: TextView = itemView.findViewById<TextView>(R.id.tv_width)
        var tv_bedroom: TextView = itemView.findViewById<TextView>(R.id.tv_bedroom)
        var tv_batroom: TextView = itemView.findViewById<TextView>(R.id.tv_batroom)
        var tv_price: TextView = itemView.findViewById<TextView>(R.id.tv_price)
        var tv_number_area: TextView = itemView.findViewById<TextView>(R.id.tv_number_area)
        var tv_sell: TextView = itemView.findViewById<TextView>(R.id.tv_sell)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.adapter_city_items,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
     return  10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
    }
}