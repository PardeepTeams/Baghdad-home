package com.baghdadhomes.Activities

import android.Manifest
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.content.pm.PackageInfo
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.ImageDecoder
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.widget.*
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.AppCompatSpinner
import androidx.core.app.ActivityCompat
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.github.dhaval2404.imagepicker.ImagePicker
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
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
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.ProgressHud
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
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.text.SimpleDateFormat
import java.util.*
import com.baghdadhomes.R


class RegisterActivity : BaseActivity() {
    private var isPermissionAsked = false
    private val PERMISSION_ALL = 1
    private val CAMERA_REQUEST = 1888
    private val PICK_IMAGE_REQUEST_CODE = 102
    var PERMISSIONS = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        arrayOf(
            Manifest.permission.CAMERA)
    } else{
        arrayOf(
            Manifest.permission.CAMERA)
    }
    var mimeTypes = arrayOf("image/jpeg", "image/jpg", "image/png")
    var random = Random()
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var GOOGLE_SIGN_IN: Int = 100
    lateinit var idToken: String
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var mCallbackManager: CallbackManager? = null

    lateinit var img_back: ImageView
    lateinit var img_resgister: ImageView
    lateinit var rl_upload_image: RelativeLayout
    lateinit var rl_google_login: RelativeLayout
    lateinit var rl_fb_login: RelativeLayout
    lateinit var rl_wp_login: RelativeLayout
    lateinit var et_name: EditText
    lateinit var et_email: EditText
    lateinit var et_password: EditText
    lateinit var et_cnf_password: EditText
    lateinit var fb_loginBtn: LoginButton
    lateinit var btn_register: TextView
    lateinit var call_country_code: CountryCodePicker
    lateinit var wp_country_code: CountryCodePicker
    lateinit var radioCustomer: RadioButton
    lateinit var radioEstate: RadioButton
    lateinit var llAddress: LinearLayout
    lateinit var etAddress: EditText
    lateinit var spinnerCity: AppCompatSpinner
    lateinit var spinnerNbhd: AppCompatSpinner
    var dateUri: Uri? = null

    var files: File? = null

    lateinit var ll_nested:LinearLayout
    lateinit var et_call_number:EditText
    lateinit var et_whatsapp_number:EditText
    lateinit var tvNameHeader:TextView
    lateinit var tvPhotoHeader:TextView
    lateinit var llTypes:LinearLayout
    lateinit var tvManagerHeader:TextView
    lateinit var etManager:EditText
    lateinit var tv_check_box:CheckBox
    var callCountryCode = "+964"
    var wpCountryCode = "+964"
    var fcmUser : FirebaseUser ?= null

    var cityList:ArrayList<NBHDDataResponse> = ArrayList()
    var nbhdList:ArrayList<NBHDArea> = ArrayList()
    var cityId : String ?= ""
    var nbhdId : String ?= ""
    var login_type : String = ""

    @Throws(IllegalStateException::class, IOException::class)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        adjustFontScale(resources.configuration)
        getHashKey()
        inits()
        clickListeners()

        login_type = intent.getStringExtra("login_type").orEmpty()

        llTypes.visibility = View.GONE
        llAddress.visibility = View.GONE
        tvPhotoHeader.visibility = View.GONE
        tvManagerHeader.visibility = View.GONE
        etManager.visibility = View.GONE
        tvNameHeader.text = getString(R.string.name_comp)
        et_name.hint = getString(R.string.enter_name)

        //radioCustomer.performClick()
    }


    private fun inits(){
        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("128895926326-1h73clo97ccgeu7jq57lrtncablhcbv2.apps.googleusercontent.com")
            .requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);
        val account = GoogleSignIn.getLastSignedInAccount(this)

        img_back = findViewById(R.id.img_back)
        ll_nested = findViewById(R.id.ll_nested)
        call_country_code = findViewById(R.id.call_country_code)
        wp_country_code = findViewById(R.id.wp_country_code)
        rl_upload_image = findViewById(R.id.rl_upload_image)
        img_resgister = findViewById(R.id.img_resgister)
        et_name = findViewById(R.id.et_name)
        rl_google_login = findViewById(R.id.rl_google_login)
        rl_fb_login = findViewById(R.id.rl_fb_login)
        rl_wp_login = findViewById(R.id.rl_wp_login)
        et_email = findViewById(R.id.et_email)
        fb_loginBtn = findViewById(R.id.fb_loginBtn)
        btn_register = findViewById(R.id.btn_register)
        et_cnf_password = findViewById(R.id.et_cnf_password)
        et_password = findViewById(R.id.et_password)
        et_whatsapp_number = findViewById(R.id.et_whatsapp_number)
        et_call_number = findViewById(R.id.et_call_number)
        tv_check_box = findViewById(R.id.tv_check_box)
        radioCustomer = findViewById(R.id.radioCustomer)
        radioEstate = findViewById(R.id.radioEstate)
        llAddress = findViewById(R.id.llAddress)
        etAddress = findViewById(R.id.etAddress)
        spinnerCity = findViewById(R.id.spinnerCity)
        spinnerNbhd = findViewById(R.id.spinnerNbhd)
        tvNameHeader = findViewById(R.id.tvNameHeader)
        tvPhotoHeader = findViewById(R.id.tvPhotoHeader)
        tvManagerHeader = findViewById(R.id.tvManagerHeader)
        etManager = findViewById(R.id.etManager)
        llTypes = findViewById(R.id.llTypes)

    }

    private fun clickListeners(){
        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                cityId = cityList[position].slug
                nbhdList.clear()
                if (!cityList[position].area.isNullOrEmpty()){
                    nbhdList.addAll(cityList[position].area)
                }
                spinnerNbhd.adapter = SpinnerNBHDAdapter(this@RegisterActivity, nbhdList)
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

        call_country_code.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                callCountryCode =  call_country_code.getSelectedCountryCodeWithPlus()
                (call_country_code.getSelectedCountryCodeWithPlus())
            }

        })

        wp_country_code.setOnCountryChangeListener(object :CountryCodePicker.OnCountryChangeListener{
            override fun onCountrySelected() {
                wpCountryCode =  wp_country_code.getSelectedCountryCodeWithPlus()
                (wp_country_code.getSelectedCountryCodeWithPlus())
            }

        })

        /*call_country_code.setOnCountryChangeListener {
            callCountryCode = "+" + it.phoneCode
        }

        wp_country_code.setOnCountryChangeListener {
            wpCountryCode = "+" + it.phoneCode
        }*/

        tv_check_box.setOnCheckedChangeListener { buttonView, isChecked ->
            if(isChecked){
                // if(et_call_number.text.toString().isNotEmpty()){
                et_whatsapp_number.setText(et_call_number.text.toString())
                wp_country_code.setCountryForPhoneCode(callCountryCode.toInt())
                //  }
            }else{

            }
        }

        ll_nested.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return true
            }
        })

        rl_wp_login.setOnClickListener {
            dismissKeyboard(rl_wp_login)
            var intent = Intent(this, WhatsappLoginActivity::class.java)
            intent.putExtra("login_type", login_type)
            startActivity(intent)
            overridePendingTransition(0,0)
        }

        et_call_number.addTextChangedListener(object :TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if(tv_check_box.isChecked){
                    et_whatsapp_number.setText(et_call_number.text.toString())
                }
                try {
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

        et_whatsapp_number.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (et_whatsapp_number.text.toString().isNotEmpty()
                        && et_whatsapp_number.text.toString().substring(0,1) == "0"){
                        et_whatsapp_number.setText(et_whatsapp_number.text.toString().replace("0",""))
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })

        btn_register.setOnClickListener {
            dismissKeyboard(btn_register)
            if (et_name.text.toString().trim().isEmpty()) {
                et_name.error = getString(R.string.please_enter_name)
            } else if (et_email.text.toString().trim()
                    .isEmpty() || !emailValidate(et_email.text.toString().trim())
            ) {
                et_email.error = getString(R.string.enter_valid_email)
            }
            else if(et_call_number.text.toString().trim().isNotEmpty() && et_call_number.text.toString().substring(0,1) == "0"){
                et_call_number.error = getString(R.string.enter_call_number_without_zero)
            }
            else if(et_whatsapp_number.text.toString().trim().isNotEmpty() && et_whatsapp_number.text.toString().substring(0,1) == "0"){
                et_whatsapp_number.error = getString(R.string.enter_whatsapp_number_without_zero)
            }
            else if(radioEstate.isChecked && cityId.isNullOrEmpty()){
                showToast(this,getString(R.string.select_jurisdiction_comp))
            }
            else if(radioEstate.isChecked && nbhdId.isNullOrEmpty()){
                showToast(this,getString(R.string.choose_the_neighborhood))
            }
            else if(radioEstate.isChecked && etAddress.text.toString().trim().isEmpty()){
                etAddress.error = getString(R.string.enter_company_address)
            }
            else if (et_password.text.toString().isEmpty()) {
                et_password.error = getString(R.string.enter_valid_password)
            } else if (et_cnf_password.text.toString().isEmpty()) {
                et_cnf_password.error = getString(R.string.enter_valid_cnf_password)
            } else if (!et_password.text.toString().equals(et_cnf_password.text.toString())) {
                et_cnf_password.error = getString(R.string.password_match_error)
            } else {
                if (isNetworkAvailable()){
                    var body:MultipartBody.Part? = null
                    if(files!=null){
                        val requestFile = files!!.asRequestBody("image/png".toMediaTypeOrNull())
                        body = MultipartBody.Part.createFormData(
                            "houzez_file_data_name",
                            files!!.name + ".png",
                            requestFile )

                    }

                    /*val type = if (radioEstate.isChecked) {
                        "agency"
                    } else {
                        "customer"
                    }*/

                    val typebody : RequestBody = login_type.toRequestBody("text/plain".toMediaTypeOrNull())
                    val namebody: RequestBody =
                        et_name.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val email: RequestBody =
                        et_email.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val password: RequestBody = et_password.text.toString().toRequestBody("text/plain".toMediaTypeOrNull())
                    val userCallNumber = callCountryCode +  et_call_number.text.toString()
                    val callNumber: RequestBody =userCallNumber.toRequestBody("text/plain".toMediaTypeOrNull())

                    val userWhatsappNumber = wpCountryCode +  et_whatsapp_number.text.toString()
                    val whatsappNumber: RequestBody = userWhatsappNumber.toRequestBody("text/plain".toMediaTypeOrNull())
                    val loginType = "simple"
                    val loginTypeBody: RequestBody = loginType.toRequestBody("text/plain".toMediaTypeOrNull())
                    val addressBody: RequestBody = etAddress.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                    val cityBody: RequestBody = cityId.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                    val nbhdBody: RequestBody = nbhdId.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())
                    val managerBody: RequestBody = etManager.text.toString().trim().toRequestBody("text/plain".toMediaTypeOrNull())

                    val map: HashMap<String, RequestBody> = HashMap()
                    map["first_name"] = namebody
                    map["last_name"] = namebody
                    map["email"] = email
                    map["password"] = password
                    map["username"] = email
                    if (et_call_number.text.isNotEmpty()){
                        map["call_number"] = callNumber
                    }
                    if (et_whatsapp_number.text.isNotEmpty()){
                        map["whatsapp_number"] = whatsappNumber
                    }
                    map["login_type"] = loginTypeBody
                    map["role_type"] = typebody
                    if (login_type == "agency"){
                        map["address"] = addressBody
                        map["city_slug"] = cityBody
                        map["nbhd_slug"] = nbhdBody
                        map["manager_name"] = managerBody
                    }

                    hitMultipartApi(Constants.REGISTER, true, Constants.REGISTER_API, map, body)
                } else{
                    showToast(this, resources.getString(R.string.intenet_error))
                }
            }
        }


        img_back.setOnClickListener { finish() }
        rl_upload_image.setOnClickListener {
            dismissKeyboard(rl_upload_image)
            openImagePicker()
        }

        rl_google_login.setOnClickListener {
            dismissKeyboard(rl_google_login)
            if (isNetworkAvailable()){
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            } else{
                showToast(this, resources.getString(R.string.intenet_error))
            }

        }

        rl_fb_login.setOnClickListener {
            dismissKeyboard(rl_fb_login)
            if (isNetworkAvailable()){
                fb_loginBtn.performClick()
            } else{
                showToast(this, resources.getString(R.string.intenet_error))
            }
        }

        fb_loginBtn.setOnClickListener {
            dismissKeyboard(fb_loginBtn)
            if(progressHUD !=null){
                progressHUD!!.show()
            }else{
                progressHUD = ProgressHud.show(
                    this,
                    false,
                    false
                )
            }
            mCallbackManager = CallbackManager.Factory.create()
            fb_loginBtn.setPermissions("email", "public_profile")
            fb_loginBtn.authType = "rerequest"
            fb_loginBtn.registerCallback(
                mCallbackManager!!, object : FacebookCallback<LoginResult> {
                    override fun onCancel() {
                        Log.d("Cancel", "Canceled")
                        if(progressHUD !=null){
                            progressHUD!!.dismiss()
                        }
                    }

                    override fun onError(error: FacebookException) {
                        Log.d("Erorrfb", error!!.message.toString())
                        if(progressHUD !=null){
                            progressHUD!!.dismiss()
                        }
                    }

                    override fun onSuccess(result: LoginResult) {
                        Log.d("SucessData", result.toString())
                        handleFacebookAccessToken(result!!.accessToken)
                    }

                }
            )

            LoginManager.getInstance().registerCallback(mCallbackManager,
                object : FacebookCallback<LoginResult> {
                    override fun onSuccess(result: LoginResult) {
                        Log.d("SucessData", result.toString())

                        handleFacebookAccessToken(result!!.accessToken)
                    }

                    override fun onCancel() {
                        Log.d("Cancel", "Canceled")
                        if(progressHUD !=null){
                            progressHUD!!.dismiss()
                        }
                    }


                    override fun onError(error: FacebookException) {
                        Log.d("Erorrfb", error!!.message.toString())
                        if(progressHUD !=null){
                            progressHUD!!.dismiss()
                        }
                    }
                })
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.REGISTER)) {
            if (respopnse.get(Constants.STATUS).asBoolean) {
                val loginModel = Gson().fromJson(respopnse,LoginModel::class.java)
                /*var intent: Intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
                overridePendingTransition(0, 0)*/
                if (loginModel.message == null){
                    val intent: Intent = Intent(this, LoginActivity::class.java)
                    intent.putExtra("login_type", login_type)
                    startActivity(intent)
                    finish()
                    overridePendingTransition(0, 0)
                } else {
                    Utility.showToast(this, loginModel.message.toString())
                }
            }
        }else if(apiType.equals(Constants.SOCIAL_LOGIN)){
            val loginModel = Gson().fromJson(respopnse, LoginModel::class.java)
            if(loginModel.success){
                PreferencesService.instance.saveUserProfile(loginModel.response)
                PreferencesService.instance.saveUserLoginStatus(true)
                PreferencesService.instance.saveUserLoginMethod("social")
                val intent:Intent = Intent(this,HomeActivity::class.java)
                startActivity(intent)
                finishAffinity()
                overridePendingTransition(0,0)
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
        }  else if (apiType == Constants.CHECK_SOCIAL_LOGIN){

            /*val responseModel = Gson().fromJson(respopnse, CheckSocialLoginModel::class.java)

            if (responseModel.success == true){

                val displayName = fcmUser!!.displayName
                val photoUrl = fcmUser!!.photoUrl.toString()
                val phoneNumber = fcmUser!!.phoneNumber
                val uID = fcmUser!!.uid
                var email = fcmUser!!.email

                if (email.isNullOrEmpty() || email == "null") {
                    try {
                        email = fcmUser!!.providerData[1].email
                    } catch (e: Exception) {
                        e.message
                    }
                }

                if (responseModel.response == true){
                    val map: HashMap<String, String> = HashMap()
                    if (!email.isNullOrEmpty()) {
                        map["email"] = email
                    }
                    if (!phoneNumber.isNullOrEmpty()) {
                        map["phone"] = phoneNumber
                    }
                    map["source"] = "phone"
                    map["user_id"] = uID
                    map["login_type"] = "google"
                    map["profile_url"] = photoUrl
                    map["display_name"] = displayName.toString()
                    map["device_token"] = firebaseDeviceToken.toString()
                    map["role_type"] = if (responseModel.role_type == "houzez_agency") {
                        "agency"
                    } else {
                        "customer"
                    }
                    hitPostApi(Constants.SOCIAL_LOGIN, true, Constants.SOCIAL_LOGIN_API, map)
                } else {

                    val model = SocialModel(
                        email = email,
                        phone = phoneNumber,
                        source = "phone",
                        user_id = uID,
                        login_type = "google",
                        profile_url = photoUrl,
                        display_name = displayName,
                        device_token = firebaseDeviceToken
                    )

                    val intent = Intent(this, WhatsappRegisterScreen::class.java)
                    intent.putExtra("socialModel", Gson().toJson(model))
                    startActivity(intent)
                }
            } else {
                try {
                    FirebaseAuth.getInstance().signOut()
                    LoginManager.getInstance().logOut()
                } catch (e : Exception){
                    println(e.localizedMessage)
                }

            }*/
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
            }
        }

    }

    private fun openImagePicker() {
      //  isPermissionAsked = false
      //  if (hasPermissions(*PERMISSIONS)) {
            val layoutInflater = LayoutInflater.from(this)
            val customView = layoutInflater.inflate(R.layout.image_picker_choose_dialog, null)
            val lytCameraPick:LinearLayout = customView.findViewById(R.id.lytCameraPick)
            val lytGalleryPick:LinearLayout = customView.findViewById(R.id.lytGalleryPick)

            val dialog = AlertDialog.Builder(this)
                .setTitle(R.string.choose_option)
                .setView(customView)
                .setNegativeButton(R.string.option_cancel) {_,_->
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
           /* ImagePicker.with(this)
                .compress(1024)			//Final image size will be less than 1 MB(Optional)
                .maxResultSize(1080, 1080)	//Final image resolution will be less than 1080 x 1080(Optional)
                .start()*/
            //  openImagePickDialog()
       /* } else {
            ActivityCompat.requestPermissions(this, PERMISSIONS, PERMISSION_ALL)
        }*/
    }

    private fun openImagePickDialog() {
        val dialog = Dialog(this@RegisterActivity)
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.setContentView(R.layout.dialog_image_option)
        val btn_gallery = dialog.findViewById<Button>(R.id.btn_gallery)
        val btn_camera = dialog.findViewById<Button>(R.id.btn_camera)
        val imv_dismiss = dialog.findViewById<ImageView>(R.id.imv_dismiss)
        imv_dismiss.setOnClickListener { dialog.dismiss() }
        btn_camera.setOnClickListener {
            dialog.dismiss()
            openCamera()
        }
        btn_gallery.setOnClickListener {
            dialog.dismiss()
            openGallery()
        }
        dialog.show()
    }

    private fun openCamera() {
        val cameraIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        startActivityForResult(cameraIntent, CAMERA_REQUEST)
    }

    private fun openGallery() {
        val photoPickerIntent = Intent()
        photoPickerIntent.type = "image/*"
        photoPickerIntent.action = Intent.ACTION_GET_CONTENT
        photoPickerIntent.putExtra(Intent.EXTRA_ALLOW_MULTIPLE, false)
        photoPickerIntent.putExtra(Intent.EXTRA_MIME_TYPES, mimeTypes)
        startActivityForResult(photoPickerIntent, PICK_IMAGE_REQUEST_CODE)
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
                        .setNegativeButton(R.string.option_cancel) { _, _ -> }
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

    private fun checkFileType(path: String?): Boolean {
        return if (path != null && (path == "image/jpg" || path == "image/jpeg" || path == "image/png")) {
            true
        } else {
            false
        }
    }


    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        try {
            mCallbackManager!!.onActivityResult(requestCode, resultCode, data);
        } catch (e: java.lang.Exception) {
            Log.d("facebookLoginError", e.message.toString())
        }
        if(requestCode == ImagePicker.REQUEST_CODE){
            if (resultCode == Activity.RESULT_OK) {
                var imageUri = data?.data!!
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
                Toast.makeText(this, "Task Cancelled", Toast.LENGTH_SHORT).show()
            }
        }

        /*if (requestCode == PICK_IMAGE_REQUEST_CODE && resultCode == RESULT_OK) {
            if (data!!.clipData != null) {
                val cout = data.clipData!!.itemCount
                for (i in 0 until cout) {
                    val uri = data.clipData!!.getItemAt(i).uri
                    if (uri != null) {
                        val mimeType = contentResolver.getType(uri)
                        if (checkFileType(mimeType)) {
                            var bMap: Bitmap? = null
                            try {
                                bMap = MediaStore.Images.Media.getBitmap(this.contentResolver, uri)
                            } catch (e: IOException) {
                                e.printStackTrace()
                            }
                            val photoFile = bitmapToFile(bMap, currentdateImageName)
                            files = photoFile
                            val uriData = Uri.fromFile(photoFile)
                            img_resgister.setImageURI(uriData)
                            dateUri = uriData

                        } else {
                            Toast.makeText(
                                this,
                                getString(R.string.some_selected_files_not_supported),
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            } else {
                val mimeType = contentResolver.getType(data.data!!)
                if (checkFileType(mimeType)) {
                    var bMap: Bitmap? = null
                    try {
                        bMap = MediaStore.Images.Media.getBitmap(this.contentResolver, data.data)
                    } catch (e: IOException) {
                        e.printStackTrace()
                    }
                    val photoFile = bitmapToFile(bMap, currentdateImageName)
                    files = photoFile
                    val uriData = Uri.fromFile(photoFile)
                    img_resgister.setImageURI(uriData)
                    dateUri = uriData
                } else {
                    Toast.makeText(
                        this,
                        getString(R.string.some_selected_files_not_supported),
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        } else if (requestCode == CAMERA_REQUEST && resultCode == RESULT_OK) {
            val bitmap = data!!.extras!!["data"] as Bitmap?
            val photoFile = bitmapToFile(bitmap, currentdateImageName)
            files = photoFile
            val uri = Uri.fromFile(photoFile)
            img_resgister.setImageURI(uri)
            dateUri = uri
        }*/

        if (requestCode == GOOGLE_SIGN_IN) {
            /*val task: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
            handleSignInResult(task)*/

            val accountTask: Task<GoogleSignInAccount> =
                GoogleSignIn.getSignedInAccountFromIntent(data)
            try {
                val account: GoogleSignInAccount = accountTask.getResult(ApiException::class.java)
                firebaseAuthWithGoogleAccount(account)

            } catch (e: Exception) {
                Log.d("Google_Login_Error", e.message.toString())
            }
        }


    }

    private fun firebaseAuthWithGoogleAccount(account: GoogleSignInAccount) {
        val credential: AuthCredential = GoogleAuthProvider.getCredential(account.idToken, null)
        firebaseAuth.signInWithCredential(credential)
            .addOnSuccessListener {
                fcmUser = firebaseAuth.currentUser

                /*val map : HashMap<String, String> = HashMap()
                map["user_id"] = fcmUser?.uid.orEmpty()

                hitPostApi(Constants.CHECK_SOCIAL_LOGIN, true, Constants.CHECK_SOCIAL_LOGIN_API, map)*/

                val displayName = fcmUser!!.displayName
                val photoUrl = fcmUser!!.photoUrl.toString()
                val phoneNumber = fcmUser!!.phoneNumber
                val uID = fcmUser!!.uid
                var email = fcmUser!!.email

                if (email.isNullOrEmpty() || email == "null") {
                    try {
                        email = fcmUser!!.providerData[1].email
                    } catch (e: Exception) {
                        e.message
                    }
                }

                val map: HashMap<String, String> = HashMap()
                if (!email.isNullOrEmpty()) {
                    map["email"] = email
                }
                if (!phoneNumber.isNullOrEmpty()) {
                    map["phone"] = phoneNumber
                }
                map["source"] = "phone"
                map["user_id"] = uID
                map["login_type"] = "google"
                map["profile_url"] = photoUrl
                map["display_name"] = displayName.toString()
                map["device_token"] = firebaseDeviceToken.toString()
                map["role_type"] = "customer"
                hitPostApi(Constants.SOCIAL_LOGIN, true, Constants.SOCIAL_LOGIN_API, map)
            }
            .addOnFailureListener { e ->
                Log.d("Google_Login_Failure", e.message.toString())
            }
    }

    private fun handleSignInResult(completedTask: Task<GoogleSignInAccount>) {
        try {
            val account: GoogleSignInAccount = completedTask.getResult(ApiException::class.java)
            val acct = GoogleSignIn.getLastSignedInAccount(this)
            if (acct != null) {
                val personName = acct.displayName
                val personGivenName = acct.givenName
                val personFamilyName = acct.familyName
                val personEmail = acct.email
                val personId = acct.id
                val personPhoto: Uri? = acct.photoUrl


                val prefs: SharedPreferences.Editor =
                    getSharedPreferences("NAJAF_LOGIN", Context.MODE_PRIVATE).edit()
                prefs.putBoolean("isLoggedIn", true)
                prefs.putString("name", acct.displayName)
                prefs.putString("email", acct.email)
                prefs.putString("photo", acct.photoUrl.toString())
                prefs.apply()

                startActivity(Intent(this@RegisterActivity, HomeActivity::class.java))
            }
        } catch (e: ApiException) {
            Log.d("failed", e.localizedMessage.toString())
        }
    }

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        idToken = token.token
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser!!
                    var providerId = firebaseUser.providerData[1].uid
                    val email = firebaseUser.email
                    val phoneNumber = firebaseUser.phoneNumber
                    val photo = firebaseUser.photoUrl.toString()
                    val displayName = firebaseUser.displayName
                    val uid = firebaseUser.uid

                    val map:HashMap<String,String> = HashMap()
                    if(!email.isNullOrEmpty()){
                        map["email"] = email
                    }
                    if(phoneNumber!=null && phoneNumber.isNotEmpty() ){
                        map["phone"] = phoneNumber
                    }
                    map["source"] = "phone"
                    map["user_id"] = uid
                    map["profile_url"] = photo
                    map["display_name"] = displayName!!
                    map["login_type"] = "facebook"
                    map["device_token"] = firebaseDeviceToken.toString()
                    hitSocialPostApi(Constants.SOCIAL_LOGIN,false,Constants.SOCIAL_LOGIN_API,map)

                } else {
                    if(progressHUD !=null){
                        progressHUD!!.dismiss()
                    }
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
                    Log.w(">>>", "signInWithCredential:failure", task.exception)
                    //facebokAuthFaled
                }

            }
    }

    private fun getHashKey() {
        try {
            val info: PackageInfo = this.packageManager
                .getPackageInfo(this.packageName, PackageManager.GET_SIGNATURES)
            for (signature in info.signatures!!) {
                val md: MessageDigest = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val hashKey = String(Base64.encode(md.digest(), 0))
                Log.i("HashKeys", "printHashKey() Hash Key: $hashKey")
            }
        } catch (e: NoSuchAlgorithmException) {
            Log.e("Erorr", "printHashKey()", e)
        } catch (e: Exception) {
            Log.e("Erorr2", "printHashKey()", e)
        }
    }
}