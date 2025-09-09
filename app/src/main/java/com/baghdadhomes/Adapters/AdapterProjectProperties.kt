package com.baghdadhomes.Adapters

import android.annotation.SuppressLint
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ChildProperty
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.bumptech.glide.Glide

class AdapterProjectProperties(
    private val context: Context,
    private val projectPropertyActions: ProjectPropertyActions,
    private val list: ArrayList<ChildProperty>,
) : RecyclerView.Adapter<AdapterProjectProperties.ViewHolder>() {

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_bookmark: ImageView = itemView.findViewById<ImageView>(R.id.img_bookmark)
        var imv_property: ImageView = itemView.findViewById<ImageView>(R.id.imv_property)
        var img_premimum: ImageView =itemView.findViewById<ImageView>(R.id.img_premimum)
        var tv_details: TextView = itemView.findViewById<TextView>(R.id.tv_details)
        var tv_title: TextView = itemView.findViewById<TextView>(R.id.tv_title)
        var tv_area_property: TextView = itemView.findViewById<TextView>(R.id.tv_area_property)
        var tv_width: TextView = itemView.findViewById<TextView>(R.id.tv_width)
        var tv_bedroom: TextView = itemView.findViewById<TextView>(R.id.tv_bedroom)
        var tv_batroom: TextView = itemView.findViewById<TextView>(R.id.tv_batroom)
        var tv_price: TextView = itemView.findViewById<TextView>(R.id.tv_price)
        var tv_number_area: TextView = itemView.findViewById<TextView>(R.id.tv_number_area)
        var tv_sell: TextView = itemView.findViewById<TextView>(R.id.tv_sell)
        var img_watermark: ImageView = itemView.findViewById<ImageView>(R.id.img_watermark)
    }


    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_detail_ads, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, @SuppressLint("RecyclerView") position: Int) {
        // holder.setIsRecyclable(false);
        val land = "شقة"
        val apartments = "اراضي"
        if (list[position].isPremium == true) {
            holder.img_premimum.setVisibility(View.VISIBLE)
        } else {
            holder.img_premimum.setVisibility(View.GONE)
        }

        holder.tv_width.text = "${context.getString(R.string.street_width)} ${list[position].land ?: "0"}${context.resources.getString(R.string.m)}"

        if (list[position].propertyType?.contains(land) == true
            || list[position].propertyType?.contains(apartments) == true) {
            holder.tv_batroom.visibility = View.GONE
            holder.tv_bedroom.visibility = View.GONE
        } else {
            holder.tv_batroom.visibility = View.VISIBLE
            holder.tv_bedroom.visibility = View.VISIBLE
        }

        if (!list[position].propertyAttr?.propertyStatus.isNullOrEmpty()) {
            holder.tv_sell.visibility = View.VISIBLE
            holder.tv_sell.text = list[position].propertyAttr?.propertyType?:""
        } else {
            holder.tv_sell.visibility = View.GONE
        }

        if(!list.get(position).thumbnail.isNullOrEmpty()){
            Glide.with(context).load(list[position].thumbnail?:"")
                .placeholder(R.drawable.img_placeholder)
                .into(holder.imv_property)
            holder.img_watermark.visibility = View.VISIBLE
        }else{
            holder.img_watermark.visibility = View.GONE
            Glide.with(context).load("")
                .placeholder(R.drawable.img_placeholder)
                .into(holder.imv_property)
        }


        holder.tv_title.text = (list[position].postTitle ?: "").trim()

        holder.tv_bedroom.text = list[position].bedrooms?:"0"
        holder.tv_batroom.text = list[position].bathrooms?:"0"
        holder.tv_number_area.text = "${list[position].size?:"0"}${context.resources.getString(R.string.m)}"
        holder.tv_price.text = "(${list[position].price?:"0"})"
        var currency = if ((list[position].faveCurrency?:"") == "USD") {
            context.getString(R.string.currency_code_usd)
        } else {
            context.getString(R.string.currency_code)
        }
        holder.tv_price.text = "${holder.tv_price.text}$currency"

        val html = list[position].postContent?:""
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            holder.tv_details.text = Html.fromHtml(html, 0)
        } else {
            holder.tv_details.text = Html.fromHtml(html)
        }

        if (!list[position].propertyAddress?.propertyArea.isNullOrEmpty()) {
            holder.tv_area_property.visibility = View.VISIBLE
            holder.tv_area_property.text = list[position].propertyAddress?.propertyArea?:""
        } else {
            holder.tv_area_property.visibility = View.GONE
        }

        holder.itemView.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                projectPropertyActions.openNextActivity(list[position], position)
            }
        })

        holder.img_bookmark.setVisibility(View.VISIBLE)
        if (list[position].isFav == true) {
            holder.img_bookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart))
        } else {
            holder.img_bookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline))
        }


        holder.img_bookmark.setOnClickListener(object : View.OnClickListener {
            override fun onClick(v: View?) {
                //holder.img_bookmark.setColorFilter(holder.img_bookmark.getContext().getResources().getColor(R.color.light_red), PorterDuff.Mode.SRC_ATOP);
                val isLogged = PreferencesService.instance.userLoginStatus == true
                if (isLogged) {
                    /*if (propertiesList[position].isFav == true) {
                        holder.img_bookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart_outline))
                    } else {
                        holder.img_bookmark.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.ic_heart))
                    }*/
                    projectPropertyActions.addRemoveFav(list[position], position)
                } else {
                    projectPropertyActions.openLoginActivity()
                }
            }
        })
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface ProjectPropertyActions {
        fun openNextActivity(model: ChildProperty?, position: Int)
        fun addRemoveFav(model: ChildProperty?, position: Int)
        fun openLoginActivity()
    }
}
