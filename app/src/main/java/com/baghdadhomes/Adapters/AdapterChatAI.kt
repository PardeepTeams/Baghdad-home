package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ModelAiSearchResponse
import com.baghdadhomes.Models.Result
import com.baghdadhomes.R

class AdapterChatAI(val context:Context, var chatList:ArrayList<ModelAiSearchResponse>): RecyclerView.Adapter<AdapterChatAI.ViewHolder>() {
    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val rv_properties:RecyclerView = itemView.findViewById(R.id.rv_properties)
        var llMyMsg : LinearLayout = itemView.findViewById(R.id.llMyMsg)
        var llOtherMsg : LinearLayout = itemView.findViewById(R.id.llOtherMsg)
        var tvMyMsg : TextView = itemView.findViewById(R.id.tvMyMsg)
        var tvOtherMsg : TextView = itemView.findViewById(R.id.tvOtherMsg)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view:View = LayoutInflater.from(context).inflate(R.layout.adapter_chat_ai,parent,false)
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
       return chatList.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if(!chatList.get(position).searchText.isNullOrEmpty()){
            holder.llMyMsg.visibility = View.VISIBLE
        }else{
            holder.llMyMsg.visibility = View.GONE
        }
       holder.tvMyMsg.text = chatList.get(position).searchText
       holder.tvOtherMsg.text = chatList.get(position).message
        holder.rv_properties.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        if(!chatList.get(position).result.isNullOrEmpty()){
            holder.rv_properties.visibility = View.VISIBLE
         //   holder.rv_properties.adapter =AdapterDetailAds(context,)

        }else{
            holder.rv_properties.visibility = View.GONE
        }



    }
}