package com.baghdadhomes.Adapters

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.baghdadhomes.Models.ServicesListResponse
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class AdapterServiceCompanies(var  context: Context, var list: ArrayList<ServicesListResponse>,
                              var detailPage: openDetailPage):
    RecyclerView.Adapter<AdapterServiceCompanies.ViewHolder>() {

    class ViewHolder(itemView: View): RecyclerView.ViewHolder(itemView){
        var img_watermark: ImageView = itemView.findViewById(R.id.img_watermark)
        var img_bookmark: ImageView = itemView.findViewById(R.id.img_bookmark)
        var img_bookmarked: ImageView = itemView.findViewById(R.id.img_bookmarked)
        var img_company: ImageView = itemView.findViewById(R.id.img_company)
        var tv_title: TextView = itemView.findViewById(R.id.tv_title)
        var tv_width: TextView = itemView.findViewById(R.id.tv_width)
        var tv_time_from: TextView = itemView.findViewById(R.id.tv_time_from)
        var tv_time_to: TextView = itemView.findViewById(R.id.tv_time_to)
        var tv_details: TextView = itemView.findViewById(R.id.tv_details)
        var rl_whatsapp: RelativeLayout = itemView.findViewById(R.id.rl_whatsapp)
        var rl_call: RelativeLayout = itemView.findViewById(R.id.rl_call)
        var rl_timing: RelativeLayout = itemView.findViewById(R.id.rl_timing)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_service_companies, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        if (list.get(position).thumbnail != null){
            holder.img_watermark.visibility = View.VISIBLE
            Glide.with(context).load(list.get(position).thumbnail).placeholder(R.drawable.img_placeholder).into(holder.img_company)
        } else{
            holder.img_watermark.visibility = View.GONE
            Glide.with(context).load(R.drawable.img_placeholder).placeholder(R.drawable.img_placeholder).into(holder.img_company)
        }

      /*  if (list.get(position).data != null){
            if (list.get(position).data.post_title != null){
                holder.tv_title.setText(list.get(position).data.post_title)
            }
        }*/


        var language = PreferencesService.instance.getLanguage()
        if (language == "ar"){
            if (list.get(position).data != null){
                if (list.get(position).meta_data != null){
                    if (list.get(position).meta_data.arabicTitle != null){
                        holder.tv_title.setText(list.get(position).meta_data.arabicTitle.get(0))
                    }
                }
            }
        } else {
            if (list.get(position).data != null){
                if (list.get(position).data.post_title != null){
                    holder.tv_title.setText(list.get(position).data.post_title)
                }
            }
        }
        if (list.get(position).meta_data != null){
            if (list.get(position).meta_data.address != null){
                holder.tv_width.setText(list.get(position).meta_data.address.get(0))
            }
        }

        /*var timeFrom: String = list.get(position).meta_data.from.get(0)
        var timeto: String = list.get(position).meta_data.to.get(0)
        if (timeFrom != null && timeto != null){
            holder.tv_company_time.text = timeFrom+ context.resources.getString(R.string.am) + " "+
                    context.resources.getString(R.string.to) + " " + timeto + context.resources.getString(R.string.pm)
        }*/
        if (list.get(position).meta_data != null){
            holder.rl_timing.visibility = View.VISIBLE
            if (list.get(position).meta_data.from != null){
                try {
                    val displayFormat = SimpleDateFormat("hh:mm a", Locale.US)
                    val parseFormat = SimpleDateFormat("HH:mm", Locale.US)
                    val from = parseFormat.parse(list.get(position).meta_data.from.get(0))
                    var timeFrom = displayFormat.format(from!!)
                    if (timeFrom.contains("AM")){
                        timeFrom = timeFrom.replace("AM", context.resources.getString(R.string.am))
                    }
                    if (timeFrom.contains("PM")){
                        timeFrom = timeFrom.replace("PM", context.resources.getString(R.string.pm))
                    }
                    holder.tv_time_from.setText(timeFrom)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }

            if (list.get(position).meta_data.to != null){
                try {
                    val displayFormat = SimpleDateFormat("hh:mm a", Locale.US)
                    val parseFormat = SimpleDateFormat("HH:mm",Locale.US)
                    val to = parseFormat.parse(list.get(position).meta_data.to.get(0))
                    var timeto = displayFormat.format(to!!)
                    if (timeto.contains("AM")){
                        timeto = timeto.replace("AM", context.resources.getString(R.string.am))
                    }
                    if (timeto.contains("PM")){
                        timeto = timeto.replace("PM", context.resources.getString(R.string.pm))
                    }
                    holder.tv_time_to.setText(timeto)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
            }
        } else {
            holder.rl_timing.visibility = View.GONE
        }

        if (list.get(position).data != null) {
            if (list.get(position).data.post_content != null){
                val html: String = list.get(position).data.post_content
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                    holder.tv_details.text = Html.fromHtml(html, 0)
                } else {
                    holder.tv_details.text = Html.fromHtml(html)
                }
            }
        }

        if (list.get(position).meta_data != null){
            if (list.get(position).meta_data.whatsapp != null){
                if (list.get(position).meta_data.whatsapp.get(0).isNotEmpty()){
                    holder.rl_whatsapp.visibility = View.VISIBLE
                    holder.rl_whatsapp.setOnClickListener {
                        val intent = Intent(Intent.ACTION_VIEW)
                        val url = "https://api.whatsapp.com/send?phone="+
                                list.get(position).meta_data.whatsapp.get(0)+
                                "&text=" + URLEncoder.encode("", "UTF-8");
                        intent.data = Uri.parse(url)
                        context.startActivity(intent)
                    }
                } else{
                    holder.rl_whatsapp.visibility = View.GONE
                }
            } else{
                holder.rl_whatsapp.visibility = View.GONE
            }

            if (list.get(position).meta_data.call_number != null){
                if (list.get(position).meta_data.call_number.get(0).isNotEmpty()){
                    holder.rl_call.visibility = View.VISIBLE
                    holder.rl_call.setOnClickListener {
                        val intent = Intent(Intent.ACTION_DIAL)
                        intent.data = Uri.parse("tel:" + list.get(position).meta_data.call_number.get(0))
                        context.startActivity(intent)
                    }
                } else{
                    holder.rl_call.visibility = View.GONE
                }
            } else{
                holder.rl_call.visibility = View.GONE
            }
        } else{
            holder.rl_whatsapp.visibility = View.GONE
            holder.rl_call.visibility = View.GONE
        }


       /* holder.img_bookmark.setOnClickListener {
            holder.img_bookmark.visibility = View.GONE
            holder.img_bookmarked.visibility = View.VISIBLE
        }

        holder.img_bookmarked.setOnClickListener {
            holder.img_bookmark.visibility = View.VISIBLE
            holder.img_bookmarked.visibility = View.GONE
        }*/

        holder.itemView.setOnClickListener {
            //context.startActivity(Intent(context, ServiceCompanyViewActivity::class.java))
            detailPage.openDetails(list.get(position))
        }

    }

    override fun getItemCount(): Int {
        return list.size
    }

    fun updateList(searchList: ArrayList<ServicesListResponse>){
        list = searchList
        notifyDataSetChanged()
    }

    override fun getItemId(position: Int): Long {
        return position.toLong()
    }

    override fun getItemViewType(position: Int): Int {
        return position
    }

    interface openDetailPage{
        fun openDetails(model: ServicesListResponse)
    }
}
