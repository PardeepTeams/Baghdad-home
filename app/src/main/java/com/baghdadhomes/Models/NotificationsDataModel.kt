package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class NotificationsDataModel (
    @SerializedName("code") var code : Int? = null,
    @SerializedName("success") var success : Boolean? = null,
    @SerializedName("data") var data : ArrayList<NotificationsList>
)

data class NotificationsList (
    @SerializedName("id") var id : String? = null,
    @SerializedName("title") var title : String? = null,
    @SerializedName("description") var description : String? = null,
    @SerializedName("status") var status : String? = null,
    var isSelected : Boolean ?= false
)