package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseAdapter
import android.widget.TextView
import com.baghdadhomes.Models.NBHDArea
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R

class SpinnerNBHDAdapter (val context: Context, var dataSource: ArrayList<NBHDArea>) : BaseAdapter() {
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
        }catch (e: Exception){}
        return view
    }


    private class ItemHolder(row: View?) {
        val label: TextView

        init {
            label = row?.findViewById(R.id.tv_spinner) as TextView

        }
    }
}