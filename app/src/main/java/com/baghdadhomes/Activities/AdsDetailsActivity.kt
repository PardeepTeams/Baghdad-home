package com.baghdadhomes.Activities

import android.content.Intent
import android.graphics.Bitmap
import android.graphics.Point
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Html
import android.util.Log
import android.view.Display
import android.view.View
import android.view.WindowManager
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidmads.library.qrgenearator.QRGContents
import androidmads.library.qrgenearator.QRGEncoder
import androidx.core.widget.NestedScrollView
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.ViewPager2
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.bumptech.glide.request.RequestOptions
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.gson.Gson
import com.google.gson.JsonObject

import com.baghdadhomes.Adapters.AdapterAutoSliderDetailPage
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Adapters.AdapterDetailAds.openDetailPage
import com.baghdadhomes.Models.*
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.ProgressHud
import com.baghdadhomes.Utils.Utility
import com.baghdadhomes.fcm.ApiClient
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject

import java.net.URLEncoder


class AdsDetailsActivity : BaseActivity(), openDetailPage, OnMapReadyCallback {
    lateinit var img_auto_scroll: ViewPager2
    lateinit var indicatorLayout: LinearLayout
    lateinit var rv_recommended: RecyclerView
    lateinit var adapterDetailAds: AdapterDetailAds
    lateinit var img_back: ImageView
    lateinit var tv_report: TextView
    lateinit var tv_viewMore: TextView
    var page:Int = 0
    var type:String = "all"
    var loading: Boolean = true
    var totalCount:Int = 0
    var propertiesList:ArrayList<Result> = ArrayList()
    var responseList:ArrayList<ResultDetail> = ArrayList()
    lateinit var nestedScrollView:NestedScrollView
    lateinit var tv_ads_title_name: TextView
    lateinit var dt_address: TextView
    lateinit var property_id: TextView
    lateinit var dt_date: TextView
    lateinit var dt_type: TextView
    lateinit var dt_price: TextView
    lateinit var dt_area: TextView
    lateinit var dt_details: TextView
    lateinit var dt_width: TextView
    lateinit var dt_rooms: TextView
    lateinit var dt_floors: TextView
    lateinit var dt_bathroom: TextView
    lateinit var tv_status: TextView
    lateinit var tv_image_count: TextView
    lateinit var imv_share: ImageView
    lateinit var imv_premium: ImageView
    lateinit var contact_whatsapp: RelativeLayout
    lateinit var relative_main: RelativeLayout
    lateinit var rl_bottom: RelativeLayout
    lateinit var contact_call: RelativeLayout
    lateinit var img_bookmark: ImageView
    lateinit var img_bookmarked: ImageView
    lateinit var ll_additional_details: LinearLayout
    lateinit var tv_additional_ads: TextView
    lateinit var tv_recommended: TextView
    lateinit var tv_view_count: TextView
    lateinit var rl_report: RelativeLayout
    lateinit var rl_counts: RelativeLayout
    lateinit var rlChat: RelativeLayout
    lateinit var rl_back: RelativeLayout
    lateinit var img_backNew: ImageView
    lateinit var rlVideoView: RelativeLayout
    lateinit var rl_fvrt: RelativeLayout
    lateinit var tvVideoCount: TextView
    lateinit var rlOwner: RelativeLayout
    lateinit var imgCircle: CircleImageView
    lateinit var tvOwnerName: TextView
    lateinit var llLocation: LinearLayout
    lateinit var mapView: MapView
    var isLogged: Boolean = true
    var prop_id: String? = null
    var prop_type: String? = null
    var prop_sub_type: String? = null
    var address: String? = null
    var map: GoogleMap? = null
    var imageList:ArrayList<String> = ArrayList()

    lateinit var tvPropertyTypeCard : TextView
    lateinit var tvPropertyTypeCard2 : TextView
    lateinit var tvAddressCard : TextView
    lateinit var tvPhoneCard : TextView
    lateinit var tvBedroomCard : TextView
    lateinit var tvFloorCard : TextView
    lateinit var tvBathroomCard : TextView
    lateinit var tvAreaCard : TextView
    lateinit var tvAppDetails : TextView
    lateinit var tvAppName : TextView
    lateinit var tvAppText : TextView
    lateinit var tvWidthCard : TextView
    lateinit var imgQr : ImageView

    var currentIndex = 0
    private var job: Job? = null
    lateinit var tv_see_all:TextView
    var isFav = false;
    var adsDetailModel: AdsDetailModel? = null

    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_ads_details)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        adjustFontScale(resources.configuration)

        inits()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        if (!prop_id.isNullOrEmpty()) {
            val map: HashMap<String, String> = HashMap()
            map["property_id"] = prop_id.toString()
            if (isLogged) {
                map["user_id"] = PreferencesService.instance.getUserData!!.ID.toString()
            }
            if (intent.getStringExtra("view_count") != null) {
                val count =intent.getStringExtra("view_count")
                map["total_view"] = count.toString()
            }else{
                val count ="0"
                map["total_view"] = count.toString()
            }
            if (isNetworkAvailable()) {
                fetchViewCount(true,map)
        // hitGetApiWithoutTokenWithParams(Constants.VIEW_COUNT,true, Constants.VIEW_COUNT_API_NEW, map)
            } else {
                Utility.showToast(this, resources.getString(R.string.intenet_error))
            }
        }

        rl_report.setOnClickListener {
            //startActivity(Intent(this, ReportAddActivity::class.java))
            if (isLogged == true){
                val intent = Intent(this, ReportAddActivity::class.java)
                intent.putExtra("post_id", prop_id.toString())
                //intent.putExtra("post_id", prop_id.toString())
                startActivity(intent)
                overridePendingTransition(0,0)
            } else{
                loginTypeDialog(false)
                //startActivity(Intent(this, LoginActivity::class.java))
                //overridePendingTransition(0,0)
            }

        }

        if (isNetworkAvailable()){
          //  fetchProperties()
           // getProperties(false)
        } else{
            showToast(this, resources.getString(R.string.intenet_error))
        }

        img_bookmark.setOnClickListener {
            if (isLogged == true){
                /*img_bookmark.visibility = View.GONE
                img_bookmarked.visibility = View.VISIBLE*/
                favUnfav()
            } else{
                loginTypeDialog(false)
                //startActivity(Intent(this, LoginActivity::class.java))
                //overridePendingTransition(0,0)
            }

        }
        img_bookmarked.setOnClickListener {
            if (isLogged == true){
                /*img_bookmark.visibility = View.VISIBLE
                img_bookmarked.visibility = View.GONE*/
                favUnfav()
            } else{
                loginTypeDialog(false)
            //startActivity(Intent(this, LoginActivity::class.java))
            //overridePendingTransition(0,0)
            }
        }


        adapterDetailAds = AdapterDetailAds(this, this,propertiesList,/* false, false*/)
        rv_recommended.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false))
        //rv_recommended.setAdapter(adapterDetailAds)
        rv_recommended.isNestedScrollingEnabled = false
        nestedScrollView.isSmoothScrollingEnabled = true

        img_back.setOnClickListener(View.OnClickListener {
            finish()
            //startActivity(Intent(this, HomeActivity::class.java))
        })
        tv_report.getPaint().isUnderlineText = true
        tv_viewMore.getPaint().isUnderlineText = true

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener(object : View.OnScrollChangeListener{
                override fun onScrollChange(
                    v: View?, scrollX: Int, scrollY: Int,
                    oldScrollX: Int, oldScrollY: Int,
                ) {
                    if (scrollY == 0){
                        img_backNew.visibility = View.GONE
                       /* rl_back.background.setTint(
                            ContextCompat.getColor(
                            this@AdsDetailsActivity, R.color.skyBlue))*/
                    } else{
                        img_backNew.visibility = View.VISIBLE
                      /*  rl_back.background.setTint(ContextCompat.getColor(
                            this@AdsDetailsActivity, R.color.red))*/
                    }
                }
            })
        }

        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener {
            if (nestedScrollView != null) {
                try {
                    if(loading){
                        val view = nestedScrollView.getChildAt(nestedScrollView.getChildCount() - 1) as View
                        val diff: Int = view.bottom - (nestedScrollView.getHeight() + nestedScrollView.getScrollY())
                        if (diff == 0) {
                            if (propertiesList.size > 0) {
                                if (isNetworkAvailable()){
                                    loading = false
                                    getProperties(true)
                                } else{
                                    showToast(this, resources.getString(R.string.intenet_error))
                                }
                            }
                        }
                    }
                } catch (e: Exception) {
                    e.message
                }
            }
        }


    }

    private fun inits(){
        tvPropertyTypeCard = findViewById(R.id.tvPropertyTypeCard)
        tvPropertyTypeCard2 = findViewById(R.id.tvPropertyTypeCard2)
        tvAddressCard = findViewById(R.id.tvAddressCard)
        tvPhoneCard = findViewById(R.id.tvPhoneCard)
        tvBedroomCard = findViewById(R.id.tvBedroomCard)
        tvFloorCard = findViewById(R.id.tvFloorCard)
        tvBathroomCard = findViewById(R.id.tvBathroomCard)
        tvWidthCard = findViewById(R.id.tvWidthCard)
        tvAreaCard = findViewById(R.id.tvAreaCard)
        tvAppDetails = findViewById(R.id.tvAppDetails)
        tvAppName = findViewById(R.id.tvAppName)
        tvAppText = findViewById(R.id.tvAppText)
        imgQr = findViewById(R.id.imgQr)
        tv_see_all = findViewById(R.id.tv_see_all)
        tv_see_all.setOnClickListener {
            var intent:Intent = Intent(this,AllPropertiesActivity::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
            overridePendingTransition(0,0)

        }
        relative_main = findViewById(R.id.relative_main)
        tv_image_count = findViewById(R.id.tv_image_count)
        tv_ads_title_name = findViewById(R.id.tv_ads_title_name)
        property_id = findViewById(R.id.property_id)
        dt_address = findViewById(R.id.dt_address)
        dt_type = findViewById(R.id.dt_type)
        dt_price = findViewById(R.id.dt_price)
        dt_area = findViewById(R.id.dt_area)
        dt_details = findViewById(R.id.dt_details)
        dt_width = findViewById(R.id.dt_width)
        dt_rooms = findViewById(R.id.dt_rooms)
        dt_date = findViewById(R.id.dt_date)
        dt_floors = findViewById(R.id.dt_floors)
        dt_bathroom = findViewById(R.id.dt_bathroom)
        tv_status = findViewById(R.id.tv_status)
        contact_whatsapp = findViewById(R.id.contact_whatsapp)
        contact_call = findViewById(R.id.contact_call)
        rl_bottom = findViewById(R.id.rl_bottom)
        ll_additional_details = findViewById(R.id.ll_additional_details)
        llLocation = findViewById(R.id.llLocation)
        mapView = findViewById(R.id.mapView)

        rl_report = findViewById(R.id.rl_report)
        rl_counts = findViewById(R.id.rl_counts)
        tv_view_count = findViewById(R.id.tv_view_count)
        tv_additional_ads = findViewById(R.id.tv_additional_ads)
        tv_recommended = findViewById(R.id.tv_recommended)
        img_auto_scroll = findViewById(R.id.img_auto_scroll)
        indicatorLayout = findViewById(R.id.indicatorLayout)
        rv_recommended = findViewById(R.id.rv_recommended)
        img_back = findViewById(R.id.img_back)
        tv_report = findViewById(R.id.tv_report)
        tv_viewMore = findViewById(R.id.tv_viewMore)
        imv_share = findViewById(R.id.imv_share)
        imv_premium = findViewById(R.id.imv_premium)
        img_bookmark = findViewById(R.id.img_bookmark)
        img_bookmarked = findViewById(R.id.img_bookmarked)
        rl_back = findViewById(R.id.rl_back)
        img_backNew = findViewById(R.id.img_backNew)
        rlChat = findViewById(R.id.rlChat)
        rlVideoView = findViewById(R.id.rlVideoView)
        tvVideoCount = findViewById(R.id.tvVideoCount)
        rl_fvrt = findViewById(R.id.rl_fvrt)
        rlOwner = findViewById(R.id.rlOwner)
        imgCircle = findViewById(R.id.imgCircle)
        tvOwnerName = findViewById(R.id.tvOwnerName)

        tv_additional_ads.paint.isUnderlineText = true
        tv_recommended.paint.isUnderlineText = true

        isLogged = PreferencesService.instance.userLoginStatus!!

        if(intent.getStringExtra("type")!=null){
            type = intent.getStringExtra("type")!!
           // modelIntent = Gson().fromJson(intent.getStringExtra("model"),Result::class.java)
            prop_id = intent.getStringExtra("propertyId")
        } else if (intent.getStringExtra("propertyId")!=null){
          //  modelIntent = Gson().fromJson(intent.getStringExtra("model"), Result::class.java)
            prop_id = intent.getStringExtra("propertyId")
        } else if (intent.getStringExtra("propertyId")!=null){
            prop_id = intent.getStringExtra("propertyId")
        }
        relative_main.visibility = View.GONE

        img_auto_scroll.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })
    }

    override fun onBackPressed() {

        val returnIntent = Intent()
        returnIntent.putExtra("id", prop_id)
        returnIntent.putExtra("isFav", isFav)
        setResult(RESULT_OK, returnIntent)
        super.onBackPressed()
    }

    fun favUnfav(){
        if(isNetworkAvailable()){
            val userData = PreferencesService.instance.getUserData
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", prop_id.toString())
            if (adsDetailModel != null) {
                if (adsDetailModel?.result?.is_fav == true) {
                    adsDetailModel?.result?.is_fav = false
                } else {
                    adsDetailModel?.result?.is_fav = true
                }
            }
            if (img_bookmark.visibility == View.VISIBLE){
                img_bookmark.visibility = View.GONE
                img_bookmarked.visibility = View.VISIBLE
            } else{
                img_bookmark.visibility = View.VISIBLE
                img_bookmarked.visibility = View.GONE
            }

            //PreferencesService.instance.saveAutoScrollPosition(true)
            hitPostApi(Constants.ADD_REMOVE_FAV_DETAIL, true, Constants.ADD_REMOVE_FAV_API, map)
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.ADD_REMOVE_FAV_DETAIL)){
            val model: NewFeatureModel = Gson().fromJson(respopnse,
                NewFeatureModel::class.java)
             isFav = !isFav
             if (model.success && adsDetailModel != null){
                 PreferencesService.instance.saveChangedPropertyDataId(adsDetailModel!!.result!!.iD,adsDetailModel!!.result!!.is_fav)
             }

        } else
        if(apiType.equals(Constants.GET_PROPERTIES_DETAIL)){
            val model: NewFeatureModel = Gson().fromJson(respopnse,
                NewFeatureModel::class.java)
            if(model.success){
                tvAppDetails.text = "www.najafhome.com\n${getString(R.string.biggest_application_in_najaf)}"
                tvAppName.text = "${getString(R.string.app_name)} | ${getString(R.string.app_name_english)}"
                tvAppText.text = getString(R.string.real_estate_co)
                if(model.count!=null){
                    totalCount = model.count
                }
                if(page == 1){
                    propertiesList.clear()
                }
                loading = true
                for (i in model.result){
                    if (i.iD!= prop_id){
                        propertiesList.add(i)
                    }
                }
                //propertiesList.addAll(model.result)
              //  val fastAdapter = FastAdapter.with(adapterDetailAds)
                rv_recommended.setAdapter(adapterDetailAds)
                adapterDetailAds.notifyDataSetChanged()
            }
        } else if (apiType.equals(Constants.VIEW_COUNT)){
                val model:AdsDetailModel = Gson().fromJson(respopnse, AdsDetailModel::class.java)
                updateUI(model)



        }
    }

    private fun updateUI(model:AdsDetailModel){
            try {
                if (progressHUD != null && progressHUD!!.isShowing()) {
                    Handler(Looper.getMainLooper()).postDelayed(Runnable {
                        progressHUD!!.dismiss()
                    },1000)

                }
            }catch (e:Exception){
                e.message
            }
            relative_main.visibility = View.VISIBLE
            if (model.success && model.result != null){
               // responseList.add(model.result)

                val newViewCount = model?.result?.property_meta?.houzez_total_property_views?.get(0) ?: "0"
                tv_view_count.setText(newViewCount)
                isFav = model.result.is_fav

              /*  if (modelIntent != null){
                    modelIntent?.totalViews = newViewCount
                    PreferencesService.instance.saveChangedPropertyData(modelIntent!!)
                }*/

                if (intent.getBooleanExtra("myAd", false)){
                    img_bookmark.visibility = View.GONE
                    img_bookmarked.visibility = View.GONE
                    rl_bottom.visibility = View.GONE
                } else{
                    img_bookmark.visibility = View.VISIBLE
                    img_bookmarked.visibility = View.GONE
                    rl_bottom.visibility = View.VISIBLE
                    if (model.result?.is_fav != null){
                        if (model.result?.is_fav == true){
                            img_bookmark.visibility = View.GONE
                            img_bookmarked.visibility = View.VISIBLE
                        } else{
                            img_bookmark.visibility = View.VISIBLE
                            img_bookmarked.visibility = View.GONE
                        }
                    }
                }

                try {
                    if (!model.result.property_meta?.houzez_geolocation_lat.isNullOrEmpty()
                        &&!model.result.property_meta?.houzez_geolocation_long.isNullOrEmpty()
                        && !model.result.property_meta?.houzez_geolocation_lat?.get(0).isNullOrEmpty()
                        && !model.result.property_meta?.houzez_geolocation_long?.get(0).isNullOrEmpty()){
                        val lat = (model.result.property_meta?.houzez_geolocation_lat?.get(0) ?: "0.0").toDouble()
                        val lng = (model.result.property_meta?.houzez_geolocation_long?.get(0) ?: "0.0").toDouble()
                        if (lat != 0.0 && lng != 0.0){
                            println("Latitude:$lat && Longitude:$lng")
                            llLocation.visibility = View.VISIBLE
                            val propertyLatLng = LatLng(lat, lng)

                            val markerOptions = MarkerOptions().position(propertyLatLng)
                                .icon(Utility.getBitmapDescriptorFromDrawable(this,R.drawable.ic_location_red))

                            Handler(Looper.getMainLooper()).postDelayed({
                                map?.addMarker(markerOptions)
                                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(propertyLatLng, 10.0f))
                            }, 100)
                        } else {
                            llLocation.visibility = View.GONE
                        }
                    } else {
                        llLocation.visibility = View.GONE
                    }
                } catch (e : Exception){
                    println(e.localizedMessage)
                }


                if (intent.getBooleanExtra("myAd", false)){
                    rlOwner.visibility = View.GONE
                } else {
                    if (model?.result?.agent_agency_info != null){
                        rlOwner.visibility = View.VISIBLE

                        Glide.with(this).load(model?.result?.agent_agency_info?.user_image.orEmpty())
                            .placeholder(R.drawable.img_placeholder).
                            apply( RequestOptions().override(300, 300).diskCacheStrategy(
                                DiskCacheStrategy.ALL)).into(imgCircle)
                        tvOwnerName.text = model?.result?.agent_agency_info?.display_name.orEmpty()

                        rlOwner.setOnClickListener {
                            val intent = Intent(this, CompanyAdsActivity::class.java)
                            intent.putExtra("agencyData", Gson().toJson(model.result.agent_agency_info))
                            startActivity(intent)
                        }
                    } else {
                        rlOwner.visibility = View.GONE
                    }
                }

                if (model.result != null && model.result?.property_meta != null
                    && !model.result.property_meta?.video.isNullOrEmpty()
                    && !model.result.property_meta?.video?.get(0).isNullOrEmpty()){
                    rlVideoView.visibility = View.VISIBLE

                } else{
                    rlVideoView.visibility = View.GONE
                }

                if (!model.result.post_modified.isNullOrEmpty()){
                    dt_date.text = Utility.getDateInFormat(model.result.post_modified)
                }

                val land = "شقة"
                if (model.result.property_type.contains(land)){
                    ll_additional_details.visibility = View.GONE
                } else{
                    ll_additional_details.visibility = View.VISIBLE
                }

                if (model.result.post_title != null){
                    tv_ads_title_name.setText(model.result.post_title)
                }

                if (model.result.iD != null){
                    property_id.setText(model.result.iD)
                }

                if (model.result.property_address!= null){
                    if(model.result.property_address.property_area!=null){
                        address =  model.result.property_address.property_area
                    }
                    if(model.result.property_address.property_city!=null){
                        address =  address + " "+model.result.property_address.property_city
                    }
                    if(model.result.property_address.property_state!=null){
                        address =  address + " "+model.result.property_address.property_state
                    }

                    if(model.result.property_address.property_country!=null){
                        address =  address + " "+model.result.property_address.property_country
                    }
                    dt_address.setText(address)
                    tvAddressCard.setText(address)
                }

                if (model.result.property_type != null && !model.result.property_type.equals("null")){
                    prop_type = model.result.property_type
                } else{
                    prop_type = ""
                }

                if (model.result.property_meta != null){
                    if (model.result.property_meta.fave_property_garage_size != null){
                        prop_sub_type = model.result.property_meta.fave_property_garage_size.get(0)
                    } else{
                        prop_sub_type = ""
                    }
                } else{
                    prop_sub_type = ""
                }

                val language = PreferencesService.instance.getLanguage()
                if (language == "ar"){
                    if (prop_sub_type!!.contains("house")){
                        prop_sub_type = resources.getString(R.string.house)
                    }
                    else if (prop_sub_type!!.contains("apart_house")){
                        prop_sub_type = resources.getString(R.string.apart_house)
                    }
                    else if (prop_sub_type!!.contains("apartment")){
                        prop_sub_type = resources.getString(R.string.apartment)
                    }
                    else if (prop_sub_type!!.contains("villa")){
                        prop_sub_type = resources.getString(R.string.villa)
                    }
                    else if (prop_sub_type!!.contains("residence_other")){
                        prop_sub_type = resources.getString(R.string.other)
                    }
                    else if (prop_sub_type!!.contains("office")){
                        prop_sub_type = resources.getString(R.string.office)
                    }
                    else if (prop_sub_type!!.contains("shop")){
                        prop_sub_type = resources.getString(R.string.shop)
                    }
                    else if (prop_sub_type!!.contains("store")){
                        prop_sub_type = resources.getString(R.string.store)
                    }
                    else if (prop_sub_type!!.contains("building")){
                        prop_sub_type = resources.getString(R.string.building)
                    }
                    else if (prop_sub_type!!.contains("factory")){
                        prop_sub_type = resources.getString(R.string.factory)
                    }
                    else if (prop_sub_type!!.contains("showroom")){
                        prop_sub_type = resources.getString(R.string.showroom)
                    }
                    else if (prop_sub_type!!.contains("commercial_other")){
                        prop_sub_type = resources.getString(R.string.other)
                    }
                    else if (prop_sub_type!!.contains("agriculture")){
                        prop_sub_type = resources.getString(R.string.agriculture)
                    }
                    else if (prop_sub_type!!.contains("commercial")){
                        prop_sub_type = resources.getString(R.string.commercial)
                    }
                    else if (prop_sub_type!!.contains("residencial")){
                        prop_sub_type = resources.getString(R.string.residential)
                    }
                    else if (prop_sub_type!!.contains("industrial")){
                        prop_sub_type = resources.getString(R.string.industrial)
                    }
                    else{
                        prop_sub_type = resources.getString(R.string.other)
                    }
                    dt_type.setText(prop_sub_type + " - " + prop_type)

                    tvPropertyTypeCard.text = "$prop_sub_type ${model?.result?.property_attr?.property_status.orEmpty()}"
                    tvPropertyTypeCard2.text = "$prop_sub_type - $prop_type ${model?.result?.property_attr?.property_status.orEmpty()}"
                } else{
                    dt_type.setText(prop_sub_type + " - " + prop_type)

                    tvPropertyTypeCard.text = "$prop_sub_type ${model?.result?.property_attr?.property_status.orEmpty()}"
                    tvPropertyTypeCard2.text = "$prop_sub_type - $prop_type ${model?.result?.property_attr?.property_status.orEmpty()}"
                }
                //dt_type.setText(prop_sub_type + " " + prop_type)

                if(model.result.price!=null && !model.result.price.equals("null")){
                    dt_price.setText("("+model.result.price+")"+ resources.getString(R.string.currency_code))
                } else{
                    dt_price.setText("(0)"+ resources.getString(R.string.currency_code))
                }

                if (model.result.property_meta.fave_property_size != null){
                    dt_area.setText(model.result.property_meta.fave_property_size.get(0) + " "+ resources.getString(R.string.m))
                    tvAreaCard.setText(model.result.property_meta.fave_property_size.get(0) + " "+ resources.getString(R.string.m))
                } else{
                    dt_area.setText("00 "+ resources.getString(R.string.m))
                    tvAreaCard.setText("00 "+ resources.getString(R.string.m))
                }

                if (model.result.post_content != null) {
                    val html = model.result.post_content
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        dt_details.setText(Html.fromHtml(html, 0))
                    } else {
                        dt_details.setText(Html.fromHtml(html))
                    }
                }else{
                    dt_details.setText("")
                }


                if (model.result.property_meta != null){
                    if (model.result.property_meta.fave_property_land != null){
                        dt_width.setText(model.result.property_meta.fave_property_land.get(0) + resources.getString(R.string.m))
                        tvWidthCard.setText(model.result.property_meta.fave_property_land.get(0) + resources.getString(R.string.m))
                    } else{
                        dt_width.setText("0" + resources.getString(R.string.m))
                        tvWidthCard.setText("0" + resources.getString(R.string.m))
                    }
                } else{
                    dt_width.setText("0" + resources.getString(R.string.m))
                    tvWidthCard.setText("0" + resources.getString(R.string.m))
                }


                if(model.result.property_meta.fave_property_bedrooms!=null){
                    dt_rooms.setText(model.result.property_meta.fave_property_bedrooms.get(0))
                    tvBedroomCard.setText(model.result.property_meta.fave_property_bedrooms.get(0))
                }else{
                    dt_rooms.setText("0")
                    tvBedroomCard.setText("0")
                }

                if (model.result.property_meta != null){
                    if (model.result.property_meta.fave_property_garage != null){
                        dt_floors.setText(model.result.property_meta.fave_property_garage.get(0))
                        tvFloorCard.setText(model.result.property_meta.fave_property_garage.get(0))
                    }
                } else{
                    dt_floors.setText("0")
                    tvFloorCard.setText("0")
                }



                if(model.result.property_meta.fave_property_bathrooms!=null){
                    dt_bathroom.setText(model.result.property_meta.fave_property_bathrooms.get(0))
                    tvBathroomCard.setText(model.result.property_meta.fave_property_bathrooms.get(0))
                }else{
                    dt_bathroom.setText("0")
                    tvBathroomCard.setText("0")
                }

                if (model.result.property_attr != null) {
                    if (model.result.property_attr!!.property_status != null) {
                        tv_status.setVisibility(View.VISIBLE)
                        tv_status.setText(model.result.property_attr.property_status)
                    } else {
                        tv_status.setVisibility(View.GONE)
                    }
                } else {
                    tv_status.setVisibility(View.GONE)
                }

                if(model.result.agent_agency_info !=null && !model.result.agent_agency_info.equals("null")){
                    if (!model.result.agent_agency_info.whatsapp_number.isNullOrEmpty()){
                        contact_whatsapp.visibility = View.VISIBLE
                        contact_whatsapp.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW)
                            val url = "https://api.whatsapp.com/send?phone="+
                                    model.result.agent_agency_info.whatsapp_number+
                                    "&text=" + URLEncoder.encode("", "UTF-8");
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                        }
                    } else{
                        contact_whatsapp.visibility = View.GONE
                    }

                    if (!model.result.agent_agency_info.call_number.isNullOrEmpty() &&
                        !model.result.agent_agency_info.call_number.equals("null") ){
                        contact_call.visibility = View.VISIBLE
                        contact_call.setOnClickListener {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:" + model.result.agent_agency_info.call_number)
                            startActivity(intent)
                        }
                        tvPhoneCard.text = model.result.agent_agency_info.call_number
                    } else{
                        contact_call.visibility = View.GONE
                        tvPhoneCard.visibility = View.GONE
                    }
                }else{
                    rl_bottom.visibility = View.GONE
                    contact_whatsapp.visibility = View.GONE
                    contact_call.visibility = View.GONE
                }

                if(model.result.is_premium){
                    imv_premium.visibility = View.VISIBLE
                }else{
                    imv_premium.visibility = View.GONE
                }

                if (model.result.link != null){
                    imv_share.setOnClickListener {
                        val sharingIntent = Intent(Intent.ACTION_SEND)
                        sharingIntent.type = "text/plain"
                        sharingIntent.putExtra(Intent.EXTRA_TEXT, "${getString(R.string.take_look_at_property)}\n\n${model.result.link}")
                        startActivity(Intent.createChooser(sharingIntent, ""))
                    }
                    generateQrCode(model.result.link.toString())
                } else{
                    imv_share.visibility = View.GONE
                    generateQrCode("www.najafhome.com")
                }


                if (!model.result.property_images.isNullOrEmpty()){
                    for (i in model.result.property_images){
                        if (i != "false"){
                            imageList.add(i)
                            //tv_image_count.setText(imageList.size.toString())
                        }
                    }
                    if (imageList.isNotEmpty()){
                        tv_image_count.setText(imageList.size.toString())
                    } else{
                        tv_image_count.setText("0")
                    }
                }else{
                    if(!model.result.thumbnail.isNullOrEmpty()){
                        imageList.add(model.result.thumbnail)
                        if (imageList.isNotEmpty()){
                            tv_image_count.setText(imageList.size.toString())
                        } else{
                            tv_image_count.setText("0")
                        }
                    }

                }

                rlChat.setOnClickListener {
                    if (PreferencesService.instance.userLoginStatus == true) {
                        if (model.result?.agent_agency_info != null) {
                            val postData = AdsDataChat(
                                iD = model.result?.iD,
                                post_title = model.result?.post_title,
                                thumbnail = model.result?.thumbnail,
                                price = model.result?.price
                            )
                            val intent = Intent(this, MessagingActivity::class.java)
                            intent.putExtra("receiverModel", Gson().toJson(model.result?.agent_agency_info))
                            intent.putExtra("postData", Gson().toJson(postData))
                            startActivity(intent)
                        }
                    } else{
                        loginTypeDialog(false)
                        //startActivity(Intent(this,LoginActivity::class.java))
                        //overridePendingTransition(0,0)
                    }
                }
                img_auto_scroll.adapter = AdapterAutoSliderDetailPage(this, imageList)
                setupIndicators(imageList.size)
                setCurrentIndicator(0)
                startAutoSlide()
                page = 0
                fetchProperties(true)

            }
    }

    private fun setCurrentIndicator(position: Int) {
        currentIndex = position;
        val childCount = indicatorLayout.childCount
        for (i in 0 until childCount) {
            val child = indicatorLayout.getChildAt(i)
            if (i == position) {
                child.setBackgroundResource(R.drawable.indicator_active)
            } else {
                child.setBackgroundResource(R.drawable.indicator_inactive)
            }
        }
    }

    private fun setupIndicators(count: Int) {
        indicatorLayout.removeAllViews()
        for (i in 0 until count) {
            val indicator = ImageView(this)
            indicator.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            (indicator.layoutParams as LinearLayout.LayoutParams).setMargins(5, 0, 5, 0)
            indicator.setBackgroundResource(R.drawable.indicator_inactive)
            indicatorLayout.addView(indicator)
        }
    }

    private fun startAutoSlide() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(4000)
                if (currentIndex == (imageList.size - 1)){
                    currentIndex = 0
                } else {
                    currentIndex += 1
                }
                img_auto_scroll.setCurrentItem(currentIndex, true)
            }
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

    private fun generateQrCode(qrData : String){
        // below line is for getting the windowmanager service.
        val manager = getSystemService(WINDOW_SERVICE) as WindowManager

        // initializing a variable for default display.
        val display: Display = manager.defaultDisplay

        // creating a variable for point which is to be displayed in QR Code.
        val point = Point()
        display.getSize(point)

        // getting width andheight of a point
        val width: Int = point.x
        val height: Int = point.y

        // generating dimension from width and height.
        var dimen = if (width < height) width else height
        //dimen = dimen * 3 / 4

        // setting this dimensions inside our qr codeencoder to generate our qr code.
        val qrgEncoder = QRGEncoder(qrData, null, QRGContents.Type.TEXT, dimen)
        try {
            // getting our qrcode in the form of bitmap.
            val bitmap: Bitmap = qrgEncoder.getBitmap()
            // the bitmap is set inside our getB using .setimagebitmap method.
            imgQr.setImageBitmap(bitmap)
        } catch (e: Exception) {
            // this method is called forexception handling.
            Log.e("Tag", e.toString())
        }
    }

    fun getProperties(show:Boolean){
        if (isNetworkAvailable()){
            if(propertiesList.size<totalCount){
                fetchProperties(true)
               // hitGetApiWithoutTokenWithParams(Constants.GET_PROPERTIES_DETAIL,show, Constants.GETFEATUREDPROPERTIESNEW, pagemap)
            }
        } else{
            showToast(this, resources.getString(R.string.intenet_error))
        }
    }

    override fun openNextActivity(model: Result?, position: Int) {
        val intent = Intent(this,AdsDetailsActivity::class.java)
        intent.putExtra("type",type)
        intent.putExtra("propertyId",model!!.iD)
        intent.putExtra("view_count",model!!.totalViews)
        intent.putExtra("myAd",false)
        startActivity(intent)
        finish()
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
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId)
            //map.put("", favStatus)
            if(propertiesList.get(position).is_fav == true){
                propertiesList.get(position).is_fav =false
            }else{
                propertiesList.get(position).is_fav =true
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


    fun fetchProperties(showLoader:Boolean){
        page = page +1
        val pagemap:HashMap<String,String> = HashMap()
        pagemap.put("page",page.toString())
        pagemap.put("per_page","10")
        if(!type.equals("all")){
            pagemap.put("type",type)
        }
        if(PreferencesService.instance.userLoginStatus == true){
            pagemap.put("user_id",PreferencesService.instance.getUserData!!.ID.toString())
        }
        lifecycleScope.launch((Dispatchers.IO)) {
            if(showLoader){
                withContext(Dispatchers.Main) {
                    try {
                        progressHUD?.dismiss()
                    } catch (e: Exception) {
                        e.printStackTrace()
                    }
                    progressHUD = ProgressHud.show(this@AdsDetailsActivity, false, false)
                }
            }
            try {

                val response = ApiClient.api?.getProperties(ApiClient.baseUrl + Constants.GETFEATUREDPROPERTIESNEW,pagemap)
                if (response != null) {
                    if (response.isSuccessful && response.code() == 200) {
                        val model: NewFeatureModel? = response.body()
                        if (model != null) {
                            if(model.success){
                                withContext(Dispatchers.Main) {
                                    tvAppDetails.text = "www.najafhome.com\n${getString(R.string.biggest_application_in_najaf)}"
                                    tvAppName.text = "${getString(R.string.app_name)} | ${getString(R.string.app_name_english)}"
                                    tvAppText.text = getString(R.string.real_estate_co)
                                    if(model.count!=null){
                                        totalCount = model.count
                                    }
                                    if(page == 1){
                                        propertiesList.clear()
                                    }
                                    loading = true
                                    for (i in model.result){
                                        if (i.iD!= prop_id){
                                            propertiesList.add(i)
                                        }
                                    }
                                    rv_recommended.setAdapter(adapterDetailAds)
                                    adapterDetailAds.notifyDataSetChanged()
                                    if(showLoader){
                                        try {
                                            lifecycleScope.launch(Dispatchers.Main) {
                                                delay(100)
                                                progressHUD?.dismiss() // Dismiss after delay
                                            }

                                        }catch (_:Exception){

                                        }
                                    }
                                }

                            }else{
                                runOnUiThread{
                                    if(showLoader){
                                        try {
                                            if(progressHUD!=null){
                                                progressHUD!!.dismiss()
                                            }

                                        }catch (e:Exception){

                                        }
                                    }
                                    Toast.makeText(
                                        this@AdsDetailsActivity,
                                        resources.getString(R.string.something_went_wrong),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }

                    }
                    else if (response.code()==400) {
                        runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                //  Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                                Utility.showToast(this@AdsDetailsActivity, jObjError.getString(Constants.MESSAGE))
                            } catch (e: Exception) {
                            }
                        }

                    }
                    else if (response.code() == 401) {
                        runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            Toast.makeText(
                                this@AdsDetailsActivity,
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (response.code() == 403 || response.code() == 404) {

                        runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            Toast.makeText(
                                this@AdsDetailsActivity,
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } else if (response.code() == 409) {
                        runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string());
                                // Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                                Utility.showToast(this@AdsDetailsActivity, jObjError.getString(Constants.MESSAGE))
                            } catch (e: Exception) {
                            }
                        }

                    } else {
                        runOnUiThread {
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            Toast.makeText(
                                this@AdsDetailsActivity,
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    if(showLoader){
                        try {
                            if(progressHUD!=null){
                                progressHUD!!.dismiss()
                            }

                        }catch (e:Exception){

                        }
                    }
                    Log.d("callJason",e.message.toString())
                    Toast.makeText(
                        this@AdsDetailsActivity,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }
        }
    }


    fun fetchViewCount(showLoader:Boolean,map: HashMap<String, String>){
        lifecycleScope.launch {
            if(showLoader){
                try {
                    if(progressHUD!=null){
                        progressHUD!!.dismiss()
                    }
                }catch (e:Exception){

                }
                progressHUD = ProgressHud.show(
                    this@AdsDetailsActivity,
                    false,
                    false
                )
            }
            try {
                val response = ApiClient.api?.getView(ApiClient.baseUrl + Constants.VIEW_COUNT_API_NEW,map)
                if (response != null) {
                    if (response.isSuccessful && response.code() == 200) {
                        adsDetailModel = response.body()
                        if (adsDetailModel != null) {
                            if(adsDetailModel!!.success){
                                runOnUiThread {
                                    updateUI(adsDetailModel!!)
                                    if(showLoader){
                                        progressHUD?.dismiss()
                                    }
                                }

                            }else{
                                runOnUiThread{
                                    if(showLoader){
                                        try {
                                            if(progressHUD!=null){
                                                progressHUD!!.dismiss()
                                            }

                                        }catch (e:Exception){

                                        }
                                    }
                                    Toast.makeText(
                                        this@AdsDetailsActivity,
                                        resources.getString(R.string.something_went_wrong),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                }
                            }
                        }

                    }
                    else if (response.code()==400) {
                        runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                //  Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                                Utility.showToast(this@AdsDetailsActivity, jObjError.getString(Constants.MESSAGE))
                            } catch (e: Exception) {
                            }
                        }

                    }
                    else if (response.code() == 401) {
                        runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            Toast.makeText(
                                this@AdsDetailsActivity,
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (response.code() == 403 || response.code() == 404) {

                        runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            Toast.makeText(
                                this@AdsDetailsActivity,
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    } else if (response.code() == 409) {
                        runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string());
                                // Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                                Utility.showToast(this@AdsDetailsActivity, jObjError.getString(Constants.MESSAGE))
                            } catch (e: Exception) {
                            }
                        }

                    } else {
                        runOnUiThread {
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }

                                }catch (e:Exception){

                                }
                            }
                            Toast.makeText(
                                this@AdsDetailsActivity,
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                }
            } catch (e: Exception) {
                runOnUiThread {
                    if(showLoader){
                        try {
                            if(progressHUD!=null){
                                progressHUD!!.dismiss()
                            }

                        }catch (e:Exception){

                        }
                    }
                    Log.d("callJason",e.message.toString())
                    Toast.makeText(
                        this@AdsDetailsActivity,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }

}
    }
}