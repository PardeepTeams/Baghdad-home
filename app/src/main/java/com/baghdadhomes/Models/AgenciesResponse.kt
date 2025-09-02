package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class AgenciesResponse(
    @SerializedName("code") val code : Int ?= null,
    @SerializedName("success") val success : Boolean ?= false,
    @SerializedName("message") val message : String ?= null,
    @SerializedName("response") val response : ArrayList<AgenciesData> ?= null
)

data class AgenciesData(
    @SerializedName("ID") val ID : String ?= null,
    @SerializedName("user_login") val user_login : String ?= null,
    @SerializedName("user_pass") val user_pass : String ?= null,
    @SerializedName("share_url") val share_url : String ?= null,
    @SerializedName("user_nicename") val user_nicename : String ?= null,
    @SerializedName("user_email") val user_email : String ?= null,
    @SerializedName("user_url") val user_url : String ?= null,
    @SerializedName("user_registered") val user_registered : String ?= null,
    @SerializedName("user_activation_key") val user_activation_key : String ?= null,
    @SerializedName("user_status") val user_status : String ?= null,
    @SerializedName("sign_in_with") val sign_in_with : String ?= null,
    @SerializedName("call_number") val call_number : String ?= null,
    @SerializedName("whatsapp_number") val whatsapp_number : String ?= null,
    @SerializedName("user_image") val user_image : String ?= null,
    @SerializedName("display_name") val display_name : String ?= null,
    @SerializedName("device_token") val device_token : String ?= null,
    @SerializedName("role_type") val role_type : String ?= null,
    @SerializedName("is_followed") val is_followed : Boolean ?= false,
    @SerializedName("total_followers") val total_followers : String ?= null,
    @SerializedName("address") val address : String ?= null,
    @SerializedName("manager_name") val manager_name : String ?= null,
    @SerializedName("total_posts") val total_posts : String ?= null,
    @SerializedName("lat") val lat : String ?= null,
    @SerializedName("lng") val lng : String ?= null,
    @SerializedName("city") val city : String ?= null,
    @SerializedName("slug") val slug : String ?= null,
    @SerializedName("nbhd_slug") val nbhd_slug : ArrayList<NBHDArea> ?= null,
    @SerializedName("city_slug") val city_slug : ArrayList<NBHDDataResponse> ?= null,
)
