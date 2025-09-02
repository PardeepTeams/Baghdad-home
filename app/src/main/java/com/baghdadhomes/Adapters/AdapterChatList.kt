package com.baghdadhomes.Adapters

import android.content.Context
import android.graphics.Typeface
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.baghdadhomes.Models.AgenciesData
import com.baghdadhomes.Models.ChatHistoryData
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Utility
import de.hdodenhof.circleimageview.CircleImageView

class AdapterChatList(var context : Context, var userID : String, var list : ArrayList<ChatHistoryData>, var onClick : OpenMessages)
    : RecyclerView.Adapter<AdapterChatList.ViewHolder>() {

    class ViewHolder(itemView : View): RecyclerView.ViewHolder(itemView){
        var imgPerson : CircleImageView = itemView.findViewById(R.id.imgPerson)
        var tvName : TextView = itemView.findViewById(R.id.tvName)
        var tvMessage : TextView = itemView.findViewById(R.id.tvMessage)
        var tvTime : TextView = itemView.findViewById(R.id.tvTime)
        var imgUnreadDot : ImageView = itemView.findViewById(R.id.imgUnreadDot)
        var view_line : View = itemView.findViewById(R.id.view_line)
        var rlPostDetail : RelativeLayout = itemView.findViewById(R.id.rlPostDetail)
        var imgPost : ShapeableImageView = itemView.findViewById(R.id.imgPost)
        var tvPostTitle : TextView = itemView.findViewById(R.id.tvPostTitle)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(LayoutInflater.from(context).inflate(R.layout.layout_chats,parent,false))
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.tvMessage.text = list.get(position).message.orEmpty()
        if (!list.get(position).time.isNullOrEmpty()) {
            holder.tvTime.text = Utility.getMessageDate(list.get(position).time.orEmpty().toLong())
        }

        if (!list.get(position).sentBy.isNullOrEmpty() && list.get(position).sentBy != userID && !list.get(position).unreadCount.isNullOrEmpty()
            && list.get(position).unreadCount != "0"){
            holder.imgUnreadDot.visibility = View.VISIBLE

            holder.tvName.setTypeface(null,Typeface.BOLD)
            holder.tvMessage.setTypeface(null,Typeface.BOLD)
            holder.tvTime.setTypeface(null,Typeface.BOLD)
        } else{
            holder.imgUnreadDot.visibility = View.GONE

            holder.tvName.setTypeface(null,Typeface.NORMAL)
            holder.tvMessage.setTypeface(null,Typeface.NORMAL)
            holder.tvTime.setTypeface(null,Typeface.NORMAL)
        }

        if (!list.get(position).sentTo.isNullOrEmpty() && list.get(position).sentTo != userID){
            holder.tvName.text = list.get(position).sent_to?.display_name.orEmpty()
            Glide.with(context).load(list.get(position).sent_to?.user_image.orEmpty()).placeholder(R.drawable.img_placeholder).into(holder.imgPerson)
        } else{
            holder.tvName.text = list.get(position).sent_by?.display_name.orEmpty()
            Glide.with(context).load(list.get(position).sent_by?.user_image.orEmpty()).placeholder(R.drawable.img_placeholder).into(holder.imgPerson)
        }

        if (!list[position].postId.isNullOrEmpty()){
            holder.view_line.visibility = View.VISIBLE
            holder.rlPostDetail.visibility = View.VISIBLE

            Glide.with(context).load(list[position].thumbnail.orEmpty()).placeholder(R.drawable.img_placeholder).into(holder.imgPost)
            holder.tvPostTitle.text = list.get(position).title.orEmpty()
        } else {
            holder.view_line.visibility = View.GONE
            holder.rlPostDetail.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            //context.startActivity(Intent(context,MessagingActivity::class.java)) )
            if (list.get(position).sent_to != null && list.get(position).sent_by != null){
                if (list.get(position).sentTo != userID){
                    onClick.onClickChat(list.get(position).sent_to, list.get(position))
                } else{
                    onClick.onClickChat(list.get(position).sent_by, list.get(position))
                }
            } else{
                Toast.makeText(context, context.getString(R.string.account_deleted_message), Toast.LENGTH_SHORT).show()
            }
        }
    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(filteredList : ArrayList<ChatHistoryData>){
        this.list = filteredList
        notifyDataSetChanged()
    }
    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface OpenMessages{
        fun onClickChat(model: AgenciesData?, postData: ChatHistoryData)
    }
}