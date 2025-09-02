package com.baghdadhomes.Activities

import android.os.Bundle
import android.widget.TextView
import com.google.gson.JsonObject
import com.baghdadhomes.R

class AboutUsActivity : BaseActivity() {
    lateinit var btn_okk: TextView
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_about_us)
        adjustFontScale(resources.configuration)

        btn_okk = findViewById(R.id.btn_okk)

        btn_okk.setOnClickListener {
            finish()
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

    }
}