package com.baghdadhomes.Activities

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import androidx.core.widget.NestedScrollView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.baghdadhomes.Adapters.AdapterChatAI
import com.baghdadhomes.Models.ModelAiSearchResponse
import com.baghdadhomes.Models.Result
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants
import com.baghdadhomes.Utils.Utility
import com.google.gson.Gson
import com.google.gson.JsonObject

class AIChatActivity : BaseActivity(), AdapterChatAI.AdapterAiChatAction {
    lateinit var imgBack : ImageView
    lateinit var imgSendMessage : ImageView
    lateinit var etMessage : EditText
    lateinit var nestedScrollView : NestedScrollView
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
                chatAdapter.notifyItemInserted(chatList.size-1)

                nestedScrollView.post{
                    nestedScrollView.fullScroll(View.FOCUS_DOWN)
                }
            }
        }

    }

    private fun inits() {
        imgBack = findViewById(R.id.imgBack)
        imgSendMessage = findViewById(R.id.imgSendMessage)
        etMessage = findViewById(R.id.etMessage)
        rvAiChat = findViewById(R.id.rvAiChat)
        nestedScrollView = findViewById(R.id.nestedScrollView)

        chatList.add(ModelAiSearchResponse(message = getString(R.string.ai_chat_default_message_1)))
        chatList.add(ModelAiSearchResponse(message = getString(R.string.ai_chat_default_message_2)))

        rvAiChat.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL,false)
        chatAdapter = AdapterChatAI(this,chatList,this)
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
                    if(PreferencesService.instance.userLoginStatus == true){
                        map["user_id"] = PreferencesService.instance.getUserData!!.ID.toString()
                    }
                    hitPostApi(Constants.AISEARCH, true, Constants.AISEARCHAPI,map)
                }else{
                    showToast(this,getString(R.string.intenet_error))
                }

            }
        }
    }

    override fun openLoginActivity() {
        loginTypeDialog(false)
    }

    override fun addRemoveFav(
        parentPosition: Int,
        childPosition: Int,
        model: Result?
    ) {
        if(isNetworkAvailable()){
            val propId = model!!.iD
            val userData = PreferencesService.instance.getUserData
            val map: HashMap<String, String> = HashMap()
            map.put("user_id", userData!!.ID!!)
            map.put("listing_id", propId)
            chatList[parentPosition].result?.get(childPosition)?.is_fav = chatList[parentPosition].result?.get(childPosition)?.is_fav != true
            hitPostApi(Constants.ADD_REMOVE_FAV, false, Constants.ADD_REMOVE_FAV_API, map)
        }
    }

    override fun openAdDetailActivity(
        parentPosition: Int,
        childPosition: Int,
        model: Result?
    ) {
        val intent = Intent(this,AdsDetailsActivity::class.java)
        intent.putExtra("propertyId",model!!.iD)
        intent.putExtra("view_count", model.totalViews)
        intent.putExtra("myAd",false)
        startActivity(intent)
       // overridePendingTransition(0, 0)
    }

    override fun onSeeAllClick(position: Int) {
        Constants.slug.clear()
        var intent = Intent(this, PropertiesSearchActivity::class.java)
        Constants.filterModel = null
        intent.putExtra("search_text", chatList[position].searchText?:"")
        startActivity(intent)
    }
}