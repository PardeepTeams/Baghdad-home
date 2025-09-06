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
import com.baghdadhomes.Adapters.AdapterChatAI
import com.baghdadhomes.Models.ModelAiSearchResponse
import com.baghdadhomes.Models.NBHDModel
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject

class AIChatActivity : BaseActivity() {
    lateinit var imgBack : ImageView
    lateinit var imgSendMessage : ImageView
    lateinit var etMessage : EditText
    lateinit var rvAiChat : RecyclerView

    private val chatList : ArrayList<ModelAiSearchResponse> = ArrayList()
    lateinit var chatAdapter: AdapterChatAI

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_aichat)
        inits()
        clickListeners()
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

        if(apiType.equals(Constants.AISEARCH)){
            val model = Gson().fromJson(respopnse, ModelAiSearchResponse::class.java)
            if(model.success == true){
                etMessage.text.clear()
                chatList.add(model)
                chatAdapter.notifyDataSetChanged()
            }
        }

    }

    private fun inits() {
        imgBack = findViewById(R.id.imgBack)
        imgSendMessage = findViewById(R.id.imgSendMessage)
        etMessage = findViewById(R.id.etMessage)
        rvAiChat = findViewById(R.id.rvAiChat)

        chatList.add(ModelAiSearchResponse(message = getString(R.string.ai_chat_default_message_1)))
        chatList.add(ModelAiSearchResponse(message = getString(R.string.ai_chat_default_message_2)))

        rvAiChat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        chatAdapter = AdapterChatAI(this,chatList)
        rvAiChat.adapter = chatAdapter

    }

    private fun clickListeners(){
        imgBack.setOnClickListener {
            finish()
        }

        imgSendMessage.setOnClickListener {
            if (etMessage.text.trim().isEmpty()) {
                Utility.showToast(this, getString(R.string.enter_message))
                return@setOnClickListener
            }else{
                if(isNetworkAvailable()){
                    val map:HashMap<String,String> = HashMap()
                    map.put("search_text",etMessage.text.toString())

                    hitPostApi(Constants.AISEARCH, true, Constants.AISEARCHAPI,map)
                }else{
                    showToast(this,getString(R.string.intenet_error))
                }

            }
        }
    }
}