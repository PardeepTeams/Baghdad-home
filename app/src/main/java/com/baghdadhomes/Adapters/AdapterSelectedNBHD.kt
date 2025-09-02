package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.NBHDArea
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R

class AdapterSelectedNBHD(var context: Context, var list: ArrayList<NBHDArea>):
    RecyclerView.Adapter<AdapterSelectedNBHD.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tv_nbhd:TextView = itemView.findViewById(R.id.tv_nbhd)
        var img_cancel:ImageView = itemView.findViewById(R.id.img_cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.layout_selected_nbhd, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: AdapterSelectedNBHD.ViewHolder, position: Int) {
        if(PreferencesService.instance.getLanguage().equals("ar")){
            holder.tv_nbhd.text = list.get(position).name
        }else{
            if(list.get(position).description!=null && list.get(position).description.isNotEmpty()){
                holder.tv_nbhd.text = list.get(position).description
            }else{
                holder.tv_nbhd.text = list.get(position).name
            }

        }

        holder.img_cancel.setOnClickListener {
            //list.get(position).re
            list.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}


/*class AdapterSelectedNBHD(var context: Context, var list: ArrayList<NBHDResponse>):
 RecyclerView.Adapter<AdapterSelectedNBHD.ViewHolder>(){

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tv_nbhd:TextView = itemView.findViewById(R.id.tv_nbhd)
        var img_cancel:ImageView = itemView.findViewById(R.id.img_cancel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int):ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.layout_selected_nbhd, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (PreferencesService.instance.getLanguage() == "ar"){
            holder.tv_nbhd.text = list.get(position).name
        } else{
            holder.tv_nbhd.text = list.get(position).description
        }

        holder.img_cancel.setOnClickListener {
            //list.get(position).re
            list.removeAt(position)
            notifyDataSetChanged()
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }
}*/
