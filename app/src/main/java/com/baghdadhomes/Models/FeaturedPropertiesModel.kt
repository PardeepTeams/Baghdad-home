package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName
import java.util.*
import kotlin.collections.ArrayList

data class FeaturedPropertiesModel(
    @SerializedName("success") val success : Boolean,
    @SerializedName("count")val count:Int,
    @SerializedName("result") val result : List<ResultFeatured>
    )

data class ResultFeatured(
    @SerializedName("ID") val iD : String,
    @SerializedName("post_content") val post_content : String,
    @SerializedName("post_title") val post_title : String,
    @SerializedName("post_excerpt") val post_excerpt : String,
    @SerializedName("post_status") val post_status : String,
    @SerializedName("post_name") val post_name : String,
    @SerializedName("thumbnail") val thumbnail : Object,
    @SerializedName("property_type") val property_type : String,
    @SerializedName("price") val price : String,
    @SerializedName("property_address")val property_address:PropertyAddress,
    @SerializedName("property_meta")val property_meta:PropertyMeta,
    @SerializedName("property_attr") val property_attr:PropertyAttr? = null,
    @SerializedName("property_images") val property_images:ArrayList<String>,
    @SerializedName("property_images_id") val property_images_id:ArrayList<String>,
    @SerializedName("link") val link:String? = null,
    @SerializedName("is_fav") var is_fav:Boolean? = false,
    @SerializedName("is_premium") var is_premium:Boolean? = false,
    @SerializedName("agent_agency_info") val agent_agency_info : AgentAgencyInfo? = null,
    @SerializedName("property_feature_details") val property_feature_details : List<AmenityData>? = ArrayList(),
    @SerializedName("youtube_url") val youtube_url : String? = null,
)

data class PropertyAttr(
    @SerializedName("property_status") val property_status:String,
    @SerializedName("property_type") val property_type:String,
    @SerializedName("property_label") val property_label:String,
)

data class PropertyMeta(
    @SerializedName("fave_property_price") val fave_property_price : List<String>,
    @SerializedName("fave_currency") val fave_currency : List<String>?=null,
    @SerializedName("fave_property_size") val fave_property_size : List<String>,
    @SerializedName("fave_property_bedrooms") val fave_property_bedrooms : List<String>,
    @SerializedName("fave_property_bathrooms") val fave_property_bathrooms : List<String>,
    @SerializedName("fave_property_map_street_view") val fave_property_map_street_view : List<String>,
    @SerializedName("fave_featured") val fave_featured : List<String>,
    @SerializedName("fave_prop_homeslider") val fave_prop_homeslider : List<String>,
    @SerializedName("fave_single_content_area") val fave_single_content_area : List<String>,
    @SerializedName("_thumbnail_id") val _thumbnail_id : List<String>,
    @SerializedName("fave_property_images") val fave_property_images : List<String>,
    @SerializedName("fave_property_garage_size") var fave_property_garage_size : List<String>,
    @SerializedName("fave_property_garage") var favePropertyGarage : List<String>,
    @SerializedName("fave_property_land") var favePropertyLand: List<String>,
    @SerializedName("houzez_total_property_views") var totalViews: List<String>,
    @SerializedName("houzez_geolocation_lat") var houzez_geolocation_lat: List<String>,
    @SerializedName("houzez_geolocation_long") var houzez_geolocation_long: List<String>,
    @SerializedName("video") val video:ArrayList<String>,
    @SerializedName("living_room") val living_room : List<String>? = null,
    @SerializedName("kitchen") val kitchen : List<String>? = null,
    @SerializedName("balconies") val balconies : List<String>? = null,
    @SerializedName("floor_number") val floor_number : List<String>? = null,
    @SerializedName("orientation") val orientation : List<String>? = null,
    @SerializedName("monthly_price") val monthly_price : List<String>? = null,
    @SerializedName("currency_monthly") val currency_monthly : List<String>? = null,
    @SerializedName("street_type") val street_type : List<String>? = null,
    @SerializedName("real_estate_situation") val real_estate_situation : List<String>? = null,
    @SerializedName("furnished") val furnished : List<String>? = null,

)

data class AgentAgencyInfo(
    @SerializedName("agent_mobile_call") val agent_mobile_call : String,
    @SerializedName("agent_whatsapp") val agent_whatsapp : String,
)

data class PropertyAddress(
    @SerializedName("property_area") val property_area:String,
    @SerializedName("property_country") val property_country:String,
    @SerializedName("property_state") val property_state:String,
    @SerializedName("property_city") val property_city:String,
)
