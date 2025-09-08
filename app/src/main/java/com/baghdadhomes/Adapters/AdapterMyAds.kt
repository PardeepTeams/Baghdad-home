package com.baghdadhomes.Adapters

import android.app.AlertDialog
import android.content.Context
import android.os.Build
import android.text.Html
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.Priority
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.baghdadhomes.Models.ResultFeatured
import com.baghdadhomes.R

class AdapterMyAds(var context: Context, var actionsInterface: perfomActions
, var list: ArrayList<ResultFeatured>, var isMyAds : Boolean):
RecyclerView.Adapter<AdapterMyAds.ViewHolder>(){

    class ViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView) {
        var img_bookmark: ImageView = itemView.findViewById(R.id.img_bookmark)
        var img_premimum: ImageView = itemView.findViewById(R.id.img_premimum)
      //  var img_bookmarked: ImageView = itemView.findViewById(R.id.img_bookmarked)
        var imv_property: ImageView = itemView.findViewById(R.id.imv_property)
        var tv_details: TextView = itemView.findViewById(R.id.tv_details)
        var tv_title: TextView = itemView.findViewById(R.id.tv_title)
        var tv_area_property: TextView = itemView.findViewById(R.id.tv_area_property)
        var tv_width_count: TextView = itemView.findViewById(R.id.tv_width)
        var tv_bedroom: TextView = itemView.findViewById(R.id.tv_bedroom)
        var tv_batroom: TextView = itemView.findViewById(R.id.tv_batroom)
        var tv_price: TextView = itemView.findViewById(R.id.tv_price)
        var tv_number_area: TextView = itemView.findViewById(R.id.tv_number_area)
        var tv_sell: TextView = itemView.findViewById(R.id.tv_sell)
     //   var rl_nbhd: RelativeLayout = itemView.findViewById(R.id.rl_nbhd)
       // var rl_bathroom: RelativeLayout = itemView.findViewById(R.id.rl_bathroom)
      //  var rl_bedroom: RelativeLayout = itemView.findViewById(R.id.rl_bedroom)
        var rl_edit: LinearLayout = itemView.findViewById(R.id.rl_edit)
        var rl_delete: LinearLayout = itemView.findViewById(R.id.rl_delete)
        var ll_edit: LinearLayout = itemView.findViewById(R.id.ll_edit)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.layout_detail_my_ads, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val land = "شقة"

        if (isMyAds) {
            holder.ll_edit.visibility = View.VISIBLE
        } else {
            holder.ll_edit.visibility = View.GONE
        }

        if(list.get(position).is_premium!!){
            holder.img_premimum.visibility = View.VISIBLE
        }else{
            holder.img_premimum.visibility = View.GONE
        }

        holder.rl_edit.setOnClickListener { actionsInterface.editAd(list.get(position)) }

        holder.rl_delete.setOnClickListener {
            val alertDialog = AlertDialog.Builder(context)
            alertDialog.setTitle(context.resources.getString(R.string.delete))
            alertDialog.setMessage(context.resources.getString(R.string.delete_message))
            //alertDialog.setMessage("Are you sure, you want to delete this ad?");
            alertDialog.setPositiveButton(
                context.resources.getString(R.string.yes)
            ) { dialog, which -> actionsInterface.deleteAd(list.get(position), position) }
            alertDialog.setNegativeButton(
                context.resources.getString(R.string.no)
            ) { dialog, which -> }
            alertDialog.show()
        }

        if (list.get(position).property_type.contains(land)) {
            holder.tv_batroom.visibility = View.GONE
            holder.tv_bedroom.visibility = View.GONE
        } else {
            holder.tv_batroom.visibility = View.VISIBLE
            holder.tv_bedroom.visibility = View.VISIBLE
        }

        if (list.get(position).property_meta.favePropertyLand != null) {
            holder.tv_width_count.setText(
                context.getString(R.string.street_width) + " " +
                        list.get(position).property_meta.favePropertyLand.get(0)
                        + context.resources.getString(R.string.m)
            )
        } else {
            holder.tv_width_count.setText(context.getString(R.string.street_width) + " " + "0" + context.resources.getString(R.string.m))
        }


        if (list.get(position).property_attr != null) {
            if (list.get(position).property_attr!!.property_status != null) {
                holder.tv_sell.visibility = View.VISIBLE
                holder.tv_sell.setText(list.get(position).property_attr!!.property_status)
            } else {
                holder.tv_sell.visibility = View.GONE
            }
        } else {
            holder.tv_sell.visibility = View.GONE
        }

        if (list.get(position).thumbnail != null) {
            val image: String = list.get(position).thumbnail.toString()
            val width: Int = holder.imv_property.getWidth()
            val height: Int = holder.imv_property.getHeight()
            if (image != null && image != "false") {
                Glide.with(context).load(image).placeholder(R.drawable.img_placeholder).
                apply(
                    RequestOptions()
                        .override(width, height) // Resize image (width x height)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH)// Cache the image for future use
                ).  thumbnail(Glide.with(context).load(image).override(width, height))
                    .into(holder.imv_property)
            } else {
                Glide.with(context).load(R.drawable.img_placeholder). apply(
                    RequestOptions()
                        .override(width, height) // Resize image (width x height)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .priority(Priority.HIGH)// Cache the image for future use
                )
                    .placeholder(R.drawable.img_placeholder).into(holder.imv_property)
            }
        }

        if (list.get(position).post_title != null) {
            holder.tv_title.setText(list.get(position).post_title.trim())
        }


        if (list.get(position).property_meta.fave_property_bedrooms != null) {
            holder.tv_bedroom.setText(list.get(position).property_meta.fave_property_bedrooms.get(0))
        } else {
            holder.tv_bedroom.text = "0"
        }

        if (list.get(position).property_meta.fave_property_bathrooms != null) {
            holder.tv_batroom.setText(list.get(position).property_meta.fave_property_bathrooms.get(0))
        } else {
            holder.tv_batroom.text = "0"
        }

        if (list.get(position).property_meta.fave_property_size != null) {
            holder.tv_number_area.setText(
                list.get(position).property_meta.fave_property_size.get(0) + " " + context.resources.getString(
                    R.string.m
                )
            )
        } else {
            holder.tv_number_area.text = "00" + context.resources.getString(R.string.m)
        }

        if (list.get(position).price != null) {
            holder.tv_price.text =
                "(" + list.get(position).price + ")"
        } else {
            holder.tv_price.text = "(0) "
        }
        var currency = if (list[position].property_meta!=null && !list[position].property_meta.fave_currency.isNullOrEmpty())  {
            if (list[position].property_meta.fave_currency?.firstOrNull() =="USD") {
                context.getString(R.string.currency_code_usd)
            } else {
                context.getString(R.string.currency_code)
            }
        } else {
            context.getString(R.string.currency_code)
        }
        holder.tv_price.text = "${holder.tv_price.text}$currency"


        if (list.get(position).post_content != null) {
            val html: String = list.get(position).post_content
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                holder.tv_details.text = Html.fromHtml(html, 0)
            } else {
                holder.tv_details.text = Html.fromHtml(html)
            }
        }

        if (list.get(position).property_address != null) {
            if (list.get(position).property_address.property_area != null) {
                holder.tv_area_property.visibility = View.VISIBLE
                holder.tv_area_property.setText(list.get(position).property_address.property_area)
            } else {
                holder.tv_area_property.visibility = View.GONE

            }
        } else {
            holder.tv_area_property.visibility = View.GONE
        }

        holder.itemView.setOnClickListener {
            actionsInterface.openNextActivity(list.get(position))
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

    fun updateList(searchList: ArrayList<ResultFeatured>) {
        list = searchList
        notifyDataSetChanged()
    }

    interface perfomActions {
        fun openNextActivity(model: ResultFeatured?)
        fun editAd(model: ResultFeatured?)
        fun addRemoveFav(model: ResultFeatured?, position: Int)
        fun deleteAd(model: ResultFeatured?, position: Int)
        fun openLoginActivity()
    }
}