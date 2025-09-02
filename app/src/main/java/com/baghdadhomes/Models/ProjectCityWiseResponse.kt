package com.baghdadhomes.Models

import com.google.gson.annotations.SerializedName

data class ProjectCityWiseResponse(
    @SerializedName("success") val success : Boolean? = false,
    @SerializedName("data") val data:List<ProjectData>? = ArrayList()
)
