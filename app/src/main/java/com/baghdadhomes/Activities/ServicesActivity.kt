package com.baghdadhomes.Activities

import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterServices
import com.baghdadhomes.Models.ServicesModel
import com.baghdadhomes.Models.ServicesResponse
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import java.util.*

class ServicesActivity : BaseActivity() {
    private lateinit var imgBack : ImageView
    private lateinit var tvHeading : TextView
    private lateinit var imgClearSearch: ImageView
    private lateinit var etSearch : EditText
    private lateinit var rvServices : RecyclerView

    private lateinit var adapterServices : AdapterServices
    private val serviceList: ArrayList<ServicesResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services)

        inits()

        if (isNetworkAvailable()){
            hitGetApiWithoutToken(Constants.SERVICES, true, Constants.SERVICES_API)
        } else{
            showToast(this, resources.getString(R.string.intenet_error))
        }

    }

    private fun inits(){
        imgBack = findViewById(R.id.imgBack)
        tvHeading = findViewById(R.id.tvHeading)
        rvServices = findViewById(R.id.rvServices)
        imgClearSearch = findViewById(R.id.imgClearSearch)
        etSearch = findViewById(R.id.etSearch)

        rvServices.layoutManager = GridLayoutManager(this, 3)
        adapterServices = AdapterServices(this, serviceList)
        rvServices.adapter = adapterServices

        rvServices.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                this@ServicesActivity.dismissKeyboard(v)
                return false
            }
        })

        imgBack.setOnClickListener {
            finish()
        }

        imgClearSearch.setOnClickListener {
            etSearch.setText("")
            this.dismissKeyboard(etSearch)
        }

        etSearch.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                this.dismissKeyboard(etSearch)
                filter(etSearch.text.toString())

                true

            }else{
                false
            }

        }

        etSearch.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (etSearch.text.isNotEmpty()){
                    imgClearSearch.visibility = View.VISIBLE
                } else{
                    imgClearSearch.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })
    }

    fun filter(text: String){
        val filterList: ArrayList<ServicesResponse> = ArrayList()
        for (data: ServicesResponse in serviceList){
            if (data.postTitle!!.lowercase(Locale.ROOT).contains(text)){
                filterList.add(data)
            } else if (data.arabic_title.lowercase(Locale.ROOT).contains(text)){
                filterList.add(data)
            }
        }
        adapterServices.updateList(filterList)
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.SERVICES)){
            val servicesModel = Gson().fromJson(respopnse, ServicesModel::class.java)
            if (servicesModel.success == "true"){
                serviceList.addAll(servicesModel.response)
                rvServices.adapter = adapterServices
                //Log.d("123456", serviceList.size.toString())

                serviceList.sortWith { lhs, rhs ->
                    Integer.valueOf(lhs.serviceOrderid!!.toInt())
                        .compareTo(rhs.serviceOrderid!!.toInt())
                }
            }
        }
    }
}