package com.baghdadhomes.Activities


import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.MotionEvent
import android.view.View
import android.widget.*
import com.facebook.AccessToken
import com.facebook.CallbackManager
import com.facebook.FacebookCallback
import com.facebook.FacebookException
import com.facebook.login.LoginManager
import com.facebook.login.LoginResult
import com.facebook.login.widget.LoginButton
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInAccount
import com.google.android.gms.auth.api.signin.GoogleSignInClient
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.common.api.ApiException
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.*
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Models.LoginModel
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.ProgressHud
import com.baghdadhomes.Utils.Utility
import com.baghdadhomes.R

class LoginActivity : BaseActivity() {
    lateinit var mGoogleSignInClient: GoogleSignInClient
    var GOOGLE_SIGN_IN: Int = 100
    lateinit var idToken: String
    var firebaseAuth: FirebaseAuth = FirebaseAuth.getInstance()
    var mCallbackManager: CallbackManager? = null

    lateinit var img_back: ImageView
    lateinit var ll_nested: LinearLayout
    lateinit var rl_google_login: RelativeLayout
    lateinit var rl_fb_login: RelativeLayout
    lateinit var rl_wp_login: RelativeLayout
    lateinit var fb_loginBtn: LoginButton
    lateinit var et_email_login:EditText
    lateinit var et_login_password:EditText
    lateinit var btn_login:TextView
    lateinit var tv_sign_up:TextView

    var fcmUser : FirebaseUser ?= null

    private var login_type : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(com.baghdadhomes.R.layout.activity_login)
        adjustFontScale(resources.configuration)

        img_back = findViewById(com.baghdadhomes.R.id.img_back)
        ll_nested = findViewById(com.baghdadhomes.R.id.ll_nested)
        rl_wp_login = findViewById(R.id.rl_wp_login)
        rl_google_login = findViewById(R.id.rl_google_login)
        rl_fb_login = findViewById(R.id.rl_fb_login)
        fb_loginBtn = findViewById(R.id.fb_loginBtn)
        tv_sign_up = findViewById(R.id.tv_sign_up)
        btn_login = findViewById(R.id.btn_login)
        et_email_login = findViewById(R.id.et_email_login)
        et_login_password = findViewById(R.id.et_login_password)

        login_type = intent.getStringExtra("login_type").orEmpty()

        val gso = GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
            .requestIdToken("128895926326-1h73clo97ccgeu7jq57lrtncablhcbv2.apps.googleusercontent.com")
            .requestEmail().build()
        mGoogleSignInClient = GoogleSignIn.getClient(this, gso);

        img_back.setOnClickListener {
            finish()
        }

        ll_nested.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return true
            }
        })

        tv_sign_up.setOnClickListener {
            dismissKeyboard(tv_sign_up)
            var intent = Intent(this, WhatsappLoginActivity::class.java)
            intent.putExtra("login_type", login_type)
            startActivity(intent)
            //startActivity(Intent(this, RegisterActivity::class.java))
            //overridePendingTransition(0,0)
        }

        rl_wp_login.setOnClickListener {
            dismissKeyboard(rl_wp_login)
            val intent = Intent(this, WhatsappLoginActivity::class.java)
            intent.putExtra("login_type", login_type)
            startActivity(intent)
            //startActivity(Intent(this, WhatsappLoginActivity::class.java))
            //overridePendingTransition(0,0)
        }

        btn_login.setOnClickListener {
            dismissKeyboard(btn_login)
            if(et_email_login.text.toString().trim().isEmpty() || !emailValidate(et_email_login.text.toString().trim())){
                et_email_login.error = getString(R.string.enter_valid_email)
            }else if(et_login_password.text.toString().trim().isEmpty()){
                et_login_password.error = getString(R.string.enter_valid_password)
            }else{
                if (isNetworkAvailable()){
                    val map:HashMap<String,String> = HashMap()
                    map.put("username",et_email_login.text.toString().trim())

                    map.put("password",et_login_password.text.toString().trim())
                    map.put("login_type","simple")
                    map.put("device_token",firebaseDeviceToken.toString())
                    hitPostApi(Constants.LOGIN,true,Constants.LOGIN_API,map)
                } else{
                    showToast(this, resources.getString(R.string.intenet_error))
                }

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

        rl_fb_login.setOnClickListener {
            dismissKeyboard(rl_fb_login)
            if (isNetworkAvailable()){
                fb_loginBtn.performClick()
            }else{
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
            mCallbackManager = CallbackManager.Factory.create();
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
        if(apiType.equals(Constants.LOGIN)){
            val loginModel = Gson().fromJson(respopnse,LoginModel::class.java)
            if(loginModel.success){
                try {
                    val mAuth :  FirebaseAuth = FirebaseAuth.getInstance()
                    mAuth.signInAnonymously().addOnCompleteListener(this) { task ->
                            if (task.isSuccessful) {
                                val user: FirebaseUser? = mAuth.currentUser
                                //Log.d("successData", "successData${user.toString()}")
                            } else {
                                //Log.d("successData", "Not Success: {task.exception}")
                            }
                        }
                } catch (e : Exception){
                    e.localizedMessage
                }
                if(loginModel.response!=null){
                    PreferencesService.instance.saveUserProfile(loginModel.response)
                    PreferencesService.instance.saveUserLoginStatus(true)
                    PreferencesService.instance.saveUserLoginMethod("simple")
                    val intent:Intent = Intent(this,HomeActivity::class.java)
                    startActivity(intent)
                    finishAffinity()
                   // overridePendingTransition(0,0)
                }else if (loginModel.message!=null){
                    Utility.showToast(this,loginModel.message.toString())
                }
            }else{

            }
        }else if(apiType.equals(Constants.SOCIAL_LOGIN)){
            var loginModel = Gson().fromJson(respopnse,LoginModel::class.java)
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
        } else if (apiType == Constants.CHECK_SOCIAL_LOGIN){

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

    private fun handleFacebookAccessToken(token: AccessToken) {
        val credential = FacebookAuthProvider.getCredential(token.token)
        idToken = token.token
        firebaseAuth.signInWithCredential(credential)
            .addOnCompleteListener(this) { task ->
                if (task.isSuccessful) {
                    val firebaseUser = firebaseAuth.currentUser!!
                    //var providerId = firebaseUser!!.providerData.get(1).uid
                    val email = firebaseUser.email
                    val phoneNumber = firebaseUser.phoneNumber
                    val photo = firebaseUser.photoUrl.toString()
                    val displayName = firebaseUser.displayName
                    val uid = firebaseUser.uid

                    val map:HashMap<String,String> = HashMap()
                    if(!email.isNullOrEmpty()){
                        map["email"] = email
                    }
                    if(!phoneNumber.isNullOrEmpty() ){
                        map["phone"] = phoneNumber
                    }
                    map["source"] = "phone"
                    map["user_id"] = uid
                    map["login_type"] = "facebook"
                    map["profile_url"] = photo
                    map["display_name"] = displayName!!
                    map["device_token"] = firebaseDeviceToken.toString()
                    hitSocialPostApi(Constants.SOCIAL_LOGIN,false,Constants.SOCIAL_LOGIN_API,map)

                } else {
                    if(progressHUD !=null){
                        progressHUD!!.dismiss()
                    }
                    Log.w(">>>", "signInWithCredential:failure", task.exception)
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

            }
    }


}