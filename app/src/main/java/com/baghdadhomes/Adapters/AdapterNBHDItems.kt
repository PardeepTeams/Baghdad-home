package com.baghdadhomes.Adapters

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.BaseExpandableListAdapter
import android.widget.Filter
import android.widget.Filterable
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import com.baghdadhomes.Models.NBHDArea
import com.baghdadhomes.Models.NBHDDataResponse
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R

class AdapterNBHDItems(private val context: Context,
                       private var categoryList: ArrayList<NBHDDataResponse>,
                       var childItemClick:ChildItemClick): BaseExpandableListAdapter(), Filterable {

    var filterList: ArrayList<NBHDDataResponse> = categoryList
    private var valueFilter: ValueFilter? = null
    interface ChildItemClick{
        fun onChildClick(slug:ArrayList<String>)
    }

    override fun getGroupCount(): Int {
        return categoryList.size
    }

    override fun getChildrenCount(groupPosition: Int): Int {
        if(categoryList[groupPosition].area!=null)
            return categoryList[groupPosition].area?.size!!
        else
            return 0
    }

    override fun getGroup(groupPosition: Int): Any {
        return categoryList[groupPosition]
    }

    override fun getChild(groupPosition: Int, childPosition: Int): Any {
        return categoryList[groupPosition].area?.get(childPosition)!!.name
    }

    override fun getGroupId(groupPosition: Int): Long {
        return groupPosition.toLong()
    }

    override fun getChildId(groupPosition: Int, childPosition: Int): Long {
        return childPosition.toLong()
    }

    override fun hasStableIds(): Boolean {
        return false
    }

    override fun getGroupView(
        groupPosition: Int,
        isExpanded: Boolean,
        view: View?,
        parent: ViewGroup?
    ): View {
        var convertView = view
        convertView = LayoutInflater.from(context).inflate(R.layout.nbhd_main_item_layout,parent,false)
       // val holder: MainCategoryViewHolder
        if (convertView == null) {
          //  groupBinding = ItemMainCategoryBinding.inflate(inflater)
          //  convertView = groupBinding.root
         //   holder = MainCategoryViewHolder(groupBinding)
         //   convertView!!.tag = holder
        } else {
          //  holder = convertView.tag as MainCategoryViewHolder
        }

        var tvName : TextView = convertView!!.findViewById(R.id.tvName)
        var ivExpand : ImageView = convertView.findViewById(R.id.ivExpand)

        if(PreferencesService.instance.getLanguage().equals("ar")){
            tvName.text = categoryList[groupPosition].name.toSentenceCase()
        }else{
            if(categoryList[groupPosition].description!=null && categoryList[groupPosition].description.isNotEmpty()) {
                tvName.text = categoryList[groupPosition].description.toSentenceCase()
            } else {
                tvName.text = categoryList[groupPosition].name.toSentenceCase()
            }
        }

      //  holder.binding.executePendingBindings()

        if (isExpanded) {
            ivExpand.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_arror_up))
        } else {
            ivExpand.setImageDrawable(ContextCompat.getDrawable(context,R.drawable.ic_arrow_down_nbhd))
        }
        return convertView
    }

    override fun getChildView(
        groupPosition: Int,
        childPosition: Int,
        isLastChild: Boolean,
        view: View?,
        parent: ViewGroup?
    ): View? {
        var convertView = view
        convertView = LayoutInflater.from(context).inflate(R.layout.nbhd_area_item_layout,parent,false)

        var tvName : TextView = convertView.findViewById(R.id.tvName)

        if(PreferencesService.instance.getLanguage().equals("ar")){
            tvName.text = categoryList[groupPosition].area[childPosition].name.toSentenceCase()
        }else{
            if(categoryList[groupPosition].area[childPosition].description!=null && categoryList[groupPosition].area[childPosition].description.isNotEmpty()) {
                tvName.text = categoryList[groupPosition].area[childPosition].description.toSentenceCase()
            } else {
                tvName.text = categoryList[groupPosition].area[childPosition].name.toSentenceCase()
            }
        }

        tvName.setOnClickListener {
            val areaList: ArrayList<String> = ArrayList()
            val selectedSlug = categoryList[groupPosition].area[childPosition].slug
            if (selectedSlug == "all"){
                for (i in categoryList[groupPosition].area){
                    areaList.add(i.slug)
                }
            } else{
                areaList.add(categoryList[groupPosition].area[childPosition].slug)
            }
            childItemClick.onChildClick(areaList)
        }


/*        val holder: SubCategoryViewHolder
        if (convertView == null) {
            itemBinding = ItemSubCategoryBinding.inflate(inflater)
            convertView = itemBinding.root
            holder = SubCategoryViewHolder(itemBinding)
            convertView.tag = holder
        } else {
            holder = convertView.tag as SubCategoryViewHolder
        }

        holder.binding.tvName.setOnClickListener {
            childItemClick.onChildClick(categoryList[groupPosition].children?.get(childPosition)!!.id!!)
        }
        holder.binding.tvName.text =
            categoryList[groupPosition].children?.get(childPosition)!!.name ?: ""*/
        return convertView
    }

    override fun isChildSelectable(groupPosition: Int, childPosition: Int): Boolean {
        return true
    }

    override fun getFilter(): Filter {
        if (valueFilter == null) {
            valueFilter = ValueFilter()
        }
        return valueFilter as ValueFilter
    }

    fun String.toSentenceCase(): String {
        return this.lowercase().replaceFirstChar {
            if (it.isLowerCase()) it.titlecase() else it.toString()
        }
    }

    inner class ValueFilter : Filter() {
        override fun performFiltering(constraint: CharSequence): FilterResults {
            val results = FilterResults()
            if (constraint.isNotEmpty()) {
                val newList: MutableList<NBHDDataResponse> = ArrayList()
                for (i in filterList.indices) {
                    filterList[i].area.let { categories ->
                        val newCategoryList: MutableList<NBHDArea> = ArrayList()
                        for (j in categories.indices) {
                            categories[j].let { category ->
                                if (category.name.lowercase().contains(constraint.toString().lowercase())
                                    || category.description.lowercase().contains(constraint.toString().lowercase())) {
                                    newCategoryList.add(
                                        NBHDArea(categories[j].name,
                                            categories[j].slug,
                                            categories[j].description))
                                }
                            }
                        }

                        if (newCategoryList.isNotEmpty())
                            newList.add(NBHDDataResponse(
                                filterList[i].name,
                                filterList[i].description,
                                filterList[i].slug,
                                newCategoryList))
                    }
               /*     if(PreferencesService.instance.getLanguage().equals("ar")){
                        filterList[i].name?.let {
                            if (it.lowercase().contains(constraint.toString().lowercase())) {
                                newList.add(filterList[i])
                            }
                        }
                    }else{
                        filterList[i].description?.let {
                            if (it.lowercase().contains(constraint.toString().lowercase())) {
                                newList.add(filterList[i])
                            }
                        }
                    }*/

                }
                results.count = newList.size
                results.values = newList
            } else {
                results.count = filterList.size
                results.values = filterList
            }
            return results
        }

        override fun publishResults(constraint: CharSequence, results: FilterResults) {
            try {
                categoryList = results.values as ArrayList<NBHDDataResponse>
            }catch (e:Exception){
                e.toString()
            }

            notifyDataSetChanged()
        }



    }
}