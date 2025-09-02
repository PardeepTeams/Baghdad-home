package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class CheckSocialLoginModel(
    @SerializedName("code") val code : String ?= null,
    @SerializedName("success") val success : Boolean ?= false,
    @SerializedName("response") val response : Boolean ?= false,
    @SerializedName("role_type") val role_type : String ?= null,
    @SerializedName("allowed_ads") val allowed_ads : String ?= null,
)
