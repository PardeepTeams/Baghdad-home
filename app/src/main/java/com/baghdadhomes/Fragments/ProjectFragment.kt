package com.baghdadhomes.Fragments


import android.content.res.Resources
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.baghdadhomes.Activities.BaseActivity
import com.baghdadhomes.Adapters.ProductsAdapter
import com.baghdadhomes.Adapters.ProductsBannerPagerAdapter
import com.baghdadhomes.Adapters.ProductsCityAdapter
import com.baghdadhomes.Models.HomeModel
import com.baghdadhomes.Models.ProjectCity
import com.baghdadhomes.Models.ProjectData
import com.baghdadhomes.Models.ProjectListModel
import com.baghdadhomes.Models.ProjectSlider
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProjectFragment : BaseFragment(), ProductsAdapter.openDetailPage {

    private var param1: String? = null
    private var param2: String? = null
    lateinit var bannerAdapter: ProductsBannerPagerAdapter
    var bannerList: ArrayList<ProjectSlider> = ArrayList()
    var cityList: ArrayList<ProjectCity> = ArrayList()
    var projectList: ArrayList<ProjectData> = ArrayList()
    lateinit var bannerPager: ViewPager2
    lateinit var rv_city: RecyclerView
    lateinit var rv_products: RecyclerView
    lateinit var productsCirtAdapter: ProductsCityAdapter
    lateinit var productsAdapter: ProductsAdapter
    lateinit var indicatorLayout: LinearLayout

    var totalCount: Int = 0
    var scrollPosition = 0
    var currentIndex = 0
    private var job: Job? = null
    lateinit var et_search_property: EditText
    lateinit var tv_search_count: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            param1 = it.getString(ARG_PARAM1)
            param2 = it.getString(ARG_PARAM2)
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_project, container, false)
    }


    override fun onResume() {
        super.onResume()
        val map: HashMap<Any, Any> = HashMap()
        if (PreferencesService.instance.userLoginStatus == true) {
            map["user_id"] = PreferencesService.instance.getUserData?.ID!!
        } else {
            map["device_id"] = Utility.getDeviceId(requireContext())
        }
        Log.d("NewMap", map.toString());
        if (isNetworkAvailable()) {
            hitPostApiWithoutTokenParams(
                Constants.GET_Project_Main,
                true,
                Constants.GET_Project_Main_API,
                map
            )
        } else {
            showToast(requireContext(), getString(R.string.intenet_error))
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view);





        bannerPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })


        bannerPager.clipToPadding = false
        bannerPager.clipChildren = false

        bannerPager.offscreenPageLimit = 3


        productsCirtAdapter = ProductsCityAdapter(requireActivity(), cityList)

        rv_city.adapter = productsCirtAdapter;

        productsAdapter = ProductsAdapter(requireActivity(), projectList, this)
        rv_products.adapter = productsAdapter
        rv_products.setHasFixedSize(true)
        rv_products.setItemViewCacheSize(10)
        rv_products.isNestedScrollingEnabled = false

    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.GET_Project_Main)) {
            val model = Gson().fromJson(respopnse, ProjectListModel::class.java)
            saveHomeData(model)

        }


    }


    fun saveHomeData(model: ProjectListModel) {
        if (model.success!!) {
            if (model.data != null) {
                projectList.clear()
                cityList.clear()
                bannerList.clear()

                if (model.data.allProjectSliders != null) {
                    bannerList.addAll(model.data.allProjectSliders)
                    bannerAdapter = ProductsBannerPagerAdapter(bannerList);
                    bannerPager.adapter = bannerAdapter
                    setupIndicators(bannerList.size)
                    setCurrentIndicator(0)
                    startAutoSlide()
                    bannerPager.addCarouselEffect(enableZoom = false)
                }
                if (model.data.allProjectCities != null) {
                    cityList.addAll(model.data.allProjectCities)
                    productsCirtAdapter.notifyDataSetChanged()
                }

                tv_search_count.visibility = View.VISIBLE
                if (model.data.projectsListing != null) {
                    projectList.addAll(model.data.projectsListing)
                    productsAdapter.notifyDataSetChanged()
                }
                tv_search_count.text =
                    projectList.size.toString() + " " + getString(R.string.project_found)

            }
        }
    }

    private fun setCurrentIndicator(position: Int) {
        currentIndex = position;
        val childCount = indicatorLayout.childCount
        for (i in 0 until childCount) {
            val child = indicatorLayout.getChildAt(i)
            if (i == position) {
                child.setBackgroundResource(R.drawable.indicator_active_white)
            } else {
                child.setBackgroundResource(R.drawable.indicator_inactive_grey)
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
            (indicator.layoutParams as LinearLayout.LayoutParams).setMargins(8, 0, 8, 0)
            indicator.setBackgroundResource(R.drawable.indicator_inactive_grey)
            indicatorLayout.addView(indicator)
        }
    }

    private fun startAutoSlide() {
        job?.cancel()
        job?.cancel() // cancel previous if any
        job = CoroutineScope(Dispatchers.Main).launch {
            while (job?.isActive == true) {
                delay(8000) // wait before moving
                if (bannerList.isNotEmpty()) {
                    currentIndex = (currentIndex + 1) % bannerList.size
                    bannerPager.setCurrentItem(currentIndex, true)
                }
            }
        }
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

    fun initView(view: View) {
        bannerPager = view.findViewById(R.id.bannerPager)
        rv_city = view.findViewById(R.id.rv_city)
        rv_products = view.findViewById(R.id.rv_products)
        indicatorLayout = view.findViewById(R.id.indicatorLayout)
        et_search_property = view.findViewById(R.id.et_search_property)
        tv_search_count = view.findViewById(R.id.tv_search_count)

        et_search_property.setOnClickListener {
            if (projectList.isEmpty()) {
                showToast(requireActivity(), "No Project found ")
            } else {
                SelectProjectBottomSheet.show(
                    parentFragmentManager,
                    projectList ?: emptyList()
                )
            }

        }

    }

    companion object {
        @JvmStatic
        fun newInstance(param1: String, param2: String) =
            ProjectFragment().apply {
                arguments = Bundle().apply {
                    putString(ARG_PARAM1, param1)
                    putString(ARG_PARAM2, param2)
                }
            }
    }

    fun ViewPager2.addCarouselEffect(enableZoom: Boolean = true) {
        clipChildren = false    // No clipping the left and right items
        clipToPadding = false   // Show the viewpager in full width without clipping the padding
        offscreenPageLimit = 3  // Render the left and right items
        (getChildAt(0) as RecyclerView).overScrollMode =
            RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

        val compositePageTransformer = CompositePageTransformer()
        compositePageTransformer.addTransformer(MarginPageTransformer((5 * Resources.getSystem().displayMetrics.density).toInt()))
        if (enableZoom) {
            compositePageTransformer.addTransformer { page, position ->
                val r = 1 - abs(position)
                page.scaleY = (0.80f + r * 0.20f)
            }
        }
        setPageTransformer(compositePageTransformer)
    }

    override fun addRemoveFav(model: ProjectData?, position: Int) {
        if (isNetworkAvailable()) {
            val propId = model!!.id
            val userData = PreferencesService.instance.getUserData

            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId.toString())
            //map.put("", favStatus)
            projectList.get(position).isFav = projectList.get(position).isFav != true
            productsAdapter.notifyDataSetChanged()
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)

        }
    }

    override fun openLoginActivity() {
        ((context) as BaseActivity).loginTypeDialog(false)
    }
}