package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class HomeModel(
    @SerializedName("success") val success:Boolean? = null,
    @SerializedName("message") val message : String ?= null,
    @SerializedName("data") val data : HomeDataModel ?= null,
)

data class HomeDataModel(
    @SerializedName("all_sliders") val all_sliders:ArrayList<BannerData>? = null,
    @SerializedName("reels_listing") val reels_listing:ArrayList<ReelsData>? = null,
    @SerializedName("featured_listing") var featured_listing : List<Result>
)