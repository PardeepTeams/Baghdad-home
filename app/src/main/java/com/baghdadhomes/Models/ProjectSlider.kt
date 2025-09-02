package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectSlider(
    @SerializedName("id")
    val id: Int? = null,

    @SerializedName("slider_image")
    val sliderImage: String? = null,

    @SerializedName("status")
    val status: String? = null,

    @SerializedName("url")
    val url: String? = null
)
