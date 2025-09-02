package com.baghdadhomes.Activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.net.Uri
import android.os.Bundle
import android.view.View
import android.widget.TextView
import android.widget.VideoView
import com.facebook.login.LoginManager
import com.google.android.gms.auth.api.signin.GoogleSignIn
import com.google.android.gms.auth.api.signin.GoogleSignInOptions
import com.google.firebase.auth.FirebaseAuth
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.BuildConfig
import com.baghdadhomes.Models.AppUpdateCheckResponse
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants

class NewSplashActivity : BaseActivity() {
    private var versionName: String = BuildConfig.VERSION_NAME
    private lateinit var videoview: VideoView
    private var type : String ?= ""
    private var sendTo : String ?= ""
    private var sendBy : String ?= ""
    private var postData : String ?= ""
    private var model : AppUpdateCheckResponse ?= null
    private var propertyId : String ?= null
    private var agentId : String ?= null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_new_splash)

        PreferencesService.init(this)
        PreferencesService.instance.getLanguage()
        videoview = findViewById(R.id.videoview)

        // ATTENTION: This was auto-generated to handle app links.
        val appLinkIntent: Intent = intent
        val appLinkAction: String? = appLinkIntent.action
        val appLinkData: Uri ?= appLinkIntent.data
        try {
            if (appLinkData != null) {
                if (appLinkData.getQueryParameter("agency_id") != null){
                    agentId = appLinkData.getQueryParameter("agency_id")
                } else {
                    type = "agency_ads"
                    propertyId = appLinkData.getQueryParameter("id")
                }
            }
        } catch ( e: Exception){
            e.localizedMessage
        }
    }


    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()){
            hitGetApiWithoutToken(Constants.APP_UPDATE_CHECK,false,Constants.APP_UPDATE_CHECK_API)
        }
        if (intent.extras != null) {

            if(!intent.extras?.getString("type").isNullOrEmpty()){
                type = intent.extras?.getString("type")
            }

            if (!intent.extras?.getString("sendTo").isNullOrEmpty()){
                sendTo = intent.extras?.getString("sendTo")
            }
            if (!intent.extras?.getString("sendBy").isNullOrEmpty()){
                sendBy = intent.extras?.getString("sendBy")
            }
            if (!intent.extras?.getString("postData").isNullOrEmpty()){
                postData = intent.extras?.getString("postData")
            }
            if (!intent.extras?.getString("property_id").isNullOrEmpty()){
                propertyId = intent.extras?.getString("property_id")
            }
        }

        videoview.setVideoURI(Uri.parse("android.resource://" + packageName + "/" + R.raw.splash_data_new1))
        videoview.setZOrderOnTop(true)

        videoview.setOnPreparedListener {
            videoview.start()
        }

        videoview.setOnCompletionListener {
            if (intent.getBooleanExtra("languageChange", false)){
                openHome()
            } else{
                 if (model != null){
                     if (model?.success == true && !model?.version.isNullOrEmpty()){
                         if (model?.version!! > versionName){
                             openUpdateDialog(model?.updation_required)
                         } else{
                             openHome()
                         }
                     } else{
                         openHome()
                     }
                 } else{
                     openHome()
                 }
            }
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType == Constants.APP_UPDATE_CHECK){
            model = Gson().fromJson(respopnse,AppUpdateCheckResponse::class.java)
        }
    }

    private fun openHome(){
        val i = Intent(this, HomeActivity::class.java)
        if (!type.isNullOrEmpty()){
            i.putExtra("type",type)
        }
        if (!sendTo.isNullOrEmpty()){
            i.putExtra("sendTo",sendTo)
        }
        if (!sendBy.isNullOrEmpty()){
            i.putExtra("sendBy",sendBy)
        }
        if (!postData.isNullOrEmpty()){
            i.putExtra("postData",postData)
        }
        if (!propertyId.isNullOrEmpty()){
            i.putExtra("propertyId",propertyId)
        }
        if (!agentId.isNullOrEmpty()){
            i.putExtra("agentId",agentId)
        }
        startActivity(i)
        finish()
    }
    private fun openUpdateDialog(isMandatory : Boolean ?= false){
        val dialog = Dialog(this,R.style.Theme_NajafHomes)
        dialog.setContentView(R.layout.dialog_update)
        dialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        dialog.setCancelable(false)
        dialog.setCanceledOnTouchOutside(false)
        dialog.show()

        val tvLater : TextView = dialog.findViewById(R.id.tvLater)
        val tvUpdate : TextView = dialog.findViewById(R.id.tvUpdate)

        if (isMandatory == true){
            tvLater.visibility = View.GONE
        }

        tvLater.setOnClickListener {
            dialog.dismiss()
            openHome()
        }

        tvUpdate.setOnClickListener {
            dialog.dismiss()

            if (PreferencesService.instance.userLoginStatus == true){
            try {
                val senderId = PreferencesService.instance.getUserData?.ID
                val timestamp = System.currentTimeMillis().toString()
                updateMyOnlineStatus(timestamp,senderId.toString())
            } catch (e : Exception){
                e.localizedMessage
            }
            FirebaseAuth.getInstance().signOut()
            LoginManager.getInstance().logOut()
            GoogleSignIn.getClient(
                this,
                GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN).build()
            ).signOut()
            // PreferencesService.instance.clear()
            PreferencesService.instance.saveUserLoginStatus(false)
            }

            val intent = Intent(Intent.ACTION_VIEW)
            intent.data = Uri.parse("https://play.google.com/store/apps/details?id=${this.packageName}")
            startActivity(intent)
        }
    }
}