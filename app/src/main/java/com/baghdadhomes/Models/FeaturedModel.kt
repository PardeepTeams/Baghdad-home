package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

class FeaturedModel (
    @SerializedName("success") val success : Boolean,
    @SerializedName("count")val count:Int,
    @SerializedName("result") val result : List<FeaturedData>
)

data class FeaturedData(
    @SerializedName("ID") val iD : String,
    @SerializedName("thumbnail") val thumbnail : Object,
    @SerializedName("property_address")val property_address:FeatureAddress,
    @SerializedName("property_meta")val property_meta:FeautredMeta,
    @SerializedName("is_fav") var is_fav:Boolean? = false,
)

data class FeautredMeta(
    @SerializedName("houzez_total_property_views") var totalViews: List<String>,
)

data class FeatureAddress(
    @SerializedName("property_area") val property_area:String,
)
