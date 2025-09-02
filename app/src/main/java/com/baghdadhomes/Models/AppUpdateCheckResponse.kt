package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class AppUpdateCheckResponse(
    @SerializedName("code") val code : String ?= null,
    @SerializedName("version") val version : String ?= null,
    @SerializedName("success") val success : Boolean ?= false,
    @SerializedName("updation_required") val updation_required : Boolean ?= false,
)