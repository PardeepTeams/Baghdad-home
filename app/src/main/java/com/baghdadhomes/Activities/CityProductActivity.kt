package com.baghdadhomes.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Adapters.ProductsAdapter
import com.baghdadhomes.Models.ProjectCityWiseResponse
import com.baghdadhomes.Models.ProjectData
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.PreferencesService.Companion.instance
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject

 class CityProductActivity : BaseActivity(),ProductsAdapter.openDetailPage{
    lateinit var rv_products_items:RecyclerView
    lateinit var productsAdapter: ProductsAdapter
    lateinit var img_back:ImageView
    var cityID:String? = null
    var cityName:String? = null
    var projectList:ArrayList<ProjectData> = ArrayList()
    lateinit var tv_heading:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_product)
        if(intent.getStringExtra("city_id")!=null){
            cityID =   intent.getStringExtra("city_id")
            cityName =   intent.getStringExtra("city_name")
        }
        rv_products_items = findViewById(R.id.rv_products_items)
        img_back = findViewById(R.id.img_back)
        tv_heading = findViewById(R.id.tv_heading)

        if(cityName!=null){
            tv_heading.text = cityName
        }

        rv_products_items.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        productsAdapter = ProductsAdapter(this,projectList,this)
        rv_products_items.adapter = productsAdapter


        val map : HashMap<String,String> = HashMap()
        if (PreferencesService.instance.userLoginStatus == true){
            map["user_id"] = PreferencesService.instance.getUserData?.ID!!
        } else{
            map["device_id"] = Utility.getDeviceId(this)
        }
        map["city_id"] = cityID!!

        if (isNetworkAvailable()){
            hitGetApiWithoutTokenWithParams(Constants.GET_Project_CITY,true, Constants.GET_Project_City_API,map)
        } else{
            showToast(this, getString(R.string.intenet_error))
        }


        img_back.setOnClickListener {
            finish()
        }

    }


    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if(apiType.equals(Constants.GET_Project_CITY)){
            val model = Gson().fromJson(respopnse, ProjectCityWiseResponse::class.java)
            if(model.success!!){
                if(model.data!=null){
                    projectList.clear()
                    projectList.addAll(model.data)
                    productsAdapter.notifyDataSetChanged()
                }
            }
        }


    }

     override fun addRemoveFav(model: ProjectData?, position: Int) {
         if(isNetworkAvailable()){
             val propId = model!!.id
             val userData = PreferencesService.instance.getUserData

             val map: HashMap<String, String> = HashMap()
             map.put("user_id", userData!!.ID!!)
             map.put("listing_id", propId.toString())
             //map.put("", favStatus)
             projectList.get(position).isFav = projectList.get(position).isFav != true
             productsAdapter.notifyDataSetChanged()
             hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)

         }
     }

     override fun openLoginActivity() {
         this.loginTypeDialog(false)
     }

     override fun openNextActivity(model: ProjectData?, position: Int) {
         val intent = Intent(this, ProjectDetailActivity::class.java)
         intent.putExtra("propertyId",model!!.id.toString())
         startActivity(intent)
      //   overridePendingTransition(0, 0)
     }

 }