package com.baghdadhomes.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Models.FilterIntentModel
import com.baghdadhomes.Models.NewFeatureModel
import com.baghdadhomes.Models.Result
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import java.util.*


class PropertiesSearchActivity : BaseActivity(), AdapterDetailAds.openDetailPage{
    lateinit var ll_linear: LinearLayout
    lateinit var rv_properties: RecyclerView
    lateinit var img_back: ImageView
    lateinit var img_clear_search: ImageView
    lateinit var adapterDetailAds: AdapterDetailAds
    var filterList:ArrayList<Result> = ArrayList()
    lateinit var tv_property_count:TextView
    lateinit var et_serach:EditText
    lateinit var filterModel:FilterIntentModel
    var searchList: ArrayList<Result> = ArrayList()
    var loading: Boolean = true
    var totalCount:Int = 0
    var visibleItemCount: Int = 0;
    var totalItemCount: Int = 0;
    var pastVisiblesItems: Int = 0
    var page:Int = 0
    var keybaord:String = ""

    override fun onResume() {
        super.onResume()
        page = 0
        loading = false
        getFilterApi()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_properties_search)
        adjustFontScale(resources.configuration)

        ll_linear = findViewById(R.id.ll_linear)
        tv_property_count = findViewById(R.id.tv_property_count)
        img_back = findViewById(R.id.img_back)
        rv_properties = findViewById(R.id.rv_properties)
        et_serach = findViewById(R.id.et_serach)
        img_clear_search = findViewById(R.id.img_clear_search)

        img_back.setOnClickListener {
            if(intent.getStringExtra("slug")!=null || !intent.getStringExtra("search_text").isNullOrEmpty()){
                super.onBackPressed()
            }else{
                startActivity(Intent(this, HomeActivity::class.java))
                overridePendingTransition(0,0)
            }
        }

        rv_properties.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return false
            }
        })



        val layoutManager:LinearLayoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rv_properties.layoutManager = layoutManager
        adapterDetailAds = AdapterDetailAds(this, this, filterList, /*false, false*/)
        // rv_properties.setHasFixedSize(true)
        //rv_properties.isNestedScrollingEnabled = true
        rv_properties.adapter = adapterDetailAds
        img_clear_search.setOnClickListener {
            et_serach.setText("")
            this.dismissKeyboard(et_serach)
        }
        et_serach.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                dismissKeyboard(et_serach)
                page = 0
                keybaord = et_serach.text.toString()
                if(loading){
                    loading = false
                    getFilterApi()
                }

              //  filter(et_serach.text.toString())
                true
            }else{
                false
            }

        }


        rv_properties.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.getChildCount()
                    totalItemCount = layoutManager.getItemCount()
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            if (visibleItemCount<totalCount) {
                                loading = false
                                getFilterApi()
                            }

                        }
                    }
                }
            }
        })



        et_serach.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
               if (et_serach.text.isEmpty()){
                   img_clear_search.visibility = View.GONE
                   if(filterList.size >= 0){
                       tv_property_count.setText(filterList.size.toString()+ " " + getString(R.string.property_found))
                   }else{
                       tv_property_count.setText(getString(R.string.no_property_found))
                   }
                   keybaord = ""
                   page = 0
                   getFilterApi()
               } else{
                   //tv_property_count.setText(searchList.size.toString())
                   img_clear_search.visibility = View.VISIBLE

                   if (searchList.size <= 0){
                       tv_property_count.setText(getString(R.string.no_property_found))
                   } else{
                       tv_property_count.setText(searchList.size.toString()+ " " + getString(R.string.property_found))
                   }
               }


            }
            override fun afterTextChanged(s: Editable?) {
               // filter(s.toString())
            }
        })

    }

    fun getFilterApi(){
        if (isNetworkAvailable()){
            page = page + 1
            if(intent.getStringExtra("filterData")!=null){
                filterModel = Gson().fromJson(intent.getStringExtra("filterData"),FilterIntentModel::class.java)
                val slug : ArrayList<String> = filterModel.area
                val pagemap:HashMap<String,String> = HashMap()
                pagemap.put("status[]", filterModel.status)
                pagemap.put("type[]", filterModel.type)
                pagemap.put("sub_type", filterModel.sub_type)
                pagemap.put("location", filterModel.location)
                /*if (filterModel.area.isEmpty()){
                    // pagemap.put("area[]", "")
                } else{
                    pagemap.put("area[]", filterModel.area.toString())
                }*/
                pagemap.put("min-price", filterModel.min_price)
                pagemap.put("max-price", filterModel.max_price)
                pagemap.put("max-area", filterModel.max_area)
                pagemap.put("min-area", filterModel.min_area)
                pagemap.put("page", page.toString())
                pagemap.put("per_page", "10")
                pagemap.put("keybaord", keybaord)

                hitPostApiFilter(Constants.FILTER,true, Constants.FILTER_API, pagemap,slug)
            }else if(intent.getStringArrayListExtra("slug")!=null){
                val slug:ArrayList<String>  = intent.getStringArrayListExtra("slug")!!
                val pagemap:HashMap<String,String> = HashMap()
                //pagemap.put("area[]", slug)
                pagemap.put("page", page.toString())
                pagemap.put("per_page", "10")
                pagemap.put("keybaord", keybaord)
                rv_properties.scrollToPosition(0)
                if(PreferencesService.instance.userLoginStatus == true){
                    pagemap.put("user_id",PreferencesService.instance.getUserData!!.ID.toString())
                }
                hitPostApiFilter(Constants.FILTER,true, Constants.FILTER_API, pagemap,slug)
            } else if (!intent.getStringExtra("search_text").isNullOrEmpty()) {
                val pagemap:HashMap<String,String> = HashMap()
                pagemap.put("page", page.toString())
                pagemap.put("per_page", "10")
                pagemap.put("search_text", intent.getStringExtra("search_text").toString())
                rv_properties.scrollToPosition(0)
                if(PreferencesService.instance.userLoginStatus == true){
                    pagemap.put("user_id",PreferencesService.instance.getUserData!!.ID.toString())
                }
                hitPostApi(Constants.FILTER,true, Constants.AISEARCHAPI, pagemap)
            }
        } else{
            loading  = true
            showToast(this, resources.getString(R.string.intenet_error))
        }

    }

    fun filter(text: String){
        for (data: Result in filterList){
            if (data.post_title.lowercase(Locale.ROOT).contains(text)){
                searchList.add(data)
            }
        }
        adapterDetailAds.updateList(searchList)
        adapterDetailAds.notifyDataSetChanged()
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
         if(apiType.equals(Constants.FILTER)){
             loading = true
             val model: NewFeatureModel = Gson().fromJson(respopnse, NewFeatureModel::class.java)
             if(model.success){
                 if(page == 1){
                     rv_properties.scrollToPosition(0)
                     filterList.clear()
                 }

                 if(model.count>0){
                     totalCount = model.count
                     tv_property_count.setText(model.count.toString()+ " " + getString(R.string.property_found))
                 }else{
                     tv_property_count.setText(getString(R.string.no_property_found))
                 }
                 filterList.addAll(model.result)
                 adapterDetailAds.notifyDataSetChanged()
             }
         }
    }


    override fun onBackPressed() {
        if(intent.getStringExtra("slug")!=null){
            super.onBackPressed()
        }else{
            startActivity(Intent(this, HomeActivity::class.java))
            overridePendingTransition(0,0)
        }
        //super.onBackPressed()

    }

    override fun onPause() {
        super.onPause()
        try {
            if(progressHUD!=null && progressHUD!!.isShowing){
                progressHUD!!.dismiss()
            }
        }catch (e:Exception){

        }
    }

    override fun openNextActivity(model: Result?, position: Int) {
        val intent:Intent = Intent(this,AdsDetailsActivity::class.java)
        //intent.putExtra("type",type)
       // intent.putExtra("model",Gson().toJson(model))
        intent.putExtra("propertyId",model!!.iD)
        intent.putExtra("view_count",model!!.totalViews)
        startActivityForResult(intent, 1)
        overridePendingTransition(0, 0)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        // Check if the request code and result code are valid
        if (requestCode == 1 && resultCode == RESULT_OK) {
            val id = data?.getStringExtra("id")
            if(id!=null){
                val isFav = data?.getBooleanExtra("isFav",false)
                for(i in filterList){
                    if(i.iD == id){
                        i.is_fav = isFav
                    }
                }
                adapterDetailAds.notifyDataSetChanged()
            }

        }
    }

    override fun editAd(model: Result?) {

    }

    override fun deleteAd(model: Result?, position: Int) {

    }

    override fun addRemoveFav(model: Result?, position: Int) {
        if(isNetworkAvailable()){
            val propId = model!!.iD
            val userData = PreferencesService.instance.getUserData
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId)
            if(filterList.get(position).is_fav == true){
                filterList.get(position).is_fav =false
            }else{
                filterList.get(position).is_fav =true
            }
            adapterDetailAds.notifyDataSetChanged()
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)

        }
    }

    override fun openLoginActivity() {
        loginTypeDialog(false)
        //startActivity(Intent(this, LoginActivity::class.java))
        //overridePendingTransition(0,0)
    }

}