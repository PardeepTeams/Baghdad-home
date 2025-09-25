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
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Adapters.AdapterStoriesAgency
import com.baghdadhomes.Models.*
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import de.hdodenhof.circleimageview.CircleImageView
import java.util.*

class CompanyAdsActivity : BaseActivity(), AdapterDetailAds.openDetailPage, OnMapReadyCallback,
    AdapterStoriesAgency.StoryClick {
    private lateinit var imgBack : ImageView
    private lateinit var etSearch : EditText
    private lateinit var imgClearSearch : ImageView
    private lateinit var tvAdsCount : TextView
    private lateinit var tvSharePage : TextView
    private lateinit var rvAds : RecyclerView
    private lateinit var imgBig : ImageView
    private lateinit var img_watermark : ImageView
    private lateinit var imgCircle : CircleImageView
    private lateinit var tvName : TextView
    private lateinit var tvCity : TextView
    private lateinit var tvAddress : TextView
    private lateinit var tvManager : TextView
    private lateinit var tvPhone : TextView
    private lateinit var cardSearch : CardView
    private lateinit var llCompanyDetails : LinearLayout
    private lateinit var llLocation : LinearLayout
    private lateinit var mapView : MapView
    private lateinit var llReels : LinearLayout
    private lateinit var rvReels : RecyclerView
    private lateinit var tvFollow : TextView
    private lateinit var tvFollowersCount : TextView
    private lateinit var llFollowDetails : LinearLayout

    private lateinit var adapterDetailAds: AdapterDetailAds
    private val adsList : ArrayList<Result> = ArrayList()

    private lateinit var adapterStoriesAgency: AdapterStoriesAgency
    private val reelsList : ArrayList<ReelsData> = ArrayList()

    private var map : GoogleMap ?= null
    private var count : Int ?= 0
    private var agencyId : String ?= ""
    private var userId : String ?= ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_company_ads)
        if (PreferencesService.instance.userLoginStatus == true && PreferencesService.instance.getUserData != null) {
            userId = PreferencesService.instance.getUserData!!.ID.toString()
        }
        inits()

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        if (intent.getStringExtra("agencyData") != null){
            val agencyData : AgenciesData? = Gson().fromJson(intent.getStringExtra("agencyData"), AgenciesData::class.java)
            agencyId = agencyData?.ID.orEmpty()

        } else if (!intent.getStringExtra("agentId").isNullOrEmpty()){
            agencyId = intent.getStringExtra("agentId").orEmpty()
        }

        if (isNetworkAvailable()){
            if (agencyId.toString().isEmpty()){
                return
            }
            val map : HashMap<String, String> = HashMap()
            map["user_id"] = agencyId.toString()
            if(PreferencesService.instance.userLoginStatus == true){
                map["logged_user_id"] = userId.orEmpty()
            }

            hitPostApi(
                type = Constants.AGENCY_ADS,
                showLoader = true,
                url = Constants.AGENCY_ADS_API,
                map = map
            )
        } else {
            showToast(this, getString(R.string.intenet_error))
        }
    }

    private fun inits(){
        imgBack = findViewById(R.id.imgBack)
        etSearch = findViewById(R.id.etSearch)
        imgClearSearch = findViewById(R.id.imgClearSearch)
        tvAdsCount = findViewById(R.id.tvAdsCount)
        rvAds = findViewById(R.id.rvAds)
        imgBig = findViewById(R.id.imgBig)
        img_watermark = findViewById(R.id.img_watermark)
        imgCircle = findViewById(R.id.imgCircle)
        tvName = findViewById(R.id.tvName)
        tvCity = findViewById(R.id.tvCity)
        tvAddress = findViewById(R.id.tvAddress)
        tvPhone = findViewById(R.id.tvPhone)
        tvManager = findViewById(R.id.tvManager)
        cardSearch = findViewById(R.id.cardSearch)
        llCompanyDetails = findViewById(R.id.llCompanyDetails)
        llLocation = findViewById(R.id.llLocation)
        mapView = findViewById(R.id.mapView)
        llReels = findViewById(R.id.llReels)
        rvReels = findViewById(R.id.rvReels)
        tvFollow = findViewById(R.id.tvFollow)
        tvFollowersCount = findViewById(R.id.tvFollowersCount)
        llFollowDetails = findViewById(R.id.llFollowDetails)
        tvSharePage = findViewById(R.id.tvSharePage)

        llCompanyDetails.visibility = View.GONE
        cardSearch.visibility = View.GONE

        adapterDetailAds = AdapterDetailAds(this, this, adsList, /*false, false*/)
        rvAds.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        rvAds.adapter = adapterDetailAds

        adapterStoriesAgency = AdapterStoriesAgency(this, reelsList, this)
        rvReels.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        rvReels.adapter = adapterStoriesAgency

        imgBack.setOnClickListener {
            finish()
        }

        rvAds.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return false
            }
        })


        imgClearSearch.setOnClickListener {
            etSearch.setText("")
            this.dismissKeyboard(etSearch)
        }

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                dismissKeyboard(etSearch)
                filter(etSearch.text.toString())
                true
            }else{
                false
            }

        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etSearch.text.isEmpty()){
                    imgClearSearch.visibility = View.GONE
                    if(adapterDetailAds.propertiesList.size >= 0){
                        tvAdsCount.text = adapterDetailAds.propertiesList.size.toString()+ " " + getString(R.string.property_found)
                    }else{
                        tvAdsCount.text = getString(R.string.no_property_found)
                    }
                } else{
                    //tv_property_count.setText(searchList.size.toString())
                    imgClearSearch.visibility = View.VISIBLE

                    if (adsList.size <= 0){
                        tvAdsCount.text = getString(R.string.no_property_found)
                    } else{
                        tvAdsCount.text = adapterDetailAds.propertiesList.size.toString()+ " " + getString(R.string.property_found)
                    }
                }


            }
            override fun afterTextChanged(s: Editable?) {
                filter(s.toString())
            }
        })

        tvFollow.setOnClickListener {
            if (PreferencesService.instance.userLoginStatus == false) {
                loginTypeDialog(false)
                return@setOnClickListener
            }

            if (!isNetworkAvailable()) {
                Utility.showToast(this, getString(R.string.intenet_error))
                return@setOnClickListener
            }

            if (tvFollow.text.toString() == getString(R.string.following)){
                count = count!! - 1
                tvFollow.text = getString(R.string.follow)

                tvFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add,0,0,0)
            } else if (tvFollow.text.toString() == getString(R.string.follow)){
                count = count!! + 1
                tvFollow.text = getString(R.string.following)

                tvFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok,0,0,0)
            }

            tvFollowersCount.text = "($count) ${getString(R.string.followers)}"

            val map : HashMap<String, String> = HashMap()
            map["user_id"] = userId.orEmpty()
            map["agency_id"] = agencyId.orEmpty()

            hitPostApi(
                type = Constants.FOLLOW_UNFOLLOW_AGENCY,
                showLoader = false,
                url = Constants.FOLLOW_UNFOLLOW_AGENCY_API,
                map = map
            )
        }
    }

    fun filter(text: String){
        val filteredList : ArrayList<Result> = ArrayList()
        for (data: Result in adsList){
            if (data.post_title.lowercase(Locale.ROOT).contains(text)){
                filteredList.add(data)
            }
        }
        adapterDetailAds.updateList(filteredList)
    }


    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType == Constants.AGENCY_ADS){
            val model = Gson().fromJson(respopnse, AgencyAdsResponse::class.java)
            if (model.success == true){
                adsList.clear()

                //"role_type"- "houzez_agency" || "houzez_seller"
                if (model?.agency_details != null && model.agency_details?.role_type == "houzez_agency"){
                    llCompanyDetails.visibility = View.VISIBLE
                    cardSearch.visibility = View.GONE

                    if (!model.agency_details?.share_url.isNullOrEmpty()){
                        tvSharePage.setOnClickListener {
                            val sharingIntent = Intent(Intent.ACTION_SEND)
                            sharingIntent.type = "text/plain"
                            sharingIntent.putExtra(Intent.EXTRA_TEXT, "${getString(R.string.take_look_at_real_estate)}\n\n${model.agency_details?.share_url}")
                            startActivity(Intent.createChooser(sharingIntent, ""))
                        }
                    } else {
                        tvSharePage.visibility = View.GONE
                    }

                    count = (model.agency_details?.total_followers ?: "0").toInt()
                    tvFollowersCount.text = "($count) ${getString(R.string.followers)}"
                    if (userId.orEmpty() != model.agency_details?.ID){
                        tvFollow.visibility = View.VISIBLE
                        if (model.agency_details?.is_followed == true){
                            tvFollow.text = getString(R.string.following)
                            tvFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_ok,0,0,0)
                        } else {
                            tvFollow.text = getString(R.string.follow)
                            tvFollow.setCompoundDrawablesWithIntrinsicBounds(R.drawable.ic_add,0,0,0)
                        }
                    } else {
                        tvFollow.visibility = View.GONE
                    }

                    if (!model.agency_details?.user_image.isNullOrEmpty()) {
                        img_watermark.visibility = View.VISIBLE

                        Glide.with(this).load(model.agency_details?.user_image.orEmpty()).placeholder(R.drawable.img_placeholder).into(imgBig)
                        Glide.with(this).load(model.agency_details?.user_image.orEmpty()).placeholder(R.drawable.img_placeholder).into(imgCircle)
                    } else {
                        img_watermark.visibility = View.GONE

                        Glide.with(this).load(R.drawable.img_placeholder).into(imgBig)
                        Glide.with(this).load(R.drawable.img_placeholder).into(imgCircle)
                    }

                    tvName.text = model.agency_details?.display_name.orEmpty()

                    var city : String ?= null
                    var nbhd : String ?= null

                    if (!model.agency_details?.city_slug.isNullOrEmpty()){
                        val cityData = model.agency_details?.city_slug?.get(0)
                        city = if(PreferencesService.instance.getLanguage().equals("ar")){
                            cityData?.name.orEmpty()
                        }else{
                            if(cityData?.description.isNullOrEmpty()){
                                cityData?.description.orEmpty()
                            }else{
                                cityData?.name.orEmpty()
                            }

                        }
                    }

                    if (!model.agency_details?.nbhd_slug.isNullOrEmpty()){
                        val nbhdData = model.agency_details?.nbhd_slug?.get(0)
                        nbhd = if(PreferencesService.instance.getLanguage().equals("ar")){
                            "\n${nbhdData?.name.orEmpty()}"
                        }else{
                            if(nbhdData?.description.isNullOrEmpty()){
                                "\n${nbhdData?.description.orEmpty()}"
                            }else{
                                "\n${nbhdData?.name.orEmpty()}"
                            }

                        }
                    }

                    tvCity.text = "${city.orEmpty()} ${nbhd.orEmpty()} "

                    tvAddress.text = model.agency_details?.address.orEmpty()

                    if (model.agency_details?.call_number.isNullOrEmpty()){
                        tvPhone.visibility = View.GONE
                    } else {
                        tvPhone.visibility = View.VISIBLE
                        tvPhone.text = model.agency_details?.call_number
                    }
                    if (model.agency_details?.manager_name.isNullOrEmpty()){
                        tvManager.visibility = View.GONE
                    } else {
                        tvManager.visibility = View.VISIBLE
                        tvManager.text = "${getString(R.string.office_manager)} : ${model.agency_details?.manager_name}"
                    }

                    try {
                        if (!model.agency_details?.lat.isNullOrEmpty() && !model.agency_details?.lng.isNullOrEmpty()) {
                            val lat = (model.agency_details?.lat ?: "0.0").toDouble()
                            val lng = (model.agency_details?.lng ?: "0.0").toDouble()
                            if (lat != 0.0 && lng != 0.0) {
                                llLocation.visibility = View.VISIBLE
                                val latLng = LatLng(lat, lng)
                                val markerOptions = MarkerOptions().position(latLng)
                                    .icon(Utility.getBitmapDescriptorFromDrawable(this,R.drawable.ic_location_red))
                                map?.addMarker(markerOptions)
                                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng, 10.0f))
                            } else {
                                llLocation.visibility = View.GONE
                            }
                        }
                    } catch (e : java.lang.Exception){
                        println(e.localizedMessage)
                    }

                    if (!model.reels_details.isNullOrEmpty()){
                        llReels.visibility = View.VISIBLE
                        reelsList.clear()
                        reelsList.addAll(model.reels_details!!)
                        adapterStoriesAgency.notifyDataSetChanged()
                    } else {
                        llReels.visibility = View.GONE
                    }
                } else {
                    llCompanyDetails.visibility = View.GONE
                    cardSearch.visibility = View.VISIBLE
                }

                if (!model.response.isNullOrEmpty()){
                    adsList.addAll(model.response!!)
                    tvAdsCount.text = adsList.size.toString()+ " " + getString(R.string.property_found)
                } else {
                    tvAdsCount.text = getString(R.string.no_property_found)
                }
                adapterDetailAds.notifyDataSetChanged()
            }
        }
    }

    override fun openNextActivity(model: Result?, position: Int) {
        val intent = Intent(this,AdsDetailsActivity::class.java)
        //intent.putExtra("type",type)
        intent.putExtra("propertyId",model!!.iD)
        intent.putExtra("view_count",model!!.totalViews)
      //  intent.putExtra("model", Gson().toJson(model))
        startActivity(intent)
        //overridePendingTransition(0, 0)
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
            map["user_id"] = userData!!.ID!!
            map["listing_id"] = propId
            val index = adsList.indexOf(model)
            adsList[index].is_fav = adsList[index].is_fav != true
            adapterDetailAds.notifyDataSetChanged()
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)

        }

    }

    override fun openLoginActivity() {
        loginTypeDialog(false)
        //startActivity(Intent(this, LoginActivity::class.java))
        //overridePendingTransition(0,0)
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }

    override fun onStoryClick(position: Int) {
        val model = ReelResult(0,"",true,reelsList)
        Constants.reelsModel = model
        val intent = Intent(this,VideoViewActivity::class.java)
        intent.putExtra("position",position)
        startActivity(intent)
       // overridePendingTransition(0, 0)
    }

}