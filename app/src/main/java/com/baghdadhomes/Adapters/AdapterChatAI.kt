package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ModelAiSearchResponse
import com.baghdadhomes.Models.PropertySubTypesModel
import com.baghdadhomes.Models.Result
import com.baghdadhomes.R

class AdapterChatAI(val context:Context, var chatList:ArrayList<ModelAiSearchResponse>, private val actions : AdapterAiChatAction): RecyclerView.Adapter<AdapterChatAI.ViewHolder>() {


    class ViewHolder(itemView:View):RecyclerView.ViewHolder(itemView){
        val rv_properties:RecyclerView = itemView.findViewById(R.id.rv_properties)
        var llMyMsg : LinearLayout = itemView.findViewById(R.id.llMyMsg)
        var tvMyMsg : TextView = itemView.findViewById(R.id.tvMyMsg)
        var tvOtherMsg : TextView = itemView.findViewById(R.id.tvOtherMsg)
        var tvSeeAll : TextView = itemView.findViewById(R.id.tvSeeAll)
        var tvCount : TextView = itemView.findViewById(R.id.tvCount)
        var llProperties : LinearLayout = itemView.findViewById(R.id.llProperties)
        var tvForRent : TextView = itemView.findViewById(R.id.tv_for_rent)
        var tvForSale : TextView = itemView.findViewById(R.id.tv_for_sale)
        var rvTypes : RecyclerView = itemView.findViewById(R.id.rvTypes)

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
        holder.tvMyMsg.text = chatList[position].searchText ?: ""
        holder.tvOtherMsg.text = chatList[position].message ?: ""
        holder.rv_properties.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        holder.rvTypes.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)

        if(!chatList[position].result.isNullOrEmpty()){
            holder.llProperties.visibility = View.VISIBLE
            var listSize = chatList[position].result?.size ?:0
            var totalCount = chatList[position].count ?:0

            if (totalCount>listSize) {
                holder.tvSeeAll.visibility = View.VISIBLE
            } else {
                holder.tvSeeAll.visibility = View.GONE
            }

            holder.tvCount.text = "$totalCount ${context.getString(R.string.property_found)}"

               holder.rv_properties.adapter =AdapterDetailAds(context, object : AdapterDetailAds.openDetailPage{
                   override fun openNextActivity(
                       model: Result?,
                       childPosition: Int
                   ) {
                       actions.openAdDetailActivity(position,childPosition,
                           chatList[position].result?.get(childPosition)
                       )
                   }

                   override fun editAd(model: Result?) {

                   }

                   override fun addRemoveFav(
                       model: Result?,
                       childPosition: Int
                   ) {
                       actions.addRemoveFav(position,childPosition,
                           chatList[position].result?.get(childPosition)
                       )
                   }

                   override fun deleteAd(model: Result?, childPosition: Int) {

                   }

                   override fun openLoginActivity() {
                       actions.openLoginActivity()
                   }
               },chatList[position].result
               )

            var subTypesList = getPropertyTypeList()
            holder.rvTypes.adapter = AdapterPropertySubTypes(context,subTypesList,object : AdapterPropertySubTypes.SubTypeClick {
                override fun onSubTypeClick(typePosition: Int) {
                    for (i in subTypesList) {
                        i.isSelected = false
                    }
                    subTypesList[typePosition].isSelected = true
                    holder.rvTypes.adapter?.notifyDataSetChanged()
                }
            })
        }else{
            holder.llProperties.visibility = View.GONE
        }

        holder.tvSeeAll.setOnClickListener {
            actions.onSeeAllClick(position)
        }

        holder.tvForRent.setOnClickListener {
            holder.tvForRent.background = ContextCompat.getDrawable(context,R.drawable.bg_outline_blue_new)
            holder.tvForRent.setTextColor(ContextCompat.getColor(context,R.color.whiteNew))

            holder.tvForSale.background = ContextCompat.getDrawable(context,R.drawable.bg_outline_solid)
            holder.tvForSale.setTextColor(ContextCompat.getColor(context,R.color.grey))
        }

        holder.tvForSale.setOnClickListener {
            holder.tvForSale.background = ContextCompat.getDrawable(context,R.drawable.bg_outline_blue_new)
            holder.tvForSale.setTextColor(ContextCompat.getColor(context,R.color.whiteNew))

            holder.tvForRent.background = ContextCompat.getDrawable(context,R.drawable.bg_outline_solid)
            holder.tvForRent.setTextColor(ContextCompat.getColor(context,R.color.grey))
        }
    }

    interface AdapterAiChatAction {
        fun openLoginActivity()
        fun addRemoveFav(parentPosition:Int, childPosition : Int, model:Result?)
        fun openAdDetailActivity(parentPosition:Int, childPosition : Int, model:Result?)
        fun onSeeAllClick(position: Int)
    }

    private fun getPropertyTypeList() : ArrayList<PropertySubTypesModel> {
        var subTypesList = ArrayList<PropertySubTypesModel>()
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_all)!!,context.getString(R.string.all),"",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_home_ad_activity)!!,context.getString(R.string.house),"house",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_apart_house)!!,context.getString(R.string.apart_house),"apart_house",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_apartment)!!,context.getString(R.string.apartment),"apartment",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_villa)!!,context.getString(R.string.villa),"villa",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_other)!!,context.getString(R.string.other),"residence_other",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_office)!!,context.getString(R.string.office),"office",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_shop)!!,context.getString(R.string.shop),"shop",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_store)!!,context.getString(R.string.store),"store",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_building)!!,context.getString(R.string.building),"building",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_factory)!!,context.getString(R.string.factory),"factory",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_showroom)!!,context.getString(R.string.showroom),"showroom",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_other)!!,context.getString(R.string.other),"commercial_other",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_agriculture)!!,context.getString(R.string.agriculture),"agriculture",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_commercial_new)!!,context.getString(R.string.commericail),"commercial",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_residential_land)!!,context.getString(R.string.residence),"residencial",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_industry)!!,context.getString(R.string.industrial),"industrial",false))
        subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(context,R.drawable.ic_all)!!,context.getString(R.string.all),"",false))
        return subTypesList
    }

}