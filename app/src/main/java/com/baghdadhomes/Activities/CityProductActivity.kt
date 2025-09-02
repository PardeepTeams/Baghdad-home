package com.baghdadhomes.Activities

import android.os.Bundle
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Adapters.ProductsAdapter
import com.baghdadhomes.R
import com.google.gson.JsonObject

class CityProductActivity : BaseActivity() {
    lateinit var rv_products_items:RecyclerView
    lateinit var productsAdapter: ProductsAdapter
    lateinit var img_back:ImageView
    var cityList:ArrayList<String> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_city_product)
        rv_products_items = findViewById(R.id.rv_products_items)
        img_back = findViewById(R.id.img_back)
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
        rv_products_items.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        productsAdapter = ProductsAdapter(this,cityList)
        rv_products_items.adapter = productsAdapter

        img_back.setOnClickListener {
            finish()
        }

    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

    }
}