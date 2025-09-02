package com.baghdadhomes.Adapters

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.baghdadhomes.Models.NBHDDataResponse
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R

class SpinnerCityAdapter(val context: Context, var dataSource: ArrayList<NBHDDataResponse>) : BaseAdapter() {
    private val inflater: LayoutInflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    override fun getCount(): Int {
        return dataSource.size;
    }

    override fun getItem(position: Int): Any {
        return dataSource[position];
    }

    override fun getItemId(position: Int): Long {
        return position.toLong();
    }

    override fun getView(position: Int, convertView: View?, parent: ViewGroup?): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.spinner_store_adapter, parent, false)
            vh = ItemHolder(view)
            view?.tag = vh
        } else {
            view = convertView
            vh = view.tag as ItemHolder
        }

        val color: Int = Color.parseColor("#484848")

        try {
            if(PreferencesService.instance.getLanguage().equals("ar")){
                vh.label.text = dataSource.get(position).name
            }else{
                if(dataSource.get(position).description!=null && dataSource.get(position).description.isNotEmpty()){
                    vh.label.text = dataSource.get(position).description
                }else{
                    vh.label.text = dataSource.get(position).name
                }

            }

            if(vh.label.text.equals("Store Name")){
                vh.label.setTextColor(Color.parseColor("#D0C9D6"))
            }else{
                vh.label.setTextColor(Color.parseColor("#484848"))
            }
        }catch (e: Exception){}
        return view
    }


    private class ItemHolder(row: View?) {
        val label: TextView

        init {
            label = row?.findViewById(R.id.tv_spinner) as TextView

        }
    }

    fun getPosition(position: String): String {
        return position
    }
}

/*
class SpinnerAdapter(context: Context, spinnerList: ArrayList<NBHDResponse>) :
    BaseAdapter() {
    private var spinnerList = ArrayList<NBHDResponse>()
    private val inflater: LayoutInflater

    override fun getCount(): Int {
        return spinnerList.size
    }

    override fun getItem(position: Int): Any {
        return spinnerList[position]
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getView(position: Int, convertView: View, parent: ViewGroup): View {
        val view: View
        val vh: ItemHolder
        if (convertView == null) {
            view = inflater.inflate(R.layout.spinner_store_adapter, parent, false)
            vh = ItemHolder(view)
            view.tag = vh
        } else {
            view = inflater.inflate(R.layout.spinner_store_adapter, parent, false)
            vh = ItemHolder(view)
            view.tag = vh
        }
        try {
            vh.label.text = spinnerList.get(position).name
            */
/*if (vh.label.text.toString() == "Choose your vehicle type") {
                vh.label.setTextColor(Color.parseColor("#636363"))
            } else {
                vh.label.setTextColor(Color.parseColor("#ff000000"))
            }*//*

        } catch (e: Exception) {
        }
        return view
    }

    private inner class ItemHolder(var row: View) {
        var label: TextView

        init {
            label = row.findViewById<View>(R.id.tv_spinner) as TextView
        }
    }

    init {
        this.spinnerList = spinnerList
        inflater = context.getSystemService(Context.LAYOUT_INFLATER_SERVICE) as LayoutInflater
    }
}*/
