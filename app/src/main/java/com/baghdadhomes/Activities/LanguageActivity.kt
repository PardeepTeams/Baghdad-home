package com.baghdadhomes.Activities

import android.os.Bundle
import android.widget.ImageView
import android.widget.TextView
import androidx.appcompat.app.AppCompatActivity
import com.baghdadhomes.R

class LanguageActivity : AppCompatActivity() {
    lateinit var img_back: ImageView
    lateinit var language_english: TextView
    lateinit var language_arabic: TextView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_language)

        img_back = findViewById(R.id.img_back)
        language_english = findViewById(R.id.language_english)
        language_arabic = findViewById(R.id.language_arabic)

        img_back.setOnClickListener { onBackPressed() }


    }

}