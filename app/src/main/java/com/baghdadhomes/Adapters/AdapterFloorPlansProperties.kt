package com.baghdadhomes.Adapters

import android.content.Context
import android.content.Intent
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Activities.FloorPlansDetailActivity
import com.baghdadhomes.Adapters.AdapterFloorPlans.ViewHolder
import com.baghdadhomes.Models.ProjectFloorPlan
import com.baghdadhomes.Models.ResultDetail
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.bumptech.glide.Glide
import com.google.gson.Gson

class AdapterFloorPlansProperties(var context : Context, var list : ArrayList<String>,var resultDetail: ResultDetail?):
    RecyclerView.Adapter<AdapterFloorPlansProperties.ViewHolder>() {

    class  ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imgPlan : ImageView = itemView.findViewById(R.id.imgPlan)
        var tvArea : TextView = itemView.findViewById(R.id.tvArea)
        var tvBedroom : TextView = itemView.findViewById(R.id.tvBedroom)
        var tvBathroom : TextView = itemView.findViewById(R.id.tvBathroom)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_floor_plans, parent, false)

        val displayMetrics = parent.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val fiftyDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            50f,
            displayMetrics
        ).toInt()

        view.layoutParams.width = screenWidth - fiftyDp
        return ViewHolder(view)
    }

    override fun getItemCount(): Int {
        return  list.size
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        Glide.with(context).load(list[position]?:"")
            .placeholder(R.drawable.img_placeholder)
            .into(holder.imgPlan)


        holder.itemView.setOnClickListener {
            Constants.resultDetail = resultDetail
            var intent:Intent = Intent(context, FloorPlansDetailActivity::class.java)
            intent.putExtra("image",list[position])
           // intent.putExtra("model",Gson().toJson(resultDetail))
            context.startActivity(intent)
        }

        if(resultDetail!!.property_meta.fave_property_bedrooms!=null){
            holder.tvBedroom.setText(resultDetail!!.property_meta.fave_property_bedrooms.get(0) + " " + context.getString(R.string.bedroom))
        }else{
            holder.tvBedroom.setText("0")
        }

        if (resultDetail!!.property_meta.fave_property_size != null){
           holder.tvArea.setText(resultDetail!!.property_meta.fave_property_size.get(0) + " "+ context.getString(R.string.m))
        } else{

            holder.tvArea.text = "00 "+ context.getString(R.string.m)
        }

        if(resultDetail!!.property_meta.fave_property_bathrooms!=null){
            holder.tvBathroom.setText(resultDetail!!.property_meta.fave_property_bathrooms.get(0) + " " + context.getString(R.string.bathrooms_new) )
        }else{
            holder.tvBathroom.setText("0"+ " " + context.getString(R.string.bathrooms_new))
        }

    }
}