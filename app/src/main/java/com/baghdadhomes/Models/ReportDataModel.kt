package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ReportDataModel(
    @SerializedName("success") val success : Boolean,
    @SerializedName("message") val message : String? = null
)
