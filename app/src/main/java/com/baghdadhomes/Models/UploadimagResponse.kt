package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class UploadimagResponse(
    @SerializedName("success") val success : Boolean,
    @SerializedName("data")val data:ImageData

)

data class ImageData(
    @SerializedName("id") val id : String,
    @SerializedName("url") val url : String,
    @SerializedName("message") val message : String,
)

data class SocialImageUpload(
    @SerializedName("success") val success : Boolean ?= false,
    @SerializedName("code")val code:String ?= null,
    @SerializedName("response")val response:String ?= null
)