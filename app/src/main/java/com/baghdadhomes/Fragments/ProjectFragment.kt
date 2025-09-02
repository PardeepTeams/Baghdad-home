package com.baghdadhomes.Fragments



import android.content.res.Resources
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ImageView
import android.widget.LinearLayout
import androidx.core.view.ViewCompat
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.RecyclerView
import androidx.viewpager2.widget.CompositePageTransformer
import androidx.viewpager2.widget.MarginPageTransformer
import androidx.viewpager2.widget.ViewPager2
import com.baghdadhomes.Adapters.ProductsAdapter
import com.baghdadhomes.Adapters.ProductsBannerPagerAdapter
import com.baghdadhomes.Adapters.ProductsCityAdapter
import com.baghdadhomes.R
import com.google.gson.JsonObject
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import kotlin.math.abs


private const val ARG_PARAM1 = "param1"
private const val ARG_PARAM2 = "param2"

class ProjectFragment : BaseFragment() {
    // TODO: Rename and change types of parameters
    private var param1: String? = null
    private var param2: String? = null
    lateinit var bannerAdapter: ProductsBannerPagerAdapter
    var bannerList:ArrayList<String> = ArrayList()
    var cityList:ArrayList<String> = ArrayList()
    lateinit var bannerPager:ViewPager2
    lateinit var rv_city:RecyclerView
    lateinit var rv_products:RecyclerView
    lateinit var productsCirtAdapter: ProductsCityAdapter
    lateinit var productsAdapter: ProductsAdapter
    lateinit var indicatorLayout:LinearLayout

    var totalCount:Int = 0
    var scrollPosition = 0
    var currentIndex = 0
    private var job: Job? = null
    lateinit var et_search_property:EditText

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



    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initView(view);
        bannerList.add("https://najafhome.com/baghdad/wp-content/uploads/2022/11/pexels-pixabay-534151-scaled1-1024x683.jpg")
        bannerList.add("https://najafhome.com//baghdad//wp-content//uploads//2025//01//438231037_957479906380327_2283534599770344636_n-1024x683.jpg")
        bannerList.add("https://najafhome.com//baghdad//wp-content//uploads//2022//11//main-ads4-1024x683.jpg")
        bannerAdapter = ProductsBannerPagerAdapter(bannerList);
        bannerPager.adapter = bannerAdapter
        setupIndicators(bannerList.size)
        setCurrentIndicator(0)
        startAutoSlide()
        bannerPager.addCarouselEffect(enableZoom = false)

        bannerPager.registerOnPageChangeCallback(object : ViewPager2.OnPageChangeCallback() {
            override fun onPageSelected(position: Int) {
                super.onPageSelected(position)
                setCurrentIndicator(position)
            }
        })


        bannerPager.clipToPadding = false
        bannerPager.clipChildren = false

        bannerPager.offscreenPageLimit = 3


        cityList.add("https://najafhome.com/baghdad/wp-content/uploads/2022/11/pexels-pixabay-534151-scaled1-1024x683.jpg")
        cityList.add("https://najafhome.com//baghdad//wp-content//uploads//2025//01//438231037_957479906380327_2283534599770344636_n-1024x683.jpg")
        cityList.add("https://najafhome.com//baghdad//wp-content//uploads//2022//11//main-ads4-1024x683.jpg")
        cityList.add("https://najafhome.com/baghdad/wp-content/uploads/2022/11/pexels-pixabay-534151-scaled1-1024x683.jpg")
        cityList.add("https://najafhome.com//baghdad//wp-content//uploads//2025//01//438231037_957479906380327_2283534599770344636_n-1024x683.jpg")
        cityList.add("https://najafhome.com//baghdad//wp-content//uploads//2022//11//main-ads4-1024x683.jpg")
        cityList.add("https://najafhome.com/baghdad/wp-content/uploads/2022/11/pexels-pixabay-534151-scaled1-1024x683.jpg")
        cityList.add("https://najafhome.com//baghdad//wp-content//uploads//2025//01//438231037_957479906380327_2283534599770344636_n-1024x683.jpg")
        cityList.add("https://najafhome.com//baghdad//wp-content//uploads//2022//11//main-ads4-1024x683.jpg")
        cityList.add("https://najafhome.com/baghdad/wp-content/uploads/2022/11/pexels-pixabay-534151-scaled1-1024x683.jpg")
        cityList.add("https://najafhome.com//baghdad//wp-content//uploads//2025//01//438231037_957479906380327_2283534599770344636_n-1024x683.jpg")
        cityList.add("https://najafhome.com//baghdad//wp-content//uploads//2022//11//main-ads4-1024x683.jpg")
        productsCirtAdapter = ProductsCityAdapter(requireActivity(),cityList)

        rv_city.adapter = productsCirtAdapter;

        productsAdapter = ProductsAdapter(requireActivity(),cityList)
        rv_products.adapter = productsAdapter
        rv_products.setHasFixedSize(true)
        rv_products.setItemViewCacheSize(10)
        rv_products.isNestedScrollingEnabled = false

    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

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
        job = CoroutineScope(Dispatchers.Main).launch {
            while (true) {
                delay(4000)
                if (currentIndex == (bannerList.size - 1)){
                    currentIndex = 0
                } else {
                    currentIndex +=1
                }
                bannerPager.setCurrentItem(currentIndex, true)
            }
        }
    }

    fun initView(view:View){
        bannerPager = view.findViewById(R.id.bannerPager)
        rv_city = view.findViewById(R.id.rv_city)
        rv_products = view.findViewById(R.id.rv_products)
        indicatorLayout = view.findViewById(R.id.indicatorLayout)
        et_search_property = view.findViewById(R.id.et_search_property)

        et_search_property.setOnClickListener {
            SelectProjectBottomSheet.show(parentFragmentManager)
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
        (getChildAt(0) as RecyclerView).overScrollMode = RecyclerView.OVER_SCROLL_NEVER // Remove the scroll effect

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
}