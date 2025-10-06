package com.baghdadhomes.Activities

import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Adapters.PhotoAdapter
import com.baghdadhomes.Models.AdsDataChat
import com.baghdadhomes.Models.AdsDetailModel
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.material.tabs.TabLayout
import com.google.gson.Gson
import com.google.gson.JsonObject
import java.net.URLEncoder

class ImagesDetailsActivity : BaseActivity(), OnMapReadyCallback {
    lateinit var tabPhotos:TextView
    lateinit var tabFloorPlan:TextView
    lateinit var tabMap:TextView
    var adsDetailModel: AdsDetailModel? = null
    var propertyImages:ArrayList<String> = ArrayList()
    var foorsImages:ArrayList<String> = ArrayList()
    var selectedImages:ArrayList<String> = ArrayList()
    lateinit var adapter:PhotoAdapter
    lateinit var mapView: MapView
    var map: GoogleMap? = null
    lateinit var llLocation:LinearLayout
    lateinit var tvHeading:TextView

    lateinit var contact_call: RelativeLayout
    lateinit var rlChat: RelativeLayout
    lateinit var contact_whatsapp: RelativeLayout

    var type = "1"
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_images_details)
        llLocation = findViewById(R.id.llLocation)
        tabFloorPlan = findViewById(R.id.tabFloorPlan)
        tabPhotos = findViewById(R.id.tabPhotos)
        tabMap = findViewById(R.id.tabMap)
        mapView = findViewById(R.id.mapView)
        tvHeading = findViewById(R.id.tvHeading)
        contact_call = findViewById(R.id.contact_call)
        contact_whatsapp = findViewById(R.id.contact_whatsapp)
        rlChat = findViewById(R.id.rlChat)
        mapView.onCreate(savedInstanceState)

        mapView.getMapAsync(this)

        var imgBack:ImageView = findViewById(R.id.imgBack)

        imgBack.setOnClickListener {
            finish()
        }


        rlChat.setOnClickListener {
            if (PreferencesService.instance.userLoginStatus == true) {
                if (adsDetailModel!!.result?.agent_agency_info != null) {
                    val postData = AdsDataChat(
                        iD = adsDetailModel!!.result?.iD,
                        post_title = adsDetailModel!!.result?.post_title,
                        thumbnail = adsDetailModel!!.result?.thumbnail,
                        price = adsDetailModel!!.result?.price
                    )
                    val intent = Intent(this, MessagingActivity::class.java)
                    Constants.postDetails = postData
                    Constants.agencyModel = adsDetailModel!!.result?.agent_agency_info
                   // intent.putExtra("receiverModel", Gson().toJson(adsDetailModel!!.result?.agent_agency_info))
                  //  intent.putExtra("postData", Gson().toJson(postData))
                    startActivity(intent)
                }
            } else{
                loginTypeDialog(false)
                //startActivity(Intent(this,LoginActivity::class.java))
                //overridePendingTransition(0,0)
            }
        }



        if(intent.getStringExtra("model")!=null){
            adsDetailModel = Gson().fromJson(intent.getStringExtra("model"),AdsDetailModel::class.java)
        }
        if(Constants.adsDetailModel !=null){
            adsDetailModel = Constants.adsDetailModel
        }
        if(intent.getStringExtra("type")!=null){
            type = intent.getStringExtra("type")!!
        }
        val recyclerView: RecyclerView = findViewById(R.id.recyclerView)

        if (!adsDetailModel!!.result.agent_agency_info.call_number.isNullOrEmpty() &&
            !adsDetailModel!!.result.agent_agency_info.call_number.equals("null") ){
            contact_call.visibility = View.VISIBLE
            contact_call.setOnClickListener {
                val intent = Intent(Intent.ACTION_DIAL)
                intent.data = Uri.parse("tel:" + adsDetailModel!!.result.agent_agency_info.call_number)
                startActivity(intent)
            }

        } else{
            contact_call.visibility = View.GONE

        }



        if(adsDetailModel!!.result.agent_agency_info !=null && !adsDetailModel!!.result.agent_agency_info.equals("null")){
            if (!adsDetailModel!!.result.agent_agency_info.whatsapp_number.isNullOrEmpty()){
                contact_whatsapp.visibility = View.VISIBLE
                contact_whatsapp.setOnClickListener {
                    val intent = Intent(Intent.ACTION_VIEW)
                    val url = "https://api.whatsapp.com/send?phone="+
                            adsDetailModel!!.result.agent_agency_info.whatsapp_number+
                            "&text=" + URLEncoder.encode("", "UTF-8");
                    intent.data = Uri.parse(url)
                    startActivity(intent)
                }
            } else{
                contact_whatsapp.visibility = View.GONE
            }
        } else{
            contact_whatsapp.visibility = View.GONE
        }
        tabMap.setOnClickListener {
            tabMap.setTextColor(ContextCompat.getColor(this,R.color.skyBlue))
            tabFloorPlan.setTextColor(ContextCompat.getColor(this,R.color.blackNew))
            tabPhotos.setTextColor(ContextCompat.getColor(this,R.color.blackNew))
            recyclerView.visibility = View.GONE

            tvHeading.text  = tabMap.text.toString()
            try {
                if (!adsDetailModel!!.result.property_meta?.houzez_geolocation_lat.isNullOrEmpty()
                    &&!adsDetailModel!!.result.property_meta?.houzez_geolocation_long.isNullOrEmpty()
                    && !adsDetailModel!!.result.property_meta?.houzez_geolocation_lat?.get(0).isNullOrEmpty()
                    && !adsDetailModel!!.result.property_meta?.houzez_geolocation_long?.get(0).isNullOrEmpty()){
                    val lat = (adsDetailModel!!.result.property_meta?.houzez_geolocation_lat?.get(0) ?: "0.0").toDouble()
                    val lng = (adsDetailModel!!.result.property_meta?.houzez_geolocation_long?.get(0) ?: "0.0").toDouble()
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
        }

        tabFloorPlan.setOnClickListener {
            tvHeading.text  = tabFloorPlan.text.toString()
            llLocation.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            tabMap.setTextColor(ContextCompat.getColor(this,R.color.blackNew))
            tabFloorPlan.setTextColor(ContextCompat.getColor(this,R.color.skyBlue))
            tabPhotos.setTextColor(ContextCompat.getColor(this,R.color.blackNew))
            selectedImages.clear()
            if(adsDetailModel!=null && !adsDetailModel!!.result.floor_plans.isNullOrEmpty()){
                for(i in adsDetailModel!!.result.floor_plans!!){
                    selectedImages.add(i.fave_plan_image!!)
                }

            }
            adapter.notifyDataSetChanged()
        }

        tabPhotos.setOnClickListener {

            llLocation.visibility = View.GONE
            recyclerView.visibility = View.VISIBLE
            selectedImages.clear()
            tabMap.setTextColor(ContextCompat.getColor(this,R.color.blackNew))
            tabFloorPlan.setTextColor(ContextCompat.getColor(this,R.color.blackNew))
            tabPhotos.setTextColor(ContextCompat.getColor(this,R.color.skyBlue))
            if(adsDetailModel!=null){
                selectedImages.addAll(adsDetailModel!!.result.property_images)
            }
            adapter.notifyDataSetChanged()

            tvHeading.text  = tabPhotos.text.toString() + "("+ selectedImages.size.toString()+")"
        }

        adapter = PhotoAdapter(selectedImages)
        // RecyclerView setup
        recyclerView.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        recyclerView.adapter = adapter

        if(type.equals("1")){
            tabPhotos.performClick()
        }else if(type.equals("2")){
            tabFloorPlan.performClick()
        }else{
            tabMap.performClick()
        }

        // Buttons
        /*btnCall.setOnClickListener {
            val intent = Intent(Intent.ACTION_DIAL)
            intent.data = Uri.parse("tel:+1234567890")
            startActivity(intent)
        }

        btnWhatsapp.setOnClickListener {
            val url = "https://wa.me/1234567890"
            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse(url)
            startActivity(intent)
        }*/
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0
    }
}