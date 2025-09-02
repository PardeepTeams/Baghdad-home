package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.NBHDArea
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R

class AdapterNBHDDialog(var context: Context, var list: ArrayList<NBHDArea>,
                        var clickInteface: onClick):
    RecyclerView.Adapter<AdapterNBHDDialog.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tv_spinner:TextView = itemView.findViewById(R.id.tv_spinner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        var view = LayoutInflater.from(context).inflate(R.layout.spinner_store_adapter, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if(PreferencesService.instance.getLanguage().equals("ar")){
            holder.tv_spinner.text = list.get(position).name
        }else{

            if(list.get(position).description!=null && list.get(position).description.isNotEmpty()){
                holder.tv_spinner.text = list.get(position).description
            }else{
                holder.tv_spinner.text = list.get(position).name
            }
        }


        holder.itemView.setOnClickListener {
            clickInteface.onItemClick(list.get(position))
        }
    }

    fun updateList(searchList: java.util.ArrayList<NBHDArea>){
        list = searchList
        notifyDataSetChanged()
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface onClick{
        fun onItemClick(model: NBHDArea)
    }

}

/*class AdapterNBHDDialog(var context: Context, var list: ArrayList<NBHDResponse>,
                        var clickInteface: onClick):
    RecyclerView.Adapter<AdapterNBHDDialog.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var tv_spinner:TextView = itemView.findViewById(R.id.tv_spinner)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
      var view = LayoutInflater.from(context).inflate(R.layout.spinner_store_adapter, parent, false)
      return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_spinner.text = list.get(position).name

        holder.itemView.setOnClickListener {
            clickInteface.onItemClick(list.get(position))
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    interface onClick{
        fun onItemClick(model: NBHDResponse)
    }

}*/
