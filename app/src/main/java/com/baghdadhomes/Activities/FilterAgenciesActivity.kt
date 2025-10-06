package com.baghdadhomes.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.AdapterView
import android.widget.ImageView
import android.widget.Spinner
import android.widget.TextView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.SpinnerCityAdapter
import com.baghdadhomes.Adapters.SpinnerNBHDAdapter
import com.baghdadhomes.Models.NBHDArea
import com.baghdadhomes.Models.NBHDDataResponse
import com.baghdadhomes.Models.NBHDModel
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants

class FilterAgenciesActivity : BaseActivity() {
    private lateinit var img_back : ImageView
    private lateinit var tv_clear : TextView
    private lateinit var tv_done : TextView
    private lateinit var spinnerCity : Spinner
    private lateinit var spinner_neighborhood : Spinner
    private var cityList:ArrayList<NBHDDataResponse> = ArrayList()
    private var nbhdList: java.util.ArrayList<NBHDArea> = java.util.ArrayList()

    private var nbhdModel : NBHDModel ?= null
    private var city : String ?= "all"
    private var nbhd : String ?= "all"

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_filter_agencies)

        city = intent.getStringExtra("city").orEmpty()
        nbhd = intent.getStringExtra("nbhd").orEmpty()

        inits()
        clickListeners()

        if (intent.getStringExtra("nbhdModel") != null){
            nbhdModel = Gson().fromJson(intent.getStringExtra("nbhdModel"), NBHDModel::class.java)
            if (nbhdModel != null){
                setData()
            } else {
                if(isNetworkAvailable()){
                    hitGetApiWithoutToken(Constants.NEIGHBORHOOD, true, Constants.NEIGHBORHOOD_API)
                }else{
                    showToast(this, resources.getString(R.string.intenet_error))
                }
            }
        }

        if(Constants.nbhdModel!=null){
            nbhdModel = Constants.nbhdModel
            if (nbhdModel != null){
                setData()
            } else {
                if(isNetworkAvailable()){
                    hitGetApiWithoutToken(Constants.NEIGHBORHOOD, true, Constants.NEIGHBORHOOD_API)
                }else{
                    showToast(this, resources.getString(R.string.intenet_error))
                }
            }
        }
    }

    private fun inits(){
        img_back = findViewById(R.id.img_back)
        tv_clear = findViewById(R.id.tv_clear)
        tv_done = findViewById(R.id.tv_done)
        spinnerCity = findViewById(R.id.spinnerCity)
        spinner_neighborhood = findViewById(R.id.spinner_neighborhood)
    }

    private fun clickListeners(){
        img_back.setOnClickListener {
            finish()
        }

        tv_clear.setOnClickListener {
            spinnerCity.setSelection(0)
            spinner_neighborhood.setSelection(0)
        }

        tv_done.setOnClickListener {
            val intent = Intent()
            intent.putExtra("city", city.orEmpty())
            intent.putExtra("nbhd", nbhd.orEmpty())
            setResult(Activity.RESULT_OK,intent)
            finish()
            overridePendingTransition(0,0)
        }

        spinnerCity.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                city = cityList[position].slug
                println("CitySlug_$city")
                nbhdList.clear()
                if (!cityList[position].area.isNullOrEmpty()){
                    nbhdList.addAll(cityList[position].area)
                }
                spinner_neighborhood.adapter = SpinnerNBHDAdapter(this@FilterAgenciesActivity, nbhdList)
                for (i in 0 until nbhdList.size){
                    if (nbhdList[i].slug == nbhd){
                        spinner_neighborhood.setSelection(i)
                    }
                }
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

        spinner_neighborhood.onItemSelectedListener = object : AdapterView.OnItemSelectedListener{
            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long,
            ) {
                nbhd = nbhdList[position].slug
                println("NBHDSlug_$nbhd")
            }

            override fun onNothingSelected(parent: AdapterView<*>?) {

            }
        }

    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if(apiType.equals(Constants.NEIGHBORHOOD)){
            val model = Gson().fromJson(respopnse, NBHDModel::class.java)
            if(model.success){
                //nbhdList.addAll(model.response)
                nbhdModel = model
                setData()
            }
        }

    }

    private fun setData(){
        val list : java.util.ArrayList<NBHDDataResponse> = java.util.ArrayList()
        val nbhd1 = ArrayList<NBHDArea>()
        nbhd1.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
        list.add(NBHDDataResponse(getString(R.string.all),getString(R.string.all),"all", nbhd1))

        if (PreferencesService.instance.getLanguage() != "ar") {
            for (i in 0 until nbhdModel?.response!!.size) {
                val listArea1 : java.util.ArrayList<NBHDArea> = java.util.ArrayList()
                val listArea : java.util.ArrayList<NBHDArea> = java.util.ArrayList()
                for (j in 0 until nbhdModel?.response?.get(i)?.area!!.size){
                    if (!nbhdModel?.response?.get(i)?.area?.get(j)?.description.isNullOrEmpty()){
                        listArea.add(nbhdModel?.response?.get(i)?.area?.get(j)!!)
                    }
                }
                listArea.sortWith{ lhs, rhs ->
                    lhs!!.description.compareTo(rhs!!.description)
                }

                listArea1.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
                listArea1.addAll(listArea)
                list.add(NBHDDataResponse(nbhdModel?.response?.get(i)?.name!!,nbhdModel?.response?.get(i)?.description!!,nbhdModel?.response?.get(i)?.slug!!,listArea1))
            }
        } else{
            //list.addAll(model.response)
            for (i in 0 until nbhdModel?.response!!.size) {
                val listArea1 : java.util.ArrayList<NBHDArea> = java.util.ArrayList()
                val listArea : java.util.ArrayList<NBHDArea> = java.util.ArrayList()
                for (j in 0 until nbhdModel?.response?.get(i)?.area!!.size){
                    listArea.add(nbhdModel?.response?.get(i)?.area?.get(j)!!)
                }
                listArea.sortWith{ lhs, rhs ->
                    lhs!!.name.compareTo(rhs!!.name)
                }

                listArea1.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
                listArea1.addAll(listArea)
                list.add(NBHDDataResponse(nbhdModel?.response?.get(i)?.name!!, nbhdModel?.response?.get(i)?.description!!,nbhdModel?.response?.get(i)?.slug!!,listArea1))
            }
        }

        cityList.addAll(list)

        spinnerCity.adapter = SpinnerCityAdapter(this, cityList)
        for (i in 0 until cityList.size){
            if (cityList[i].slug == city){
                spinnerCity.setSelection(i)
            }
        }
    }


}