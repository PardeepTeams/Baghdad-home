package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class UploadVideoResponse(
    @SerializedName("message") val message : String ?= null,
    @SerializedName("file_url") val file_url : String ?= null,
)
