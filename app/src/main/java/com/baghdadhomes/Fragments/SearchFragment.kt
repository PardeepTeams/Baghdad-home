package com.baghdadhomes.Fragments

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Models.ResultFeatured
import com.baghdadhomes.R

class SearchFragment : BaseFragment() {
    lateinit var rv_properties: RecyclerView
    lateinit var img_back: ImageView
    lateinit var adapterDetailAds: AdapterDetailAds
    var totalCount:Int= 0
    var propertiesList:ArrayList<ResultFeatured> = ArrayList()
    lateinit var tv_property_count: TextView


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_search, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        tv_property_count = view.findViewById(R.id.tv_property_count)
        img_back = view.findViewById(R.id.img_back)
        rv_properties = view.findViewById(R.id.rv_properties)


        /*if(intent.getStringExtra("search_text")!=null){
            var keyword = intent.getStringExtra("search_text")
            var pagemap:HashMap<String,String> = HashMap()
            pagemap.put("keyword",keyword!!)
            hitPostApi(Constants.GET_PROPERTIES_SEARCH,true, Constants.GETFEATUREDPROPERTIES, pagemap)
        }*/




        rv_properties.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        // rv_properties.setHasFixedSize(true)
        rv_properties.isNestedScrollingEnabled = true

    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
       /* if(apiType.equals(Constants.GET_PROPERTIES_SEARCH)){
            var model: FeaturedPropertiesModel = Gson().fromJson(respopnse,
                FeaturedPropertiesModel::class.java)
            if(model.success){
                if(model.count!=null){
                    totalCount = model.count
                }
                tv_property_count.setText(totalCount.toString()+ " " + getString(R.string.property_found))
                propertiesList.clear()
                propertiesList.addAll(model.result)
                adapterDetailAds = AdapterDetailAds(this, this,propertiesList)
                rv_properties.adapter = adapterDetailAds


            }
        }*/
    }
}