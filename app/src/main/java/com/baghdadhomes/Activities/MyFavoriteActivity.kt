package com.baghdadhomes.Activities

import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Models.NewFeatureModel
import com.baghdadhomes.Models.Result
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants

class MyFavoriteActivity : BaseActivity(), AdapterDetailAds.openDetailPage {
    lateinit var img_back: ImageView
    lateinit var tvProperties: TextView
    lateinit var tvProject: TextView
    lateinit var rv_my_fav: RecyclerView
    lateinit var adapterDetailAds: AdapterDetailAds
    var favList: ArrayList<Result> = ArrayList()

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()){
            val userID:String = PreferencesService.instance.getUserData!!.ID.toString()
            //var userID:String = "57"
            hitGetApiWithoutToken(Constants.GET_FAVORITE,true,Constants.GET_FAVORITE_API + userID)
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
        tvProject = findViewById(R.id.tvProject)
        rv_my_fav = findViewById(R.id.rv_my_fav)

        img_back.setOnClickListener { onBackPressed() }

        rv_my_fav.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapterDetailAds = AdapterDetailAds(this, this, favList, /*false, false*/)
        //rv_my_fav.adapter = favAdapter
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.GET_FAVORITE)){
            val model = Gson().fromJson(respopnse, NewFeatureModel::class.java)
            favList.clear()
            if (model.success){
                for (i in model.result){
                    favList.add(i)
                }
                rv_my_fav.adapter = adapterDetailAds
                adapterDetailAds.notifyDataSetChanged()
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

    override fun openLoginActivity() {
        loginTypeDialog(false)
        //startActivity(Intent(this, LoginActivity::class.java))
        //overridePendingTransition(0,0)
    }

}