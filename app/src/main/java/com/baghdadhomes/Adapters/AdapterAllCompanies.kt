package com.baghdadhomes.Adapters

import android.content.Context
import android.content.Intent
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.baghdadhomes.Activities.CompanyAdsActivity
import com.baghdadhomes.Models.AgenciesData
import com.baghdadhomes.R
import de.hdodenhof.circleimageview.CircleImageView

class AdapterAllCompanies(var context: Context, var list : ArrayList<AgenciesData>)
    : RecyclerView.Adapter<AdapterAllCompanies.ViewHolder>(){

    class ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView){
        var imgBig : ImageView = itemView.findViewById(R.id.imgBig)
        var imgCircle : CircleImageView = itemView.findViewById(R.id.imgCircle)
        var tvName : TextView = itemView.findViewById(R.id.tvName)
        var tvAddress : TextView = itemView.findViewById(R.id.tvAddress)
        var tvAdsCount : TextView = itemView.findViewById(R.id.tvAdsCount)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_companies, parent, false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(list[position].user_image.orEmpty())
            .placeholder(R.drawable.img_placeholder).into(holder.imgBig)

        Glide.with(context).load(list[position].user_image.orEmpty())
            .placeholder(R.drawable.img_placeholder).into(holder.imgCircle)

        holder.tvName.text = list[position].display_name.orEmpty()
        holder.tvAddress.text = list[position].address.orEmpty()
        holder.tvAdsCount.text = "${context.getString(R.string.ads)}:(${list[position].total_posts ?: "0"} ${context.getString(R.string.real_estate)})"

        holder.itemView.setOnClickListener {
            val intent = Intent(context, CompanyAdsActivity::class.java)
            intent.putExtra("agencyData", Gson().toJson(list[position]))
            context.startActivity(intent)
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(searchList: ArrayList<AgenciesData>){
        list = searchList
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}