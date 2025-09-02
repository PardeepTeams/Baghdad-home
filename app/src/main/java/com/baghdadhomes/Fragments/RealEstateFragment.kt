package com.baghdadhomes.Fragments

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.GoogleMap.OnMarkerClickListener
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.LatLng
import com.google.android.gms.maps.model.Marker
import com.google.android.gms.maps.model.MarkerOptions
import com.google.android.gms.maps.model.PolygonOptions
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Activities.CompanyAdsActivity
import com.baghdadhomes.Activities.FilterAgenciesActivity
import com.baghdadhomes.Adapters.AdapterAllCompanies
import com.baghdadhomes.Models.AgenciesData
import com.baghdadhomes.Models.AgenciesResponse
import com.baghdadhomes.Models.NBHDModel
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import de.hdodenhof.circleimageview.CircleImageView
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets


class RealEstateFragment : BaseFragment(), OnMapReadyCallback {
    private lateinit var imgFilter: ImageView
    private lateinit var imgClearSearch: ImageView
    private lateinit var etSerach: EditText
    private lateinit var rvServices: RecyclerView
    private lateinit var rlList: RelativeLayout
    private lateinit var rlMap: RelativeLayout
    private lateinit var imgList: ImageView
    private lateinit var imgMap: ImageView
    private lateinit var tvList: TextView
    private lateinit var tvMap: TextView
    private lateinit var viewList: View
    private lateinit var viewMap: View
    private lateinit var llListing: LinearLayout
    private lateinit var llMapView: RelativeLayout
    private lateinit var rlCompanyDetails: RelativeLayout
    private lateinit var mapView: MapView
    private lateinit var imgBig: ImageView
    private lateinit var imgCircle: CircleImageView
    private lateinit var tvName: TextView
    private lateinit var tvAddress: TextView
    private lateinit var tvAdsCount: TextView

    private lateinit var adapterServices: AdapterAllCompanies
    private val agenciesList: ArrayList<AgenciesData> = ArrayList()

    private var iraqCenter = LatLng((33.2232), (43.6793)) //center point of iraq
    private var map : GoogleMap ?=  null
    private var lastClickedMarker: Marker? = null

    private var nbhdModel : NBHDModel ?= null
    private var city : String ?= "all"
    private var nbhd : String ?= "all"

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?,
    ): View? {
        return inflater.inflate(R.layout.fragment_real_estate, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        inits(view)
        clickListeners()

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        rlList.performClick()

        if (isNetworkAvailable()){
            hitGetApiWithoutToken(Constants.AGENCIES, true, Constants.AGENCIES_API)
            hitGetApiWithoutToken(Constants.NEIGHBORHOOD, false, Constants.NEIGHBORHOOD_API)
        } else{
            showToast(requireContext(), resources.getString(R.string.intenet_error))
        }

    }

    private fun inits(view: View){
        imgFilter = view.findViewById(R.id.imgFilter)
        imgClearSearch = view.findViewById(R.id.img_clear_search)
        etSerach = view.findViewById(R.id.et_serach)
        rvServices = view.findViewById(R.id.rv_services)
        rlList = view.findViewById(R.id.rlList)
        rlMap = view.findViewById(R.id.rlMap)
        imgList = view.findViewById(R.id.imgList)
        imgMap = view.findViewById(R.id.imgMap)
        tvList = view.findViewById(R.id.tvList)
        tvMap = view.findViewById(R.id.tvMap)
        viewList = view.findViewById(R.id.viewList)
        viewMap = view.findViewById(R.id.viewMap)
        llListing = view.findViewById(R.id.llListing)
        llMapView = view.findViewById(R.id.llMapView)
        rlCompanyDetails = view.findViewById(R.id.rlCompanyDetails)
        mapView = view.findViewById(R.id.mapView)
        imgBig = view.findViewById(R.id.imgBig)
        imgCircle = view.findViewById(R.id.imgCircle)
        tvName = view.findViewById(R.id.tvName)
        tvAddress = view.findViewById(R.id.tvAddress)
        tvAdsCount = view.findViewById(R.id.tvAdsCount)

        rvServices.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        adapterServices = AdapterAllCompanies(requireContext(), agenciesList)
        rvServices.adapter = adapterServices
    }

    private fun clickListeners(){

        imgFilter.setOnClickListener {
            val intent = Intent(requireActivity(), FilterAgenciesActivity::class.java)
            intent.putExtra("nbhdModel", Gson().toJson(nbhdModel))
            intent.putExtra("city", city.orEmpty())
            intent.putExtra("nbhd", nbhd.orEmpty())
            launcher.launch(intent)
        }

        rlList.setOnClickListener {
            imgList.drawable.setTint(ContextCompat.getColor(requireContext(), R.color.skyBlue))
            tvList.setTextColor(ContextCompat.getColor(requireContext(), R.color.skyBlue))
            viewList.visibility = View.VISIBLE
            llListing.visibility = View.VISIBLE

            imgMap.drawable.setTint(ContextCompat.getColor(requireContext(), R.color.grey))
            tvMap.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            viewMap.visibility = View.GONE
            llMapView.visibility = View.GONE
        }

        rlMap.setOnClickListener {
            imgMap.drawable.setTint(ContextCompat.getColor(requireContext(), R.color.skyBlue))
            tvMap.setTextColor(ContextCompat.getColor(requireContext(), R.color.skyBlue))
            viewMap.visibility = View.VISIBLE
            llMapView.visibility = View.VISIBLE

            imgList.drawable.setTint(ContextCompat.getColor(requireContext(), R.color.grey))
            tvList.setTextColor(ContextCompat.getColor(requireContext(), R.color.grey))
            viewList.visibility = View.GONE
            llListing.visibility = View.GONE
        }

        rvServices.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                context!!.dismissKeyboard(v)
                return false
            }
        })

        imgClearSearch.setOnClickListener {
            etSerach.setText("")
            requireContext().dismissKeyboard(etSerach)
            updateMarkersOnMap(agenciesList)
        }

        etSerach.setOnEditorActionListener { _, actionId, _ ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                requireActivity().dismissKeyboard(etSerach)
                filter(etSerach.text.toString(), true)
                true
            }else{
                false
            }

        }
        etSerach.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etSerach.text.isNotEmpty()){
                    imgClearSearch.visibility = View.VISIBLE
                } else{
                    imgClearSearch.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable) {
                city = "all"
                nbhd = "all"
                filter(s.toString(), false)
            }
        })

    }

    fun filter(text: String, updateMap : Boolean){
        val filterList: ArrayList<AgenciesData> = ArrayList()
        for (i in agenciesList){
            if (i.display_name.orEmpty().contains(text, ignoreCase = true)){
                filterList.add(i)
            } else if (i.address.orEmpty().contains(text, ignoreCase = true)){
                filterList.add(i)
            }
        }
        adapterServices.updateList(filterList)
        if (updateMap){
            updateMarkersOnMap(filterList)
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType == Constants.AGENCIES){
            val model = Gson().fromJson(respopnse, AgenciesResponse::class.java)
            if (model.success == true){
                agenciesList.clear()
                if (!model.response.isNullOrEmpty()){
                    agenciesList.addAll(model.response)
                }
                adapterServices.notifyDataSetChanged()

                if (agenciesList.isNotEmpty()) {
                    updateMarkersOnMap(agenciesList)
                }
            }
        }

        if(apiType.equals(Constants.NEIGHBORHOOD)){
            val model = Gson().fromJson(respopnse, NBHDModel::class.java)
            if(model.success){
                nbhdModel = model
            }
        }

    }

    private fun updateMarkersOnMap(list: ArrayList<AgenciesData>) {
        map?.clear()
        addBoundryToMap()
        activity?.runOnUiThread {
            try {
                for (i in list){
                    if (!i.lat.isNullOrEmpty() && !i.lng.isNullOrEmpty()) {
                        val lat = (i.lat ?: "0.0").toDouble()
                        val lng = (i.lng ?: "0.0").toDouble()
                        if (lat != 0.0 && lng != 0.0) {
                            val pos = LatLng(lat, lng)
                            val markerOptions = MarkerOptions().position(pos)
                                .snippet(i.ID.orEmpty())
                                .title(i.display_name.orEmpty())
                                .icon(Utility.getBitmapDescriptorFromDrawable(requireContext(),R.drawable.ic_location_red))
                            map?.addMarker(markerOptions)
                            map?.animateCamera(CameraUpdateFactory.newLatLngZoom(pos, 10f))
                        }
                    }
                }
            } catch (e : java.lang.Exception){
                println(e.localizedMessage)
            }
        }
    }

    override fun onMapReady(p0: GoogleMap) {
        map = p0

        addBoundryToMap()

        map!!.setOnMarkerClickListener(object : OnMarkerClickListener{
            override fun onMarkerClick(marker: Marker): Boolean {
                // Deselect previous marker
                if (lastClickedMarker != null) {
                    // Reset previous marker appearance
                    lastClickedMarker!!.setIcon(Utility.getBitmapDescriptorFromDrawable(requireContext(),R.drawable.ic_location_red))
                }
                // Highlight clicked marker
                marker.setIcon(Utility.getBitmapDescriptorFromDrawable(requireContext(),R.drawable.ic_location_large))
                // Store the clicked marker
                lastClickedMarker = marker

                val snippetId = lastClickedMarker?.snippet.orEmpty()
                var agencyData : AgenciesData ?= null
                //Cycle through places array
                for (i in agenciesList) {
                    if (i.ID == snippetId) {
                        agencyData = i
                    }
                }

                if (agencyData != null){
                    showBottomDialog(agencyData)
                }
                map?.moveCamera(CameraUpdateFactory.newLatLng(lastClickedMarker?.position!!))
                return true
            }
        })
    }

    private fun addBoundryToMap(){
        map?.animateCamera(CameraUpdateFactory.newLatLngZoom(iraqCenter, 10F))

        Handler(Looper.getMainLooper()).postDelayed({
            try {
                //drawBoundaryFromKml()
                drawBoundaryFromGeoJSON()
            } catch (e: IOException) {
                e.printStackTrace()
                println("ErrorKML1:${e.localizedMessage}")
            } catch (e: XmlPullParserException) {
                e.printStackTrace()
                println("ErrorKML2:${e.localizedMessage}")
            }
        }, 100)
    }

    private fun showBottomDialog(agenciesData: AgenciesData){
        rlCompanyDetails.visibility = View.VISIBLE

        Glide.with(requireContext()).load(agenciesData.user_image.orEmpty())
            .placeholder(R.drawable.img_placeholder).into(imgBig)

        Glide.with(requireContext()).load(agenciesData.user_image.orEmpty())
            .placeholder(R.drawable.img_placeholder).into(imgCircle)

        tvName.text = agenciesData.display_name.orEmpty()
        tvAddress.text = agenciesData.address.orEmpty()
        tvAdsCount.text = "${getString(R.string.ads)}:(${agenciesData.total_posts ?: "0"} ${getString(R.string.real_estate)})"

        rlCompanyDetails.setOnClickListener {
            val intent = Intent(context, CompanyAdsActivity::class.java)
            intent.putExtra("agencyData", Gson().toJson(agenciesData))
            startActivity(intent)
        }
    }

    private fun drawBoundaryFromGeoJSON() {
        // Read GeoJSON file from assets
        try {
            val inputStream: InputStream = resources.openRawResource(R.raw.iraq_new)
            val size = inputStream.available()
            val buffer = ByteArray(size)
            inputStream.read(buffer)
            inputStream.close()
            val jsonString = String(buffer, StandardCharsets.UTF_8)

            // Parse GeoJSON
            val geoJson = JSONObject(jsonString)
            //val coordinates = geoJson.getJSONArray("coordinates").getJSONArray(0)
            val coordinates = (geoJson.getJSONArray("features").get(0) as JSONObject).getJSONObject("geometry").getJSONArray("coordinates").getJSONArray(0).getJSONArray(0)
            // Draw boundary polygon
            val latLngs: MutableList<LatLng> = java.util.ArrayList()
            for (i in 0 until coordinates.length()) {
                val coord = coordinates.getJSONArray(i)
                val lat = coord.getDouble(1)
                val lng = coord.getDouble(0)
                val latLng = LatLng(lat, lng)
                latLngs.add(latLng)
            }
            val polygonOptions = PolygonOptions()
                .addAll(latLngs)
                .strokeWidth(2f)
                .strokeColor(ContextCompat.getColor(requireContext(), R.color.skyBlue))
                .fillColor(ContextCompat.getColor(requireContext(), R.color.skyBlueLight))
            map!!.addPolygon(polygonOptions)

            // Zoom to the boundary
            /*val builder = LatLngBounds.Builder()
            for (latLng in latLngs) {
                builder.include(latLng)
            }
            mKmlBounds = builder.build()*/
        } catch (e : java.lang.Exception){
            println(e.localizedMessage)
        }

    }

    private val launcher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()){
        if (it.resultCode == Activity.RESULT_OK && it.data != null){
            if (!it.data?.getStringExtra("city").isNullOrEmpty()
                && !it.data?.getStringExtra("nbhd").isNullOrEmpty()){
                city = it.data?.getStringExtra("city")
                nbhd = it.data?.getStringExtra("nbhd")

                val filterList: ArrayList<AgenciesData> = ArrayList()
                for (i in agenciesList){
                    if (city == "all" && nbhd == "all"){
                        filterList.add(i)
                    } else if (city != "all" && nbhd == "all" && city == i.city){
                        filterList.add(i)
                    } else if (city != "all" && nbhd != "all" && city == i.city && nbhd == i.slug){
                        filterList.add(i)
                    }
                }
                adapterServices.updateList(filterList)
                updateMarkersOnMap(filterList)
            }
        }
    }

}