package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class HomeCityResponseModel(
    @SerializedName("success")
    val success: Boolean? = false,

    @SerializedName("count")
    val count: Int? = null,

    @SerializedName("cities")
    val cities: List<HomeCity>? = ArrayList()
)

data class HomeCity(
    @SerializedName("term_id")
    val termId: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("slug")
    val slug: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("image")
    val image: String? = null,

    var isSelected:Boolean?= false
)
