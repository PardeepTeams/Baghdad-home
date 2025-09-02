package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class NewFeatureModel (

    @SerializedName("success") val success : Boolean,
    @SerializedName("count") val count : Int,
    @SerializedName("result") var result : List<Result>
)

data class Result (
    @SerializedName("ID") var iD : String,
    @SerializedName("post_title") var post_title : String,
    @SerializedName("post_content") var post_content : String,
    @SerializedName("post_date") var post_date : String,
    @SerializedName("thumbnail") var thumbnail : String,
    @SerializedName("is_fav") var is_fav : Boolean? = false,
    @SerializedName("property_address") var property_address : Property_address_New,
    @SerializedName("property_type") var property_type : String,
    @SerializedName("property_attr") var property_attr : Property_attr_New,
    @SerializedName("fave_property_bedrooms") var fave_property_bedrooms : String,
    @SerializedName("fave_property_bathrooms") var fave_property_bathrooms : String,
    @SerializedName("fave_property_size") val fave_property_size : String,
    @SerializedName("fave_property_land") val fave_property_land : String,
    @SerializedName("price") val price : String,
    @SerializedName("houzez_total_property_views") var totalViews: String,
    @SerializedName("is_premium") var is_premium: Boolean,
)

data class Property_attr_New (
    @SerializedName("property_type") val property_type : String,
    @SerializedName("property_status") val property_status : String
)

data class Property_address_New (
    @SerializedName("property_area") val property_area : String,
    @SerializedName("property_state") val property_state : String,
    @SerializedName("property_country") val property_country:String,
    @SerializedName("property_city") val property_city:String,
)