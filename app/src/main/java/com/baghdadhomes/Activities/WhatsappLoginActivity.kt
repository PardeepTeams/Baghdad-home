package com.baghdadhomes.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import com.facebook.login.LoginManager
import com.github.ybq.android.spinkit.SpinKitView
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.FirebaseException
import com.google.firebase.FirebaseTooManyRequestsException
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.hbb20.CountryCodePicker
import com.baghdadhomes.Models.CheckSocialLoginModel
import com.baghdadhomes.Models.LoginModel
import com.baghdadhomes.Models.SendOtpResponse
import com.baghdadhomes.Models.SocialModel
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import `in`.aabhasjindal.otptextview.OtpTextView
import pl.droidsonroids.gif.GifImageView
import java.util.*
import java.util.concurrent.TimeUnit
import com.baghdadhomes.R

class WhatsappLoginActivity : BaseActivity() {
    var countryCode = "+964"
    lateinit var et_whatsapp_no: EditText
    lateinit var country_code: CountryCodePicker
    lateinit var auth: FirebaseAuth
    lateinit var phoneCredential: PhoneAuthCredential
    var mVerificationId: String = ""
    var verificationCode: String = ""
    lateinit var resendToken: PhoneAuthProvider.ForceResendingToken
    lateinit var callbacks:PhoneAuthProvider.OnVerificationStateChangedCallbacks

    lateinit var btn_continue: Button
    lateinit var linear_phone: LinearLayout
    lateinit var rl_otp_detext: RelativeLayout
    lateinit var title_whatsapp: TextView

    lateinit var spin_kit: SpinKitView
    lateinit var imageView_progress: GifImageView
    lateinit var back_arrow_iv:ImageView
    lateinit var llSocial:LinearLayout
    lateinit var rl_google_login:RelativeLayout

    lateinit var et_otp: OtpTextView

    lateinit var btn_otp_continue:TextView
    lateinit var resend_otp:TextView
    private var login_type : String = ""

    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var fcmUser : FirebaseUser ?= null

    lateinit var mGoogleSignInClient: GoogleSignInClient
    var GOOGLE_SIGN_IN: Int = 100
    var otp : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_whatsapp_login)
        adjustFontScale(resources.configuration)
        auth = FirebaseAuth.getInstance();
        et_whatsapp_no = findViewById(R.id.et_whatsapp_no)
        country_code = findViewById(R.id.country_code)
        btn_continue = findViewById(R.id.btn_continue)
        linear_phone = findViewById(R.id.linear_phone)
        rl_otp_detext = findViewById(R.id.rl_otp_detext)
        title_whatsapp = findViewById(R.id.title_whatsapp)
        spin_kit = findViewById(R.id.spin_kit)
        imageView_progress = findViewById(R.id.imageView_progress)
        back_arrow_iv = findViewById(R.id.back_arrow_iv)
        btn_otp_continue = findViewById(R.id.btn_otp_continue)
        et_otp = findViewById(R.id.et_otp)
        resend_otp  = findViewById(R.id.resend_otp)
        llSocial  = findViewById(R.id.llSocial)
        rl_google_login  = findViewById(R.id.rl_google_login)

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("128895926326-1h73clo97ccgeu7jq57lrtncablhcbv2.apps.googleusercontent.com")
            .requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso)

        login_type = intent.getStringExtra("login_type").orEmpty()
        if (login_type == "agency"){
            llSocial.visibility = View.VISIBLE
        } else {
            llSocial.visibility = View.GONE
        }

        back_arrow_iv.setOnClickListener {
            finish()
        }

        et_whatsapp_no.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    if (et_whatsapp_no.text.toString().isNotEmpty()
                        && et_whatsapp_no.text.toString().substring(0,1) == "0"){
                        et_whatsapp_no.setText(et_whatsapp_no.text.toString().replace("0",""))
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            }

            override fun afterTextChanged(p0: Editable?) {

            }
        })
        resend_otp.setOnClickListener {
            /*imageView_progress.visibility = View.VISIBLE
            getResendOTP(countryCode + et_whatsapp_no.text.toString())*/
            sendOtpApi()
        }
        btn_otp_continue.setOnClickListener {
            if (et_otp.otp.toString().isEmpty()) {
                Utility.showToast(this, getString(R.string.enter_otp))
            } else {
                /*if (isNetworkAvailable()){
                    imageView_progress.visibility = View.VISIBLE
                    val credential: PhoneAuthCredential = PhoneAuthProvider.getCredential(
                        mVerificationId, et_otp.otp.toString().trim()
                    )
                    signInWithPhoneAuthCredential(credential)
                } else{
                    showToast(this, resources.getString(R.string.intenet_error))
                }*/
                if (et_otp.otp.toString() == otp.orEmpty()){
                    if (isNetworkAvailable()){
                        hitWhatsappLoginApi()
                    } else{
                        showToast(this, resources.getString(R.string.intenet_error))
                    }
                } else {
                    Utility.showToast(this, getString(R.string.valid_otp))
                }
            }
        }

        country_code.setOnCountryChangeListener {
            countryCode = country_code.selectedCountryCodeWithPlus
        }

        btn_continue.setOnClickListener {
            if (et_whatsapp_no.text.toString().isEmpty()) {
                et_whatsapp_no.setError(getString(R.string.please_enter_number))
            } else if (et_whatsapp_no.text.toString().substring(0,1) == "0") {
                et_whatsapp_no.setError(getString(R.string.enter_whatsapp_number_without_zero))
            } else {
                /*imageView_progress.visibility = View.VISIBLE
                getOTP(countryCode + et_whatsapp_no.text.toString())*/

                sendOtpApi()

                /*countryCode = "+91"
                hitWhatsappLoginApi()*/
            }
        }


        callbacks  = object : PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            override fun onVerificationCompleted(credential: PhoneAuthCredential) {
                //  spin_kit.visibility = View.GONE
                imageView_progress.visibility = View.GONE
                Log.d("TAG", "onVerificationCompleted:$credential")
                verificationCode = credential.getSmsCode()!!
                phoneCredential = credential
                setUI()
            }

            override fun onVerificationFailed(e: FirebaseException) {
                Log.w("TAG", "onVerificationFailed", e)
                Utility.showToast(this@WhatsappLoginActivity,e.message.toString())
                // spin_kit.visibility = View.GONE
                imageView_progress.visibility = View.GONE
                if (e is FirebaseAuthInvalidCredentialsException) {
                    // Invalid request
                    Log.w("TAG", "onVerificationFailed", e)
                } else if (e is FirebaseTooManyRequestsException) {
                    // The SMS quota for the project has been exceeded
                    Log.w("TAG", "onVerificationFailed", e)
                }
            }

            override fun onCodeSent(
                verificationId: String,
                token: PhoneAuthProvider.ForceResendingToken,
            ) {
                // spin_kit.visibility = View.GONE
                imageView_progress.visibility = View.GONE
                resendToken = token
                Utility.showToast(this@WhatsappLoginActivity,getString(R.string.otp_send))

                var storedVerificationId = verificationId
                mVerificationId = verificationId

                var resendToken = token
                setUI()
            }
        }

        rl_google_login.setOnClickListener {
            dismissKeyboard(rl_google_login)
            if (isNetworkAvailable()){
                val signInIntent = mGoogleSignInClient.signInIntent
                startActivityForResult(signInIntent, GOOGLE_SIGN_IN)
            }else{
                showToast(this, resources.getString(R.string.intenet_error))
            }
        }
    }

    private fun sendOtpApi(){
        if (isNetworkAvailable()){
            /*{
                "recipient":"9647502171212",
                "sender_id":"SenderID",
                "type":"whatsapp",
                "message":"123654",
                "lang":"en"
            }*/

            otp = getRandomNumberString()
            Log.d("OTP", otp!!);

            val map : HashMap<Any, Any> = HashMap()
            map["recipient"] = "964${et_whatsapp_no.text}"
            //map["sender_id"] = "IraqVin"
            map["sender_id"] = "Najaf Home"
            map["type"] = "whatsapp"//"plain"
            map["message"] = "Your OTP Code is $otp"
            map["lang"] = "en"
            hitSendOtpApi(
                url = Constants.SEND_SMS_OTP_API,
                type = Constants.SEND_SMS_OTP,
                showLoader = true,
                token = Constants.SEND_SMS_OTP_TOKEN,
                map = map
            )
        } else {
            showToast(this,getString(R.string.intenet_error))
        }
    }

    private fun getRandomNumberString(): String {
        // It will generate 6 digit random Number.
        // from 0 to 999999
        val number: Int = Random().nextInt(999999)

        // this will convert any number sequence into 6 character.
        val roundNumber = String.format("%06d", number)
        val localeNumber = convertToEnglishNumber(roundNumber)
        return localeNumber
    }

    private fun convertToEnglishNumber(arabicNumber: String): String {
        val englishNumbers = arrayOf("0", "1", "2", "3", "4", "5", "6", "7", "8", "9")
        val arabicNumbers = arrayOf("٠", "١", "٢", "٣", "٤", "٥", "٦", "٧", "٨", "٩")
        val map = arabicNumbers.zip(englishNumbers).toMap()

        val englishNumber = StringBuilder()
        for (char in arabicNumber) {
            englishNumber.append(map[char.toString()] ?: char)
        }

        return englishNumber.toString()
    }

    fun getOTP(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this)                 // Activity (for callback binding)
            .setCallbacks(callbacks)           // OnVerificationStateChangedCallbacks
            .build()
        PhoneAuthProvider.verifyPhoneNumber(options)
    }

    fun getResendOTP(phoneNumber: String) {
        val options = PhoneAuthOptions.newBuilder(auth)
            .setPhoneNumber(phoneNumber)       // Phone number to verify
            .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
            .setActivity(this) // Activity (for callback binding)
            .setCallbacks(callbacks)
            .setForceResendingToken(resendToken).build()
        PhoneAuthProvider.verifyPhoneNumber(options)

    }
    private fun signInWithPhoneAuthCredential(credential: PhoneAuthCredential) {
        auth!!.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    Log.d("otpSend","otpSend")
                    // spin_kit.visibility = View.GONE
                    imageView_progress.visibility = View.GONE
                    val user = task.result?.user
                    hitWhatsappLoginApi()
                    /*   var intent:Intent = Intent(this,WhatsappRegisterScreen::class.java)
                       intent.putExtra("phone",phoneString)
                       intent.putExtra("password",et_otp.otp.toString())
                       startActivity(intent)*/
                } else {
                    //spin_kit.visibility = View.GONE
                    imageView_progress.visibility = View.GONE
                    Log.w("TAG", "signInWithCredential:failure", task.exception)
                    if (task.exception is FirebaseAuthInvalidCredentialsException) {
                        Utility.showToast(this, getString(R.string.valid_otp))
                    }
                    // Update UI
                }
            }
    }

    private fun hitWhatsappLoginApi(){
        val phoneString = countryCode + et_whatsapp_no.text.toString()
        val map:HashMap<String,String> = HashMap()
        map.put("phone", phoneString)
        map.put("login_type","whatsapp")
        map.put("device_token",firebaseDeviceToken.toString())
        hitPostApi(Constants.CHECK_WHATSAPP_LOGIN,true,Constants.CHECK_WHATSAPP_LOGIN_API,map)
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if(apiType.equals(Constants.CHECK_WHATSAPP_LOGIN)){
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
              //  overridePendingTransition(0,0)
            }else{
                val intent:Intent = Intent(this,WhatsappRegisterScreen::class.java)
                val phoneString = countryCode + et_whatsapp_no.text.toString()
                intent.putExtra("phone",phoneString)
                intent.putExtra("password",et_otp.otp.toString())
                intent.putExtra("login_type",login_type)
                startActivity(intent)
                finish()
            }

        }  else if (apiType == Constants.CHECK_SOCIAL_LOGIN){

            val responseModel = Gson().fromJson(respopnse, CheckSocialLoginModel::class.java)

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
                    if (!responseModel?.allowed_ads.isNullOrEmpty()){
                        map["allowed_ads"] = responseModel?.allowed_ads.toString()
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
                    intent.putExtra("login_type", login_type)
                    startActivity(intent)
                }
            } else {
                try {
                    FirebaseAuth.getInstance().signOut()
                    LoginManager.getInstance().logOut()
                } catch (e : Exception){
                    println(e.localizedMessage)
                }

            }
        } else if(apiType.equals(Constants.SOCIAL_LOGIN)){
            val loginModel = Gson().fromJson(respopnse,LoginModel::class.java)
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
        } else if (apiType.equals(Constants.SEND_SMS_OTP, true)){
            val model = Gson().fromJson(respopnse, SendOtpResponse::class.java)
            if (model.status == "success"){
                setUI()
                showToast(this, getString(R.string.otp_send))
            } else {
                val message = if (!model.message.isNullOrEmpty()){
                    model.message
                } else {
                    getString(R.string.something_went_wrong)
                }
                showToast(this, message)
            }
        }
    }

    fun setUI(){
        linear_phone.visibility = View.GONE
        rl_otp_detext.visibility = View.VISIBLE
        title_whatsapp.setText(getString(R.string.verify_otp_title))
    }

    override fun onBackPressed() {
        if(linear_phone.visibility == View.GONE){
            linear_phone.visibility = View.VISIBLE
            rl_otp_detext.visibility = View.GONE
            title_whatsapp.setText(getString(R.string.whatsapp_login))
        }else{
            super.onBackPressed()
            overridePendingTransition(0,0)
        }

    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if(requestCode == GOOGLE_SIGN_IN){
            val accountTask: Task<GoogleSignInAccount> = GoogleSignIn.getSignedInAccountFromIntent(data)
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

                val map: HashMap<String, String> = HashMap()
                map["user_id"] = fcmUser?.uid.orEmpty()

              /*  val displayName = fcmUser!!.displayName
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

                //   val map: HashMap<String, String> = HashMap()
                if (!email.isNullOrEmpty()) {
                    map["email"] = email
                }
                if (!phoneNumber.isNullOrEmpty()) {
                    map["phone"] = phoneNumber
                }
                map["source"] = "phone"
                //   map["user_id"] = uID
                map["login_type"] = "google"
                map["profile_url"] = photoUrl
                map["display_name"] = displayName.toString()
                map["device_token"] = firebaseDeviceToken.toString()
                map["role_type"] = "agency"*//*if (role_type == "houzez_agency") {
                        "agency"
                    } else {
                        "customer"
                    }*//*
                *//* if (!responseModel?.allowed_ads.isNullOrEmpty()){
                        map["allowed_ads"] = responseModel?.allowed_ads.toString()
                    }*//*
                hitPostApi(Constants.SOCIAL_LOGIN, true, Constants.SOCIAL_LOGIN_API, map)*/

                  hitPostApi(Constants.CHECK_SOCIAL_LOGIN, true, Constants.CHECK_SOCIAL_LOGIN_API, map)
            }
            .addOnFailureListener { e ->
                Log.d("Google_Login_Failure", e.message.toString())
            }
    }

}