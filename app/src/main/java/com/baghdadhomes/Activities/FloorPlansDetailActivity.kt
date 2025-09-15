package com.baghdadhomes.Activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import com.baghdadhomes.Models.ResultDetail
import com.baghdadhomes.R
import com.bumptech.glide.Glide
import com.google.gson.Gson
import com.google.gson.JsonObject

class FloorPlansDetailActivity : BaseActivity() {
    var model: ResultDetail? = null
    var image: String? = null

    lateinit var linArea:LinearLayout
    lateinit var linBedrooms:LinearLayout
    lateinit var linBath:LinearLayout
    lateinit var tv_area:TextView
    lateinit var tvBedrooms:TextView
    lateinit var tvBath:TextView
    lateinit var floorPlanImage:ImageView
    lateinit var imgBack:ImageView


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_floor_plans_detail)
        linArea = findViewById(R.id.linArea)
        tv_area = findViewById(R.id.tv_area)
        linBedrooms = findViewById(R.id.linBedrooms)
        tvBedrooms = findViewById(R.id.tvBedrooms)
        linBath = findViewById(R.id.linBath)
        tvBath = findViewById(R.id.tvBath)
        floorPlanImage = findViewById(R.id.floorPlanImage)
        imgBack = findViewById(R.id.imgBack)

        imgBack.setOnClickListener {
            finish()
        }

        if(intent.getStringExtra("model")!=null){
            model = Gson().fromJson(intent.getStringExtra("model"),ResultDetail::class.java)
        }

        if(intent.getStringExtra("image")!=null){
            image = intent.getStringExtra("image")
        }



        if(model!!.property_meta.fave_property_bedrooms!=null){
            tvBedrooms.setText(model!!.property_meta.fave_property_bedrooms.get(0))
        }else{
            tvBedrooms.setText("0")
        }

        if (model!!.property_meta.fave_property_size != null){
            tv_area.setText(model!!.property_meta.fave_property_size.get(0) + " "+ getString(R.string.m))
        } else{

            tv_area.text = "00 "+ getString(R.string.m)
        }

        if(model!!.property_meta.fave_property_bathrooms!=null){
            tvBath.setText(model!!.property_meta.fave_property_bathrooms.get(0))
        }else{
            tvBath.setText("0")
        }

        Glide.with(this).load(image?:"")
            .placeholder(R.drawable.img_placeholder)
            .into(floorPlanImage)
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

    }
}