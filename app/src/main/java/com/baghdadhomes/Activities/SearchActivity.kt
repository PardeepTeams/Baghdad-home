package com.baghdadhomes.Activities

import android.app.Dialog
import android.content.Intent
import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.*
import androidx.appcompat.widget.AppCompatSpinner
import androidx.cardview.widget.CardView
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterNBHDDialog
import com.baghdadhomes.Adapters.AdapterPropertySubTypes
import com.baghdadhomes.Adapters.AdapterSelectedNBHD
import com.baghdadhomes.Adapters.SpinnerCityAdapter
import com.baghdadhomes.Models.*
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import java.util.*

class SearchActivity : BaseActivity(), AdapterNBHDDialog.onClick,
    AdapterPropertySubTypes.SubTypeClick {
    lateinit var img_back :ImageView
    lateinit var tv_for_rent :TextView
    lateinit var tv_for_sale :TextView
    lateinit var property_residence :TextView
    lateinit var property_commercial :TextView
    lateinit var property_land :TextView
    lateinit var tv_done :TextView
    lateinit var tv_clear :TextView
    lateinit var tv_price_head :TextView
    lateinit var area_from :EditText
    lateinit var area_to :EditText
    lateinit var et_price_from :EditText
    lateinit var et_price_to :EditText
    lateinit var rv_selectedNBHD :RecyclerView
    lateinit var spinner_jurisdriction :AppCompatSpinner
    lateinit var spinner_neighborhood :TextView
    var nbhdList : ArrayList<NBHDDataResponse> = ArrayList()
    var selectedNBHDList : ArrayList<NBHDArea> = ArrayList()
    var cityList : ArrayList<NBHDArea> = ArrayList()
    /*var nbhdList : ArrayList<NBHDResponse> = ArrayList()
    var selectedNBHDList : ArrayList<NBHDResponse> = ArrayList()
    var cityList : ArrayList<NBHDResponse> = ArrayList()*/
    lateinit var nbhdDialog: Dialog
    lateinit var nbhdAdapter: AdapterNBHDDialog
    var selectedNBHDAdapter: AdapterSelectedNBHD? = null

    lateinit var ll_nested: LinearLayout
    lateinit var rvSubTypes: RecyclerView

    var filterList:ArrayList<FilterIntentModel> = ArrayList()

    private var subTypesList : ArrayList<PropertySubTypesModel> = ArrayList()
    private var adapterSubTypes = AdapterPropertySubTypes(this,subTypesList,this)
    var propertySubType: String? = ""

    var status: String? = null
    var type: String? = null
    var location: String? = null
    var area: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_search)
        adjustFontScale(resources.configuration)

        ll_nested = findViewById(R.id.ll_nested)
        tv_price_head = findViewById(R.id.tv_price_head)
        img_back = findViewById(R.id.img_back)
        tv_for_rent = findViewById(R.id.tv_for_rent)
        tv_for_sale = findViewById(R.id.tv_for_sale)
        property_residence = findViewById(R.id.property_residence)
        property_commercial = findViewById(R.id.property_commercial)
        property_land = findViewById(R.id.property_land)
        tv_done = findViewById(R.id.tv_done)
        tv_clear = findViewById(R.id.tv_clear)
        area_from = findViewById(R.id.area_from)
        area_to = findViewById(R.id.area_to)
        et_price_from = findViewById(R.id.et_price_from)
        et_price_to = findViewById(R.id.et_price_to)
        spinner_jurisdriction = findViewById(R.id.spinner_jurisdriction)
        spinner_neighborhood = findViewById(R.id.spinner_neighborhood)
        rv_selectedNBHD = findViewById(R.id.rv_selectedNBHD)
        rvSubTypes = findViewById(R.id.rvSubTypes)


        switchRentOrSale()
        setPropertyType()

        img_back.setOnClickListener { onBackPressed() }

        rvSubTypes.layoutManager = LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL,false)
        rvSubTypes.itemAnimator = null
        rvSubTypes.adapter = adapterSubTypes

        spinner_jurisdriction.setOnItemSelectedListener(object : AdapterView.OnItemSelectedListener {
            override fun onItemSelected(
                adapterView: AdapterView<*>, view: View?,
                position: Int, id: Long
            ) {
                val item = adapterView.getItemAtPosition(position)
                cityList.clear()
                cityList.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
                cityList.addAll(nbhdList.get(position).area)
                //cityList = nbhdList.get(position).area as ArrayList<NBHDArea>
                selectedNBHDList.clear()
                try {
                    selectedNBHDAdapter!!.notifyDataSetChanged()
                }catch (e:Exception){
                    e.toString()
                }


            }

            override fun onNothingSelected(adapterView: AdapterView<*>?) {

            }
        })

        if (isNetworkAvailable()){
            hitGetApiWithoutToken(Constants.NEIGHBORHOOD, true, Constants.NEIGHBORHOOD_API)
            //hitGetApiWithoutToken(Constants.GET_NBHD, true, Constants.GET_NBHD_URL)
            //hitGetApiWithoutToken(Constants.GET_CITY, false, Constants.GET_CITY_URL)
        } else{
            showToast(this, resources.getString(R.string.intenet_error))
        }


        spinner_neighborhood.setOnClickListener {
            dismissKeyboard(spinner_neighborhood)
            openNeighborhoodDialog()
        }

        /*val adapter: ArrayAdapter<*> = ArrayAdapter.createFromResource(this,
            R.array.cities, R.layout.support_simple_spinner_dropdown_item)
        spinner_jurisdriction.setAdapter(adapter)
        spinner_neighborhood.setAdapter(adapter)*/


       /*tv_done.setOnClickListener {
           dismissKeyboard(tv_done)
           startActivity(Intent(this, PropertiesSearchActivity::class.java))
           overridePendingTransition(0,0)
       }*/

        ll_nested.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return true
            }
        })

        tv_clear.setOnClickListener {
            dismissKeyboard(tv_clear)
            switchRentOrSale()
            setPropertyType()
            area_from.setText("")
            area_to.setText("")
            et_price_from.setText("")
            et_price_to.setText("")
            selectedNBHDList.clear()
            selectedNBHDAdapter!!.notifyDataSetChanged()
        }

        tv_done.setOnClickListener {
            dismissKeyboard(tv_done)
            //filterData()

            for (i in subTypesList){
                if (i.isSelected == true){
                    propertySubType = i.value.toString()
                }
            }

            for (i in nbhdList) {
                if (spinner_jurisdriction.selectedItem == i) {
                    location = i.slug.toString()
                }
            }

            val areaList: ArrayList<String> = ArrayList()
            for (i in selectedNBHDList){
                if (i.slug != "all") {
                    areaList.add(i.slug.toString())
                }
            }

            val minPrice = et_price_from.text.toString().trim()
            val maxPrice = et_price_to.text.toString().trim()
            val minArea = area_from.text.toString().trim()
            val maxArea = area_to.text.toString().trim()

            val filterModel = FilterIntentModel(status, type,propertySubType, location, minPrice, maxPrice, maxArea, minArea, areaList)
            val intent = Intent(this,PropertiesSearchActivity::class.java)
            intent.putExtra("filterData",Gson().toJson(filterModel))
            startActivity(intent)
        }

        spinner_jurisdriction.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return false
            }
        })

    }


    private fun switchRentOrSale(){
        status = "for-sale"
        tv_for_rent.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
        tv_for_sale.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
        tv_for_rent.setTextColor(ContextCompat.getColor(this,R.color.grey))
        tv_for_sale.setTextColor(ContextCompat.getColor(this,R.color.whiteNew))
        tv_price_head.text = getString(R.string.selling_price)

        tv_for_sale.setOnClickListener {
            dismissKeyboard(tv_for_sale)
            status = "for-sale"
            tv_for_rent.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            tv_for_sale.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
            tv_for_rent.setTextColor(ContextCompat.getColor(this,R.color.grey))
            tv_for_sale.setTextColor(ContextCompat.getColor(this,R.color.whiteNew))
            tv_price_head.text = getString(R.string.selling_price)
        }

        tv_for_rent.setOnClickListener {
            dismissKeyboard(tv_for_rent)
            status = "for-rent"
            tv_for_rent.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
            tv_for_sale.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            tv_for_rent.setTextColor(ContextCompat.getColor(this,R.color.whiteNew))
            tv_for_sale.setTextColor(ContextCompat.getColor(this,R.color.grey))
            tv_price_head.text = getString(R.string.rent_price)
        }
    }

    private fun setPropertyType() {
        type = "residential"
        propertySubType = ""
        setSubType()
        property_residence.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
        property_commercial.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
        property_land.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
        property_residence.setTextColor(ContextCompat.getColor(this, R.color.whiteNew))
        property_commercial.setTextColor(ContextCompat.getColor(this, R.color.grey))
        property_land.setTextColor(ContextCompat.getColor(this, R.color.grey))

        property_residence.setOnClickListener {
            dismissKeyboard(property_residence)
            type = "residential"
            propertySubType = ""
            setSubType()
            property_residence.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
            property_commercial.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            property_land.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            property_residence.setTextColor(ContextCompat.getColor(applicationContext, R.color.whiteNew))
            property_commercial.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            property_land.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }

        property_commercial.setOnClickListener {
            dismissKeyboard(property_commercial)
            type = "commercial"
            propertySubType = ""
            setSubType()
            property_residence.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            property_commercial.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
            property_land.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            property_residence.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            property_commercial.setTextColor(ContextCompat.getColor(applicationContext, R.color.whiteNew))
            property_land.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
        }

        property_land.setOnClickListener {
            dismissKeyboard(property_land)
            type = "apartments"
            propertySubType = ""
            setSubType()
            property_residence.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            property_commercial.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_solid)
            property_land.background = ContextCompat.getDrawable(this,R.drawable.bg_outline_blue_new)
            property_residence.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            property_commercial.setTextColor(ContextCompat.getColor(applicationContext, R.color.grey))
            property_land.setTextColor(ContextCompat.getColor(applicationContext, R.color.whiteNew))
        }
    }

    private fun openNeighborhoodDialog(){
        nbhdDialog= Dialog(this, androidx.appcompat.R.style.Theme_AppCompat_Light_NoActionBar)
        nbhdDialog.setContentView(R.layout.layout_neighborhood_dialog)
        nbhdDialog.window!!.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT));
        nbhdDialog.setCancelable(true)
        nbhdDialog.setCanceledOnTouchOutside(true)
        nbhdDialog.show()

        val rl_outter: RelativeLayout = nbhdDialog.findViewById(R.id.rl_outter)
        val rv_nbhd: RecyclerView = nbhdDialog.findViewById(R.id.rv_nbhd)
        val cv_cardView: CardView = nbhdDialog.findViewById(R.id.cv_cardView)
        val et_serach: EditText = nbhdDialog.findViewById(R.id.et_serach)
        val img_clear_search: ImageView = nbhdDialog.findViewById(R.id.img_clear_search)

        cv_cardView.visibility = View.VISIBLE

        rv_nbhd.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        nbhdAdapter = AdapterNBHDDialog(this, cityList, this)
        rv_nbhd.adapter = nbhdAdapter
        nbhdAdapter.notifyDataSetChanged()

        et_serach.setOnEditorActionListener{ v, actionId, event ->
            if (actionId == EditorInfo.IME_ACTION_SEARCH){
                dismissKeyboard(et_serach)
                if (et_serach.text.isNotEmpty()){
                    filter(et_serach.text.toString())
                    nbhdAdapter.notifyDataSetChanged()
                }
                true
            } else{
                false
            }
        }

        et_serach.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {

            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                filter(s.toString())
                nbhdAdapter.notifyDataSetChanged()
                if (et_serach.text.isNotEmpty()){
                    img_clear_search.visibility = View.VISIBLE
                } else{
                    img_clear_search.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {

            }
        })

        rl_outter.setOnClickListener {
            nbhdDialog.dismiss()
        }
    }

    fun filter(text: String){
        val filterList: ArrayList<NBHDArea> = ArrayList()
        for (data: NBHDArea in cityList){
            if (data.name.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))){
                filterList.add(data)
            } else if (data.description.lowercase(Locale.ROOT).contains(text.lowercase(Locale.ROOT))){
                filterList.add(data)
            }
        }
        nbhdAdapter.updateList(filterList)
        nbhdAdapter.notifyDataSetChanged()
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.GET_NBHD)){
            /*var nbhdSpinnerModel = Gson().fromJson(respopnse, NBHDSpinnerModel::class.java)
            if (nbhdSpinnerModel.code == 200 && nbhdSpinnerModel.success == "true"){
                //  nbhdList.clear()
                for (i in nbhdSpinnerModel.response) {
                    //  nbhdList.add(i)
                }
            }*/
            // val adapter = NBHDSpinnerAdapter(this, nbhdList)
            //spinner_neighborhood.setAdapter(adapter)
        } else if (apiType.equals(Constants.GET_CITY)){
            /*var nbhdSpinnerModel = Gson().fromJson(respopnse, NBHDSpinnerModel::class.java)
            if (nbhdSpinnerModel.code == 200 && nbhdSpinnerModel.success == "true"){
                cityList.clear()
                for (i in nbhdSpinnerModel.response){
                    //  cityList.add(i)
                }
            }
            val adapter = CitySpinnerAdapter(this, cityList)*/
        }else if(apiType.equals(Constants.NEIGHBORHOOD)){
            val model = Gson().fromJson(respopnse, NBHDModel::class.java)
            if(model.success){
                //nbhdList.addAll(model.response)
                val list : ArrayList<NBHDDataResponse> = ArrayList()
                if (PreferencesService.instance.getLanguage() != "ar") {
                    for (i in 0 until model.response.size) {
                        val listArea : ArrayList<NBHDArea> = ArrayList()
                        for (j in 0 until model.response.get(i).area.size){
                            if (!model.response.get(i).area.get(j).description.isNullOrEmpty()){
                                listArea.add(model.response.get(i).area.get(j))
                            }
                        }
                        list.add(NBHDDataResponse(model.response[i].name,model.response[i].description,model.response[i].slug,listArea))
                    }
                } else{
                   list.addAll(model.response)
                }
                nbhdList.addAll(list)
                if(PreferencesService.instance.getLanguage().equals("ar")){
                    for(i in nbhdList){
                        Collections.sort(
                            i.area,
                            Comparator<NBHDArea?> { lhs, rhs ->
                                lhs!!.name.compareTo(rhs!!.name)
                            })

                    }
                }else{
                    for(i in nbhdList){
                        Collections.sort(
                            i.area,
                            Comparator<NBHDArea?> { lhs, rhs ->
                                lhs!!.description.lowercase().compareTo(rhs!!.description.lowercase())
                            })
                    }
                }
                // val adapter = CitySpinnerAdapter(this, cityList)
                //  spinner_jurisdriction.adapter = adapter

                val adapter = SpinnerCityAdapter(this, nbhdList)
                spinner_jurisdriction.adapter = adapter
                if(nbhdList.size>0){
                    cityList.clear()
                    cityList.add(NBHDArea(getString(R.string.all),"all",getString(R.string.all)))
                    cityList.addAll(nbhdList.get(0).area)
                    //cityList = nbhdList.get(0).area as ArrayList<NBHDArea>
                }

            }
        }
    }



    override fun onItemClick(model: NBHDArea) {
        nbhdDialog.dismiss()
        if (selectedNBHDList.size <= 2){
            if (model.slug == "all"){
                selectedNBHDList.clear()
            }
            selectedNBHDList.add(model)
        } else{
            Utility.showToast(this, resources.getString(R.string.only_three_neighborhood))
        }

        try {
            if (selectedNBHDList.size >= 2){
                for (i in 0 until selectedNBHDList.size){
                    if (selectedNBHDList.get(i).slug == "all"){
                        selectedNBHDList.removeAt(i)
                    }
                }
            }
        } catch (e : Exception){
            e.localizedMessage
        }

        rv_selectedNBHD.layoutManager = GridLayoutManager(this, 3)
        selectedNBHDAdapter = AdapterSelectedNBHD(this, selectedNBHDList)
        rv_selectedNBHD.adapter = selectedNBHDAdapter
        selectedNBHDAdapter!!.notifyDataSetChanged()
    }

    private fun setSubType(){
        subTypesList.clear()
        if (type == "residential"){
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_all)!!,getString(R.string.all),"",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_home_ad_activity)!!,getString(R.string.house),"house",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_apart_house)!!,getString(R.string.apart_house),"apart_house",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_apartment)!!,getString(R.string.apartment),"apartment",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_villa)!!,getString(R.string.villa),"villa",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_other)!!,getString(R.string.other),"residence_other",false))
        } else if (type == "commercial"){
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_all)!!,getString(R.string.all),"",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_office)!!,getString(R.string.office),"office",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_shop)!!,getString(R.string.shop),"shop",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_store)!!,getString(R.string.store),"store",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_building)!!,getString(R.string.building),"building",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_factory)!!,getString(R.string.factory),"factory",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_showroom)!!,getString(R.string.showroom),"showroom",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_other)!!,getString(R.string.other),"commercial_other",false))
        } else if ( type == "apartments") {
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_all)!!,getString(R.string.all),"",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_agriculture)!!,getString(R.string.agriculture),"agriculture",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_commercial_new)!!,getString(R.string.commericail),"commercial",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_residential_land)!!,getString(R.string.residence),"residencial",false))
            subTypesList.add(PropertySubTypesModel(ContextCompat.getDrawable(this,R.drawable.ic_industry)!!,getString(R.string.industrial),"industrial",false))
        }

        for (i in subTypesList){
            if (i.value == propertySubType){
                i.isSelected = true
            }
        }
        adapterSubTypes.notifyDataSetChanged()
    }

    override fun onSubTypeClick(position: Int) {
        for (i in subTypesList){
            i.isSelected = false
        }
        subTypesList.get(position).isSelected = true
        adapterSubTypes.notifyItemRangeChanged(0,subTypesList.size)
    }

}