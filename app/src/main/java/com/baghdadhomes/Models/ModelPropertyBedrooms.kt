package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ModelPropertyBedrooms(
    @SerializedName("isSelected") var isSelected : Boolean?=false,
    @SerializedName("bedrooms") val bedrooms : String?=null,
)