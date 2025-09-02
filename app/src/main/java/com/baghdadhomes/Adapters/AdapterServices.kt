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
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.baghdadhomes.Activities.ServicesSearchActivity
import com.baghdadhomes.Models.ServicesResponse
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R

class AdapterServices(var context:Context, var list: ArrayList<ServicesResponse>)
    : RecyclerView.Adapter<AdapterServices.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var img_service:ImageView = itemView.findViewById(R.id.img_service)
        var ic_service:ImageView = itemView.findViewById(R.id.ic_service)
        var tv_service_name:TextView = itemView.findViewById(R.id.tv_service_name)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.layout_services, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (list.get(position).thumbnail != null){
            Glide.with(context).load(list.get(position).thumbnail)
                .placeholder(R.drawable.img_placeholder).into(holder.img_service)
        } else{
            Glide.with(context).load(R.drawable.img_placeholder)
                .placeholder(R.drawable.img_placeholder).into(holder.img_service)
        }

        if (list.get(position).icon != null){
            Glide.with(context).load(list.get(position).icon).placeholder(R.drawable.img_placeholder)
                .apply(RequestOptions().override(300, 300).diskCacheStrategy(DiskCacheStrategy.ALL))
                .into(holder.ic_service)
        } else{
            Glide.with(context).load(R.drawable.img_placeholder)
                .apply(RequestOptions().override(300, 300).diskCacheStrategy(DiskCacheStrategy.ALL))
                .placeholder(R.drawable.img_placeholder).into(holder.ic_service)
        }


        if (list.get(position).postTitle != null) {
            var language = PreferencesService.instance.getLanguage()
            if (language == "ar"){
                holder.tv_service_name.text = list.get(position).arabic_title
            } else{
                holder.tv_service_name.text = list.get(position).postTitle
            }
            //holder.tv_service_name.text = list.get(position).postTitle
        }


        holder.itemView.setOnClickListener {
            var intent = Intent(context, ServicesSearchActivity::class.java)
            intent.putExtra("id", list.get(position).id.toString())
            intent.putExtra("postTitle", list.get(position).postTitle.toString())
            intent.putExtra("arabic_title", list.get(position).arabic_title.toString())
            context.startActivity(intent)
            //context.startActivity(Intent(context, ServicesSearchActivity::class.java))
        }
    }

    override fun getItemCount(): Int {
       return list.size
    }

    fun updateList(searchList: ArrayList<ServicesResponse>){
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