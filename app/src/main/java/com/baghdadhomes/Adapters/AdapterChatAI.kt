package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.R

class AdapterChatAI(val context:Context): RecyclerView.Adapter<AdapterChatAI.ViewHolder>() {


    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val rv_properties:RecyclerView = itemView.findViewById(R.id.rv_properties)
        var llMyMsg : LinearLayout = itemView.findViewById(R.id.llMyMsg)
        var llOtherMsg : LinearLayout = itemView.findViewById(R.id.llOtherMsg)
        var tvMyMsg : TextView = itemView.findViewById(R.id.tvMyMsg)
        var tvOtherMsg : TextView = itemView.findViewById(R.id.tvOtherMsg)
        var tvMyTime : TextView = itemView.findViewById(R.id.tvMyTime)
        var tvOtherTime : TextView = itemView.findViewById(R.id.tvOtherTime)

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.adapter_chat_ai,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return 10
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
      holder.rv_properties.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)


    }
}