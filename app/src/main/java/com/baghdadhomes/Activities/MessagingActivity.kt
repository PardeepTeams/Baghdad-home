package com.baghdadhomes.Activities

import android.content.Intent
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.text.Editable
import android.text.TextWatcher
import android.view.View
import android.widget.EditText
import android.widget.ImageView
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.google.android.material.imageview.ShapeableImageView
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterMessage
import com.baghdadhomes.Models.AdsDataChat
import com.baghdadhomes.Models.AgenciesData
import com.baghdadhomes.Models.ModelMessages
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.ProgressHud
import com.baghdadhomes.Utils.Utility
import de.hdodenhof.circleimageview.CircleImageView

class MessagingActivity : BaseActivity() {
    private lateinit var img_back : ImageView
    private lateinit var imgSendMessage : ImageView
    private lateinit var imgPerson : CircleImageView
    private lateinit var tvName : TextView
    private lateinit var tvLastSeen : TextView
    private lateinit var etMessage : EditText
    private lateinit var rvMessage : RecyclerView
    private lateinit var rlPostDetail : RelativeLayout
    private lateinit var imgPost : ShapeableImageView
    private lateinit var tvPostTitle : TextView
    private lateinit var tvPostPrice : TextView

    private var chatList : ArrayList<ModelMessages> = ArrayList()
    private lateinit var adapterMessages : AdapterMessage

    private var sendTo : AgenciesData ?= null
    private var sendBy : AgenciesData ?= null
    private var postData : AdsDataChat ?= null
    private var myId : String ?= null
    private var myName : String ?= null
    private var receiverId : String ?= null
    private var requestId : String ?= null
    private var myChatListRef : String ?= null
    private var receiverChatListRef : String ?= null
    private var receiverUnreadCount = 0

    private var refUsers = FirebaseDatabase.getInstance().getReference("UsersStatusBaghdad")
    private var refChatList = FirebaseDatabase.getInstance().getReference("ChatListBaghdad")
    private var refChats = FirebaseDatabase.getInstance().getReference("MessagesBaghdad")

    private var getReceiverStatusListener: ValueEventListener?= null
    private var updateUnreadCountListener: ValueEventListener ?= null
    private var getUnreadCountListener: ValueEventListener ?= null
    private var getChatsListener: ValueEventListener ?= null

    private var mAuth : FirebaseAuth = FirebaseAuth.getInstance()

    private var firstLoad : Boolean = true

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_messaging)
        adjustFontScale(resources.configuration)
        if (mAuth.currentUser == null){
            mAuth.signInAnonymously()
        }
        inits()
        clickListeners()

    }

    private fun inits(){
        img_back = findViewById(R.id.img_back)
        imgSendMessage = findViewById(R.id.imgSendMessage)
        imgPerson = findViewById(R.id.imgPerson)
        tvName = findViewById(R.id.tvName)
        tvLastSeen = findViewById(R.id.tvLastSeen)
        etMessage = findViewById(R.id.etMessage)
        rvMessage = findViewById(R.id.rvMessage)
        rlPostDetail = findViewById(R.id.rlPostDetail)
        imgPost = findViewById(R.id.imgPost)
        tvPostTitle = findViewById(R.id.tvPostTitle)
        tvPostPrice = findViewById(R.id.tvPostPrice)

        if (intent.getStringExtra("receiverModel") != null){
            sendTo = Gson().fromJson(intent.getStringExtra("receiverModel"),AgenciesData::class.java)

            tvName.text = sendTo?.display_name!!

            if (!sendTo?.user_image.isNullOrEmpty()){
                Glide.with(this).load(sendTo?.user_image).placeholder(R.drawable.img_placeholder).into(imgPerson)
            }
        }
        if (intent.getStringExtra("postData") != null){
            postData = Gson().fromJson(intent.getStringExtra("postData"),AdsDataChat::class.java)

            if (postData != null){
                rlPostDetail.visibility = View.VISIBLE

                Glide.with(this).load(postData?.thumbnail.orEmpty()).placeholder(R.drawable.img_placeholder).into(imgPost)

                tvPostTitle.text = postData?.post_title.orEmpty()
                tvPostPrice.text = "(${postData?.price ?: "0"})${getString(R.string.currency_code)}"
            }
        }

        val myData = PreferencesService.instance.getUserData
        sendBy = AgenciesData(call_number = myData?.callNumber.orEmpty(), whatsapp_number = myData?.whatsappNumber.orEmpty(), ID = myData?.ID, display_name = myData?.displayName, user_image = myData?.userImage, device_token = myData?.device_token)
        myId = myData?.ID
        myName = myData?.displayName
        receiverId = sendTo?.ID

        if (myId.toString().toInt() < receiverId.toString().toInt()){
            requestId = myId+"_"+receiverId
        } else{
            requestId = receiverId+"_"+myId
        }
        if (postData != null){
            requestId += "_${postData?.iD}"
        }

        receiverChatListRef = if (postData != null){
            "id_${receiverId}_${postData?.iD}"
        } else {
            "id_$receiverId"
        }

        myChatListRef = if (postData != null){
            "id_${myId}_${postData?.iD}"
        } else {
            "id_$myId"
        }

        adapterMessages = AdapterMessage(this,myId!!,chatList)
        rvMessage.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        rvMessage.itemAnimator = null
        rvMessage.adapter = adapterMessages
    }

    override fun onBackPressed() {
        //super.onBackPressed()
        val intent = Intent(this, HomeActivity::class.java)
        intent.putExtra("type", "Message")
        startActivity(intent)
        overridePendingTransition(0,0)
    }
    private fun clickListeners(){
        rlPostDetail.setOnClickListener {
            val intent = Intent(this, AdsDetailsActivity::class.java)
            intent.putExtra("propertyId", postData?.iD.orEmpty())
            startActivity(intent)
            overridePendingTransition(0,0)
        }
        img_back.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.putExtra("type", "Message")
            startActivity(intent)
            overridePendingTransition(0,0)
        }

        etMessage.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {

            }

            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                updateMyTypingStatus(receiverId.toString())
            }

            override fun afterTextChanged(p0: Editable?) {
                Handler(Looper.getMainLooper()).postDelayed({
                    updateMyTypingStatus("0")
                },2000)
            }
        })

        imgSendMessage.setOnClickListener {
            if (etMessage.text.toString().trim().isEmpty()) {
                showToast(this,getString(R.string.enter_message))
            } else if (!isNetworkAvailable()){
                showToast(this,getString(R.string.intenet_error))
            } else {
                sendMessage(etMessage.text.toString().trim())
                etMessage.setText("")
                this.dismissKeyboard(etMessage)
            }
        }
    }

    override fun onPause() {
        updateMyTypingStatus("0")

        if (getReceiverStatusListener != null){
            refUsers.child(receiverId!!).removeEventListener(getReceiverStatusListener!!)
        }

        if (updateUnreadCountListener != null){
            refChatList.child("id_$myId").child(receiverChatListRef!!/*"id_$receiverId"*/).removeEventListener(updateUnreadCountListener!!)
        }

        if (getUnreadCountListener != null){
            refChatList.child("id_$receiverId").child(myChatListRef!!/*"id_$myId"*/).removeEventListener(getUnreadCountListener!!)
        }

        if (getChatsListener != null){
            refChats.child(requestId!!).removeEventListener(getChatsListener!!)
        }
        super.onPause()
    }

    override fun onResume() {
        super.onResume()
        getReceiverStatus()
        getUnreadCount()
        updateUnreadCount()
        getMessages()
    }

    private fun getReceiverStatus(){
        getReceiverStatusListener = refUsers.child(receiverId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {

                var onlineStatus = "Offline"
                if (dataSnapshot.child("onlineStatus").value != null){
                    onlineStatus = dataSnapshot.child("onlineStatus").value.toString()
                }

                val typingTo = if (dataSnapshot.child("typingTo").value!= null){
                    dataSnapshot.child("typingTo").value
                } else{
                    "0"
                }

                if (typingTo == myId) {
                    tvLastSeen.text = "Typing...."
                } else {
                    if (onlineStatus == "Offline"){
                        tvLastSeen.text = "Offline"
                    } else if (onlineStatus == "0") {
                        tvLastSeen.text = "Online"
                    } else {
                        val lastSeen = Utility.getLastSeenTime(this@MessagingActivity,onlineStatus.toLong())
                        tvLastSeen.text = lastSeen
                    }
                }
            }

            override fun onCancelled(databaseError: DatabaseError) {}
        })

    }

    private fun updateUnreadCount(){
        updateUnreadCountListener = refChatList.child("id_$myId").child(receiverChatListRef!!/*"id_$receiverId"*/)//.child("unreadCount").setValue(0)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastMsgBy = snapshot.child("sentTo").value
                    if (lastMsgBy != null && lastMsgBy == myId){
                        refChatList.child("id_$myId").child(receiverChatListRef!!/*"id_$receiverId"*/).child("unreadCount")
                            .setValue(0)
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun getUnreadCount(){
        getUnreadCountListener = refChatList.child("id_$receiverId").child(myChatListRef!!/*"id_$myId"*/)
            .addValueEventListener(object : ValueEventListener{
                override fun onDataChange(snapshot: DataSnapshot) {
                    val lastMsgBy = snapshot.child("sentBy").value
                    if (lastMsgBy != null && lastMsgBy == myId){
                        if (snapshot.child("unreadCount").value != null){
                            receiverUnreadCount = snapshot.child("unreadCount").value.toString().toInt()
                        }
                    }
                }

                override fun onCancelled(error: DatabaseError) {

                }
            })
    }

    private fun getMessages() {
        if (firstLoad){
            progressHUD = ProgressHud.show(this,false,false)
        }
        getChatsListener = refChats.child(requestId!!).addValueEventListener(object : ValueEventListener {
            override fun onDataChange(dataSnapshot: DataSnapshot) {
                chatList.clear()
                for (i in dataSnapshot.children) {
                    val modelChat = i.getValue(ModelMessages::class.java)
                    chatList.add(modelChat!!)
                }

                firstLoad = false
                adapterMessages.notifyItemRangeChanged(0,chatList.size)
                Handler(Looper.getMainLooper()).post {
                    rvMessage.scrollToPosition(chatList.size-1)
                }
                try {
                    if (progressHUD?.isShowing == true){
                        progressHUD?.dismiss()
                    }
                } catch (e : Exception){ e.localizedMessage}
            }

            override fun onCancelled(error: DatabaseError) {
                try {
                    if (progressHUD?.isShowing == true){
                        progressHUD?.dismiss()
                    }
                } catch (e : Exception){ e.localizedMessage}
            }
        })
    }

    private fun updateMyTypingStatus(typing: String) {
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["typingTo"] = typing
        refUsers.child(myId!!).updateChildren(hashMap)
    }

    private fun sendMessage(message: String) {
        val timestamp = System.currentTimeMillis().toString()
        val hashMap: HashMap<String, Any> = HashMap()
        hashMap["sender"] = myId!!
        hashMap["receiver"] = receiverId!!
        hashMap["requestId"] = requestId!!
        if (postData != null) {
            hashMap["postId"] = postData?.iD.orEmpty()
        }
        hashMap["message"] = message
        hashMap["timestamp"] = timestamp
        refChats.child(requestId!!).push().setValue(hashMap)

        val mapMyList : HashMap<String,Any> = HashMap()
        mapMyList["message"] = message
        mapMyList["time"] = System.currentTimeMillis().toString()
        mapMyList["sentBy"] = myId!!
        mapMyList["sentTo"] = receiverId!!
        if (postData != null) {
            mapMyList["postId"] = postData?.iD.orEmpty()
        }

        refChatList.child("id_$myId").child(receiverChatListRef!!/*"id_$receiverId"*/).updateChildren(mapMyList)

        mapMyList["unreadCount"] = receiverUnreadCount + 1
        refChatList.child("id_$receiverId").child(myChatListRef!!/*"id_$myId"*/).updateChildren(mapMyList)

        if (!sendTo?.device_token.isNullOrEmpty()){
            sendNotificationToUser(sendTo?.device_token!!,myName!!,message,Gson().toJson(sendTo),Gson().toJson(sendBy),Gson().toJson(postData))
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {

    }
}