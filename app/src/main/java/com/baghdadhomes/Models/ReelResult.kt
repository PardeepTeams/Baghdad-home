package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName



data class ReelResult(
    @SerializedName("code") val code : Int ?= null,
    @SerializedName("message") val message : String ?= null,
    @SerializedName("success") val success : Boolean ?= false,
    @SerializedName("response")  val response : ArrayList<ReelsData> ?= null,
)

data class ReelsData(
    @SerializedName("post_id") val post_id : String ?= null,
    @SerializedName("is_fav") var is_fav : Boolean ?= false,
    @SerializedName("houzez_total_property_views") val houzez_total_property_views : String ?= null,
    @SerializedName("title") val title : String ?= null,
    @SerializedName("thumbnail") val thumbnail : String ?= null,
    @SerializedName("price") val price : String ?= null,
    @SerializedName("video") val video : String ?= null,
    @SerializedName("video_thumbnail") val video_thumbnail : String ?= null,
    @SerializedName("link") val link : String ?= null,
    @SerializedName("call_number") val call_number : String ?= null,
    @SerializedName("whatsapp_number") val whatsapp_number : String ?= null,
    @SerializedName("status") var status : String ?= null,
    @SerializedName("menu_order") var menu_order : String ?= null,
    @SerializedName("view_date") val view_date : String ?= null,
    @SerializedName("ID") val ID : String ?= null,
    @SerializedName("display_name") val display_name : String ?= null,
    @SerializedName("user_image") val user_image : String ?= null,
    @SerializedName("device_token") val device_token : String ?= null,
    @SerializedName("reel_view") var reel_view : String ?= "0",
)
