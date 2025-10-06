package com.baghdadhomes.Activities

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.MotionEvent
import android.view.View
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterServiceCompanies
import com.baghdadhomes.Models.ServicesListModel
import com.baghdadhomes.Models.ServicesListResponse
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import java.util.*

class ServicesSearchActivity : BaseActivity(), AdapterServiceCompanies.openDetailPage {
    lateinit var rv_service_companies: RecyclerView
    lateinit var adapterServiceCompanies: AdapterServiceCompanies
    lateinit var img_back: ImageView
    lateinit var et_search: EditText
    lateinit var tv_companies_count: TextView
    lateinit var img_clear_search: ImageView
    var companiesList: ArrayList<ServicesListResponse> = ArrayList()
    var id: String? = null
    var postTitle: String? = null
    var arabic_title: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_services_search)
        adjustFontScale(resources.configuration)

        rv_service_companies = findViewById(R.id.rv_service_companies)
        img_back = findViewById(R.id.img_back)
        et_search = findViewById(R.id.et_search)
        tv_companies_count = findViewById(R.id.tv_companies_count)
        img_clear_search = findViewById(R.id.img_clear_search)

        if (intent.getStringExtra("id") != null){
            id = intent.getStringExtra("id").toString()
        }
        if (intent.getStringExtra("postTitle") != null){
            postTitle = intent.getStringExtra("postTitle").toString()
        }

        if (intent.getStringExtra("arabic_title") != null){
            arabic_title = intent.getStringExtra("arabic_title")
        }

        if (isNetworkAvailable()){
            hitGetApiWithoutToken(Constants.SERVICES_COMPANY, true, Constants.SERVICES_COMPANY_API + id)
        } else{
            Utility.showToast(this,resources.getString(R.string.intenet_error))
        }

        rv_service_companies.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)
        adapterServiceCompanies = AdapterServiceCompanies(this, companiesList, this)

        img_back.setOnClickListener { onBackPressed() }

        img_clear_search.setOnClickListener {
            et_search.setText("")
            this.dismissKeyboard(et_search)
        }
        et_search.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                dismissKeyboard(et_search)
                filter(et_search.text.toString())

                true

            }else{
                false
            }

        }
        et_search.addTextChangedListener(object : TextWatcher{
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (et_search.text.isNotEmpty()){
                    img_clear_search.visibility = View.VISIBLE
                } else{
                    img_clear_search.visibility = View.GONE
                }
            }

            override fun afterTextChanged(s: Editable?) {
               filter(s.toString())
            }
        })

        rv_service_companies.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                dismissKeyboard(v)
                return false
            }
        })
    }

    fun filter(text: String){
        val filterList: ArrayList<ServicesListResponse> = ArrayList()
        for (data: ServicesListResponse in companiesList){
            if (data.data.post_title.lowercase(Locale.ROOT).contains(text)){
                filterList.add(data)
            } else if (data.meta_data.arabicTitle.get(0).lowercase(Locale.ROOT).contains(text)){
                filterList.add(data)
            }
        }
        adapterServiceCompanies.updateList(filterList)
        adapterServiceCompanies.notifyDataSetChanged()
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.SERVICES_COMPANY)){
            val model = Gson().fromJson(respopnse, ServicesListModel::class.java)
            if (model.success == true){
                if(model.response!=null){
                    companiesList.addAll(model.response!!)
                    tv_companies_count.setText(companiesList.size.toString()+ " " + resources.getString(R.string.company_found))
                    rv_service_companies.adapter = adapterServiceCompanies
                    /*if (companiesList.size>=0){
                        tv_companies_count.setText(companiesList.size.toString()+ " " + resources.getString(R.string.company_found))
                    } else{
                        tv_companies_count.setText(resources.getString(R.string.no_company_found))
                    }*/
                } else{
                    tv_companies_count.setText(resources.getString(R.string.no_company_found))
                }

            }
        }
    }

    override fun openDetails(model: ServicesListResponse) {
        Constants.serviceListResponse = model
        val intent = Intent(this, ServiceCompanyViewActivity::class.java)
      //  intent.putExtra("model", Gson().toJson(model))

        intent.putExtra("postTitle", postTitle)
        intent.putExtra("arabic_title", arabic_title)
        startActivity(intent)
    }
}