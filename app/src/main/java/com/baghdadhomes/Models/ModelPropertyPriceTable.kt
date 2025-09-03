package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

class ModelPropertyPriceTable (
    @SerializedName("price") var price : String?=null,
    @SerializedName("text") val text : String?=null,
)