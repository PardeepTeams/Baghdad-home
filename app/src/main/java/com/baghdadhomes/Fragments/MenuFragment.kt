package com.baghdadhomes.Fragments

//import com.baghdadhomes.Adapters.AdapterAutoSlider
import android.Manifest
import android.content.Intent
import android.content.res.Resources
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.cardview.widget.CardView
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.amar.library.ui.StickyScrollView
import com.amar.library.ui.interfaces.IScrollViewListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Activities.*
import com.baghdadhomes.Adapters.AdapterAutoSlider
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Adapters.AdapterDetailAds.openDetailPage
import com.baghdadhomes.Adapters.AdapterFeatureAds
import com.baghdadhomes.Adapters.AdapterStories
import com.baghdadhomes.Adapters.GridAdapterDetailAds
import com.baghdadhomes.Models.*
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.ProgressHud
import com.baghdadhomes.Utils.Utility
import com.baghdadhomes.Utils.ZoomOutPageTransformer
import com.baghdadhomes.fcm.ApiClient
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import kotlin.math.abs

/*import com.smarteist.autoimageslider.IndicatorView.animation.type.IndicatorAnimationType
import com.smarteist.autoimageslider.SliderAnimations
import com.smarteist.autoimageslider.SliderView*/


class MenuFragment : BaseFragment(), openDetailPage, AdapterFeatureAds.openFeatureDetailPage,
    AdapterStories.StoryClick,GridAdapterDetailAds.openDetailPage {
    lateinit var ll_main: LinearLayout
    lateinit var rv_ads_type: RecyclerView
    lateinit var rv_detail_ads: RecyclerView
    lateinit var tv_all: TextView
    lateinit var tv_residential: TextView
    lateinit var tv_commercial: TextView
    lateinit var tv_land: TextView
    lateinit var adapterADs: AdapterFeatureAds
    lateinit var adapterDetailAds: AdapterDetailAds
    lateinit var gridAdapterDetailAds: GridAdapterDetailAds
    lateinit var img_auto_scroll: ViewPager2
    lateinit var indicatorLayout: LinearLayout
    lateinit var scrollView: StickyScrollView
    lateinit var title: LinearLayout
    lateinit var ll_to_hide: LinearLayout
    lateinit var rl_search: RelativeLayout
    lateinit var rl_filter: RelativeLayout
    lateinit var rlAddReel: RelativeLayout
    lateinit var scrollViewStories: HorizontalScrollView
    lateinit var rvStories: RecyclerView
    lateinit var et_search_property: EditText
    lateinit var tv_search_count: TextView
    lateinit var rlListGrid: RelativeLayout
    lateinit var imgListGrid: ImageView
    lateinit var card_search: CardView
    var keyword:String = ""
    var feautredList:ArrayList<Result> = ArrayList()
    var propertiesList:ArrayList<Result> = ArrayList()
    val arrayList: ArrayList<BannerData> = ArrayList()
    var type:String = "all"
    var page:Int = 0

    var reelsList : ArrayList<ReelsData> = ArrayList()
    lateinit var adapterStories :  AdapterStories

    var loading: Boolean = true
    var totalCount:Int = 0
    var scrollPosition = 0
    var currentIndex = 0
    private var job: Job? = null
    var isGrid = true;
    lateinit var tv_all_properties:TextView
    lateinit var tv_see_all:TextView
    lateinit var swipe_refresh:SwipeRefreshLayout

    override fun onResume() {
        super.onResume()

        try {
            if(PreferencesService.instance.getAdsId() !=null && PreferencesService.instance.getAdsId()!!
                    .isNotEmpty()){
               val isFav =  PreferencesService.instance.getIsFav()
                for(i in propertiesList){
                    if(i.iD ==PreferencesService.instance.getAdsId() ){
                        i.is_fav = isFav
                    }
                }
                gridAdapterDetailAds.notifyDataSetChanged()
                adapterDetailAds.notifyDataSetChanged()

                PreferencesService.instance.saveChangedPropertyDataId("",false)
            }
        } catch (e: java.lang.Exception){ }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_menu, container, false)
    }

    private fun startAutoSlide() {
        job?.cancel()
        job?.cancel() // cancel previous if any
        job = CoroutineScope(Dispatchers.Main).launch {
            while (job?.isActive == true) {
                delay(8000) // wait before moving
                if (arrayList.isNotEmpty()) {
                    currentIndex = (currentIndex + 1) % arrayList.size
                    img_auto_scroll.setCurrentItem(currentIndex, true)
                }
            }
        }
     /*   job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(8000)
                if (currentIndex == (arrayList.size - 1)){
                    currentIndex = 0
                } else {
                    currentIndex += 1
                }
                img_auto_scroll.setCurrentItem(currentIndex, true)
            }
        }*/
    }

    override fun onDestroy() {
        super.onDestroy()
        job?.cancel()
        job = null
    }
    override fun onPause() {
        super.onPause()
        job?.cancel()
        job = null
    }

    fun saveFeaturedData(model: NewFeatureModel){
        scrollView.visibility = View.VISIBLE
        if(model.success){
            //  PreferencesService.instance.saveFeaturedData(model)
        }
    }
    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if(apiType == Constants.GETFEATURED){
            val model: NewFeatureModel = Gson().fromJson(respopnse,NewFeatureModel::class.java)
            if(page == 1){
                feautredList.clear()
            }

            try {
                if (progressHUD != null && progressHUD!!.isShowing()) {
                    progressHUD!!.dismiss()
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
            /*Handler().postDelayed(Runnable {
                try {
                    if (progressHUD != null && progressHUD!!.isShowing()) {
                        progressHUD!!.dismiss()
                    }
                } catch (e: Exception) {
                    e.printStackTrace()
                }
            },1000)*/
        }else if(apiType == Constants.GET_PROPERTIES){
            Thread{
                val model: NewFeatureModel = Gson().fromJson(respopnse,NewFeatureModel::class.java)
                if(model.success){
                    activity?.runOnUiThread {
                        if(model.count!=null){
                            totalCount = model.count
                        }
                        if(page == 1){
                            propertiesList.clear()
                            saveFeaturedData(model)
                            try {
                                if(scrollPosition>0){
                                    scrollView.scrollTo(0, rv_detail_ads.y.toInt()-1)
                                }else{
                                    scrollView.scrollTo(0,0)
                                }

                            }catch (e:Exception){
                                e.toString()
                            }

                        }
                        propertiesList.addAll(model.result)
                        tv_search_count.text = totalCount.toString()+" "+ resources.getString(R.string.property_found)
                        //adapterDetailAds.notifyDataSetChanged()
                        val lastcount = propertiesList.size
                        if(lastcount == 0 || page == 1){
                            adapterDetailAds.notifyItemRangeInserted(lastcount,propertiesList.size-1)
                            gridAdapterDetailAds.notifyItemRangeInserted(lastcount,propertiesList.size-1)
                        }else{
                            adapterDetailAds.notifyItemRangeInserted(lastcount,propertiesList.size-1)
                            gridAdapterDetailAds.notifyItemRangeInserted(lastcount,propertiesList.size-1)
                        }
                        loading = true
                    }

                }
            }.start()


        } else if (apiType == Constants.GET_BANNER){
            val bannerModel = Gson().fromJson(respopnse, HomeBannerModel::class.java)
            if (bannerModel.status != false){
                arrayList.addAll(bannerModel.data!!)
                img_auto_scroll.adapter = AdapterAutoSlider(requireActivity(), arrayList)
                setupIndicators(arrayList.size)
                setCurrentIndicator(0)
                startAutoSlide()
                img_auto_scroll.addCarouselEffect(enableZoom = true)
                /*img_auto_scroll.setSliderAdapter(AdapterAutoSlider(requireActivity(), arrayList))
                img_auto_scroll.setIndicatorAnimation(IndicatorAnimationType.WORM) //set indicator animation by using IndicatorAnimationType. :WORM or THIN_WORM or COLOR or DROP or FILL or NONE or SCALE or SCALE_DOWN or SLIDE and SWAP!!
                img_auto_scroll.setSliderTransformAnimation(SliderAnimations.SIMPLETRANSFORMATION)
                img_auto_scroll.autoCycleDirection = SliderView.LAYOUT_DIRECTION_LTR
                img_auto_scroll.indicatorRadius = 4
                img_auto_scroll.indicatorSelectedColor = ContextCompat.getColor(requireActivity(), R.color.skyBlue)
                img_auto_scroll.indicatorUnselectedColor = ContextCompat.getColor(requireActivity(), R.color.lightBlue)
                img_auto_scroll.scrollTimeInSec = 4 //set scroll delay in seconds :
                img_auto_scroll.startAutoCycle()*/
            }
        } else if (apiType == Constants.ADD_REMOVE_FAV){
            /*var model: FeaturedPropertiesModel = Gson().fromJson(respopnse,FeaturedPropertiesModel::class.java)
            if (model.success){
                adapterDetailAds.notif
            }*/
        } else if (apiType == Constants.GET_REELS){
            val model = Gson().fromJson(respopnse,ReelResult::class.java)
            if (model.success == true){
                reelsList.clear()
                if (!model.response.isNullOrEmpty()){
                    reelsList.addAll(model.response)
                }
                adapterStories.notifyItemRangeChanged(0,reelsList.size)
                try {
                    if (PreferencesService.instance.getLanguage() == "ar"){
                        scrollViewStories.post {
                            scrollViewStories.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                        }
                    }
                } catch (e : Exception){
                    e.localizedMessage
                }
            } else{
                if (!model.message.isNullOrEmpty()){
                    showToast(requireContext(),model.message.toString())
                } else {
                    showToast(requireContext(), getString(R.string.something_went_wrong))
                }
            }
        }else if(apiType == Constants.GET_HOME){
            Thread {
                val model = Gson().fromJson(respopnse,HomeModel::class.java)
                saveHomeData(model)
            }.start()
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
            val indicator = ImageView(requireContext())
            indicator.setLayoutParams(
                LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.WRAP_CONTENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT
                )
            )
            (indicator.layoutParams as LinearLayout.LayoutParams).setMargins(5, 0, 5, 0)
            indicator.setBackgroundResource(R.drawable.indicator_inactive)
            indicatorLayout.addView(indicator)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        ll_main = view.findViewById(R.id.ll_main)
        ll_to_hide = view.findViewById(R.id.ll_to_hide)
        tv_search_count = view.findViewById(R.id.tv_search_count)
        scrollView = view.findViewById(R.id.scrollView)
        title = view.findViewById(R.id.title)
        img_auto_scroll = view.findViewById(R.id.img_auto_scroll)
        rv_ads_type = view.findViewById(R.id.rv_ads_type)
        rv_detail_ads = view.findViewById(R.id.rv_detail_ads)

        tv_all_properties = view.findViewById(R.id.tv_all_properties)
        tv_see_all = view.findViewById(R.id.tv_see_all)
        card_search = view.findViewById(R.id.card_search)
        tv_all_properties.text = capitalizeFirstLetter(type) + " " + getString(R.string.ads)
        getReels()
        swipe_refresh = view.findViewById(R.id.swipe_refresh)
        swipe_refresh.setOnRefreshListener {
            swipe_refresh.isRefreshing = false;
           getReels()
        }


        tv_see_all.setOnClickListener {
            var intent:Intent = Intent(requireContext(),AllPropertiesActivity::class.java)
            intent.putExtra("type",type)
            startActivity(intent)
            requireActivity().overridePendingTransition(0,0)

        }

        tv_all = view.findViewById(R.id.tv_all)
        tv_residential = view.findViewById(R.id.tv_residential)
        tv_commercial = view.findViewById(R.id.tv_commercial)
        tv_land = view.findViewById(R.id.tv_land)
        rl_search = view.findViewById(R.id.rl_search)
        rl_filter = view.findViewById(R.id.rl_filter)
        et_search_property = view.findViewById(R.id.et_search_property)
        scrollViewStories = view.findViewById(R.id.scrollViewStories)
        rlAddReel = view.findViewById(R.id.rlAddReel)
        rvStories = view.findViewById(R.id.rvStories)
        rlListGrid = view.findViewById(R.id.rlListGrid)
        imgListGrid = view.findViewById(R.id.imgListGrid)
        indicatorLayout = view.findViewById(R.id.indicatorLayout)

        /*if (isNetworkAvailable()){
            hitGetApiWithoutToken(Constants.GET_BANNER, false, Constants.GET_HOME_BANNER)
        } else{
            //showToast(requireContext(), resources.getString(R.string.intenet_error))
        }*/




        //  getProperties(true)

        changeAdapters()

        img_auto_scroll.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })

        rvStories.layoutManager = LinearLayoutManager(context,LinearLayoutManager.HORIZONTAL,false)
        adapterStories = AdapterStories(requireContext(),reelsList,this)
        rvStories.adapter = adapterStories

        adapterADs = AdapterFeatureAds(context,feautredList,this)
        rv_ads_type.layoutManager = LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
        rv_ads_type.adapter = adapterADs

        adapterDetailAds = AdapterDetailAds(context, this, propertiesList /*false, true*/)
        gridAdapterDetailAds = GridAdapterDetailAds(context, this, propertiesList /*false, true*/)
        //rv_detail_ads.setLayoutManager(LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false))
        //rv_detail_ads.setAdapter(adapterDetailAds)

        rv_detail_ads.isNestedScrollingEnabled = false
        scrollView.isSmoothScrollingEnabled = true

        imgListGrid.contentDescription = "Grid"
        imgListGrid.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_list))

        /*rv_detail_ads.layoutManager = GridLayoutManager(context, 2)
        rv_detail_ads.adapter = adapterDetailAds*/
        if(isGrid){
            imgListGrid.contentDescription = "Grid"
            imgListGrid.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_list))

            rv_detail_ads.layoutManager = GridLayoutManager(context, 2)
            rv_detail_ads.adapter = gridAdapterDetailAds

        }else{
            imgListGrid.contentDescription = "List"
            imgListGrid.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid))

            rv_detail_ads.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            rv_detail_ads.adapter = adapterDetailAds
        }
        /*   if (adapterDetailAds.isGrid == true){
               imgListGrid.contentDescription = "Grid"
               imgListGrid.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_list))

               rv_detail_ads.layoutManager = GridLayoutManager(context, 2)
               rv_detail_ads.adapter = adapterDetailAds
           } else {
               imgListGrid.contentDescription = "List"
               imgListGrid.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid))

               rv_detail_ads.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
               rv_detail_ads.adapter = adapterDetailAds
           }*/

        rlListGrid.setOnClickListener {
            if (imgListGrid.contentDescription == "Grid"){
                imgListGrid.contentDescription = "List"
                imgListGrid.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_grid))

                // adapterDetailAds.isGrid = false

                rv_detail_ads.layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
                rv_detail_ads.adapter = adapterDetailAds
            } else {
                imgListGrid.contentDescription = "Grid"
                imgListGrid.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.ic_list))


                rv_detail_ads.layoutManager = GridLayoutManager(context, 2)
                rv_detail_ads.adapter = gridAdapterDetailAds
            }
        }

        /*  if (isNetworkAvailable()){
              val map:HashMap<String,String> = HashMap()
              map["featured"] = "1"
              map["page"] = "1"
              map["per_page"] = "10"
              if(PreferencesService.instance.userLoginStatus == true){
                  map["user_id"] = PreferencesService.instance.getUserData!!.ID.toString()
              }
              hitPostApi(Constants.GETFEATURED,true,Constants.GETFEATUREDPROPERTIES,map)
              getProperties(false)
          } else{
              showToast(requireContext(), resources.getString(R.string.intenet_error))
          }*/

        rv_detail_ads.isNestedScrollingEnabled = false

        scrollView.setScrollViewListener(object : IScrollViewListener {

            override fun onScrollChanged(l: Int, t: Int, oldl: Int, oldt: Int) {
                context!!.dismissKeyboard(view)
                scrollPosition = t
                if (scrollView.isHeaderSticky) {
                    title.setBackgroundResource(android.R.color.white)
                    if(loading){
                        val childView  = scrollView.getChildAt(scrollView.childCount - 1) as View
                        val diff: Int = childView.bottom - (scrollView.height + scrollView.scrollY)
                        if (diff == 0) {
                            if (propertiesList.size > 0) {
                                if (isNetworkAvailable()){
                                    loading = false
                                    fetchProperties(true)
                                }else{
                                    showToast(requireContext(),resources.getString(R.string.intenet_error))
                                }

                            }
                        }
                    }

                } else {
                    title.setBackgroundResource(android.R.color.transparent)
                }
            }

            override fun onScrollStopped(isStopped: Boolean) {}
        })

        rl_filter.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
            requireActivity().overridePendingTransition(0, 0)
        }

        card_search.setOnClickListener {
            startActivity(Intent(activity, SearchActivity::class.java))
            requireActivity().overridePendingTransition(0, 0)
        }

        et_search_property.setOnClickListener {
            card_search.performClick()
        }

        et_search_property.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                requireActivity().dismissKeyboard(et_search_property)
                if(isNetworkAvailable()){
                    page = 0
                    keyword = et_search_property.text.toString()
                    fetchProperties(true)
                    // getProperties(true)
                    ll_to_hide.visibility = View.GONE
                    title.visibility = View.GONE
                    tv_search_count.visibility = View.VISIBLE
                }else{
                    Utility.showToast(requireContext(),getString(R.string.intenet_error))
                }

                /*if (propertiesList.size >= 0){
                    tv_search_count.setText(totalCount.toString()+" "+ resources.getString(R.string.property_found))
                } else{
                    tv_search_count.setText("0 "+ resources.getString(R.string.property_found))
                }*/

                true
            } else {
                false
            }
        }

        et_search_property.addTextChangedListener(object: TextWatcher {
            override fun beforeTextChanged(s:CharSequence, start:Int, count:Int, after:Int){}
            override fun onTextChanged(s:CharSequence, start:Int, before:Int, count:Int) {
                //ll_to_hide.visibility = View.GONE
                keyword = et_search_property.text.toString()
                /*et_search_property.setOnEditorActionListener { v, actionId, event ->
                    if(et_search_property.text.toString().isNotEmpty()){
                        var intent:Intent = Intent(requireActivity(), PropertiesSearchActivity::class.java)
                        intent.putExtra("search_text",keyword)
                        startActivity(intent)
                        true
                    }else{
                        false
                    }
                }*/

                if(et_search_property.text.isEmpty()){

                    if (isNetworkAvailable()){
                        ll_to_hide.visibility = View.VISIBLE
                        title.visibility = View.VISIBLE
                        tv_search_count.visibility = View.GONE
                        page = 0
                        keyword = ""
                        fetchProperties(true)
                    }else{
                        showToast(requireContext(),getString(R.string.intenet_error))
                    }
                }
            }
            override fun afterTextChanged(s:Editable){}
        })

        rlAddReel.setOnClickListener {
            ((context) as HomeActivity).rl_add.performClick()
        }
    }

    private fun changeAdapters() {
        tv_all.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_blue_square)
        tv_residential.background = null
        tv_commercial.background = null
        tv_land.background = null
        tv_all.setTextColor(ContextCompat.getColor(requireContext(),R.color.whiteNew))
        tv_residential.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
        tv_commercial.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
        tv_land.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
        tv_all.setOnClickListener {
            if(isNetworkAvailable()){
                type = "all"
                tv_all_properties.text = capitalizeFirstLetter(type) + " " + getString(R.string.ads)
                tv_all.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_blue_square)
                tv_residential.background = null
                tv_commercial.background = null
                tv_land.background = null
                tv_all.setTextColor(ContextCompat.getColor(requireContext(),R.color.whiteNew))
                tv_residential.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_commercial.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_land.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                page = 0
                fetchProperties(true)
                // getProperties(true)
            }else{
                Utility.showToast(requireContext(), resources.getString(R.string.intenet_error))
            }

        }
        tv_residential.setOnClickListener {
            if(isNetworkAvailable()){
                type = "residential"
                tv_all_properties.text = capitalizeFirstLetter(type) + " " + getString(R.string.ads)
                tv_all.background = null
                tv_residential.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_blue_square)
                tv_commercial.background = null
                tv_land.background = null
                tv_all.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_residential.setTextColor(ContextCompat.getColor(requireContext(),R.color.whiteNew))
                tv_commercial.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_land.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                page = 0
                fetchProperties(true)
                // getProperties(true)
            }else{
                Utility.showToast(requireContext(), resources.getString(R.string.intenet_error))
            }

        }
        tv_commercial.setOnClickListener {
            if(isNetworkAvailable()){
                type = "commercial"
                tv_all_properties.text = capitalizeFirstLetter(type) + " " + getString(R.string.ads)
                tv_all.background = null
                tv_residential.background = null
                tv_commercial.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_blue_square)
                tv_land.background = null
                tv_all.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_residential.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_commercial.setTextColor(ContextCompat.getColor(requireContext(),R.color.whiteNew))
                tv_land.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                page = 0
                fetchProperties(true)
            }else{
                Utility.showToast(requireContext(), resources.getString(R.string.intenet_error))
            }

        }
        tv_land.setOnClickListener {
            if(isNetworkAvailable()){
                type = "apartments"
                tv_all_properties.text = capitalizeFirstLetter(type) + " " + getString(R.string.ads)
                tv_all.background = null
                tv_residential.background = null
                tv_commercial.background = null
                tv_land.background = ContextCompat.getDrawable(requireContext(), R.drawable.bg_blue_square)
                tv_all.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_residential.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_commercial.setTextColor(ContextCompat.getColor(requireContext(),R.color.darkGrey))
                tv_land.setTextColor(ContextCompat.getColor(requireContext(),R.color.whiteNew))
                page = 0
                fetchProperties(true)
                //getProperties(true)
            }else{
                Utility.showToast(requireContext(), resources.getString(R.string.intenet_error))
            }

        }
    }

    fun getProperties(show:Boolean){
        if (isNetworkAvailable()){
            if(propertiesList.size<totalCount){
                page += 1
                val pageMap:HashMap<String,String> = HashMap()
                pageMap["page"] = page.toString()
                pageMap["per_page"] = "10"
                pageMap["keyword"] = keyword
                if(PreferencesService.instance.userLoginStatus == true){
                    pageMap["user_id"] = PreferencesService.instance.getUserData!!.ID.toString()
                }
                if(type != "all"){
                    pageMap["type"] = type
                }
                hitGetApiWithoutTokenWithParams(Constants.GET_PROPERTIES,show,Constants.GETFEATUREDPROPERTIESNEW, pageMap)
            }else if(page == 0){
                page += 1
                val pageMap:HashMap<String,String> = HashMap()
                pageMap["page"] = page.toString()
                pageMap["per_page"] = "10"
                pageMap["keyword"] = keyword
                if(type != "all"){
                    pageMap["type"] = type
                }
                if(PreferencesService.instance.userLoginStatus == true){
                    pageMap["user_id"] = PreferencesService.instance.getUserData!!.ID.toString()
                }
                hitGetApiWithoutTokenWithParams(Constants.GET_PROPERTIES,show,Constants.GETFEATUREDPROPERTIESNEW, pageMap)
            }
        } else{
            if(PreferencesService.instance.getFeaturedData!=null){
                saveFeaturedData(PreferencesService.instance.getFeaturedData!!)
                propertiesList.addAll(PreferencesService.instance.getFeaturedData!!.result)
                tv_search_count.text = totalCount.toString()+" "+ resources.getString(R.string.property_found)
            }
        }


    }

    private fun getReels(){

        val map : HashMap<String,String> = HashMap()
        if (PreferencesService.instance.userLoginStatus == true){
            map["user_id"] = PreferencesService.instance.getUserData?.ID!!
        } else{
            map["device_id"] = Utility.getDeviceId(requireContext())
        }
        map["featured"] ="1"
        if (isNetworkAvailable()){
            scrollView.scrollTo(0,0)
            hitGetApiWithoutTokenWithParams(Constants.GET_HOME,true,Constants.GET_HOME_API,map)
        } else{
            showToast(requireContext(), getString(R.string.intenet_error))
        }
    }

    override fun openNextActivity(model: Result?, position: Int) {
        val intent = Intent(activity,AdsDetailsActivity::class.java)
        intent.putExtra("type",type)
        intent.putExtra("propertyId",model!!.iD)
        intent.putExtra("view_count",model!!.totalViews)
        intent.putExtra("myAd",false)
        startActivity(intent)
        requireActivity().overridePendingTransition(0, 0)
    }

    override fun openFeatureDetail(model: Result?) {
        val intent = Intent(activity,AdsDetailsActivity::class.java)
        intent.putExtra("type",type)
        intent.putExtra("propertyId",model!!.iD)
        intent.putExtra("view_count",model!!.totalViews)
        // intent.putExtra("model",Gson().toJson(model))
        intent.putExtra("myAd",false)
        startActivity(intent)
        requireActivity().overridePendingTransition(0, 0)
    }

    override fun editAd(model: Result?) {

    }

    override fun deleteAd(model: Result?, position: Int) {

    }

    override fun addRemoveFav(model: Result?, position: Int) {
        if(isNetworkAvailable()){
            val propId = model!!.iD
            val userData = PreferencesService.instance.getUserData

            /*var favStatus = model.is_fav
            if(favStatus == true){
                favStatus = false
            }else{
                favStatus = true
            }*/
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId)
            //map.put("", favStatus)
            propertiesList.get(position).is_fav = propertiesList.get(position).is_fav != true
            adapterDetailAds.notifyDataSetChanged()
            gridAdapterDetailAds.notifyDataSetChanged()
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)

        }

    }

    override fun openLoginActivity() {
        ((context) as BaseActivity).loginTypeDialog(false)
        //startActivity(Intent(activity, LoginActivity::class.java))
        //requireActivity().overridePendingTransition(0,0)
    }

    override fun onStoryClick(position: Int) {
        val model = ReelResult(0,"",true,reelsList)
        Constants.reelsModel = model
        requireActivity().runOnUiThread {
            val intent = Intent(activity,VideoViewActivity::class.java)
            intent.putExtra("position",position)
            startActivity(intent)
            requireActivity().overridePendingTransition(0, 0)
        }

    }

    private  fun saveHomeData(model:HomeModel){
        if (model.success == true){
            if(model.data!=null){
                activity?.runOnUiThread {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                        if (!hasPermissions(Manifest.permission.POST_NOTIFICATIONS)){
                            ActivityCompat.requestPermissions(requireActivity(), arrayOf(Manifest.permission.POST_NOTIFICATIONS),101)
                        }
                    }
                    reelsList.clear()
                    if (!model.data.reels_listing.isNullOrEmpty()){
                        reelsList.addAll(model.data.reels_listing)
                        // reelsList.add(model.data.reels_listing.get(0))
                    }
                    adapterStories.notifyItemRangeChanged(0,reelsList.size)
                    arrayList.clear()
                    arrayList.addAll(model.data.all_sliders!!)
                    img_auto_scroll.adapter = AdapterAutoSlider(requireActivity(), arrayList)
                    setupIndicators(arrayList.size)
                    setCurrentIndicator(0)
                    if (arrayList.size>1) {
                        img_auto_scroll.setCurrentItem(1,true)
                    }
                    startAutoSlide()
                   // img_auto_scroll.setPageTransformer(ZoomOutPageTransformer())
                    img_auto_scroll.addCarouselEffect(enableZoom = true)

                    feautredList.clear()
                    scrollView.visibility = View.VISIBLE
                    feautredList.addAll(model.data.featured_listing)
                    adapterADs.notifyDataSetChanged()


                    page = 0
                    loading = false
                    scrollView.scrollTo(0,0)
                    fetchProperties(false)
                    try {
                        if (PreferencesService.instance.getLanguage() == "ar"){
                            scrollViewStories.post {
                                scrollViewStories.fullScroll(HorizontalScrollView.FOCUS_RIGHT)
                            }
                        }
                    } catch (e : Exception){
                        e.localizedMessage
                    }
                }
                // PreferencesService.instance.saveHomeData(model)

            }

        }else{
            if (!model.message.isNullOrEmpty()){
                showToast(requireContext(),model.message.toString())
            } else {
                showToast(requireContext(), getString(R.string.something_went_wrong))
            }
        }
    }



    fun fetchProperties(showLoader:Boolean){

        page = page +1
        val pagemap:HashMap<String,String> = HashMap()
        pagemap.put("page",page.toString())
        pagemap.put("per_page","10")
        pagemap["keyword"] = keyword
        if(!type.equals("all")){
            pagemap.put("type",type)
        }
        if(PreferencesService.instance.userLoginStatus == true){
            pagemap.put("user_id",PreferencesService.instance.getUserData!!.ID.toString())
        }
        lifecycleScope.launch {
            if(showLoader){
                try {
                    if(progressHUD!=null){
                        progressHUD!!.dismiss()
                    }
                }catch (e:Exception){

                }
                progressHUD = ProgressHud.show(
                    requireActivity(),
                    false,
                    false
                )
            }
            try {
                val response = ApiClient.api?.getProperties(ApiClient.baseUrl + Constants.GETFEATUREDPROPERTIESNEW,pagemap)

                if (response != null) {
                    if (response.isSuccessful && response.code() == 200) {
                        val model: NewFeatureModel? = response.body()
                        if (model != null) {
                            if(model.success){
                                activity?.runOnUiThread {
                                    if(model.count!=null){
                                        totalCount = model.count
                                    }
                                    if(page == 1){
                                        propertiesList.clear()
                                        scrollView.visibility = View.VISIBLE
                                    }

                                    tv_search_count.text = totalCount.toString()+" "+ resources.getString(R.string.property_found)
                                    val lastcount = propertiesList.size
                                    if(lastcount == 0 || page == 1){
                                        propertiesList.addAll(model.result)
                                        adapterDetailAds.notifyDataSetChanged()
                                        gridAdapterDetailAds.notifyDataSetChanged()
                                        tv_see_all.visibility = View.VISIBLE
                                    }else{
                                        propertiesList.addAll(model.result)
                                        adapterDetailAds.notifyDataSetChanged()
                                        gridAdapterDetailAds.notifyDataSetChanged()
                                    }
                                    rv_detail_ads.post(Runnable { rv_detail_ads.requestLayout() })
                                    loading = true
                                    if(showLoader){
                                        try {
                                            try {
                                                lifecycleScope.launch(Dispatchers.Main) {
                                                    delay(100)
                                                    progressHUD?.dismiss() // Dismiss after delay
                                                }
                                            }catch (e:Exception){

                                            }

                                        }catch (e:Exception){

                                        }
                                    }
                                }

                            }else{
                                activity?.runOnUiThread{
                                    Toast.makeText(
                                        requireActivity(),
                                        resources.getString(R.string.something_went_wrong),
                                        Toast.LENGTH_SHORT
                                    )
                                        .show()
                                    if(showLoader){
                                        try {
                                            if(progressHUD!=null){
                                                progressHUD!!.dismiss()
                                            }
                                        }catch (e:Exception){

                                        }
                                    }
                                }
                            }
                        }

                    }else if (response.code()==400) {
                        activity?.runOnUiThread{
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string())
                                //  Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                                Utility.showToast(requireActivity(), jObjError.getString(Constants.MESSAGE))
                            } catch (e: Exception) {
                            }
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }
                                }catch (e:Exception){

                                }
                            }
                        }

                    }
                    else if (response.code() == 401) {
                        activity?.runOnUiThread{
                            if(showLoader){
                                try {
                                    if(progressHUD!=null){
                                        progressHUD!!.dismiss()
                                    }
                                }catch (e:Exception){

                                }
                            }
                            Toast.makeText(
                                requireActivity(),
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (response.code() == 403 || response.code() == 404) {
                        if(showLoader){
                            try {
                                if(progressHUD!=null){
                                    progressHUD!!.dismiss()
                                }
                            }catch (e:Exception){

                            }
                        }
                        activity?.runOnUiThread{
                            Toast.makeText(
                                requireActivity(),
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }
                    } else if (response.code() == 409) {
                        if(showLoader){
                            try {
                                if(progressHUD!=null){
                                    progressHUD!!.dismiss()
                                }
                            }catch (e:Exception){

                            }
                        }
                        activity?.runOnUiThread{
                            try {
                                val jObjError = JSONObject(response.errorBody()!!.string());
                                // Utility.showSnackBar(this.findViewById(android.R.id.content),jObjError.getString(Constants.MESSAGE),this)
                                Utility.showToast(requireActivity(), jObjError.getString(Constants.MESSAGE))
                            } catch (e: Exception) {
                            }
                        }

                    } else {
                        if(showLoader){
                            try {
                                if(progressHUD!=null){
                                    progressHUD!!.dismiss()
                                }
                            }catch (e:Exception){

                            }
                        }
                        activity?.runOnUiThread {
                            Toast.makeText(
                                requireActivity(),
                                resources.getString(R.string.something_went_wrong),
                                Toast.LENGTH_SHORT
                            )
                                .show()
                        }

                    }
                }
            } catch (e: Exception) {
                if(showLoader){
                    try {
                        if(progressHUD!=null){
                            progressHUD!!.dismiss()
                        }
                    }catch (e:Exception){

                    }
                }
                activity?.runOnUiThread {
                    Log.d("callJason",e.message.toString())
                    Toast.makeText(
                        requireActivity(),
                        resources.getString(R.string.something_went_wrong),
                        Toast.LENGTH_SHORT
                    )
                        .show()
                }

            }
        }
    }

    fun capitalizeFirstLetter(input: String): String {
        return input.replaceFirstChar { it.uppercase() }
    }

    fun ViewPager2.addCarouselEffect(enableZoom: Boolean = true) {
        val density = Resources.getSystem().displayMetrics.density

        // Convert dp to px
        val nextItemVisiblePx = (32 * density).toInt()  // 40dp side padding
        val currentItemHorizontalMarginPx = (2 * density).toInt()

        setPadding(nextItemVisiblePx, 0, nextItemVisiblePx, 0)

        clipToPadding = false
        clipChildren = false
        offscreenPageLimit = 3
        (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(
            MarginPageTransformer(currentItemHorizontalMarginPx)
        )

        if (enableZoom) {
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                val scale = 0.90f + r * 0.15f
                page.scaleY = scale
                page.scaleX = scale

                val cardView = page as? com.google.android.material.card.MaterialCardView
                cardView?.let {
                    val maxRadius = 15f * density // side item corner radius
                    val minRadius = 40f * density  // center item corner radius
                    it.radius = minRadius + (1 - r) * (maxRadius - minRadius)
                }
            }
        }

        setPageTransformer(compositePageTransformer)
    }


}

