package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ModelMessages
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Utility

class AdapterMessage(var context : Context, private var myId : String, var list : ArrayList<ModelMessages>)
    : RecyclerView.Adapter<AdapterMessage.ViewHolder>() {

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        var llMyMsg : LinearLayout = itemView.findViewById(R.id.llMyMsg)
        var llOtherMsg : LinearLayout = itemView.findViewById(R.id.llOtherMsg)
        var tvMyMsg : TextView = itemView.findViewById(R.id.tvMyMsg)
        var tvOtherMsg : TextView = itemView.findViewById(R.id.tvOtherMsg)
        var tvMyTime : TextView = itemView.findViewById(R.id.tvMyTime)
        var tvOtherTime : TextView = itemView.findViewById(R.id.tvOtherTime)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_messages,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        if (list[position].sender == myId){
            holder.llOtherMsg.visibility = View.GONE
            holder.llMyMsg.visibility = View.VISIBLE

            holder.tvMyMsg.text = list[position].message
            holder.tvMyTime.text = Utility.getMessageDate(list[position].timestamp!!.toLong())
        } else{
            holder.llOtherMsg.visibility = View.VISIBLE
            holder.llMyMsg.visibility = View.GONE

            holder.tvOtherMsg.text = list[position].message
            holder.tvOtherTime.text = Utility.getMessageDate(list[position].timestamp!!.toLong())
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }
}