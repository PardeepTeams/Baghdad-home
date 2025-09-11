package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.HomeCity
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import java.util.Locale

class HomeSelectCityAdapter(val context:Context,val cityList:ArrayList<HomeCity>,val cityClickInterface:CityClickInterface):
    RecyclerView.Adapter<HomeSelectCityAdapter.ViewHolder>() {
    private var filteredList: ArrayList<HomeCity> = ArrayList(cityList)
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
    val tvCity:TextView = itemView.findViewById(R.id.tvCity)
    val ivCheck:ImageView = itemView.findViewById(R.id.ivCheck)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.adapter_home_select_city,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return filteredList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val city: HomeCity = filteredList.get(position)
        if(PreferencesService.instance.getLanguage().equals("ar")){
            holder.tvCity.text = city.name
        }else{
            if(city.description!=null && city.description.isNotEmpty()) {
                holder.tvCity.text = city.description
            } else {
                holder.tvCity.text = city.name
            }
        }

        if(city.isSelected == true){
            holder.ivCheck.visibility = View.VISIBLE
        }else{
            holder.ivCheck.visibility = View.GONE
        }


        holder.itemView.setOnClickListener {
            filteredList.get(position).isSelected = true
            notifyDataSetChanged()
          cityClickInterface.onCityClick(city)
        }

    }


    fun filter(query: String) {
        filteredList.clear()
        if (query.isEmpty()) {
            filteredList.addAll(cityList)
        } else {
            val lowerCaseQuery = query.lowercase()
            for (city in cityList) {
                if (city.name!!.lowercase().contains(lowerCaseQuery)) {
                    filteredList.add(city)
                }
            }
        }
        notifyDataSetChanged()
    }


    interface CityClickInterface{
        fun onCityClick(homeCity: HomeCity)
    }
}