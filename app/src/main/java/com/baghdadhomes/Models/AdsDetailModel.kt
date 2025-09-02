package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class AdsDetailModel(
    @SerializedName("code") val code : Int,
    @SerializedName("success") val success : Boolean,
    @SerializedName("message") val message : String,
    @SerializedName("result") val result : ResultDetail
)

data class ResultDetail (
    @SerializedName("ID") val iD : String,
    @SerializedName("post_content") val post_content : String,
    @SerializedName("post_title") val post_title : String,
    @SerializedName("post_status") val post_status : String,
    @SerializedName("post_type") val post_type : String,
    @SerializedName("is_fav") var is_fav : Boolean,
    @SerializedName("link") val link : String,
    @SerializedName("post_modified") val post_modified : String,
    @SerializedName("thumbnail") val thumbnail : String,
    @SerializedName("property_meta") val property_meta : Property_meta,
    @SerializedName("property_type") val property_type : String,
    @SerializedName("property_attr") val property_attr : Property_attr,
    @SerializedName("property_images") val property_images : List<String>,
    @SerializedName("property_images_id") val property_images_id : List<Int>,
    @SerializedName("property_images_thumb") val property_images_thumb : List<String>,
    @SerializedName("price") val price : String,
    @SerializedName("agent_agency_info") val agent_agency_info : AgenciesData,
    @SerializedName("property_address") val property_address : Property_address,
    @SerializedName("is_premium") val is_premium : Boolean,
)

data class Property_meta (
    @SerializedName("fave_property_size") val fave_property_size : List<String>,
    @SerializedName("fave_property_land") val fave_property_land : List<String>,
    @SerializedName("fave_property_bedrooms") val fave_property_bedrooms : List<String>,
    @SerializedName("fave_property_bathrooms") val fave_property_bathrooms : List<String>,
    @SerializedName("fave_property_garage") val fave_property_garage : List<String>,
    @SerializedName("fave_property_garage_size") val fave_property_garage_size : List<String>,
    @SerializedName("houzez_total_property_views") val houzez_total_property_views : List<String>,
    @SerializedName("_thumbnail_id") val _thumbnail_id : List<String>,
    @SerializedName("fave_property_images") val fave_property_images : List<String>,
    @SerializedName("video") val video : List<String>,
    @SerializedName("houzez_geolocation_lat") val houzez_geolocation_lat : List<String>,
    @SerializedName("houzez_geolocation_long") val houzez_geolocation_long : List<String>,
    @SerializedName("reel_view") val reel_view : List<String>,
)

/*data class Agent_agency_info (
    @SerializedName("call_number") val call_number : String,
    @SerializedName("whatsapp_number") val whatsapp_number : String,
    @SerializedName("ID") val ID : String ?= null,
    @SerializedName("display_name") val display_name : String ?= null,
    @SerializedName("user_image") val user_image : String ?= null,
    @SerializedName("device_token") val device_token : String ?= null,
    @SerializedName("address") val address : String ?= null,
    @SerializedName("role_type") val role_type : String ?= null,
)*/

data class Property_address (
    @SerializedName("property_area") val property_area:String,
    @SerializedName("property_country") val property_country:String,
    @SerializedName("property_state") val property_state:String,
    @SerializedName("property_city") val property_city:String,
)

data class Property_attr (
    @SerializedName("property_type") val property_type : String,
    @SerializedName("property_status") val property_status : String,
)