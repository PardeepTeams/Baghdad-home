package com.baghdadhomes.Activities

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.maps.model.LatLng
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.baghdadhomes.Adapters.SpinnerCityAdapter
import com.baghdadhomes.Adapters.SpinnerNBHDAdapter
import com.baghdadhomes.Models.LoginModel
import com.baghdadhomes.Models.NBHDArea
import com.baghdadhomes.Models.NBHDDataResponse
import com.baghdadhomes.Models.NBHDModel
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class UpdateProfileActivity : BaseActivity() {
    var PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA)
    } else{
        arrayOf(
            Manifest.permission.CAMERA)
    }
    private val PERMISSION_ALL = 1
    private val CAMERA_REQUEST = 1888
    private val PICK_IMAGE_REQUEST_CODE = 102
    private var isPermissionAsked = false
    var files: File? = null
    var random = Random()

    lateinit var rl_upload_image: RelativeLayout
    lateinit var rl_wp_head: RelativeLayout
    lateinit var rl_wp_number: RelativeLayout
    lateinit var ll_nested: LinearLayout
    lateinit var img_back: ImageView
    lateinit var img_resgister:ImageView
    lateinit var et_name: EditText
    lateinit var et_email:EditText
    lateinit var etAddress:EditText
    lateinit var et_call_number:EditText
    lateinit var et_whatsapp_number:EditText
    lateinit var call_country_code: CountryCodePicker
    lateinit var wp_country_code:CountryCodePicker
    lateinit var tv_check_box: CheckBox
    lateinit var btn_register:TextView
    lateinit var tv_email:TextView
    lateinit var llAddress:LinearLayout
    var callCountryCode:String = "+964"
    var whatsappCountryCode = "+964"
    var userId: String? = null

    lateinit var spinnerCity: AppCompatSpinner
    lateinit var spinnerNbhd: AppCompatSpinner

    lateinit var tvNameHeader:TextView
    lateinit var tvPhotoHeader:TextView
    lateinit var tvManagerHeader:TextView
    lateinit var etManager:EditText
    lateinit var tvAllowedAds:TextView
    lateinit var tvLocationOnMap:TextView

    var cityList:ArrayList<NBHDDataResponse> = ArrayList()
    var nbhdList:ArrayList<NBHDArea> = ArrayList()

    var cityId : String ?= ""
    var nbhdId : String ?= ""
    var cityNameEnglish : String ?= null
    var nbhdNameEnglish : String ?= null
    var propertyLatLng : LatLng?= null
    private val REQUEST_CODE_LOCATION = 10101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_update_profile)
        adjustFontScale(resources.configuration)
        init()

        img_back.setOnClickListener {
            finish()
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

        rl_upload_image.setOnClickListener {
            dismissKeyboard(rl_upload_image)
            openImagePicker()
        }

        ll_nested.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return true
            }
        })

        val loginMethod = PreferencesService.instance.getLoginMethod()
        if (loginMethod == "whatsapp"){
            rl_wp_number.visibility = View.GONE
            rl_wp_head.visibility = View.GONE
            wp_country_code.isEnabled = false
            et_whatsapp_number.isEnabled = false
        } else{
            rl_wp_number.visibility = View.VISIBLE
            rl_wp_head.visibility = View.VISIBLE
            wp_country_code.isEnabled = true
            et_whatsapp_number.isEnabled = true
        }


        btn_register.setOnClickListener {
            /*call_country_code.setOnCountryChangeListener {
                callCountryCode = "+"+ it.phoneCode
            }
            wp_country_code.setOnCountryChangeListener {
                whatsappCountryCode = "+" + it.phoneCode
            }*/

            call_country_code.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
                override fun onCountrySelected() {
                    callCountryCode =  call_country_code.getSelectedCountryCodeWithPlus()
                    (call_country_code.getSelectedCountryCodeWithPlus())
                }

            })

            wp_country_code.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
                override fun onCountrySelected() {
                    whatsappCountryCode =  wp_country_code.getSelectedCountryCodeWithPlus()
                    (wp_country_code.getSelectedCountryCodeWithPlus())
                }

            })

            userId = PreferencesService.instance.getUserData!!.ID
            Log.d("id123", userId.toString())

            if (et_name.text.isEmpty()){
                et_name.setError(resources.getString(R.string.enter_name))
            } else if (llAddress.visibility == View.VISIBLE && etAddress.text.toString().trim().isEmpty()){
                etAddress.setError(resources.getString(R.string.enter_company_address))
            }
            /*else if (et_email.text.isEmpty() || !emailValidate(et_email.text.toString())){
                et_email.setError(resources.getString(R.string.enter_valid_email))
            }
            else if (et_call_number.text.isEmpty()){
                et_call_number.setError(resources.getString(R.string.enter_call_number))
            } else if (et_whatsapp_number.text.isEmpty()){
                et_whatsapp_number.setError(resources.getString(R.string.enter_whatsapp_number))
            } */
            else{
                /*val profileMap: HashMap<String, String> = HashMap()
                profileMap.put("user_id", userId.toString())
                profileMap.put("display_name", et_name.text.toString().trim())
                profileMap.put("call_number", callCountryCode+et_call_number.text.toString().trim())
                profileMap.put("whatsapp_number", whatsappCountryCode+et_whatsapp_number.text.toString().trim())
                profileMap.put("houzez_file_data_name", files!!.)*/

                var body: MultipartBody.Part? = null
                if(files!=null){
                    val requestFile = files!!.asRequestBody("image/png".toMediaTypeOrNull())
                    body = MultipartBody.Part.createFormData(
                        "houzez_file_data_name",
                        files!!.name + ".png",
                        requestFile )

                }
                var callNumber = ""
                var wpNumber = ""
                val userIdBody = userId.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                val namebody: RequestBody =
                    et_name.getText().toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                if (et_call_number.text.isNotEmpty()){
                    callNumber = callCountryCode+et_call_number.text.toString().trim()
                }
                val callBody = callNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                if (et_whatsapp_number.text.isNotEmpty()){
                    wpNumber = whatsappCountryCode+et_whatsapp_number.text.toString().trim()
                }
                val wpBody = wpNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                val addressBody: RequestBody = etAddress.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                val cityBody: RequestBody = cityId.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                val nbhdBody: RequestBody = nbhdId.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                val managerBody: RequestBody = etManager.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())

                if (isNetworkAvailable()){
                    val map: HashMap<String, RequestBody> = HashMap()
                    map.put("user_id", userIdBody)
                    map.put("display_name", namebody)
                    map.put("call_number", callBody)
                    map.put("whatsapp_number", wpBody)
                    if (llAddress.visibility == View.VISIBLE){
                        map.put("address", addressBody)
                        map["city_slug"] = cityBody
                        map["nbhd_slug"] = nbhdBody
                        map["manager_name"] = managerBody
                        if (propertyLatLng != null){
                            map["lat"] = propertyLatLng?.latitude.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                            map["lng"] = propertyLatLng?.longitude.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                        }
                    }
                    hitMultipartApi(Constants.PROFILE_UPDATE, true, Constants.PROFILE_UPDATE_API, map, body)
                } else{
                    showToast(this,resources.getString(R.string.intenet_error))
                }
            }
        }

        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                cityId = cityList[position].slug
                cityNameEnglish = cityList[position].description.orEmpty()
                println("CitySlug-$cityId")
                nbhdList.clear()
                if (!cityList[position].area.isNullOrEmpty()){
                    nbhdList.addAll(cityList[position].area)
                }
                spinnerNbhd.adapter = SpinnerNBHDAdapter(this@UpdateProfileActivity, nbhdList)
                for (i in 0 until nbhdList.size){
                    if (nbhdList[i].slug == nbhdId){
                        spinnerNbhd.setSelection(i)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        spinnerNbhd.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                nbhdId = nbhdList[position].slug
                nbhdNameEnglish = nbhdList[position].description.orEmpty()
                println("NBHDSlug-$nbhdId")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    fun init(){
        ll_nested = findViewById(R.id.ll_nested)
        rl_wp_head = findViewById(R.id.rl_wp_head)
        rl_wp_number = findViewById(R.id.rl_wp_number)
        rl_upload_image = findViewById(R.id.rl_upload_image)
        img_resgister = findViewById(R.id.img_resgister)
        img_back = findViewById(R.id.img_back)
        llAddress = findViewById(R.id.llAddress)
        etAddress = findViewById(R.id.etAddress)
        img_resgister = findViewById(R.id.img_resgister)
        et_name = findViewById(R.id.et_name)
        et_email = findViewById(R.id.et_email)
        et_call_number = findViewById(R.id.et_call_number)
        et_whatsapp_number = findViewById(R.id.et_whatsapp_number)
        call_country_code = findViewById(R.id.call_country_code)
        wp_country_code = findViewById(R.id.wp_country_code)
        tv_check_box = findViewById(R.id.tv_check_box)
        btn_register = findViewById(R.id.btn_register)
        tv_email = findViewById(R.id.tv_email)
        spinnerCity = findViewById(R.id.spinnerCity)
        spinnerNbhd = findViewById(R.id.spinnerNbhd)
        tvNameHeader = findViewById(R.id.tvNameHeader)
        tvPhotoHeader = findViewById(R.id.tvPhotoHeader)
        tvManagerHeader = findViewById(R.id.tvManagerHeader)
        etManager = findViewById(R.id.etManager)
        tvAllowedAds = findViewById(R.id.tvAllowedAds)
        tvLocationOnMap = findViewById(R.id.tvLocationOnMap)

        tvAllowedAds.text = "${getString(R.string.allowed_ads_msg)} ${PreferencesService.instance.getUserData?.allowed_ads ?: "0"}"

        //"role_type"- "houzez_agency" || "houzez_seller"

        if(PreferencesService.instance.getUserData!!.role_type == "houzez_agency"){
            llAddress.visibility = View.VISIBLE
            tvPhotoHeader.visibility = View.VISIBLE
            tvManagerHeader.visibility = View.VISIBLE
            etManager.visibility = View.VISIBLE
            tvAllowedAds.visibility = View.VISIBLE
            tvNameHeader.text = getString(R.string.name_of_the_company_comp)
            et_name.hint = getString(R.string.name_of_the_company_comp)

            etAddress.setText(PreferencesService.instance.getUserData?.address.orEmpty())
            etManager.setText(PreferencesService.instance.getUserData?.manager_name.orEmpty())

            cityId = PreferencesService.instance.getUserData?.city_slug.orEmpty()
            nbhdId = PreferencesService.instance.getUserData?.nbhd_slug.orEmpty()

            if (cityList.isEmpty()){
                if(isNetworkAvailable()){
                    hitGetApiWithoutToken(Constants.NEIGHBORHOOD, true, Constants.NEIGHBORHOOD_API)
                }else{
                    showToast(this, resources.getString(R.string.intenet_error))
                }
            }

            try {
                if (!PreferencesService.instance.getUserData?.lat.isNullOrEmpty() && !PreferencesService.instance.getUserData?.lng.isNullOrEmpty()){
                    val lat = (PreferencesService.instance.getUserData?.lat ?: "0.0").toDouble()//data?.getDoubleExtra("lat",0.0)
                    val lng = (PreferencesService.instance.getUserData?.lng ?: "0.0").toDouble()//data?.getDoubleExtra("lng",0.0)
                    if (lat != 0.0 && lng != 0.0){
                        propertyLatLng = LatLng(lat, lng)
                        tvLocationOnMap.text = "${Utility.formatDoubleValue(propertyLatLng?.latitude!!)},${Utility.formatDoubleValue(propertyLatLng?.longitude!!)}"
                    }
                }
            } catch (e : Exception){
                println(e.localizedMessage)
            }
        } else {
            llAddress.visibility = View.GONE
            tvPhotoHeader.visibility = View.GONE
            tvManagerHeader.visibility = View.GONE
            etManager.visibility = View.GONE
            tvAllowedAds.visibility = View.GONE
            tvNameHeader.text = getString(R.string.name_comp)
            et_name.hint = getString(R.string.enter_name)
        }

        if (PreferencesService.instance.getUserData!!.displayName != null
            && PreferencesService.instance.getUserData!!.displayName!!.isNotEmpty()){
            et_name.setText(PreferencesService.instance.getUserData!!.displayName)
        }
        /*if(PreferencesService.instance.getUserData!!.userEmail!=null){
            et_email.visibility = View.GONE
            tv_email.visibility = View.GONE
            et_email.setText(PreferencesService.instance.getUserData!!.userEmail)
        }else{
            et_email.visibility = View.GONE
            tv_email.visibility = View.GONE
        }*/

        if(PreferencesService.instance.getUserData!!.userImage!=null){
            Glide.with(this).load(PreferencesService.instance.getUserData!!.userImage).placeholder(R.drawable.img_placeholder).into(img_resgister)
        }

        if(PreferencesService.instance.getUserData!!.callNumber!=null){
            var  callnumber = PreferencesService.instance.getUserData!!.callNumber
            callnumber = callnumber!!.replace(" ","")
            if(callnumber.length>10){
                val addno =  (callnumber.length)-10
                val callhghfgfg = callnumber.substring(addno,callnumber.length)
                var countryCode = callnumber.substring(0,addno)
                et_call_number.setText(callhghfgfg)
                countryCode = countryCode.replace("+","")
                call_country_code.setCountryForPhoneCode(countryCode.toInt())
                callCountryCode ="+"+countryCode;
            }else{
                et_call_number.setText(callnumber)
            }
        }
        if(PreferencesService.instance.getUserData!!.whatsappNumber!=null){
            var  whatsappNumber = PreferencesService.instance.getUserData!!.whatsappNumber
            whatsappNumber = whatsappNumber!!.replace(" ","")
            if(whatsappNumber.length>10){
                val addno =  (whatsappNumber.length)-10
                val callhghfgfg = whatsappNumber.substring(addno,whatsappNumber.length)
                var countryCode = whatsappNumber.substring(0,addno)
                et_whatsapp_number.setText(callhghfgfg)
                countryCode = countryCode.replace("+","")
                wp_country_code.setCountryForPhoneCode(countryCode.toInt())
                whatsappCountryCode ="+"+countryCode;
            }else{
                et_whatsapp_number.setText(whatsappNumber)
            }
        }

        /*wp_country_code.setOnCountryChangeListener {
            whatsappCountryCode = "+" + it.phoneCode
        }*/

        wp_country_code.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                whatsappCountryCode =  wp_country_code.getSelectedCountryCodeWithPlus()
                (wp_country_code.getSelectedCountryCodeWithPlus())
            }

        })

        /*call_country_code.setOnCountryChangeListener {
            callCountryCode = "+" + it.phoneCode
            if(tv_check_box.isChecked){
                whatsappCountryCode = "+" + it.phoneCode
                wp_country_code.setCountryForPhoneCode(it.phoneCode.toInt())
            }
        }*/

        call_country_code.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                callCountryCode =  call_country_code.getSelectedCountryCodeWithPlus()
                (call_country_code.getSelectedCountryCodeWithPlus())
                /*if (tv_check_box.isChecked){
                    whatsappCountryCode = call_country_code.getSelectedCountryCodeWithPlus()
                    wp_country_code.setCountryForPhoneCode(callCountryCode.toInt())
                }*/
            }

        })


        tv_check_box.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                // if(et_call_number.text.toString().isNotEmpty()){
                et_whatsapp_number.setText(et_call_number.text.toString())
                wp_country_code.setCountryForPhoneCode(callCountryCode.toInt())
                //  }
            }else{

            }
        }

        et_call_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(tv_check_box.isChecked){
                    et_whatsapp_number.setText(et_call_number.text.toString())
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }

        })

    }

    private fun openImagePicker() {
        isPermissionAsked = false
        if (hasPermissions(*PERMISSIONS)) {
            /*ImagePicker.with(this)
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()*/
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

            // Handle Camera option click
            lytCameraPick.setOnClickListener {
                ImagePicker.with(this)
                    .cameraOnly()
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
                dialog.dismiss()
            }

            // Handle Gallery option click
            lytGalleryPick.setOnClickListener {
                ImagePicker.with(this)
                    .galleryOnly()
                    .compress(1024)			//Final image size will be less than 1 MB(Optional)
                    .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                    .start()
                dialog.dismiss()
            }
            //  openImagePickDialog()
        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ALL) {
            if (!isPermissionAsked) {
                isPermissionAsked = true
                if (hasPermissions(*permissions)) {
                    // openImagePickDialog()
                    /*ImagePicker.with(this)
                        .compress(1024)			//Final image size will be less than 1 MB(Optional)
                        .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                        .start()*/
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

                    // Handle Camera option click
                    lytCameraPick.setOnClickListener {
                        ImagePicker.with(this)
                            .cameraOnly()
                            .compress(1024)			//Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                            .start()
                        dialog.dismiss()
                    }

                    // Handle Gallery option click
                    lytGalleryPick.setOnClickListener {
                        ImagePicker.with(this)
                            .galleryOnly()
                            .compress(1024)			//Final image size will be less than 1 MB(Optional)
                            .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                            .start()
                        dialog.dismiss()
                    }
                }
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
            if(requestCode == ImagePicker.REQUEST_CODE){
                if (resultCode == Activity.RESULT_OK) {
                    val imageUri = data?.data!!
                    if (imageUri != null) {
                        files = File(imageUri.path)
                        if (Build.VERSION.SDK_INT < 28) {
                            files = File(imageUri.path)
                            val bitmap =
                                MediaStore.Images.Media.getBitmap(contentResolver, imageUri)
                            img_resgister.setImageBitmap(bitmap)
                        } else {
                            val source = imageUri.let {
                                contentResolver?.let { it1 ->
                                    ImageDecoder.createSource(
                                        it1,
                                        it
                                    )
                                }
                            }
                            // val bitmap =  MediaStore.Images.Media.getBitmap(activity?.contentResolver, imageUri)
                            val bitmap = source.let { ImageDecoder.decodeBitmap(it!!) }
                            files = bitmapToFile(bitmap, currentdateImageName!!)
                            img_resgister.setImageBitmap(bitmap)
                            // Glide.with(context).load(bitmap).into(userProfile)
                        }
                    }
                    // Use Uri object instead of File to avoid storage permissions
                    // imgProfile.setImageURI(fileUri)
                } else if (resultCode == ImagePicker.RESULT_ERROR) {
                    Toast.makeText(this, ImagePicker.getError(data), Toast.LENGTH_SHORT).show()
                } else {
                    //Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
                }
            }  else if (requestCode == REQUEST_CODE_LOCATION && resultCode == Activity.RESULT_OK){
                Log.d("imagePicker","VideoPicker")
                val lat = data?.getDoubleExtra("lat",0.0)
                val lng = data?.getDoubleExtra("lng",0.0)
                if (lat != 0.0 && lng != 0.0){
                    propertyLatLng = LatLng(lat!!,lng!!)
                    tvLocationOnMap.text = "${Utility.formatDoubleValue(propertyLatLng?.latitude!!)},${Utility.formatDoubleValue(propertyLatLng?.longitude!!)}"
                }
            }
    }

    private fun bitmapToFile(bitmap: Bitmap?, fileNameToSave: String): File? {
        var file: File? = null
        try {
            file =
                File(getExternalFilesDir("").toString() + File.separator + fileNameToSave + random.nextInt())
            file.createNewFile() //Convert bitmap to byte array
            val bos = ByteArrayOutputStream()
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 40, bos)
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
        get() {
            val formatter = SimpleDateFormat("yyyyMMdd", Locale.US)
            val now = Date()
            val fileName = formatter.format(now).toString()
            return "NajafHomeImage" + fileName
        }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.PROFILE_UPDATE)){
            val loginModel = Gson().fromJson(respopnse, LoginModel::class.java)
            if(loginModel.success){
                if(loginModel.response!=null){
                    PreferencesService.instance.saveUserProfile(loginModel.response)
                    showToast(this, resources.getString(R.string.profile_updated))
                    finish()
                }
            }
        } else if(apiType == Constants.NEIGHBORHOOD){
            val model = Gson().fromJson(respopnse, NBHDModel::class.java)
            if(model.success){
                val list : ArrayList<NBHDDataResponse> = ArrayList()
                if (PreferencesService.instance.getLanguage() != "ar") {
                    for (i in 0 until model.response.size) {
                        val listArea1 : ArrayList<NBHDArea> = ArrayList()
                        val listArea : ArrayList<NBHDArea> = ArrayList()
                        for (j in 0 until model.response.get(i).area.size){
                            if (!model.response.get(i).area.get(j).description.isNullOrEmpty()){
                                listArea.add(model.response.get(i).area.get(j))
                            }
                        }
                        listArea.sortWith{ lhs, rhs ->
                            lhs!!.description.compareTo(rhs!!.description)
                        }

                        //listArea1.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
                        listArea1.addAll(listArea)
                        list.add(NBHDDataResponse(model.response[i].name,model.response[i].description,model.response[i].slug,listArea1))
                    }
                } else{
                    for (i in 0 until model.response.size) {
                        val listArea1 : ArrayList<NBHDArea> = ArrayList()
                        val listArea : ArrayList<NBHDArea> = ArrayList()
                        for (j in 0 until model.response.get(i).area.size){
                            listArea.add(model.response.get(i).area.get(j))
                        }
                        listArea.sortWith{ lhs, rhs ->
                            lhs!!.name.compareTo(rhs!!.name)
                        }

                        //listArea1.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
                        listArea1.addAll(listArea)
                        list.add(NBHDDataResponse(model.response[i].name,model.response[i].description,model.response[i].slug,listArea1))
                    }
                }

                cityList.addAll(list)
                spinnerCity.adapter = SpinnerCityAdapter(this, cityList)
                for (i in 0 until cityList.size){
                    if (cityList[i].slug == cityId){
                        spinnerCity.setSelection(i)
                    }
                }
            }
        }
    }
}