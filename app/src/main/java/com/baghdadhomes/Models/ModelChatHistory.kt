package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ModelChatHistory (
    @SerializedName("code") val code : String ?= null,
    @SerializedName("success") val success : Boolean ?= false,
    @SerializedName("response") val response : ArrayList<ChatHistoryData> ?= null,
    @SerializedName("message") val message : String? = null
)

data class ChatHistoryData(
    @SerializedName("message") val message : String? = null,
    @SerializedName("sentBy") val sentBy : String? = null,
    @SerializedName("sentTo") val sentTo : String? = null,
    @SerializedName("postId") val postId : String? = null,
    @SerializedName("title") val title : String? = null,
    @SerializedName("thumbnail") val thumbnail : String? = null,
    @SerializedName("price") val price : String? = null,
    @SerializedName("time") val time : String? = null,
    @SerializedName("unreadCount") val unreadCount : String? = null,
    @SerializedName("sent_by") val sent_by : AgenciesData? = null,
    @SerializedName("sent_to") val sent_to : AgenciesData? = null,
)

data class AdsDataChat (
    @SerializedName("ID") var iD : String ?= null,
    @SerializedName("post_title") var post_title : String ?= null,
    @SerializedName("thumbnail") var thumbnail : String ?= null,
    @SerializedName("price") val price : String ?= null,
)