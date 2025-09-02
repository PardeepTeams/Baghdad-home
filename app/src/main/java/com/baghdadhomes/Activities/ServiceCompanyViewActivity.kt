package com.baghdadhomes.Activities

import android.content.Intent
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.text.Html
import android.view.View
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.core.widget.NestedScrollView
import androidx.viewpager2.widget.ViewPager2
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterCompaniesSlider
//import com.baghdadhomes.Adapters.AdapterCompaniesSlider
import com.baghdadhomes.Models.ServicesListResponse
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
/*import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView*/
import java.net.URLEncoder
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

class ServiceCompanyViewActivity : BaseActivity() {
    lateinit var img_back: ImageView
    lateinit var img_auto_scroll: ViewPager2
    lateinit var indicatorLayout: LinearLayout
    lateinit var nestedScrollView: NestedScrollView
    lateinit var tv_company_title:TextView
    lateinit var tv_service_name:TextView
    lateinit var tv_company_address:TextView
    lateinit var tv_time_from:TextView
    lateinit var tv_time_to:TextView
    lateinit var tv_company_details:TextView
    lateinit var social_fb:ImageView
    lateinit var social_insta:ImageView
    lateinit var contact_whatsapp:RelativeLayout
    lateinit var contact_call:RelativeLayout
    lateinit var serviceCompanyModel: ServicesListResponse
    lateinit var img_backNew: ImageView
    var postTitle: String? = null
    var arabic_title: String? = null
    val imagesList:ArrayList<String> = ArrayList()
    var currentIndex = 0
    private var job: Job? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_service_company_view)
        adjustFontScale(resources.configuration)

        img_back = findViewById(R.id.img_back)
        nestedScrollView = findViewById(R.id.nestedScrollView)
        img_auto_scroll = findViewById(R.id.img_auto_scroll)
        indicatorLayout = findViewById(R.id.indicatorLayout)
        img_backNew = findViewById(R.id.img_backNew)

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            nestedScrollView.setOnScrollChangeListener(object : View.OnScrollChangeListener{
                override fun onScrollChange(v: View?, scrollX: Int, scrollY: Int,
                    oldScrollX: Int, oldScrollY: Int) {
                    if (scrollY == 0){
                        img_backNew.visibility = View.GONE
                    } else{
                        img_backNew.visibility = View.VISIBLE
                    }
                }
            })
        }

        img_back.setOnClickListener {
            onBackPressed()
        }

        if (intent.getStringExtra("postTitle") != null){
            postTitle = intent.getStringExtra("postTitle")
        }

        if (intent.getStringExtra("arabic_title") != null){
            arabic_title = intent.getStringExtra("arabic_title")
        }
        if(intent.getStringExtra("model")!=null){
            serviceCompanyModel = Gson().fromJson(intent.getStringExtra("model"),ServicesListResponse::class.java)
        }

        init()

        imagesList.clear()
        imagesList.add(serviceCompanyModel.thumbnail)

        img_auto_scroll.adapter = AdapterCompaniesSlider(this, imagesList)
        setupIndicators(imagesList.size)
        setCurrentIndicator(0)
        startAutoSlide()
        /*img_auto_scroll.setSliderAdapter(AdapterCompaniesSlider(this,list))
        img_auto_scroll.setIndicatorAnimation(IndicatorAnimationType.WORM) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
        img_auto_scroll.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
        img_auto_scroll.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
        img_auto_scroll.indicatorRadius = 4
        img_auto_scroll.indicatorSelectedColor = ContextCompat.getColor(this, R.color.skyBlue)
        img_auto_scroll.indicatorUnselectedColor = ContextCompat.getColor(this, R.color.lightBlue)
        img_auto_scroll.scrollTimeInSec = 4 //set scroll delay in seconds :
        img_auto_scroll.startAutoCycle()*/
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

    }

    fun init(){
        tv_company_title = findViewById(R.id.tv_company_title)
        tv_service_name = findViewById(R.id.tv_service_name)
        tv_company_address = findViewById(R.id.tv_company_address)
        tv_time_from = findViewById(R.id.tv_time_from)
        tv_time_to = findViewById(R.id.tv_time_to)
        tv_company_details = findViewById(R.id.tv_company_details)
        social_fb = findViewById(R.id.social_fb)
        social_insta = findViewById(R.id.social_insta)
        contact_whatsapp = findViewById(R.id.contact_whatsapp)
        contact_call = findViewById(R.id.contact_call)

       /* if(serviceCompanyModel.data.post_name!=null){
            tv_company_title.text = serviceCompanyModel.data.post_name
        }*/

        img_auto_scroll.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        var language = PreferencesService.instance.getLanguage()

        if (language == "ar"){
            if (serviceCompanyModel.meta_data != null){
                if (serviceCompanyModel.meta_data.arabicTitle != null){
                    tv_company_title.setText(serviceCompanyModel.meta_data.arabicTitle.get(0))
                }
            }
        } else {
            if (serviceCompanyModel.data != null){
                if (serviceCompanyModel.data.post_title != null){
                    tv_company_title.setText(serviceCompanyModel.data.post_title)
                }
            }
        }


        if (language == "ar"){
            tv_service_name.setText(arabic_title)
        } else{
            tv_service_name.setText(postTitle)
        }

       // tv_service_name.setText(postTitle)

        if(serviceCompanyModel.meta_data!=null){
            if(serviceCompanyModel.meta_data.address!=null){
                tv_company_address.text = serviceCompanyModel.meta_data.address.get(0)
            }

            if(serviceCompanyModel.meta_data.from!=null && serviceCompanyModel.meta_data.to!=null){
                try {
                    val displayFormat = SimpleDateFormat("hh:mm a", Locale.US)
                    val parseFormat = SimpleDateFormat("HH:mm",Locale.US)
                    val from = parseFormat.parse(serviceCompanyModel.meta_data.from.get(0))
                    var timeFrom = displayFormat.format(from!!)
                    if (timeFrom.contains("AM")){
                        timeFrom = timeFrom.replace("AM", resources.getString(R.string.am))
                    }
                    if (timeFrom.contains("PM")){
                        timeFrom = timeFrom.replace("PM", resources.getString(R.string.pm))
                    }
                    tv_time_from.setText(timeFrom)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }

                try {
                    val displayFormat = SimpleDateFormat("hh:mm a", Locale.US)
                    val parseFormat = SimpleDateFormat("HH:mm",Locale.US)
                    val to = parseFormat.parse(serviceCompanyModel.meta_data.to.get(0))
                    var timeto = displayFormat.format(to!!)
                    if (timeto.contains("AM")){
                        timeto = timeto.replace("AM", resources.getString(R.string.am))
                    }
                   if (timeto.contains("PM")){
                       timeto = timeto.replace("PM", resources.getString(R.string.pm))
                    }
                    tv_time_to.setText(timeto)
                } catch (e: ParseException) {
                    e.printStackTrace()
                }
                //Log.d("timeData", timeFrom + " to " + timeto)
                /*tv_company_timing.text = serviceCompanyModel.meta_data.from.get(0) +
                        resources.getString(R.string.am)+ " "+
                resources.getString(R.string.to) + " "+ serviceCompanyModel.meta_data.to.get(0)+
                        resources.getString(R.string.pm)*/
            }

        }

        if(serviceCompanyModel.data.post_content!=null){
            val html:String = serviceCompanyModel.data.post_content
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                tv_company_details.text = Html.fromHtml(html, Html.FROM_HTML_MODE_LEGACY);
            }else{
                tv_company_details.text = Html.fromHtml(html);
            }

        }

        if (serviceCompanyModel.meta_data != null){
            if (serviceCompanyModel.meta_data.facebook_url != null &&
                serviceCompanyModel.meta_data.facebook_url.isNotEmpty() &&
                !serviceCompanyModel.meta_data.instagram_url.equals("null")){
                    if(serviceCompanyModel.meta_data.facebook_url.get(0).isNotEmpty()){
                        social_fb.visibility = View.VISIBLE
                        social_fb.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(serviceCompanyModel.meta_data.facebook_url.get(0))
                            startActivity(i)
                        }
                    }else{
                        social_fb.visibility = View.GONE
                    }

            } else{
                social_fb.visibility = View.GONE
            }
        } else{
            social_fb.visibility = View.GONE
        }

        if (serviceCompanyModel.meta_data != null){
            if (serviceCompanyModel.meta_data.instagram_url != null &&
                serviceCompanyModel.meta_data.instagram_url.size>0 ){
                    if(serviceCompanyModel.meta_data.instagram_url.get(0).isNotEmpty()){
                        social_insta.visibility = View.VISIBLE
                        social_insta.setOnClickListener {
                            val i = Intent(Intent.ACTION_VIEW)
                            i.data = Uri.parse(serviceCompanyModel.meta_data.instagram_url.get(0))
                            startActivity(i)
                        }
                    }else{
                        social_insta.visibility = View.GONE
                    }

            } else{
                social_insta.visibility = View.GONE
            }
        } else{
            social_insta.visibility = View.GONE
        }


        if (serviceCompanyModel.meta_data != null){
            if (serviceCompanyModel.meta_data.whatsapp != null &&
                serviceCompanyModel.meta_data.whatsapp.isNotEmpty()){
                    if(serviceCompanyModel.meta_data.whatsapp.get(0).isNotEmpty()){
                        contact_whatsapp.visibility = View.VISIBLE
                        contact_whatsapp.setOnClickListener {
                            val intent = Intent(Intent.ACTION_VIEW)
                            val url = "https://api.whatsapp.com/send?phone="+
                                    serviceCompanyModel.meta_data.whatsapp.get(0)+
                                    "&text=" + URLEncoder.encode("", "UTF-8");
                            intent.data = Uri.parse(url)
                            startActivity(intent)
                        }
                    }else{
                        contact_whatsapp.visibility = View.GONE
                    }

            } else{
                contact_whatsapp.visibility = View.GONE
            }
        }else{
            contact_whatsapp.visibility = View.GONE
        }

        if (serviceCompanyModel.meta_data != null){
            if (serviceCompanyModel.meta_data.call_number != null &&
                serviceCompanyModel.meta_data.call_number.isNotEmpty()){
                    if(serviceCompanyModel.meta_data.call_number.get(0).isNotEmpty()){
                        contact_call.visibility = View.VISIBLE
                        contact_call.setOnClickListener {
                            val intent = Intent(Intent.ACTION_DIAL)
                            intent.data = Uri.parse("tel:" + serviceCompanyModel.meta_data.call_number.get(0))
                            startActivity(intent)
                        }
                    }else{
                        contact_call.visibility = View.GONE
                    }

            }else{
                contact_call.visibility = View.GONE
            }
        } else{
            contact_call.visibility = View.GONE
        }
    }

    private fun startAutoSlide() {
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(4000)
                if (currentIndex == (imagesList.size - 1)){
                    currentIndex = 0
                } else {
                    currentIndex += 1
                }
                img_auto_scroll.setCurrentItem(currentIndex, true)
            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        currentIndex = position;
        val childCount = indicatorLayout.childCount
        for (i in 0 until childCount) {
            val child = indicatorLayout.getChildAt(i)
            if (i == position) {
                child.setBackgroundResource(R.drawable.indicator_active)
            } else {
                child.setBackgroundResource(R.drawable.indicator_inactive)
            }
        }
    }

    private fun setupIndicators(count: Int) {
        indicatorLayout.removeAllViews()
        for (i in 0 until count) {
            val indicator = ImageView(this)
            indicator.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            (indicator.layoutParams as LinearLayout.LayoutParams).setMargins(8, 0, 8, 0)
            indicator.setBackgroundResource(R.drawable.indicator_inactive)
            indicatorLayout.addView(indicator)
        }
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
    }

}