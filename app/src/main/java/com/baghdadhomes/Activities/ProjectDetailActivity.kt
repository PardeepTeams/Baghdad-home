package com.baghdadhomes.Activities

import android.annotation.SuppressLint
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
import com.baghdadhomes.Activities.ProjectDetailActivity
import com.baghdadhomes.Adapters.AdapterAutoSliderDetailPage
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Adapters.AdapterDetailAds.openDetailPage
import com.baghdadhomes.Models.AdsDataChat
import com.baghdadhomes.Models.AdsDetailModel
import com.baghdadhomes.Models.NewFeatureModel
import com.baghdadhomes.Models.Result
import com.baghdadhomes.Models.ResultDetail
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.ProgressHud
import com.baghdadhomes.Utils.Utility
import com.baghdadhomes.fcm.ApiClient
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
import de.hdodenhof.circleimageview.CircleImageView
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import org.json.JSONObject
import java.net.URLEncoder
import androidx.core.view.size
import androidx.core.view.isVisible
import com.baghdadhomes.Adapters.AdapterAreaItem
import com.baghdadhomes.Adapters.AdapterFloorBedrooms
import com.baghdadhomes.Adapters.AdapterFloorPlans
import com.baghdadhomes.Adapters.AdapterPriceItems
import com.baghdadhomes.Adapters.AdapterProjectProperties
import com.baghdadhomes.Adapters.AdapterProjectProperties.ProjectPropertyActions
import com.baghdadhomes.Models.ChildProperty
import com.baghdadhomes.Models.ModelPropertyBedrooms
import com.baghdadhomes.Models.ModelPropertyPriceTable
import com.baghdadhomes.Models.ProjectDetailResponse
import com.baghdadhomes.Models.ProjectFloorPlan

class ProjectDetailActivity : BaseActivity(), ProjectPropertyActions, OnMapReadyCallback {
    lateinit var img_auto_scroll: ViewPager2
    lateinit var indicatorLayout: LinearLayout
    lateinit var rv_recommended: RecyclerView
    lateinit var adapterProjectProperties : AdapterProjectProperties
    lateinit var img_back: ImageView
    lateinit var tv_report: TextView
    lateinit var tv_viewMore: TextView
    var page:Int = 0
    var type:String = "all"
    var loading: Boolean = true
    var totalCount:Int = 0
    var propertiesList:ArrayList<ChildProperty> = ArrayList()
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
    lateinit var rvAreas: RecyclerView
    lateinit var rvAreaPrices: RecyclerView
    lateinit var rvBedrooms: RecyclerView
    lateinit var rvFloorPlans: RecyclerView

    var isLogged: Boolean = true
    var prop_id: String = ""
    var prop_type: String? = null
    var prop_sub_type: String? = null
    var address: String? = null
    var map: GoogleMap? = null
    var imageList:ArrayList<String> = ArrayList()

    var areaList : ArrayList<ModelPropertyBedrooms> = ArrayList()
    lateinit var adapterAreaItem : AdapterAreaItem

    var priceTableList : ArrayList<ModelPropertyPriceTable> = ArrayList()
    lateinit var priceItemsAdapter : AdapterPriceItems

    var bedroomsList : ArrayList<ModelPropertyBedrooms> = ArrayList()
    lateinit var adapterFloorBedrooms : AdapterFloorBedrooms

    var floorPlansList : ArrayList<ProjectFloorPlan> = ArrayList()
    lateinit var adapterFloorPlans: AdapterFloorPlans


    var currentIndex = 0
    private var job: Job? = null
    lateinit var tv_see_all:TextView
    var isFav = false;
    var adsDetailModel: ProjectDetailResponse? = null

    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_project_detail)

        nestedScrollView = findViewById(R.id.nestedScrollView)
        adjustFontScale(resources.configuration)

        inits()
        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        if(intent.getStringExtra("propertyId")!=null){
            prop_id = intent.getStringExtra("propertyId")!!;
        }

        if (!prop_id.isNullOrEmpty()) {
            val map: HashMap<String, String> = HashMap()
            map["project_id"] = prop_id.toString()
            if (isLogged) {
                map["user_id"] = PreferencesService.instance.getUserData!!.ID.toString()
            }
            if (isNetworkAvailable()) {
                fetchViewCount(true,map)
                // hitGetApiWithoutTokenWithParams(Constants.VIEW_COUNT,true, Constants.VIEW_COUNT_API_NEW, map)
            } else {
                Utility.showToast(this, resources.getString(R.string.intenet_error))
            }
        }

        rl_report.setOnClickListener {
            if (isLogged == true){
                val intent = Intent(this, ReportAddActivity::class.java)
                intent.putExtra("post_id", prop_id.toString())
                startActivity(intent)
                overridePendingTransition(0,0)
            } else{
                loginTypeDialog(false)
            }

        }

        img_bookmark.setOnClickListener {
            if (isLogged == true){
                favUnfav()
            } else{
                loginTypeDialog(false)
            }

        }
        img_bookmarked.setOnClickListener {
            if (isLogged == true){
                favUnfav()
            } else{
                loginTypeDialog(false)
            }
        }


        adapterProjectProperties = AdapterProjectProperties(this, this, propertiesList)
        rv_recommended.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false))
        rv_recommended.isNestedScrollingEnabled = false
        nestedScrollView.isSmoothScrollingEnabled = true
        rv_recommended.setAdapter(adapterProjectProperties)

        img_back.setOnClickListener(View.OnClickListener {
            finish()
            //startActivity(Intent(this, HomeActivity::class.java))
        })
        tv_report.paint.isUnderlineText = true
        tv_viewMore.paint.isUnderlineText = true

        nestedScrollView.setOnScrollChangeListener(object : View.OnScrollChangeListener{
            override fun onScrollChange(
                v: View?, scrollX: Int, scrollY: Int,
                oldScrollX: Int, oldScrollY: Int,
            ) {
                if (scrollY == 0){
                    img_backNew.visibility = View.GONE
                    /* rl_back.background.setTint(
                         ContextCompat.getColor(
                         this@ProjectDetailActivity, R.color.skyBlue))*/
                } else{
                    img_backNew.visibility = View.VISIBLE
                    /*  rl_back.background.setTint(ContextCompat.getColor(
                          this@ProjectDetailActivity, R.color.red))*/
                }
            }
        })

        nestedScrollView.getViewTreeObserver().addOnScrollChangedListener {
            try {
                if(loading){
                    val view = nestedScrollView.getChildAt(nestedScrollView.size - 1) as View
                    val diff: Int = view.bottom - (nestedScrollView.getHeight() + nestedScrollView.getScrollY())
                    if (diff == 0) {
                        if (propertiesList.size > 0) {
                            if (isNetworkAvailable()){
                                loading = false
                                // getProperties(true)
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

    private fun inits(){
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
        rlVideoView = findViewById(R.id.rlVideoView)
        tvVideoCount = findViewById(R.id.tvVideoCount)
        rl_fvrt = findViewById(R.id.rl_fvrt)
        rlOwner = findViewById(R.id.rlOwner)
        imgCircle = findViewById(R.id.imgCircle)
        tvOwnerName = findViewById(R.id.tvOwnerName)
        rvAreas = findViewById(R.id.rvAreas)
        rvAreaPrices = findViewById(R.id.rvAreaPrices)
        rvBedrooms = findViewById(R.id.rvBedrooms)
        rvFloorPlans = findViewById(R.id.rvFloorPlans)

        tv_additional_ads.paint.isUnderlineText = true

        isLogged = PreferencesService.instance.userLoginStatus!!

        relative_main.visibility = View.GONE

        img_auto_scroll.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        rvAreas.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        adapterAreaItem = AdapterAreaItem(this, areaList, onAreaItemClickInterface = object  : AdapterAreaItem.AreaItemClickInterface{
            override fun onAreaItemClick(position: Int) {
                for (i in areaList) {
                    i.isSelected = false
                }
                areaList[position].isSelected = true
                adapterAreaItem.notifyDataSetChanged()

                updatePriceTable(position)
            }
        })
        rvAreas.adapter = adapterAreaItem


        rvAreaPrices.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        priceItemsAdapter = AdapterPriceItems(this,priceTableList)
        rvAreaPrices.adapter = priceItemsAdapter

        rvBedrooms.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        adapterFloorBedrooms = AdapterFloorBedrooms(this, bedroomsList, onAreaItemClickInterface = object  : AdapterFloorBedrooms.AreaItemClickInterface{
            override fun onAreaItemClick(position: Int) {
                for (i in bedroomsList) {
                    i.isSelected = false
                }
                bedroomsList[position].isSelected = true
                adapterFloorBedrooms.notifyDataSetChanged()

                updateFloorPlansList(position)
            }
        })

        rvBedrooms.adapter = adapterFloorBedrooms

        rvFloorPlans.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        adapterFloorPlans = AdapterFloorPlans(this, floorPlansList)
        rvFloorPlans.adapter = adapterFloorPlans
    }

    override fun onBackPressed() {

        val returnIntent = Intent()
        returnIntent.putExtra("id", prop_id)
        returnIntent.putExtra("isFav", isFav)
        setResult(RESULT_OK, returnIntent)
        super.onBackPressed()
    }

    @SuppressLint("UseKtx")
    fun favUnfav(){
        if(isNetworkAvailable()){
            val userData = PreferencesService.instance.getUserData
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", prop_id.toString())
            if (adsDetailModel != null) {
                adsDetailModel?.data?.isFav = adsDetailModel?.data?.isFav != true
            }
            if (img_bookmark.isVisible){
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

    @SuppressLint("NotifyDataSetChanged")
    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType == Constants.ADD_REMOVE_FAV_DETAIL){
            val model: NewFeatureModel = Gson().fromJson(respopnse,
                NewFeatureModel::class.java)
            isFav = !isFav

        }
    }

    private fun updateUI(model:ProjectDetailResponse){
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
        if (model.data != null){
            // responseList.add(model.result)

            val newViewCount = model.data?.totalViews ?: "0"
            tv_view_count.text = newViewCount
            isFav = model.data.isFav?:false


            img_bookmark.visibility = View.VISIBLE
            img_bookmarked.visibility = View.GONE
            rl_bottom.visibility = View.VISIBLE
            if (model.data.isFav == true){
                img_bookmark.visibility = View.GONE
                img_bookmarked.visibility = View.VISIBLE
            }

            try {
                if (!model.data.houzezGeoLocationLat.isNullOrEmpty()
                    &&!model.data.houzezGeoLocationLong.isNullOrEmpty()){
                    val lat = model.data.houzezGeoLocationLat.toDouble()
                    val lng = model.data.houzezGeoLocationLong.toDouble()
                    if (lat != 0.0 && lng != 0.0){
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


            rlOwner.visibility = View.GONE
            /*if (model?.result?.agent_agency_info != null){
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
            }*/

            rlVideoView.visibility = View.GONE
//            if (model.result != null && model.result?.property_meta != null
//                && !model.result.property_meta?.video.isNullOrEmpty()
//                && !model.result.property_meta?.video?.get(0).isNullOrEmpty()){
//                rlVideoView.visibility = View.VISIBLE
//            } else{
//                rlVideoView.visibility = View.GONE
//            }

            dt_date.text = Utility.getDateInFormat(model.data?.postDate ?:"")

            val land = "شقة"
            if (model.data?.propertyType?.contains(land) == true){
                ll_additional_details.visibility = View.GONE
            } else{
                ll_additional_details.visibility = View.VISIBLE
            }

            tv_ads_title_name.text = model.data?.postTitle ?: ""

            property_id.text = (model.data?.id?:0).toString()

            address =  "${model.data.propertyAddress?.propertyArea?:""} ${model.data.propertyAddress?.propertyCity?:""}"
            dt_address.setText(address)

            prop_type = model.data.propertyType ?:""

            prop_sub_type = ""
            /*if (model.result.property_meta != null){
                if (model.result.property_meta.fave_property_garage_size != null){
                    prop_sub_type = model.result.property_meta.fave_property_garage_size.get(0)
                } else{
                    prop_sub_type = ""
                }
            } else{
                prop_sub_type = ""
            }*/

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
            }
            dt_type.setText(prop_sub_type + " - " + prop_type)

            dt_price.text = "(${model.data.price?:"0"})${resources.getString(R.string.currency_code)}"

            dt_area.setText("${model.data.size?:"00"} ${resources.getString(R.string.m)}")

            val html = model.data.postContent ?:""
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                dt_details.setText(Html.fromHtml(html, 0))
            } else {
                dt_details.setText(Html.fromHtml(html))
            }


            dt_width.setText("${model.data.land ?: "0"} ${resources.getString(R.string.m)}")


            dt_rooms.setText(model.data.bedrooms?:"0")
            dt_floors.setText(model.data.floors?:"0")
            dt_bathroom.setText(model.data.bathrooms?:"0")

            if (!model.data.propertyAttr?.propertyStatus.isNullOrEmpty()) {
                tv_status.setText(model.data.propertyAttr?.propertyStatus ?:"")
            } else {
                tv_status.setVisibility(View.GONE)
            }

            contact_whatsapp.visibility = View.GONE
            /*if(model.result.agent_agency_info !=null && !model.result.agent_agency_info.equals("null")){
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
                }*/

            contact_call.visibility = View.GONE
            /*if (!model.result.agent_agency_info.call_number.isNullOrEmpty() &&
                !model.result.agent_agency_info.call_number.equals("null") ){
                contact_call.visibility = View.VISIBLE
                contact_call.setOnClickListener {
                    val intent = Intent(Intent.ACTION_DIAL)
                    intent.data = Uri.parse("tel:" + model.result.agent_agency_info.call_number)
                    startActivity(intent)
                }
            } else{
                contact_call.visibility = View.GONE
            }*/
        }else{
            rl_bottom.visibility = View.GONE
            contact_whatsapp.visibility = View.GONE
            contact_call.visibility = View.GONE
        }

        if(model.data?.isPremium == true){
            imv_premium.visibility = View.VISIBLE
        }else{
            imv_premium.visibility = View.GONE
        }

        if (!model.data?.link.isNullOrEmpty()) {
            imv_share.visibility = View.VISIBLE
            imv_share.setOnClickListener {
                val sharingIntent = Intent(Intent.ACTION_SEND)
                sharingIntent.type = "text/plain"
                sharingIntent.putExtra(Intent.EXTRA_TEXT, "${getString(R.string.take_look_at_property)}\n\n${model.data!!.link}")
                startActivity(Intent.createChooser(sharingIntent, ""))
            }
        } else {
            imv_share.visibility = View.INVISIBLE
        }

        if (!model.data?.galleryImages.isNullOrEmpty()) {
            for (i in model.data!!.galleryImages!!) {
                if (!i.url.isNullOrEmpty()) {
                    imageList.add(i.url)
                }
            }
        } else if (!model.data?.thumbnail.isNullOrEmpty()){
            imageList.add(model.data!!.thumbnail!!)
        }
        tv_image_count.text = imageList.size.toString()

        img_auto_scroll.adapter = AdapterAutoSliderDetailPage(this, imageList)
        setupIndicators(imageList.size)
        setCurrentIndicator(0)
        startAutoSlide()
        page = 0

        if (!model.data?.floorPlans.isNullOrEmpty()) {
            bedroomsList.clear()
            areaList.clear()

            for (i in model.data!!.floorPlans!!) {
                var isBedroomAdded = bedroomsList.any {
                    it.bedrooms == i.favePlanRooms
                }
                if (!isBedroomAdded) {
                    bedroomsList.add(ModelPropertyBedrooms(false,i.favePlanRooms))
                }

                var isAreaAdded = areaList.any {
                    it.bedrooms == i.favePlanSize
                }
                if (!isAreaAdded) {
                    areaList.add(ModelPropertyBedrooms(false,i.favePlanSize))
                }
            }

            if (bedroomsList.isNotEmpty()) {
                bedroomsList[0].isSelected = true
                adapterFloorBedrooms.notifyDataSetChanged()
                updateFloorPlansList(0)
            }
            if (areaList.isNotEmpty()) {
                areaList.sortBy { item ->
                    (item.bedrooms?:"").replace("[^0-9]".toRegex(), "").toInt()
                }
                areaList[0].isSelected = true
                adapterAreaItem.notifyDataSetChanged()
                updatePriceTable(0)
            }
        } else {
            rvFloorPlans.visibility = View.GONE
        }

        if (!model.data?.childProperties.isNullOrEmpty()) {
            propertiesList.clear()
            propertiesList.addAll(model.data!!.childProperties!!)
            adapterProjectProperties.notifyDataSetChanged()
        }
    }

    private fun updateFloorPlansList(position : Int) {
        floorPlansList.clear()
        if (!adsDetailModel?.data?.floorPlans.isNullOrEmpty()) {
            var filteredPlanList = adsDetailModel?.data?.floorPlans?.filter {
                it.favePlanRooms == bedroomsList[position].bedrooms
            }.orEmpty()

            floorPlansList.addAll(filteredPlanList as ArrayList<ProjectFloorPlan>)
        }
        adapterFloorPlans.notifyDataSetChanged()
    }

    private fun updatePriceTable(position : Int) {
        priceTableList.clear()
        if (!adsDetailModel?.data?.floorPlans.isNullOrEmpty()) {
            var filteredPlanList = adsDetailModel?.data?.floorPlans?.filter {
                it.favePlanSize == areaList[position].bedrooms
            }.orEmpty()

            for (i in filteredPlanList) {
                priceTableList.add(ModelPropertyPriceTable(i.favePlanPrice,"Down Payment"))
            }
        }
        priceItemsAdapter.notifyDataSetChanged()
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

    override fun openNextActivity(model: ChildProperty?, position: Int) {
        val intent = Intent(this, AdsDetailsActivity::class.java)
        intent.putExtra("propertyId",(model?.id ?: 0).toString())
        intent.putExtra("view_count", model?.totalViews ?: "0")
        intent.putExtra("myAd",false)
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun addRemoveFav(model: ChildProperty?, position: Int) {
        if(isNetworkAvailable()){
            val propId = model?.id ?: 0
            val userData = PreferencesService.instance.getUserData
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId.toString())
            if(propertiesList[position].isFav == true){
                propertiesList[position].isFav = false
            }else{
                propertiesList[position].isFav =true
            }
            adapterProjectProperties.notifyDataSetChanged()
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)
        }
    }

    override fun openLoginActivity() {
        loginTypeDialog(false)
        //startActivity(Intent(this, LoginActivity::class.java))
        //overridePendingTransition(0,0)
    }

    fun fetchViewCount(showLoader: Boolean, params: HashMap<String, String>) {
        println("Params : $params")
        lifecycleScope.launch {
            if (showLoader) {
                progressHUD?.dismiss()
                progressHUD = ProgressHud.show(this@ProjectDetailActivity, false, false)
            }

            try {
                val response = ApiClient.api?.getPropertyDetails(ApiClient.baseUrl + Constants.GET_PROJECT_DETAILS, params)

                if (response == null) {
                    showError(showLoader)
                    return@launch
                }

                if (response.isSuccessful && response.code() == 200) {
                    adsDetailModel = response.body()
                    adsDetailModel?.takeIf { it.success == "true"}?.let { model ->
                        runOnUiThread { updateUI(model) }
                    } ?: showError(showLoader)
                } else {
                    val errorMsg = try {
                        JSONObject(response.errorBody()?.string() ?: "")
                            .getString(Constants.MESSAGE)
                    } catch (_: Exception) {
                        null
                    }
                    showError(showLoader, errorMsg)
                }
            } catch (e: Exception) {
                Log.e("fetchViewCount", e.message.toString())
                showError(showLoader)
            } finally {
                if (showLoader) progressHUD?.dismiss()
            }
        }
    }

    private fun showError(showLoader: Boolean, message: String? = null) {
        runOnUiThread {
            if (showLoader) progressHUD?.dismiss()
            Toast.makeText(
                this,
                message ?: getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
        }
    }

}