package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectDetailResponse(
    @SerializedName("success") val success: String? = null,
    @SerializedName("data") val data: PropertyDetailData? = null
)

data class PropertyDetailData(
    @SerializedName("ID") val id: Int? = null,
    @SerializedName("post_title") val postTitle: String? = null,
    @SerializedName("post_content") val postContent: String? = null,
    @SerializedName("post_date") val postDate: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("is_fav") var isFav: Boolean? = null,
    @SerializedName("property_address") val propertyAddress: ProjectPropertyAddress? = null,
    @SerializedName("property_type") val propertyType: String? = null,
    @SerializedName("property_attr") val propertyAttr: ProjectPropertyAttr? = null,
    @SerializedName("fave_property_bedrooms") val bedrooms: String? = null,
    @SerializedName("fave_property_bathrooms") val bathrooms: String? = null,
    @SerializedName("fave_property_size") val size: String? = null,
    @SerializedName("fave_property_land") val land: String? = null,
    @SerializedName("fave_property_garage") val floors: String? = null,
    @SerializedName("houzez_total_property_views") val totalViews: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("fave_currency") val faveCurrency: String? = null,
    @SerializedName("is_paid_ad") val isPaidAd: String? = null,
    @SerializedName("is_paid_ad_start_date") val paidAdStartDate: String? = null,
    @SerializedName("is_paid_ad_end_date") val paidAdEndDate: String? = null,
    @SerializedName("fave_property_location") val favePropertyLocation: String? = null,
    @SerializedName("houzez_geolocation_lat") val houzezGeoLocationLat: String? = null,
    @SerializedName("houzez_geolocation_long") val houzezGeoLocationLong: String? = null,
    @SerializedName("link") val link: String? = null,
    @SerializedName("is_premium") val isPremium: Boolean? = null,
    @SerializedName("gallery_images") val galleryImages: ArrayList<GalleryImages>? = null,
    @SerializedName("floor_plans") val floorPlans: ArrayList<ProjectFloorPlan>? = null,
    @SerializedName("child_properties") val childProperties: ArrayList<ChildProperty>? = null
)

data class GalleryImages(
    @SerializedName("id") val id: String? = null,
    @SerializedName("url") val url: String? = null,
)

data class ChildProperty(
    @SerializedName("ID") val id: Int? = null,
    @SerializedName("post_title") val postTitle: String? = null,
    @SerializedName("post_content") val postContent: String? = null,
    @SerializedName("post_date") val postDate: String? = null,
    @SerializedName("thumbnail") val thumbnail: String? = null,
    @SerializedName("is_fav") var isFav: Boolean? = null,
    @SerializedName("property_address") val propertyAddress: ProjectPropertyAddress? = null,
    @SerializedName("property_type") val propertyType: String? = null,
    @SerializedName("property_attr") val propertyAttr: ProjectPropertyAttr? = null,
    @SerializedName("fave_property_bedrooms") val bedrooms: String? = null,
    @SerializedName("fave_property_bathrooms") val bathrooms: String? = null,
    @SerializedName("fave_property_size") val size: String? = null,
    @SerializedName("fave_property_land") val land: String? = null,
    @SerializedName("houzez_total_property_views") val totalViews: String? = null,
    @SerializedName("price") val price: String? = null,
    @SerializedName("fave_currency") val faveCurrency: String? = null,
    @SerializedName("is_paid_ad") val isPaidAd: String? = null,
    @SerializedName("is_paid_ad_start_date") val paidAdStartDate: String? = null,
    @SerializedName("is_paid_ad_end_date") val paidAdEndDate: String? = null,
    @SerializedName("is_premium") val isPremium: Boolean? = null
)