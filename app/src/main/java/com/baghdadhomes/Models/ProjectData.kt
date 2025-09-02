package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectData(
    @SerializedName("ID") val id: Int,
    @SerializedName("post_title") val postTitle: String,
    @SerializedName("post_content") val postContent: String,
    @SerializedName("post_date") val postDate: String,
    @SerializedName("thumbnail") val thumbnail: String,
    @SerializedName("is_fav") val isFav: Boolean,
    @SerializedName("property_address") val propertyAddress: PropertyAddress,
    @SerializedName("property_type") val propertyType: String,
    @SerializedName("property_attr") val propertyAttr: PropertyAttr,
    @SerializedName("fave_property_bedrooms") val favePropertyBedrooms: String,
    @SerializedName("fave_property_bathrooms") val favePropertyBathrooms: String,
    @SerializedName("fave_property_size") val favePropertySize: String,
    @SerializedName("fave_property_land") val favePropertyLand: String,
    @SerializedName("houzez_total_property_views") val houzezTotalPropertyViews: Any?, // Can be Int? if always number
    @SerializedName("price") val price: String,
    @SerializedName("rohit") val rohit: String,
    @SerializedName("is_paid_ad") val isPaidAd: String,
    @SerializedName("is_paid_ad_start_date") val isPaidAdStartDate: String,
    @SerializedName("is_paid_ad_end_date") val isPaidAdEndDate: String,
    @SerializedName("is_premium") val isPremium: Boolean,
    @SerializedName("floor_plans") val floorPlans: List<ProjectFloorPlan>
)
