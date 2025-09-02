package com.baghdadhomes.Activities

import android.os.Bundle
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterReportItems
import com.baghdadhomes.Models.ReportDataModel
import com.baghdadhomes.Models.ReportModel
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility

class ReportAddActivity : BaseActivity(), AdapterReportItems.selectedReason {

    lateinit var adapter: AdapterReportItems
    lateinit var rv_report_items:RecyclerView
    lateinit var img_back:ImageView
    lateinit var et_comment:EditText
    var reportList:ArrayList<ReportModel> = ArrayList()
    lateinit var btn_send_review:Button
    var post_id: String? = null
    var reason: String? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_report_add)
        rv_report_items = findViewById(R.id.rv_report_items)
        img_back = findViewById(R.id.img_back)
        et_comment = findViewById(R.id.et_comment)
        btn_send_review = findViewById(R.id.btn_send_review)

        if (intent.getStringExtra("post_id") != null){
            post_id = intent.getStringExtra("post_id")
        }


        img_back.setOnClickListener {
            onBackPressed()
            overridePendingTransition(0,0)
        }
        reportList.add(ReportModel(true,getString(R.string.wrong_type)))
        reportList.add(ReportModel(false,getString(R.string.against_public)))
        reportList.add(ReportModel(false,getString(R.string.sold_property)))
        reportList.add(ReportModel(false,getString(R.string.property_images)))
        reportList.add(ReportModel(false,getString(R.string.property_status)))
        reportList.add(ReportModel(false,getString(R.string.other)))
        adapter = AdapterReportItems(this,reportList, this)
        rv_report_items.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rv_report_items.adapter = adapter


        btn_send_review.setOnClickListener {
            for(i in reportList){
                if(i.isChecked == true){
                  reason = i.text
                }
            }

            if (isNetworkAvailable()){
                if (et_comment.visibility == View.VISIBLE){
                    if (et_comment.text.isNotEmpty()){
                        val user_id = PreferencesService.instance.getUserData!!.ID
                        val map: HashMap<String, String> = HashMap()
                        map.put("user_id", user_id.toString())
                        map.put("property_id", post_id.toString())
                        map.put("reason", et_comment.text.toString().trim())
                        hitPostApi(Constants.REPORT_PROPERTY, true, Constants.REPORT_PROPERTY_API, map)
                    } else{
                        Utility.showToast(this, resources.getString(R.string.enter_comment))
                    }
                } else{
                    val user_id = PreferencesService.instance.getUserData!!.ID
                    val map: HashMap<String, String> = HashMap()
                    map.put("user_id", user_id.toString())
                    map.put("property_id", post_id.toString())
                    map.put("reason", reason!!)
                    hitPostApi(Constants.REPORT_PROPERTY, true, Constants.REPORT_PROPERTY_API, map)
                }
            } else{
                Utility.showToast(this, resources.getString(R.string.intenet_error))
            }
         /*   if(reason!=null *//*&& reason != "Other"*//*){
                var user_id = PreferencesService.instance.getUserData!!.ID
                var map: HashMap<String, String> = HashMap()
                map.put("user_id", user_id.toString())
                map.put("property_id", post_id.toString())
                *//*if (reason == "Other" && et_comment.text.isEmpty()){
                    Utility.showToast(this, resources.getString(R.string.enter_comment))
                } else{
                    reason = et_comment.text.toString().trim()
                }*//*
                map.put("reason", reason!!)
                Log.d("reasonMy", reason!!)
                //map.put("comment", et_comment.text.toString().trim())
                //hitPostApi(Constants.REPORT_PROPERTY, true, Constants.REPORT_PROPERTY_API, map)
            }else{
                Utility.showToast(this, resources.getString(R.string.select_reason))
            }*/
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType.equals(Constants.REPORT_PROPERTY)){
           val model: ReportDataModel = Gson().fromJson(respopnse, ReportDataModel::class.java)
            if (model.success){
                Utility.showToast(this, resources.getString(R.string.report_scceccfully))
                onBackPressed()
                overridePendingTransition(0,0)
            }
        }
    }

    override fun selectReason(model: ReportModel, position: Int) {
         for(i in reportList){
                if(i.isChecked == true){
                    i.isChecked = false
                }
            }

        if (model.text == resources.getString(R.string.other)){
            et_comment.visibility = View.VISIBLE
        } else{
            et_comment.setText("")
            et_comment.visibility = View.GONE
        }
        reportList.get(position).isChecked = true
        adapter.notifyDataSetChanged()
    }
}