package com.baghdadhomes.Adapters

import android.content.Context
import android.util.TypedValue
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.R
import com.bumptech.glide.Glide

class AdapterFloorPlans(var context : Context,var list : ArrayList<String>) : RecyclerView.Adapter<AdapterFloorPlans.ViewHolder>(){

    class  ViewHolder(itemView : View) : RecyclerView.ViewHolder(itemView) {
        var imgPlan : ImageView = itemView.findViewById(R.id.imgPlan)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(parent.context).inflate(R.layout.layout_floor_plans, parent, false)

        val displayMetrics = parent.context.resources.displayMetrics
        val screenWidth = displayMetrics.widthPixels
        val fiftyDp = TypedValue.applyDimension(
            TypedValue.COMPLEX_UNIT_DIP,
            100f,
            displayMetrics
        ).toInt()

        view.layoutParams.width = screenWidth - fiftyDp
        return ViewHolder(view)
    }


    override fun onBindViewHolder(
        holder: ViewHolder,
        position: Int
    ) {
        Glide.with(context).load(list[position]).placeholder(R.drawable.img_placeholder).into(holder.imgPlan)
    }

    override fun getItemCount(): Int {
        return list.size
    }
}