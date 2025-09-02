package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ServicesListModel(
    @SerializedName("code") val code : String,
    @SerializedName("success") val success : Boolean? = false,
    @SerializedName("response") val response : List<ServicesListResponse>? = null
)

data class ServicesListResponse(
    @SerializedName("data") val data : ServicesListData,
    @SerializedName("meta_data") val meta_data : ServicesListMetaData,
    @SerializedName("thumbnail") val thumbnail : String
)

data class ServicesListData(
    @SerializedName("ID") val iD : String,
    @SerializedName("post_author") val post_author : String,
    @SerializedName("post_date") val post_date : String,
    @SerializedName("post_date_gmt") val post_date_gmt : String,
    @SerializedName("post_content") val post_content : String,
    @SerializedName("post_title") val post_title : String,
    @SerializedName("post_excerpt") val post_excerpt : String,
    @SerializedName("post_status") val post_status : String,
    @SerializedName("comment_status") val comment_status : String,
    @SerializedName("ping_status") val ping_status : String,
    @SerializedName("post_password") val post_password : String,
    @SerializedName("post_name") val post_name : String,
    @SerializedName("to_ping") val to_ping : String,
    @SerializedName("pinged") val pinged : String,
    @SerializedName("post_modified") val post_modified : String,
    @SerializedName("post_modified_gmt") val post_modified_gmt : String,
    @SerializedName("post_content_filtered") val post_content_filtered : String,
    @SerializedName("post_parent") val post_parent : String,
    @SerializedName("guid") val guid : String,
    @SerializedName("menu_order") val menu_order : String,
    @SerializedName("post_type") val post_type : String,
    @SerializedName("post_mime_type") val post_mime_type : String,
    @SerializedName("comment_count") val comment_count : String,
    @SerializedName("filter") val filter : String
)
data class ServicesListMetaData(
    @SerializedName("address") val address : List<String>,
    @SerializedName("select_type") val select_type : List<String>,
    @SerializedName("facebook_url") val facebook_url : List<String>,
    @SerializedName("instagram_url") val instagram_url : List<String>,
    @SerializedName("call_number") val call_number : List<String>,
    @SerializedName("whatsapp") val whatsapp : List<String>,
    @SerializedName("from") val from : List<String>,
    @SerializedName("to") val to : List<String>,
    @SerializedName("arabic_title") var arabicTitle : List<String>
)