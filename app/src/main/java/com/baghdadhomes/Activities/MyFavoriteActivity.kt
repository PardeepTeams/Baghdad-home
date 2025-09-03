package com.baghdadhomes.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Adapters.ProductsAdapter
import com.baghdadhomes.Models.NewFeatureModel
import com.baghdadhomes.Models.ProjectCityWiseResponse
import com.baghdadhomes.Models.ProjectData
import com.baghdadhomes.Models.Result
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants

class MyFavoriteActivity : BaseActivity(), AdapterDetailAds.openDetailPage,ProductsAdapter.openDetailPage {
    lateinit var img_back: ImageView
    lateinit var tvProperties: TextView
    lateinit var tvProject: TextView
    lateinit var rv_my_fav: RecyclerView
    lateinit var rv_project_fav: RecyclerView
    lateinit var adapterDetailAds: AdapterDetailAds
    var favList: ArrayList<Result> = ArrayList()
    var favProjectList: ArrayList<ProjectData> = ArrayList()
    lateinit var productsAdapter: ProductsAdapter
    var isProject:Boolean = false
    var hitApi = false

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()){
            val userID:String = PreferencesService.instance.getUserData!!.ID.toString()

            if(isProject){
                val map:HashMap<String,String> = HashMap()
                map.put("user_id",userID)
                hitPostApi(Constants.GET_FAV_Project,true,Constants.GET_FAV_Project_API , map)
            }else{
                hitGetApiWithoutToken(Constants.GET_FAVORITE,true,Constants.GET_FAVORITE_API + userID)

            }
            //var userID:String = "57"
        } else{
            showToast(this,resources.getString(R.string.intenet_error))
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        setContentView(R.layout.activity_my_favorite)

        img_back = findViewById(R.id.img_back)
        tvProperties = findViewById(R.id.tvProperties)
        rv_project_fav = findViewById(R.id.rv_project_fav)
        tvProject = findViewById(R.id.tvProject)
        rv_my_fav = findViewById(R.id.rv_my_fav)

        img_back.setOnClickListener { onBackPressed() }
        clickListeners()
     //   tvProperties.performClick()

        rv_my_fav.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_project_fav.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapterDetailAds = AdapterDetailAds(this, this, favList, /*false, false*/)
        productsAdapter = ProductsAdapter(this,favProjectList,this)
    }

    fun clickListeners() {
        tvProperties.setOnClickListener {
            isProject = false
            tvProperties.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
            tvProperties.setTextColor(ContextCompat.getColor(this,R.color.whiteNew))

            tvProject.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            tvProject.setTextColor(ContextCompat.getColor(this,R.color.grey))
            onResume()
        }

        tvProject.setOnClickListener {
            isProject = true
            tvProperties.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            tvProperties.setTextColor(ContextCompat.getColor(this,R.color.grey))

            tvProject.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
            tvProject.setTextColor(ContextCompat.getColor(this,R.color.whiteNew))
            onResume()
        }

    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType == Constants.GET_FAVORITE){
            val model = Gson().fromJson(respopnse, NewFeatureModel::class.java)
            favList.clear()
            if (model.success){
                favList.addAll(model.result)
                rv_project_fav.visibility = View.GONE
                rv_my_fav.visibility = View.VISIBLE
                rv_my_fav.adapter = adapterDetailAds
                adapterDetailAds.notifyDataSetChanged()
            }
        }
        if(apiType.equals(Constants.GET_FAV_Project)){

            val model = Gson().fromJson(respopnse, ProjectCityWiseResponse::class.java)
            if(model.success!!){
                if(model.data!=null){
                    favProjectList.clear()
                    favProjectList.addAll(model.data)

                }
                rv_my_fav.visibility = View.GONE
                rv_project_fav.visibility = View.VISIBLE
                rv_project_fav.adapter = productsAdapter
                productsAdapter.notifyDataSetChanged()
            }
        }
    }

    override fun openNextActivity(model: Result?, position: Int) {
        val intent:Intent = Intent(this, AdsDetailsActivity::class.java)
        intent.putExtra("myAd",false)
        intent.putExtra("propertyId",model!!.iD)
        intent.putExtra("view_count",model!!.totalViews)
       // intent.putExtra("model",Gson().toJson(model))
        startActivity(intent)
        // startActivity(Intent(activity, AdsDetailsActivity::class.java))
        overridePendingTransition(0, 0)
    }

    override fun editAd(model: Result?) {

    }

    override fun deleteAd(model: Result?, position: Int) {

    }

    override fun addRemoveFav(model: Result?, position: Int) {
        if(isNetworkAvailable()){
            val propId = model!!.iD
            val userData = PreferencesService.instance.getUserData

            /*var favStatus = model.is_fav
            if(favStatus == true){
                favStatus = false
            }else{
                favStatus = true
            }*/
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId)
            //map.put("", favStatus)
            if(favList.get(position).is_fav == true){
                favList.get(position).is_fav =false
            }else{
                favList.get(position).is_fav =true
            }
            favList.removeAt(position)
            adapterDetailAds.notifyItemRemoved(position)
            adapterDetailAds.notifyItemRangeChanged(0,favList.size)
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)
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
            favProjectList.get(position).isFav = favProjectList.get(position).isFav != true
            productsAdapter.notifyDataSetChanged()
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)

        }
    }

    override fun openLoginActivity() {
        loginTypeDialog(false)
        //startActivity(Intent(this, LoginActivity::class.java))
        //overridePendingTransition(0,0)
    }

    override fun openNextActivity(model: ProjectData?, position: Int) {
        val intent = Intent(this, ProjectDetailActivity::class.java)
        intent.putExtra("propertyId",model!!.id.toString())
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

}