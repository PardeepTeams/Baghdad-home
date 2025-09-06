package com.baghdadhomes.Activities

import android.os.Bundle
import android.widget.EditText
import android.widget.ImageView
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Models.ModelAiSearchResponse
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Utility

class AIChatActivity : AppCompatActivity() {
    lateinit var imgBack : ImageView
    lateinit var imgSendMessage : ImageView
    lateinit var etMessage : EditText
    lateinit var rvAiChat : RecyclerView

    private val chatList : ArrayList<ModelAiSearchResponse> = ArrayList()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aichat)
        inits()
        clickListeners()
    }

    private fun inits() {
        imgBack = findViewById(R.id.imgBack)
        imgSendMessage = findViewById(R.id.imgSendMessage)
        etMessage = findViewById(R.id.etMessage)
        rvAiChat = findViewById(R.id.rvAiChat)

        chatList.add(ModelAiSearchResponse(message = getString(R.string.ai_chat_default_message_1)))
        chatList.add(ModelAiSearchResponse(message = getString(R.string.ai_chat_default_message_2)))

        rvAiChat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
    }

    private fun clickListeners(){
        imgBack.setOnClickListener {
            finish()
        }

        imgSendMessage.setOnClickListener {
            if (etMessage.text.trim().isEmpty()) {
                Utility.showToast(this, getString(R.string.enter_message))
                return@setOnClickListener
            }
        }
    }
}