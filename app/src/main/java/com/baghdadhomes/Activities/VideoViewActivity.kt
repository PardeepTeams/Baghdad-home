package com.baghdadhomes.Activities

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.util.Log
import android.view.WindowManager
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterVideoView
import com.baghdadhomes.Models.AdsDataChat
import com.baghdadhomes.Models.AgenciesData
import com.baghdadhomes.Models.ReelResult
import com.baghdadhomes.Models.ReelsData
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility

class VideoViewActivity : BaseActivity(), AdapterVideoView.VideoViewAction {
    private lateinit var viewPager : ViewPager2

    private var storiesList : ArrayList<ReelsData> = ArrayList()
    private var adapterVideos = AdapterVideoView(this,storiesList,this)

    @SuppressLint("NewApi")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_video_view)

        viewPager = findViewById(R.id.viewPager)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        val model: ReelResult?
        if (intent.getIntExtra("position",-1) != null){
            model = Constants.reelsModel!!
           // model = Gson().fromJson(intent.getStringExtra("model"),ReelResult::class.java)
            if (!model?.response.isNullOrEmpty()){
                storiesList.addAll(model.response!!)

                Handler(Looper.getMainLooper()).post {
                    val scrollPosition : Int = intent.getIntExtra("position",0)
                    viewPager.setCurrentItem(scrollPosition,false)
                }
            }
        }

        viewPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback(){
            override fun onPageScrolled(
                position: Int, positionOffset: Float, positionOffsetPixels: Int) {
                super.onPageScrolled(position, positionOffset, positionOffsetPixels)
                Log.d("currentPage","OnPageScrolled: $position")
            }
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                Log.d("currentPage","OnPageSelected: $position")
            }
            override fun onPageScrollStateChanged(state: Int) {
                super.onPageScrollStateChanged(state)
                Log.d("currentPage","$state")
            }
        })
        viewPager.orientation = ViewPager2.ORIENTATION_VERTICAL
        viewPager.adapter = adapterVideos

    }

    override fun onCallClick(position: Int) {
        val intent = Intent(Intent.ACTION_DIAL)
        intent.data = Uri.parse("tel:" + storiesList[position].call_number)
        startActivity(intent)
    }

    override fun onFavClick(position: Int) {
        val propId = storiesList[position].post_id!!
        val userId = PreferencesService.instance.getUserData?.ID!!
        val map: HashMap<String, String> = HashMap()
        map["user_id"] = userId
        map["listing_id"] = propId
        storiesList[position].is_fav = storiesList[position].is_fav != true
        hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)
    }

    override fun onShareClick(position: Int) {
        val sharingIntent = Intent(Intent.ACTION_SEND)
        sharingIntent.type = "text/plain"
        sharingIntent.putExtra(Intent.EXTRA_TEXT, storiesList[position].link)
        startActivity(Intent.createChooser(sharingIntent, ""))
    }

    override fun onTitleClick(position: Int) {
        val intent = Intent(this,AdsDetailsActivity::class.java)
        intent.putExtra("propertyId",storiesList[position].post_id.orEmpty())
        startActivity(intent)
        overridePendingTransition(0, 0)
    }

    override fun onCancelClick() {
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        finish()
        overridePendingTransition(0, 0)
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        window.clearFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)
        finish()
        overridePendingTransition(0, 0)
    }
    override fun onVideoCompleted(position:Int) {
        try {
            val nextVideoPosition = position + 1
            if (storiesList.size > nextVideoPosition){
                viewPager.setCurrentItem(nextVideoPosition,false)
            } else{
                finish()
                overridePendingTransition(0, 0)
            }
        } catch (e : Exception){
            e.localizedMessage!!.toString()
        }
      //  adapterVideos.notifyDataSetChanged()
    }

    override fun onVideoReady() {
        /*Handler(Looper.getMainLooper()).postDelayed({
            Log.d("viewedReel", "$position")
        },1000)*/
        val position = viewPager.currentItem
        if (/*storiesList.get(position).status == "0" && */isNetworkAvailable()){
            Handler(Looper.getMainLooper()).postDelayed({
                Log.d("viewedReel", position.toString())
                val map : HashMap<String,String> = HashMap()
                if (PreferencesService.instance.userLoginStatus == true){
                    map["user_id"] = PreferencesService.instance.getUserData?.ID!!
                } else{
                    map["device_id"] = Utility.getDeviceId(this)
                }
                map["post_id"] = storiesList.get(position).post_id!!
                map["total_view"] = (storiesList.get(position).reel_view!!.toInt() -1).toString()
                hitPostApi(Constants.REEL_VIEW_UPDATE,false,Constants.REEL_VIEW_UPDATE_API,map)
                storiesList.get(position).status = "1"
            },1000)
        }
    }

    override fun onMsgClick(position: Int) {
        if(PreferencesService.instance.userLoginStatus == true) {
            val model = storiesList.get(position)
            val receiverModel = AgenciesData(
                call_number = model.call_number!!,
                whatsapp_number = model.whatsapp_number!!,
                ID = model.ID,
                display_name = model.display_name,
                user_image = model.user_image,
                device_token = model.device_token
            )

            val postData = AdsDataChat(
                iD = model.post_id,
                post_title = model.title,
                thumbnail = model.thumbnail,
                price = model.price
            )

            val intent = Intent(this, MessagingActivity::class.java)
            intent.putExtra("receiverModel", Gson().toJson(receiverModel))
            intent.putExtra("postData", Gson().toJson(postData))
            startActivity(intent)
            overridePendingTransition(0, 0)
        } else {
            loginTypeDialog(false)
            //startActivity(Intent(this, LoginActivity::class.java))
            //overridePendingTransition(0,0)
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

    }
}