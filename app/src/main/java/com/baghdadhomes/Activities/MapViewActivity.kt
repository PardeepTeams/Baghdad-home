package com.baghdadhomes.Activities

import android.app.Activity
import android.content.Intent
import android.location.Address
import android.location.Geocoder
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.CameraUpdateFactory
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.MapView
import com.google.android.gms.maps.OnMapReadyCallback
import com.google.android.gms.maps.model.*
import com.google.maps.android.data.kml.KmlLayer
import com.google.maps.android.data.kml.KmlPoint
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Utility
import org.json.JSONObject
import org.xmlpull.v1.XmlPullParserException
import java.io.IOException
import java.io.InputStream
import java.nio.charset.StandardCharsets
import java.util.*
import kotlin.math.max
import kotlin.math.min

class MapViewActivity : AppCompatActivity(), OnMapReadyCallback {
    private lateinit var tvCancel : TextView
    private lateinit var tvDone : TextView
    private lateinit var imgBack : ImageView
    private lateinit var mapView : MapView

    private var map: GoogleMap? = null
    private var location : String ?= null
    private var marker : Marker ?= null
    private var mKmlBounds: LatLngBounds? = null
    private var iraqCenter = LatLng((33.2232), (43.6793)) //center point of iraq
    private var initialLatLng : LatLng ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_map_view)

        inits()
        clickListeners()

        mapView.onCreate(savedInstanceState)
        mapView.getMapAsync(this)

        if (!intent.getStringExtra("city").isNullOrEmpty()){
            location = intent.getStringExtra("city").toString()
            if (!intent.getStringExtra("nbhd").isNullOrEmpty()){
                location = "${intent.getStringExtra("nbhd").toString()}, $location"
            }
        }

        val lat = intent.getDoubleExtra("lat", 0.0)
        val lng = intent.getDoubleExtra("lng", 0.0)
        if (lat != 0.0 && lng != 0.0){
            initialLatLng = LatLng(lat, lng)

            val markerOptions = MarkerOptions().position(initialLatLng!!)
                //.title(getLocationNameFromLatLng())
                .icon(Utility.getBitmapDescriptorFromDrawable(this,R.drawable.ic_location_red))

            Handler(Looper.getMainLooper()).postDelayed({
                marker = map?.addMarker(markerOptions)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng!!, 10.0f))
            }, 100)

        } else {
            if(location!=null && location!!.isNotEmpty()){
                getLocationFromAddress(location)
            }

        }

    }

    private fun inits(){
        tvCancel =  findViewById(R.id.tvCancel)
        tvDone =  findViewById(R.id.tvDone)
        imgBack =  findViewById(R.id.img_back)
        mapView =  findViewById(R.id.mapView)
    }

    private fun clickListeners(){
        tvCancel.setOnClickListener {
            finish()
        }

        imgBack.setOnClickListener {
            finish()
        }

        tvDone.setOnClickListener {
            println("Latitude:${marker?.position?.latitude} && Longitude:${marker?.position?.longitude}")
            val intent = Intent()
            intent.putExtra("lat", marker?.position?.latitude)
            intent.putExtra("lng", marker?.position?.longitude)
            setResult(Activity.RESULT_OK,intent)
            finish()
            overridePendingTransition(0,0)
        }
    }

    override fun onMapReady(googleMap: GoogleMap) {
        map = googleMap

        Handler(Looper.getMainLooper()).postDelayed({
            map!!.setOnCameraMoveListener {
                val pos: LatLng = map!!.cameraPosition.target
                if (marker != null) {
                    marker?.position = pos
                    if (mKmlBounds != null && !mKmlBounds!!.contains(pos)) {
                        // If the new position is outside the KML bounds, reset marker position
                        marker!!.position = iraqCenter//calculateClosestPoint(pos, mKmlBounds!!)
                        map?.moveCamera(CameraUpdateFactory.newLatLng(marker!!.position))
                    }
                }
            }
        },1000)

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

    private fun getLocationFromAddress(strAddress: String?) {
        val coder = Geocoder(this)
        val address: List<Address>?
        try {
            address = coder.getFromLocationName(strAddress!!, 5)
            if (address.isNullOrEmpty()) {
                Utility.showToast(this, getString(R.string.error_getting_location))
            }

            initialLatLng = if (!address.isNullOrEmpty()){
                val location = address[0]
                LatLng((location.latitude), (location.longitude))
            } else {
                iraqCenter
            }

            val markerOptions = MarkerOptions().position(initialLatLng!!)
                //.title(getLocationNameFromLatLng())
                .icon(Utility.getBitmapDescriptorFromDrawable(this,R.drawable.ic_location_red))

            Handler(Looper.getMainLooper()).postDelayed({
                marker = map?.addMarker(markerOptions)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(initialLatLng!!, 10.0f))
            }, 100)
        } catch (e: IOException) {
            e.printStackTrace()
            val markerOptions = MarkerOptions().position(initialLatLng!!)
                //.title(getLocationNameFromLatLng())
            .icon(Utility.getBitmapDescriptorFromDrawable(this, R.drawable.ic_location_red))
            Handler(Looper.getMainLooper()).postDelayed({
                marker = map?.addMarker(markerOptions)
                map?.animateCamera(CameraUpdateFactory.newLatLngZoom(iraqCenter, 10.0f))
            }, 100)
        }
    }

    private fun getLocationNameFromLatLng() : String{
        return try {
            val geo = Geocoder(this.applicationContext, Locale.getDefault())
            val addresses: List<Address> = geo.getFromLocation(marker?.position?.latitude!!, marker?.position?.longitude!!, 1) as List<Address>
            if (addresses.isNotEmpty()) {
                addresses[0].getAddressLine(0)
            } else {
                "Unable to find your Location"
            }
        } catch (e: Exception){
            e.message
            "Unable to find your Location"
        }
    }

    // Calculate KML bounds
    private fun calculateKmlBounds(kmlLayer: KmlLayer): LatLngBounds {
        val builder = LatLngBounds.Builder()
        val placeMark = kmlLayer.placemarks
        for (i in placeMark) {
            val geometry/*: KmlGeometry*/ = i.geometry
            if (geometry.geometryType.equals("Point")) {
                val point = geometry as KmlPoint
                builder.include(point.geometryObject)
            }
        }
        return builder.build()
    }

    // Calculate the closest point to the KML bounds from a given position
    private fun calculateClosestPoint(position: LatLng, bounds: LatLngBounds): LatLng {
        val lat = max(bounds.southwest.latitude, min(position.latitude, bounds.northeast.latitude))
        val lng = max(bounds.southwest.longitude, min(position.longitude, bounds.northeast.longitude))
        return LatLng(lat, lng)
    }

    private fun drawBoundaryFromKml() {
        try {
            val inputStream: InputStream = resources.openRawResource(R.raw.iraq_boundary)
            val kmlLayer = KmlLayer(map, inputStream, applicationContext)
            kmlLayer.addLayerToMap()

            if (kmlLayer.hasPlacemarks()) {
                val builder = LatLngBounds.Builder()
                val placeMark = kmlLayer.placemarks
                for (i in placeMark) {
                    val geometry = i.geometry
                    if (geometry.geometryType.equals("Point")) {
                        val point = geometry as KmlPoint
                        builder.include(point.geometryObject)
                    }
                }
                mKmlBounds = builder.build()
            }
        } catch (e : java.lang.Exception){
            println(e.localizedMessage)
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
            val latLngs: MutableList<LatLng> = ArrayList()
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
                .strokeColor(ContextCompat.getColor(this, R.color.skyBlue))
                .fillColor(ContextCompat.getColor(this, R.color.skyBlueLight))
            map!!.addPolygon(polygonOptions)

            // Zoom to the boundary
            val builder = LatLngBounds.Builder()
            for (latLng in latLngs) {
                builder.include(latLng)
            }
            mKmlBounds = builder.build()
        } catch (e : java.lang.Exception){
            println(e.localizedMessage)
        }

    }

}