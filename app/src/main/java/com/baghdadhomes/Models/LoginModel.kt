package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class LoginModel(
    @SerializedName("success") val success : Boolean,
    @SerializedName("response") val response : LoginResponse,
    @SerializedName("message") val message : String? = null
)

data class LoginResponse(
    @SerializedName("ID") var ID: String? = null,
    @SerializedName("user_login") var userLogin: String? = null,
    @SerializedName("user_pass") var userPass: String? = null,
    @SerializedName("user_nicename") var userNicename: String? = null,
    @SerializedName("user_email") var userEmail: String? = null,
    @SerializedName("user_url") var userUrl: String? = null,
    @SerializedName("user_registered") var userRegistered: String? = null,
    @SerializedName("user_activation_key") var userActivationKey: String? = null,
    @SerializedName("user_status") var userStatus: String? = null,
    @SerializedName("display_name") var displayName: String? = null,
    @SerializedName("call_number") var callNumber: String? = null,
    @SerializedName("whatsapp_number") var whatsappNumber: String? = null,
    @SerializedName("user_image") var userImage: String? = null,
    @SerializedName("device_token") var device_token: String? = null,
    @SerializedName("address") var address: String? = null,
    @SerializedName("role_type") var role_type: String? = null,
    @SerializedName("city_slug") var city_slug: String? = null,
    @SerializedName("nbhd_slug") var nbhd_slug: String? = null,
    @SerializedName("manager_name") var manager_name: String? = null,
    @SerializedName("allowed_ads") var allowed_ads: String? = null,
    @SerializedName("lat") var lat: String? = null,
    @SerializedName("lng") var lng: String? = null,
)