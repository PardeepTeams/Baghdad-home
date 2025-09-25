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
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import com.bumptech.glide.Glide
import com.facebook.login.LoginManager
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.maps.model.LatLng
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.baghdadhomes.Adapters.SpinnerCityAdapter
import com.baghdadhomes.Adapters.SpinnerNBHDAdapter
import com.baghdadhomes.Models.*
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import pl.droidsonroids.gif.GifImageView
import java.io.ByteArrayOutputStream
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class WhatsappRegisterScreen : BaseActivity() {
    lateinit var rl_upload_image: RelativeLayout
    private var isPermissionAsked = false
    private val PERMISSION_ALL = 1

    lateinit var img_resgister: ImageView

    var files: File? = null

    var random = Random()
    var PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA)
    } else{
        arrayOf(
            Manifest.permission.CAMERA)
    }
    lateinit var et_name:EditText
    lateinit var et_email:EditText
    lateinit var etAddress:EditText
    lateinit var llAddress:LinearLayout
    lateinit var radioEstate:RadioButton
    lateinit var radioCustomer:RadioButton

    lateinit var btn_register:TextView
    var phoneNumber:String? = null
    var password:String? = null
    lateinit var imageView_progress:GifImageView

    var socialModel : SocialModel ?= null
    lateinit var spinnerCity: AppCompatSpinner
    lateinit var spinnerNbhd: AppCompatSpinner
    lateinit var img_back: ImageView

    lateinit var tvNameHeader:TextView
    lateinit var tvPhotoHeader:TextView
    lateinit var tvManagerHeader:TextView
    lateinit var etManager:EditText
    lateinit var llTypes:LinearLayout
    lateinit var llNumbers:LinearLayout
    lateinit var call_country_code:CountryCodePicker
    lateinit var wp_country_code:CountryCodePicker
    lateinit var et_call_number:EditText
    lateinit var et_whatsapp_number:EditText
    lateinit var tv_check_box:CheckBox
    lateinit var tvLocationOnMap:TextView

    var cityList:ArrayList<NBHDDataResponse> = ArrayList()
    var nbhdList:ArrayList<NBHDArea> = ArrayList()

    var cityId : String ?= ""
    var nbhdId : String ?= ""
    var login_type : String = ""
    var imgUrl : String = ""
    var cityNameEnglish : String ?= null
    var nbhdNameEnglish : String ?= null
    var propertyLatLng : LatLng ?= null
    private val REQUEST_CODE_LOCATION = 10101

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whatsapp_register_screen)

        inits()
        clickListeners()

        llTypes.visibility = View.GONE

        login_type = intent.getStringExtra("login_type").orEmpty()
        if (login_type == "agency"){
            llAddress.visibility = View.VISIBLE
            tvPhotoHeader.visibility = View.VISIBLE
            tvManagerHeader.visibility = View.VISIBLE
            etManager.visibility = View.VISIBLE
            tvNameHeader.text = getString(R.string.name_of_the_company_comp)
            et_name.hint = getString(R.string.name_of_the_company_comp)

            if (cityList.isEmpty()){
                if(isNetworkAvailable()){
                    hitGetApiWithoutToken(Constants.NEIGHBORHOOD, true, Constants.NEIGHBORHOOD_API)
                }else{
                    showToast(this, resources.getString(R.string.intenet_error))
                }
            }
        } else {
            llAddress.visibility = View.GONE
            tvPhotoHeader.visibility = View.GONE
            tvManagerHeader.visibility = View.GONE
            etManager.visibility = View.GONE
            tvNameHeader.text = getString(R.string.name_comp)
            et_name.hint = getString(R.string.enter_name)
        }
        //radioCustomer.performClick()
    }

    private fun inits(){
        imageView_progress = findViewById(R.id.imageView_progress)
        rl_upload_image = findViewById(R.id.rl_upload_image)
        et_name = findViewById(R.id.et_name)
        et_email = findViewById(R.id.et_email)
        img_resgister = findViewById(R.id.img_resgister)
        btn_register = findViewById(R.id.btn_register)
        etAddress = findViewById(R.id.etAddress)
        llAddress = findViewById(R.id.llAddress)
        radioEstate = findViewById(R.id.radioEstate)
        radioCustomer = findViewById(R.id.radioCustomer)
        spinnerCity = findViewById(R.id.spinnerCity)
        spinnerNbhd = findViewById(R.id.spinnerNbhd)
        tvNameHeader = findViewById(R.id.tvNameHeader)
        tvPhotoHeader = findViewById(R.id.tvPhotoHeader)
        tvManagerHeader = findViewById(R.id.tvManagerHeader)
        etManager = findViewById(R.id.etManager)
        img_back = findViewById(R.id.img_back)
        llTypes = findViewById(R.id.llTypes)
        llNumbers = findViewById(R.id.llNumbers)
        call_country_code = findViewById(R.id.call_country_code)
        wp_country_code = findViewById(R.id.wp_country_code)
        et_call_number = findViewById(R.id.et_call_number)
        et_whatsapp_number = findViewById(R.id.et_whatsapp_number)
        tv_check_box = findViewById(R.id.tv_check_box)
        tvLocationOnMap = findViewById(R.id.tvLocationOnMap)

        if(intent.getStringExtra("phone")!=null){
            phoneNumber =  intent.getStringExtra("phone")
            password = intent.getStringExtra("password")
        }

        if (intent.getStringExtra("socialModel") != null){
            socialModel = Gson().fromJson(intent.getStringExtra("socialModel"), SocialModel::class.java)
            et_name.setText(socialModel?.display_name.orEmpty())
            et_call_number.setText(socialModel?.phone.orEmpty())
            imgUrl = socialModel?.profile_url.orEmpty()
            Glide.with(this).load(imgUrl)
                .into(img_resgister)

            llNumbers.visibility = View.VISIBLE
        }
    }

    private fun clickListeners(){
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

        tv_check_box.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                et_whatsapp_number.setText(et_call_number.text.toString())
                wp_country_code.setCountryForPhoneCode(call_country_code.selectedCountryCodeWithPlus.toInt())
            }
        }


        et_call_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if(tv_check_box.isChecked){
                        et_whatsapp_number.setText(et_call_number.text.toString())
                    }
                    if (et_call_number.text.toString().isNotEmpty()
                        && et_call_number.text.toString().substring(0,1) == "0"){
                        et_call_number.setText(et_call_number.text.toString().replace("0",""))
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }

        })

        et_whatsapp_number.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                try {
                    if (et_whatsapp_number.text.toString().isNotEmpty()
                        && et_whatsapp_number.text.toString().substring(0,1) == "0"){
                        et_whatsapp_number.setText(et_whatsapp_number.text.toString().replace("0",""))
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            }
            override fun afterTextChanged(s: Editable?) {

            }

        })


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
                spinnerNbhd.adapter = SpinnerNBHDAdapter(this@WhatsappRegisterScreen, nbhdList)
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

        radioEstate.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                llAddress.visibility = View.VISIBLE
                tvPhotoHeader.visibility = View.VISIBLE
                tvManagerHeader.visibility = View.VISIBLE
                etManager.visibility = View.VISIBLE
                tvNameHeader.text = getString(R.string.name_of_the_company_comp)
                et_name.hint = getString(R.string.name_of_the_company_comp)

                if (cityList.isEmpty()){
                    if(isNetworkAvailable()){
                        hitGetApiWithoutToken(Constants.NEIGHBORHOOD, true, Constants.NEIGHBORHOOD_API)
                    }else{
                        showToast(this, resources.getString(R.string.intenet_error))
                    }
                }
            }
        }

        radioCustomer.setOnCheckedChangeListener { buttonView, isChecked ->
            if (isChecked){
                llAddress.visibility = View.GONE
                tvPhotoHeader.visibility = View.GONE
                tvManagerHeader.visibility = View.GONE
                etManager.visibility = View.GONE
                tvNameHeader.text = getString(R.string.name_comp)
                et_name.hint = getString(R.string.enter_name)
            }
        }

        rl_upload_image.setOnClickListener {
            openImagePicker()
        }

        btn_register.setOnClickListener {
            if(et_name.text.toString().isEmpty()){
                et_name.error = getString(R.string.please_enter_name)
            }
            /* else if(et_email.text.toString().isEmpty() && !emailValidate(et_email.text.toString())){
                 et_email.error = getString(R.string.enter_valid_email)
             }*/
            else if(radioEstate.isChecked && cityId.isNullOrEmpty()){
                showToast(this,getString(R.string.select_jurisdiction_comp))
            }
            else if(radioEstate.isChecked && nbhdId.isNullOrEmpty()){
                showToast(this,getString(R.string.choose_the_neighborhood))
            }
            else if(radioEstate.isChecked && etAddress.text.toString().trim().isEmpty()){
                etAddress.error = getString(R.string.enter_company_address)
            }
            else{
                if (isNetworkAvailable()){
                    if (socialModel != null){
                        /*val type = if (radioEstate.isChecked) {
                            "agency"
                        } else {
                            "customer"
                        }*/

                        val map : HashMap<String, String> = HashMap()
                        if (!socialModel?.email.isNullOrEmpty()) {
                            map["email"] = socialModel?.email.orEmpty()
                        }
                        if (!et_call_number.text.isNullOrEmpty()) {
                            map["phone"] = "${call_country_code.selectedCountryCodeWithPlus}${et_call_number.text.toString()}"
                        }
                        if (!et_whatsapp_number.text.isNullOrEmpty()) {
                            map["whatsapp_number"] = "${wp_country_code.selectedCountryCodeWithPlus}${et_whatsapp_number.text.toString()}"
                        }
                        map["source"] = socialModel?.source.orEmpty()
                        map["user_id"] = socialModel?.user_id.orEmpty()
                        map["login_type"] = socialModel?.login_type.orEmpty()
                        map["profile_url"] = imgUrl
                        map["display_name"] = et_name.text.toString()
                        map["device_token"] = socialModel?.device_token.orEmpty()
                        map["role_type"] = login_type
                        if (login_type == "agency"){
                            map["address"] = etAddress.text.toString()
                            map["city_slug"] = cityId.toString()
                            map["nbhd_slug"] = nbhdId.toString()
                            map["manager_name"] = etManager.text.toString()
                            if (propertyLatLng != null){
                                map["lat"] = propertyLatLng?.latitude.toString()
                                map["lng"] = propertyLatLng?.longitude.toString()
                            }
                        }

                        hitPostApi(Constants.SOCIAL_LOGIN, true, Constants.SOCIAL_LOGIN_API, map)
                    } else {
                        var body: MultipartBody.Part? = null
                        if(files!=null){
                            val requestFile = files!!.asRequestBody("image/png".toMediaTypeOrNull())
                            body = MultipartBody.Part.createFormData("houzez_file_data_name",
                                files!!.name + ".png", requestFile )

                        }

                        val nameBody: RequestBody = et_name.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                        var emailBody: RequestBody = et_email.getText().toString().toRequestBody("text/plain".toMediaTypeOrNull())
                        val callBody: RequestBody = phoneNumber!!.toRequestBody("text/plain".toMediaTypeOrNull())
                        val whatsappBody: RequestBody = phoneNumber!!.toRequestBody("text/plain".toMediaTypeOrNull())
                        var passwordBody: RequestBody = phoneNumber!!.toRequestBody("text/plain".toMediaTypeOrNull())
                        val loginTypeBody: RequestBody = "whatsapp".toRequestBody("text/plain".toMediaTypeOrNull())

                        /*val type = if (radioEstate.isChecked) {
                            "agency"
                        } else {
                            "customer"
                        }*/
                        val registerTypeBody : RequestBody = login_type.toRequestBody("text/plain".toMediaTypeOrNull())
                        val addressBody : RequestBody = etAddress.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                        val cityBody: RequestBody = cityId.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                        val nbhdBody: RequestBody = nbhdId.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                        val managerBody: RequestBody = etManager.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())

                        val map: HashMap<String, RequestBody> = HashMap()
                        map["first_name"] = nameBody
                        map["last_name"] = nameBody
                        map["username"] = callBody
                        map["call_number"] = callBody
                        map["whatsapp_number"] = whatsappBody
                        map["login_type"] = loginTypeBody
                        map["role_type"] = registerTypeBody
                        if (login_type == "agency"){
                            map["address"] = addressBody
                            map["city_slug"] = cityBody
                            map["nbhd_slug"] = nbhdBody
                            map["manager_name"] = managerBody
                            if (propertyLatLng != null){
                                map["lat"] = propertyLatLng?.latitude.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                                map["lng"] = propertyLatLng?.longitude.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                            }
                        }

                        hitMultipartApi(Constants.WHATSAPP_REGISTER, true, Constants.REGISTER_API, map, body)
                    }
                } else{
                    Utility.showToast(this, resources.getString(R.string.intenet_error))
                }

            }
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if(apiType.equals(Constants.WHATSAPP_REGISTER)){
            val loginModel = Gson().fromJson(respopnse, LoginModel::class.java)
            if(loginModel.success){
                if(loginModel.response!=null){
                    PreferencesService.instance.saveUserProfile(loginModel.response)
                    PreferencesService.instance.saveUserLoginStatus(true)
                    PreferencesService.instance.saveUserLoginMethod("whatsapp")
                    val intent:Intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                    overridePendingTransition(0,0)
                }else if (loginModel.message!=null){
                    Utility.showToast(this,loginModel.message.toString())
                }else{
                    imageView_progress.visibility = View.VISIBLE
                    val map:HashMap<String,String> = HashMap()
                    map.put("phone",phoneNumber!!)
                    map.put("login_type","whatsapp")
                    map.put("device_token",firebaseDeviceToken.toString())
                    hitPostApiWithLoader(Constants.CHECK_WHATSAPP_LOGIN,false,Constants.CHECK_WHATSAPP_LOGIN_API,map,imageView_progress)
                }
            }else{
                Utility.showToast(this,getString(R.string.something_went_wrong))
            }
        }else if(apiType.equals(Constants.CHECK_WHATSAPP_LOGIN)){
            imageView_progress.visibility = View.GONE
            val loginModel = Gson().fromJson(respopnse, LoginModel::class.java)
            if(loginModel.message!=null){
                Utility.showToast(this,loginModel.message.toString())
            }else if(loginModel.response!= null){
                PreferencesService.instance.saveUserProfile(loginModel.response)
                PreferencesService.instance.saveUserLoginStatus(true)
                PreferencesService.instance.saveUserLoginMethod("whatsapp")
                val intent:Intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
                overridePendingTransition(0,0)
            }else{

            }

        } else if(apiType == Constants.SOCIAL_LOGIN){
            val loginModel = Gson().fromJson(respopnse,LoginModel::class.java)
            if(loginModel.success){
                PreferencesService.instance.saveUserProfile(loginModel.response)
                PreferencesService.instance.saveUserLoginStatus(true)
                PreferencesService.instance.saveUserLoginMethod("social")
                val intent:Intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
              //  overridePendingTransition(0,0)
            }else{
                try {
                    FirebaseAuth.getInstance().signOut()
                    LoginManager.getInstance().logOut()
                    GoogleSignIn.getClient(
                        this,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    ).signOut()
                }catch (e:Exception){
                    e.message
                }
            }
        }  else if(apiType == Constants.NEIGHBORHOOD){
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
            }
        } else if (apiType.equals(Constants.UPLOAD_USER_IMAGE)){
            val model = Gson().fromJson(respopnse, SocialImageUpload::class.java)
            if (model.success == true && !model.response.isNullOrEmpty()){
                imgUrl = model.response.toString()
                Glide.with(this).load(imgUrl).into(img_resgister)
            }
        }
    }

    private fun openImagePicker() {
        isPermissionAsked = false
        if (hasPermissions(*PERMISSIONS)) {
            /*ImagePicker.with(this)
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()*/
            //  openImagePickDialog()

            val layoutInflater = LayoutInflater.from(this)
            val customView = layoutInflater.inflate(R.layout.image_picker_choose_dialog, null)
            val lytCameraPick:LinearLayout = customView.findViewById(R.id.lytCameraPick)
            val lytGalleryPick:LinearLayout = customView.findViewById(R.id.lytGalleryPick)

            val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.choose_option)
                .setView(customView)
                .setNegativeButton(R.string.option_cancel) { _, _ ->
                    // listener.onResult(null)
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

        } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
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
                        val bitmap = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                            val source = ImageDecoder.createSource(contentResolver, imageUri)
                            ImageDecoder.decodeBitmap(source) { decoder, _, _ ->
                                decoder.isMutableRequired = true
                                decoder.allocator = ImageDecoder.ALLOCATOR_SOFTWARE // or HARDWARE if needed
                            }
                        } else {
                            MediaStore.Images.Media.getBitmap(contentResolver, imageUri) // fallback for older devices
                        }
                        if (socialModel != null){
                            hitImageUploadApi(files)
                        } else {
                            img_resgister.setImageBitmap(bitmap)
                        }
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
                        if (socialModel != null){
                            hitImageUploadApi(files)
                        } else {
                            img_resgister.setImageBitmap(bitmap)
                        }
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

    private fun hitImageUploadApi(file: File?) {
        if (isNetworkAvailable()){
            if(file!=null){
                val requestFile = file.asRequestBody("image/png".toMediaTypeOrNull())
                val body: MultipartBody.Part = MultipartBody.Part.createFormData("image",
                    file.name + ".png", requestFile )

                hitMultipartVideoUploadApi(Constants.UPLOAD_USER_IMAGE,true,Constants.UPLOAD_USER_IMAGE_API, body)
            }
        } else {
            Utility.showToast(this, getString(R.string.intenet_error))
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
        private get() {
            val formatter = SimpleDateFormat("yyyyMMdd", Locale.US)
            val now = Date()
            val fileName = formatter.format(now).toString()
            return "NajafHomeImage" + fileName
        }


    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray,
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == PERMISSION_ALL) {
            if (!isPermissionAsked) {
                isPermissionAsked = true
                if (hasPermissions(*permissions)) {
                    // openImagePickDialog()
                   /* ImagePicker.with(this)
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
                            // listener.onResult(null)
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
}