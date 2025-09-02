package com.baghdadhomes.Activities

import android.app.AlertDialog
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.baghdadhomes.Adapters.AdapterNotifications
import com.baghdadhomes.Models.NotificationsDataModel
import com.baghdadhomes.Models.NotificationsList
import com.baghdadhomes.PreferencesService
import com.baghdadhomes.R
import com.baghdadhomes.Utils.Constants

class NotificationsActivity : BaseActivity(), AdapterNotifications.NotificationClick {
    private lateinit var notificationAdapter: AdapterNotifications
    //var notificationList:ArrayList<NotificationData> = ArrayList()
    lateinit var img_back:ImageView
    lateinit var img_select:ImageView
    lateinit var img_delete_selected:ImageView
    private lateinit var rv_notifications:RecyclerView
    private var notificationList: ArrayList<NotificationsList> = ArrayList()
    private var userId : String ?= null
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_notifications)
        adjustFontScale(resources.configuration)
        userId = PreferencesService.instance.getUserData!!.ID.toString()
        img_back = findViewById(R.id.img_back)
        rv_notifications = findViewById(R.id.rv_notifications)
        img_select = findViewById(R.id.img_select)
        img_delete_selected = findViewById(R.id.img_delete_selected)

        img_back.setOnClickListener {
            finish()
            overridePendingTransition(0,0)
        }

        /*val sharedPreferences = getSharedPreferences("Notifications", MODE_PRIVATE)
        if (sharedPreferences.getString("Notificationdata", null) != null) {
            val data: String = sharedPreferences.getString("Notificationdata", null)!!
            var notificationModel: NotificationsModel?= Gson().fromJson(
                data,
                NotificationsModel::class.java
            )
            notificationList = notificationModel!!.notificationData!!
        }*/

        hitGetApiWithoutToken(Constants.GET_NOTIFICATIONS, true,
            Constants.GET_NOTIFICATIONS_API + userId.toString())

        rv_notifications.layoutManager = LinearLayoutManager(this,LinearLayoutManager.VERTICAL,false)
        notificationAdapter = AdapterNotifications(this,notificationList,this,false)
        rv_notifications.itemAnimator = null
        rv_notifications.adapter = notificationAdapter

        img_select.setOnClickListener {
            img_select.visibility = View.GONE
            img_delete_selected.visibility = View.VISIBLE
            notificationAdapter.isSelectionEnabled = true
            notificationAdapter.notifyItemRangeChanged(0,notificationList.size)
        }

        img_delete_selected.setOnClickListener {
            val selectedData : ArrayList<String> = ArrayList()
            for (i in notificationList){
                if (i.isSelected == true){
                    selectedData.add(i.id!!)
                }
            }

            if (selectedData.isNotEmpty()){
                val alertDialog = AlertDialog.Builder(this)
                alertDialog.setTitle(resources.getString(R.string.delete))
                alertDialog.setMessage(resources.getString(R.string.delete_notification_message_multiple))
                alertDialog.setPositiveButton(resources.getString(R.string.yes))
                { _, _ ->
                    if (isNetworkAvailable()){
                        img_select.visibility = View.VISIBLE
                        img_delete_selected.visibility = View.GONE
                        notificationAdapter.isSelectionEnabled = false
                        notificationAdapter.notifyItemRangeChanged(0,notificationList.size)

                        Log.d("selectedData",selectedData.toString())

                        val map: HashMap<String, String> = HashMap()
                        map["user_id"] = userId.toString()
                        //map["notification_id[]"] = selectedData
                        hitPostApiNotificationDelete(Constants.DELETE_MULTIPLE_NOTIFICATIONS, true,
                            Constants.DELETE_MULTIPLE_NOTIFICATIONS_API, map,selectedData)
                    }
                }
                alertDialog.setNegativeButton(resources.getString(R.string.no))
                { dialog, _ ->
                    dialog.dismiss()
                    img_select.visibility = View.VISIBLE
                    img_delete_selected.visibility = View.GONE
                    notificationAdapter.isSelectionEnabled = false
                    for (i in notificationList){
                        i.isSelected = false
                    }
                    notificationAdapter.notifyItemRangeChanged(0,notificationList.size)
                }
                alertDialog.show()
            } else{
                img_select.visibility = View.VISIBLE
                img_delete_selected.visibility = View.GONE
                notificationAdapter.isSelectionEnabled = false
                for (i in notificationList){
                    i.isSelected = false
                }
                notificationAdapter.notifyItemRangeChanged(0,notificationList.size)
            }
        }
    }

    override fun getResponse(apiType: String, respopnse: JsonObject) {
        if (apiType == Constants.GET_NOTIFICATIONS){
            val model = Gson().fromJson(respopnse, NotificationsDataModel::class.java)
            if (model.success == true && model.code == 200){
                notificationList.clear()
                if (model.data != null){
                    notificationList.addAll(model.data)
                }
                //notificationList.addAll(model.data)
                notificationAdapter.notifyItemRangeChanged(0,notificationList.size)
            }
        } else if (apiType == Constants.DELETE_MULTIPLE_NOTIFICATIONS){
            val model = Gson().fromJson(respopnse, NotificationsDataModel::class.java)
            if (model.success == true && model.code == 200) {
                hitGetApiWithoutToken(
                    Constants.GET_NOTIFICATIONS, false,
                    Constants.GET_NOTIFICATIONS_API + userId.toString())
            } else{
                showToast(this,getString(R.string.something_went_wrong))
            }
        }
    }

    override fun onNotificationRemove(model: NotificationsList, position: Int) {
        val alertDialog = AlertDialog.Builder(this)
        alertDialog.setTitle(resources.getString(R.string.delete))
        alertDialog.setMessage(resources.getString(R.string.delete_notification_message))
        alertDialog.setPositiveButton(resources.getString(R.string.yes))
        { _, _ ->
            if (isNetworkAvailable()){
                val notificationId = model.id.toString()
                notificationList.removeAt(position)
                notificationAdapter.notifyItemRemoved(position)
                notificationAdapter.notifyItemRangeChanged(0,notificationList.size)
                //notificationAdapter.notifyDataSetChanged()
                val map: HashMap<String, String> = HashMap()
                map["user_id"] = userId.toString()
                map["notification_id"] = notificationId
                hitPostApi(Constants.DELETE_NOTIFICATIONS, false,
                    Constants.DELETE_NOTIFICATIONS_API, map)
            }
        }
        alertDialog.setNegativeButton(resources.getString(R.string.no))
        { dialog, _ ->
            dialog.dismiss()
        }
        alertDialog.show()
    }
}