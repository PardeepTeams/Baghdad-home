package com.baghdadhomes.Activities

import android.content.Intent
import android.content.pm.PackageManager
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.widget.FrameLayout
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment

import com.google.firebase.FirebaseApp
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Fragments.*
import com.baghdadhomes.Models.AdsDataChat
import com.baghdadhomes.Models.AgenciesData
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import kotlinx.coroutines.CoroutineStart
import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import android.util.Base64
import android.util.Log

class HomeActivity : BaseActivity() {
    lateinit var fl_container: FrameLayout
    lateinit var rl_add: RelativeLayout
    lateinit var rl_more: RelativeLayout
    lateinit var rl_relators: RelativeLayout
    lateinit var rl_nbhd: RelativeLayout
    lateinit var rl_menu: RelativeLayout
    lateinit var rl_add_ads: RelativeLayout
    lateinit var img_more: ImageView
    lateinit var img_services: ImageView
    lateinit var img_nbhd: ImageView
    lateinit var img_menu: ImageView
    lateinit var tv_more: TextView
    lateinit var tv_relators: TextView
    lateinit var tv_add_ads: TextView
    lateinit var tv_nbhd: TextView
    lateinit var tv_menu: TextView

    override fun onResume() {
        super.onResume()
    }

    fun printKeyHash() {
        try {
            val info = packageManager.getPackageInfo(
                packageName,
                PackageManager.GET_SIGNATURES
            )
            for (signature in info.signatures!!) {
                val md = MessageDigest.getInstance("SHA")
                md.update(signature.toByteArray())
                val keyHash = Base64.encodeToString(md.digest(), Base64.DEFAULT)
                Log.d("KeyHash:", keyHash)
            }
        } catch (e: PackageManager.NameNotFoundException) {
            e.printStackTrace()
        } catch (e: NoSuchAlgorithmException) {
            e.printStackTrace()
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_home)
        FirebaseApp.initializeApp(this)
        printKeyHash()
        adjustFontScale(resources.configuration)
        PreferencesService.init(this)
        fl_container = findViewById(R.id.fl_container)
        rl_add = findViewById(R.id.rl_add)
        rl_more = findViewById(R.id.rl_more)
        img_more = findViewById(R.id.img_more)
        tv_more = findViewById(R.id.tv_more)
        rl_relators = findViewById(R.id.rl_relators)
        img_services = findViewById(R.id.img_services)
        tv_relators = findViewById(R.id.tv_relators)
        tv_add_ads = findViewById(R.id.tv_add_ads)
        rl_nbhd = findViewById(R.id.rl_nbhd)
        img_nbhd = findViewById(R.id.img_nbhd)
        tv_nbhd = findViewById(R.id.tv_nbhd)
        rl_menu = findViewById(R.id.rl_menu)
        img_menu = findViewById(R.id.img_menu)
        tv_menu = findViewById(R.id.tv_menu)
        rl_add_ads = findViewById(R.id.rl_add_ads)

        Handler(Looper.getMainLooper()).postDelayed({
            val map: HashMap<String, String> = HashMap()
            map.put("token", firebaseDeviceToken.toString())
            hitPostApi(Constants.SEND_TOKEN, false, Constants.SEND_TOKEN_API, map)
        },100)

        clickListeners()

        try {
            if (PreferencesService.instance.userLoginStatus == true){
                if (intent.getStringExtra("type") != null && intent.getStringExtra("type").equals("Message",ignoreCase = true)) {
                    val sender = intent.getStringExtra("sendBy")
                    val receiver = intent.getStringExtra("sendTo")
                    val post = intent.getStringExtra("postData")

                    var userId: String? = null
                    if (PreferencesService.instance.userLoginStatus == true) {
                        userId = PreferencesService.instance.getUserData?.ID

                        if (!sender.isNullOrEmpty() && !receiver.isNullOrEmpty()) {
                            val sendBy = Gson().fromJson(sender, AgenciesData::class.java)
                            val sendTo = Gson().fromJson(receiver, AgenciesData::class.java)
                            val postData = Gson().fromJson(post, AdsDataChat::class.java)

                            val receiverModel = if (userId != null && userId != sendTo?.ID) {
                                sendTo
                            } else {
                                sendBy
                            }
                            val intent = Intent(this, MessagingActivity::class.java)
                            Constants.agencyModel = receiverModel
                            Constants.postDetails = postData
                                // intent.putExtra("receiverModel", Gson().toJson(receiverModel))
                          //  intent.putExtra("postData", Gson().toJson(postData))
                            startActivity(intent)
                            this.overridePendingTransition(0, 0)
                        } else {
                            setChatFragment()
                        }

                    }
                } else {
                    setHomeFragment()
                }
            } else{
                setHomeFragment()
            }
        } catch (e :Exception){
            setHomeFragment()
        }

        runOnUiThread {
            try {
                if (intent.getStringExtra("type") != null && intent.getStringExtra("type").equals("agency_ads",ignoreCase = true)){
                    if (!intent.getStringExtra("propertyId").isNullOrEmpty()){
                        val propertyId = intent.getStringExtra("propertyId").orEmpty()
                        val intent = Intent(this,AdsDetailsActivity::class.java)
                        intent.putExtra("propertyId",propertyId)
                        startActivity(intent)
                        overridePendingTransition(0, 0)
                    }
                } else if (!intent.getStringExtra("agentId").isNullOrEmpty()){
                    val agentId = intent.getStringExtra("agentId").orEmpty()
                    val intent = Intent(this,CompanyAdsActivity::class.java)
                    intent.putExtra("agentId",agentId)
                    startActivity(intent)
                    overridePendingTransition(0, 0)
                }
            } catch (e : Exception){
                println(e.localizedMessage)
            }
        }

        rl_add.setOnClickListener{
            val isLogged = PreferencesService.instance.userLoginStatus
            if (isLogged == true){
                Constants.resultFeautred = null
                startActivity(Intent(this@HomeActivity, PostAdActivity::class.java))
                overridePendingTransition(0, 0)
            } else{
                loginTypeDialog(false)
                //startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                //overridePendingTransition(0, 0)
            }
        }
    }

    fun setHomeFragment(){
        setFragment(MenuFragment())
        img_menu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_menu_blue))
        img_nbhd.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_nbhd))
        img_services.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_projects))
        img_more.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_more))
        tv_menu.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
        tv_nbhd.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_add_ads.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_relators.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_more.setTextColor(ContextCompat.getColor(this, R.color.grey))
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType == Constants.SEND_TOKEN){
            println("Response : $respopnse")
        }
    }



    fun setChatFragment(){
        setFragment(ChatHistoryActivity())
        img_menu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_menu))
        img_nbhd.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_nbhd))
        img_services.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_projects))
        img_more.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_more))
        tv_menu.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_nbhd.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_add_ads.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_relators.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_more.setTextColor(ContextCompat.getColor(this, R.color.grey))
    }

    fun setRealEstateFragment(){
        // setFragment(RealEstateFragment())
        img_menu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_menu))
        img_nbhd.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_nbhd))
        img_services.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_projects))
        img_more.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_more))
        tv_menu.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_nbhd.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_add_ads.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_relators.setTextColor(ContextCompat.getColor(this, R.color.grey))
        tv_more.setTextColor(ContextCompat.getColor(this, R.color.grey))
    }

    private fun clickListeners() {
        rl_menu.setOnClickListener {
            setFragment(MenuFragment())
            img_menu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_menu_blue))
            img_nbhd.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_nbhd))
            img_services.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_projects))
            img_more.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_more))
            tv_menu.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_nbhd.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_add_ads.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_relators.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_more.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }
        rl_nbhd.setOnClickListener { //setFragment(new MenuFragment());
            //setFragment(NeighborhoodFragment())
            setFragment(NBHDFragment())
            img_menu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_menu))
            img_nbhd.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_nbhd_blue))
            img_services.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_projects))
            img_more.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_more))
            tv_menu.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_nbhd.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_add_ads.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_relators.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_more.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }
        rl_relators.setOnClickListener {
            /*val token = "cAtzUqSOTY6-VHNMwqGals:APA91bGyMuTLd-qBEoPmLa2hAJq5RH0xR4AxUBDlMYBriWqhGfXhiDF2MHfyQddrpbXROj_iryU6uvWoNgUSb12hFt3ryY7dy80CKWGTazsPcL29zDHy66y6HHBJXit7wyVdot3WFSYA"
            sendNotificationToUser(token, "Najaf", "Najaf Notification")*/
          //  setFragment(RealEstateFragment());
            setFragment(ProjectFragment());
            img_menu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_menu))
            img_nbhd.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_nbhd))
            img_services.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_projects_blue))
            img_more.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_more))
            tv_menu.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_nbhd.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_add_ads.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_relators.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
            tv_more.setTextColor(ContextCompat.getColor(this, R.color.grey))
        }
        rl_more.setOnClickListener { //setFragment(new MenuFragment());
            //throw new RuntimeException("This is a crash");
            setFragment(MoreFragment())
            img_menu.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_menu))
            img_nbhd.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_nbhd))
            img_services.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_projects))
            img_more.setImageDrawable(ContextCompat.getDrawable(this,R.drawable.ic_more_blue))
            tv_menu.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_nbhd.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_add_ads.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_relators.setTextColor(ContextCompat.getColor(this, R.color.grey))
            tv_more.setTextColor(ContextCompat.getColor(this, R.color.skyBlue))
        }

        rl_add_ads.setOnClickListener { //setFragment(new MenuFragment());
            //throw new RuntimeException("This is a crash");

            /*img_menu.setImageDrawable(getDrawable(R.drawable.ic_menu))
            img_nbhd.setImageDrawable(getDrawable(R.drawable.ic_nbhd))
            img_services.setImageDrawable(getDrawable(R.drawable.ic_service))
            img_more.setImageDrawable(getDrawable(R.drawable.ic_more))
            tv_menu.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            tv_nbhd.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            tv_add_ads.setTextColor(ContextCompat.getColor(applicationContext, R.color.skyBlue))
            tv_services.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            tv_more.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))*/

            val isLogged = PreferencesService.instance.userLoginStatus
            if (isLogged == true){
                Constants.resultFeautred = null
                startActivity(Intent(this@HomeActivity, PostAdActivity::class.java))
                overridePendingTransition(0, 0)
            } else{
                loginTypeDialog(false)
                //startActivity(Intent(this@HomeActivity, LoginActivity::class.java))
                //overridePendingTransition(0, 0)
            }

        }
    }

    private fun setFragment(fragment: Fragment) {
        val manager = supportFragmentManager
        val transaction = manager.beginTransaction()
        transaction.replace(R.id.fl_container, fragment)
        transaction.addToBackStack(null)
        transaction.commit()
    }

    override fun onBackPressed() {
        val manager = supportFragmentManager.findFragmentById(R.id.fl_container)
        if (manager is MenuFragment) {
            finishAffinity()
        } else {
            setHomeFragment()
        }
    }




}