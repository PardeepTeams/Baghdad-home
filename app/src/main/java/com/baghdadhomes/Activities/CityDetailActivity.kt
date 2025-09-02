package com.baghdadhomes.Activities

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Adapters.AdapterCityProperties
import com.baghdadhomes.Adapters.AdapterDetailAds
import com.baghdadhomes.R
import com.google.gson.JsonObject

class CityDetailActivity : BaseActivity() {
    lateinit var adapter: AdapterCityProperties

    lateinit var rv_items:RecyclerView
    lateinit var img_back:ImageView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_detail)
        adjustFontScale(resources.configuration)
        rv_items = findViewById(R.id.rv_items)
        img_back = findViewById(R.id.img_back)

        adapter = AdapterCityProperties(this)
        rv_items.setLayoutManager(LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false))
        rv_items.adapter = adapter

        img_back.setOnClickListener {
            finish()
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

    }
}