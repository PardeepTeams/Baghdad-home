package com.baghdadhomes.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.app.Activity
import android.app.Dialog
import android.content.ContentResolver
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Color
import android.graphics.ImageDecoder
import android.graphics.PorterDuff
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.os.Looper
import android.provider.MediaStore
import android.text.Editable
import android.text.Html
import android.text.SpannableString
import android.text.Spanned
import android.text.TextPaint
import android.text.TextWatcher
import android.text.method.LinkMovementMethod
import android.text.style.ClickableSpan
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.view.inputmethod.InputMethodManager
import android.webkit.MimeTypeMap
import android.widget.AdapterView
import android.widget.Button
import android.widget.CheckBox
import android.widget.EditText
import android.widget.HorizontalScrollView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RadioButton
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.net.toUri
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.abedelazizshe.lightcompressorlibrary.CompressionListener
import com.abedelazizshe.lightcompressorlibrary.VideoCompressor
import com.abedelazizshe.lightcompressorlibrary.VideoQuality
import com.abedelazizshe.lightcompressorlibrary.config.Configuration
import com.abedelazizshe.lightcompressorlibrary.config.SaveLocation
import com.abedelazizshe.lightcompressorlibrary.config.SharedStorageConfiguration
import com.baghdadhomes.Adapters.AdapterNBHDDialog
import com.baghdadhomes.Adapters.AdapterPropertyImages
import com.baghdadhomes.Adapters.AdapterPropertyImages.InterfaceSelectImage
import com.baghdadhomes.Adapters.AmenitiesAdapter
import com.baghdadhomes.Adapters.CommonBottomSheetSelectedAdapter
import com.baghdadhomes.Adapters.FrequencyAdapter
import com.baghdadhomes.Adapters.SpinnerCityAdapter
import com.baghdadhomes.Models.AmenityData
import com.baghdadhomes.Models.AmenityModel
import com.baghdadhomes.Models.FrequencyModel
import com.baghdadhomes.Models.ImageData
import com.baghdadhomes.Models.LoginModel
import com.baghdadhomes.Models.NBHDArea
import com.baghdadhomes.Models.NBHDDataResponse
import com.baghdadhomes.Models.NBHDModel
import com.baghdadhomes.Models.ResultFeatured
import com.baghdadhomes.Models.UploadVideoResponse
import com.baghdadhomes.Models.UploadimagResponse
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import com.baghdadhomes.fcm.ApiClient
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.gms.maps.model.LatLng
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.sangcomz.fishbun.FishBun
import com.sangcomz.fishbun.FishBun.Companion.INTENT_PATH
import com.sangcomz.fishbun.adapter.image.impl.GlideAdapter
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import pl.droidsonroids.gif.GifImageView
import java.io.ByteArrayInputStream
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileInputStream
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.Collections
import java.util.Date
import java.util.Locale
import java.util.Random
import kotlin.Array
import kotlin.Boolean
import kotlin.CharSequence
import kotlin.Comparator
import kotlin.Double
import kotlin.Exception
import kotlin.Float
import kotlin.Int
import kotlin.IntArray
import kotlin.Long
import kotlin.String
import kotlin.arrayOf
import kotlin.isInitialized
import kotlin.let
import kotlin.toString


class PostAdActivity : BaseActivity(), InterfaceSelectImage, AdapterNBHDDialog.onClick {
    private var isPermissionAsked = false
    private val PERMISSION_ALL = 1
    private val CAMERA_REQUEST = 1888
    private val PICK_IMAGE_REQUEST_CODE = 102
    private val VIDEO_PERMISSION_REQUEST_CODE = 103
    private val VIDEO_UPLOAD_CAMERA_CODE = 104
    private val VIDEO_UPLOAD_GALLERY_CODE = 105
    private val REQUEST_CODE_TRIM = 110
    lateinit var wp_country_code_id: CountryCodePicker
    lateinit var call_country_code_id:CountryCodePicker
    lateinit var image_property:ImageView
    var PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.READ_MEDIA_IMAGES,
            Manifest.permission.READ_MEDIA_VIDEO,
            Manifest.permission.CAMERA)
    } else{
        arrayOf(
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.CAMERA)
    }
    var mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
    var random = Random()

    lateinit var adapterPropertyImages: AdapterPropertyImages
    lateinit var rv_property_type: RecyclerView
    lateinit var rv_photo: RecyclerView
    var status: Int = 0
    lateinit var ll_nested: LinearLayout

    lateinit var tv_for_rent: TextView
    lateinit var tv_for_sale: TextView
    lateinit var tv_for_wanted: TextView
    lateinit var property_residence: TextView
    lateinit var property_commercial: TextView
    lateinit var property_land: TextView
    var path = ArrayList<Uri>()
    private val REQUEST_CODE = 123
    lateinit var spin_kit: SpinKitView
    lateinit var imageView_progress: GifImageView
    var images_count:Int = 0
    lateinit var adapterNBHD : SpinnerCityAdapter
    //lateinit var adapterCity : CitySpinnerAdapter
    var floors: String = ""
    lateinit var floor_1: TextView
    lateinit var floor_2: TextView
    lateinit var floor_3: TextView
    lateinit var floor_4: TextView
    lateinit var floor_5: TextView

    var rooms: String = ""
    lateinit var rooms_1: TextView
    lateinit var rooms_2: TextView
    lateinit var rooms_3: TextView
    lateinit var rooms_4: TextView
    lateinit var rooms_5: TextView

    var bathroom: String = ""
    lateinit var bathroom_1: TextView
    lateinit var bathroom_2: TextView
    lateinit var bathroom_3: TextView
    lateinit var bathroom_4: TextView
    lateinit var bathroom_5: TextView
    lateinit var tv_price_head: TextView

    var kitchen: String = ""
    lateinit var kitchen_1: TextView
    lateinit var kitchen_2: TextView
    lateinit var kitchen_3: TextView
    lateinit var kitchen_4: TextView
    lateinit var kitchen_5: TextView

    var livingRoom: String = ""
    lateinit var livingRoom_1: TextView
    lateinit var livingRoom_2: TextView
    lateinit var livingRoom_3: TextView
    lateinit var livingRoom_4: TextView
    lateinit var livingRoom_5: TextView

    var balcony: String = ""
    lateinit var balcony_1: TextView
    lateinit var balcony_2: TextView
    lateinit var balcony_3: TextView
    lateinit var balcony_4: TextView
    lateinit var balcony_5: TextView

    lateinit var et_street_type: EditText
    lateinit var et_monthlyPrice: EditText

    var furnishType = ""
    lateinit var radio_furnish_yes: RadioButton
    lateinit var radio_furnish_no: RadioButton
    lateinit var radio_furnish_half: RadioButton

    var currency_MS = ""
    lateinit var radioIQD_MS: RadioButton
    lateinit var radioUSD_MS: RadioButton

    var currency_SP = ""
    lateinit var radioIQD_SP: RadioButton
    lateinit var radioUSD_SP: RadioButton

    lateinit var checkTermsPrivacy: CheckBox
    lateinit var tvTerms: TextView

    /*lateinit var comm_shop: TextView
    lateinit var comm_office: TextView
    lateinit var comm_store: TextView
    lateinit var comm_building: TextView
    lateinit var comm_factory: TextView
    lateinit var comm_showroom: TextView
    lateinit var comm_other: TextView*/

    var propertyType: Int = 0
    var propertySubType: String = ""
    /*lateinit var land_residence: TextView
    lateinit var land_commercial: TextView
    lateinit var land_industry: TextView*/

    lateinit var et_width: EditText
    lateinit var et_addTitle: EditText
    lateinit var et_postDetail: EditText
    lateinit var et_postPrice: EditText
    lateinit var et_postArea: EditText
    lateinit var et_name: EditText
    lateinit var et_callNo: EditText
    lateinit var et_wpNo: EditText
    lateinit var button_addPost: Button
    lateinit var ll_counts: LinearLayout
    //lateinit var ll_land_type: LinearLayout
    //lateinit var commercial_type: HorizontalScrollView
    lateinit var spinner_jurisdriction: AppCompatSpinner
    lateinit var spinner_neighborhood: TextView
    lateinit var img_back: ImageView
    lateinit var el_main: RelativeLayout
    var selectedPosition = 0
    var cityList1: ArrayList<NBHDArea> = ArrayList()
    var nbhdList1: ArrayList<NBHDDataResponse> = ArrayList()
    var imagesList: ArrayList<ImageData> = ArrayList()
    var files: File? = null
    var userId: String = ""
    var name: String = ""
    var callnumber: String = ""
    var whatsappNumber: String = ""
    lateinit var resindential_type: HorizontalScrollView
    lateinit var commercial_type: HorizontalScrollView
    lateinit var land_type: HorizontalScrollView
    lateinit var intentModel : ResultFeatured

    lateinit var rl_whatsapp_No: RelativeLayout
    lateinit var rl_house_out: RelativeLayout
    lateinit var rl_apart_outter: RelativeLayout
    lateinit var rl_office_out: RelativeLayout
    lateinit var rl_shop: RelativeLayout
    lateinit var rl_agriculture_out: RelativeLayout
    lateinit var rl_commercail_outter: RelativeLayout
    lateinit var rl_apartment: RelativeLayout
    lateinit var rl_villa: RelativeLayout
    lateinit var rl_residence_other: RelativeLayout
    lateinit var rl_store: RelativeLayout
    lateinit var rl_building: RelativeLayout
    lateinit var rl_factory: RelativeLayout
    lateinit var rl_showroom_outter: RelativeLayout
    lateinit var rl_comm_other: RelativeLayout
    lateinit var rl_residnce_land: RelativeLayout
    lateinit var rl_industrial: RelativeLayout

    lateinit var img_house: ImageView
    lateinit var img_apart: ImageView
    lateinit var img_office: ImageView
    lateinit var img_shop: ImageView
    lateinit var img_agriculture: ImageView
    lateinit var img_commercail: ImageView
    lateinit var img_apartment: ImageView
    lateinit var img_villa: ImageView
    lateinit var img_residence_other: ImageView
    lateinit var img_store: ImageView
    lateinit var img_building: ImageView
    lateinit var img_factory: ImageView
    lateinit var img_showroom: ImageView
    lateinit var img_comm_other: ImageView
    lateinit var img_residnce_land: ImageView
    lateinit var img_industrial: ImageView

    lateinit var tv_house: TextView
    lateinit var tv_apart: TextView
    lateinit var tv_office: TextView
    lateinit var tv_shop: TextView
    lateinit var tv_agriculture: TextView
    lateinit var tv_commercail: TextView
    lateinit var tv_apartment: TextView
    lateinit var tv_villa: TextView
    lateinit var tv_residence_other: TextView
    lateinit var tv_store: TextView
    lateinit var tv_building: TextView
    lateinit var tv_factory: TextView
    lateinit var tv_showroom: TextView
    lateinit var tv_comm_other: TextView
    lateinit var tv_residnce_land: TextView
    lateinit var tv_industrial: TextView
    lateinit var nested_scroll: NestedScrollView
    lateinit var rl_main: RelativeLayout

    private val amenityList : ArrayList<AmenityData> = ArrayList()
    lateinit var adapterAmenities :AmenitiesAdapter
    lateinit var rvAmenities: RecyclerView
    lateinit var tv_see_more: TextView

    lateinit var tvOrientation: TextView
    lateinit var tvRealEstateSituation: TextView

    lateinit var rlVideo: RelativeLayout
    lateinit var imgAddVideo: ImageView
    lateinit var imgRemoveVideo: ImageView
    lateinit var imgPreviewVideo: ImageView
    lateinit var tvLocationOnMap: TextView
    lateinit var et_youtube_url: EditText

    lateinit var tv_heading: TextView

    var rentalFrequency = ""
    lateinit var llRentalFrequency: LinearLayout
    lateinit var rvRentalFrequency: RecyclerView

    private val frequencyList : ArrayList<FrequencyModel> = ArrayList()
    lateinit var adapterFrequency : FrequencyAdapter

    var isUpdate:Boolean = false

    var cityNameEnglish : String? = null
    var nbhdNameEnglish : String? = null
    var nbhd:String? = null
    var citySpinerItem:String? = null
    var callCountryCode = "+964"
    var wpCountryCode = "+964"
    var nbhdSlug:String ?= null
    var videoUrl : String = ""
    private val REQUEST_CODE_LOCATION = 10101
    private var propertyLatLng : LatLng?= null

    lateinit var nbhdAdapter: AdapterNBHDDialog
    lateinit var nbhdDialog: Dialog

    @SuppressLint("ClickableViewAccessibility")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_post_ad)
        adjustFontScale(resources.configuration)

        image_property = findViewById(R.id.image_property)

        rl_whatsapp_No = findViewById(R.id.rl_whatsapp_No)
        ll_nested = findViewById(R.id.ll_nested)
        call_country_code_id = findViewById(R.id.call_country_code)
        wp_country_code_id = findViewById(R.id.wp_country_code)
        rl_apartment = findViewById(R.id.rl_apartment)
        img_apartment = findViewById(R.id.img_apartment)
        tv_apartment = findViewById(R.id.tv_apartment)
        rl_villa = findViewById(R.id.rl_villa)
        img_villa = findViewById(R.id.img_villa)
        tv_villa = findViewById(R.id.tv_villa)
        rl_residence_other = findViewById(R.id.rl_residence_other)
        img_residence_other = findViewById(R.id.img_residence_other)
        tv_residence_other = findViewById(R.id.tv_residence_other)
        rl_store = findViewById(R.id.rl_store)
        img_store = findViewById(R.id.img_store)
        tv_store = findViewById(R.id.tv_store)
        rl_building = findViewById(R.id.rl_building)
        img_building = findViewById(R.id.img_building)
        tv_building = findViewById(R.id.tv_building)
        rl_factory = findViewById(R.id.rl_factory)
        img_factory = findViewById(R.id.img_factory)
        tv_factory = findViewById(R.id.tv_factory)
        rl_showroom_outter = findViewById(R.id.rl_showroom_outter)
        img_showroom = findViewById(R.id.img_showroom)
        tv_showroom = findViewById(R.id.tv_showroom)
        rl_comm_other = findViewById(R.id.rl_comm_other)
        img_comm_other = findViewById(R.id.img_comm_other)
        tv_comm_other = findViewById(R.id.tv_comm_other)
        rl_residnce_land = findViewById(R.id.rl_residnce_land)
        img_residnce_land = findViewById(R.id.img_residnce_land)
        tv_residnce_land = findViewById(R.id.tv_residnce_land)
        rl_industrial = findViewById(R.id.rl_industrial)
        img_industrial = findViewById(R.id.img_industrial)
        tv_industrial = findViewById(R.id.tv_industrial)
        tvLocationOnMap = findViewById(R.id.tvLocationOnMap)

        rl_main = findViewById(R.id.rl_main)
        nested_scroll = findViewById(R.id.nested_scroll)
        spin_kit = findViewById(R.id.spin_kit)
        imageView_progress = findViewById(R.id.imageView_progress)
        imageView_progress.setColorFilter(ContextCompat.getColor(this, R.color.blue), android.graphics.PorterDuff.Mode.SRC_ATOP)
        tv_heading = findViewById(R.id.tv_heading)
        tv_commercail = findViewById(R.id.tv_commercail)
        img_commercail = findViewById(R.id.img_commercail)
        rl_commercail_outter = findViewById(R.id.rl_commercail_outter)
        tv_agriculture = findViewById(R.id.tv_agriculture)
        img_agriculture = findViewById(R.id.img_agriculture)
        rl_agriculture_out = findViewById(R.id.rl_agriculture_out)
        tv_shop = findViewById(R.id.tv_shop)
        img_shop = findViewById(R.id.img_shop)
        rl_shop = findViewById(R.id.rl_shop)
        tv_office = findViewById(R.id.tv_office)
        img_office = findViewById(R.id.img_office)
        rl_office_out = findViewById(R.id.rl_office_out)
        resindential_type = findViewById(R.id.resindential_type)
        commercial_type = findViewById(R.id.commercial_type)
        land_type = findViewById(R.id.land_type)
        img_house = findViewById(R.id.img_house)
        tv_house = findViewById(R.id.tv_house)
        rl_apart_outter = findViewById(R.id.rl_apart_outter)
        img_apart = findViewById(R.id.img_apart)
        tv_apart = findViewById(R.id.tv_apart)
        rl_house_out = findViewById(R.id.rl_house_out)
        et_width = findViewById(R.id.et_width)
        tv_for_rent = findViewById(R.id.tv_for_rent)
        tv_for_sale = findViewById(R.id.tv_for_sale)
        tv_for_wanted = findViewById(R.id.tv_for_wanted)
        rv_property_type = findViewById(R.id.rv_property_type)
        rv_photo = findViewById(R.id.rv_photo)
        property_residence = findViewById(R.id.property_residence)
        property_commercial = findViewById(R.id.property_commercial)
        property_land = findViewById(R.id.property_land)
        spinner_jurisdriction = findViewById(R.id.spinner_jurisdriction)
        spinner_neighborhood = findViewById(R.id.spinner_neighborhood)
        img_back = findViewById(R.id.img_back)
        tv_price_head = findViewById(R.id.tv_price_head)
        ll_counts = findViewById(R.id.ll_counts)
        et_addTitle = findViewById(R.id.et_addTitle)
        et_postDetail = findViewById(R.id.et_postDetail)
        et_postPrice = findViewById(R.id.et_postPrice)
        et_postArea = findViewById(R.id.et_postArea)
        et_name = findViewById(R.id.et_name)
        et_callNo = findViewById(R.id.et_callNo)
        et_wpNo = findViewById(R.id.et_wpNo)
        el_main = findViewById(R.id.el_main)
        button_addPost = findViewById(R.id.button_addPost)

        floor_1 = findViewById(R.id.floor_1)
        floor_2 = findViewById(R.id.floor_2)
        floor_3 = findViewById(R.id.floor_3)
        floor_4 = findViewById(R.id.floor_4)
        floor_5 = findViewById(R.id.floor_5)
        rooms_1 = findViewById(R.id.rooms_1)
        rooms_2 = findViewById(R.id.rooms_2)
        rooms_3 = findViewById(R.id.rooms_3)
        rooms_4 = findViewById(R.id.rooms_4)
        rooms_5 = findViewById(R.id.rooms_5)
        bathroom_1 = findViewById(R.id.bathroom_1)
        bathroom_2 = findViewById(R.id.bathroom_2)
        bathroom_3 = findViewById(R.id.bathroom_3)
        bathroom_4 = findViewById(R.id.bathroom_4)
        bathroom_5 = findViewById(R.id.bathroom_5)
        kitchen_1 = findViewById(R.id.kitchen_1)
        kitchen_2 = findViewById(R.id.kitchen_2)
        kitchen_3 = findViewById(R.id.kitchen_3)
        kitchen_4 = findViewById(R.id.kitchen_4)
        kitchen_5 = findViewById(R.id.kitchen_5)
        livingRoom_1 = findViewById(R.id.livingRoom_1)
        livingRoom_2 = findViewById(R.id.livingRoom_2)
        livingRoom_3 = findViewById(R.id.livingRoom_3)
        livingRoom_4 = findViewById(R.id.livingRoom_4)
        livingRoom_5 = findViewById(R.id.livingRoom_5)
        balcony_1 = findViewById(R.id.balcony_1)
        balcony_2 = findViewById(R.id.balcony_2)
        balcony_3 = findViewById(R.id.balcony_3)
        balcony_4 = findViewById(R.id.balcony_4)
        balcony_5 = findViewById(R.id.balcony_5)
        et_street_type = findViewById(R.id.et_street_type)
        et_monthlyPrice = findViewById(R.id.et_monthlyPrice)
        radio_furnish_yes = findViewById(R.id.radio_furnish_yes)
        radio_furnish_no = findViewById(R.id.radio_furnish_no)
        radio_furnish_half = findViewById(R.id.radio_furnish_half)
        radioIQD_SP = findViewById(R.id.radioIQD_SP)
        radioUSD_SP = findViewById(R.id.radioUSD_SP)
        radioIQD_MS = findViewById(R.id.radioIQD_MS)
        radioUSD_MS = findViewById(R.id.radioUSD_MS)
        rvAmenities = findViewById(R.id.rvAmenities)
        tv_see_more = findViewById(R.id.tv_see_more)
        checkTermsPrivacy = findViewById(R.id.checkTermsPrivacy)
        tvTerms = findViewById(R.id.tvTerms)
        et_youtube_url = findViewById(R.id.et_youtube_url)

        tv_see_more.setOnClickListener {v ->
            if(tv_see_more.text.equals(getString(R.string.see_more))){
                tv_see_more.text = getString(R.string.see_less)
                tv_see_more.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_arrow_up_see, 0
                )
            }else{
                tv_see_more.text = getString(R.string.see_more)
                tv_see_more.setCompoundDrawablesWithIntrinsicBounds(
                    0, 0, R.drawable.ic_arrow_down, 0
                )
            /*    Handler(Looper.getMainLooper()).postDelayed({
                    val y = v.top + v.height + 2000
                    nested_scroll.smoothScrollTo(0, y)
                }, 200)*/

            }
            adapterAmenities.seeAll = !adapterAmenities.seeAll
            adapterAmenities.notifyDataSetChanged()

        }

        rlVideo = findViewById(R.id.rlVideo)
        imgAddVideo = findViewById(R.id.imgAddVideo)
        imgRemoveVideo = findViewById(R.id.imgRemoveVideo)
        imgPreviewVideo = findViewById(R.id.imgPreviewVideo)
        tvOrientation = findViewById(R.id.tvOrientation)
        tvRealEstateSituation = findViewById(R.id.tvRealEstateSituation)

        llRentalFrequency = findViewById(R.id.llRentalFrequency)
        rvRentalFrequency = findViewById(R.id.rvRentalFrequency)


        setupTermsAndPrivacyClick()
        switchRentOrSale()
        setPropertyType()
        setResindetalType()
        setCommercialType()
        setLandType()
        setFloors()
        setKitchen()
        setLivingRoom()
        setBalcony()
        setRooms()
        setBathroom()
        setFurnishType()
        setPriceCurrency()
        setMonthlyServiceCurrency()


        rvAmenities.layoutManager = GridLayoutManager(this,3)
        adapterAmenities = AmenitiesAdapter(this,false,amenityList,{position->
            amenityList[position].isSelected = !amenityList[position].isSelected
            adapterAmenities.notifyDataSetChanged()
        })
        rvAmenities.adapter = adapterAmenities

        frequencyList.add(FrequencyModel("Daily"))
        frequencyList.add(FrequencyModel("Weekly"))
        frequencyList.add(FrequencyModel("Monthly"))
        frequencyList.add(FrequencyModel("Yearly"))

        rvRentalFrequency.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        adapterFrequency = FrequencyAdapter(frequencyList,{position->
            for (i in frequencyList) {
                i.isSelected = false
            }
            rentalFrequency = frequencyList[position].name
            frequencyList[position].isSelected = true
            adapterFrequency.notifyDataSetChanged()
        })
        rvRentalFrequency.adapter = adapterFrequency

        tvOrientation.setOnClickListener {
            showOrientationBottomSheet()
        }

        tvRealEstateSituation.setOnClickListener {
            showRealEstateBottomSheet()
        }

        tvLocationOnMap.setOnClickListener {
            println("$cityNameEnglish && $nbhdNameEnglish")
            val intent = Intent(this, MapViewActivity::class.java)
            intent.putExtra("city", cityNameEnglish.orEmpty())
            intent.putExtra("nbhd", nbhdNameEnglish.orEmpty())
            if (propertyLatLng != null){
                intent.putExtra("lat", propertyLatLng?.latitude)
                intent.putExtra("lng", propertyLatLng?.longitude)
            }
            startActivityForResult(intent, REQUEST_CODE_LOCATION)
        }

        rlVideo.setOnClickListener {
            if (isNetworkAvailable()){
                openVideoChooser()
            } else{
                showToast(this,getString(R.string.intenet_error))
            }
        }

        imgRemoveVideo.setOnClickListener {
            videoUrl = ""
            imgAddVideo.visibility = View.VISIBLE
            imgRemoveVideo.visibility = View.GONE
            imgPreviewVideo.visibility = View.GONE
        }

        if (intent.getStringExtra("model")!=null){
            intentModel = Gson().fromJson(intent.getStringExtra("model"), ResultFeatured::class.java)
            isUpdate= intent.getBooleanExtra("isUpdate",false)!!
            setIntentData()
        }


        rv_property_type.visibility = View.GONE
        img_back.setOnClickListener { onBackPressed() }

        if (isNetworkAvailable()){
            hitGetApiWithoutToken(Constants.NEIGHBORHOOD, true, Constants.NEIGHBORHOOD_API)
            hitGetApiWithoutToken(Constants.AMINITY, false, Constants.AMINITY_API)
        } else{
            showToast(this, resources.getString(R.string.intenet_error))
        }

        el_main.setOnClickListener {
            dismissKeyboard(el_main)
            val imm = getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow( el_main.windowToken, 0)
            isPermissionAsked = false
            if (hasPermissions(*PERMISSIONS)) {
                if(isUpdate){
                    if(imagesList.size>0){
                        val count  = 15-imagesList.size
                        FishBun.with(this)
                            .setImageAdapter(GlideAdapter())
                            .setPickerCount(count)
                            .setPickerSpanCount(5)
                            .setActionBarColor(
                                Color.parseColor("#ffffff"),
                                Color.parseColor("#ffffff"),
                                true
                            )
                            .setActionBarTitleColor(Color.parseColor("#000000"))
                            .setAlbumSpanCount(1, 2)
                            .setButtonInAlbumActivity(false)
                            .setCamera(true)
                            .exceptGif(true)
                            .setReachLimitAutomaticClose(false)
                            .hasCameraInPickerPage(true)
                            .setMenuDoneText(resources.getString(R.string.upload_photos))
                            .setMenuTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                            .setHomeAsUpIndicatorDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_back_arrow
                                )
                            )
                            .setAllViewTitle(resources.getString(R.string.all_of_your_photo))
                            .setActionBarTitle(getString(R.string.app_name))
                            .textOnImagesSelectionLimitReached("You can't select any more.")
                            .textOnNothingSelected("I need a photo!")
                            .startAlbumWithOnActivityResult(REQUEST_CODE)
                    } else{
                        val count  = 15-imagesList.size
                        FishBun.with(this)
                            .setImageAdapter(GlideAdapter())
                            .setPickerCount(count)
                            .setPickerSpanCount(5)
                            .setActionBarColor(
                                Color.parseColor("#ffffff"),
                                Color.parseColor("#ffffff"),
                                true
                            )
                            .setActionBarTitleColor(Color.parseColor("#000000"))
                            .setAlbumSpanCount(1, 2)
                            .setButtonInAlbumActivity(false)
                            .setCamera(true)
                            .exceptGif(true)
                            .setReachLimitAutomaticClose(false)
                            .hasCameraInPickerPage(true)
                            .setMenuDoneText(resources.getString(R.string.upload_photos))
                            .setMenuTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                            .setHomeAsUpIndicatorDrawable(
                                ContextCompat.getDrawable(
                                    this,
                                    R.drawable.ic_back_arrow
                                )
                            )
                            .setAllViewTitle(resources.getString(R.string.all_of_your_photo))
                            .setActionBarTitle(getString(R.string.app_name))
                            .textOnImagesSelectionLimitReached("You can't select any more.")
                            .textOnNothingSelected("I need a photo!")
                            .startAlbumWithOnActivityResult(REQUEST_CODE)
                    }
                }else{
                    val count  = 15-imagesList.size
                    FishBun.with(this)
                        .setImageAdapter(GlideAdapter())
                        .setPickerCount(count)
                        .setPickerSpanCount(5)
                        .setActionBarColor(
                            Color.parseColor("#ffffff"),
                            Color.parseColor("#ffffff"),
                            true
                        )
                        .setActionBarTitleColor(Color.parseColor("#000000"))
                        .setAlbumSpanCount(1, 2)
                        .setButtonInAlbumActivity(false)
                        .setCamera(true)
                        .exceptGif(true)
                        .setReachLimitAutomaticClose(false)
                        .hasCameraInPickerPage(true)
                        .setMenuDoneText(resources.getString(R.string.upload_photos))
                        .setMenuTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        .setHomeAsUpIndicatorDrawable(
                            ContextCompat.getDrawable(
                                this,
                                R.drawable.ic_back_arrow
                            )
                        )
                        .setAllViewTitle(resources.getString(R.string.all_of_your_photo))
                        .setActionBarTitle(getString(R.string.app_name))
                        .textOnImagesSelectionLimitReached("You can't select any more.")
                        .textOnNothingSelected("I need a photo!")
                        .startAlbumWithOnActivityResult(REQUEST_CODE)
                }
            } else {
                ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
            }
        }

        et_wpNo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (et_wpNo.text.toString().isNotEmpty()
                        && et_wpNo.text.toString().substring(0,1) == "0"){
                        et_wpNo.setText(et_wpNo.text.toString().replace("0",""))
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        et_callNo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (et_callNo.text.toString().isNotEmpty()
                        && et_callNo.text.toString().substring(0,1) == "0"){
                        et_callNo.setText(et_callNo.text.toString().replace("0",""))
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        val loginInfo = PreferencesService.instance.getUserData
        if (loginInfo != null) {
            if (loginInfo.ID != null) {
                userId = loginInfo.ID.toString()
            }
            if (loginInfo.displayName != null) {
                name = loginInfo.displayName.toString()
                et_name.setText(name)
            }
            if (loginInfo.callNumber != null && !loginInfo.callNumber.equals("null")) {
                et_callNo.visibility = View.VISIBLE
                callnumber = loginInfo.callNumber.toString()
                callnumber = callnumber.replace(" ","")
                if(callnumber.length>10){
                    val addno =  (callnumber.length)-10
                    val callhghfgfg = callnumber.substring(addno,callnumber.length)
                    var countryCode = callnumber.substring(0,addno)
                    et_callNo.setText(callhghfgfg)
                    countryCode = countryCode.replace("+","")
                    call_country_code_id.setCountryForPhoneCode(countryCode.toInt())
                    callCountryCode ="+"+countryCode;
                }else{
                    et_callNo.setText(callnumber)
                }
            }else{
                // et_callNo.visibility = View.GONE
            }
            if (loginInfo.whatsappNumber != null && !loginInfo.whatsappNumber.equals("null")) {
                et_wpNo.visibility = View.VISIBLE
                whatsappNumber = loginInfo.whatsappNumber.toString()
                whatsappNumber = whatsappNumber.replace(" ","")
                //et_wpNo.setText(whatsappNumber)
                if(whatsappNumber.length>10){
                    val addno =  (whatsappNumber.length)-10
                    val callhghfgfg = whatsappNumber.substring(addno,whatsappNumber.length)
                    var countryCode = whatsappNumber.substring(0,addno)
                    countryCode = countryCode.replace("+","")
                    et_wpNo.setText(callhghfgfg)
                    wp_country_code_id.setCountryForPhoneCode(countryCode.toInt())
                    wpCountryCode ="+"+countryCode;
                }else{
                    et_wpNo.setText(whatsappNumber)
                }
            }else{
                // et_wpNo.visibility = View.GONE
            }
        }

        adapterPropertyImages = AdapterPropertyImages(this, imagesList, this)

        rv_photo.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        rv_photo.setAdapter(adapterPropertyImages)

        et_callNo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (et_callNo.text.toString().isNotEmpty()
                        && et_callNo.text.toString().substring(0,1) == "0"){
                        et_callNo.setText(et_callNo.text.toString().replace("0",""))
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        et_wpNo.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (et_wpNo.text.toString().isNotEmpty()
                        && et_wpNo.text.toString().substring(0,1) == "0"){
                        et_wpNo.setText(et_wpNo.text.toString().replace("0",""))
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        button_addPost.setOnClickListener {
            dismissKeyboard(button_addPost)
            if (spinner_neighborhood.text.isEmpty() || spinner_neighborhood.text.equals(getString(R.string.choose_the_neighborhood))){
                spinner_neighborhood.error = resources.getString(R.string.choose_the_neighborhood)
                showToast(this, resources.getString(R.string.choose_the_neighborhood))
                scrollNestedScrollViewToRequiredTarget(spinner_neighborhood)
            } else if (tvLocationOnMap.text.isEmpty() || tvLocationOnMap.text.equals(getString(R.string.location_on_map))){
                tvLocationOnMap.error = getString(R.string.location_on_map)
                showToast(this, resources.getString(R.string.location_on_map))
                scrollNestedScrollViewToRequiredTarget(tvLocationOnMap)
            } else  if (et_addTitle.text.isEmpty()){
                et_addTitle.error = resources.getString(R.string.ad_title_required)
                showToast(this, resources.getString(R.string.ad_title_required))
                scrollNestedScrollViewToRequiredTarget(et_addTitle)
            }  else if (et_postPrice.text.isEmpty()){
                et_postPrice.error = getString(R.string.enter_price)
                showToast(this, resources.getString(R.string.enter_price))
                scrollNestedScrollViewToRequiredTarget(et_postPrice)
            } else if (imagesList.isEmpty()){
                showToast(this, resources.getString(R.string.upload_photo))
              //  scrollNestedScrollViewToRequiredTarget(rv_photo)
                scrollNestedScrollViewToRequiredTarget(et_postPrice)
            } else if (et_youtube_url.text.isNotEmpty() && Utility.isYouTubeVideoUrl(et_youtube_url.text.toString())){
                et_youtube_url.error = getString(R.string.enter_valid_youtube_url)
                showToast(this, resources.getString(R.string.enter_valid_youtube_url))
               // scrollNestedScrollViewToRequiredTarget(et_youtube_url)
                scrollNestedScrollViewToRequiredTarget(et_youtube_url)
            } else if (et_name.text.isEmpty()){
                et_name.error = getString(R.string.enter_name)
                showToast(this, resources.getString(R.string.enter_name))
                scrollNestedScrollViewToRequiredTarget(et_name)
                    //    scrollToView(nested_scroll, et_name)
            } else if (!checkTermsPrivacy.isChecked){
                showToast(this, resources.getString(R.string.accept_terms_privacy))
              //  scrollNestedScrollViewToRequiredTarget(checkTermsPrivacy)
              //  scrollToView(nested_scroll, checkTermsPrivacy)
            } else{
                if(isUpdate){
                    updatePost()
                } else {
                    addPost()
                }
            }

        }

        ll_nested.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return true
            }
        })

        spinner_jurisdriction.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return false
            }
        })

        spinner_neighborhood.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return false
            }
        })

        if (PreferencesService.instance.getLoginMethod() != "whatsapp"){
            rl_whatsapp_No.visibility = View.VISIBLE
            wp_country_code_id.visibility = View.VISIBLE
            et_wpNo.visibility = View.VISIBLE
        } else{
            rl_whatsapp_No.visibility = View.GONE
            wp_country_code_id.visibility = View.GONE
            et_wpNo.visibility = View.GONE
        }


        /*spinner_jurisdriction.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>, view: View?,
                position: Int, id: Long) {
                val item = adapterView.getItemAtPosition(position)
                cityList1 = nbhdList1.get(position).area as ArrayList<NBHDArea>
                adapterCity = CitySpinnerAdapter(this@PostAdActivity, cityList1)
                spinner_neighborhood.adapter = adapterCity

                Handler().postDelayed(Runnable {
                    var selectedPosition  = 0
                    for (i in 0..cityList1.size-1){
                        if(nbhd!=null){
                            if(cityList1.get(i).name.equals(nbhd)){
                                selectedPosition = i
                            }
                        }

                    }
                    spinner_neighborhood.setSelection(selectedPosition)
                },100)
            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })*/

        spinner_jurisdriction.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>, view: View?,
                position: Int, id: Long
            ) {
                val item = adapterView.getItemAtPosition(position)
                cityNameEnglish = nbhdList1.get(position).description.orEmpty()
                cityList1 = nbhdList1.get(position).area as ArrayList<NBHDArea>
                //selectedNBHDList.clear()
                /*try {
                    selectedNBHDAdapter!!.notifyDataSetChanged()
                }catch (e:Exception){
                    e.toString()
                }*/
                var language = PreferencesService.instance.getLanguage()
                Handler(Looper.getMainLooper()).postDelayed({
                    for (i in cityList1){
                        if (nbhd != null){
                            if (i.name.equals(nbhd)){
                                nbhdSlug = i.slug
                                nbhdNameEnglish = i.description.orEmpty()
                                if (language == "ar"){
                                    spinner_neighborhood.text = i.name
                                } else{
                                    spinner_neighborhood.text = i.description
                                }
                            }
                        }
                    }
                }, 100)

            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })

        spinner_neighborhood.setOnClickListener {
            dismissKeyboard(spinner_neighborhood)
            openNeighborhoodDialog()
        }

        nbhdAdapter = AdapterNBHDDialog(this, cityList1, this)
    }

    fun scrollNestedScrollViewToRequiredTarget(targetView : View) {
        nested_scroll.smoothScrollTo(0, targetView.top)
    }

    fun openNeighborhoodDialog(){
        nbhdDialog= Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar)
        nbhdDialog.setContentView(R.layout.layout_neighborhood_dialog)
        nbhdDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        nbhdDialog.setCancelable(true)
        nbhdDialog.setCanceledOnTouchOutside(true)
        nbhdDialog.show()

        val rl_outter: RelativeLayout = nbhdDialog.findViewById(R.id.rl_outter)
        val rv_nbhd: RecyclerView = nbhdDialog.findViewById(R.id.rv_nbhd)
        val cv_cardView: CardView = nbhdDialog.findViewById(R.id.cv_cardView)
        val et_serach: EditText = nbhdDialog.findViewById(R.id.et_serach)
        val img_clear_search: ImageView = nbhdDialog.findViewById(R.id.img_clear_search)

        cv_cardView.visibility = View.VISIBLE

        rv_nbhd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        nbhdAdapter = AdapterNBHDDialog(this, cityList1, this)
        rv_nbhd.adapter = nbhdAdapter
        nbhdAdapter.notifyDataSetChanged()

        et_serach.setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                dismissKeyboard(et_serach)
                if (et_serach.text.isNotEmpty()){
                    filter(et_serach.text.toString())
                    nbhdAdapter.notifyDataSetChanged()
                }
                true
            } else{
                false
            }
        }

        et_serach.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
                nbhdAdapter.notifyDataSetChanged()
                if (et_serach.text.isNotEmpty()){
                    img_clear_search.visibility = View.VISIBLE
                } else{
                    img_clear_search.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        rl_outter.setOnClickListener {
            nbhdDialog.dismiss()
        }
    }

    fun filter(text: String){
        val filterList: ArrayList<NBHDArea> = ArrayList()
        for (data: NBHDArea in cityList1){
            if (data.name.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))){
                filterList.add(data)
            } else if (data.description.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))){
                filterList.add(data)
            }
        }
        nbhdAdapter.updateList(filterList)
        nbhdAdapter.notifyDataSetChanged()
    }

    fun switchRentOrSale() {
        llRentalFrequency.visibility = View.GONE
        status = 29
        tv_for_rent.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
        tv_for_sale.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_blue_new))
        tv_for_wanted.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
        tv_for_rent.setTextColor(resources.getColor(R.color.grey))
        tv_for_sale.setTextColor(resources.getColor(R.color.whiteNew))
        tv_for_wanted.setTextColor(resources.getColor(R.color.grey))
        tv_price_head.setText(resources.getString(R.string.selling_price))
        tv_for_sale.setOnClickListener {
            dismissKeyboard(tv_for_sale)
            llRentalFrequency.visibility = View.GONE
            status = 29
            tv_for_rent.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
            tv_for_sale.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_blue_new))
            tv_for_wanted.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
            tv_for_rent.setTextColor(resources.getColor(R.color.grey))
            tv_for_sale.setTextColor(resources.getColor(R.color.whiteNew))
            tv_for_wanted.setTextColor(resources.getColor(R.color.grey))
            tv_price_head.setText(resources.getString(R.string.selling_price))
        }

        tv_for_rent.setOnClickListener {
            dismissKeyboard(tv_for_rent)
            llRentalFrequency.visibility = View.GONE
            status = 28
            tv_for_rent.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_blue_new))
            tv_for_sale.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
            tv_for_wanted.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
            tv_for_rent.setTextColor(resources.getColor(R.color.whiteNew))
            tv_for_sale.setTextColor(resources.getColor(R.color.grey))
            tv_for_wanted.setTextColor(resources.getColor(R.color.grey))
            tv_price_head.setText(resources.getString(R.string.rent_price))
        }

        tv_for_wanted.setOnClickListener {
            dismissKeyboard(tv_for_rent)
            llRentalFrequency.visibility = View.GONE
            status = 28
            tv_for_rent.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
            tv_for_sale.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
            tv_for_wanted.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_blue_new))
            tv_for_rent.setTextColor(resources.getColor(R.color.grey))
            tv_for_sale.setTextColor(resources.getColor(R.color.grey))
            tv_for_wanted.setTextColor(resources.getColor(R.color.whiteNew))
            tv_price_head.setText(resources.getString(R.string.rent_price))
        }
    }

    private fun setPropertyType() {
        propertyType = 55
        property_residence.background = getDrawable(R.drawable.bg_outline_blue_new)
        property_commercial.background = getDrawable(R.drawable.bg_outline_solid)
        property_land.background = getDrawable(R.drawable.bg_outline_solid)
        property_residence.setTextColor(ContextCompat.getColor(this, R.color.whiteNew))
        property_commercial.setTextColor(ContextCompat.getColor(this, R.color.grey))
        property_land.setTextColor(ContextCompat.getColor(this, R.color.grey))
        ll_counts.visibility = View.VISIBLE
        resindential_type.visibility = View.VISIBLE
        commercial_type.visibility = View.GONE
        land_type.visibility = View.GONE

        property_residence.setOnClickListener {
            dismissKeyboard(property_residence)
            propertyType = 55
            propertySubType = "house"
            property_residence.background = getDrawable(R.drawable.bg_outline_blue_new)
            property_commercial.background = getDrawable(R.drawable.bg_outline_solid)
            property_land.background = getDrawable(R.drawable.bg_outline_solid)
            property_residence.setTextColor(ContextCompat.getColor(applicationContext, R.color.whiteNew))
            property_commercial.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.grey
                )
            )
            property_land.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            ll_counts.visibility = View.VISIBLE
            resindential_type.visibility = View.VISIBLE
            commercial_type.visibility = View.GONE
            land_type.visibility = View.GONE

        }
        property_commercial.setOnClickListener {
            dismissKeyboard(property_commercial)
            propertyType = 25
            propertySubType = "office"
            property_residence.background = getDrawable(R.drawable.bg_outline_solid)
            property_commercial.background = getDrawable(R.drawable.bg_outline_blue_new)
            property_land.background = getDrawable(R.drawable.bg_outline_solid)
            property_residence.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.grey
                )
            )
            property_commercial.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.whiteNew
                )
            )
            property_land.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            ll_counts.visibility = View.VISIBLE
            resindential_type.visibility = View.GONE
            commercial_type.visibility = View.VISIBLE
            land_type.visibility = View.GONE

        }
        property_land.setOnClickListener {
            dismissKeyboard(property_land)
            propertyType = 73
            propertySubType = "agriculture"
            floors = ""
            rooms = ""
            bathroom = ""
            property_residence.background = getDrawable(R.drawable.bg_outline_solid)
            property_commercial.background = getDrawable(R.drawable.bg_outline_solid)
            property_land.background = getDrawable(R.drawable.bg_outline_blue_new)
            property_residence.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.grey
                )
            )
            property_commercial.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.grey
                )
            )
            property_land.setTextColor(
                ContextCompat.getColor(
                    applicationContext,
                    R.color.whiteNew
                )
            )
            ll_counts.visibility = View.GONE
            resindential_type.visibility = View.GONE
            commercial_type.visibility = View.GONE
            land_type.visibility = View.VISIBLE

        }
    }

    fun setResindetalType(){
        propertySubType = "house"
        rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
        rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
        img_house.setColorFilter(img_house.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
        img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        tv_house.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
        tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))

        rl_house_out.setOnClickListener {
            dismissKeyboard(rl_house_out)
            propertySubType = "house"
            rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_house.setColorFilter(img_house.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_house.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_apart_outter.setOnClickListener {
            dismissKeyboard(rl_apart_outter)
            propertySubType = "apart_house"
            rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_house.setColorFilter(img_house.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_house.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_apart.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_apartment.setOnClickListener {
            dismissKeyboard(rl_apartment)
            propertySubType = "apartment"
            rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_house.setColorFilter(img_house.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_house.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_villa.setOnClickListener {
            dismissKeyboard(rl_villa)
            propertySubType = "villa"
            rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_villa.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_house.setColorFilter(img_house.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_house.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_villa.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_residence_other.setOnClickListener {
            dismissKeyboard(rl_residence_other)
            propertySubType = "residence_other"
            rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_blue)
            img_house.setColorFilter(img_house.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            tv_house.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
        }
    }

    fun setCommercialType(){
        propertySubType = "office"
        rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
        rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
        img_office.setColorFilter(img_office.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
        img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        tv_office.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
        tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))

        rl_office_out.setOnClickListener {
            dismissKeyboard(rl_office_out)
            propertySubType = "office"
            rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_office.setColorFilter(img_office.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_office.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_shop.setOnClickListener {
            dismissKeyboard(rl_shop)
            propertySubType = "shop"
            rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_shop.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_shop.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_store.setOnClickListener {
            dismissKeyboard(rl_store)
            propertySubType = "store"
            rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_store.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_store.setColorFilter(img_store.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_store.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_building.setOnClickListener {
            dismissKeyboard(rl_building)
            propertySubType = "building"
            rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_building.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_building.setColorFilter(img_building.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_building.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_factory.setOnClickListener {
            dismissKeyboard(rl_factory)
            propertySubType = "factory"
            rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_factory.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_factory.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_showroom_outter.setOnClickListener {
            dismissKeyboard(rl_showroom_outter)
            propertySubType = "showroom"
            rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_comm_other.setOnClickListener {
            dismissKeyboard(rl_comm_other)
            propertySubType = "commercial_other"
            rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_blue)
            img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
        }

    }

    fun setLandType(){
        propertySubType = "agriculture"
        rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
        rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_solid)
        rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_solid)
        img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
        img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
        tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
        tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.grey))

        rl_agriculture_out.setOnClickListener {
            dismissKeyboard(rl_agriculture_out)
            propertySubType = "agriculture"
            rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_commercail_outter.setOnClickListener {
            dismissKeyboard(rl_commercail_outter)
            propertySubType = "commercial"
            rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_residnce_land.setOnClickListener {
            dismissKeyboard(rl_residnce_land)
            propertySubType = "residencial"
            rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_blue)
            rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_solid)
            img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }

        rl_industrial.setOnClickListener {
            dismissKeyboard(rl_industrial)
            propertySubType = "industrial"
            rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_solid)
            rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_blue)
            img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
            img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
            tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
        }
    }

    private fun setFloors() {
        floors = "1"
        floor_1.background = getDrawable(R.drawable.bg_number_outline)
        floor_2.background = getDrawable(R.drawable.bg_outline_square)
        floor_3.background = getDrawable(R.drawable.bg_outline_square)
        floor_4.background = getDrawable(R.drawable.bg_outline_square)
        floor_5.background = getDrawable(R.drawable.bg_outline_square)
        floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        floor_1.setOnClickListener {
            dismissKeyboard(floor_1)
            floors = "1"
            floor_1.background = getDrawable(R.drawable.bg_number_outline)
            floor_2.background = getDrawable(R.drawable.bg_outline_square)
            floor_3.background = getDrawable(R.drawable.bg_outline_square)
            floor_4.background = getDrawable(R.drawable.bg_outline_square)
            floor_5.background = getDrawable(R.drawable.bg_outline_square)
            floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        floor_2.setOnClickListener {
            dismissKeyboard(floor_2)
            floors = "2"
            floor_1.background = getDrawable(R.drawable.bg_outline_square)
            floor_2.background = getDrawable(R.drawable.bg_number_outline)
            floor_3.background = getDrawable(R.drawable.bg_outline_square)
            floor_4.background = getDrawable(R.drawable.bg_outline_square)
            floor_5.background = getDrawable(R.drawable.bg_outline_square)
            floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }

        floor_3.setOnClickListener {
            dismissKeyboard(floor_3)
            floors = "3"
            floor_1.background = getDrawable(R.drawable.bg_outline_square)
            floor_2.background = getDrawable(R.drawable.bg_outline_square)
            floor_3.background = getDrawable(R.drawable.bg_number_outline)
            floor_4.background = getDrawable(R.drawable.bg_outline_square)
            floor_5.background = getDrawable(R.drawable.bg_outline_square)
            floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        floor_4.setOnClickListener {
            dismissKeyboard(floor_4)
            floors = "4"
            floor_1.background = getDrawable(R.drawable.bg_outline_square)
            floor_2.background = getDrawable(R.drawable.bg_outline_square)
            floor_3.background = getDrawable(R.drawable.bg_outline_square)
            floor_4.background = getDrawable(R.drawable.bg_number_outline)
            floor_5.background = getDrawable(R.drawable.bg_outline_square)
            floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        floor_5.setOnClickListener {
            dismissKeyboard(floor_5)
            floors = "5"
            floor_1.background = getDrawable(R.drawable.bg_outline_square)
            floor_2.background = getDrawable(R.drawable.bg_outline_square)
            floor_3.background = getDrawable(R.drawable.bg_outline_square)
            floor_4.background = getDrawable(R.drawable.bg_outline_square)
            floor_5.background = getDrawable(R.drawable.bg_number_outline)
            floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        }
    }

    private fun setKitchen() {
        kitchen = "1"
        kitchen_1.background = getDrawable(R.drawable.bg_number_outline)
        kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
        kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
        kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
        kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
        kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        kitchen_1.setOnClickListener {
            dismissKeyboard(floor_1)
            kitchen = "1"
            kitchen_1.background = getDrawable(R.drawable.bg_number_outline)
            kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        kitchen_2.setOnClickListener {
            dismissKeyboard(floor_2)
            kitchen = "2"
            kitchen_1.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_2.background = getDrawable(R.drawable.bg_number_outline)
            kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }

        kitchen_3.setOnClickListener {
            dismissKeyboard(floor_3)
            kitchen = "3"
            kitchen_1.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_3.background = getDrawable(R.drawable.bg_number_outline)
            kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        kitchen_4.setOnClickListener {
            dismissKeyboard(floor_4)
            kitchen = "4"
            kitchen_1.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_4.background = getDrawable(R.drawable.bg_number_outline)
            kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        kitchen_5.setOnClickListener {
            dismissKeyboard(floor_5)
            kitchen = "5"
            kitchen_1.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
            kitchen_5.background = getDrawable(R.drawable.bg_number_outline)
            kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        }
    }

    private fun setLivingRoom() {
        livingRoom = "1"
        livingRoom_1.background = getDrawable(R.drawable.bg_number_outline)
        livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
        livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
        livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
        livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
        livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        livingRoom_1.setOnClickListener {
            dismissKeyboard(floor_1)
            livingRoom = "1"
            livingRoom_1.background = getDrawable(R.drawable.bg_number_outline)
            livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        livingRoom_2.setOnClickListener {
            dismissKeyboard(floor_2)
            livingRoom = "2"
            livingRoom_1.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_2.background = getDrawable(R.drawable.bg_number_outline)
            livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }

        livingRoom_3.setOnClickListener {
            dismissKeyboard(floor_3)
            livingRoom = "3"
            livingRoom_1.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_3.background = getDrawable(R.drawable.bg_number_outline)
            livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        livingRoom_4.setOnClickListener {
            dismissKeyboard(floor_4)
            livingRoom = "4"
            livingRoom_1.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_4.background = getDrawable(R.drawable.bg_number_outline)
            livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        livingRoom_5.setOnClickListener {
            dismissKeyboard(floor_5)
            livingRoom = "5"
            livingRoom_1.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
            livingRoom_5.background = getDrawable(R.drawable.bg_number_outline)
            livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        }
    }

    private fun setBalcony() {
        balcony = "1"
        balcony_1.background = getDrawable(R.drawable.bg_number_outline)
        balcony_2.background = getDrawable(R.drawable.bg_outline_square)
        balcony_3.background = getDrawable(R.drawable.bg_outline_square)
        balcony_4.background = getDrawable(R.drawable.bg_outline_square)
        balcony_5.background = getDrawable(R.drawable.bg_outline_square)
        balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        balcony_1.setOnClickListener {
            dismissKeyboard(floor_1)
            balcony = "1"
            balcony_1.background = getDrawable(R.drawable.bg_number_outline)
            balcony_2.background = getDrawable(R.drawable.bg_outline_square)
            balcony_3.background = getDrawable(R.drawable.bg_outline_square)
            balcony_4.background = getDrawable(R.drawable.bg_outline_square)
            balcony_5.background = getDrawable(R.drawable.bg_outline_square)
            balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        balcony_2.setOnClickListener {
            dismissKeyboard(floor_2)
            balcony = "2"
            balcony_1.background = getDrawable(R.drawable.bg_outline_square)
            balcony_2.background = getDrawable(R.drawable.bg_number_outline)
            balcony_3.background = getDrawable(R.drawable.bg_outline_square)
            balcony_4.background = getDrawable(R.drawable.bg_outline_square)
            balcony_5.background = getDrawable(R.drawable.bg_outline_square)
            balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }

        balcony_3.setOnClickListener {
            dismissKeyboard(floor_3)
            balcony = "3"
            balcony_1.background = getDrawable(R.drawable.bg_outline_square)
            balcony_2.background = getDrawable(R.drawable.bg_outline_square)
            balcony_3.background = getDrawable(R.drawable.bg_number_outline)
            balcony_4.background = getDrawable(R.drawable.bg_outline_square)
            balcony_5.background = getDrawable(R.drawable.bg_outline_square)
            balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        balcony_4.setOnClickListener {
            dismissKeyboard(floor_4)
            balcony = "4"
            balcony_1.background = getDrawable(R.drawable.bg_outline_square)
            balcony_2.background = getDrawable(R.drawable.bg_outline_square)
            balcony_3.background = getDrawable(R.drawable.bg_outline_square)
            balcony_4.background = getDrawable(R.drawable.bg_number_outline)
            balcony_5.background = getDrawable(R.drawable.bg_outline_square)
            balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        balcony_5.setOnClickListener {
            dismissKeyboard(floor_5)
            balcony = "5"
            balcony_1.background = getDrawable(R.drawable.bg_outline_square)
            balcony_2.background = getDrawable(R.drawable.bg_outline_square)
            balcony_3.background = getDrawable(R.drawable.bg_outline_square)
            balcony_4.background = getDrawable(R.drawable.bg_outline_square)
            balcony_5.background = getDrawable(R.drawable.bg_number_outline)
            balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        }
    }

    private fun setRooms() {
        rooms = "1"
        rooms_1.background = getDrawable(R.drawable.bg_number_outline)
        rooms_2.background = getDrawable(R.drawable.bg_outline_square)
        rooms_3.background = getDrawable(R.drawable.bg_outline_square)
        rooms_4.background = getDrawable(R.drawable.bg_outline_square)
        rooms_5.background = getDrawable(R.drawable.bg_outline_square)
        rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        rooms_1.setOnClickListener {
            dismissKeyboard(rooms_1)
            rooms = "1"
            rooms_1.background = getDrawable(R.drawable.bg_number_outline)
            rooms_2.background = getDrawable(R.drawable.bg_outline_square)
            rooms_3.background = getDrawable(R.drawable.bg_outline_square)
            rooms_4.background = getDrawable(R.drawable.bg_outline_square)
            rooms_5.background = getDrawable(R.drawable.bg_outline_square)
            rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        rooms_2.setOnClickListener {
            dismissKeyboard(rooms_2)
            rooms = "2"
            rooms_1.background = getDrawable(R.drawable.bg_outline_square)
            rooms_2.background = getDrawable(R.drawable.bg_number_outline)
            rooms_3.background = getDrawable(R.drawable.bg_outline_square)
            rooms_4.background = getDrawable(R.drawable.bg_outline_square)
            rooms_5.background = getDrawable(R.drawable.bg_outline_square)
            rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        rooms_3.setOnClickListener {
            dismissKeyboard(rooms_3)
            rooms = "3"
            rooms_1.background = getDrawable(R.drawable.bg_outline_square)
            rooms_2.background = getDrawable(R.drawable.bg_outline_square)
            rooms_3.background = getDrawable(R.drawable.bg_number_outline)
            rooms_4.background = getDrawable(R.drawable.bg_outline_square)
            rooms_5.background = getDrawable(R.drawable.bg_outline_square)
            rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        rooms_4.setOnClickListener {
            dismissKeyboard(rooms_4)
            rooms = "4"
            rooms_1.background = getDrawable(R.drawable.bg_outline_square)
            rooms_2.background = getDrawable(R.drawable.bg_outline_square)
            rooms_3.background = getDrawable(R.drawable.bg_outline_square)
            rooms_4.background = getDrawable(R.drawable.bg_number_outline)
            rooms_5.background = getDrawable(R.drawable.bg_outline_square)
            rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        rooms_5.setOnClickListener {
            dismissKeyboard(rooms_5)
            rooms = "5"
            rooms_1.background = getDrawable(R.drawable.bg_outline_square)
            rooms_2.background = getDrawable(R.drawable.bg_outline_square)
            rooms_3.background = getDrawable(R.drawable.bg_outline_square)
            rooms_4.background = getDrawable(R.drawable.bg_outline_square)
            rooms_5.background = getDrawable(R.drawable.bg_number_outline)
            rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        }
    }

    private fun setBathroom() {
        bathroom = "1"
        bathroom_1.background = getDrawable(R.drawable.bg_number_outline)
        bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
        bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
        bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
        bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
        bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        bathroom_1.setOnClickListener {
            dismissKeyboard(bathroom_1)
            bathroom = "1"
            bathroom_1.background = getDrawable(R.drawable.bg_number_outline)
            bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        bathroom_2.setOnClickListener {
            dismissKeyboard(bathroom_2)
            bathroom = "2"
            bathroom_1.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_2.background = getDrawable(R.drawable.bg_number_outline)
            bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        bathroom_3.setOnClickListener {
            dismissKeyboard(bathroom_3)
            bathroom = "3"
            bathroom_1.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_3.background = getDrawable(R.drawable.bg_number_outline)
            bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        bathroom_4.setOnClickListener {
            dismissKeyboard(bathroom_4)
            bathroom = "4"
            bathroom_1.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_4.background = getDrawable(R.drawable.bg_number_outline)
            bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }
        bathroom_5.setOnClickListener {
            dismissKeyboard(bathroom_5)
            bathroom = "5"
            bathroom_1.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
            bathroom_5.background = getDrawable(R.drawable.bg_number_outline)
            bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
        }
    }

    private fun setFurnishType() {
        furnishType = "Yes"
        radio_furnish_yes.isChecked = true
        radio_furnish_yes.setOnCheckedChangeListener { it,it1->
            if (it.isChecked) {
                furnishType = "yes"
            }
        }
        radio_furnish_no.setOnCheckedChangeListener { it,it1->
            if (it.isChecked) {
                furnishType = "No"
            }
        }
        radio_furnish_half.setOnCheckedChangeListener { it,it1->
            if (it.isChecked) {
                furnishType = "Half Furnished"
            }
        }
    }

    private fun setPriceCurrency() {
        currency_SP = "IQD"
        radioIQD_SP.isChecked = true
        radioIQD_SP.setOnCheckedChangeListener { it,it1->
            if (it.isChecked) {
                currency_SP = "IQD"
            }
        }
        radioUSD_SP.setOnCheckedChangeListener { it,it1->
            if (it.isChecked) {
                currency_SP = "USD"
            }
        }
    }

    private fun setMonthlyServiceCurrency() {
        currency_MS = "IQD"
        radioIQD_MS.isChecked = true
        radioIQD_MS.setOnCheckedChangeListener { it,it1->
            if (it.isChecked) {
                currency_MS = "IQD"
            }
        }
        radioUSD_MS.setOnCheckedChangeListener { it,it1->
            if (it.isChecked) {
                currency_MS = "USD"
            }
        }
    }


    private fun openGallery() {
        val photoPickerIntent = Intent()
        photoPickerIntent.type = "image/*"
        photoPickerIntent.action = Intent.ACTION_GET_CONTENT
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE)
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    override fun onRequestPermissionsResult(requestCode: Int, permissions: Array<String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ALL) {
            if (!isPermissionAsked) {
                isPermissionAsked = true
                if (hasPermissions(*permissions)) {
                    if(isUpdate){
                        if(imagesList.size>0){
                            val count  = 15-imagesList.size
                            FishBun.with(this)
                                .setImageAdapter(GlideAdapter())
                                .setPickerCount(count)
                                .setPickerSpanCount(5)
                                .setSelectedImages(path)
                                .setActionBarColor(
                                    Color.parseColor("#ffffff"),
                                    Color.parseColor("#ffffff"),
                                    true
                                )
                                .setActionBarTitleColor(Color.parseColor("#000000"))
                                .setAlbumSpanCount(1, 2)
                                .setButtonInAlbumActivity(false)
                                .setCamera(true)
                                .exceptGif(true)
                                .setReachLimitAutomaticClose(false)
                                .hasCameraInPickerPage(true)
                                .setMenuDoneText(resources.getString(R.string.upload_photos))
                                .setMenuTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                                .setHomeAsUpIndicatorDrawable(
                                    ContextCompat.getDrawable(
                                        this,
                                        R.drawable.ic_back_arrow
                                    )
                                )
                                .setAllViewTitle(resources.getString(R.string.all_of_your_photo))
                                .setActionBarTitle(getString(R.string.app_name))
                                .textOnImagesSelectionLimitReached("You can't select any more.")
                                .textOnNothingSelected("I need a photo!")
                                .startAlbumWithOnActivityResult(REQUEST_CODE)
                        } else{
                            val count  = 15-imagesList.size
                            FishBun.with(this)
                                .setImageAdapter(GlideAdapter())
                                .setPickerCount(count)
                                .setPickerSpanCount(5)
                                .setSelectedImages(path)
                                .setActionBarColor(
                                    Color.parseColor("#ffffff"),
                                    Color.parseColor("#ffffff"),
                                    true
                                )
                                .setActionBarTitleColor(Color.parseColor("#000000"))
                                .setAlbumSpanCount(1, 2)
                                .setButtonInAlbumActivity(false)
                                .setCamera(true)
                                .exceptGif(true)
                                .setReachLimitAutomaticClose(false)
                                .hasCameraInPickerPage(true)
                                .setMenuDoneText(resources.getString(R.string.upload_photos))
                                .setMenuTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                                .setHomeAsUpIndicatorDrawable(
                                    ContextCompat.getDrawable(
                                        this,
                                        R.drawable.ic_back_arrow
                                    )
                                )
                                .setAllViewTitle(resources.getString(R.string.all_of_your_photo))
                                .setActionBarTitle(getString(R.string.app_name))
                                .textOnImagesSelectionLimitReached("You can't select any more.")
                                .textOnNothingSelected("I need a photo!")
                                .startAlbumWithOnActivityResult(REQUEST_CODE)
                        }
                    }else{
                        val count  = 15-imagesList.size
                        FishBun.with(this)
                            .setImageAdapter(GlideAdapter())
                            .setPickerCount(count)
                            .setPickerSpanCount(5)
                            .setActionBarColor(
                                Color.parseColor("#ffffff"),
                                Color.parseColor("#ffffff"),
                                true
                            )
                            .setActionBarTitleColor(Color.parseColor("#000000"))
                            .setAlbumSpanCount(1, 2)
                            .setButtonInAlbumActivity(false)
                            .setCamera(true)
                            .exceptGif(true)
                            .setReachLimitAutomaticClose(false)
                            .hasCameraInPickerPage(true)
                            .setMenuDoneText(resources.getString(R.string.upload_photos))
                            .setMenuTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                            .setMenuTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                            .setHomeAsUpIndicatorDrawable(ContextCompat.getDrawable(this, R.drawable.ic_back_arrow))
                            .setAllViewTitle(resources.getString(R.string.all_of_your_photo))
                            .setActionBarTitle(getString(R.string.app_name))
                            .textOnImagesSelectionLimitReached("You can't select any more.")
                            .textOnNothingSelected("I need a photo!")
                            .startAlbumWithOnActivityResult(REQUEST_CODE)
                    }
                }
                return
            }
        }

        if (requestCode == VIDEO_PERMISSION_REQUEST_CODE && grantResults[0] == PackageManager.PERMISSION_GRANTED){
            openVideoChooser()
        }
    }

    override fun onSelectImage(position: Int) {
        imagesList.removeAt(position)
        adapterPropertyImages.notifyDataSetChanged()
        el_main.visibility = View.VISIBLE
    }

    override fun onPointerCaptureChanged(hasCapture: Boolean) {
        super.onPointerCaptureChanged(hasCapture)
    }


    fun updatePost() {
        /*for (i in cityList1) {
            if (spinner_neighborhood.selectedItem == i) {
                nbhd = i.slug.toString()
            }
        }*/

        var city: String = ""
        for (i in nbhdList1) {
            if (spinner_jurisdriction.selectedItem == i) {
                city = i.slug.toString()
            }
        }

        val map: HashMap<String, String> = HashMap()
        map.put("action", "update_property")
        map.put("user_id", userId)
        map.put("prop_id",intentModel.iD.toString())
        map.put("prop_title", et_addTitle.text.toString().trim())
        map.put("prop_des", et_postDetail.text.toString().trim())
        map.put("prop_type[]", propertyType.toString())
        map.put("prop_status[]", status.toString())
        map.put("prop_price", et_postPrice.text.toString().trim())
        //map.put("prop_label", )
        //map.put("prop_price_prefix", )
        //map.put("prop_sec_price", )
        map.put("currency", currency_SP)
        //map.put("prop_video_url", "")
        map.put("prop_beds", rooms)
        map.put("prop_baths", bathroom)
        map.put("prop_size", et_postArea.text.toString().trim())
        map.put("prop_size_prefix", "sqft")
        map.put("prop_land_area", et_width.text.toString().trim())
        map.put("prop_land_area_prefix", "sqft")
        map.put("prop_garage", floors)
        //map.put("prop_features[]",)
        //map.put("property_map_address",)
        //map.put("country",)
        map.put("video", videoUrl)
        map.put("youtube_url", et_youtube_url.text.toString().trim())
        map.put("locality", city)
        //map.put("neighborhood", nbhdSlug!!)
        if (nbhdSlug != null && nbhdSlug!!.isNotEmpty()){
            map.put("neighborhood", nbhdSlug.toString())
        }
        //map.put("postal_code",)
        if (propertyLatLng != null) {
            map.put("lat",propertyLatLng?.latitude.toString())
            map.put("lng",propertyLatLng?.longitude.toString())
        }
        map.put("prop_garage_size", propertySubType)
        map.put("user_submit_has_no_membership", "no")
        val imageStringId:ArrayList<String> = ArrayList()
        for(i in imagesList){
            imageStringId.add(i.id)
            // map.put("propperty_image_ids[]", i.id)
        }

        map.put("living_room", livingRoom)
        map.put("kitchen", kitchen)
        map.put("balconies", balcony)
        map.put("floor_number", floors)
        map.put("monthly_price", et_monthlyPrice.text.toString())
        map.put("currency_monthly", currency_MS)
        map.put("street_type", et_street_type.text.toString())
        map.put("furnished", furnishType)
        if (status == 28 && rentalFrequency.isNotEmpty()) {
            // map.put("rental_frequency", rentalFrequency)
        }
        if (!tvOrientation.text.equals(getString(R.string.select))) {
            map.put("orientation", tvOrientation.text.toString())
        }
        if (!tvRealEstateSituation.text.equals(getString(R.string.select))) {
            map.put("real_estate_situation", tvRealEstateSituation.text.toString())
        }
        val selectedAmenities:ArrayList<String> = ArrayList()
        for(i in amenityList){
            if (i.isSelected == true) {
                selectedAmenities.add(i.id?:"")
            }
        }

        //map.put("featured_image_id", "")
        //map.put("fave_agent_display_option", "")

        /*call_country_code_id.setOnCountryChangeListener {
            callCountryCode = "+"+ it.phoneCode
        }*/

        /*wp_country_code_id.setOnCountryChangeListener {
            wpCountryCode = "+" + it.phoneCode
        }*/
        call_country_code_id.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                callCountryCode =  call_country_code_id.getSelectedCountryCodeWithPlus()
                (call_country_code_id.getSelectedCountryCodeWithPlus())
            }

        })

        wp_country_code_id.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                wpCountryCode =  wp_country_code_id.getSelectedCountryCodeWithPlus()
                (wp_country_code_id.getSelectedCountryCodeWithPlus())
            }

        })
        val profileMap: HashMap<String, String> = HashMap()
        profileMap.put("user_id", userId)
        profileMap.put("display_name", et_name.text.toString().trim())
        if (et_callNo.text.isNotEmpty()){
            profileMap.put("call_number", callCountryCode+et_callNo.text.toString().trim())
        }
        if (et_wpNo.text.isNotEmpty()){
            profileMap.put("whatsapp_number", wpCountryCode+et_wpNo.text.toString().trim())
        }
        if (isNetworkAvailable()){
            ApiClient.api!!.hitPostApiWithouTokenFieldParams(
                ApiClient.baseUrl + Constants.PROFILE_UPDATE_API,profileMap)
            hitAddPostApiWithoutTokenParams(Constants.UPDATE_ADD, true, Constants.UPDATE_POST_URL, map,imageStringId,selectedAmenities)
        } else{
            showToast(this, resources.getString(R.string.intenet_error))
        }

    }

    fun addPost() {
        /*var nbhd: String = ""
        for (i in cityList1) {
            if (spinner_neighborhood.selectedItem == i) {
                nbhd = i.slug.toString()
            }
        }*/

        var city: String = ""
        for (i in nbhdList1) {
            if (spinner_jurisdriction.selectedItem == i) {
                city = i.slug.toString()
            }
        }

        //modelIntent = Gson().fromJson(intent.getStringExtra("model"), ResultFeatured::class.java)

        //Log.d("seelctNBHD", loginInfo!!.ID!!)
        name = et_name.text.toString()
        callnumber = et_callNo.text.toString()
        whatsappNumber = et_wpNo.text.toString()

        val map: HashMap<String, String> = HashMap()
        map.put("action", "add_property")
        map.put("user_id", userId)
        map.put("prop_title", et_addTitle.text.toString().trim())
        map.put("prop_des", et_postDetail.text.toString().trim())
        map.put("prop_type[]", propertyType.toString())
        map.put("prop_status[]", status.toString())
        map.put("prop_price", et_postPrice.text.toString().trim())
        //map.put("prop_label", )
        //map.put("prop_price_prefix", )
        //map.put("prop_sec_price", )
        map.put("currency", currency_SP)
        //map.put("prop_video_url", "")
        map.put("prop_beds", rooms)
        map.put("prop_baths", bathroom)
        map.put("prop_size", et_postArea.text.toString().trim())
        map.put("prop_size_prefix", "sqft")
        map.put("prop_land_area", et_width.text.toString().trim())
        map.put("prop_land_area_prefix", "sqft")
        map.put("prop_garage", floors)
        //map.put("prop_features[]",)
        //map.put("property_map_address",)
        //map.put("country",)
        map.put("locality", city)
        map.put("video", videoUrl)
        map.put("youtube_url", et_youtube_url.text.toString().trim())
        if (nbhdSlug != null && nbhdSlug!!.isNotEmpty()){
            map.put("neighborhood", nbhdSlug.toString())
        }
        //map.put("postal_code",)
        if (propertyLatLng != null) {
            map.put("lat", propertyLatLng?.latitude.toString())
            map.put("lng", propertyLatLng?.longitude.toString())
        }
        map.put("prop_garage_size", propertySubType)
        map.put("user_submit_has_no_membership", "no")
        val imageStringId:ArrayList<String> = ArrayList()
        for(i in imagesList){
            imageStringId.add(i.id)
            // map.put("propperty_image_ids[]", i.id)
        }
        map.put("living_room", livingRoom)
        map.put("kitchen", kitchen)
        map.put("balconies", balcony)
        map.put("floor_number", floors)
        map.put("monthly_price", et_monthlyPrice.text.toString())
        map.put("currency_monthly", currency_MS)
        map.put("street_type", et_street_type.text.toString())
        map.put("furnished", furnishType)
        if (status == 28 && rentalFrequency.isNotEmpty()) {
            // map.put("rental_frequency", rentalFrequency)
        }
        if (!tvOrientation.text.equals(getString(R.string.select))) {
            map.put("orientation", tvOrientation.text.toString())
        }
        if (!tvRealEstateSituation.text.equals(getString(R.string.select))) {
            map.put("real_estate_situation", tvRealEstateSituation.text.toString())
        }
        val selectedAmenities:ArrayList<String> = ArrayList()
        for(i in amenityList){
            if (i.isSelected == true) {
                selectedAmenities.add(i.id?:"")
            }
        }
        //map.put("featured_image_id", "")
        //map.put("fave_agent_display_option", "")
        /*call_country_code_id.setOnCountryChangeListener {
            callCountryCode = "+"+ it.phoneCode
        }
        wp_country_code_id.setOnCountryChangeListener {
            wpCountryCode = "+" + it.phoneCode
        }*/

        call_country_code_id.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                callCountryCode =  call_country_code_id.getSelectedCountryCodeWithPlus()
                (call_country_code_id.getSelectedCountryCodeWithPlus())
            }

        })

        wp_country_code_id.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                wpCountryCode =  wp_country_code_id.getSelectedCountryCodeWithPlus()
                (wp_country_code_id.getSelectedCountryCodeWithPlus())
            }

        })

        val profileMap: HashMap<String, String> = HashMap()
        profileMap.put("user_id", userId)
        profileMap.put("display_name", et_name.text.toString().trim())
        if (et_callNo.text.toString().isNotEmpty()){
            profileMap.put("call_number", callCountryCode+et_callNo.text.toString().trim())
        } else{
            profileMap.put("call_number", "")
        }
        if (PreferencesService.instance.getLoginMethod() != "whatsapp"){
            if (et_wpNo.text.toString().isNotEmpty()){
                profileMap.put("whatsapp_number", wpCountryCode+et_wpNo.text.toString().trim())
            } else{
                profileMap.put("whatsapp_number","")
            }

        }
        if (isNetworkAvailable()){
            ApiClient.api!!.hitPostApiWithouTokenFieldParams(
                ApiClient.baseUrl + Constants.PROFILE_UPDATE_API,profileMap)
            hitAddPostApiWithoutTokenParams(Constants.ADD_POST, true, Constants.ADD_POST_URL, map,imageStringId,selectedAmenities)
        } else{
            showToast(this,resources.getString(R.string.intenet_error))
        }

    }


    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.ADD_POST, true)) {
            val model = Gson().fromJson(respopnse, LoginModel::class.java)
            if (model.success == true) {
                startActivity(Intent(this, MyAdsActivity::class.java))
                finish()
                overridePendingTransition(0, 0)
            } else {
                if (model.message == "limit") {
                    showToast(this, getString(R.string.ads_limit_reached))
                } else {
                    val message = if (!model.message.isNullOrEmpty()){
                        model.message.orEmpty()
                    } else {
                        getString(R.string.something_went_wrong)
                    }
                    showToast(this, message)
                }
            }


        /*    CoroutineScope(Dispatchers.IO).launch {
                val model = Gson().fromJson(respopnse, LoginModel::class.java)

                withContext(Dispatchers.Main) {
                    if (model.success == true) {
                        showToast(this@YourActivity, getString(R.string.add_post_success_msg))
                        startActivity(Intent(this@YourActivity, MyAdsActivity::class.java))
                        finish()
                        overridePendingTransition(0, 0)
                    } else {
                        val message = if (model.message == "limit") {
                            getString(R.string.ads_limit_reached)
                        } else if (!model.message.isNullOrEmpty()) {
                            model.message.orEmpty()
                        } else {
                            getString(R.string.something_went_wrong)
                        }
                        showToast(this@YourActivity, message)
                    }
                }
            }*/
        }
        else if(apiType.equals(Constants.NEIGHBORHOOD)){
            val model = Gson().fromJson(respopnse, NBHDModel::class.java)
            if(model.success){
                //nbhdList1.addAll(model.response)
                val list : ArrayList<NBHDDataResponse> = ArrayList()
                if (PreferencesService.instance.getLanguage() != "ar") {
                    for (i in 0 until model.response.size) {
                        val listArea : ArrayList<NBHDArea> = ArrayList()
                        for (j in 0 until model.response.get(i).area.size){
                            if (!model.response.get(i).area.get(j).description.isNullOrEmpty()){
                                listArea.add(model.response.get(i).area.get(j))
                            }
                        }
                        list.add(NBHDDataResponse(model.response[i].name,model.response[i].description,model.response[i].slug,listArea))
                    }
                } else{
                    list.addAll(model.response)
                }
                nbhdList1.addAll(list)
                if(PreferencesService.instance.getLanguage().equals("ar")){
                    for(i in nbhdList1){
                        Collections.sort(
                            i.area,
                            Comparator<NBHDArea?> { lhs, rhs ->
                                lhs!!.name.compareTo(rhs!!.name)
                            })

                    }
                }else{
                    for(i in nbhdList1){
                        Collections.sort(
                            i.area,
                            { lhs, rhs ->
                                lhs!!.description.lowercase().compareTo(rhs!!.description.lowercase())
                            })
                    }
                }

                adapterNBHD = SpinnerCityAdapter(this, nbhdList1)
                spinner_jurisdriction.setAdapter(adapterNBHD)
                var spinnerPosition  = 0
                Handler().postDelayed({
                    if(citySpinerItem!=null){
                        for(i in 0..nbhdList1.size-1){
                            if(nbhdList1.get(i).name.equals(citySpinerItem)){
                                spinnerPosition = i
                                cityList1 = nbhdList1.get(i).area as ArrayList<NBHDArea>

                            }
                        }
                    }
                    cityNameEnglish = nbhdList1[spinnerPosition].description.orEmpty()
                    spinner_jurisdriction.setSelection(spinnerPosition)
                    cityList1 = nbhdList1.get(spinnerPosition).area as ArrayList<NBHDArea>

                    /*adapterCity = CitySpinnerAdapter(this, cityList1)
                    spinner_neighborhood.adapter = adapterCity*/


                },100)
            }

        }
        else if (apiType.equals(Constants.UPLOAD_IMAGE)) {
            val model: UploadimagResponse =
                Gson().fromJson(respopnse, UploadimagResponse::class.java)
            if (model.success) {
                imagesList.add(model.data)
                if (imagesList.size < 15) {
                    el_main.visibility = View.VISIBLE
                } else {
                    el_main.visibility = View.GONE
                }
                adapterPropertyImages.notifyDataSetChanged()
                if(images_count<15){
                    if(imagesList.size >= images_count){
                        spin_kit.visibility = View.GONE
                        imageView_progress.visibility = View.GONE
                    }
                }else{
                    if(imagesList.size == images_count){
                        spin_kit.visibility = View.GONE
                        imageView_progress.visibility = View.GONE
                    }
                }

            }
        }else if(apiType.equals(Constants.UPDATE_ADD)){
            finish()
            overridePendingTransition(0,0)
        } else if (apiType.equals(Constants.PROFILE_UPDATE)){
            val loginModel = Gson().fromJson(respopnse,LoginModel::class.java)
            if(loginModel.success){
                if(loginModel.response!=null){
                    PreferencesService.instance.saveUserProfile(loginModel.response)
                }
            }
        } else if (apiType.equals(Constants.UPLOAD_VIDEO)){
            deleteFilesFromStorage()
            try {
                if (progressHUD!!.isShowing){
                    progressHUD!!.dismiss()
                }
            } catch (e : Exception){
                e.localizedMessage
            }
            val responseModel = Gson().fromJson(respopnse,UploadVideoResponse::class.java)
            if (!responseModel.file_url.isNullOrEmpty()){
                videoUrl = responseModel.file_url!!
                Log.d("videoUrl",videoUrl)
                imgAddVideo.visibility = View.GONE
                imgRemoveVideo.visibility = View.VISIBLE
                imgPreviewVideo.visibility = View.VISIBLE
                try {
                    Glide.with(this).load(videoUrl).placeholder(R.drawable.img_placeholder).into(imgPreviewVideo)
                }catch (e:Exception){
                    Log.d("exception",e.toString())
                }

            }else{
                if (!responseModel.message.isNullOrEmpty()) {
                    showToast(this, responseModel?.message!!)
                } else{
                    showToast(this,getString(R.string.something_went_wrong))
                }
            }
        } else if(apiType.equals(Constants.AMINITY)){
            val model = Gson().fromJson(respopnse, AmenityModel::class.java)
            if(model.success == true){
                amenityList.addAll(model.features!!)
                if(::intentModel.isInitialized){
                    if(!intentModel.property_feature_details.isNullOrEmpty()){
                        for(i in 0..intentModel.property_feature_details!!.size-1){
                            for(j in 0..amenityList.size-1){
                                if(amenityList.get(j).id == intentModel.property_feature_details!!.get(i).id){
                                    amenityList.get(j).isSelected = true
                                }
                            }
                        }
                    }
                }

                if(amenityList.size>9){
                    tv_see_more.visibility = View.VISIBLE
                }else{
                    tv_see_more.visibility = View.GONE
                }


                adapterAmenities.notifyDataSetChanged()
            }
        }

    }

    private fun bitmapToFile(bitmap: Bitmap?, fileNameToSave: String,fileSize:Double): File? { // File name like "image.png"
        var file: File? = null
        try {
            file =
                File(getExternalFilesDir("").toString() + File.separator + fileNameToSave + random.nextInt())
            file.createNewFile() //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
          //  if(!file.extension.toLowerCase().equals("jpg")){
                if(fileSize.toInt()>=15){
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 90, bos)
                }else if(fileSize.toInt()>=10 && fileSize.toInt()<15){
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 80, bos)
                }else if(fileSize.toInt()>=5 && fileSize.toInt()<10){
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 60, bos)
                }else{
                    bitmap!!.compress(Bitmap.CompressFormat.JPEG, 40, bos)
                }
          //  }
            val bitmapdata = bos.toByteArray() // YOU can also save it in JPEG
            val fos = FileOutputStream(file)
            fos.write(bitmapdata)
            fos.flush()
            fos.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return file
    }

    private val currentdateImageName: String
        private get() {
            val formatter = SimpleDateFormat("yyyyMMdd", Locale.US)
            val now = Date()
            val fileName = formatter.format(now).toString()
            return "NajafHomeImage $fileName"
        }

    private fun checkFileType(path: String?): Boolean {
        return if (path != null && (path == "image/jpg" || path == "image/jpeg" || path == "image/png")) {
            true
        } else {
            false
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

        if (requestCode == ImagePicker.REQUEST_CODE && resultCode == Activity.RESULT_OK) {
            var imageUri = data?.data!!
            if (imageUri != null) {
                files = File(imageUri.path)
                if (Build.VERSION.SDK_INT < 28) {
                    files = File(imageUri.path)
                    val file_size: Double = java.lang.String.valueOf(files!!.length() / 1024).toDouble()
                    val file_sizeMB: Double = (file_size/1024)
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                    files = bitmapToFile(bitmap, currentdateImageName,file_sizeMB)
                } else {
                    val source = imageUri.let {
                        contentResolver?.let { it1 ->
                            ImageDecoder.createSource(
                                it1,
                                it
                            )
                        }
                    }
                  //  val bitmap = decodeFile(files)
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = contentResolver.query(
                        imageUri,
                        filePathColumn, null, null, null
                    )
                    cursor!!.moveToFirst()
                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn.get(0))
                    val imgDecodableString = cursor.getString(columnIndex)
                    cursor.close()
                    files = File(imgDecodableString)
                    val bitmap = decodeFile(files)
                 //   val bitmap = source.let { ImageDecoder.decodeBitmap(it!!) }

                    val file_size: Double = java.lang.String.valueOf(files!!.length() / 1024).toDouble()
                    val file_sizeMB: Double = file_size/1024
                    files = bitmapToFile(bitmap, currentdateImageName,file_sizeMB)
                }

                    val requestFile = files!!.asRequestBody("image/*".toMediaTypeOrNull())
                    val body = MultipartBody.Part.createFormData(
                        "async-upload",
                        files!!.name + ".png",
                        requestFile
                    )

                    if (isNetworkAvailable()){
                        imageView_progress.visibility = View.VISIBLE
                        hitMultipartApiWithoutParams(
                            Constants.UPLOAD_IMAGE, true, "houzez-mobile-api/v1/media_upload",
                            body,imageView_progress
                        )
                    } else{
                        showToast(this, resources.getString(R.string.intenet_error))
                    }
            }
        } else if (requestCode == REQUEST_CODE && resultCode == RESULT_OK) {
            path.clear()
            path = data!!.getParcelableArrayListExtra(INTENT_PATH)!!;
            images_count = path.size
            for (i in path) {
                val imageListInUri = i
                if (Build.VERSION.SDK_INT < 28) {
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = contentResolver.query(
                        imageListInUri,
                        filePathColumn, null, null, null
                    )
                    cursor!!.moveToFirst()
                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn.get(0))
                    var imgDecodableString = cursor.getString(columnIndex)
                    val bitmap =
                        MediaStore.Images.Media.getBitmap(contentResolver, imageListInUri)
                    files = File(i.path)
                    val file_size: Double = java.lang.String.valueOf(files!!.length() / 1024).toDouble()
                    val file_sizeMB: Double = (file_size/1024)
                    //files = File(imgDecodableString)
                    files = bitmapToFile(bitmap, currentdateImageName,file_sizeMB)
                    val fileSizeUpload = (files!!.length() / 1024 )
                    println("FileSizeUpload:$fileSizeUpload")
                        val requestFile = files!!.asRequestBody("image/*".toMediaTypeOrNull())
                        var body = MultipartBody.Part.createFormData(
                            "async-upload",
                            files!!.name + ".png",
                            requestFile
                        )
                        if (isNetworkAvailable()){
                            imageView_progress.visibility = View.VISIBLE
                            hitMultipartApiWithoutParams(
                                Constants.UPLOAD_IMAGE, false, "houzez-mobile-api/v1/media_upload",
                                body,imageView_progress
                            )
                        } else{
                            showToast(this, resources.getString(R.string.intenet_error))
                        }
                } else {
                    val source = imageListInUri.let {
                        contentResolver?.let { it1 ->
                            ImageDecoder.createSource(
                                it1,
                                it
                            )
                        }
                    }
                    val filePathColumn = arrayOf(MediaStore.Images.Media.DATA)
                    val cursor: Cursor? = contentResolver.query(
                        imageListInUri,
                        filePathColumn, null, null, null
                    )
                    cursor!!.moveToFirst()
                    val columnIndex: Int = cursor.getColumnIndex(filePathColumn.get(0))
                    val imgDecodableString = cursor.getString(columnIndex)
                    cursor.close()
                    try {
                      //  val bitmap = source.let { ImageDecoder.decodeBitmap(it!!) }
                        files = File(imgDecodableString)
                        val bitmap = decodeFile(files)
                        val file_size: Double = java.lang.String.valueOf(files!!.length() / 1024).toDouble()
                        val file_sizeMB: Double = (file_size/1024)
                        files = bitmapToFile(bitmap, currentdateImageName,file_sizeMB)
                        val fileSizeUpload = (files!!.length() / 1024 )
                        println("FileSizeUpload:$fileSizeUpload")
                        val requestFile = files!!.asRequestBody("image/*".toMediaTypeOrNull())
                        val body = MultipartBody.Part.createFormData(
                            "async-upload",
                            files!!.name + ".png",
                            requestFile
                        )
                        if (isNetworkAvailable()){
                            imageView_progress.visibility = View.VISIBLE
                            hitMultipartApiWithoutParams(
                                Constants.UPLOAD_IMAGE, false, "houzez-mobile-api/v1/media_upload",
                                body,imageView_progress
                            )
                        } else{
                            showToast(this, resources.getString(R.string.intenet_error))
                        }
                    }catch (e:Exception){
                      Log.d("ImageError",e.message.toString())
                    }


                }
            }

        } else if (requestCode == VIDEO_UPLOAD_CAMERA_CODE && resultCode == RESULT_OK){
            val uri : Uri? = data?.data!!
            if (uri != null) {
                val videoFile = getFile(this,uri)
                val videoUri = Uri.fromFile(videoFile)
                val uris = mutableListOf<Uri>()
                uris.add(videoUri)
                compressVideo(uris)
                /*val intent = Intent(this,VideoTrimmerActivity::class.java)
                intent.putExtra("videoUri",Uri.fromFile(videoFile))
                startActivityForResult(intent,REQUEST_CODE_TRIM)*/
            }
        } else if (requestCode == VIDEO_UPLOAD_GALLERY_CODE && resultCode == RESULT_OK){
            Log.d("imagePicker","VideoPicker")
            val uri : Uri? = data?.data!!
            if (uri != null && checkIfUriCanBeUsedForVideo(uri)){
                val videoFile = getFile(this,uri)
                val intent = Intent(this,VideoTrimmerActivity::class.java)
                intent.putExtra("videoUri",Uri.fromFile(videoFile))
                startActivityForResult(intent,REQUEST_CODE_TRIM)
                overridePendingTransition(0,0)
            } else{
                showToast(this,getString(R.string.file_format_unupport))
            }
        } else if (requestCode == REQUEST_CODE_TRIM && resultCode == Activity.RESULT_OK){
            Log.d("imagePicker","VideoPicker")
            val uri: Uri? = data?.getStringExtra("videoUri").toString().toUri()
            if (uri != null && uri.toString() != "null"){
                val videoFile = File(uri.path!!)
                val videoUri = Uri.fromFile(videoFile)
                val uris = mutableListOf<Uri>()
                uris.add(videoUri)
                compressVideo(uris)
            }
        } else if (requestCode == REQUEST_CODE_LOCATION && resultCode == Activity.RESULT_OK){
            Log.d("imagePicker","VideoPicker")
            val lat = data?.getDoubleExtra("lat",0.0)
            val lng = data?.getDoubleExtra("lng",0.0)
            if (lat != 0.0 && lng != 0.0){
                propertyLatLng = LatLng(lat!!,lng!!)
                tvLocationOnMap.text = "${Utility.formatDoubleValue(propertyLatLng?.latitude!!)},${Utility.formatDoubleValue(propertyLatLng?.longitude!!)}"
            }
        }
        else if (resultCode == ImagePicker.RESULT_ERROR) {
            Log.d("imagePicker","ImagePicker")
            Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
        } else {
            Log.d("imagePicker","ImagePicker")
            //Toast.makeText(this, getString(R.string.task_cancelled), Toast.LENGTH_SHORT).show()
        }

    }

    fun deleteFilesFromStorage() {
        val fullPath = File(Environment.getExternalStorageDirectory(), "Movies")
        try {
            val file1 = File(fullPath, "NajafTrim.mp4")
            val file2 = File(fullPath, "NajafCompress.mp4")
            if (file1.exists()) {
                file1.delete()
            }
            if (file2.exists()) {
                file2.delete()
            }
        } catch (e: java.lang.Exception) {
            Log.e("App", "Exception while deleting file " + e.message)
        }
    }
    private val allowedVideoFileExtensions = arrayOf("video/mkv", "video/mp4", "video/3gp", "video/mov", "video/mts")
    private fun checkIfUriCanBeUsedForVideo(uri: Uri): Boolean {
        val mimeType = getMimeType(this, uri)
        val identifiedAsVideo = mimeType != null && allowedVideoFileExtensions.contains(mimeType)
        if (!identifiedAsVideo)
            return false
        try {
            //check that it can be opened and trimmed using our technique
            val fileDescriptor = contentResolver.openFileDescriptor(uri, "r")?.fileDescriptor
            val inputStream = (if (fileDescriptor == null) null else contentResolver.openInputStream(uri))
                ?: return false
            inputStream.close()
            return true
        } catch (e: Exception) {
            return false
        }
    }

    fun getMimeType(context: Context, uri: Uri): String? {
        return if (ContentResolver.SCHEME_CONTENT == uri.scheme) {
            val cr = context.contentResolver
            cr.getType(uri)
        } else {
            val fileExtension = MimeTypeMap.getFileExtensionFromUrl(uri.toString())
            MimeTypeMap.getSingleton().getMimeTypeFromExtension(fileExtension.lowercase())
        }
    }

    fun openVideoChooser(){
        if (hasPermissions(*PERMISSIONS)) {
            val layoutInflater = LayoutInflater.from(this)
            val customView = layoutInflater.inflate(R.layout.image_picker_choose_dialog, null)
            val lytCameraPick:LinearLayout = customView.findViewById(R.id.lytCameraPick)
            val lytGalleryPick:LinearLayout = customView.findViewById(R.id.lytGalleryPick)

            val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.choose_option)
                .setView(customView)
                .setNegativeButton(R.string.option_cancel) { _, _ ->

                }
                .show()

            lytCameraPick.setOnClickListener {
                val intent = Intent(MediaStore.ACTION_VIDEO_CAPTURE)
                intent.putExtra(MediaStore.EXTRA_VIDEO_QUALITY, 1)
                intent.putExtra(MediaStore.EXTRA_DURATION_LIMIT,30)
                //intent.putExtra(MediaStore.EXTRA_SIZE_LIMIT,50*1048576L) //size in Mb * 1048576L
                startActivityForResult(intent,VIDEO_UPLOAD_CAMERA_CODE)
                dialog.dismiss()
            }


            lytGalleryPick.setOnClickListener {
                val intent = Intent(Intent.ACTION_PICK)
                intent.type = "video/*"
                intent.action = Intent.ACTION_GET_CONTENT
                startActivityForResult(intent,VIDEO_UPLOAD_GALLERY_CODE)
                dialog.dismiss()
            }

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, VIDEO_PERMISSION_REQUEST_CODE)
        }

    }

    private fun compressVideo(uris: MutableList<Uri>) {
        imageView_progress.visibility = View.VISIBLE
        //lifecycleScope.launch {
        VideoCompressor.start(
            context = applicationContext,
            uris = uris,
            isStreamable = false,
            sharedStorageConfiguration = SharedStorageConfiguration(
                //saveAt = SaveLocation.movies,
                saveAt = SaveLocation.movies,
                videoName = "NajafCompress"
            ),
//                appSpecificStorageConfiguration = AppSpecificStorageConfiguration(
//
//                ),
            configureWith = Configuration(
                quality = VideoQuality.VERY_LOW,
                //videoNames = uris.map { uri -> uri.pathSegments.last() },
                isMinBitrateCheckEnabled = false,
            ),
            listener = object : CompressionListener {
                override fun onProgress(index: Int, percent: Float) {
                    //Update UI
                    if (percent <= 100)
                        runOnUiThread {
                            /*data[index] = VideoDetailsModel(
                                "",
                                uris[index],
                                "",
                                percent
                            )
                            adapter.notifyDataSetChanged()*/
                        }
                }

                override fun onStart(index: Int) {
                    /*data.add(
                        index,
                        VideoDetailsModel("", uris[index], "")
                    )
                    adapter.notifyDataSetChanged()*/
                }

                override fun onSuccess(index: Int, size: Long, path: String?) {
                    /*data[index] = VideoDetailsModel(
                        path,
                        uris[index],
                        getFileSize(size),
                        100F
                    )
                    adapter.notifyDataSetChanged()*/
                    imageView_progress.visibility = View.GONE
                    val videoFile = File(path!!)
                    if (videoFile != null){
                        hitUploadVideoApi(videoFile)
                    }
                }

                override fun onFailure(index: Int, failureMessage: String) {
                    imageView_progress.visibility = View.GONE
                    Log.wtf("failureMessage", failureMessage)
                    showToast(this@PostAdActivity,getString(R.string.failed_trim))
                }

                override fun onCancelled(index: Int) {
                    imageView_progress.visibility = View.GONE
                    Log.wtf("TAG", "compression has been cancelled")
                    // make UI changes, cleanup, etc
                }
            },
        )
        //}
    }

    private fun hitUploadVideoApi(videoFile : File){
        val sdfTime = SimpleDateFormat("_yyyyMMdd_HHmmss", Locale.US)
        val currentTime = sdfTime.format(Date()).toString()
        val fileName = "${videoFile.name.replace(".mp4","")}$currentTime.mp4"
        val requestFile = videoFile.asRequestBody("video/*".toMediaTypeOrNull())
        val body : MultipartBody.Part? = MultipartBody.Part.createFormData("video",
            fileName, requestFile)

        if (body != null){
            if (isNetworkAvailable()){
                hitMultipartVideoUploadApi(
                    Constants.UPLOAD_VIDEO, true, Constants.UPLOAD_VIDEO_API,body)
            } else{
                showToast(this, resources.getString(R.string.intenet_error))
            }
        }
    }

    private fun compressImage(image: Bitmap): Bitmap? {
        val baos = ByteArrayOutputStream()
        image.compress(
            Bitmap.CompressFormat.JPEG,
            100,
            baos
        ) //Compression quality, here 100 means no compression, the storage of compressed data to baos
        var options = 90
        while (baos.toByteArray().size / 1024 > 400) {  //Loop if compressed picture is greater than 400kb, than to compression
            baos.reset() //Reset baos is empty baos
            image.compress(
                Bitmap.CompressFormat.JPEG,
                options,
                baos
            ) //The compression options%, storing the compressed data to the baos
            options -= 10 //Every time reduced by 10
        }
        val isBm =
            ByteArrayInputStream(baos.toByteArray()) //The storage of compressed data in the baos to ByteArrayInputStream
        return BitmapFactory.decodeStream(isBm, null, null)
    }


    fun setIntentData(){
        var type: String = ""
        tv_heading.setText(resources.getString(R.string.edit_the_ad))
        button_addPost.setText(resources.getString(R.string.edit))

        if (!intentModel?.property_meta?.houzez_geolocation_lat.isNullOrEmpty()
            &&!intentModel?.property_meta?.houzez_geolocation_long.isNullOrEmpty()
            && !intentModel?.property_meta?.houzez_geolocation_lat?.get(0).isNullOrEmpty()
            && !intentModel?.property_meta?.houzez_geolocation_long?.get(0).isNullOrEmpty()){
            val lat = (intentModel?.property_meta?.houzez_geolocation_lat?.get(0) ?: "0.0").toDouble()
            val lng = (intentModel?.property_meta?.houzez_geolocation_long?.get(0) ?: "0.0").toDouble()
            if (lat != 0.0 && lng != 0.0){
                propertyLatLng = LatLng(lat, lng)
                tvLocationOnMap.text = "[${propertyLatLng?.latitude},${propertyLatLng?.longitude}]"
            }
        }

        if (intentModel.property_address != null){
            if (intentModel.property_address.property_area != null){
                nbhd = intentModel.property_address.property_area
            }
        }

        if(intentModel.property_address!=null){
            if(intentModel.property_address.property_city!=null){
                citySpinerItem = intentModel.property_address.property_city
            }
        }



        if(intentModel.property_images!=null && intentModel.property_images.size>0){
            for(i in 0..intentModel.property_images.size-1){
                imagesList.add(ImageData(intentModel.property_images_id.get(i),intentModel.property_images.get(i),""))
            }

        }
        if (imagesList.size < 15) {
            el_main.visibility = View.VISIBLE
        } else {
            el_main.visibility = View.GONE
        }

        adapterPropertyImages = AdapterPropertyImages(this, imagesList, this)

        rv_photo.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false))
        rv_photo.setAdapter(adapterPropertyImages)

        if (intentModel.post_title != null){
            et_addTitle.setText(intentModel.post_title)
        }

        if (intentModel.post_content != null){
            val html: String = intentModel.post_content
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                et_postDetail.setText(Html.fromHtml(html, 0))
            } else {
                et_postDetail.setText(Html.fromHtml(html))
            }
        }



        if(intentModel.property_meta != null){
            if(!intentModel.property_meta.monthly_price.isNullOrEmpty()){
                et_monthlyPrice.setText(intentModel.property_meta!!.monthly_price!!.get(0))
            }
        }

        if(!intentModel.property_feature_details.isNullOrEmpty() && amenityList .isNotEmpty() ){
            if(intentModel.property_feature_details!=null){
                for(i in 0..intentModel.property_feature_details!!.size-1){
                    for(j in 0..amenityList.size-1){
                        if(amenityList.get(j).id == intentModel.property_feature_details!!.get(i).id){
                            amenityList.get(j).isSelected = true
                        }
                    }
                }
            }

            adapterAmenities.notifyDataSetChanged()
        }


        if(intentModel.property_meta != null){
            if(!intentModel.property_meta.orientation.isNullOrEmpty()) {
                tvOrientation.text = intentModel.property_meta.orientation!!.get(0)
            }
        }

        if(intentModel.property_meta != null){
            if(!intentModel.property_meta.street_type.isNullOrEmpty()) {
                et_street_type.setText(intentModel.property_meta.street_type!!.get(0))
            }
        }

        if(intentModel.property_meta != null){
            if(!intentModel.property_meta.real_estate_situation.isNullOrEmpty()) {
                tvRealEstateSituation.text = intentModel.property_meta.real_estate_situation!!.get(0)
            }
        }

        if(intentModel.property_meta != null){
            if(!intentModel.property_meta.furnished.isNullOrEmpty()) {
                Log.d("furnishType",intentModel.property_meta.furnished!!.get(0))
                if(intentModel.property_meta.furnished!!.get(0)!!.lowercase(Locale.getDefault()).equals("yes")){
                    furnishType ="yes"
                    radio_furnish_yes.isChecked = true
                }else if(intentModel.property_meta.furnished!!.get(0)!!.lowercase(Locale.getDefault())
                        .equals("no")){
                    furnishType ="no"
                    radio_furnish_no.isChecked = true
                }else if(intentModel.property_meta.furnished!!.get(0)!!.lowercase().equals("half furnished")){
                    furnishType ="half"
                    radio_furnish_half.isChecked = true
                }
            }
        }



        if (intentModel.property_meta != null){
            if (!intentModel.property_meta.living_room.isNullOrEmpty()){
                livingRoom = intentModel.property_meta.living_room!!.get(0)
                if (livingRoom == "1"){
                    livingRoom_1.background = getDrawable(R.drawable.bg_number_outline)
                    livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if ( livingRoom == "2" ){
                    livingRoom_1.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_2.background = getDrawable(R.drawable.bg_number_outline)
                    livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (livingRoom == "3"){
                    livingRoom_1.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_3.background = getDrawable(R.drawable.bg_number_outline)
                    livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (livingRoom == "4"){
                    livingRoom_1.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_4.background = getDrawable(R.drawable.bg_number_outline)
                    livingRoom_5.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (livingRoom == "5") {
                    livingRoom_1.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_2.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_3.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_4.background = getDrawable(R.drawable.bg_outline_square)
                    livingRoom_5.background = getDrawable(R.drawable.bg_number_outline)
                    livingRoom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    livingRoom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                }
            }
        }

        if (intentModel.property_meta != null){
            if (!intentModel.property_meta.balconies.isNullOrEmpty()){
                balcony = intentModel.property_meta.balconies!!.get(0)
                if (balcony == "1"){
                    balcony_1.background = getDrawable(R.drawable.bg_number_outline)
                    balcony_2.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_3.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_4.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_5.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if ( balcony == "2" ){
                    balcony_1.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_2.background = getDrawable(R.drawable.bg_number_outline)
                    balcony_3.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_4.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_5.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (balcony == "3"){
                    balcony_1.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_2.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_3.background = getDrawable(R.drawable.bg_number_outline)
                    balcony_4.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_5.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (balcony == "4"){
                    balcony_1.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_2.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_3.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_4.background = getDrawable(R.drawable.bg_number_outline)
                    balcony_5.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (balcony == "5") {
                    balcony_1.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_2.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_3.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_4.background = getDrawable(R.drawable.bg_outline_square)
                    balcony_5.background = getDrawable(R.drawable.bg_number_outline)
                    balcony_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    balcony_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                }
            }
        }



        if (intentModel.property_meta != null){
            if (!intentModel.property_meta.kitchen.isNullOrEmpty()){
                kitchen = intentModel.property_meta.kitchen!!.get(0)
                if (kitchen == "1"){
                    kitchen_1.background = getDrawable(R.drawable.bg_number_outline)
                    kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if ( kitchen == "2" ){
                    kitchen_1.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_2.background = getDrawable(R.drawable.bg_number_outline)
                    kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (kitchen == "3"){
                    kitchen_1.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_3.background = getDrawable(R.drawable.bg_number_outline)
                    kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (kitchen == "4"){
                    kitchen_1.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_4.background = getDrawable(R.drawable.bg_number_outline)
                    kitchen_5.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (kitchen == "5") {
                    kitchen_1.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_2.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_3.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_4.background = getDrawable(R.drawable.bg_outline_square)
                    kitchen_5.background = getDrawable(R.drawable.bg_number_outline)
                    kitchen_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    kitchen_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                }
            }
        }





        if (intentModel.property_meta != null){
            if (intentModel.property_meta.fave_property_size != null){
                et_postArea.setText(intentModel.property_meta.fave_property_size.get(0))
            }
        }

        if (intentModel.price != null){
            et_postPrice.setText(intentModel.price)
        }

        if (intentModel.property_meta != null){
            if (intentModel.property_meta.fave_currency?.firstOrNull() == "USD") {
                currency_SP = "USD"
                radioUSD_SP.isChecked = true
            } else {
                currency_SP = "IQD"
                radioIQD_SP.isChecked = true
            }

            if (intentModel.property_meta.currency_monthly?.firstOrNull() == "USD") {
                currency_MS = "USD"
                radioUSD_MS.isChecked = true
            } else {
                currency_MS = "IQD"
                radioIQD_MS.isChecked = true
            }
        }

        if (intentModel.property_meta != null){
            if (intentModel.property_meta.fave_property_bedrooms != null){
                rooms = intentModel.property_meta.fave_property_bedrooms.get(0)
                if (rooms == "1"){
                    rooms_1.background = getDrawable(R.drawable.bg_number_outline)
                    rooms_2.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_3.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_4.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_5.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if ( rooms == "2" ){
                    rooms_1.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_2.background = getDrawable(R.drawable.bg_number_outline)
                    rooms_3.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_4.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_5.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (rooms == "3"){
                    rooms_1.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_2.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_3.background = getDrawable(R.drawable.bg_number_outline)
                    rooms_4.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_5.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (rooms == "4"){
                    rooms_1.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_2.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_3.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_4.background = getDrawable(R.drawable.bg_number_outline)
                    rooms_5.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (rooms == "5") {
                    rooms_1.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_2.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_3.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_4.background = getDrawable(R.drawable.bg_outline_square)
                    rooms_5.background = getDrawable(R.drawable.bg_number_outline)
                    rooms_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    rooms_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                }
            }
        }

        if (intentModel.property_meta != null){
            if (intentModel.property_meta.fave_property_bathrooms != null){
                bathroom = intentModel.property_meta.fave_property_bathrooms.get(0)
                if (bathroom == "1"){
                    bathroom_1.background = getDrawable(R.drawable.bg_number_outline)
                    bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (bathroom == "2"){
                    bathroom_1.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_2.background = getDrawable(R.drawable.bg_number_outline)
                    bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (bathroom == "3"){
                    bathroom_1.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_3.background = getDrawable(R.drawable.bg_number_outline)
                    bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (bathroom == "4"){
                    bathroom_1.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_4.background = getDrawable(R.drawable.bg_number_outline)
                    bathroom_5.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (bathroom == "5"){
                    bathroom_1.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_2.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_3.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_4.background = getDrawable(R.drawable.bg_outline_square)
                    bathroom_5.background = getDrawable(R.drawable.bg_number_outline)
                    bathroom_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    bathroom_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                }
            }
        }

        if (intentModel.property_meta != null){
            if (intentModel.property_meta.favePropertyGarage != null){
                floors = intentModel.property_meta.favePropertyGarage.get(0)
                if (floors == "1"){
                    floor_1.background = getDrawable(R.drawable.bg_number_outline)
                    floor_2.background = getDrawable(R.drawable.bg_outline_square)
                    floor_3.background = getDrawable(R.drawable.bg_outline_square)
                    floor_4.background = getDrawable(R.drawable.bg_outline_square)
                    floor_5.background = getDrawable(R.drawable.bg_outline_square)
                    floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (floors == "2"){
                    floor_1.background = getDrawable(R.drawable.bg_outline_square)
                    floor_2.background = getDrawable(R.drawable.bg_number_outline)
                    floor_3.background = getDrawable(R.drawable.bg_outline_square)
                    floor_4.background = getDrawable(R.drawable.bg_outline_square)
                    floor_5.background = getDrawable(R.drawable.bg_outline_square)
                    floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (floors == "3"){
                    floor_1.background = getDrawable(R.drawable.bg_outline_square)
                    floor_2.background = getDrawable(R.drawable.bg_outline_square)
                    floor_3.background = getDrawable(R.drawable.bg_number_outline)
                    floor_4.background = getDrawable(R.drawable.bg_outline_square)
                    floor_5.background = getDrawable(R.drawable.bg_outline_square)
                    floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (floors == "4"){
                    floor_1.background = getDrawable(R.drawable.bg_outline_square)
                    floor_2.background = getDrawable(R.drawable.bg_outline_square)
                    floor_3.background = getDrawable(R.drawable.bg_outline_square)
                    floor_4.background = getDrawable(R.drawable.bg_number_outline)
                    floor_5.background = getDrawable(R.drawable.bg_outline_square)
                    floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                    floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                } else if (floors == "5"){
                    floor_1.background = getDrawable(R.drawable.bg_outline_square)
                    floor_2.background = getDrawable(R.drawable.bg_outline_square)
                    floor_3.background = getDrawable(R.drawable.bg_outline_square)
                    floor_4.background = getDrawable(R.drawable.bg_outline_square)
                    floor_5.background = getDrawable(R.drawable.bg_number_outline)
                    floor_1.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_2.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_3.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_4.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    floor_5.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
                }
            }
        }

        if (intentModel.property_attr != null){
            if (intentModel.property_attr!!.property_type != null){
                type = intentModel.property_attr!!.property_type
                Log.d("type12", type)
                if (intentModel.property_meta != null){
                    if (intentModel.property_meta.fave_property_garage_size != null){
                        propertySubType = intentModel.property_meta.fave_property_garage_size.get(0)
                    }
                }
                if (type == "سكني"){
                    propertyType = 55
                    property_residence.background = getDrawable(R.drawable.bg_outline_blue_new)
                    property_commercial.background = getDrawable(R.drawable.bg_outline_solid)
                    property_land.background = getDrawable(R.drawable.bg_outline_solid)
                    property_residence.setTextColor(ContextCompat.getColor(applicationContext, R.color.whiteNew))
                    property_commercial.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    property_land.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    ll_counts.visibility = View.VISIBLE
                    resindential_type.visibility = View.VISIBLE
                    commercial_type.visibility = View.GONE
                    land_type.visibility = View.GONE

                    if (propertySubType == "house"){
                        rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_house.setColorFilter(img_house.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_house.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "apart_house"){
                        rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_house.setColorFilter(img_house.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_house.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_apart.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "apartment"){
                        rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_house.setColorFilter(img_house.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_house.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "villa"){
                        rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_villa.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_house.setColorFilter(img_house.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_house.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_villa.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "residence_other"){
                        rl_house_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_apart_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_apartment.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_villa.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_residence_other.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        img_house.setColorFilter(img_house.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_apart.setColorFilter(img_apart.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_apartment.setColorFilter(img_apartment.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_villa.setColorFilter(img_villa.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_residence_other.setColorFilter(img_residence_other.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        tv_house.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_apart.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_apartment.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_villa.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_residence_other.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                    }

                } else if (type == "تجاري"){
                    propertyType = 25
                    property_residence.background = getDrawable(R.drawable.bg_outline_solid)
                    property_commercial.background = getDrawable(R.drawable.bg_outline_blue_new)
                    property_land.background = getDrawable(R.drawable.bg_outline_solid)
                    property_residence.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    property_commercial.setTextColor(ContextCompat.getColor(applicationContext, R.color.whiteNew))
                    property_land.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    ll_counts.visibility = View.VISIBLE
                    resindential_type.visibility = View.GONE
                    commercial_type.visibility = View.VISIBLE
                    land_type.visibility = View.GONE

                    if (propertySubType == "office"){
                        rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_office.setColorFilter(img_office.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_office.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "shop"){
                        rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_shop.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_shop.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "store"){
                        rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_store.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_store.setColorFilter(img_store.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_store.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "building"){
                        rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_building.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_building.setColorFilter(img_building.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_building.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "factory"){
                        rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_factory.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_factory.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "showroom"){
                        rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "commercial_other"){
                        rl_office_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_shop.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_store.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_building.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_factory.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_showroom_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_comm_other.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        img_office.setColorFilter(img_office.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_shop.setColorFilter(img_shop.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_store.setColorFilter(img_store.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_building.setColorFilter(img_building.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_factory.setColorFilter(img_factory.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_showroom.setColorFilter(img_showroom.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_comm_other.setColorFilter(img_comm_other.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        tv_office.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_shop.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_store.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_building.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_factory.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_showroom.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_comm_other.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                    }

                } else if (type == "شقة"){
                    propertyType = 73
                    property_residence.background = getDrawable(R.drawable.bg_outline_solid)
                    property_commercial.background = getDrawable(R.drawable.bg_outline_solid)
                    property_land.background = getDrawable(R.drawable.bg_outline_blue_new)
                    property_residence.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    property_commercial.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
                    property_land.setTextColor(ContextCompat.getColor(applicationContext, R.color.whiteNew))
                    ll_counts.visibility = View.GONE
                    resindential_type.visibility = View.GONE
                    commercial_type.visibility = View.GONE
                    land_type.visibility = View.VISIBLE

                    if (propertySubType == "agriculture"){
                        rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if(propertySubType == "commercial"){
                        rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "residencial"){
                        rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                        tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.grey))
                    } else if (propertySubType == "industrial"){
                        rl_agriculture_out.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_commercail_outter.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_residnce_land.background = resources.getDrawable(R.drawable.bg_outline_solid)
                        rl_industrial.background = resources.getDrawable(R.drawable.bg_outline_blue)
                        img_agriculture.setColorFilter(img_agriculture.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_commercail.setColorFilter(img_commercail.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_residnce_land.setColorFilter(img_residnce_land.context.resources.getColor(R.color.grey), PorterDuff.Mode.SRC_ATOP)
                        img_industrial.setColorFilter(img_industrial.context.resources.getColor(R.color.skyBlue), PorterDuff.Mode.SRC_ATOP)
                        tv_agriculture.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_commercail.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_residnce_land.setTextColor(ContextCompat.getColor(this, R.color.grey))
                        tv_industrial.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
                    }
                }
            }
        }

        if (intentModel.property_attr!= null) {
            if (intentModel.property_attr!!.property_status != null) {
                val rentStatus:String = intentModel.property_attr!!.property_status
                // status = intentModel.property_attr!!.property_status
                if (rentStatus == "للبيع") {
                    status = 29
                    tv_for_rent.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
                    tv_for_sale.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_blue_new))
                    tv_for_rent.setTextColor(resources.getColor(R.color.grey))
                    tv_for_sale.setTextColor(resources.getColor(R.color.whiteNew))
                    tv_price_head.setText(resources.getString(R.string.selling_price))
                } else {
                    status = 28
                    tv_for_rent.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_blue_new))
                    tv_for_sale.setBackgroundDrawable(resources.getDrawable(R.drawable.bg_outline_solid))
                    tv_for_rent.setTextColor(resources.getColor(R.color.whiteNew))
                    tv_for_sale.setTextColor(resources.getColor(R.color.grey))
                    tv_price_head.setText(resources.getString(R.string.rent_price))
                }
            }
        }

        if (intentModel.property_meta != null){
            if (intentModel.property_meta!!.favePropertyLand != null){
                val width:String  = intentModel.property_meta!!.favePropertyLand.get(0)
                et_width.setText(width)
            }
        }

        if (!intentModel.property_meta?.video.isNullOrEmpty() && !intentModel.property_meta?.video!!.get(0).isNullOrEmpty()){
            videoUrl = intentModel.property_meta?.video!!.get(0)
            imgAddVideo.visibility = View.GONE
            imgRemoveVideo.visibility = View.VISIBLE
            imgPreviewVideo.visibility = View.VISIBLE

            try {
                Glide.with(this).load(videoUrl).placeholder(R.drawable.img_placeholder).into(imgPreviewVideo)
            }catch (e:Exception){
                Log.d("exception",e.toString())
            }
        }

    }

    override fun onItemClick(model: NBHDArea) {
        nbhdDialog.dismiss()
        nbhdSlug = model.slug
        nbhdNameEnglish = model.description.orEmpty()
        if(PreferencesService.instance.getLanguage().equals("ar")){
            spinner_neighborhood.text = model.name
        }else{
            if(model.description!=null && model.description.isNotEmpty()){
                spinner_neighborhood.text = model.description
            }else{
                spinner_neighborhood.text = model.name
            }
        }

    }


    fun decodeFile(f: File?): Bitmap? {
        var b: Bitmap? = null
        try {
            // Decode image size
            val o = BitmapFactory.Options()
            o.inJustDecodeBounds = true
            var fis = FileInputStream(f)
            BitmapFactory.decodeStream(fis, null, o)
            fis.close()
            Log.d("ImageHeight",o.outHeight.toString())
            Log.d("ImageHeight",o.outWidth.toString())
            val IMAGE_MAX_SIZE = 1000
            val IMAGE_MAX_WIDTH_SIZE = o.outWidth/2
            var scale:Double = 1.0
            while(o.outWidth / scale / 2 >= IMAGE_MAX_SIZE &&
                o.outHeight / scale / 2 >= IMAGE_MAX_SIZE) {
                scale *= 2;
            }
           /* if (o.outHeight > IMAGE_MAX_SIZE || o.outWidth > IMAGE_MAX_WIDTH_SIZE) {
                scale = Math.pow(
                    2.0,
                    Math.round(Math.log((IMAGE_MAX_WIDTH_SIZE/Math.max(o.outHeight, o.outWidth)).toDouble())/Math.log(0.5))
                        .toDouble()
                ) as Double
            }*/

            // Decode with inSampleSize
            val o2 = BitmapFactory.Options()
            o2.inSampleSize = scale.toInt()
            fis = FileInputStream(f)
            b = BitmapFactory.decodeStream(fis, null, o2)
            fis.close()
        } catch (e: IOException) {
            e.printStackTrace()
        }
        return b
    }

    private fun showRealEstateBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_real_estate, null)
        bottomSheetDialog.setContentView(view)

        val txtTitle:TextView = view.findViewById(R.id.txtTitle)

        txtTitle.text = getString(R.string.real_estate_situation)
        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerSituations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val options = listOf(
            "TAPU",
            "LAND CONTRACT",
            "NON",
            "INVESTMENT",
            "MUSATAHA",
            "CARD",
            "AGRICULTURAL TAPU"
        )

        val adapter = CommonBottomSheetSelectedAdapter(options) { selected ->
            tvRealEstateSituation.text = selected
            bottomSheetDialog.dismiss()
        }

        recyclerView.adapter = adapter
        bottomSheetDialog.show()
    }


    private fun showOrientationBottomSheet() {
        val bottomSheetDialog = BottomSheetDialog(this)
        val view = layoutInflater.inflate(R.layout.bottom_sheet_real_estate, null)
        bottomSheetDialog.setContentView(view)

        val txtTitle:TextView = view.findViewById(R.id.txtTitle)

        txtTitle.text = getString(R.string.orientation)

        val recyclerView = view.findViewById<RecyclerView>(R.id.recyclerSituations)
        recyclerView.layoutManager = LinearLayoutManager(this)

        val options = listOf(
            "Sun Set",
            "Sun Rise",
            "North East",
            "North West",
            "South East",
            "South West",
            "South",
            "North",
            "Qibla"
        )

        val adapter = CommonBottomSheetSelectedAdapter(options) { selected ->
            tvOrientation.text = selected
            bottomSheetDialog.dismiss()
        }

        recyclerView.adapter = adapter
        bottomSheetDialog.show()
    }

    private fun setupTermsAndPrivacyClick() {
        val isArabic = PreferencesService.instance.getLanguage() == "ar"
        val text = getString(R.string.terms_and_privacy)

        val spannable = SpannableString(text)

        // English indices
        if (!isArabic) {
            val termsStart = text.indexOf("Terms")
            val termsEnd = termsStart + "Terms".length

            val privacyStart = text.indexOf("Privacy Policy")
            val privacyEnd = privacyStart + "Privacy Policy".length

            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    launchTermsUrl()
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = true
                    // ds.color = ContextCompat.getColor(this@PostAdActivity, R.color.skyBlue)
                }
            }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    launchPrivacyPolicyUrl()
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = true
                    // ds.color = ContextCompat.getColor(this@PostAdActivity, R.color.skyBlue)
                }
            }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Arabic indices
        else {
            val termsStart = text.indexOf("الشروط")
            val termsEnd = termsStart + "الشروط".length

            val privacyStart = text.indexOf("سياسة الخصوصية")
            val privacyEnd = privacyStart + "سياسة الخصوصية".length

            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    launchTermsUrl()
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = true
                    // ds.color = ContextCompat.getColor(this@PostAdActivity, R.color.skyBlue)
                }
            }, termsStart, termsEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)

            spannable.setSpan(object : ClickableSpan() {
                override fun onClick(widget: View) {
                    launchPrivacyPolicyUrl()
                }
                override fun updateDrawState(ds: TextPaint) {
                    ds.isUnderlineText = true
                    // ds.color = ContextCompat.getColor(this@PostAdActivity, R.color.skyBlue)
                }
            }, privacyStart, privacyEnd, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE)
        }

        // Apply to RadioButton
        tvTerms.text = spannable
        tvTerms.movementMethod = LinkMovementMethod.getInstance()
        tvTerms.highlightColor = Color.TRANSPARENT
    }

    private fun launchTermsUrl() {
        launchPrivacyPolicyUrl()
    }

    private fun launchPrivacyPolicyUrl() {
        try {
            val url = "https://baghdadhome.com/terms-and-conditions-2/"
            val i = Intent(Intent.ACTION_VIEW)
            i.setData(Uri.parse(url))
            startActivity(i)
        } catch (e : Exception) {
            println("Error launching url : $e")
        }
    }
    private fun scrollToView(nestedScrollView: NestedScrollView, targetView: View) {
        nestedScrollView.post {
            val y = targetView.top - 50  // small offset for better visibility
            nestedScrollView.smoothScrollTo(0, y)
        }
    }
}