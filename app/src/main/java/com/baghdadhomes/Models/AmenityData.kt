package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName


data class AmenityModel(
    @SerializedName("success")
    val success: Boolean? = false,

    @SerializedName("count")
    val count: Int? = null,

    @SerializedName("features")
    val features: List<AmenityData>? = null
)

data class AmenityData(
    @SerializedName("id")
    val id: String? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("slug")
    val slug: String? = null,

    @SerializedName("description")
    val description: String? = null,

    @SerializedName("count")
    val count: Int? = null,

    @SerializedName("icon")
    val icon: String? = null,

    var isSelected: Boolean = false
)
