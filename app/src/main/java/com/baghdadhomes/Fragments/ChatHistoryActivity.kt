package com.baghdadhomes.Fragments

import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.MotionEvent
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Activities.HomeActivity
import com.baghdadhomes.Activities.MessagingActivity
import com.baghdadhomes.Adapters.AdapterChatList
import com.baghdadhomes.Models.AdsDataChat
import com.baghdadhomes.Models.AgenciesData
import com.baghdadhomes.Models.ChatHistoryData
import com.baghdadhomes.Models.ModelChatHistory
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants

class ChatHistoryActivity : BaseFragment(), AdapterChatList.OpenMessages {
    private lateinit var img_back : ImageView
    private lateinit var rvChats : RecyclerView
    private lateinit var img_search : ImageView
    private lateinit var et_serach : EditText
    private lateinit var img_clear_search : ImageView

    private var chatList : ArrayList<ChatHistoryData> = ArrayList()
    private lateinit var chatsAdapter : AdapterChatList

    private var userId : String ?= null

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.activity_chat_history, container, false)
    }

   /* override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_chat_history)
        adjustFontScale(resources.configuration)


    }*/

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        inits(view)
    }

    private fun inits(view: View){
        img_back = view.findViewById(R.id.img_back)
        rvChats = view.findViewById(R.id.rvChats)
        img_search = view.findViewById(R.id.img_search)
        et_serach = view.findViewById(R.id.et_serach)
        img_clear_search = view.findViewById(R.id.img_clear_search)

        if (PreferencesService.instance.userLoginStatus == true) {
            userId = PreferencesService.instance.getUserData?.ID
        }

        img_back.setOnClickListener {
            //finish()
            ((context) as HomeActivity).onBackPressed()
        }

        chatsAdapter = AdapterChatList(requireContext(),userId!!,chatList,this)
        rvChats.layoutManager = LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL,false)
        rvChats.setHasFixedSize(true)
        rvChats.itemAnimator = null
        rvChats.adapter = chatsAdapter

        rvChats.setOnTouchListener(object : View.OnTouchListener{
            override fun onTouch(v: View?, event: MotionEvent?): Boolean {
                context!!.dismissKeyboard(v)
                et_serach.clearFocus()
                return false
            }
        })


        img_clear_search.setOnClickListener {
            et_serach.setText("")
            et_serach.clearFocus()
            requireContext().dismissKeyboard(et_serach)
        }
        et_serach.setOnEditorActionListener { v, actionId, event ->
            if(actionId == EditorInfo.IME_ACTION_SEARCH){
                requireActivity().dismissKeyboard(et_serach)
                et_serach.clearFocus()
                filter(et_serach.text.toString())

                true

            }else{
                false
            }

        }
        et_serach.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (et_serach.text.isNotEmpty()){
                    img_clear_search.visibility = View.VISIBLE
                } else{
                    img_clear_search.visibility = View.GONE
                }
            }
            override fun afterTextChanged(s: Editable) {
                filter(s.toString())
            }
        })
    }

    fun filter(text: String){
        val filterList: java.util.ArrayList<ChatHistoryData> = java.util.ArrayList()
        for (i in chatList){
            if (i.message.orEmpty().contains(text,ignoreCase = true)){
                filterList.add(i)
            } else if (i.sent_by?.display_name.orEmpty().contains(text, ignoreCase = true)){
                filterList.add(i)
            } else if (i.sent_to?.display_name.orEmpty().contains(text,ignoreCase = true)){
                filterList.add(i)
            }
        }
        chatsAdapter.updateList(filterList)
    }

    override fun onResume() {
        super.onResume()
        if (isNetworkAvailable()){
            if (!userId.isNullOrEmpty()){
                var map : HashMap<String, String> = HashMap()
                map["user_id"] = userId.toString()
                hitPostApi(Constants.GET_CHAT_LIST,true, Constants.GET_CHAT_LIST_API, map)
            }
        } else{
            showToast(requireContext(),getString(R.string.intenet_error))
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType == Constants.GET_CHAT_LIST){
            val model = Gson().fromJson(respopnse,ModelChatHistory::class.java)
            chatList.clear()
            if (model.success == true){
                if (!model.response.isNullOrEmpty()){
                    chatList.addAll(model.response)

                    chatList.sortWith{ date1, date2 ->
                        date2.time.toString().toLong().compareTo(date1.time.toString().toLong())
                    }

                    chatsAdapter.notifyDataSetChanged()
                }
            } else{
                if (model.message != null){
                    showToast(requireContext(), model.message)
                } else{
                    showToast(requireContext(),getString(R.string.something_went_wrong))
                }
            }
        }
    }

    override fun onClickChat(model: AgenciesData?, postData: ChatHistoryData) {
        var postDetails : AdsDataChat ?= null
        if (!postData.postId.isNullOrEmpty()){
            postDetails = AdsDataChat(
                iD = postData.postId,
                post_title = postData.title,
                thumbnail = postData.thumbnail,
                price = postData.price
            )
        }
        val intent = Intent(context, MessagingActivity::class.java)
        intent.putExtra("receiverModel",Gson().toJson(model))
        if (postDetails != null) {
            intent.putExtra("postData", Gson().toJson(postDetails))
        }
        startActivity(intent)
      //  requireActivity().overridePendingTransition(0,0)
    }
}