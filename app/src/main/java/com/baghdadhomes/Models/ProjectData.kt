package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectData(
    @SerializedName("ID")
    val id: Int? = null,

    @SerializedName("post_title")
    val postTitle: String? = null,

    @SerializedName("post_content")
    val postContent: String? = null,

    @SerializedName("post_date")
    val postDate: String? = null,

    @SerializedName("thumbnail")
    val thumbnail: String? = null,

    @SerializedName("is_fav")
    val isFav: Boolean? = null,

    @SerializedName("property_address")
    val propertyAddress: ProjectPropertyAddress? = null,

    @SerializedName("property_type")
    val propertyType: String? = null,

    @SerializedName("property_attr")
    val propertyAttr: ProjectPropertyAttr? = null,

    @SerializedName("fave_property_bedrooms")
    val favePropertyBedrooms: String? = null,

    @SerializedName("fave_property_bathrooms")
    val favePropertyBathrooms: String? = null,

    @SerializedName("fave_property_size")
    val favePropertySize: String? = null,

    @SerializedName("fave_property_land")
    val favePropertyLand: String? = null,

    @SerializedName("houzez_total_property_views")
    val houzezTotalPropertyViews: String? = null,

    @SerializedName("price")
    val price: String? = null,

    @SerializedName("is_paid_ad")
    val isPaidAd: String? = null,

    @SerializedName("is_paid_ad_start_date")
    val isPaidAdStartDate: String? = null,

    @SerializedName("is_paid_ad_end_date")
    val isPaidAdEndDate: String? = null,

    @SerializedName("is_premium")
    val isPremium: Boolean? = null,

    @SerializedName("floor_plans")
    val floorPlans: List<ProjectFloorPlan>? = ArrayList()

)

data class ProjectPropertyAddress(
    @SerializedName("property_area")
    val propertyArea: String? = null,

    @SerializedName("property_city")
    val propertyCity: String? = null
)
data class ProjectPropertyAttr(
    @SerializedName("property_type")
    val propertyType: String? = null,

    @SerializedName("property_status")
    val propertyStatus: String? = null
)
