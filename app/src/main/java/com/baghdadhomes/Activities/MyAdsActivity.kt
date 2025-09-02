package com.baghdadhomes.Activities


import android.content.Intent
import android.os.Bundle
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterMyAds
import com.baghdadhomes.Models.FeaturedPropertiesModel
import com.baghdadhomes.Models.ResultFeatured
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale


class MyAdsActivity : BaseActivity(), AdapterMyAds.perfomActions {
    private lateinit var img_back: ImageView
    private lateinit var rv_my_ads: RecyclerView
    //private lateinit var adapterMyAds: AdapterDetailAds
    //var myAdsList:ArrayList<Result> = ArrayList()
    private lateinit var adapterMyAds: AdapterMyAds
    var myAdsList:ArrayList<ResultFeatured> = ArrayList()
    var deletePosition : Int? = null

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()){
            val userData = PreferencesService.instance.getUserData
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID.toString())
            map.put("page", "1")
            map.put("per_page", "1000")
            map.put("status", "")
            //Log.d("user1212", userData!!.ID.toString())
            hitGetApiWithoutTokenWithParams(Constants.MY_ADD, true, Constants.MY_ADD_URL, map)
        } else{
            showToast(this,resources.getString(R.string.intenet_error))
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_my_ads)
        adjustFontScale(resources.configuration)
        img_back = findViewById(R.id.img_back)
        rv_my_ads = findViewById(R.id.rv_my_ads)

        img_back.setOnClickListener { onBackPressed() }

       // showDatePickerDialog()

        adapterMyAds = AdapterMyAds(this, this, myAdsList, true)
        rv_my_ads.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false))
    //   rv_my_ads.setAdapter(adapterDetailAds)
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.MY_ADD)){
            val model = Gson().fromJson(respopnse, FeaturedPropertiesModel::class.java)
            myAdsList.clear()
            if (model.success){
                for (i in model.result){
                    myAdsList.add(i)
                }
                rv_my_ads.adapter = adapterMyAds
                adapterMyAds.notifyDataSetChanged()
            }
        } else if (apiType.equals(Constants.DELETE_ADD)){
            val model = Gson().fromJson(respopnse, FeaturedPropertiesModel::class.java)
            if (model.success){
                myAdsList.removeAt(deletePosition!!)
                adapterMyAds.notifyDataSetChanged()
                adapterMyAds.notifyItemRangeChanged(0,myAdsList.size)
                deletePosition = null
            }
        }
    }



    /*override fun openNextActivity(model: Result?) {
        var intent:Intent = Intent(this, AdsDetailsActivity::class.java)
        intent.putExtra("myAd",true)
        intent.putExtra("model",Gson().toJson(model))
        startActivity(intent)
        // startActivity(Intent(activity, AdsDetailsActivity::class.java))
        overridePendingTransition(0, 0)
    }

    override fun editAd(model: Result?) {
        var intent = Intent(this,PostAdActivity::class.java)
        //intent.putExtra("isEdit",true)
        intent.putExtra("model",Gson().toJson(model))
        intent.putExtra("isUpdate",true)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun deleteAd(model: Result?, position: Int) {
        if (isNetworkAvailable()){
            deletePosition = position
            var userData = PreferencesService.instance.getUserData
            var propID = model!!.iD
            var map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID.toString())
            map.put("prop_id", propID.toString())
            hitGetApiWithoutTokenWithParams(Constants.DELETE_ADD, true, Constants.DELETE_ADD_URL, map)
        } else{
            Utility.showToast(this, resources.getString(R.string.intenet_error))
        }

    }

    override fun addRemoveFav(model: Result?, position: Int) {
        if(isNetworkAvailable()){
            Log.d("gaurav", "gauravgaurav")
            var propId = model!!.iD
            var userData = PreferencesService.instance.getUserData
            var map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId)
            if(myAdsList.get(position).is_fav == true){
                myAdsList.get(position).is_fav =false
            }else{
                myAdsList.get(position).is_fav =true
            }
            adapterMyAds.notifyDataSetChanged()
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)

        }

    }

    override fun openLoginActivity() {
        startActivity(Intent(this, LoginActivity::class.java))
        overridePendingTransition(0,0)
    }*/

    override fun openNextActivity(model: ResultFeatured?) {
        val intent:Intent = Intent(this, AdsDetailsActivity::class.java)
        intent.putExtra("myAd",true)
        intent.putExtra("propertyId",model!!.iD)
     //   intent.putExtra("model",Gson().toJson(model))
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun editAd(model: ResultFeatured?) {
        val intent = Intent(this,PostAdActivity::class.java)
        intent.putExtra("model",Gson().toJson(model))
        intent.putExtra("isUpdate",true)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun addRemoveFav(model: ResultFeatured?, position: Int) {
        if(isNetworkAvailable()){
            val propId = model!!.iD
            val userData = PreferencesService.instance.getUserData
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId)
            if(myAdsList.get(position).is_fav == true){
                myAdsList.get(position).is_fav =false
            }else{
                myAdsList.get(position).is_fav =true
            }
            adapterMyAds.notifyDataSetChanged()
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)

        }
    }

    override fun deleteAd(model: ResultFeatured?, position: Int) {
        if (isNetworkAvailable()){
            deletePosition = position
            val userData = PreferencesService.instance.getUserData
            val propID = model!!.iD
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID.toString())
            map.put("prop_id", propID.toString())
            hitGetApiWithoutTokenWithParams(Constants.DELETE_ADD, true, Constants.DELETE_ADD_URL, map)
        } else{
            Utility.showToast(this, resources.getString(R.string.intenet_error))
        }
    }

    override fun openLoginActivity() {
        loginTypeDialog(false)
        //startActivity(Intent(this, LoginActivity::class.java))
        //overridePendingTransition(0,0)
    }
    private fun showDatePickerDialog() {
        // Create the MaterialDatePicker for date range
        val builder = MaterialDatePicker.Builder.dateRangePicker()
        builder.setTitleText("Select a date range")
        builder.setTheme(R.style.MaterialDatePickerStyle)

        val datePicker = builder.build()

        datePicker.addOnPositiveButtonClickListener { selection ->
            // Retrieve the start and end dates (milliseconds)
            val startDate = selection.first
            val endDate = selection.second

            // Format the start and end dates as strings
            val sdf = SimpleDateFormat("dd/MM/yyyy", Locale.getDefault())
            val startDateString = sdf.format(Date(startDate))
            val endDateString = sdf.format(Date(endDate))

            // Create the date range string
            val selectedDateRange = "$startDateString - $endDateString"

            // Display the selected range
          //  selectedDate.text = selectedDateRange
        }

        // Show the date picker dialog
        datePicker.show(supportFragmentManager, "DATE_PICKER")
    }
}