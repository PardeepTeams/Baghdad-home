package com.baghdadhomes.Activities

import android.annotation.TargetApi
import android.app.Activity
import android.app.Dialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.content.res.Configuration
import android.content.res.Resources
import android.database.Cursor
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.provider.OpenableColumns
import android.util.Log
import android.view.View
import android.view.WindowManager
import android.view.inputmethod.InputMethodManager
import android.widget.*
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.android.gms.tasks.OnCompleteListener
import com.google.android.material.bottomsheet.BottomSheetDialog
import com.google.auth.oauth2.GoogleCredentials
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Models.UploadimagResponse
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.ProgressHud
import com.baghdadhomes.Utils.Utility
import com.baghdadhomes.fcm.*
import com.baghdadhomes.fcm.ApiClient.baseUrl
import com.baghdadhomes.fcm.ApiClient.baseUrlSmsApi
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MultipartBody
import okhttp3.RequestBody
import okhttp3.ResponseBody
import org.json.JSONObject
import pl.droidsonroids.gif.GifImageView
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.http.Part
import retrofit2.http.PartMap
import java.io.File
import java.io.FileOutputStream
import java.io.IOException
import java.io.InputStream
import java.util.*
import java.util.regex.Pattern


abstract class BaseActivity : AppCompatActivity() {
    internal var progressHUD: ProgressHud? = null
    var firebaseDeviceToken : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        adjustFontScale(resources.configuration)
        PreferencesService.init(this)
        if(PreferencesService.instance.getLanguage()!=null && PreferencesService.instance.getLanguage().isNotEmpty()){
            var code  = PreferencesService.instance.getLanguage()
            setAppLocale(this, code)
        }else{
            setAppLocale(this, "ar")
        }

        getFireBaseToken()
    }

    private fun getFireBaseToken() {
        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.w("TAG", "Fetching FCM registration token failed", task.exception)
                return@OnCompleteListener
            }
            firebaseDeviceToken = task.result
            println("DeviceToken : $firebaseDeviceToken")
        })
    }

    override fun onResume() {
        super.onResume()
        try {
            if (PreferencesService.instance.userLoginStatus == true) {
                val senderId = PreferencesService.instance.getUserData?.ID
                val timestamp = System.currentTimeMillis().toString()
                updateMyOnlineStatus("0",senderId.toString())
            }
        } catch (e : Exception){ }
    }
    fun updateMyOnlineStatus(status: String,senderId: String) {
        // check online status
        val refUsers = FirebaseDatabase.getInstance().getReference("UsersStatusBaghdad")
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["onlineStatus"] = status
        refUsers.child(senderId).updateChildren(hashMap)
    }

    override fun onPause() {
        super.onPause()
        try {
            if (PreferencesService.instance.userLoginStatus == true) {
                val senderId = PreferencesService.instance.getUserData?.ID
                val timestamp = System.currentTimeMillis().toString()
                updateMyOnlineStatus(timestamp,senderId.toString())
            }
        } catch (e : Exception){

        }
    }

    fun Activity.dismissKeyboard(view: View?) {
        view?.let {
            val inputMethodManager =
                getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            inputMethodManager.hideSoftInputFromWindow(it.windowToken, 0)
        }
    }

     fun isNetworkAvailable(): Boolean {
        val connectivityManager =
            this.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
            val capabilities =
                connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)
            if (capabilities != null) {
                when {
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> {
                        return true
                    }
                    capabilities.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> {
                        return true
                    }
                }
            }
        } else {
            val activeNetworkInfo = connectivityManager.activeNetworkInfo
            if (activeNetworkInfo != null && activeNetworkInfo.isConnected) {
                return true
            }
        }
        return false
    }

    fun showToast(context: Context, message: String){
        Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
    }

    fun adjustFontScale(configuration: Configuration) {
        if (configuration.fontScale > 1.0) {
            configuration.fontScale = 1.0f
            val metrics = resources.displayMetrics
            val wm = getSystemService(WINDOW_SERVICE) as WindowManager
            wm.defaultDisplay.getMetrics(metrics)
            metrics.scaledDensity = configuration.fontScale * metrics.density
            baseContext.resources.updateConfiguration(configuration, metrics)
        }
    }

    fun emailValidate(email: String): Boolean {
        return Pattern.compile("^[_A-Za-z0-9-\\+]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9-]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$")
            .matcher(email).matches()
    }

     fun showBottomSheetDialog(activity: BaseActivity) {
        val bottomSheetDialog = BottomSheetDialog(this)
        bottomSheetDialog.setContentView(com.baghdadhomes.R.layout.activity_language)
         val rl_english: RelativeLayout? = bottomSheetDialog.findViewById(R.id.rl_english)
         val rl_arabic: RelativeLayout? = bottomSheetDialog.findViewById(R.id.rl_arabic)
         val imv_tick_ar: ImageView? = bottomSheetDialog.findViewById(R.id.imv_tick_ar)
         val imv_tick: ImageView? = bottomSheetDialog.findViewById(R.id.imv_tick)
         val selectedLang = PreferencesService.instance.getLanguage()
         if(selectedLang.equals("ar")){
             imv_tick_ar!!.visibility = View.VISIBLE
             imv_tick!!.visibility = View.GONE
         }else{
             imv_tick_ar!!.visibility = View.GONE
             imv_tick!!.visibility = View.VISIBLE
         }
         rl_english!!.setOnClickListener {
             imv_tick_ar!!.visibility = View.GONE
             imv_tick!!.visibility = View.VISIBLE
             bottomSheetDialog.dismiss()
             PreferencesService.instance.getInstance()!!.saveLanguage("en")
             setAppLocale(activity, "en")

             val intent = Intent(this, NewSplashActivity::class.java)
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
             intent.putExtra("languageChange",true)
             startActivity(intent)
         }

         rl_arabic!!.setOnClickListener {
             imv_tick_ar!!.visibility = View.VISIBLE
             imv_tick!!.visibility = View.GONE
             bottomSheetDialog.dismiss()
             PreferencesService.instance.getInstance()!!.saveLanguage("ar")
             setAppLocale(this, "ar")

             val intent = Intent(this, NewSplashActivity::class.java)
             intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
             startActivity(intent)
         }

        bottomSheetDialog.show()
    }

    fun setAppLocale(loginActivity: Activity, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = loginActivity.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        loginActivity.createConfigurationContext(configuration)
        loginActivity.resources.updateConfiguration(configuration, baseContext.resources.displayMetrics)
        /*if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            updateResources(loginActivity, language)
        }
        updateResourcesLegacy(loginActivity, language)*/
    }

    @TargetApi(Build.VERSION_CODES.N)
    fun updateResources(context: Context, language: String): Context? {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val configuration = context.resources.configuration
        configuration.setLocale(locale)
        configuration.setLayoutDirection(locale)
        return context.createConfigurationContext(configuration)
    }


    fun updateResourcesLegacy(context: Context, language: String): Context? {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val resources: Resources = context.resources
        val configuration: Configuration = resources.getConfiguration()
        configuration.locale = locale
        configuration.setLayoutDirection(locale)
        resources.updateConfiguration(configuration, resources.getDisplayMetrics())
        return context
    }

    /*fun setAppLocale(loginActivity: BaseActivity, language: String) {
        val locale = Locale(language)
        Locale.setDefault(locale)
        val config = loginActivity.resources.configuration
        config.setLocale(locale)
        loginActivity.createConfigurationContext(config)
        loginActivity.resources.updateConfiguration(config, baseContext.resources.displayMetrics)
    }*/

    fun hitPostApiWithLoader(
        type: String,
        showLoader: Boolean,
        url: String,
        /*   page:String,*/
        map:HashMap<String,String>,
        imageView:GifImageView
        /*perPage:String*/
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this,
                false,
                false
            )
        }

        ApiClient.api!!.hitPostApiWithouTokenFieldParams(
            ApiClient.baseUrl + url,map).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                if(!response.isSuccessful){
                    imageView.visibility = View.GONE
                }
                if(!((response.body() as JsonObject).get(Constants.STATUS).asString.equals("true")) ){
                    imageView.visibility = View.GONE
                }
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                imageView.visibility = View.GONE
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }

    fun hitPostApiNotificationDelete(
        type: String,
        showLoader: Boolean,
        url: String,
        params: HashMap<String,String>,
        ids : kotlin.collections.ArrayList<String>
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this,
                false,
                false
            )
        }

        ApiClient.api!!.hitPostApiNotificationRemove(
            ApiClient.baseUrl + url,params,ids).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }

                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }


    fun hitPostApiFilter(
        type: String,
        showLoader: Boolean,
        url: String,
        map:HashMap<String,String>, areas : ArrayList<String>) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this,
                false,
                false
            )
        }

        ApiClient.api!!.hitPostApiFilter(
            ApiClient.baseUrl + url,map,areas).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                try {
                    FirebaseAuth.getInstance().signOut()
                    LoginManager.getInstance().logOut()
                    GoogleSignIn.getClient(
                        this@BaseActivity,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    ).signOut()
                }catch (e:Exception){
                    e.message
                }
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }


    fun hitPostApi(
        type: String,
        showLoader: Boolean,
        url: String,
        /*   page:String,*/
        map:HashMap<String,String>
        /*perPage:String*/
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this,
                false,
                false
            )
        }

        ApiClient.api!!.hitPostApiWithouTokenFieldParams(
            ApiClient.baseUrl + url,map).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                try {
                    FirebaseAuth.getInstance().signOut()
                    LoginManager.getInstance().logOut()
                    GoogleSignIn.getClient(
                        this@BaseActivity,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    ).signOut()
                }catch (e:Exception){
                    e.message
                }
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }


    fun hitGetApiWithoutToken(
        type: String,
        showLoader: Boolean,
        url: String
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this@BaseActivity,
                false,
                false
            )
        }

        ApiClient.api!!.hitGetApiWithouToken(
            baseUrl + url).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                  Log.d("callJason",t.message.toString())
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }

    fun hitSendOtpApi(
        url: String,
        type: String,
        showLoader: Boolean,
        token: String,
        map : HashMap<Any, Any>
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(this@BaseActivity, indeterminate = false, cancelable = false)
        }

        ApiClient.smsClient!!.hitSendOtpApi(token, "$baseUrlSmsApi$url",map).enqueue(object : Callback<JsonObject> {
                override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                    getData(response, type, showLoader)
                }

                override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                    if (showLoader) {
                        progressHUD!!.dismiss()
                    }
                    Log.d("callJason",t.message.toString())
                    Toast.makeText(
                        this@BaseActivity,
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }
            })

    }

  fun hitGetApiWithoutTokenWithParams(
        type: String,
        showLoader: Boolean,
        url: String,
        params: HashMap<String,String>
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this@BaseActivity,
                false,
                false
            )
        }

          ApiClient.api!!.hitGetApiWithouTokenWithParams(
              baseUrl + url, params).enqueue(object : Callback<JsonObject> {
              override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                  getData(response, type, showLoader)


              }

              override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                  if (showLoader) {
                          progressHUD!!.dismiss()
                      }
                      Log.d("callJason",t.message.toString())
                      Toast.makeText(
                          this@BaseActivity,
                          resources.getString(R.string.something_went_wrong),
                          Toast.LENGTH_SHORT
                      )
                          .show()


              }
          })




    }

    //hit multipart api
    fun hitMultipartApi(
        type: String,
        showLoader: Boolean,
        url: String,
        @PartMap parameters: HashMap<String, RequestBody>,
        @Part imageBody: MultipartBody.Part?= null
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this@BaseActivity,
                false,
                false
            )
        }

        ApiClient.api!!.hitMultipartApi(
            baseUrl + url,parameters,imageBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("responseBody",response.body().toString())
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                Log.d("responseBody",t.toString())
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }
    fun hitMultipartVideoUploadApi(
        type: String,
        showLoader: Boolean,
        url: String,
        @Part imageBody: MultipartBody.Part
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this@BaseActivity,
                false,
                false
            )
        }

        ApiClient.api!!.hitMultipartApiWithoutParams(baseUrl + url,imageBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("responseBody",response.body().toString())
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                Log.d("responseBody",t.toString())
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }

    /*fun hitMultipartApiWithoutParams(
        type: String,
        showLoader: Boolean,
        url: String,
        @Part imageBody: MultipartBody.Part
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this@BaseActivity,
                false,
                false
            )
        }

        ApiClient.api2!!.uploadsImages(imageBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("responseBody",response.body().toString())
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                Log.d("responseBody",t.toString())
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }*/
    fun hitMultipartApiWithoutParams(
        type: String,
        showLoader: Boolean,
        url: String,
        @Part imageBody: MultipartBody.Part,
        spinKit: GifImageView
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this@BaseActivity,
                false,
                false
            )
        }

        ApiClient.api2!!.uploadsImages(imageBody).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                Log.d("responseBody",response.body().toString())
                if(!response.isSuccessful){
                    spinKit.visibility = View.GONE
                }
                try {
                    if ((response.body() as JsonObject).get(Constants.STATUS).asString.equals("false")){
                        spinKit.visibility = View.GONE
                    }
                } catch (e : Exception){
                    spinKit.visibility = View.GONE
                }
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }
                spinKit.visibility = View.GONE
                Log.d("responseBody",t.toString())
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }

    fun  hitSocialPostApi(type: String,
                          showLoader: Boolean,
                          url: String,
        /*   page:String,*/
                          map:HashMap<String,String>){
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this,
                false,
                false
            )
        }

        ApiClient.api!!.hitPostApiWithouTokenFieldParams(
            ApiClient.baseUrl + url,map).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                try {
                    progressHUD!!.dismiss()
                }catch (e:Exception){
                    e.message
                }
                try {
                    FirebaseAuth.getInstance().signOut()
                    LoginManager.getInstance().logOut()
                    GoogleSignIn.getClient(
                        this@BaseActivity,
                        GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
                    ).signOut()
                }catch (e:Exception){
                    e.message
                }
                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })
    }
    fun hitAddPostApiWithoutTokenParams(
        type: String,
        showLoader: Boolean,
        url: String,
        params: HashMap<String,String>,
        imagesList:ArrayList<String>
    ) {
        if (showLoader) {
            progressHUD = ProgressHud.show(
                this,
                false,
                false
            )
        }

        ApiClient.api!!.hitAddPostApiWithouTokenFieldParams(
            ApiClient.baseUrl + url,params,imagesList).enqueue(object : Callback<JsonObject> {
            override fun onResponse(call: Call<JsonObject>, response: Response<JsonObject>) {
                getData(response, type, showLoader)
            }

            override fun onFailure(call: Call<JsonObject>, t: Throwable) {
                if (showLoader) {
                    progressHUD!!.dismiss()
                }

                Toast.makeText(
                    this@BaseActivity,
                    resources.getString(R.string.something_went_wrong),
                    Toast.LENGTH_SHORT
                )
                    .show()
            }
        })

    }

    abstract fun getResponse(apiType: String, respopnse: JsonObject)
    //get api response
    fun getData(response: Response<JsonObject>, type: String, showLoader: Boolean) {
        if (response.isSuccessful() && response.code() == 200) {
            if(type.equals(Constants.ADD_POST) || type.equals(Constants.UPDATE_ADD) || type.equals(Constants.UPLOAD_VIDEO) || type.equals(Constants.SEND_SMS_OTP)){
                getResponse(type, response.body() as JsonObject)
            }else{

                if ((response.body() as JsonObject).get(Constants.STATUS).asString.equals("true")) {
                    if(response.body()!=null)
                        getResponse(type, response.body() as JsonObject)

                } else {
                    try {
                        if(type.equals(Constants.UPLOAD_IMAGE)){
                            try{
                                var model: UploadimagResponse =
                                    Gson().fromJson(response.body(), UploadimagResponse::class.java)
                                Utility.showToast(
                                    this@BaseActivity,
                                    model.data.message
                                )
                            }catch (e:Exception){
                                e.message
                            }

                        }else{
                            Utility.showToast(
                                this@BaseActivity,
                                (response.body() as JsonObject).get(Constants.MESSAGE).asString
                            )
                        }
                    }catch (e:Exception){
                        e.message
                    }
                    // Utility.showSnackBar(this.findViewById(android.R.id.content),(response.body() as JsonObject).get(Constants.MESSAGE).asString,this)


                }
            }

        } else if (response.code() == 285 && type.equals(Constants.ADD_POST)){
            Utility.showToast(this@BaseActivity, getString(R.string.ads_limit_reached))
        }
        else if (response.code()==400) {
            try {
                val jObjError = JSONObject(response.errorBody()!!.string())
                //  Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                Utility.showToast(this@BaseActivity, jObjError.getString(Constants.MESSAGE))
            } catch (e: Exception) { }

            if(type.equals(Constants.SOCIAL_LOGIN) || type.equals(Constants.CHECK_SOCIAL_LOGIN)){
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
        } else if (response.code() == 401) {
            if (type.equals(Constants.LOGIN)){
                var language = PreferencesService.instance.getLanguage()
                if (language == "ar"){
                    Utility.showToast(this@BaseActivity, resources.getString(R.string.account_not_exist))
                } else{
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Utility.showToast(this@BaseActivity, jObjError.getString(Constants.MESSAGE))
                }
            } else{
                try {
                    val jObjError = JSONObject(response.errorBody()!!.string())
                    Utility.showToast(this@BaseActivity, jObjError.getString(Constants.MESSAGE))
                } catch (e: Exception) { }
            }
            if(type.equals(Constants.SOCIAL_LOGIN) || type.equals(Constants.CHECK_SOCIAL_LOGIN)){
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
        } else if (response.code() == 403 || response.code() == 404) {
            if(type.equals(Constants.SOCIAL_LOGIN) || type.equals(Constants.CHECK_SOCIAL_LOGIN)){
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

        } else if (response.code() == 409) {
            try {
                val jObjError = JSONObject(response.errorBody()!!.string());
                // Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                Utility.showToast(this@BaseActivity, jObjError.getString(Constants.MESSAGE))
            } catch (e: Exception) {
            }
            if(type.equals(Constants.SOCIAL_LOGIN) || type.equals(Constants.CHECK_SOCIAL_LOGIN)){
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
        } else {
            if(type.equals(Constants.SOCIAL_LOGIN) || type.equals(Constants.CHECK_SOCIAL_LOGIN)){
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
            try {
                if (progressHUD != null && progressHUD!!.isShowing()) {
                    progressHUD!!.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            Toast.makeText(
                this@BaseActivity,
                resources.getString(R.string.something_went_wrong),
                Toast.LENGTH_SHORT
            ).show()
        }
        try {
            if(!type.equals(Constants.GET_PROPERTIES)){
                try {
                    if (progressHUD != null && progressHUD!!.isShowing()) {
                        progressHUD!!.dismiss()
                    }
                }catch (e:Exception){
                    e.message
                }


            }else if(!type.equals(Constants.GET_PROPERTIES_DETAIL)){
                try {
                    if (progressHUD != null && progressHUD!!.isShowing()) {
                        progressHUD!!.dismiss()
                    }
                }catch (e:Exception){
                    e.message
                }


            } else if(type.equals(Constants.VIEW_COUNT)){
                try {
                    if (progressHUD != null && progressHUD!!.isShowing()) {
                        // progressHUD!!.dismiss()
                        Handler(Looper.getMainLooper()).postDelayed(Runnable {
                            progressHUD!!.dismiss()
                        },1000)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
            else{
                try {
                    if (progressHUD != null && progressHUD!!.isShowing()) {
                       // progressHUD!!.dismiss()
                         Handler(Looper.getMainLooper()).postDelayed(Runnable {
                             progressHUD!!.dismiss()
                         },1000)

                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }

    }
    fun hasPermissions(vararg permissions: String?): Boolean {
        if (permissions != null) {
            for (permission in permissions) {
                if (ActivityCompat.checkSelfPermission(this, permission!!)
                    == PackageManager.PERMISSION_GRANTED
                ) {
                    return true
                }
            }
        }
        return false
    }

    @Throws(IOException::class)
    fun getFile(context: Context, uri: Uri): File {
        val destinationFilename = File(
            context.filesDir.path + File.separatorChar.toString() + queryName(
                context,
                uri
            )
        )
        try {
            context.contentResolver.openInputStream(uri).use { ins ->
                if (ins != null) {
                    createFileFromStream(
                        ins,
                        destinationFilename
                    )
                }
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
        return destinationFilename
    }

    private fun createFileFromStream(ins: InputStream, destination: File?) {
        try {
            FileOutputStream(destination).use { os ->
                val buffer = ByteArray(4096)
                var length: Int
                while (ins.read(buffer).also { length = it } > 0) {
                    os.write(buffer, 0, length)
                }
                os.flush()
            }
        } catch (ex: java.lang.Exception) {
            Log.e("Save File", ex.message!!)
            ex.printStackTrace()
        }
    }

    fun queryName(context: Context, uri: Uri): String {
        val returnCursor: Cursor =
            context.contentResolver.query(uri, null, null, null, null)!!
        val nameIndex = returnCursor.getColumnIndex(OpenableColumns.DISPLAY_NAME)
        returnCursor.moveToFirst()
        val name = returnCursor.getString(nameIndex)
        returnCursor.close()
        return name
    }

    fun sendNotificationToUser(token: String, name: String, message: String, sendTo : String, sendBy : String, postData : String) {

            getAuthToken { authToken->
                if (authToken != null){
                    val rootModel = NotificationInfo(/* token = */ token, /* notification = */
                    NotificationModel(name, message), /* data = */
                    DataModel(
                        name = name,
                        message = message,
                        type = "Message",
                        sendTo = sendTo,
                        sendBy = sendBy,
                        postData = postData
                    ))
                    val root = RootModel(rootModel)
                    val apiService: ApiService = ApiClient.client!!.create(ApiService::class.java)
                    val responseBodyCall = apiService.sendNotification("Bearer $authToken", root)
                    responseBodyCall!!.enqueue(object : Callback<ResponseBody?> {
                        override fun onResponse(call: Call<ResponseBody?>, response: Response<ResponseBody?>){
                            Log.d("SendNotificationDone1", response.message().toString())
                            Log.d("SendNotificationDone2", call.toString())
                            Log.d("SendNotificationDone", "Notification send by using retrofit.")
                        }

                        override fun onFailure(call: Call<ResponseBody?>, t: Throwable) {
                            Log.d("SendNotificationFailed", t.message!!)
                        }
                    })
                }
            }

    }

    fun getAuthToken(onTokenFound : (String?)-> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            try {
                // Open the InputStream from the raw resources directory
                val inputStream = resources.openRawResource(R.raw.service_account)

                val googleCredentials: GoogleCredentials = GoogleCredentials
                    .fromStream(inputStream)
                    .createScoped(listOf("https://www.googleapis.com/auth/cloud-platform"))
                googleCredentials.refresh()
                val token =  googleCredentials.getAccessToken().tokenValue
                onTokenFound.invoke(token)
                withContext(Dispatchers.Main){
                    println("AuthToken = $token")
                }
            } catch (e : Exception){
                e.printStackTrace() // Print the full stack trace
                onTokenFound.invoke(null)
                withContext(Dispatchers.Main) {
                    println("AuthTokenError = ${e.localizedMessage}")
                }
            }
        }
    }

    fun loginTypeDialog(isRegister : Boolean){
        val dialog= Dialog(this, R.style.Theme_NajafHomes)
        dialog.setContentView(R.layout.dialog_login_type)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        dialog.setCancelable(true)
        dialog.setCanceledOnTouchOutside(true)
        dialog.show()

        val rlMainDialog: RelativeLayout = dialog.findViewById(R.id.rlMainDialog)
        val llOptions: LinearLayout = dialog.findViewById(R.id.llOptions)
        val tvHeader: TextView = dialog.findViewById(R.id.tvHeader)
        val tvMsg: TextView = dialog.findViewById(R.id.tvMsg)
        val rlCustomer: RelativeLayout = dialog.findViewById(R.id.rlCustomer)
        val rlCompany: RelativeLayout = dialog.findViewById(R.id.rlCompany)
        val rlCancel: RelativeLayout = dialog.findViewById(R.id.rlCancel)

        if (isRegister){
            tvHeader.text = getString(R.string.sign_up)
            tvMsg.text = getString(R.string.sign_up_as)
        } else {
            tvHeader.text = getString(R.string.log_in)
            tvMsg.text = getString(R.string.login_as)
        }

        //val type = "agency" || "customer"

        rlMainDialog.setOnClickListener {
            dialog.dismiss()
        }

        llOptions.setOnClickListener {

        }

        rlCustomer.setOnClickListener {
            dialog.dismiss()
            val intent : Intent = if (isRegister){
                Intent(this, RegisterActivity::class.java)
            } else {
                Intent(this, LoginActivity::class.java)
            }
            intent.putExtra("login_type", "customer")
            startActivity(intent)
        }

        rlCompany.setOnClickListener {
            dialog.dismiss()
            val intent = Intent(this, WhatsappLoginActivity::class.java)
            intent.putExtra("login_type", "agency")
            startActivity(intent)
        }

        rlCancel.setOnClickListener {
            dialog.dismiss()
        }
    }

}