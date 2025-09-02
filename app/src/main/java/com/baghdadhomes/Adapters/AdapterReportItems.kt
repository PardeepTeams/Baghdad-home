package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ReportModel
import com.baghdadhomes.R

class AdapterReportItems(var context:Context,var reportModel:ArrayList<ReportModel>,
var reason:selectedReason):
    RecyclerView.Adapter<AdapterReportItems.ViewHolder>() {

    class ViewHolder(itemView:View): RecyclerView.ViewHolder(itemView){
      var rd_circle_item:ImageView = itemView.findViewById(R.id.rd_circle_item)
      var tv_title_text:TextView = itemView.findViewById(R.id.tv_title_text)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
       var view:View = LayoutInflater.from(context).inflate(R.layout.adapter_report_items,parent,false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tv_title_text.setText(reportModel.get(position).text)
        if(reportModel.get(position).isChecked == true){
            holder.rd_circle_item.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_blue_circle))
        }else{
            holder.rd_circle_item.setImageDrawable(null)
        }

        holder.itemView.setOnClickListener {
            reason.selectReason(reportModel.get(position), position)
           /* for(i in reportModel){
                if(i.isChecked == true){
                    i.isChecked = false
                }
            }

            reportModel.get(position).isChecked = true
            notifyDataSetChanged()*/
        }
    }

    override fun getItemCount(): Int {
       return reportModel.size
    }

    interface selectedReason{
        fun selectReason(model: ReportModel, position: Int)
    }
}