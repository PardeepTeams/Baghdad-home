package com.baghdadhomes.Activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.Adapters.AdapterDetailAds.openDetailPage
import com.baghdadhomes.Models.NewFeatureModel
import com.baghdadhomes.Models.Result
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants



class AllPropertiesActivity : BaseActivity(), openDetailPage {
    lateinit var adapter :AdapterDetailAds
    var propertiesList:ArrayList<Result> = ArrayList()
    var page:Int = 1
    var type:String = "all"
    var loading: Boolean = true
    var totalCount:Int = 0
    var visibleItemCount: Int = 0;
    var totalItemCount: Int = 0;
    var pastVisiblesItems: Int = 0
    lateinit var rv_ads:RecyclerView
    lateinit var img_back:ImageView
    lateinit var tv_heading:TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_all_properties)
        rv_ads = findViewById(R.id.rv_all_ads)
        img_back = findViewById(R.id.img_back)
        tv_heading = findViewById(R.id.tv_heading)
        val layoutManager: LinearLayoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        rv_ads.layoutManager = layoutManager
        img_back.setOnClickListener {
            finish()
        }

        if(intent.getStringExtra("type")!=null){
            type = intent.getStringExtra("type")!!
            if(type.equals("all")){
                tv_heading.text = getString(R.string.all) + " " + getString(R.string.ads)
            }else if(type.equals("residential")){
                tv_heading.text = getString(R.string.residence) + " " + getString(R.string.ads)
            }else if(type.equals("commercial")){
                tv_heading.text = getString(R.string.commericail) + " " + getString(R.string.ads)
            }else if(type.equals("apartments")){
                tv_heading.text = getString(R.string.land) + " " + getString(R.string.ads)
            }

        }else{
            tv_heading.text = type+ " " + getString(R.string.ads)
        }
        rv_ads.setHasFixedSize(true);
        adapter = AdapterDetailAds(this, this, propertiesList/* false, false*/)
        rv_ads.adapter = adapter
        getProperties()
        rv_ads.setItemAnimator(null);

        rv_ads.addOnScrollListener(object : RecyclerView.OnScrollListener() {
            override fun onScrollStateChanged(recyclerView: RecyclerView, newState: Int) {

            }

            override fun onScrolled(recyclerView: RecyclerView, dx: Int, dy: Int) {
                super.onScrolled(recyclerView, dx, dy)
                if (dy > 0) { //check for scroll down
                    visibleItemCount = layoutManager.getChildCount()
                    totalItemCount = layoutManager.getItemCount()
                    pastVisiblesItems = layoutManager.findFirstVisibleItemPosition()
                    if (loading) {
                        if (visibleItemCount + pastVisiblesItems >= totalItemCount) {
                            if (visibleItemCount<totalCount) {
                                loading = false
                                getProperties()
                            }

                        }
                    }
                }
            }
        })


    }

    fun getProperties(){
        page = page +1
        val pagemap:HashMap<String,String> = HashMap()
        pagemap.put("page",page.toString())
        pagemap.put("per_page","20")
        if(!type.equals("all")){
            pagemap.put("type",type)
        }
        rv_ads.post(Runnable { rv_ads.requestLayout() })
        hitGetApiWithoutTokenWithParams(Constants.GET_PROPERTIES_DETAIL,true, Constants.GETFEATUREDPROPERTIESNEW, pagemap)

    }


    override fun getResponse(apiType: String, respopnse: JsonObject) {
      if(apiType.equals(Constants.GET_PROPERTIES_DETAIL)) {
          rv_ads.post(Runnable { rv_ads.requestLayout() })

          val model: NewFeatureModel = Gson().fromJson(
              respopnse,
              NewFeatureModel::class.java
          )
          if (model.success) {
              if (model.count != null) {
                  totalCount = model.count
              }
              if (page == 1) {
                  propertiesList.clear()
              }
              loading = true
              val lastCount = propertiesList.size
              propertiesList.addAll(model.result)

              adapter.notifyItemRangeInserted(lastCount,propertiesList.size-1)
          }
      }
    }

    override fun openNextActivity(model: Result?, position: Int) {

    }

    override fun editAd(model: Result?) {

    }

    override fun addRemoveFav(model: Result?, position: Int) {

    }

    override fun deleteAd(model: Result?, position: Int) {

    }

    override fun openLoginActivity() {

    }
    fun capitalizeFirstLetter(input: String): String {
        return input.replaceFirstChar { it.uppercase() }
    }
}