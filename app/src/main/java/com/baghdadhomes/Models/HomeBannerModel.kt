package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class HomeBannerModel(
    @SerializedName("success") val status:Boolean? = null,
    @SerializedName("data") val data:ArrayList<BannerData>? = null,
)

data class BannerData(
   @SerializedName("id") val id:String? = null,
   @SerializedName("slider_image") val slider_image:String? = null,
   @SerializedName("status") val status:String? = null,
   @SerializedName("url") val url:String? = null,
)
