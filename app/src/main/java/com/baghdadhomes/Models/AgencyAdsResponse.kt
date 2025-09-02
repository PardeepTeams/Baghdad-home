package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class AgencyAdsResponse(
    @SerializedName("success") val success : Boolean ?= false,
    @SerializedName("count") val count : Int ?= 0,
    @SerializedName("response") var response : List<Result> ?= null,
    @SerializedName("agency_details") var agency_details : AgenciesData ?= null,
    @SerializedName("reels_details") var reels_details : ArrayList<ReelsData> ?= null,
)
