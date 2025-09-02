package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectCity(
    @SerializedName("term_id")
    val termId: Int? = null,

    @SerializedName("name")
    val name: String? = null,

    @SerializedName("slug")
    val slug: String? = null,

    @SerializedName("image")
    val image: String? = null
)
